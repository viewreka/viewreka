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
package org.beryx.vbundle.chart.xy

import groovy.util.logging.Slf4j
import javafx.scene.layout.Pane
import org.beryx.viewreka.dsl.chart.AxisHandler
import org.beryx.viewreka.dsl.chart.ChartHandler
import org.beryx.viewreka.dsl.chart.FxChartBuilderBuilder
import org.beryx.viewreka.dsl.transform.AliasHandler
import org.beryx.viewreka.dsl.transform.Keyword
import org.beryx.viewreka.dsl.transform.KeywordVisitor
import org.beryx.viewreka.fxui.chart.xy.XYChartData
import org.codehaus.groovy.ast.GroovyCodeVisitor

/**
 * The {@link ChartHandler} associated with the <code>xy</code> and <code>xyChart</code> aliases.
 */
@Slf4j
public class XYChartHandler implements ChartHandler<XYChartData,Pane> {
	private static AxisKeyword X_AXIS_KEYWORD = new AxisKeyword() {@Override public String getName() { return "xAxis"; }}
	private static AxisKeyword Y_AXIS_KEYWORD = new AxisKeyword() {@Override public String getName() { return "yAxis"; }}

	private static abstract class AxisKeyword implements Keyword {
		private static final Map<String, Class<? extends AliasHandler<?>>> aliasHandlers = new LinkedHashMap<>();
		static { aliasHandlers.put("type", AxisHandler.class); }

		@Override public Map<String, Class<? extends AliasHandler<?>>> getAliasHandlers() { return aliasHandlers; }
	};

	@Override
	public Class<? extends FxChartBuilderBuilder> getAliasClass() {
		return XYChartBuilderBuilder
	}

	@Override
	public Collection<String> getAliases() {
		return Arrays.asList("xy", "xyChart")
	}

	@Override
	public GroovyCodeVisitor getCodeVisitor() {
		return new KeywordVisitor(X_AXIS_KEYWORD, Y_AXIS_KEYWORD)
	}
}
