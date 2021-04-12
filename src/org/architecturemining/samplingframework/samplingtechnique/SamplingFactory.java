package org.architecturemining.samplingframework.samplingtechnique;

import org.deckfour.xes.model.XLog;

public class SamplingFactory {
	
	private static RandomSampler randomSampler = null;
	
	public static AbstractSampleGenerator createRandomSample(XLog log, double ratio) {
		if (randomSampler == null) {
			randomSampler = new RandomSampler(log);
		}
		return randomSampler;
	}

}
