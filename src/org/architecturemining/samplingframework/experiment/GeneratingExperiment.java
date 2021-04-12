package org.architecturemining.samplingframework.experiment;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

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
import org.processmining.eigenvalue.generator.GenerateLogAndModel;
import org.processmining.eigenvalue.generator.NAryTreeGenerator;
import org.processmining.framework.plugin.PluginContext;
import org.processmining.framework.plugin.events.Logger.MessageLevel;
import org.processmining.plugins.InductiveMiner.efficienttree.EfficientTree;
import org.processmining.plugins.InductiveMiner.efficienttree.EfficientTree2AcceptingPetriNet;
import org.processmining.plugins.InductiveMiner.efficienttree.ProcessTree2EfficientTree;
import org.processmining.plugins.etm.model.narytree.NAryTree;
import org.processmining.plugins.etm.model.narytree.conversion.NAryTreeToProcessTree;
import org.processmining.processtree.ProcessTree;

public class GeneratingExperiment extends AbstractExperiment {

	public Object run(PluginContext context) {
		// Determine the right directory
		String basePath = "./experiments/generate";

		if (!Files.isDirectory(Paths.get(basePath))) {
			context.log("BasePath: " + basePath + " does not exist!", MessageLevel.ERROR);
			return null;
		}
		int nr = 1;
		while(Files.isDirectory(Paths.get(basePath + "/exp" + nr))) {
			nr++;
		}
		basePath = basePath + "/exp" + nr + "/";
		File base = new File(basePath);
		base.mkdirs();
		
		//experiment(context, basePath, 100, 10, 10000, 10, 10, 0.01, 0.05, 0.1, 0.2, 0.3, 0.4, 0.5, 0.6, 0.7, 0.8, 0.9);
		experiment(context, basePath, 100, 15, 5000, 1, 10, 0.01, 0.05, 0.1, 0.2, 0.3, 0.4, 0.5, 0.6, 0.7, 0.8, 0.9);
		//experiment(context, basePath, 2, 5, 100, 2, 5, 0.1, 0.2);
		return null;
	}
	
	
	public void experiment(PluginContext context, String basePath, int nrOfModels, int activitySize, int logSize, int nrOfLogs, int nrOfSamples, double...  ratios) {
		BufferedResultList<ModelLogResult> modelResults = new BufferedResultList<>(new File(basePath + "/modelresults.csv"));
		BufferedResultList<SampleResult> sampleResults = new BufferedResultList<>(new File(basePath + "/results.csv"));
		try {
			sampleResults.writeLine(SampleResult.HEADERLINE);
			modelResults.writeLine(ModelLogResult.HEADERLINE);
		} catch (IOException e) {
			context.log(e);
			return;
		}
		
		for (int model = 1; model <= nrOfModels ; model++) {
			// Step 1: generate a model
			NAryTreeGenerator generator = new NAryTreeGenerator();
			NAryTree trueProcessNAryTree = generator.generate(activitySize);
			ProcessTree trueProcess = NAryTreeToProcessTree.convert(trueProcessNAryTree, null);
			EfficientTree efficientTrueProcess = ProcessTree2EfficientTree.convert(trueProcess);
			AcceptingPetriNet trueProcessNet = EfficientTree2AcceptingPetriNet.convert(efficientTrueProcess);
			
			// Step 1a: store the process model
			String trueProcessFile = basePath + "/model" + model + ".pnml";
			exportProcessModel(context, trueProcessNet, new File(trueProcessFile));
			
			// Step 2: create a log
			for(int log = 1 ; log <= nrOfLogs ; log++) {
				XLog originalLog = GenerateLogAndModel.generateLog(trueProcessNAryTree, logSize);
				String originalLogFile = basePath + "/m" + model + "_log" + log + ".xes";
				exportLog(context, originalLog, new File(originalLogFile));
				
				// Calculate the DF matrix
				DirectlyFollowsFrequencyMatrix<String> dfOriginalLog = DirectlyFollowsComputer.computeMatrix(originalLog);
				
				// Calculate the Entropy of the log with the true process
				ModelLogResult modelLogResult = new ModelLogResult(model, log);
				modelLogResult.setEntropy(EntropyComputer.calculateRecallAndPrecision(context, originalLogFile, trueProcessFile));
				
				modelResults.add(modelLogResult);
				
				// Create a sampler
				AbstractSampleGenerator sampleGenerator = new RandomSampler(originalLog);
				
				for(double ratio: ratios) {
					// For each ratio, create a sample
					for(int sample = 1 ; sample <= nrOfSamples; sample++) {
						SampleResult result = new SampleResult(model, log, sample, ratio);
						
						// Generate a sample
						XLog sampleLog = sampleGenerator.drawSample(ratio);
						String sampleLogFile = basePath + "/m" + model + "_l" + log + "s" + ratio + "/s" + ratio + "_" + sample + ".xes";
						exportLog(context, sampleLog, new File(sampleLogFile));
						
						DirectlyFollowsFrequencyMatrix<String> dfSampleLog = DirectlyFollowsComputer.computeMatrix(sampleLog);
						result.setSampleQuality(SampleMeasureComputer.computeMeasures(dfOriginalLog, dfSampleLog, ratio));
						
						// Discover a model
						AcceptingPetriNet sampleModel = discoverModel(context, sampleLog);
						String sampleModelFile = basePath + "/m" + model + "_l" + log + "s" + ratio + "/model_" + ratio + sample + ".pnml";
						exportProcessModel(context, sampleModel, new File(sampleModelFile));
						
						// Measure entropy sample - model
						result.setSampleLogWithModel(EntropyComputer.calculateRecallAndPrecision(context, sampleLogFile, sampleModelFile));
						// Measure entropy original - model
						result.setOriginalLogWithModel(EntropyComputer.calculateRecallAndPrecision(context, originalLogFile, sampleModelFile));
						
						sampleResults.add(result);
					} // sample
				} // ratio
				
			} // Log
		} // Model
	}
	
