/*
 * Copyright 2015-2016 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.beryx.viewreka.dsl
import groovy.util.logging.Slf4j
import org.beryx.viewreka.core.ViewrekaException
import org.beryx.viewreka.dsl.chart.FxChartBuilderBuilder
import org.beryx.viewreka.dsl.data.DataSourceBuilder
import org.beryx.viewreka.dsl.data.QueryBuilder
import org.beryx.viewreka.dsl.parameter.ParameterBuilder
import org.beryx.viewreka.dsl.project.FxProjectImpl
import org.beryx.viewreka.fxui.FxViewImpl
import org.beryx.viewreka.fxui.chart.FxChartBuilder
import org.beryx.viewreka.model.DataSource
import org.beryx.viewreka.model.DatasetProvider
import org.beryx.viewreka.model.Query
import org.beryx.viewreka.parameter.Parameter
import org.beryx.viewreka.parameter.ParameterGroup

import java.util.function.Supplier
/**
 * The base class of Viewreka scripts.
 */
@Slf4j
abstract class ViewrekaScript extends Script {

    FxProjectImpl project

    private queryBuilders = [:]
    private String currentViewName = null
    private Map<String, DatasetProvider> currentDataSetProviders = null
    private ParameterGroup currentParameterGroup = null
    private Map<String, FxChartBuilder<?>> currentChartBuilders = null
    // FIXME
    private Parameter<?> currentChartParameter = null

    @Override
    public void setBinding(Binding binding) {
        this.project = binding.variables.remove("project")
        Binding b = new Binding(binding.variables) {
            void setVariable(String name, Object value) {
                if(ViewrekaScript.this.project?.hasProperty(name)) {
                    ViewrekaScript.this.project."$name" = value
                } else {
                    super.setVariable(name, value)
                }
            };
        }
        super.setBinding(b);
    }

    def datasource(String name, Map options, Closure closure) {
        if(project.dataSources.containsKey(name)) throw new ViewrekaException("Duplicate data source: $name")
        Class<? extends DataSourceBuilder> type = options?.type
        if(!type) throw new ViewrekaException("Missing type in satasource $name")
        def builder = type.getConstructor().newInstance()
        def ds = builder.build(name, closure)
        project.dataSources[name] = ds
        queryBuilders[name] = builder.queryBuilder
    }

    def view(String viewName, Closure closure) {
        currentViewName = viewName
        currentDataSetProviders = [:]
        currentParameterGroup = new ParameterGroup()
        currentChartBuilders = [:]


        def FxViewImpl view = new ViewBuilder(project).build(viewName, closure)

        view.datasetProviders.putAll(currentDataSetProviders)
        currentParameterGroup.parameters.each { prm -> view.parameters.put(prm.name, prm) }
        view.chartBuilders.putAll(currentChartBuilders)

        // FIXME
        view.chartParameter = currentChartParameter

        view.initParameters()


        Supplier<String> titleSupplier = {
            String iteratedPrmName = view.viewSettings.selectedIteratedParameter;
            Parameter iteratedParameter = view.parameters[iteratedPrmName]
            String title = iteratedParameter?.valueAsString ?: view.name;
            return title;
        }
        view.chartBuilders.values().each { builder -> builder.titleSupplier = titleSupplier }

        project.views << view

        currentChartBuilders = null
        currentParameterGroup = null
        currentDataSetProviders = null
        currentViewName = null
    }

    def dataset(String name, Closure closure) {
        dataset(name, null, closure)
    }

    def dataset(String name, Map options, Closure closure) {
        log.debug "Dataset $name, options: $options"

        if(currentDataSetProviders == null || currentParameterGroup == null) throw new ViewrekaException("Dataset $name declared outside of a view")
        if(currentDataSetProviders?.containsKey(name)) throw new ViewrekaException("Duplicate dataset: $name")
        String dataSourceName
        def src = options?.source
        if(options?.source instanceof String) {
            dataSourceName = options.source
        } else {
            if(project.dataSources.size() == 1) {
                dataSourceName = project.dataSources.keySet().first()
            } else {
                throw new ViewrekaException("No data source specified for the dataset $name and no default data source can be determined")
            }
        }
        assert dataSourceName
        DataSource<?> dataSource = project.dataSources[dataSourceName]
        if(!dataSource) throw new ViewrekaException("Unknown dataSource specified for the dataset $name: $dataSourceName")

        def QueryBuilder builder
        Class<? extends QueryBuilder> type = options?.type
        if(type) {
            builder = type.getConstructor().newInstance()
        } else {
            builder = queryBuilders[dataSourceName]
        }
        assert builder

        Query query = builder.build(name, closure)

        currentDataSetProviders[name] = dataSource.getDatasetProvider(name, query, currentParameterGroup)
    }

    def parameter(String name, Map options) {
        parameter(name, options, null)
    }

    def parameter(String name, Map options, Closure closure) {
        log.debug "Parameter $name, options: $options"

        if(currentParameterGroup == null || currentDataSetProviders == null) throw new ViewrekaException("Parameter $name declared outside of a view")
        if(currentParameterGroup?.getParameter(name)) throw new ViewrekaException("Duplicate parameter: $name")
        if(!(options?.type instanceof Class)) throw new ViewrekaException("No valid type defined for parameter $name: type = ${options.type}")

        Class<? extends ParameterBuilder> type = options?.type;
        def builder = type.getConstructor().newInstance()

        def parameter = builder.build(name, closure, currentParameterGroup, currentDataSetProviders)
        currentParameterGroup.addParameter(parameter)
    }


    def chart(String name, Map options, Closure closure) {
        log.debug "Chart $name, options: $options"

        if(currentChartBuilders == null || currentDataSetProviders == null || currentParameterGroup == null) throw new ViewrekaException("Chart $name declared outside of a view")
        if(currentChartBuilders[name]) throw new ViewrekaException("Duplicate chart: $name")

        if(!(options?.type instanceof Class)) throw new ViewrekaException("No valid type defined for chart $name: type = ${options.type}")
        Class<? extends FxChartBuilderBuilder> type = options?.type;
        FxChartBuilderBuilder builder = type.getConstructor().newInstance()

        Parameter<?> chartParameter = null
        if(options?.parameter) {
            chartParameter = currentParameterGroup.getParameter(options.parameter)
            if(!chartParameter) throw new ViewrekaException("Unknown parameter: ${options.parameter}")
        }
        // FIXME
        currentChartParameter = chartParameter

        FxChartBuilder<?> chartBuilder = builder.build(name, closure, chartParameter, currentDataSetProviders)
        currentChartBuilders.put(name, chartBuilder)

    }
}
