package org.architecturemining.samplingframework.models.impl;

import org.architecturemining.samplingframework.models.EntropyMeasure;

public class EntropyMeasureImpl implements EntropyMeasure {

	private double precision = 0.0;
	private double recall = 0.0;
	
	public EntropyMeasureImpl(double recall, double precision) {
		this.precision = precision;
		this.recall = recall;
	}

	public double getPrecision() {
		return precision;
	}

	public double getRecall() {
		return recall;
	}


}
