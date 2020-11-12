package org.architecturemining.samplingframework.algorithms;

import org.architecturemining.samplingframework.models.DirectlyFollowsFrequencyMatrix;
import org.architecturemining.samplingframework.models.impl.DirectlyFollowsFrequencyMatrixImpl;
import org.deckfour.xes.classification.XEventClassifier;
import org.deckfour.xes.classification.XEventNameClassifier;
import org.deckfour.xes.model.XEvent;
import org.deckfour.xes.model.XLog;
import org.deckfour.xes.model.XTrace;

/**
 * 
 * @author jmw
 *
 * Computes the Directly Follows Relation as a Frequency Matrix
 */
public class DirectlyFollowsComputer {

	/**
	 * 
	 * @param log
	 * @return
	 */
	public static DirectlyFollowsFrequencyMatrix<String> computeMatrix(XLog log) {
		XEventClassifier classifier;
		if(log.getClassifiers() == null || log.getClassifiers().isEmpty()) {
			classifier = new XEventNameClassifier();
		} else {
			classifier = log.getClassifiers().get(0);
		}
		return computeMatrix(log, classifier);
	}
	

	/**
	 * Computes the Directly Follows Relation as a Frequency Matrix.
	 * Simple implementation, taking the classifier to calculate the event identifier.
	 * @param log
	 * @param classifier
	 * @return
	 */
	public static DirectlyFollowsFrequencyMatrix<String> computeMatrix(XLog log, XEventClassifier classifier) {
		
		DirectlyFollowsFrequencyMatrixImpl<String> matrix = new DirectlyFollowsFrequencyMatrixImpl<>();
		
		// Walk through each trace
		for(XTrace trace : log) {
			String previous = null;
			// Walk through each event
			for(XEvent event: trace) {
				String current = classifier.getClassIdentity(event);
				
				if (previous != null) {
					matrix.increment(previous, current);
				}
				previous = current;
			}
		}
		
		return matrix;
	}
	
}
