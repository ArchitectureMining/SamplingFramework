package org.architecturemining.samplingframework.experiment;

import java.io.File;
import java.io.IOException;

import org.architecturemining.samplingframework.algorithms.DirectlyFollowsComputer;
import org.architecturemining.samplingframework.algorithms.EntropyComputer;
import org.architecturemining.samplingframework.algorithms.SampleMeasureComputer;
import org.architecturemining.samplingframework.models.BufferedResultList;
import org.architecturemining.samplingframework.models.DirectlyFollowsFrequencyMatrix;
import org.architecturemining.samplingframework.models.EntropyMeasure;
import org.architecturemining.samplingframework.models.SampleMeasureSummary;
import org.architecturemining.samplingframework.samplingtechnique.AbstractSampleGenerator;
import org.architecturemining.samplingframework.samplingtechnique.RandomSampler;
import org.deckfour.xes.model.XLog;
import org.processmining.acceptingpetrinet.models.AcceptingPetriNet;
import org.processmining.framework.plugin.PluginContext;

public class SampleExistingLog extends AbstractExperiment {

	private XLog log;
	private String logFile;
	private String basePath;
		
	public SampleExistingLog(String logFile, String basePath) throws Exception {
		this.logFile = logFile;
		this.basePath = basePath;
		
		this.log = loadXESLog(logFile);
		
		if (log == null) {
			throw new Exception("Could not load log: '" + logFile + "'");
		}
	}
	
	public Object run(PluginContext context) {
		experiment(context, 10, 0.01, 0.02, 0.05, 0.1, 0.2, 0.3, 0.4, 0.5, 0.6, 0.7, 0.8, 0.9);
		//experiment(context, 2, 0.01);
		
		return null;
	}
	
	private void experiment(PluginContext context, int nrOfSamples, double... ratios) {
		BufferedResultList<Result> results = new BufferedResultList<>(new File(basePath + "/results.csv"));
		try {
			results.writeLine(Result.HEADERLINE);
		} catch(IOException e) {
			context.log(e);
			return;
		}
		
		AbstractSampleGenerator generator = new RandomSampler(log);
		DirectlyFollowsFrequencyMatrix<String> dfOriginal = DirectlyFollowsComputer.computeMatrix(log);
		
		for(double ratio: ratios) {
			for(int sample = 1; sample <= nrOfSamples; sample++) {
				Result result = new Result(sample, ratio);
				
				// Step 0: Draw a sample, and calculate the sample measures
				XLog sampleLog = generator.drawSample(ratio);
				String sampleLogFile = basePath + "/s_" + ratio + "_" + sample + ".xes";
				exportLog(context, sampleLog, new File(sampleLogFile));
				
				result.setSampleMeasures(SampleMeasureComputer.computeMeasures(dfOriginal, sampleLog, ratio));
								
				// Step 1: Discover a model
				AcceptingPetriNet model = discoverModel(context, sampleLog);
				String modelFile = basePath + "/model_" + ratio + "_" + sample + ".pnml";
				exportProcessModel(context, model, new File(modelFile));
				
				// Step 2: Calculate the entropy sample - model
				result.setSampleLogEntropy(EntropyComputer.calculateRecallAndPrecision(context, sampleLogFile, modelFile));
				
				// Step 3: Calculate the entropy original - model
				result.setOriginalLogEntropy(EntropyComputer.calculateRecallAndPrecision(context, logFile, modelFile));
				
				results.add(result);
			}
		}
		
	}
	
	private class Result {
		public static final String HEADERLINE = "sample,ratio,coverage,sMAPE,sRMSPE,NMAE,NRMSE,precisionSample,recallSample,precisionOrig,recallOrig";
		
		private int sample;
		private double ratio;
		
		private SampleMeasureSummary sampleMeasures;
		
		private EntropyMeasure originalLogEntropy;
		
		private EntropyMeasure sampleLogEntropy;
		
		public Result(int sample, double ratio) {
			this.sample = sample;
			this.ratio = ratio;
		}

		/**
		 * @return the sampleMeasures
		 */
		public SampleMeasureSummary getSampleMeasures() {
			return sampleMeasures;
		}

		/**
		 * @param sampleMeasures the sampleMeasures to set
		 */
		public void setSampleMeasures(SampleMeasureSummary sampleMeasures) {
			this.sampleMeasures = sampleMeasures;
		}

		/**
		 * @return the originalLogEntropy
		 */
		public EntropyMeasure getOriginalLogEntropy() {
			return originalLogEntropy;
		}

		/**
		 * @param originalLogEntropy the originalLogEntropy to set
		 */
		public void setOriginalLogEntropy(EntropyMeasure originalLogEntropy) {
			this.originalLogEntropy = originalLogEntropy;
		}

		/**
		 * @return the sampleLogEntropy
		 */
		public EntropyMeasure getSampleLogEntropy() {
			return sampleLogEntropy;
		}

		/**
		 * @param sampleLogEntropy the sampleLogEntropy to set
		 */
		public void setSampleLogEntropy(EntropyMeasure sampleLogEntropy) {
			this.sampleLogEntropy = sampleLogEntropy;
		}

		/**
		 * @return the sample
		 */
		public int getSample() {
			return sample;
		}

		/**
		 * @return the ratio
		 */
		public double getRatio() {
			return ratio;
		}
		
		@Override
		public String toString() {
			StringBuilder sb = new StringBuilder();
			
			sb.append(getSample());
			sb.append(",");
			sb.append(getRatio());
			sb.append(",");
			sb.append(getSampleMeasures().getCoverage());
			sb.append(",");
			sb.append(getSampleMeasures().getSymmetricMeanAbsolutePercentageError());
			sb.append(",");
			sb.append(getSampleMeasures().getSymmetricRootMeanSquarePercentageError());
			sb.append(",");
			sb.append(getSampleMeasures().getNormalisedMeanAbsoluteError());
			sb.append(",");
			sb.append(getSampleMeasures().getNormalisedRootMeanSquareError());
			sb.append(",");
			sb.append(getSampleLogEntropy().getPrecision());
			sb.append(",");
			sb.append(getSampleLogEntropy().getRecall());
			sb.append(",");
			sb.append(getOriginalLogEntropy().getPrecision());
			sb.append(",");
			sb.append(getOriginalLogEntropy().getRecall());
			
			return sb.toString();
		}
		
	}

}
