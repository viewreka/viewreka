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

import org.beryx.viewreka.core.CodeContext

abstract class KeywordPathTemplate extends AbstractTemplate {
    final String keyword
    final List<String> allowedKeywordChain = []

    KeywordPathTemplate(String name, String keywordPath, String description, String sample) {
        super(name, description, sample)

        String[] keywords = getKeywords(keywordPath)
        if(keywords.length == 0) {
            throw new RuntimeException("Empty keywordPath for template " + name)
        }
        this.keyword = keywords[keywords.length - 1]
        for(int i=0; i<keywords.length - 1; i++) {
            allowedKeywordChain.add(keywords[i])
        }
    }

    private static String[] getKeywords(String keywordPath) {
        String path = keywordPath.trim()
        path = path.replaceAll("\\s", "")
        path = path.replaceAll("\\\\", "/")
        path = path.replaceAll("//", "/")
        if(path.startsWith("/")) {
            path = path.substring(1)
        }
        if(path.endsWith("/")) {
            path = path.substring(0, path.length() - 1)
        }
        return path.split("/")
    }

    @Override
    boolean isAllowedInContext(CodeContext context) {
        List<String> keywordChain = context.keywordChain
        if(keywordChain == null) return true
        return allowedKeywordChain.equals(keywordChain)
    }
}
