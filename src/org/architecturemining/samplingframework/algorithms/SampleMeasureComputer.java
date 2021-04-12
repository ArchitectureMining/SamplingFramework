package org.architecturemining.samplingframework.algorithms;

import org.architecturemining.samplingframework.models.DirectlyFollowsFrequencyMatrix;
import org.architecturemining.samplingframework.models.SampleMeasureSummary;
import org.architecturemining.samplingframework.models.impl.SampleMeasureSummaryImpl;
import org.deckfour.xes.model.XLog;

/**
 * 
 * @author jmw
 *
 * This class computes all sample measures in one go, for efficiency purposes.
 *
 */
public class SampleMeasureComputer {

	public static SampleMeasureSummary computeMeasures(XLog originalLog, XLog sampleLog, double ratio) {
		DirectlyFollowsFrequencyMatrix<String> original = DirectlyFollowsComputer.computeMatrix(originalLog);
		DirectlyFollowsFrequencyMatrix<String> sample = DirectlyFollowsComputer.computeMatrix(sampleLog);
		
		return computeMeasures(original, sample, ratio);
	}
	
	public static SampleMeasureSummary computeMeasures(XLog originalLog, DirectlyFollowsFrequencyMatrix<String> sample, double ratio) {
		return computeMeasures( DirectlyFollowsComputer.computeMatrix(originalLog), sample, ratio);
	}
	
	public static SampleMeasureSummary computeMeasures(DirectlyFollowsFrequencyMatrix<String> original, XLog sampleLog, double ratio) {
		return computeMeasures(original, DirectlyFollowsComputer.computeMatrix(sampleLog), ratio);
	}
	
	public static SampleMeasureSummary computeMeasures(DirectlyFollowsFrequencyMatrix<String> original, DirectlyFollowsFrequencyMatrix<String> sample, double ratio) {
		
		double total = 0;
		int covered = 0;
		double weightedTotal = 0;
		
		double sumSMAPE = 0;
		double sumSRMSPE = 0;
		double sumSNRMSE = 0;
		double sumNMAE = 0;
		
		for(String from : original.getFrom()) {
			for (String to: original.getTo(from)) {
				double expected = ratio * original.frequency(from, to);
				double sampled = sample.frequency(from, to);
				
				// Coverage
				if (sampled > 0) {
					covered++;
				}
				
				// sMAPE
				sumSMAPE += ( Math.abs(expected - sampled) / (expected + sampled) );
				// SRMSPE
				sumSRMSPE += Math.pow( (Math.abs(expected - sampled) / (expected + sampled))  , 2);
				// SNRMSE
				sumSNRMSE += Math.pow(sampled - expected, 2);
				// NMAE
				sumNMAE += Math.abs(sampled - expected);
				
				// Accounting
				total++;
				weightedTotal += expected;
			}
		}
		
		
		return new SampleMeasureSummaryImpl(
				covered / total, // coverage 
				Math.sqrt(sumSRMSPE / total), // sRMSPE
				sumSMAPE / total,  // sMAPE
				 Math.sqrt(sumSNRMSE) / ( weightedTotal * Math.sqrt(1 / total) ),  // NRMSE
				 sumNMAE / weightedTotal);  // NMAE
	}
	
	
}
