package org.beryx.viewreka.fxapp.codearea;

public class GroovyCodeArea extends SimpleCodeArea {
	private static final String[] KEYWORDS = new String[] {
			"abstract", "any", "as", "assert", "boolean", "break", "byte", "case", "catch", "char", "class", "const", "continue", "def", "default", "do", "double",
			"else", "enum", "extends", "false", "final", "finally", "float", "for", "goto", "if", "implements", "import", "in", "instanceof", "int",
			"interface", "it", "long", "native", "new", "null", "package", "private", "protected", "public", "return", "short", "static", "strictfp", "super",
			"switch", "synchronized", "this", "threadsafe", "throw", "throws", "traits", "transient", "true", "try", "void", "volatile", "while"
	};

	public static final CodeAreaConfig CONFIG = new CodeAreaConfig().withCaseInsensitive(false).withKeywords(KEYWORDS);

	public GroovyCodeArea() {
		applyConfiguration(CONFIG);
	}
}
