package org.architecturemining.samplingframework.measures;

import org.architecturemining.samplingframework.models.DirectlyFollowsFrequencyMatrix;
import org.architecturemining.samplingframework.models.impl.DirectlyFollowsFrequencyMatrixImpl;

import junit.framework.TestCase;

public abstract class AbstractMeasureTest extends TestCase {
	
	public abstract AbstractMeasure getMeasure();
	
	protected void populateMatrix(DirectlyFollowsFrequencyMatrixImpl<String> matrix, int frequency, String...sequence) {
		// No negative frequence, and the sequence should have at least two elements
		if (frequency < 0 || sequence.length < 2) {
			return;
		}
		
		for(int i = 1 ; i < sequence.length ; i++) {
			matrix.add(sequence[i-1], sequence[i], frequency);
		}
	}
	
	protected DirectlyFollowsFrequencyMatrix<String> getOriginal() {
		DirectlyFollowsFrequencyMatrixImpl<String> matrix = new DirectlyFollowsFrequencyMatrixImpl<>();
		
		populateMatrix(matrix, 4, "a", "d", "g");
		populateMatrix(matrix, 2, "a", "c", "g");
		populateMatrix(matrix, 1, "a", "b", "g");
		populateMatrix(matrix, 1, "a", "e", "g");
		
		return matrix;
	}
	
	protected DirectlyFollowsFrequencyMatrix<String> getSample1() {
		DirectlyFollowsFrequencyMatrixImpl<String> matrix = new DirectlyFollowsFrequencyMatrixImpl<>();
		
		populateMatrix(matrix, 1, "a", "c", "g");
		populateMatrix(matrix, 1, "a", "d", "g");
				
		return matrix;
	}
	
	protected DirectlyFollowsFrequencyMatrix<String> getSample2() {
		DirectlyFollowsFrequencyMatrixImpl<String> matrix = new DirectlyFollowsFrequencyMatrixImpl<>();
		
		populateMatrix(matrix, 1, "a", "b", "g");
		populateMatrix(matrix, 1, "a", "c", "g");
		populateMatrix(matrix, 1, "a", "d", "g");
		populateMatrix(matrix, 1, "a", "e", "g");
		
		return matrix;
	}
	
	public double measureForSample1() {
		DirectlyFollowsFrequencyMatrix<String> original = getOriginal();
		DirectlyFollowsFrequencyMatrix<String> sample = getSample1();
		
		return getMeasure().compute(original, sample, 0.25);
	}
	
	public double measureForSample2() {
		DirectlyFollowsFrequencyMatrix<String> original = getOriginal();
		DirectlyFollowsFrequencyMatrix<String> sample = getSample2();
		
		return getMeasure().compute(original, sample, 0.25);
	}

}
