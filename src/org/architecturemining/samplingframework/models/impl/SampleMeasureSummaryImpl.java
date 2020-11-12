package org.architecturemining.samplingframework.models.impl;

import org.architecturemining.samplingframework.models.SampleMeasureSummary;

public class SampleMeasureSummaryImpl implements SampleMeasureSummary {
	
	private double coverage;
	private double sRMSPE;
	private double sMAPE;
	private double NRMSE;
	private double NMAE;
	
	public SampleMeasureSummaryImpl(double coverage, double sRMSPE, double sMAPE, double NRMSE, double NMAE) {
		this.coverage = coverage;
		this.sRMSPE = sRMSPE;
		this.sMAPE = sMAPE;
		this.NRMSE = NRMSE;
		this.NMAE = NMAE;
	}

	public double getCoverage() {
		return coverage;
	}

	public double getSymmetricRootMeanSquarePercentageError() {
		return sRMSPE;
	}

	public double getSymmetricMeanAbsolutePercentageError() {
		return sMAPE;
	}

	public double getNormalisedRootMeanSquareError() {
		return NRMSE;
	}

	public double getNormalisedMeanAbsoluteError() {
		return NMAE;
	}
}
