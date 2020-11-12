package org.architecturemining.samplingframework.measures;

import org.architecturemining.samplingframework.algorithms.SampleMeasureComputer;
import org.architecturemining.samplingframework.models.SampleMeasureSummary;
import org.junit.Test;

public class SampleMeasureComputerTest extends AbstractMeasureTest {

	public AbstractMeasure getMeasure() {
		return null;
	}
	
	@Test
	public void testSample1() {
		SampleMeasureSummary summary = SampleMeasureComputer.computeMeasures(getOriginal(), getSample1(), 0.25);
		
		assertEquals(0.5, summary.getCoverage());
		assertEquals(0.726, summary.getSymmetricRootMeanSquarePercentageError(), 0.001);
		assertEquals(0.583, summary.getSymmetricMeanAbsolutePercentageError(), 0.001);
		assertEquals(0.612, summary.getNormalisedRootMeanSquareError(), 0.001);
		assertEquals(0.5, summary.getNormalisedMeanAbsoluteError(), 0.001);
	}
	
	@Test
	public void testSample2() {
		SampleMeasureSummary summary = SampleMeasureComputer.computeMeasures(getOriginal(), getSample2(), 0.25);
		
		assertEquals(1.0, summary.getCoverage());
		assertEquals(0.456, summary.getSymmetricRootMeanSquarePercentageError(), 0.001);
		assertEquals(0.383, summary.getSymmetricMeanAbsolutePercentageError(), 0.001);
		assertEquals(1.173, summary.getNormalisedRootMeanSquareError(), 0.001);
		assertEquals(1.0, summary.getNormalisedMeanAbsoluteError(), 0.001);
	}

}
