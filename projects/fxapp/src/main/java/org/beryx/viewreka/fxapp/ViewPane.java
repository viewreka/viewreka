package org.beryx.viewreka.fxapp;

import static org.beryx.viewreka.core.Util.requireNonNull;

import java.net.URL;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.function.BiFunction;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.collections.ObservableList;
import javafx.concurrent.Worker.State;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.SplitPane;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

import org.beryx.viewreka.chart.ChartController;
import org.beryx.viewreka.core.ViewrekaException;
import org.beryx.viewreka.fxcommons.Dialogs;
import org.beryx.viewreka.fxcommons.FXMLNode;
import org.beryx.viewreka.fxui.FxView;
import org.beryx.viewreka.fxui.ValueIteratorService;
import org.beryx.viewreka.fxui.chart.FxChartBuilder;
import org.beryx.viewreka.fxui.editor.FxParameterEditor;
import org.beryx.viewreka.fxui.editor.FxParameterEditorBuilder;
import org.beryx.viewreka.parameter.Parameter;
import org.beryx.viewreka.parameter.Parameter.Value;
import org.beryx.viewreka.parameter.ParameterListener;
import org.beryx.viewreka.project.ParameterEditor;
import org.beryx.viewreka.settings.ProjectSettings;
import org.beryx.viewreka.settings.ViewSettings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The pane for displaying a view.
 * @param <D> the type of the data used by the view charts.
 */
public class ViewPane<D> extends SplitPane implements FXMLNode {
    private static final Logger log = LoggerFactory.getLogger(ViewPane.class);

    public static final String PROP_VIEW_SPLIT_PANE_DIVIDER_POSITION = "viewSplitPane.divider.position";
    public static final String PROP_VIEW_CURRENT_CHART = "view.current.chart";

    private static final Long[] CHART_FRAME_DURATIONS = {10L, 20L, 50L, 100L, 200L, 500L, 1000L, 2000L, 5000L};
    private static final long DEFAULT_CHART_FRAME_DURATION = 200L;

    private final static String STATUS_PLAYING = "playing";
    private final static String STATUS_PAUSED = "paused";


    private final FxView view;
    private final ProjectSettings projectSettings;

    private final Map<String, FxParameterEditor<?>> editorMap = new LinkedHashMap<>();

    private ChangeListener<String> currentIteratedValuesListener = null;

    private final ParameterListener<?> iteratedParameterListener = (prm, oldValue) -> Platform.runLater(() -> updateIteratedValues());

    private String currentChartName;
    private FxChartBuilder<D> chartBuilder;
    private ChartController<D> chartController;
    private ValueIteratorService valueIteratorService;

    @FXML private SplitPane viewSplitPane;
    @FXML private AnchorPane chartPane;
    @FXML private FlowPane stylePane;
    @FXML private ComboBox<String> cmbChartStyle;
    @FXML private BorderPane dashboard;
    @FXML private ScrollPane parameterScrollPane;
    @FXML private VBox parameterPane;
    @FXML private VBox controlPane;

    @FXML private Button butFirstValue;
    @FXML private Button butPreviousValue;
    @FXML private Button butNextValue;
    @FXML private Button butLastValue;
    @FXML private Button butPlay;

    @FXML private StackPane iteratedParameterStack;
    @FXML private HBox boxIteratedParameterMulti;
    @FXML private Label lbIteratedParameterSingle;
    @FXML private ComboBox<String> cmbIteratedParameter;
    @FXML private ComboBox<String> cmbIteratedValues;
    @FXML private ComboBox<Long> cmbFrameDuration;

    private ViewPane(FxView view, ProjectSettings projectSettings) {
        this.view = view;
        this.projectSettings = projectSettings;
    }

