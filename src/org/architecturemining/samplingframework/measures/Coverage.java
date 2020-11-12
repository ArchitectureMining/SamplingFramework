/**
 * 
 */
package org.architecturemining.samplingframework.measures;

import org.architecturemining.samplingframework.models.DirectlyFollowsFrequencyMatrix;

/**
 * @author janma
 *
 */
public class Coverage extends AbstractMeasure {

	/**
	 * This measure calculates a simple coverage.
	 * Assumption: Sample is a subset of the original, i.e., DF(S) \ DF(L) is empty!
	 */
	public double compute(DirectlyFollowsFrequencyMatrix<String> original,
			DirectlyFollowsFrequencyMatrix<String> sample, double expectedRatio) {
		
		int total = 0;
		int covered = 0;
		
		for(String from: original.getFrom()) {
			for(String to: original.getTo(from)) {
				total++;
				if (sample.exists(from, to)) {
					covered++;
				}
			}
		}

		// Requires an explicit cast to become a fraction.
		return (double) covered / total;
	}

}
