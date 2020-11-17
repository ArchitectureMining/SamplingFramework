package org.architecturemining.samplingframework.samplingtechnique;

import org.deckfour.xes.model.XLog;

public class SamplingFactory {
	
	private static RandomSampler randomSampler = null;
	
	public static XLog createRandomSample(XLog log, double ratio) {
		if (randomSampler == null) {
			randomSampler = new RandomSampler();
		}
		return randomSampler.generateSample(log, ratio);
	}

}