    public static <DD> ViewPane<DD> fromModel(FxView view, ProjectSettings projectSettings) {
        ViewPane<DD> viewPane = new ViewPane<>(view, projectSettings).load();
        viewPane.configure();
        return viewPane;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        check("viewSplitPane", viewSplitPane);
        check("chartPane", chartPane);
        check("dashboard", dashboard);
        check("stylePane", stylePane);
        check("cmbChartStyle", cmbChartStyle);
        check("parameterScrollPane", parameterScrollPane);
        check("parameterPane", parameterPane);
        check("controlPane", controlPane);
        check("cmbIteratedParameter", cmbIteratedParameter);
        check("cmbIteratedValues", cmbIteratedValues);
        check("butFirst", butFirstValue);
        check("butPrevious", butPreviousValue);
        check("butNext", butNextValue);
        check("butLast", butLastValue);
        check("butPlay", butPlay);
        check("cmbFrameDuration", cmbFrameDuration);


        String chartName = view.getViewSettings().getProperty(PROP_VIEW_CURRENT_CHART, null, true);
        try {
            setChart(chartName);
        } catch(Exception e) {
            Dialogs.error("View Error", "Cannot create the view '" + view.getName() + "'.", e);
        }
    }

    @SuppressWarnings("unchecked")
    private void setChart(String chartName) {
        Map<String, FxChartBuilder<?>> chartBuilders = view.getChartBuilders();
        if(chartBuilders.isEmpty()) throw new ViewrekaException("No charts defined for view" + view.getName());

        currentChartName = chartName;
        if(currentChartName != null) {
            chartBuilder = (FxChartBuilder<D>)chartBuilders.get(chartName);
            if(chartBuilder == null) {
                Dialogs.error("Chart error", "Chart with name " + currentChartName + " does not exist.");
                currentChartName = null;
            }
        }
        if(currentChartName == null) {
            Entry<String, FxChartBuilder<?>> entry = chartBuilders.entrySet().iterator().next();
            this.chartBuilder = (FxChartBuilder<D>)entry.getValue();
            currentChartName = entry.getKey();
        }
        if(chartBuilder == null) throw new ViewrekaException("Chart with name " + currentChartName + " is null.");


        view.getViewSettings().setProperty(PROP_VIEW_CURRENT_CHART, currentChartName);
        cmbChartStyle.getItems().clear();
        chartBuilder.getChartStyles().forEach(style -> cmbChartStyle.getItems().add(style));

        cmbChartStyle.getSelectionModel().select(0);
        if(cmbChartStyle.getItems().size() <= 1) {
            controlPane.getChildren().remove(stylePane);
        }
    }

    public void updateChartStyle() {
        configureChartController();
        updateChart();
    }

