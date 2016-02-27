package org.beryx.viewreka.bundle.util

import org.beryx.viewreka.bundle.api.CodeTemplate

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
    boolean isAllowedInContext(CodeTemplate.Context context) {
        List<String> keywordChain = context.keywordChain
        if(keywordChain == null) return true
        return allowedKeywordChain.equals(keywordChain)
    }
}
