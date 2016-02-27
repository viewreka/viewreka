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
        this.caretPosition = code.indexOf(caretPositionMarker)
        this.code = (this.caretPosition < 0) ? code : code.replace(caretPositionMarker, "")
    }

    @Override String toString() { code }
}