    @SuppressWarnings("unchecked")
    private void configureChartController() {
        Map<String, FxChartBuilder<?>> chartBuilders = view.getChartBuilders();
        if(chartBuilders.isEmpty()) throw new ViewrekaException("No charts defined for view" + view.getName());

        // TODO / FIXME - display all chartBuilders, not only the first one
        Entry<String, FxChartBuilder<?>> chartBuilderEntry = chartBuilders.entrySet().iterator().next();
        this.chartBuilder = (FxChartBuilder<D>)chartBuilderEntry.getValue();

        String chartStyle = cmbChartStyle.getSelectionModel().getSelectedItem();
        chartBuilder.setCurrentChartStyle(chartStyle);

        chartBuilder.setStylesheetSupplier(() -> projectSettings.getProperty(Viewreka.PROP_CHART_STYLESHEET, null, true));

        chartPane.getChildren().clear();
        chartController = chartBuilder.createController(chartPane);

    }
    @SuppressWarnings({ "rawtypes", "unchecked" })
    private void configure() {

        configureChartController();

        Parameter<?> chartParameter = view.getChartParameter();

        List<FxParameterEditor<?>> prmEditors = new ArrayList<>();
        for(Parameter<?> parameter : view.getParameters().values()) {
            if(parameter == chartParameter) {
                log.debug("No FxParameterEditor created for the chart parameter {}", parameter);
                continue;
            }
            log.debug("Creating FxParameterEditor for {}", parameter);

            FxParameterEditorBuilder<?> parameterEditorBuilder = (FxParameterEditorBuilder)requireNonNull(
                    view.getParameterEditorBuilder((Parameter)parameter), "parameterEditorBuilder for " + parameter.getName());
            ParameterEditor prmEditor = requireNonNull(parameterEditorBuilder.getEditor((Parameter)parameter), "parameterEditor for " + parameter.getName());
            FxParameterEditor<?> fxPrmEditor = (FxParameterEditor<?>)prmEditor;
            editorMap.put(parameter.getName(), fxPrmEditor);
            prmEditors.add(fxPrmEditor);
            parameter.addListener((prm, oldValue) -> updateChart());
        }

        for(Parameter<?> parameter : view.getParameters().values()) {
            for(Parameter<?> affectedParameter : parameter.getAffectedParameters()) {
                String prmName = affectedParameter.getName();
                if(affectedParameter == chartParameter) {
                    // TODO - fix it
                    String chartName = view.getChartBuilders().entrySet().iterator().next().getKey();
                    throw new ViewrekaException("Parameter '" + prmName + "' cannot be defined as parameter of chart '" + chartName + "', because it is affected by '" + parameter.getName() + "'");
                }
                FxParameterEditor<?> prmEditor = Objects.requireNonNull(editorMap.get(prmName), "No editor exists for parameter " + prmName);
                ((Parameter)parameter).addListener(prmEditor);
            }
        }

        ViewSettings viewSettings = view.getViewSettings();

        Stream<Parameter<?>> prmStream = view.getParameters().values().stream();
        Stream<Parameter<?>> iterableTerminals = prmStream.filter(prm -> ((prm != chartParameter) && prm.isIterable() && prm.getAffectedParameters().isEmpty()));
        String[] iterableItems = iterableTerminals.map(prm -> prm.getName()).toArray(String[]::new);
        if(iterableItems.length <= 0) {
            controlPane.setVisible(false);
        } else {
            cmbIteratedParameter.getItems().setAll(iterableItems);
            if(iterableItems.length > 0) {
                String selIteratedPrmName = viewSettings.getSelectedIteratedParameter();
                if(!cmbIteratedParameter.getItems().contains(selIteratedPrmName)) selIteratedPrmName = iterableItems[0];
                cmbIteratedParameter.setValue(selIteratedPrmName);
                cmbIteratedParameter.valueProperty().addListener((observable, oldValue, newValue) -> {
                    viewSettings.setSelectedIteratedParameter(newValue);
                    updateIteratedValues();
                    updateParameterPane();
                });
            }
            viewSettings.setSelectedIteratedParameter(cmbIteratedParameter.getValue());
            updateIteratedValues();

            updateParameterPane();

            cmbFrameDuration.getItems().setAll(CHART_FRAME_DURATIONS);
            long chartFrameDurationMillis = viewSettings.getChartFrameDurationMillis();
            if(chartFrameDurationMillis < 0) chartFrameDurationMillis = DEFAULT_CHART_FRAME_DURATION;
            cmbFrameDuration.setValue(chartFrameDurationMillis);
            cmbFrameDuration.valueProperty().addListener((observable, oldValue, newValue) -> viewSettings.setChartFrameDurationMillis(newValue));

            if(iterableItems.length == 1) {
                lbIteratedParameterSingle.setText(iterableItems[0] + ":");
                iteratedParameterStack.getChildren().remove(boxIteratedParameterMulti);
            } else {
                iteratedParameterStack.getChildren().remove(lbIteratedParameterSingle);
            }
        }

        if(prmEditors.isEmpty() || (prmEditors.size() == 1 && iterableItems.length > 0)) {
            parameterScrollPane.setVisible(false);
        } else {
            updateParameterPane();
            prmEditors.stream().forEach(editor -> editor.updateEditor());
        }

        double dividerPosition = -1;
        if(controlPane.isVisible() || parameterScrollPane.isVisible()) {
            Divider divider = viewSplitPane.getDividers().get(0);
            dividerPosition = viewSettings.getProperty(PROP_VIEW_SPLIT_PANE_DIVIDER_POSITION, -1.0, false);
            divider.positionProperty().addListener((observable, oldValue, newValue) ->
                viewSettings.setProperty(PROP_VIEW_SPLIT_PANE_DIVIDER_POSITION, newValue.doubleValue()));
            if(!controlPane.isVisible()) {
                dashboard.setLeft(null);
            }
            if(!parameterScrollPane.isVisible()) {
                dashboard.setCenter(null);
            }
        } else {
            viewSplitPane.getItems().remove(dashboard);
        }

        valueIteratorService = new ValueIteratorService();
        valueIteratorService.setOnCancelled(event -> {valueIteratorServiceError(); updatePlayButton();});
        valueIteratorService.setOnFailed(event -> {valueIteratorServiceError(); updatePlayButton();});
        valueIteratorService.setOnRunning(event -> updatePlayButton());
        valueIteratorService.setOnSucceeded(event -> updatePlayButton());

        updateChart();

        double splitPaneDividerPosition = dividerPosition;
        Platform.runLater(() -> {
            if(splitPaneDividerPosition >= 0) {
                Divider divider = viewSplitPane.getDividers().get(0);
                divider.setPosition(splitPaneDividerPosition);
            }
        });
    }

