package org.beryx.viewreka.dsl.data

import groovy.lang.Closure;

import java.util.Map;

import org.beryx.viewreka.model.Query;

interface QueryBuilder {
	def Query build(String name, Closure dsClosure);
}
