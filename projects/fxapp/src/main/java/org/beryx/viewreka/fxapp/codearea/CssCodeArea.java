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

import java.util.function.Supplier;

/**
 * Specialization of {@link SimpleCodeArea} for CSS content.
 */
public class CssCodeArea extends SimpleCodeArea {
    public static final Supplier<String> KEYWORD_PATTERN_SUPPLIER = () -> "-fx-[\\w-]+";
    public static final CodeAreaConfig CONFIG = new CodeAreaConfig().withKeywordPatternSupplier(KEYWORD_PATTERN_SUPPLIER);

    public CssCodeArea() {
        applyConfiguration(CONFIG);
    }
}
