package org.beryx.viewreka.settings;

import static org.beryx.viewreka.core.Util.getValue;

import java.io.Serializable;
import java.util.Map;

import org.beryx.viewreka.parameter.Parameter;

/**
 * Interface for accessing the settings of a view.
 */
public interface ViewSettings {
    /**
     * Retrieves the view parameters.
     * @return the map of parameter values (as strings returned by {@link Parameter#getValueAsString()}) indexed by parameter names.
     */
    Map<String, String> getParameters();

    /**
     * If a view has iterable parameters (see {@link Parameter#isIterable()}), one of them can be selected in order to provide the values needed to generate a sequence of charts.
     * This method retrieves this selected iterable parameter.
     * @return the last selected iterable parameter or null if not available.
     */
    String getSelectedIteratedParameter();

    /**
     * Sets the selected iterable parameter.
     * @param parameterName the name of the iterable parameter to be set
     */
    void setSelectedIteratedParameter(String parameterName);


    /**
     * Retrieves the number of milliseconds between displaying two successive charts in a sequence controlled by an iterated parameter (see {@link #getSelectedIteratedParameter()}).
     * @return the number of milliseconds between displaying two successive charts in a sequence
     */
    long getChartFrameDurationMillis();

    /**
     * Sets the number of milliseconds between displaying two successive charts in a sequence controlled by an iterated parameter (see {@link #getSelectedIteratedParameter()}).
     * @param millis the number of milliseconds between displaying two successive charts in a sequence
     */
    void setChartFrameDurationMillis(long millis);

    /**
     * Retrieves the view properties.
     * @return the map of property values indexed by their names.
     */
    Map<String, Serializable> getProperties();

    /**
     * Retrieves the value of the property with the specified name or a default value.
     * @param name the name of the property to be retrieved
     * @param defaultValue the default value to be returned if the specified property is not available.
     * @param allowNull true, if null is a permitted value for the specified property
     * @return the value of the property with the specified name or a default value if no such property exists.
     * The default value is also returned if the value of the specified property is null and {@code allowNull} is false.
     */
    default <T> T getProperty(String name, T defaultValue, boolean allowNull) {
        return getValue(getProperties(), name, defaultValue, allowNull);
    }

    /**
     * Sets the value of a specified property.
     * @param name the name of the property to be set
     * @param value the value to be set
     * @return the previous value of the property with the specified name
     */
    default <T extends Serializable> Object setProperty(String name, T value) {
        return getProperties().put(name, value);
    }
}
