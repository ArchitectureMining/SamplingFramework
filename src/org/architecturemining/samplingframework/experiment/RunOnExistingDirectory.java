package org.architecturemining.samplingframework.experiment;

import java.io.File;
import java.io.IOException;

import org.architecturemining.samplingframework.algorithms.DirectlyFollowsComputer;
import org.architecturemining.samplingframework.algorithms.EntropyComputer;
import org.architecturemining.samplingframework.algorithms.SampleMeasureComputer;
import org.architecturemining.samplingframework.models.BufferedResultList;
import org.architecturemining.samplingframework.models.DirectlyFollowsFrequencyMatrix;
import org.architecturemining.samplingframework.models.ExperimentResult;
import org.deckfour.xes.model.XLog;
import org.processmining.acceptingpetrinet.models.AcceptingPetriNet;
import org.processmining.framework.plugin.PluginContext;

public class RunOnExistingDirectory extends AbstractExperiment {

	private String directory;
	
	public RunOnExistingDirectory(String directory) {
		this.directory = directory; // "D:\\projects\\ProM\\workspace\\SamplingFramework\\experiments\\road"
	}
		
	@Override
	public Object run(PluginContext context) {
		XLog originalLog = createXESLogFromCSV(context, directory + "/original.csv");
		DirectlyFollowsFrequencyMatrix<String> dfOriginalLog = DirectlyFollowsComputer.computeMatrix(originalLog);
		String originalLogFile = directory + "/original.xes";
		exportLog(context, originalLog, new File(originalLogFile));
		
		try {
			BufferedResultList<ExperimentResult> results = new BufferedResultList<ExperimentResult>(new File(directory + "/results.csv"));
			results.writeLine(ExperimentResult.HEADERLINE);
		
			AcceptingPetriNet discoveredModel = discoverModel(context, originalLog);
			
			String discoveredModelFileName = directory + "/discovered.pnml";
			exportProcessModel(context, discoveredModel, new File(discoveredModelFileName));
			
			ExperimentResult originalResult = new ExperimentResult(1.0, 0);
			originalResult.setSampleQuality(SampleMeasureComputer.computeMeasures(dfOriginalLog, dfOriginalLog, 1.0));
			originalResult.setLogQualityDiscoveredProcess(EntropyComputer.calculateRecallAndPrecision(context, directory + "/original.xes", directory + "/discovered.pnml"));
			
			results.add(originalResult);
		
			// Now, go through the samples directory, and take each CSV file 
			for (int name = 1 ; name <= 30 ; name++) {
				
				XLog sampleLog = createXESLogFromCSV(context, directory + "/samples/" + name + ".csv");
				DirectlyFollowsFrequencyMatrix<String> dfSample = DirectlyFollowsComputer.computeMatrix(sampleLog);
				String sampleLogFile = directory + "/samples/converted_" + name + ".xes";
				
				exportLog(context, sampleLog, new File(sampleLogFile));
				
				double ratio = (double) sampleLog.size() / originalLog.size();
				
				ExperimentResult sampleResult = new ExperimentResult(ratio, name);
				sampleResult.setSampleQuality(SampleMeasureComputer.computeMeasures(dfOriginalLog, dfSample, ratio));
				
				// Discover a model
				AcceptingPetriNet sampleNet = discoverModel(context, sampleLog);
				String sampleModelFile = directory + "/samples/discovered_" + name + ".pnml";
				exportProcessModel(context, sampleNet, new File(sampleModelFile));
				
				// Calculate its entropy
				sampleResult.setSampleLogQualityDiscoveredProcess(EntropyComputer.calculateRecallAndPrecision(context, sampleLogFile, sampleModelFile));
				sampleResult.setLogQualityDiscoveredProcess(EntropyComputer.calculateRecallAndPrecision(context, originalLogFile, sampleModelFile));
				results.add(sampleResult);
			}

		} catch (IOException e) {
			context.log(e);
		}
		
		return null;
	}
	
}
