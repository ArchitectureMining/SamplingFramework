package org.architecturemining.samplingframework.measures;

import org.architecturemining.samplingframework.models.impl.DirectlyFollowsFrequencyMatrixImpl;
import org.junit.Test;

public class CoverageTest extends AbstractMeasureTest {
	
	private Coverage coverage = new Coverage();
	
	public AbstractMeasure getMeasure() {
		return coverage;
	}
	
	@Test
	public void testComputeSimple() {
		DirectlyFollowsFrequencyMatrixImpl<String> original = new DirectlyFollowsFrequencyMatrixImpl<>();
		DirectlyFollowsFrequencyMatrixImpl<String> sample = new DirectlyFollowsFrequencyMatrixImpl<>();
		DirectlyFollowsFrequencyMatrixImpl<String> empty = new DirectlyFollowsFrequencyMatrixImpl<>();
		
		populateMatrix(original, 1, "a", "b", "c");
		populateMatrix(sample, 1, "a", "b");
		
		assertEquals(1.0, coverage.compute(original, original));
		assertEquals(0.0, coverage.compute(original, empty));
		assertEquals(0.5, coverage.compute(original, sample));
		
	}
	
	@Test
	public void testComputeSample1() {
		assertEquals(0.5, measureForSample1());
	}
	
	@Test
	public void testComputeSample2() {
		assertEquals(1.0, measureForSample2());
	}
	
	
}
