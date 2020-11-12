package org.architecturemining.samplingframework.measures;

import org.junit.Test;

public class NormalisedMeanAbsoluteErrorTest extends AbstractMeasureTest {

	private AbstractMeasure measure = new NormalisedMeanAbsoluteError();
	
	public AbstractMeasure getMeasure() {
		return measure;
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
