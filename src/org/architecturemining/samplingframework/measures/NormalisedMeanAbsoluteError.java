package org.architecturemining.samplingframework.measures;

import org.architecturemining.samplingframework.models.DirectlyFollowsFrequencyMatrix;

/**
 * 
 * @author jmw
 *
 * Calculates the NMAE_m
 */
public class NormalisedMeanAbsoluteError extends AbstractMeasure {

	public double compute(DirectlyFollowsFrequencyMatrix<String> original,
			DirectlyFollowsFrequencyMatrix<String> sample, double expectedRatio) {
		
		double total = 0;
		double sum = 0;
		
		
		for(String from : original.getFrom()) {
			for(String to: original.getTo(from)) {
				double expected = expectedRatio * original.frequency(from, to);
				double sampled = sample.frequency(from, to);
				
				double value = Math.abs(sampled - expected);
				
				sum += value; 
				
				total += expected;
			}
		}
		
		return sum / total;
	}

}
