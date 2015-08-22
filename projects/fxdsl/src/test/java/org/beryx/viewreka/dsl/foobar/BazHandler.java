package org.beryx.viewreka.dsl.foobar;

import org.beryx.viewreka.dsl.transform.AliasHandler;
import org.beryx.viewreka.dsl.transform.Keyword;
import org.beryx.viewreka.dsl.transform.KeywordVisitor;
import org.codehaus.groovy.ast.GroovyCodeVisitor;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

public interface BazHandler extends AliasHandler<Number> {
    class KEYWORD implements Keyword {
        private static final Map<String, Class<? extends AliasHandler<?>>> aliasHandlers = new LinkedHashMap<>();
        static { aliasHandlers.put("type", BazHandler.class); }

        @Override public String getName() { return "bazbaz"; }
        @Override public Map<String, Class<? extends AliasHandler<?>>> getAliasHandlers() { return aliasHandlers; }
    }

    class Baz implements BazHandler {
        public static BazKeyword BAZAAR_KEYWORD = new BazKeyword() {@Override public String getName() { return "bazaar"; }};
        public static BazKeyword BAZOOKA_KEYWORD = new BazKeyword() {@Override public String getName() { return "bazooka"; }};

        private static abstract class BazKeyword implements Keyword {
            private static final Map<String, Class<? extends AliasHandler<?>>> aliasHandlers = new LinkedHashMap<>();
            static { aliasHandlers.put("bazType", BazHandler.class); }

            @Override public Map<String, Class<? extends AliasHandler<?>>> getAliasHandlers() { return aliasHandlers; }
        }

        @Override
        public Class<? extends Number> getAliasClass() {
            return BigInteger.class;
        }

        @Override
        public Collection<String> getAliases() {
            return Arrays.asList("baz", "bazz", "bazzz");
        }

        @Override
        public GroovyCodeVisitor getCodeVisitor() {
            return new KeywordVisitor(BAZAAR_KEYWORD, BAZOOKA_KEYWORD, FooBarHandler.Bar.BARBECUE_KEYWORD);
        }
    }
}
