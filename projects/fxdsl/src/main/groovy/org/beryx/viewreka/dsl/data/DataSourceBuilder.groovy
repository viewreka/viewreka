package org.beryx.viewreka.dsl.data

import org.beryx.viewreka.model.DataSource
import org.beryx.viewreka.model.Query
/**
 * Builder interface for creating {@link DataSource}s.
 */
interface DataSourceBuilder {
    def <Q extends Query> DataSource<Q> build(String dsName, Closure dsClosure);
    def QueryBuilder getQueryBuilder()
}
