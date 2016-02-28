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
package org.beryx.viewreka.fxapp;

import spock.lang.*

public class ProjectBasicInfoSpec extends Specification {
    def "validate project name"() {
        expect:
        ProjectBasicInfo.isProjectNameValid(name) == valid

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
        ProjectBasicInfo.isDirPathValid(path) == valid

        where:
        path      | valid
        null      | false
        ""        | false
        ".."      | true
        "../.."   | true
        "aaa"     | true
        "aaa/bbb" | true
    }
}
