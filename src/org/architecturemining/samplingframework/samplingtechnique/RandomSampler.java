package org.architecturemining.samplingframework.samplingtechnique;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.deckfour.xes.model.XLog;
import org.deckfour.xes.model.XTrace;

public class RandomSampler extends AbstractSampleGenerator {

	private List<XTrace> myTraces;
	
	public RandomSampler(XLog log) {
		super(log);
		myTraces = new ArrayList<>(log);		
	}

	protected void populateSample(XLog sample, double ratio) {
		long amount = Math.round(getLog().size() * ratio);
		
		Collections.shuffle(myTraces);
				
		for(int i = 0; i < amount ; i++) {
			copyTraceToLog(sample, myTraces.get(i));
		}
	}
}
