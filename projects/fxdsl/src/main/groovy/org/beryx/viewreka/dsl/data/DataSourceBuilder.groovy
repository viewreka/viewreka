package org.beryx.viewreka.dsl.data

import groovy.lang.Closure;

import java.util.Map;

import org.beryx.viewreka.model.DataSource;
import org.beryx.viewreka.model.Query;

interface DataSourceBuilder {
	def <Q extends Query> DataSource<Q> build(String dsName, Closure dsClosure);
	def QueryBuilder getQueryBuilder()
}
