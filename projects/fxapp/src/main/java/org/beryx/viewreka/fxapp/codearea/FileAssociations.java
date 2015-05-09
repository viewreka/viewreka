package org.beryx.viewreka.fxapp.codearea;

import java.util.HashMap;
import java.util.Map;

public enum FileAssociations {
	INSTANCE;

	static {
		INSTANCE.addAssociations(ViewrekaCodeArea.CONFIG, "viewreka");
		INSTANCE.addAssociations(JavaCodeArea.CONFIG, "java");
		INSTANCE.addAssociations(GroovyCodeArea.CONFIG, "groovy");
		INSTANCE.addAssociations(SqlCodeArea.CONFIG, "sql");
		INSTANCE.addAssociations(CssCodeArea.CONFIG, "css");
	}

	private final Map<String, CodeAreaConfig> associations = new HashMap<>();

	public CodeAreaConfig getConfig(String extension) {
		return associations.get(extension.toLowerCase());
	}

	public void addAssociations(CodeAreaConfig config, String... extensions) {
		for(String extension : extensions) {
			associations.put(extension.toLowerCase(), config);
		}
	}

	public void removeAssociations(String... extensions) {
		for(String extension : extensions) {
			associations.remove(extension.toLowerCase());
		}
	}
}
