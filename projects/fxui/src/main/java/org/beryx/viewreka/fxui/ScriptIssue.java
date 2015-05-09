package org.beryx.viewreka.fxui;

public interface ScriptIssue {
	String getMessage();
	boolean isFatal();
	Throwable getError();
	String getSourceLocator();
	int getLine();
	int getColumn();
}
