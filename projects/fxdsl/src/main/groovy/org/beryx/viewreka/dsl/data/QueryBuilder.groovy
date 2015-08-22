package org.beryx.viewreka.dsl.data

import org.beryx.viewreka.model.Query
/**
 * Builder interface for creating queries.
 */
interface QueryBuilder {
    def Query build(String name, Closure dsClosure);
}
