package org.architecturemining.samplingframework.samplingtechnique;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.deckfour.xes.model.XLog;
import org.deckfour.xes.model.XTrace;

public class RandomSampler extends AbstractSampleGenerator {

	protected XLog populateSample(XLog log, XLog sample, double ratio) {
		long amount = Math.round(log.size() * ratio);
		
		List<XTrace> myTraces = new ArrayList<>(log);
		
		Collections.shuffle(myTraces);
				
		for(int i = 0; i < amount ; i++) {
			copyTraceToLog(sample, myTraces.get(i));
		}
		
		return null;
	}
}
