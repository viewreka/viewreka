package org.beryx.viewreka.parameter

import org.beryx.viewreka.core.ViewrekaException
import org.beryx.viewreka.model.Dataset
import org.beryx.viewreka.model.DatasetProvider
import spock.lang.Shared
import spock.lang.Specification

class AbstractParameterSpec extends Specification {
    ParameterGroup group = new ParameterGroup()
    DatasetProvider datasetProvider

    def setup() {
        Dataset dataset = Spy() {
            getRowCount() >> 5
            getColumnCount() >> 10
            rawGet(_, _, _) >> { row, column, _ -> row * column }
            close() >> {}
        }
        datasetProvider = Stub() { getDataset() >> dataset }
    }

    def createParameter(index, prmNames) {
        AbstractParameter.Builder builder = Spy(constructorArgs: ["prm$index", Integer, null, null])
        builder.datasetProvider(datasetProvider).valColumn(index)
        def prm = Spy(AbstractParameter, constructorArgs: [builder]) {
            getParameterNames() >>  prmNames
            getParameterGroup() >> group;
            parameterGroupChanged() >> {};
        }
        prm.compareTo(_) >> {Parameter p -> prm.name <=> p.name}
        prm.toString() >> prm.name
        group.addParameter(prm)
        return prm
    }

    def "should correctly retrieve the possible values"() {
        given:
        def prm3 = createParameter(3, [])

        expect:
        prm3.getPossibleValues().collect {v -> v.value} == [0, 3, 6, 9, 12]
    }

    def "getPossibleValues() should detect out of bounds column"() {
        given:
        def prm11 = createParameter(11, [])

        when:
        prm11.getPossibleValues()

        then:
        thrown(ViewrekaException)
    }

    def "setValue() should invalidate and nullify affected parameters; should detect invalid values"() {
        given:
        def prm1 = createParameter(1, ['prm2', 'prm3'])
        def prm2 = createParameter(2, [])
        def prm3 = createParameter(3, ['prm4', 'prm4'])
        def prm4 = createParameter(4, [])
        def prm5 = createParameter(5, [])
        def prm6 = createParameter(6, ['prm3', 'prm7'])
        def prm7 = createParameter(7, [])

        when:
        prm4.setValue(val)

        then:
        1 * prm1.invalidate()
        0 * prm2.invalidate()
        1 * prm3.invalidate()
        1 * prm4.invalidate()
        0 * prm5.invalidate()
        1 * prm6.invalidate()
        0 * prm7.invalidate()

        1 * prm1.setValue(null)
        0 * prm1.setValue(_)
        1 * prm3.setValue(null)
        1 * prm4.setValue(val)
        0 * prm5.setValue(_)
        1 * prm6.setValue(null)
        0 * prm7.setValue(_)

        prm4.value == val
        prm4.isValid() == valid

        where:
        val | valid
        -4  | false
         0  | true
         3  | false
         8  | true
        11  | false
        12  | true
        15  | false
        16  | true
        20  | false
    }

    def "asString() for non-iterable parameter"() {
        given:
        def prm = Spy(AbstractParameter, constructorArgs: [Mock(AbstractParameter.Builder)]) { isIterable() >> false }

        expect:
        prm.asString(val) == sVal

        where:
        val             | sVal
        null            | ''
        777             | '777'
        777L            | '777'
        777f            | '777.0'
        777d            | '777.0'
        0111            | '73'
        0x111           | '273'
        'abc'           | 'abc'
        1.000d          | '1.0'
        new ArrayList() | '[]'
        new HashMap()   | '{}'
    }

    @Shared def currDate = new Date()
    def "asString() for iterable parameter"() {
        given:
        def prm = Spy(AbstractParameter, constructorArgs: [Mock(AbstractParameter.Builder)]) {
            isIterable() >> true;
            getPossibleValues() >> [
                    new Parameter.Value(1, 'one', 'This is one'),
                    new Parameter.Value(2.0, 'two point oh', 'This is two point oh'),
                    new Parameter.Value(-5, 'negative', 'This is a negative number'),
                    new Parameter.Value(new ArrayList(), 'empty list', 'This is an empty list'),
                    new Parameter.Value(currDate, 'current date', 'This is the current date')
            ]
        }

        expect:
        prm.asString(val) == sVal

        where:
        val             | sVal
        null            | ''
        777             | '777'
        777L            | '777'
        777f            | '777.0'
        777d            | '777.0'
        0111            | '73'
        0x111           | '273'
        'abc'           | 'abc'
        1.000d          | '1.0'
        1               | 'one'
        2               | '2'
        2.0             | 'two point oh'
        -5              | 'negative'
        new ArrayList() | 'empty list'
        []              | 'empty list'
        [1,3,    5]     | '[1, 3, 5]'
        new HashMap()   | '{}'
        currDate        | 'current date'
    }
}
