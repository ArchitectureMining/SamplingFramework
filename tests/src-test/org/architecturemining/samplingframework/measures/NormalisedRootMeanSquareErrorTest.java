/**
 * 
 */
package org.architecturemining.samplingframework.measures;

import org.junit.Test;

/**
 * @author janma
 *
 */
public class NormalisedRootMeanSquareErrorTest extends AbstractMeasureTest {

	private AbstractMeasure measure = new NormalisedRootMeanSquareError();
	
	public AbstractMeasure getMeasure() {
		return measure;
	}
	
	@Test
	public void testComputeSample1() {
		assertEquals(0.612, measureForSample1(), 0.001);
	}
	
	@Test
	public void testComputeSample2() {
		assertEquals(1.173, measureForSample2(), 0.001);
	}

}
