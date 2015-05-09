package org.beryx.viewreka.dsl

import groovy.transform.ToString;

import org.beryx.viewreka.fxui.FxProject;

@ToString(includePackage=false)
class ViewDelegate extends BaseDelegate {
	final String id
	String name
	String description

	public ViewDelegate(String viewId, FxProject project) {
		this.id = viewId
		this.name = viewId
		injectProperties(project.dataSources.keySet())
	}
}
