package org.beryx.viewreka.bundle.util

import org.beryx.viewreka.bundle.api.CodeTemplate

abstract class AbstractTemplate implements CodeTemplate {
    final String name
    final String description
    final String sample;

    AbstractTemplate(String name, String description, String sample) {
        this.name = name
        this.description = description
        this.sample = sample
    }

    @Override String toString() {
        "name ($description):\n$sample"
    }
}
