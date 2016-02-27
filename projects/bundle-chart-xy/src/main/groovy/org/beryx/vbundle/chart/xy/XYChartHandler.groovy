package org.beryx.vbundle.chart.xy

import groovy.util.logging.Slf4j

import org.beryx.viewreka.dsl.chart.AxisHandler
import org.beryx.viewreka.dsl.chart.ChartHandler
import org.beryx.viewreka.dsl.chart.FxChartBuilderBuilder
import org.beryx.viewreka.dsl.transform.AliasHandler
import org.beryx.viewreka.dsl.transform.Keyword
import org.beryx.viewreka.dsl.transform.KeywordVisitor
import org.codehaus.groovy.ast.GroovyCodeVisitor

/**
 * The {@link ChartHandler} associated with the <code>xy</code> and <code>xyChart</code> aliases.
 */
@Slf4j
public class XYChartHandler implements ChartHandler {
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
