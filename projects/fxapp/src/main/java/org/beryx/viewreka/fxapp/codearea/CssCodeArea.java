package org.beryx.viewreka.fxapp.codearea;

import java.util.function.Supplier;

public class CssCodeArea extends SimpleCodeArea {
	public static final Supplier<String> KEYWORD_PATTERN_SUPPLIER = () -> "-fx-[\\w-]+";
	public static final CodeAreaConfig CONFIG = new CodeAreaConfig().withKeywordPatternSupplier(KEYWORD_PATTERN_SUPPLIER);

	public CssCodeArea() {
		applyConfiguration(CONFIG);
	}
}