	private class ModelLogResult {
		
		public static final String HEADERLINE = "model,log,precision,recall"; 
				
		private int model;
		
		private int log;
		
		private EntropyMeasure entropy;
		
		public ModelLogResult(int model, int log) {
			this.model = model;
			this.log = log;
		}

		/**
		 * @return the entropy
		 */
		public EntropyMeasure getEntropy() {
			return entropy;
		}

		/**
		 * @param entropy the entropy to set
		 */
		public void setEntropy(EntropyMeasure entropy) {
			this.entropy = entropy;
		}

		/**
		 * @return the model
		 */
		public int getModel() {
			return model;
		}

		/**
		 * @return the log
		 */
		public int getLog() {
			return log;
		}
		
		@Override
		public String toString() {
			StringBuilder sb = new StringBuilder();
			
			sb.append(getModel());
			sb.append(",");
			sb.append(getLog());
			sb.append(",");
			sb.append(getEntropy().getPrecision());
			sb.append(",");
			sb.append(getEntropy().getRecall());
			
			return sb.toString();
		}
		
	}
		
	private class SampleResult {
		
		public static final String HEADERLINE = "model,number,ratio,coverage,sRMSPE,sMAPE,NRMSE,NMAE,precisionOrig,recallOrig,precisionSample,recallSample";
		
		private int model;
		
		private int log;
		
		private int number;
		
		private double ratio;
				
		private EntropyMeasure originalLogWithModel;
		
		private EntropyMeasure sampleLogWithModel;
		
		private SampleMeasureSummary sampleQuality;
		
		public SampleResult(int model, int log, int number, double ratio) {
			this.model = model;
			this.log = log;
			this.number = number;
			this.ratio = ratio;
		}

		/**
		 * @return the originalLogWithModel
		 */
		public EntropyMeasure getOriginalLogWithModel() {
			return originalLogWithModel;
		}

		/**
		 * @param originalLogWithModel the originalLogWithModel to set
		 */
		public void setOriginalLogWithModel(EntropyMeasure originalLogWithModel) {
			this.originalLogWithModel = originalLogWithModel;
		}

		/**
		 * @return the sampleWithModel
		 */
		public EntropyMeasure getSampleLogWithModel() {
			return sampleLogWithModel;
		}

		/**
		 * @param sampleLogWithModel the sampleWithModel to set
		 */
		public void setSampleLogWithModel(EntropyMeasure sampleLogWithModel) {
			this.sampleLogWithModel = sampleLogWithModel;
		}

		/**
		 * @return the sampleQuality
		 */
		public SampleMeasureSummary getSampleQuality() {
			return sampleQuality;
		}

		/**
		 * @param sampleQuality the sampleQuality to set
		 */
		public void setSampleQuality(SampleMeasureSummary sampleQuality) {
			this.sampleQuality = sampleQuality;
		}

		/**
		 * @return the model
		 */
		public int getModel() {
			return model;
		}
		
		public int getLog() {
			return log;
		}

		/**
		 * @return the number
		 */
		public int getNumber() {
			return number;
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
			
			sb.append(getModel());
			sb.append(",");
			sb.append(getLog());
			sb.append(",");
			sb.append(getNumber());
			sb.append(",");
			sb.append(getRatio());
			sb.append(",");
					
			sb.append(getSampleQuality().getCoverage());
			sb.append(",");
			sb.append(getSampleQuality().getSymmetricRootMeanSquarePercentageError());
			sb.append(",");
			sb.append(getSampleQuality().getSymmetricMeanAbsolutePercentageError());
			sb.append(",");
			sb.append(getSampleQuality().getNormalisedRootMeanSquareError());
			sb.append(",");
			sb.append(getSampleQuality().getNormalisedMeanAbsoluteError());
			sb.append(",");
			
			sb.append(getOriginalLogWithModel().getPrecision());
			sb.append(",");
			sb.append(getOriginalLogWithModel().getRecall());
			sb.append(",");
			
			sb.append(getSampleLogWithModel().getPrecision());
			sb.append(",");
			sb.append(getSampleLogWithModel().getRecall());
			
			return sb.toString();
		}
		
	}

}
