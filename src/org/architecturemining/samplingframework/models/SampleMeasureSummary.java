package org.architecturemining.samplingframework.models;

public interface SampleMeasureSummary {
	
	public double getCoverage();
	public double getSymmetricRootMeanSquarePercentageError();
	public double getSymmetricMeanAbsolutePercentageError();
	public double getNormalisedRootMeanSquareError();
	public double getNormalisedMeanAbsoluteError();
	
}
