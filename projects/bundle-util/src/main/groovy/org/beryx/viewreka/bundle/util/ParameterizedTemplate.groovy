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

import javafx.scene.Scene
import javafx.stage.Modality
import javafx.stage.Stage
import javafx.stage.StageStyle
import org.beryx.viewreka.bundle.api.CodeTemplate

import java.util.function.Function
import java.util.regex.Matcher
import java.util.regex.Pattern

class ParameterizedTemplate extends KeywordPathTemplate {
    final String template
    final String caretPositionMarker
    final String parameterRegex
    final List<TemplateParameter> parameters
    final boolean excludeLinesWithMissingValues
    final List<Function<Map<String,String>, String>> validators

    static class Builder {
        final String name
        final String keywordPath
        final String template
        String caretPositionMarker = "<#>"
        String description
        String sample
        String parameterRegex = "<([a-zA-Z_]\\w*)>"
        final List<TemplateParameter> parameters = new ArrayList<>()
        boolean excludeLinesWithMissingValues = true
        final List<Function<Map<String,String>, String>> validators = new ArrayList<>();

        Builder(String name, String keywordPath, String template) {
            this.name = name
            this.keywordPath = keywordPath
            this.template = template
        }

        Builder withCaretPositionMarker(String caretPositionMarker) {
            this.caretPositionMarker = caretPositionMarker
            this
        }

        Builder withDescription(String description) {
            this.description = description
            this
        }

        Builder withSample(String sample) {
            this.sample = sample
            this
        }

        Builder withParameterPattern(String parameterPattern) {
            this.parameterRegex = parameterPattern
            this
        }

        Builder withParameter(TemplateParameter prm) {
            this.parameters.add(prm)
            return this
        }

        Builder withParameter(String name, String description, String sampleValue, boolean optional) {
            this.parameters.add(new SimpleParameter.Builder(name, sampleValue).withDescription(description).withOptional(optional).build())
            this
        }

        Builder withIdParameter(String name, String description, String sampleValue) {
            this.parameters.add(new SimpleParameter.Builder(name, sampleValue).withDescription(description).withIdTextGetter().build())
            this
        }

        Builder withExcludeLinesWithMissingValues(boolean exclude) {
            this.excludeLinesWithMissingValues = exclude
            this
        }

        Builder withValidator(Function<Map<String,String>, String> validator)  {
            validators.add(validator)
            this
        }

        ParameterizedTemplate build() {
            String descr = (description != null) ? description : name
            String smpl = sample
            if(smpl == null) {
                def values = parameters.collectEntries {[it.name, it.sampleValue]}
                smpl = getMergedTemplate(template, parameterRegex, excludeLinesWithMissingValues, parameters, values)
            }
            new ParameterizedTemplate(name, keywordPath, descr, smpl, template, caretPositionMarker,
                    parameterRegex, parameters, excludeLinesWithMissingValues, validators)
        }
    }

    private static String getMergedTemplate(String template, String parameterRegex, boolean excludeLinesWithMissingValues,
                                        List<TemplateParameter> parameters, Map<String, String> values) {
        StringBuilder sb = new StringBuilder()
        String[] lines = template.replaceAll("\\r", "").split("\\n")
        Pattern pattern = Pattern.compile(parameterRegex)
        for(String line : lines) {
            String mergedLine = getMergedTemplateLine(line, pattern, excludeLinesWithMissingValues, parameters, values)
            if(mergedLine != null) {
                sb.append(mergedLine).append('\n')
            }
        }
        return sb.toString()
    }

    private static String getMergedTemplateLine(String templateLine, Pattern pattern, boolean excludeLinesWithMissingValues,
                                                List<TemplateParameter> parameters, Map<String, String> values) {
        StringBuilder head = new StringBuilder(80)
        String tail = templateLine
        while(true) {
            Matcher matcher = pattern.matcher(tail)
            if(!matcher.find()) break
            String placeholder = matcher.group()
            String prmName = matcher.group(1)
            if(placeholder.isEmpty()) throw new RuntimeException("Internal error: empty placeholder")
            if(prmName.isEmpty()) throw new RuntimeException("Internal error: empty parameter name")
            TemplateParameter prm = parameters.find {p -> prmName.equals(p.name)}
            if(prm == null) throw new RuntimeException("Unknown parameter: " + prmName)
            String prmVal = values[prmName]
            if(prmVal == null) throw new RuntimeException("Parameter " + prmName + " not set")
            if(prmVal.isEmpty() && excludeLinesWithMissingValues) return null
            int start = matcher.start()
            int end = matcher.end()
            head.append(tail.substring(0, start)).append(prmVal)
            tail = tail.substring(end)
        }
        head.append(tail)
        return head.toString()
    }

    private ParameterizedTemplate(String name, String keywordPath, String description, String sample, String template, String caretPositionMarker, String parameterRegex,
                                  List<TemplateParameter> parameters, boolean excludeLinesWithMissingValues, List<Function<Map<String,String>, String>> validators) {
        super(name, keywordPath, description, sample)
        this.template = template
        this.caretPositionMarker = caretPositionMarker
        this.parameterRegex = parameterRegex
        this.parameters = Collections.unmodifiableList(parameters)
        this.excludeLinesWithMissingValues = excludeLinesWithMissingValues
        this.validators = Collections.unmodifiableList(validators)
    }

    @Override
    public CodeTemplate.CodeFragment getCodeFragment(CodeTemplate.Configuration configuration) {
        if(parameters.isEmpty()) return new SimpleCodeFragment(template, caretPositionMarker)
        Map<String, String> values = readParameterValues()
        new SimpleCodeFragment(mergeTemplate(values), caretPositionMarker)
    }

    protected String mergeTemplate(Map<String, String> values) {
        if(values == null || values.isEmpty()) return null
        getMergedTemplate(template, parameterRegex, excludeLinesWithMissingValues, parameters, values)
    }

    protected Map<String, String> readParameterValues() {
        Stage stage = new Stage()
        stage.initStyle(StageStyle.UTILITY)
        stage.initModality(Modality.APPLICATION_MODAL)
        stage.title = name

        ParameterConfigPane cfgPane = new ParameterConfigPane(stage, this)
        Scene scene = new Scene(cfgPane)
        stage.scene = scene
        stage.showAndWait()

        cfgPane.values
    }
}
