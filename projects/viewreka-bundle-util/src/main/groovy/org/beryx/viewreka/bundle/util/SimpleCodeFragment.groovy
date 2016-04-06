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
package org.beryx.viewreka.bundle.util

import org.beryx.viewreka.bundle.api.CodeTemplate

class SimpleCodeFragment implements CodeTemplate.CodeFragment {
    final String code
    final int caretPosition;

    /** Convenience constructor that sets caretPosition to -1 (that is, the caret should be placed after the last character of the code fragment) */
    SimpleCodeFragment(String code) {
        this(code, -1)
    }

    SimpleCodeFragment(String code, int caretPosition) {
        this.code = code
        this.caretPosition = caretPosition
    }

    /**
     * Constructs a code fragment from a string in which the caret position is pointed by a marker
     * @param code the code containing the marker
     * @param caretPositionMarker the marker used to show the caret position in the code
     */
    SimpleCodeFragment(String code, String caretPositionMarker) {
        if(!code) code = ""
        this.caretPosition = code.indexOf(caretPositionMarker)
        this.code = (this.caretPosition < 0) ? code : code.replace(caretPositionMarker, "")
    }

    @Override String toString() { code }
}
