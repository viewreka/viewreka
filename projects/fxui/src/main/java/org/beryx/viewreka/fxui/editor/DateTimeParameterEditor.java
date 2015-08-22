package org.beryx.viewreka.fxui.editor;

import java.net.URL;
import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import jfxtras.scene.control.CalendarTextField;

import org.beryx.viewreka.core.ViewrekaException;
import org.beryx.viewreka.parameter.DateConfiguration;
import org.beryx.viewreka.parameter.Parameter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A parameter editor that allows setting the value of a date/time parameter.
 * @param <T> the type of the parameter handled by this editor
 */
public class DateTimeParameterEditor<T> extends FxParameterEditor<T> {
    private static final Logger log = LoggerFactory.getLogger(DateTimeParameterEditor.class);

    private final DateConfiguration<T> dateConfig;
    private final boolean showTime;

    @FXML private Label lbPrmName;
    @FXML private CalendarTextField calendarText;

    /**
     * The default builder of DateTimeParameterEditor.
     * @param <TT> the type of the {@link DateTimeParameterEditor}s created by this builder
     */
    public static class Builder<TT> implements FxParameterEditorBuilder<TT> {
        private boolean showTime;

        @Override
        public FxParameterEditor<TT> createEditor(Parameter<TT> parameter, Parent parentPane) {
            return new DateTimeParameterEditor<>(parameter, showTime);
        }

        public Builder<TT> showTime(boolean val) { this.showTime = val; return this; }
    }


    /**
     * @param parameter the parameter handled by this editor
     * @param showTime true, if the editor should also show the time
     */
    @SuppressWarnings("unchecked")
    public DateTimeParameterEditor(Parameter<T> parameter, boolean showTime) {
        super(parameter);
        if(!(parameter instanceof DateConfiguration)) {
            throw new ViewrekaException("Parameter " + parameter.getName() + " does not implement DateConfiguration, therefore is not compatible with DateTimeParameterEditor.");
        }
        this.dateConfig = (DateConfiguration<T>)parameter;
        this.showTime = showTime;
    }


    @Override
    public void valueChanged(Parameter<T> prm, T oldValue) {
        throw new UnsupportedOperationException("A DateTimeParameterEditor cannot act as ParameterListener.");
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        check("lbPrmName", lbPrmName);
        check("calendarText", calendarText);

        log.debug("Initializing DateTimeParameterEditor({}).", parameter);

        getStylesheets().add(this.getClass().getResource("/editor.css").toExternalForm());

        lbPrmName.setText(parameter.getName());

        Locale locale = dateConfig.getLocale();
        calendarText.setLocale(locale);
        calendarText.setDateFormat(dateConfig.getDateFormat());

        calendarText.setShowTime(showTime);

        log.debug("locale: {}, datePattern: {}", locale, dateConfig.getDatePattern());

        calendarText.setAllowNull(parameter.isNullAllowed());

        String sVal = parameter.getValueAsString();
        if(sVal != null && !"null".equals(sVal)) {
            Calendar calendar = Calendar.getInstance(locale);
            try {
                Date date = dateConfig.getDateFormat().parse(sVal);
                calendar.setTime(date);
                calendarText.setCalendar(calendar);
            } catch(ParseException e) {
                throw new ViewrekaException("Parameter " + parameter.getName() + ": cannot parse '" + sVal + "' using the date format '" + dateConfig.getDatePattern() + "'");
            }
        }

        calendarText.calendarProperty().addListener((observable, oldValue, newValue) -> {
            if(newValue == null) {
                parameter.setValue(null);
            } else {
                long millis = newValue.getTimeInMillis();
                parameter.setValue(dateConfig.fromMilliseconds(millis));
            }
            updateEditor();
        });

        calendarText.textProperty().addListener((observable, oldValue, newValue) -> updateEditor());

    }

    @Override
    public void updateEditor() {
        // Nothing to do here
    }
}
