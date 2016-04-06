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
package org.beryx.viewreka.bundle.util

import javafx.scene.Node
import javafx.scene.control.TextField
import org.codehaus.groovy.tools.Utilities

import java.util.function.BiConsumer
import java.util.function.Function

public class SimpleParameter implements TemplateParameter {
    final String name
    final String description
    final String sampleValue
    final boolean optional
    final List<Function<String, String>> validators
    final Class<? extends Node> controlType
    final double controlMinWidth
    final Function<Node, String> textGetter
    final BiConsumer<Node, String> textSetter

    public static class Builder {
        private final String name
        private final String sampleValue
        private String description
        private boolean optional = false
        private List<Function<String, String>> validators = []
        private Class<? extends Node> controlType = TextField
        private double controlMinWidth = -1
        private Function<Node, String> textGetter = {field -> field.text }
        private BiConsumer<Node, String> textSetter = {field, txt -> field.text = txt}

        public Builder(String name, String sampleValue) {
            this.name = name
            this.sampleValue = sampleValue
        }

        public Builder withDescription(String description) {
            this.description = description
            this
        }

        public Builder withOptional(boolean optional) {
            this.optional = optional
            this
        }

        public Builder withValidator(Function<String, String> validator) {
            validators.add(validator)
            this
        }

        public Builder withValidationPattern(String validationPattern) {
            if(validationPattern != null) {
                validators.add{val -> val.matches(validationPattern) ? null: "The " + name + " must match the pattern " + validationPattern}
            }
            this
        }

        public Builder withTextLength(int minLength, int maxLength) {
            validators.add{val -> val.length() < minLength ? "The " + name + " must be at least " + minLength + " characters long"
                    : val.length() > maxLength ? "The " + name + " must be at most " + maxLength + " characters long" : null}
            this
        }

        public Builder withLongValidator(long minValue, long maxValue) {
            validators.add{val ->
                try {
                    long lVal = Long.parseLong(val)
                    return (lVal < minValue) ? "The minimum value allowed for " + name + " is " + minValue
                            : (lVal < minValue) ? "The maximum value allowed for " + name + " is " + maxValue : null
                } catch (NumberFormatException e) {
                    return "An integer value is required for " + name
                }
            }
            this
        }
        public Builder withLongValidator() {
            withLongValidator(Long.MIN_VALUE, Long.MAX_VALUE)
        }

        public Builder withIntValidator() {
            withLongValidator(Integer.MIN_VALUE, Integer.MAX_VALUE)
        }

        public <C extends Node> Builder withControl(Class<C> controlType, Function<C, String> textGetter, BiConsumer<C, String> textSetter) {
            this.controlType = controlType
            this.textGetter = textGetter
            this.textSetter = textSetter
            this
        }

        public Builder withTextGetter(Function<Node, String> textGetter) {
            this.textGetter = textGetter
            this
        }

        public Builder withIdTextGetter() {
            this.textGetter = { field -> Utilities.isJavaIdentifier(field.text) ? field.text : ("'$field.text'" as String) }
            this
        }

        public Builder withTextSetter(BiConsumer<Node, String> textSetter) {
            this.textSetter = textSetter
            this
        }

        public <C extends Node> Builder withControlMinWidth(double width) {
            this.controlMinWidth = width
            this
        }

        public SimpleParameter build() {
            String descr = (description != null) ? description : name
            new SimpleParameter(name, descr, sampleValue, optional, validators, controlType, controlMinWidth, textGetter, textSetter)
        }
    }

    private SimpleParameter(String name, String description, String sampleValue, boolean optional,
                            List<Function<String, String>> validators, Class<? extends Node> controlType, double controlMinWidth,
                            Function<Node, String> textGetter, BiConsumer<Node, String> textSetter) {
        this.name = name
        this.description = description
        this.sampleValue = sampleValue
        this.optional = optional
        this.validators = Collections.unmodifiableList(validators)
        this.controlType = controlType
        this.controlMinWidth = controlMinWidth
        this.textGetter = textGetter
        this.textSetter = textSetter
    }

    @Override
    public String getValidationErrorMessage(String value) {
        if(value == null) return "Null value"
        if(value.isEmpty()) {
            return optional ? null : "Parameter " + getName() + " is mandatory"
        }
        for(Function<String, String> validator : validators) {
            String errMsg = validator.apply(value)
            if(errMsg != null) return errMsg
        }
        null
    }

    @Override
    public String toString() {
        "$name ($description), sampleValue: $sampleValue"
    }
}
