package org.architecturemining.samplingframework.measures;

import org.junit.Test;

public class SymmetricMeanAbsolutePercentageErrorTest extends AbstractMeasureTest {
	
	private AbstractMeasure measure = new SymmetricMeanAbsolutePercentageError();
	
	public AbstractMeasure getMeasure() {
		return measure;
	}
	
	@Test
	public void testComputeSample1() {
		assertEquals(0.583, measureForSample1(), 0.001);
	}
	
	@Test
	public void testComputeSample2() {
		assertEquals(0.383, measureForSample2(), 0.001);
	}

}
