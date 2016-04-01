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
/**
 * Specialization of {@link SimpleCodeArea} for Groovy content.
 */
package org.beryx.viewreka.fxapp.codearea;

public class GroovyCodeArea extends SimpleCodeArea {
    private static final String[] KEYWORDS = new String[] {
            "abstract", "any", "as", "assert", "boolean", "break", "byte", "case", "catch", "char", "class", "const", "continue", "def", "default", "do", "double",
            "else", "enum", "extends", "false", "final", "finally", "float", "for", "goto", "if", "implements", "import", "in", "instanceof", "int",
            "interface", "it", "long", "native", "new", "null", "package", "private", "protected", "public", "return", "short", "static", "strictfp", "super",
            "switch", "synchronized", "this", "threadsafe", "throw", "throws", "traits", "transient", "true", "try", "void", "volatile", "while"
    };

    public static final CodeAreaConfig CONFIG = new CodeAreaConfig().withCaseInsensitive(false).withKeywords(KEYWORDS);

    public GroovyCodeArea() {
        applyConfiguration(CONFIG);
    }
}
