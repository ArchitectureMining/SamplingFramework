package org.architecturemining.samplingframework.models;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.processmining.entropia.models.EntropyMeasure;

public class ExperimentResult {
	
	private EntropyMeasure logQualityTrueProcess;
	private EntropyMeasure logQualityDiscoveredProcess;
	private EntropyMeasure sampleLogQualityDiscoveredProcess;
	
	private SampleMeasureSummary sampleQuality;
	
	private double sampleRatio;
	
	private int sampleNumber = 0;
	
	public ExperimentResult() {
		this(0, 0);
	}
	
	public ExperimentResult(double sampleRatio, int sampleNumber) {
		this.sampleRatio = sampleRatio;
		this.sampleNumber = sampleNumber;
	}
	
	public EntropyMeasure getLogQualityTrueProcess() {
		return logQualityTrueProcess;
	}
	
	public void setLogQualityTrueProcess(EntropyMeasure logQualityTrueProcess) {
		this.logQualityTrueProcess = logQualityTrueProcess;
	}
	
	public EntropyMeasure getLogQualityDiscoveredProcess() {
		return logQualityDiscoveredProcess;
	}
	
	public void setLogQualityDiscoveredProcess(EntropyMeasure logQualityDiscoveredProcess) {
		this.logQualityDiscoveredProcess = logQualityDiscoveredProcess;
	}

	public SampleMeasureSummary getSampleQuality() {
		return sampleQuality;
	}

	public void setSampleQuality(SampleMeasureSummary sampleQuality) {
		this.sampleQuality = sampleQuality;
	}

	public EntropyMeasure getSampleLogQualityDiscoveredProcess() {
		return sampleLogQualityDiscoveredProcess;
	}

	public void setSampleLogQualityDiscoveredProcess(EntropyMeasure sampleLogQualityDiscoveredProcess) {
		this.sampleLogQualityDiscoveredProcess = sampleLogQualityDiscoveredProcess;
	}

	public double getSampleRatio() {
		return sampleRatio;
	}
	
	public static void export(List<ExperimentResult> results, File file) throws IOException {
		
	}

	public int getSampleNumber() {
		return sampleNumber;
	}
	
}
