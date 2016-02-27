package org.beryx.viewreka.bundle.util

import org.beryx.viewreka.bundle.api.CodeTemplate

public class SimpleContext implements CodeTemplate.Context {
    final List<String> keywordChain = []

    @Override
    public List<String> getKeywordChain() {
        return keywordChain
    }

    @Override String toString() { "$keywordChain" }
}
