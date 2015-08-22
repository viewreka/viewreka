package org.beryx.viewreka.parameter

import spock.lang.Specification

class AbstractComparableValueParameterSpec extends Specification {
    def "should check the min and max values"() {
        given:
        AbstractComparableValueParameter.CompBuilder builder = Spy(constructorArgs: ["prm1", Integer, new ParameterGroup()])
        builder.minValue(minVal).maxValue(maxVal).minValueAllowed(minValAllowed).maxValueAllowed(maxValAllowed)
        def prm = Spy(AbstractComparableValueParameter, constructorArgs: [builder]) { isIterable() >> false }

        when:
        prm.setValue(val)

        then:
        prm.isValid() == valid

        where:
        val  | minVal | maxVal | minValAllowed | maxValAllowed || valid
         5   | 1      | 8      | true          | true          || true
         5   | 1      | 5      | true          | true          || true
         5   | 1      | 5      | true          | false         || false
         5   | 1      | 4      | true          | true          || false
         5   | 5      | 8      | true          | true          || true
         5   | 5      | 8      | false         | true          || false
         5   | 6      | 8      | true          | true          || false
         5   | 5      | 5      | true          | true          || true
         5   | 5      | 5      | false         | true          || false
         5   | 5      | 5      | true          | false         || false
         7   | 1      | null   | true          | true          || true
         7   | 1      | null   | true          | false         || true
         7   | 7      | null   | true          | false         || true
         7   | 7      | null   | false         | false         || false
         3   | null   | 8      | true          | true          || true
         3   | null   | 8      | false         | true          || true
         3   | null   | 3      | false         | true          || true
         3   | null   | 3      | false         | false         || false
         3   | null   | null   | true          | true          || true
        -3   | null   | null   | false         | false         || true
    }
}
