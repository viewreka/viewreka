package org.beryx.viewreka.dsl.transform;

import java.util.LinkedHashMap;
import java.util.Map;

import org.beryx.viewreka.dsl.chart.ChartHandler;
import org.beryx.viewreka.dsl.data.DataSourceHandler;
import org.beryx.viewreka.dsl.parameter.ParameterHandler;

/**
 *
 * Words denoting specific building blocks of a Viewreka project represent <em>Viewreka keywords</em>.
 * Keywords are used to define instances of their corresponding concepts.
 * A number of predefined keywords (such as <em>datasource</em>, <em>dataset</em>, <em>view</em>, <em>parameter</em>, <em>chart</em>) are available to all projects.
 * In addition, Viewreka extensions may register further keywords to be used in their associated projects.
 */
public interface Keyword {

    String getName();

    /**
     * Default implementation returning null
     * @return null
    */
    default Map<String, Class<? extends AliasHandler<?>>> getAliasHandlers() {
        return null;
    }

    /** The <code>datasource</code> keyword */
    public static class Datasource implements Keyword {
        private static final Map<String, Class<? extends AliasHandler<?>>> aliasHandlers = new LinkedHashMap<>();
        static { aliasHandlers.put("type", DataSourceHandler.class); }

        @Override public String getName() { return "datasource"; }
        @Override public Map<String, Class<? extends AliasHandler<?>>> getAliasHandlers() { return aliasHandlers; }
    };

    /** The <code>view</code> keyword */
    public static class View implements Keyword { @Override public String getName() { return "view"; } };

    /** The <code>dataset</code> keyword */
    public static class Dataset implements Keyword { @Override public String getName() { return "dataset"; } };

    /** The <code>parameter</code> keyword */
    public static class Parameter implements Keyword {
        private static final Map<String, Class<? extends AliasHandler<?>>> aliasHandlers = new LinkedHashMap<>();
        static { aliasHandlers.put("type", ParameterHandler.class); }

        @Override public String getName() { return "parameter"; }
        @Override public Map<String, Class<? extends AliasHandler<?>>> getAliasHandlers() { return aliasHandlers; }
    };

    /** The <code>chart</code> keyword */
    public static class Chart implements Keyword {
        private static final Map<String, Class<? extends AliasHandler<?>>> aliasHandlers = new LinkedHashMap<>();
        static {
            aliasHandlers.put("type", ChartHandler.class);
//			aliasHandlers.put("dataset", ToStringHandler.class);
            aliasHandlers.put("parameter", ToStringHandler.class);
        }

        @Override public String getName() { return "chart"; }
        @Override public Map<String, Class<? extends AliasHandler<?>>> getAliasHandlers() { return aliasHandlers; }
    }

}
