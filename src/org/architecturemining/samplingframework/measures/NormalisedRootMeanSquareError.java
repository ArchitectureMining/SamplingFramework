/**
 * 
 */
package org.architecturemining.samplingframework.measures;

import org.architecturemining.samplingframework.models.DirectlyFollowsFrequencyMatrix;

/**
 * @author jmw
 * 
 * Computes the NRMSE_m
 *
 * sqrt(1/n * sum( (s - e)^2 ) ) / ( 1/n * sum(e) )
 * ==
 * sqrt(1/n) * sqrt( sum( (s - e)^2 )) / ( 1/n * sum(e) )
 * ==
 * sqrt( sum( (s - e)^2 ) ) / (sum(e) * sqrt( 1 / n ) )  
 */
public class NormalisedRootMeanSquareError extends AbstractMeasure {

	public double compute(DirectlyFollowsFrequencyMatrix<String> original,
			DirectlyFollowsFrequencyMatrix<String> sample, double expectedRatio) {
		
		int total = 0;
		double weightedTotal = 0;
		double sum = 0;
		
		
		for(String from : original.getFrom()) {
			for(String to: original.getTo(from)) {
				double expected = expectedRatio * original.frequency(from, to);
				double sampled = sample.frequency(from, to);
				
				sum += Math.pow(sampled - expected, 2); 
				weightedTotal += expected;
				total++;
			}
		}
		
		return Math.sqrt(sum) / ( weightedTotal * Math.sqrt((double) 1 / total) );
	}

}
