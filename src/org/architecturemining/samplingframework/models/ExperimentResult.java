package org.architecturemining.samplingframework.models;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class ExperimentResult {
	
	public static final String HEADERLINE = "number;ratio;coverage;sRMSPE;sMAPE;NRMSE;NMAE;recallTP;precisionTP;recallDisc;precisionDisc;recallOrig;precisionOrig";
	
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
		boolean addFirstLine = true;
		if (file.exists()) {
			addFirstLine = false;
		}
		
		FileWriter writer = new FileWriter(file, true);
		
		// sample number, sample ratio, Sample Quality
		String firstline = "number;ratio;coverage;sRMSPE;sMAPE;NRMSE;NMAE;recallTP;precisionTP;recallDisc;precisionDisc;recallOrig;precisionOrig";
		if (addFirstLine) {
			writer.write(firstline);
		}
		
		for(ExperimentResult r: results) {
			writer.write(r.toString());
		}
		writer.close();
	}

	public int getSampleNumber() {
		return sampleNumber;
	}
	
	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer();
		// Always start with a newline
		
		sb.append(getSampleNumber());
		sb.append(";");
		sb.append(getSampleRatio());
		sb.append(";");
		
		if (getSampleQuality() == null) {
			// if sample quality is not set, just write zeros in these positions
			sb.append("0;0;0;0;0;"); 
		} else {
			sb.append(getSampleQuality().getCoverage());
			sb.append(";");
			sb.append(getSampleQuality().getSymmetricRootMeanSquarePercentageError());
			sb.append(";");
			sb.append(getSampleQuality().getSymmetricMeanAbsolutePercentageError());
			sb.append(";");
			sb.append(getSampleQuality().getNormalisedRootMeanSquareError());
			sb.append(";");
			sb.append(getSampleQuality().getNormalisedMeanAbsoluteError());
			sb.append(";");
		}
		if (getLogQualityTrueProcess() == null) {
			sb.append("0;0;");
		} else {
			sb.append(getLogQualityTrueProcess().getRecall());
			sb.append(";");
			sb.append(getLogQualityTrueProcess().getPrecision());
			sb.append(";");
		}
		
		if (getSampleLogQualityDiscoveredProcess() == null) {
			sb.append("0;0;");
		} else {
			sb.append(getSampleLogQualityDiscoveredProcess().getRecall());
			sb.append(";");
			sb.append(getSampleLogQualityDiscoveredProcess().getPrecision());
			sb.append(";");
		}
		
		if (getLogQualityDiscoveredProcess() == null) {
			sb.append("0;0");
		} else {
			sb.append(getLogQualityDiscoveredProcess().getRecall());
			sb.append(";");
			sb.append(getLogQualityDiscoveredProcess().getPrecision());
		}
		
		return sb.toString();
	}
  
}
