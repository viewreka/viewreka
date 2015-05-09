package org.beryx.viewreka.dsl.transform;

import java.util.LinkedHashMap;
import java.util.Map;

import org.beryx.viewreka.dsl.chart.ChartHandler;
import org.beryx.viewreka.dsl.data.DataSourceHandler;
import org.beryx.viewreka.dsl.parameter.ParameterHandler;

public abstract class Keyword {
	public static class Datasource extends Keyword {
		private static final Map<String, Class<? extends AliasHandler<?>>> aliasHandlers = new LinkedHashMap<>();
		static { aliasHandlers.put("type", DataSourceHandler.class); }

		@Override public String getName() { return "datasource"; }
		@Override public Map<String, Class<? extends AliasHandler<?>>> getAliasHandlers() { return aliasHandlers; }
	};

	public static class View extends Keyword { @Override public String getName() { return "view"; } };

	public static class Dataset extends Keyword { @Override public String getName() { return "dataset"; } };

	public static class Parameter extends Keyword {
		private static final Map<String, Class<? extends AliasHandler<?>>> aliasHandlers = new LinkedHashMap<>();
		static { aliasHandlers.put("type", ParameterHandler.class); }

		@Override public String getName() { return "parameter"; }
		@Override public Map<String, Class<? extends AliasHandler<?>>> getAliasHandlers() { return aliasHandlers; }
	};

	public static class Chart extends Keyword {
		private static final Map<String, Class<? extends AliasHandler<?>>> aliasHandlers = new LinkedHashMap<>();
		static {
			aliasHandlers.put("type", ChartHandler.class);
//			aliasHandlers.put("dataset", ToStringHandler.class);
			aliasHandlers.put("parameter", ToStringHandler.class);
		}

		@Override public String getName() { return "chart"; }
		@Override public Map<String, Class<? extends AliasHandler<?>>> getAliasHandlers() { return aliasHandlers; }
	};



	public abstract String getName();

	/**
	 * Default implementation returning null
	 * @return null
	*/
	public Map<String, Class<? extends AliasHandler<?>>> getAliasHandlers() {
		return null;
	}
}
