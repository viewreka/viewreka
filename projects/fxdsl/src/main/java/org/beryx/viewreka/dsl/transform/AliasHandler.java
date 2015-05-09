package org.beryx.viewreka.dsl.transform;

import java.util.Collection;

import org.codehaus.groovy.ast.GroovyCodeVisitor;

public interface AliasHandler<T> {
	Class<? extends T> getAliasClass();
	Collection<String> getAliases();
	default GroovyCodeVisitor getCodeVisitor() {
		return null;
	}
}
