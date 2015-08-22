package org.beryx.viewreka.fxapp;

import spock.lang.*

public class NewProjectTest extends Specification {
    def "validate project name"() {
        expect:
        NewProject.isProjectNameValid(name) == valid

        where:
        name | valid
        null | false
        ""   | false
        "0"  | false
        "0a" | false
        "_"  | false
        "_a" | false
        "_1" | false
        "1_" | false
        "__" | false
        "_1a"| false
        "_a1"| false
        "a"  | true
        "a0" | true
        "aa" | true
        "a_" | true
        "a_1"| true
    }

    def "validate project dir"() {
        expect:
        NewProject.isDirPathValid(path) == valid

        where:
        path      | valid
        null      | false
        ""        | false
        "?"       | false
        "*"       | false
        "a*"      | false
        "a?"      | false
        "?a"      | false
        "*a"      | false
        "?*"      | false
        "*?"      | false
        ".."      | true
        "../.."   | true
        "aaa"     | true
        "aaa/bbb" | true
    }
}
