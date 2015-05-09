package org.beryx.viewreka.project;

public interface ProjectReader<P extends Project<?>> {
	P getProject(String projectUri);
}
