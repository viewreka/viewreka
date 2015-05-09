package org.beryx.viewreka.fxui;

import static org.beryx.viewreka.core.Util.requireNonNull;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import org.beryx.viewreka.core.ViewrekaException;
import org.beryx.viewreka.fxui.chart.FxChartBuilder;
import org.beryx.viewreka.fxui.editor.ParameterEditorManager;
import org.beryx.viewreka.model.ViewModelImpl;
import org.beryx.viewreka.parameter.Parameter;
import org.beryx.viewreka.parameter.Parameter.Value;
import org.beryx.viewreka.project.ParameterEditor;
import org.beryx.viewreka.project.ParameterEditorBuilder;
import org.beryx.viewreka.settings.ViewSettings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FxViewImpl extends ViewModelImpl implements FxView {
	private static final Logger log = LoggerFactory.getLogger(FxViewImpl.class);

	private final ViewSettings viewSettings;
	private final ParameterEditorManager parameterEditorManager = new ParameterEditorManager();

	private final Map<String, FxChartBuilder<?>> chartBuilders = new LinkedHashMap<>();
	private Parameter<?> chartParameter;

	public FxViewImpl(String name, String description, ViewSettings viewSettings) {
		super(name, description);
		this.viewSettings = viewSettings;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void initParameters() {
		Map<String, String> parametersMap = viewSettings.getParameters();
		Map<String, Parameter<?>> parameters = getParameters();
		parameters.values().forEach(prm -> {
			String prmName = prm.getName();
			String prmVal = parametersMap.get(prmName);
			if(prmVal != null) {
				try {
					prm.setValueFromString(prmVal);
				} catch(Exception e) {
					log.warn("Cannot initialize parameter {} of view {} with value {}", prmName, getName(), prmVal);
				}
			} else if(prm.isIterable()) {
				List<Value> possibleValues = (List)prm.getPossibleValues();
				if(!possibleValues.isEmpty()) {
					prm.setPossibleValue(0);
				}
			}
			prm.addListener((parameter, oldValue) -> parametersMap.put(prmName, parameter.getValueAsString()));
		});
		getDatasetProviders().values().forEach(datasetProvider -> {
			log.trace("Adding dirty listeners for datasetProvider {}", datasetProvider);

			Stream<Parameter<?>> prmStream = datasetProvider.getParameterNames().stream().map(nm -> {
				Parameter<?> prm = parameters.get(nm);
				if(prm == null) throw new ViewrekaException("Undefined parameter: " + nm);
				return prm;
			});
			prmStream.forEach(prm -> {
				log.trace("Adding listener for parameter {} in order to mark dataset {} as dirty", prm.getName(), datasetProvider.getName());
				prm.addListener((p,v) -> datasetProvider.setDirty());
			});
		});

	}

	@Override
	public ViewSettings getViewSettings() {
		return viewSettings;
	}

	@Override
	public <T, E extends ParameterEditor<T>, P> ParameterEditorBuilder<T, E, P> getParameterEditorBuilder(Parameter<T> parameter) {
		return parameterEditorManager.getBuilder(parameter);
	}

	public ParameterEditorManager getParameterEditorManager() {
		return parameterEditorManager;
	}

	@Override
	public Map<String, FxChartBuilder<?>> getChartBuilders() {
		return chartBuilders;
	}

	public <D> void addChartBuilder(String name, FxChartBuilder<D> chartBuilder) {
		this.chartBuilders.put(name, chartBuilder);
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public <T> Parameter<T> getChartParameter() {
		return (Parameter)chartParameter;
	}

	public <T> void setChartParameter(Parameter<T> chartParameter) {
		this.chartParameter = chartParameter;
	}

	public void validate() {
		requireNonNull(viewSettings, "viewSettings");
	}
}
