/**
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
