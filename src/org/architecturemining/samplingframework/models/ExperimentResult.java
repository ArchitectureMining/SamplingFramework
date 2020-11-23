package org.architecturemining.samplingframework.models;

import java.io.File;
import java.io.FileWriter;
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
		FileWriter writer = new FileWriter(file);
		// sample number, sample ratio, Sample Quality
		String firstline = "number;ratio;coverage;sRMSPE;sMAPE;NRMSE;NMAE;recallTP;precisionTP;recallDisc;precisionDisc;recallOrig;precisionOrig";
		writer.write(firstline);
		for(ExperimentResult r: results) {
			StringBuffer sb = new StringBuffer();
			// Always start with a newline
			sb.append(System.lineSeparator());
			
			sb.append(r.getSampleNumber());
			sb.append(";");
			sb.append(r.getSampleRatio());
			sb.append(";");
			
			if (r.getSampleQuality() == null) {
				// if sample quality is not set, just write zeros in these positions
				sb.append("0;0;0;0;0;"); 
			} else {
				sb.append(r.getSampleQuality().getCoverage());
				sb.append(";");
				sb.append(r.getSampleQuality().getSymmetricRootMeanSquarePercentageError());
				sb.append(";");
				sb.append(r.getSampleQuality().getSymmetricMeanAbsolutePercentageError());
				sb.append(";");
				sb.append(r.getSampleQuality().getNormalisedRootMeanSquareError());
				sb.append(";");
				sb.append(r.getSampleQuality().getNormalisedMeanAbsoluteError());
				sb.append(";");
			}
			if (r.getLogQualityTrueProcess() == null) {
				sb.append("0;0;");
			} else {
				sb.append(r.getLogQualityTrueProcess().getRecall());
				sb.append(";");
				sb.append(r.getLogQualityTrueProcess().getPrecision());
				sb.append(";");
			}
			
			if (r.getSampleLogQualityDiscoveredProcess() == null) {
				sb.append("0;0;");
			} else {
				sb.append(r.getSampleLogQualityDiscoveredProcess().getRecall());
				sb.append(";");
				sb.append(r.getSampleLogQualityDiscoveredProcess().getPrecision());
				sb.append(";");
			}
			
			if (r.getLogQualityDiscoveredProcess() == null) {
				sb.append("0;0");
			} else {
				sb.append(r.getLogQualityDiscoveredProcess().getRecall());
				sb.append(";");
				sb.append(r.getLogQualityDiscoveredProcess().getPrecision());
			}
			
			writer.write(sb.toString());
		}
		writer.close();
	}

	public int getSampleNumber() {
		return sampleNumber;
	}	
  
}
