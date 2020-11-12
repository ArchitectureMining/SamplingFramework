/**
 * 
 */
package org.architecturemining.samplingframework.measures;

import org.architecturemining.samplingframework.models.DirectlyFollowsFrequencyMatrix;

/**
 * @author jmw
 *
 * Calculates the sRMSPE
 */
public class SymmetricRootMeanSquarePercentageError extends AbstractMeasure {

	public double compute(DirectlyFollowsFrequencyMatrix<String> original,
			DirectlyFollowsFrequencyMatrix<String> sample, double expectedRatio) {

		int total = 0;
		double sum = 0;
		
		for(String from : original.getFrom()) {
			for(String to: original.getTo(from)) {
				double expected = expectedRatio * original.frequency(from, to);
				double sampled = sample.frequency(from, to);
				
				sum += Math.pow( (Math.abs(expected - sampled) / (expected + sampled))  , 2); 
				
				total++;
			}
		}
		
		return Math.sqrt(sum / total);
		
	}

}
