package org.beryx.viewreka.dsl.parameter

import java.util.Map;

import groovy.transform.ToString

import org.beryx.viewreka.core.ViewrekaException;
import org.beryx.viewreka.dsl.BaseDelegate;
import org.beryx.viewreka.fxui.FxProject
import org.beryx.viewreka.fxui.FxView
import org.beryx.viewreka.model.DatasetProvider


@ToString(includePackage=false)
class ParameterDelegate<T> extends BaseDelegate {
	final String name
	boolean nullAllowed = false
	T minValue = null
	T maxValue = null
	boolean minValueAllowed = true
	boolean maxValueAllowed = true

	DatasetProvider datasetProvider = null
	int valColumn = 1
	String valColumnName = null
	int textValColumn = -1
	String textValColumnName = null
	int displayedValColumn = -1
	String displayedValColumnName = null


	private final Map<String, DatasetProvider> dataSetProviders

	public ParameterDelegate(String name, Map<String, DatasetProvider> dataSetProviders) {
		this.name = name
		this.dataSetProviders = dataSetProviders
		injectProperties(dataSetProviders.keySet())
	}

	def possibleValues(Map options) {
		def datasetName = options.dataset
		if(!datasetName) throw new ViewrekaException("possibleValues for parameter '$name': No dataset defined")
		def datasetProvider = dataSetProviders[datasetName]
		if(!(datasetProvider instanceof DatasetProvider)) throw new ViewrekaException("possibleValues for parameter '$name': unknown dataset '$datasetProvider'")
		this.datasetProvider = datasetProvider

		if(options.valueColumn instanceof Number) {
			this.valColumn = ((Number)options.valueColumn).intValue()
			this.valColumnName = null

		} else if(options.valueColumn) {
			this.valColumn = -1
			this.valColumnName = "${options.valueColumn}"
		} else {
			this.valColumn = 1
			this.valColumnName = null
		}

		if(options.textColumn instanceof Number) {
			this.textValColumn = ((Number)options.textColumn).intValue()
			this.textValColumnName = null

		} else if(options.textColumn) {
			this.textValColumn = -1
			this.textValColumnName = "${options.textColumn}"
		} else {
			this.textValColumn = -1
			this.textValColumnName = null
		}

		if(options.displayedColumn instanceof Number) {
			this.displayedValColumn = ((Number)options.displayedColumn).intValue()
			this.displayedValColumnName = null

		} else if(options.displayedColumn) {
			this.displayedValColumn = -1
			this.displayedValColumnName = "${options.displayedColumn}"
		} else {
			this.displayedValColumn = -1
			this.displayedValColumnName = null
		}
	}

}
