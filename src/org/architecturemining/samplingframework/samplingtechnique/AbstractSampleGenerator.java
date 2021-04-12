package org.architecturemining.samplingframework.samplingtechnique;

import org.deckfour.xes.factory.XFactoryRegistry;
import org.deckfour.xes.model.XAttributeMap;
import org.deckfour.xes.model.XEvent;
import org.deckfour.xes.model.XLog;
import org.deckfour.xes.model.XTrace;
import org.deckfour.xes.model.impl.XEventImpl;
import org.deckfour.xes.model.impl.XTraceImpl;

public abstract class AbstractSampleGenerator {
	
	private XLog log;
	
	public AbstractSampleGenerator(XLog log) {
		this.log = log;
	}
	
	public XLog getLog() {
		return log;
	}

	public XLog drawSample(double ratio) {
		XLog sample = XFactoryRegistry.instance().currentDefault().createLog();
		
		populateSample(sample, ratio);
		
		return sample;
	}
	
	protected abstract void populateSample(XLog sample, double ratio);
	
	protected void copyTraceToLog(XLog toAdd, XTrace trace) {
		XAttributeMap traceMap = (XAttributeMap) trace.getAttributes().clone();
        XTrace copy = new XTraceImpl(traceMap);
        
        for(XEvent event : trace) {
        	XAttributeMap attMap = (XAttributeMap) event.getAttributes().clone();
        	copy.add(new XEventImpl(attMap));
        }
        
        toAdd.add(copy);
	}
}
