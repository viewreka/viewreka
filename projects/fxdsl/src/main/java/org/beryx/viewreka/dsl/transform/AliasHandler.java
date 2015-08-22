package org.beryx.viewreka.dsl.transform;

import java.util.Collection;
import java.util.ServiceLoader;

import org.codehaus.groovy.ast.GroovyCodeVisitor;

/**
 * An alias provides an alternative name to a class. A {@link KeywordVisitor} performs an AST transformation that replaces the alias with its correlated class,
 * which is provided by the {@link #getAliasClass()} method.
 * The <code>AliasHandler</code> associated with an alias is selected from the set of available <code>AliasHandlers</code> retrieved by a {@link ServiceLoader}.
 * The chosen <code>AliasHandler</code> is one whose {@link #getAliases()} method returns a set containing the alias.
 * @param <T> the type of the class used to replace the alias
 */
public interface AliasHandler<T> {
    /**
     * @return the class used to replace the alias
     */
    Class<? extends T> getAliasClass();

    /**
     * @return the aliases handled by this <code>AliasHandler</code>
     */
    Collection<String> getAliases();

    /**
     * @return the code visitor used to perform AST transformations on the code of the closure that succeeds the alias. Default value: <code>null</code>.
     */
    default GroovyCodeVisitor getCodeVisitor() {
        return null;
    }
}
