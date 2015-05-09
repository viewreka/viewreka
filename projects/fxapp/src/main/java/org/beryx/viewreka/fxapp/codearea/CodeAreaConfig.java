package org.beryx.viewreka.fxapp.codearea;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;
import java.util.regex.Pattern;

import org.beryx.viewreka.fxapp.codearea.SimpleCodeArea.StyleGroup;

public class CodeAreaConfig {
	private boolean caseInsensitive;
	private String[] keywords;
	private Supplier<String> keywordPatternSupplier;
	private Supplier<Pattern> codePatternCreator;
	private final List<StyleGroup> styleGroups = new ArrayList<>();
	private final List<String> removedStyleGroups = new ArrayList<>();

	public boolean isCaseInsensitive() {
		return caseInsensitive;
	}
	public CodeAreaConfig withCaseInsensitive(@SuppressWarnings("hiding") boolean caseInsensitive) {
		this.caseInsensitive = caseInsensitive;
		return this;
	}

	public String[] getKeywords() {
		return keywords;
	}
	public CodeAreaConfig withKeywords(@SuppressWarnings("hiding") String[] keywords) {
		this.keywords = keywords;
		return this;
	}

	public Supplier<Pattern> getCodePatternCreator() {
		return codePatternCreator;
	}
	public CodeAreaConfig withCodePatternCreator(Supplier<Pattern> creator) {
		this.codePatternCreator = creator;
		return this;
	}

	public Supplier<String> getKeywordPatternSupplier() {
		return keywordPatternSupplier;
	}
	public CodeAreaConfig withKeywordPatternSupplier(Supplier<String> supplier) {
		this.keywordPatternSupplier = supplier;
		return this;
	}

	public List<StyleGroup> getStyleGroups() {
		return styleGroups;
	}
	public CodeAreaConfig withStyleGroup(String groupName, String styleClass, String groupPattern) {
		styleGroups.add(new StyleGroup(groupName, styleClass, groupPattern));
		return this;
	}

	public List<String> getRemovedStyleGroups() {
		return removedStyleGroups;
	}
	public CodeAreaConfig withoutStyleGroup(String groupName) {
		removedStyleGroups.add(groupName);
		return this;
	}
}
