package org.beryx.viewreka.model

import org.beryx.viewreka.core.ViewrekaException

import spock.lang.Specification

class DatasetSpec extends Specification {
    def "getObject() and getValue() should use the value returned by rawGet()"() {
        given:
        def dataset = Spy(Dataset) {
            getName() >> "SpyDataset"
            getRowCount() >> 5
            getColumnCount() >> 5
            rawGet(_, _) >> {row, column -> row * column}
            rawGet(_, _, _) >> {row, column, type -> row * column}
        }

        expect:
        dataset.getObject(row, column) == value
        dataset.getValue(row, column, type) == value

        where:
        row | column | type    | value
        1   | 2      | Integer |  2
        2   | 3      | Double  |  6
        0   | 5      | Short   |  0
        4   | 3      | Float   | 12
    }

    def "getObject() and getValue() should map the column name to the corresponding index"() {
        given:
        def dataset = Spy(Dataset) {
            getName() >> "SpyDataset"
            getRowCount() >> 5
            getColumnCount() >> 5
            getColumn(_) >> {String name -> ["one":1, "two":2, "three":3, "four":4, "five":5][name]}
            rawGet(_, _) >> {row, column -> row * column}
            rawGet(_, _, _) >> {row, column, type -> row * column}
        }

        expect:
        dataset.getObject(row, column) == value
        dataset.getValue(row, column, type) == value

        where:
        row | column  | type    | value
        1   | 'two'   | Integer |  2
        2   | 'three' | Double  |  6
        0   | 'five'  | Short   |  0
        4   | 'three' | Float   | 12
    }

    def "getObject() should check the value of the row and column arguments"() {
        given:
        def dataset = Spy(Dataset) {
            getName() >> "SpyDataset"
            getRowCount() >> 5
            getColumnCount() >> 5
            rawGet(_, _) >> {row, column -> row * column}
        }

        when:
        dataset.getObject(row, column)

        then:
        thrown(ViewrekaException)

        where:
        row | column
        -1  |   2
         2  |  -1
         4  |   0
         1  |   6
         5  |   3
    }

    def "getValue() should check the value of the row and column arguments"() {
        given:
        def dataset = Spy(Dataset) {
            getName() >> "SpyDataset"
            getRowCount() >> 5
            getColumnCount() >> 5
            rawGet(_, _, _) >> {row, column, type -> row * column}
        }

        when:
        dataset.getValue(row, column, type)

        then:
        thrown(ViewrekaException)

        where:
        row | column | type
        -2  |  2     | Integer
         3  | -1     | Double
         2  |  6     | Short
         4  |  0     | Float
         5  |  3     | Float
    }
}
