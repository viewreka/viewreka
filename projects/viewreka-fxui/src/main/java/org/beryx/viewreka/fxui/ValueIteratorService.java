/**
 * Copyright 2015-2016 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.beryx.viewreka.fxui;

import java.util.List;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.LongProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.concurrent.Service;
import javafx.concurrent.Task;

import org.beryx.viewreka.parameter.Parameter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A JavaFX service used to iterate through the possible values of an iterated parameter.
 */
public class ValueIteratorService extends Service<Void> {
    private static final Logger log = LoggerFactory.getLogger(ValueIteratorService.class);

    private final ObjectProperty<Parameter<?>> iteratedParameterProperty = new SimpleObjectProperty<>();
    private final IntegerProperty startValueIndexProperty = new SimpleIntegerProperty();
    private final LongProperty chartFrameDurationMillisProperty = new SimpleLongProperty();

    private long lastStart;

    /**
     * @return the parameter providing the possible values through which this service will iterate
     */
    public Parameter<?> getIteratedParameter() {
        return iteratedParameterProperty.get();
    }

    /**
     * @param parameter the parameter providing the possible values through which this service should iterate
     */
    public void setIteratedParameter(Parameter<?> parameter) {
        iteratedParameterProperty.set(parameter);
    }

    /**
     * @return the index in the list of possible values of the iterated parameter from which this service will start iterating
     */
    public int getStartValueIndex() {
        return startValueIndexProperty.get();
    }

    /**
     * @param index the index in the list of possible values of the iterated parameter from which this service should start iterating
     */
    public void setStartValueIndex(int index) {
        startValueIndexProperty.set(index);
    }

    /**
     * @return the number of milliseconds between two consecutive chart frames (each chart frame corresponds to a possible value of the iterated parameter)
     */
    public long getChartFrameDurationMillis() {
        return chartFrameDurationMillisProperty.get();
    }

    /**
     * @param millis the number of milliseconds between two consecutive chart frames (each chart frame corresponds to a possible value of the iterated parameter)
     */
    public void setChartFrameDurationMillis(long millis) {
        chartFrameDurationMillisProperty.set(millis);
    }

    @Override
    public void start() {
        lastStart = System.currentTimeMillis();
        super.start();
    }

    @Override
    protected Task<Void> createTask() {
        return new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                Parameter<?> iteratedParameter = getIteratedParameter();
                log.debug("ValueIteratorService: iteratedParameter = {}", iteratedParameter);
                if(iteratedParameter == null) return null;

                List<?> possibleValues = iteratedParameter.getPossibleValues();
                int valueCount = possibleValues.size();

                int startValueIndex = getStartValueIndex();
                double maxIterations = valueCount - startValueIndex;
                log.debug("ValueIteratorService: valueCount: {}, startValueIndex: {}, maxIterations: {}", valueCount, startValueIndex, maxIterations);
                if(maxIterations <= 0) return null;
                for(int i = startValueIndex; !isCancelled() && (i < valueCount); i++) {
                    iteratedParameter.setPossibleValue(i);
                    log.debug("Iteration #{} of {}: iteratedParameter: {}", i, valueCount, iteratedParameter);

                    double percentDone = 100.0 * (i - startValueIndex) / maxIterations;
                    updateProgress(percentDone, 100.0);
                    long millis = lastStart + getChartFrameDurationMillis() - System.currentTimeMillis();
                    if(millis > 0) {
                        Thread.sleep(millis);
                    }
                    lastStart = System.currentTimeMillis();
                }
                updateProgress(100.0, 100.0);
                return null;
            }
        };
    }
}
