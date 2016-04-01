/**
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
package org.beryx.viewreka.fxui.settings;

import javafx.beans.property.Property;
import javafx.stage.Window;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.util.Map;
import java.util.TreeMap;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * A helper class for retrieving and saving {@link Property} values.
 */
public class FxPropsManager {
    private static final Logger log = LoggerFactory.getLogger(GuiSettings.class);
    private final GuiSettings settings;
    private final String prefix;
    private final Map<String, Pair<Consumer<?>, Supplier<?>>> propsMap = new TreeMap<>();

    public FxPropsManager(GuiSettings settings, String prefix) {
        this.settings = settings;
        this.prefix = prefix;
    }

    public GuiSettings getSettings() {
        return settings;
    }

    public String getPrefix() {
        return prefix;
    }

    public void register(Consumer<?> consumer, Supplier<?> supplier, String propName, String propPrefix) {
        String key = prefix + "." + propPrefix + "." + propName;
        propsMap.put(key, new ImmutablePair<>(consumer, supplier));
    }

    public void register(Property<?> appliedProp, Supplier<?> supplier, String propPrefix) {
        register(((Property<Object>)appliedProp)::setValue, supplier, appliedProp.getName(), propPrefix);
    }

    public void register(Property<?> prop, String propPrefix) {
        register(prop, prop::getValue, propPrefix);
    }

    public void unregisterAll() {
        propsMap.clear();
    }

    public void applySettings() {
        propsMap.forEach((key, propPair) -> {
            Object value = settings.getProperty(key, null, true);
            if(value != null) {
                try {
                    Consumer<Object> consumer = (Consumer<Object>) propPair.getKey();
                    consumer.accept(value);
                } catch (Exception e) {
                    log.warn("Cannot apply value '" + value + "' to '" + key + "'", e);
                }
            }
        });
    }

    public void saveSettings() {
        propsMap.forEach((key, propPair) -> {
            Serializable value = null;
            try {
                Supplier<?> supplier = propPair.getValue();
                value = (Serializable) supplier.get();
                if (value != null) {
                    settings.setProperty(key, value);
                }
            } catch (Exception e) {
                log.warn("Cannot save value '" + value + "' of '" + key + "'", e);
            }
        });
    }

    public void attachTo(Window window) {
        window.setOnShowing(ev -> applySettings());
        window.setOnHiding(ev -> saveSettings());
    }
}
