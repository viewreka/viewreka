/*
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
package org.beryx.viewreka.core

import spock.lang.Specification

class UtilSpec extends Specification {
    def "requireNonNull() should throw ViewrekaException when called with null argument"() {
        when:
        Util.requireNonNull(null)
        then:
        thrown(ViewrekaException)

        when:
        Util.requireNonNull(null, "myValue")
        then:
        ViewrekaException e = thrown()
        e.message == "myValue is null"
    }

    def "requireNonNull() should return its argument, if it is not null"() {
        expect:
        val == Util.requireNonNull(val)
        val == Util.requireNonNull(val, "myValue")
        where:
        val  << ["null", new Object(), 1, 0.0, false, new Exception()]
    }

    def "checkNotNull() should throw ViewrekaException when called with null argument"() {
        when:
        Util.checkNotNull(null, "myValue")
        then:
        ViewrekaException e = thrown()
        e.message == "myValue is null"
    }

    def "checkNotNull() should not throw an exception when its first argument is not null"() {
        when:
        Util.checkNotNull(val, "myValue")
        then:
        noExceptionThrown()
        where:
        val  << ["null", new Object(), 1, 0.0, false, new Exception()]
    }

    def "check() should throw ViewrekaException when called with false condition"() {
        when:
        Util.check(false, "false condition")
        then:
        ViewrekaException e = thrown()
        e.message == "false condition"
    }

    def "check() should not throw an exception when called with true condition"() {
        when:
        Util.check(true, "true condition")
        then:
        noExceptionThrown()
    }

    def "castOrDefault() should return its first argument if it has the required type and complies with the allowNull policy"() {
        expect:
        expectedValue == Util.castOrDefault(type, value, defaultValue, allowNull)
        where:
        type    | value  | defaultValue | allowNull || expectedValue
        Integer | 1      | 2            | true      || 1
        String  | "aaa"  | "bbb"        | false     || "aaa"
        String  | null   | "xxx"        | true      || null
        String  | null   | "xxx"        | false     || "xxx"
        String  | "aaa"  | null         | false     || "aaa"
        String  | null   | null         | false     || null
        String  | null   | null         | true      || null
        Number  | 23L    | 3.14         | false     || 23L
    }


    def "castOrDefault() with RuntimeException return type should return its first argument if it has a compatible return type; otherwise it should return the defaultValue"() {
        given:
        def returnValue = Util.castOrDefault(RuntimeException, value, new UnsupportedOperationException(), false)
        expect:
        returnValue.class.simpleName == expectedClassName
        where:
        value                          || expectedClassName
        "aaa"                          || "UnsupportedOperationException"
        111                            || "UnsupportedOperationException"
        new Throwable()                || "UnsupportedOperationException"
        new Error()                    || "UnsupportedOperationException"
        new Exception()                || "UnsupportedOperationException"
        new RuntimeException()         || "RuntimeException"
        new IllegalArgumentException() || "IllegalArgumentException"
    }

    def "getValue() should return the corresponding value"() {
        given:
        def map = [nullValue:null, s:"aaa", i:111, 5:555]
        expect:
        Util.getValue(map, key, defaultValue, allowNull) == expectedValue
        where:
        key          | defaultValue | allowNull || expectedValue
        null         | 222          | true      || 222
        "nullValue"  | 222          | true      || null
        "nullValue"  | 222          | false     || 222
        "s"          | "xxx"        | true      || "aaa"
        "i"          | "xxx"        | false     || 111
        5            | null         | false     || 555
        777          | null         | false     || null
        777          | null         | true      || null
        777          | 888          | true      || 888
        777          | "888"        | false     || "888"
    }
}
