/**
 * Inspired by (and using code from) org.fxmisc.richtext.demo.JavaKeywords (https://github.com/TomasMikula/RichTextFX)
 */
package org.beryx.viewreka.fxapp.codearea;

/**
 * Specialization of {@link SimpleCodeArea} for Java content.
 */
public class JavaCodeArea extends SimpleCodeArea {
    private static final String[] KEYWORDS = new String[] {
        "abstract", "assert", "boolean", "break", "byte",
        "case", "catch", "char", "class", "const",
        "continue", "default", "do", "double", "else",
        "enum", "extends", "final", "finally", "float",
        "for", "goto", "if", "implements", "import",
        "instanceof", "int", "interface", "long", "native",
        "new", "package", "private", "protected", "public",
        "return", "short", "static", "strictfp", "super",
        "switch", "synchronized", "this", "throw", "throws",
        "transient", "try", "void", "volatile", "while"
    };

    public static final CodeAreaConfig CONFIG = new CodeAreaConfig().withCaseInsensitive(false).withKeywords(KEYWORDS);

    public JavaCodeArea() {
        applyConfiguration(CONFIG);
    }
}
