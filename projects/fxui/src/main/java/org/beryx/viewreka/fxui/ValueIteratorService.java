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

public class ValueIteratorService extends Service<Void> {
	private static final Logger log = LoggerFactory.getLogger(ValueIteratorService.class);
	
	private final ObjectProperty<Parameter<?>> iteratedParameterProperty = new SimpleObjectProperty<>();
	private final IntegerProperty startValueIndexProperty = new SimpleIntegerProperty();
	private final LongProperty delayProperty = new SimpleLongProperty();
	
    private long lastStart;

    public Parameter<?> getIteratedParameter() {
    	return iteratedParameterProperty.get();
    }
    
    public void setIteratedParameter(Parameter<?> parameter) {
    	iteratedParameterProperty.set(parameter);
    }
    
    public int getStartValueIndex() {
    	return startValueIndexProperty.get();
    }
    
    public void setStartValueIndex(int index) {
    	startValueIndexProperty.set(index);
    }
    
    public long getDelay() {
    	return delayProperty.get();
    }
   
    public void setDelay(long delay) {
    	delayProperty.set(delay);
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
            		long delay = lastStart + getDelay() - System.currentTimeMillis();
            		if(delay > 0) {
            			Thread.sleep(delay);
            		}
            		lastStart = System.currentTimeMillis();
            	}
        		updateProgress(100.0, 100.0);
                return null;
            }
        };
    }
}
