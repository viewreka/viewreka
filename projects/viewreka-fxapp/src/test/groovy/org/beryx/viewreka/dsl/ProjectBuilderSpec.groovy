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

import org.beryx.viewreka.dsl.project.ProjectBuilder
import org.beryx.viewreka.fxui.FxProject
import spock.lang.Specification

class ProjectBuilderSpec extends Specification {

    def static script1 = '''
            view hello {
              chart c1 (type : html) {
                content = '<h1>Hello, world!</h1>'
              }
            }
        '''

    def static script2 = '''
        datasource dbWB(type : sql) {
            driver = 'org.beryx.viewreka.h2.RelocatedDriver\'
            connection = "jdbc:relocated-h2:jar:(${System.properties['user.dir']}/worldbank"
        }

        view About {
            chart descr(type : htmlChart) {
              content = file('wb01.html')
            }
        }

        view lifeExpectancy {
            dataset dsLifeExp {
              query = "select calendar_year, life_exp, life_exp_male, life_exp_female from v_indicator where country_name='Germany'"
            }

            chart c1(type: xyChart) {
                dataset = dsLifeExp

                xAxis (type : int)
                yAxis (type : double)

                series (
                    total : ['calendar_year','life_exp'],
                    male : ['calendar_year','life_exp_male'],
                    female : ['calendar_year','life_exp_female']
                )
            }
        }
    '''

    def "should correctly build an FxProject"() {
        given:
        def scriptSource = new GroovyCodeSource(script, 'script.viewreka', 'test')
        FxProject project = ProjectBuilder.createFromGroovyCodeSource(scriptSource)

        expect:
        project.getScriptIssues().isEmpty()
        project.views.size() == vCount
        project.dataSources.keySet() == dataSources as Set
        def view = project.views[-1]
        view.name == vName
        view.chartBuilders.keySet() == charts as Set
        view.datasetProviders.keySet() == datasets as Set

        where:
        script  | vCount | dataSources | vName            | charts | datasets
        script1 | 1      | []          | 'hello'          | ['c1'] | []
        script2 | 2      | ['dbWB']    | 'lifeExpectancy' | ['c1'] | ['dsLifeExp']
    }

    def "should correctly build an XYChart"() {
        given:
        def scriptSource = new GroovyCodeSource(script2, 'script.viewreka', 'test')
        FxProject project = ProjectBuilder.createFromGroovyCodeSource(scriptSource)

        expect:
        def view = project.views[-1]
        Map series = view.chartBuilders['c1'].seriesConfigMap
        series.keySet() == ['total', 'male', 'female'] as Set
        series.values()*.datasetProvider.name == ['dsLifeExp'] * 3
        series.values()*.XColumnName == ['calendar_year'] * 3
        series.values()*.XColumnType == [Integer] * 3
        series.values()*.YColumnName == ['life_exp', 'life_exp_male', 'life_exp_female']
        series.values()*.YColumnType == [Double] * 3
    }
}
