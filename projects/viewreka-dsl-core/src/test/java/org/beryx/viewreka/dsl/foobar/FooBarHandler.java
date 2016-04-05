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
package org.beryx.viewreka.dsl.foobar;

import org.beryx.viewreka.dsl.transform.AliasHandler;
import org.beryx.viewreka.dsl.transform.Keyword;
import org.beryx.viewreka.dsl.transform.KeywordVisitor;
import org.codehaus.groovy.ast.GroovyCodeVisitor;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

public interface FooBarHandler extends AliasHandler<Number> {
    class KEYWORD implements Keyword {
        private static final Map<String, Class<? extends AliasHandler<?>>> aliasHandlers = new LinkedHashMap<>();
        static { aliasHandlers.put("type", FooBarHandler.class); }

        @Override public String getName() { return "foobar"; }
        @Override public Map<String, Class<? extends AliasHandler<?>>> getAliasHandlers() { return aliasHandlers; }
    }


    class Foo implements FooBarHandler {
        @Override
        public Class<? extends Number> getAliasClass() {
            return Float.class;
        }

        @Override
        public Collection<String> getAliases() {
            return Arrays.asList("foo");
        }
    }

    class Bar implements FooBarHandler {
        public static FooBarKeyword FOOTBALL_KEYWORD = new FooBarKeyword() {@Override public String getName() { return "football"; }};
        public static FooBarKeyword BARBECUE_KEYWORD = new FooBarKeyword() {@Override public String getName() { return "barbecue"; }};

        private static abstract class FooBarKeyword implements Keyword {
            private static final Map<String, Class<? extends AliasHandler<?>>> aliasHandlers = new LinkedHashMap<>();
            static { aliasHandlers.put("foobarType", FooBarHandler.class); }

            @Override public Map<String, Class<? extends AliasHandler<?>>> getAliasHandlers() { return aliasHandlers; }
        }

        @Override
        public Class<? extends Number> getAliasClass() {
            return BigDecimal.class;
        }

        @Override
        public Collection<String> getAliases() {
            return Arrays.asList("bar", "Bar", "BAR", "pub");
        }

        @Override
        public GroovyCodeVisitor getCodeVisitor() {
            return new KeywordVisitor(FOOTBALL_KEYWORD, BARBECUE_KEYWORD);
        }
    }
}
