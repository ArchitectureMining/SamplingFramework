package org.architecturemining.samplingframework.measures;

import org.architecturemining.samplingframework.algorithms.DirectlyFollowsComputer;
import org.architecturemining.samplingframework.models.DirectlyFollowsFrequencyMatrix;
import org.deckfour.xes.model.XLog;

public abstract class AbstractMeasure {

	public double compute(XLog originalLog, XLog sampleLog) {
		return compute(originalLog, sampleLog, 1);
	}
	
	public double compute(XLog originalLog, XLog sampleLog, double expectedRatio) {
		DirectlyFollowsFrequencyMatrix<String> original = DirectlyFollowsComputer.computeMatrix(originalLog);
		DirectlyFollowsFrequencyMatrix<String> sample = DirectlyFollowsComputer.computeMatrix(sampleLog);
		
		return compute(original, sample, expectedRatio);
	}
	
	public double compute(DirectlyFollowsFrequencyMatrix<String> original, DirectlyFollowsFrequencyMatrix<String> sample) {
		return compute(original, sample, 1);
	}
	
	public abstract double compute(DirectlyFollowsFrequencyMatrix<String> original, DirectlyFollowsFrequencyMatrix<String> sample, double expectedRatio); 
}
