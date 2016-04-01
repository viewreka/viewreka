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

import java.util.HashMap;
import java.util.Map;

/**
 * A registry for associating file extensions with configurations of {@link SimpleCodeArea} (that is, {@link CodeAreaConfig} instances).
 */
public enum FileAssociations {
    INSTANCE;

    static {
        INSTANCE.addAssociations(ViewrekaCodeArea.CONFIG, "viewreka");
        INSTANCE.addAssociations(JavaCodeArea.CONFIG, "java");
        INSTANCE.addAssociations(GroovyCodeArea.CONFIG, "groovy");
        INSTANCE.addAssociations(SqlCodeArea.CONFIG, "sql");
        INSTANCE.addAssociations(CssCodeArea.CONFIG, "css");
    }

    private final Map<String, CodeAreaConfig> associations = new HashMap<>();

    public CodeAreaConfig getConfig(String extension) {
        return associations.get(extension.toLowerCase());
    }

    public void addAssociations(CodeAreaConfig config, String... extensions) {
        for(String extension : extensions) {
            associations.put(extension.toLowerCase(), config);
        }
    }

    public void removeAssociations(String... extensions) {
        for(String extension : extensions) {
            associations.remove(extension.toLowerCase());
        }
    }
}