    private void valueIteratorServiceError() {
        log.warn("valueIteratorService {}: {}", valueIteratorService.getState(), valueIteratorService.getMessage());
        if(valueIteratorService.getException() != null) {
            log.error("valueIteratorService exception", valueIteratorService.getException());
        }
    }

    private void updateParameterPane() {
        parameterPane.getChildren().clear();
        String prmName = cmbIteratedParameter.getValue();
        FxParameterEditor<?> hiddenEditor = (prmName == null) ? null : editorMap.get(prmName);
        for(FxParameterEditor<?> editor : editorMap.values()) {
            if(editor != hiddenEditor) {
                editor.updateEditor();
                parameterPane.getChildren().add(editor);
                editor.autosize();
            }
        }
        parameterPane.autosize();
    }

    public Parameter<?> getIteratedParameter() {
        String prmName = cmbIteratedParameter.getValue();
        if(prmName == null) return null;
        return view.getParameters().get(prmName);
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    private void updateIteratedValues() {
        removeIteratedValuesListener();

        Parameter<?> parameter = getIteratedParameter();
        if(parameter == null) return;

        view.getParameters().values().forEach(prm -> ((Parameter)prm).removeListener(iteratedParameterListener));
        ((Parameter)parameter).addListener(iteratedParameterListener);

        configureIteratedDisplayedValue(parameter);
        configureIteratedValuesListener(parameter);
    }

    private void configureIteratedDisplayedValue(Parameter<?> parameter) {
        log.debug("configureIteratedDisplayedValue({})", parameter);

        cmbIteratedValues.getItems().clear();
        String currentDisplayedValue = null;
        @SuppressWarnings({ "unchecked", "rawtypes" })
        List<Value<?>> possibleValues = (List)parameter.getPossibleValues();
        for(Value<?> value : possibleValues) {
            cmbIteratedValues.getItems().add(value.getDisplayedValue());
            if(Objects.equals(value.getValue(), parameter.getValue())) {
                currentDisplayedValue = value.getDisplayedValue();
            }
        }
        if(currentDisplayedValue == null && !possibleValues.isEmpty()) {
            currentDisplayedValue = possibleValues.get(0).getDisplayedValue();
        }
        cmbIteratedValues.setValue(currentDisplayedValue);
    }

    private void removeIteratedValuesListener() {
        if(currentIteratedValuesListener != null) {
            cmbIteratedValues.valueProperty().removeListener(currentIteratedValuesListener);
            currentIteratedValuesListener = null;
        }
        cmbIteratedValues.getItems().clear();
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    private void configureIteratedValuesListener(Parameter parameter) {
        log.debug("configureIteratedValuesListener({})", parameter);

        currentIteratedValuesListener = (observable, oldValue, newValue) -> {

            log.debug("Calling iteratedValuesListener({})", parameter);

            String currentDisplayedValue = cmbIteratedValues.getValue();
            List<Value<?>> possibleValues = parameter.getPossibleValues();
            for(Value<?> value : possibleValues) {
                if(Objects.equals(value.getDisplayedValue(), currentDisplayedValue)) {
                    parameter.setValue(value.getValue());
                }
            }
        };
        cmbIteratedValues.valueProperty().addListener(currentIteratedValuesListener);
    }

    private void updateChart() {
        Platform.runLater(() -> updateButtons());

        boolean allValid = view.getParameters().values().stream().collect(Collectors.reducing(true, prm -> prm.isValid(), Boolean::logicalAnd));
        if(!allValid) {
            Platform.runLater(() -> chartController.displayChart(null));
        } else {
            D chartData = chartController.createChartData();
            Platform.runLater(() -> chartController.displayChart(chartData));
        }
    }

    private <T> void updateButtons() {
        String prmName = cmbIteratedParameter.getValue();
        if(prmName == null) return;
        @SuppressWarnings("unchecked")
        Parameter<T> parameter = (Parameter<T>)view.getParameters().get(prmName);
        if(parameter == null) return;

        int currentIndex = getValueIndex(parameter);
        boolean isFirst = (currentIndex <= 0);
        boolean isLast = (currentIndex >= parameter.getPossibleValues().size() - 1);
        butFirstValue.setDisable(isFirst);
        butPreviousValue.setDisable(isFirst);
        butNextValue.setDisable(isLast);
        butLastValue.setDisable(isLast);
        butPlay.setDisable(isLast);

        updatePlayButton();
    }

    private void updatePlayButton() {
        boolean playing = isPlaying();
        ObservableList<String> styleClass = butPlay.getStyleClass();
        styleClass.remove(playing ? STATUS_PAUSED : STATUS_PLAYING);
        String newStyle = playing ? STATUS_PLAYING : STATUS_PAUSED;
        if(!styleClass.contains(newStyle)) styleClass.add(newStyle);
    }

    private static <T> int getValueIndex(Parameter<T> parameter) {
        T currentValue = parameter.getValue();
        List<Value<T>> possibleValues = parameter.getPossibleValues();
        int currentIndex = 0;
        int valueCount = possibleValues.size();
        for(int i=0; i<valueCount; i++) {
            if(Objects.equals(possibleValues.get(i).getValue(), currentValue)) {
                currentIndex = i;
                break;
            }
        }
        return currentIndex;
    }

    public <T> void setIteratedValue(BiFunction<List<Value<T>>, Integer, Integer> valueIndexSupplier) {
        String prmName = cmbIteratedParameter.getValue();
        if(prmName == null) return;
        @SuppressWarnings("unchecked")
        Parameter<T> parameter = (Parameter<T>)view.getParameters().get(prmName);
        if(parameter == null) return;

        List<Value<T>> possibleValues = parameter.getPossibleValues();
        if(possibleValues.isEmpty()) return;

        T currentValue = parameter.getValue();
        int valueCount = possibleValues.size();
        for(int i=0; i<valueCount; i++) {
            if(Objects.equals(possibleValues.get(i).getValue(), currentValue)) {
                Integer newIndex = valueIndexSupplier.apply(possibleValues, i);
                if(newIndex != null) {
                    if(newIndex < 0) newIndex = 0;
                    if(newIndex >= valueCount) newIndex = valueCount - 1;
                    parameter.setValue(possibleValues.get(newIndex).getValue());
                }
                break;
            }
        }
    }

    public void showFirst() {
        setIteratedValue((values, currIdx) -> 0);
    }

    public void showPrevious() {
        setIteratedValue((values, currIdx) -> currIdx - 1);
    }

    public void showNext() {
        setIteratedValue((values, currIdx) -> currIdx + 1);
    }

    public void showLast() {
        setIteratedValue((values, currIdx) -> values.size() - 1);
    }

    public long getChartFrameDurationMillis() {
        return Optional.ofNullable(cmbFrameDuration.getValue()).orElse(CHART_FRAME_DURATIONS[0]);
    }

    public boolean isPlaying() {
        if(valueIteratorService == null) return false;
        State state = valueIteratorService.getState();
        return (state == State.SCHEDULED || state == State.RUNNING);
    }

    public void play() {
        if(isPlaying()) {
            log.debug("Stop playing...");
            valueIteratorService.cancel();
        } else {
            log.debug("Start playing...");
            valueIteratorService.reset();

            String prmName = cmbIteratedParameter.getValue();
            if(prmName == null) return;
            Parameter<?> parameter = view.getParameters().get(prmName);
            if(parameter == null) return;

            valueIteratorService.setIteratedParameter(parameter);
            valueIteratorService.setStartValueIndex(getValueIndex(parameter));
            valueIteratorService.setChartFrameDurationMillis(getChartFrameDurationMillis());

            valueIteratorService.start();
        }
    }

    public void updateChartFrameDuration() {
        if(valueIteratorService != null) {
            valueIteratorService.setChartFrameDurationMillis(getChartFrameDurationMillis());
        }
    }

    public WritableImage getChartImage() {
        Node chartNode = chartPane;
        ObservableList<Node> children = chartPane.getChildren();
        if(children != null && !children.isEmpty()) {
            chartNode = children.get(0);
        }
        return chartNode.snapshot(null, null);
    }

    FxChartBuilder<D> getChartBuilder() {
        return chartBuilder;
    }

}
