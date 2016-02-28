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
package org.beryx.viewreka.fxapp.codearea;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;
import java.util.regex.Pattern;

import org.beryx.viewreka.fxapp.codearea.SimpleCodeArea.StyleGroup;

/**
 * The configuration of a {@link SimpleCodeArea}.
 */
public class CodeAreaConfig {
    private boolean caseInsensitive;
    private String[] keywords;
    private Supplier<String> keywordPatternSupplier;
    private Supplier<Pattern> codePatternCreator;
    private final List<StyleGroup> styleGroups = new ArrayList<>();
    private final List<String> removedStyleGroups = new ArrayList<>();

    public boolean isCaseInsensitive() {
        return caseInsensitive;
    }
    public CodeAreaConfig withCaseInsensitive(@SuppressWarnings("hiding") boolean caseInsensitive) {
        this.caseInsensitive = caseInsensitive;
        return this;
    }

    public String[] getKeywords() {
        return keywords;
    }
    public CodeAreaConfig withKeywords(@SuppressWarnings("hiding") String[] keywords) {
        this.keywords = keywords;
        return this;
    }

    public Supplier<Pattern> getCodePatternCreator() {
        return codePatternCreator;
    }
    public CodeAreaConfig withCodePatternCreator(Supplier<Pattern> creator) {
        this.codePatternCreator = creator;
        return this;
    }

    public Supplier<String> getKeywordPatternSupplier() {
        return keywordPatternSupplier;
    }
    public CodeAreaConfig withKeywordPatternSupplier(Supplier<String> supplier) {
        this.keywordPatternSupplier = supplier;
        return this;
    }

    public List<StyleGroup> getStyleGroups() {
        return styleGroups;
    }
    public CodeAreaConfig withStyleGroup(String groupName, String styleClass, String groupPattern) {
        styleGroups.add(new StyleGroup(groupName, styleClass, groupPattern));
        return this;
    }

    public List<String> getRemovedStyleGroups() {
        return removedStyleGroups;
    }
    public CodeAreaConfig withoutStyleGroup(String groupName) {
        removedStyleGroups.add(groupName);
        return this;
    }
}
