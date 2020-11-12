package org.architecturemining.samplingframework.measures;

import org.junit.Test;

public class SymmetricRootMeanSquarePercentageErrorTest extends AbstractMeasureTest {

	private AbstractMeasure measure = new SymmetricRootMeanSquarePercentageError(); 
	
	public AbstractMeasure getMeasure() {
		return measure;
	}
	
	@Test
	public void testComputeSample1() {
		assertEquals(0.726, measureForSample1(), 0.001);
	}
	
	@Test
	public void testComputeSample2() {
		assertEquals(0.456, measureForSample2(), 0.001);
	}

}
