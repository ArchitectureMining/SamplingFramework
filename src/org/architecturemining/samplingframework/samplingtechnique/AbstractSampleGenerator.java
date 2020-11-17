package org.architecturemining.samplingframework.samplingtechnique;

import org.deckfour.xes.factory.XFactoryRegistry;
import org.deckfour.xes.model.XAttributeMap;
import org.deckfour.xes.model.XEvent;
import org.deckfour.xes.model.XLog;
import org.deckfour.xes.model.XTrace;
import org.deckfour.xes.model.impl.XEventImpl;
import org.deckfour.xes.model.impl.XTraceImpl;

public abstract class AbstractSampleGenerator {

	public XLog generateSample(XLog log, double ratio) {
		XLog sample = XFactoryRegistry.instance().currentDefault().createLog();
		
		populateSample(log, sample, ratio);
		
		return sample;
	}
	
	protected abstract XLog populateSample(XLog log, XLog sample, double ratio);
	
	protected void copyTraceToLog(XLog log, XTrace trace) {
		XAttributeMap traceMap = (XAttributeMap) trace.getAttributes().clone();
        XTrace copy = new XTraceImpl(traceMap);
        
        for(XEvent event : trace) {
        	XAttributeMap attMap = (XAttributeMap) event.getAttributes().clone();
        	copy.add(new XEventImpl(attMap));
        }
        
        log.add(copy);
	}
}
