package org.beryx.viewreka.parameter

import org.beryx.viewreka.core.ViewrekaException
import spock.lang.Specification

class ParameterSpec extends Specification {

    def "should determine the correct sets of prerequisite and affected parameters"() {
        given:
        def group = new ParameterGroup()

        def prm1 = Spy(Parameter) { getName() >> 'prm1'; getParameterNames() >> ['prm2', 'prm3']}
        def prm2 = Spy(Parameter) { getName() >> 'prm2'; getParameterNames() >> []}
        def prm3 = Spy(Parameter) { getName() >> 'prm3'; getParameterNames() >> ['prm4', 'prm5']}
        def prm4 = Spy(Parameter) { getName() >> 'prm4'; getParameterNames() >> []}
        def prm5 = Spy(Parameter) { getName() >> 'prm5'; getParameterNames() >> []}
        def prm6 = Spy(Parameter) { getName() >> 'prm6'; getParameterNames() >> ['prm3', 'prm7']}
        def prm7 = Spy(Parameter) { getName() >> 'prm7'; getParameterNames() >> []}

        [prm1, prm2, prm3, prm4, prm5, prm6, prm7].each {prm ->
            prm.getParameterGroup() >> group;
            prm.parameterGroupChanged() >> {};
            prm.compareTo(_) >> {Parameter p -> prm.name <=> p.name}
            prm.toString() >> prm.name
            group.addParameter(prm)
        }

        expect:
        prm1.getPrerequisiteParameters() as List == [prm2, prm3, prm4, prm5]
        prm2.getPrerequisiteParameters() as List == []
        prm3.getPrerequisiteParameters() as List == [prm4, prm5]
        prm4.getPrerequisiteParameters() as List == []
        prm5.getPrerequisiteParameters() as List == []
        prm6.getPrerequisiteParameters() as List == [prm3, prm4, prm5, prm7]
        prm7.getPrerequisiteParameters() as List == []

        prm1.getAffectedParameters() as List == []
        prm2.getAffectedParameters() as List == [prm1]
        prm3.getAffectedParameters() as List == [prm1, prm6]
        prm4.getAffectedParameters() as List == [prm1, prm3, prm6]
        prm5.getAffectedParameters() as List == [prm1, prm3, prm6]
        prm6.getAffectedParameters() as List == []
        prm7.getAffectedParameters() as List == [prm6]
    }

    def "should detect circular dependencies"() {
        def group = new ParameterGroup()

        def prm1 = Spy(Parameter) { getName() >> 'prm1'; getParameterNames() >> ['prm2', 'prm3']}
        def prm2 = Spy(Parameter) { getName() >> 'prm2'; getParameterNames() >> []}
        def prm3 = Spy(Parameter) { getName() >> 'prm3'; getParameterNames() >> ['prm4', 'prm5']}
        def prm4 = Spy(Parameter) { getName() >> 'prm4'; getParameterNames() >> []}
        def prm5 = Spy(Parameter) { getName() >> 'prm5'; getParameterNames() >> ['prm6', 'prm7']}
        def prm6 = Spy(Parameter) { getName() >> 'prm6'; getParameterNames() >> []}
        def prm7 = Spy(Parameter) { getName() >> 'prm7'; getParameterNames() >> ['prm3']}

        [prm1, prm2, prm3, prm4, prm5, prm6, prm7].each {prm ->
            prm.getParameterGroup() >> group;
            prm.parameterGroupChanged() >> {};
            prm.compareTo(_) >> {Parameter p -> prm.name <=> p.name}
            prm.toString() >> prm.name
            group.addParameter(prm)
        }

        when:
        prm3.getPrerequisiteParameters()

        then:
        thrown(ViewrekaException)
    }

    def "should detect invalid values"() {
        given:
        def prm1 = Spy(Parameter) {
            getName() >> 'prm1'
            getValue() >> value
            getPossibleValues() >> possibleValues.collect{v -> new Parameter.Value(v, "$v", "$v")}
            isNullAllowed() >> nullAllowed
            isIterable() >> true
        }

        expect:
        prm1.isValid() == valid

        where:
        value | possibleValues | nullAllowed || valid
        0     | [0]            | true        || true
        1     | [1, 3, 5, 7]   | false       || true
        5     | [1, 3, 5, 7]   | false       || true
        7     | [1, 3, 5, 7]   | true        || true
        4     | [1, 3, 5, 7]   | false       || false
        4     | []             | false       || false
        null  | [1, 3, 5, 7]   | false       || false
        null  | [1, 3, 5, 7]   | true        || true
        null  | []             | true        || true
    }
}
