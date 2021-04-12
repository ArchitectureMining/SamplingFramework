package org.architecturemining.samplingframework.experiment;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.DirectoryStream;
import java.nio.file.DirectoryStream.Filter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import org.architecturemining.samplingframework.algorithms.DialogFreeInductiveMiner;
import org.architecturemining.samplingframework.algorithms.DirectlyFollowsComputer;
import org.architecturemining.samplingframework.algorithms.SampleMeasureComputer;
import org.architecturemining.samplingframework.models.BufferedResultList;
import org.architecturemining.samplingframework.models.DirectlyFollowsFrequencyMatrix;
import org.architecturemining.samplingframework.models.EntropyMeasure;
import org.architecturemining.samplingframework.models.ExperimentResult;
import org.architecturemining.samplingframework.models.impl.EntropyMeasureImpl;
import org.architecturemining.samplingframework.samplingtechnique.AbstractSampleGenerator;
import org.architecturemining.samplingframework.samplingtechnique.RandomSampler;
import org.deckfour.xes.model.XLog;
import org.processmining.acceptingpetrinet.models.AcceptingPetriNet;
import org.processmining.acceptingpetrinet.plugins.ExportAcceptingPetriNetPlugin;
import org.processmining.eigenvalue.generator.GenerateLogAndModel;
import org.processmining.eigenvalue.generator.NAryTreeGenerator;
import org.processmining.framework.plugin.PluginContext;
import org.processmining.framework.plugin.annotations.Plugin;
import org.processmining.framework.plugin.annotations.PluginVariant;
import org.processmining.framework.plugin.events.Logger.MessageLevel;
import org.processmining.log.csv.CSVFile;
import org.processmining.log.csv.CSVFileReferenceUnivocityImpl;
import org.processmining.log.csv.config.CSVConfig;
import org.processmining.log.csvimport.CSVConversion;
import org.processmining.log.csvimport.CSVConversion.ConversionResult;
import org.processmining.log.csvimport.config.CSVConversionConfig;
import org.processmining.log.csvimport.exception.CSVConversionException;
import org.processmining.models.graphbased.AttributeMap;
import org.processmining.models.graphbased.directed.petrinet.elements.Transition;
import org.processmining.plugins.InductiveMiner.efficienttree.EfficientTree;
import org.processmining.plugins.InductiveMiner.efficienttree.EfficientTree2AcceptingPetriNet;
import org.processmining.plugins.InductiveMiner.efficienttree.ProcessTree2EfficientTree;
import org.processmining.plugins.etm.model.narytree.NAryTree;
import org.processmining.plugins.etm.model.narytree.conversion.NAryTreeToProcessTree;
import org.processmining.plugins.log.exporting.ExportLogXes;
import org.processmining.processtree.ProcessTree;

@Plugin(
		name = "Experiment - runner",
		returnLabels = { "Example" },
		returnTypes = { Object.class },
		parameterLabels = {},
		userAccessible = true
		)
public class Experiment {
	
	/**
	 * 
	 * @param context
	 * @param dirName the directory location with the original log file, and a sub dir with samples
	 * @return
	 */
	public Object run2(PluginContext context, String dirName) {
		XLog originalLog = createXESLogFromCSV(context, dirName + "/original.csv");
		DirectlyFollowsFrequencyMatrix<String> dfOriginalLog = DirectlyFollowsComputer.computeMatrix(originalLog);
		exportLog(context, originalLog, new File(dirName + "/original.xes"));
		
		try {
			List<ExperimentResult> results = new BufferedResultList(new File(dirName + "/results.csv"));
		
			AcceptingPetriNet discoveredModel = discoverModel(context, originalLog);
			
			String discoveredModelFileName = dirName + "/discovered.pnml";
			exportProcessModel(context, discoveredModel, new File(discoveredModelFileName));
			
			ExperimentResult originalResult = new ExperimentResult(1.0, 0);
			originalResult.setSampleQuality(SampleMeasureComputer.computeMeasures(dfOriginalLog, dfOriginalLog, 1.0));
			originalResult.setLogQualityDiscoveredProcess(calculateRecallAndPrecision(context, dirName + "/original.xes", dirName + "/discovered.pnml"));
			
			results.add(originalResult);
		
		// Now, go through the samples directory, and take each CSV file 
			int counter = 1;
			
			DirectoryStream<Path> stream = Files.newDirectoryStream(Paths.get(dirName + "/samples/"), csvFilter);
			for (Path path: stream) {
				XLog sampleLog = createXESLogFromCSV(context, path.toString());
				DirectlyFollowsFrequencyMatrix<String> dfSample = DirectlyFollowsComputer.computeMatrix(sampleLog);
				String name = path.toFile().getName().substring(0, path.toFile().getName().lastIndexOf('.'));
				String sampleLogFile = dirName + "/samples/converted_" + name + ".xes";
				
				exportLog(context, sampleLog, new File(sampleLogFile));
				
				double ratio = (double) sampleLog.size() / originalLog.size();
				
				ExperimentResult sampleResult = new ExperimentResult(ratio, counter++);
				sampleResult.setSampleQuality(SampleMeasureComputer.computeMeasures(dfOriginalLog, dfSample, ratio));
				
				// Discover a model
				AcceptingPetriNet sampleNet = discoverModel(context, sampleLog);
				String sampleModelFile = dirName + "/samples/discovered_" + name + ".pnml";
				exportProcessModel(context, sampleNet, new File(sampleModelFile));
				
				// Calculate its entropy
				sampleResult.setLogQualityDiscoveredProcess(calculateRecallAndPrecision(context, sampleLogFile, sampleModelFile));
				results.add(sampleResult);
			}

		} catch (IOException e) {
			context.log(e);
		}
	
		return null;
	}
	
	private static Filter<Path> csvFilter = new Filter<Path>() {
		@Override
		public boolean accept(final Path entry) throws IOException {
			return entry.toFile().getName().endsWith(".csv");
		}
	};
	

	/**
	 * Opens a CSV file and converts it to a XES log
	 * @param context
	 * @param filename
	 * @return
	 */
	public XLog createXESLogFromCSV(final PluginContext context, String filename) {	
		try {
			File file = new File(filename);
			if (!file.exists()) {
				throw new IOException("File: '" + filename + "' does not exist!");
			}
			CSVFile csvFile = new CSVFileReferenceUnivocityImpl( file.toPath() );
			CSVConfig importConfig = new CSVConfig(csvFile);
			CSVConversionConfig conversionConfig = new CSVConversionConfig(csvFile, importConfig);
			conversionConfig.autoDetectDataTypes();
			
			String[] columns = csvFile.readHeader(importConfig);
			
			if (columns.length != 4 ) {
				context.log("Wrong input format!", MessageLevel.ERROR);
				return null;
			}
			
			List<String> caseColumns = new ArrayList<>();
			caseColumns.add(columns[0]);
			List<String> eventNameColumns = new ArrayList<>();
			eventNameColumns.add(columns[1]);
			
			conversionConfig.setCaseColumns(caseColumns);
			conversionConfig.setEventNameColumns(eventNameColumns);
			
			conversionConfig.setStartTimeColumn(columns[2]);
			
			CSVConversion converter = new CSVConversion();
			ConversionResult<XLog> result = converter.doConvertCSVToXES(csvFile, importConfig, conversionConfig);
			
			context.log(result.getConversionErrors());
			return result.getResult();
			
		} catch (CSVConversionException | IOException e) {
			context.log(e);
		}
		
		return null;
	}
	
	public void processFile(PluginContext context, String fileName) {
		XLog log = createXESLogFromCSV(context, fileName);
		
		DirectlyFollowsFrequencyMatrix<String> matrix = DirectlyFollowsComputer.computeMatrix(log);
		
		
		
		AcceptingPetriNet net = DialogFreeInductiveMiner.mineWithInfrequentInductiveMiner(context, log);
		
		context.log("Places     : " + net.getNet().getPlaces().size());
		context.log("Transitions: " + net.getNet().getTransitions().size());
		
		// EntropyPrecisionRecallMeasure epr = new EntropyPrecisionRecallMeasure(relevantTraces, retrievedTraces, 0, 0, bPrecision, bRecall, bSilent);
		// result = epr.computeMeasure();
	}
	
		
	@PluginVariant( variantLabel = "Experiment - main", requiredParameterLabels = {})
	public Object run(final PluginContext context) {
		try {
			// return run(context, 1, 1, 1, 1000, 20, 0.01);
			// return run(context, 20, 1, 1, 100000, 20, 0.01, 0.02, 0.05, 0.1, 0.2, 0.3, 0.4, 0.5, 0.6, 0.7, 0.8, 0.9);
			return run(context, 20, 20, 2, 100000, 20, 0.01, 0.02, 0.05, 0.1, 0.2, 0.3, 0.4, 0.5, 0.6, 0.7, 0.8, 0.9);
		} catch (IOException e) {
			context.log(e);
		}
		
		return null;
	}
	
	
	private void handleTrueProcess(PluginContext context, AcceptingPetriNet trueProcess, String trueProcessFileName, XLog originalLog, String originalLogFileName, AbstractSampleGenerator sampleGenerator, String basePath, int samplesPerIteration, double... ratios) {
		if (samplesPerIteration < 1) {
			samplesPerIteration = 1;
		}
		if (ratios.length == 0) {
			ratios = new double[1];
			ratios[0] = 0.1;
		}
		
		BufferedResultList<ExperimentResult> results;
		results = new BufferedResultList<ExperimentResult>(new File(basePath + "/results.csv"));
		try {
			results.writeLine(ExperimentResult.HEADERLINE);
		} catch (IOException e) {
			context.log(e);
			return;
		}
				
		ExperimentResult result = new ExperimentResult();
		
		// Step 1, generate the frequency matrix of the log
		DirectlyFollowsFrequencyMatrix<String> dfOriginalLog = DirectlyFollowsComputer.computeMatrix(originalLog);
		
		// Step 2, calculate precision and recall wrt true process model
		// result.setLogQualityTrueProcess(epr.computeMeasure(context, originalLog, trueProcessNet));
		result.setLogQualityTrueProcess(calculateRecallAndPrecision(context, originalLogFileName, trueProcessFileName));
		
		// Step 4a, discover a model from the log
		AcceptingPetriNet discoveredModel = discoverModel(context, originalLog);
		// Step 4b, store the model on disk
		String discoveredModelFileName = basePath + "/discovered.pnml";
		exportProcessModel(context, discoveredModel, new File(discoveredModelFileName));
		
		// Step 5, calculate precision and recall wrt discovered model
		// result.setLogQualityDiscoveredProcess(epr.computeMeasure(context, originalLog, discoveredModel));
		result.setLogQualityDiscoveredProcess(calculateRecallAndPrecision(context, originalLogFileName, discoveredModelFileName));
		
		// To validate the results, we also add the sample qualities of the log with itself. Should return 1 for all measures
		result.setSampleQuality(SampleMeasureComputer.computeMeasures(dfOriginalLog, dfOriginalLog, 1));
		results.add(result);
		
		for(double ratio: ratios) {
			for(int sample = 0; sample < samplesPerIteration ; sample++) {
				
				context.log("Sample (r: " + ratio + "): " + (sample + 1) + " / " + samplesPerIteration);
				
				// Step 6, create a sample
				XLog sampleLog =  sampleGenerator.drawSample(ratio);
				String sampleLogFileName = basePath + "/s" + ratio + "/s_" + ratio + "_" + sample + ".xes";
				exportLog(context, sampleLog, new File(sampleLogFileName));
				
				results.add(handleSample(context, trueProcess, trueProcessFileName, originalLog, originalLogFileName, dfOriginalLog, sampleLog, sampleLogFileName, basePath + "/s" + ratio + "/", sample, ratio));
				
			} // End of sample
			
			// ExperimentResult.export(results, new File(basePath + "/log"+ logIteration + "/results.csv"));
		}
	}
	
	private ExperimentResult handleSample(PluginContext context, AcceptingPetriNet trueProcess, String trueProcessFileName, XLog originalLog, String originalLogFileName, DirectlyFollowsFrequencyMatrix<String> dfOriginalLog, XLog sampleLog, String sampleLogFileName, String basePath, int sampleNr, double ratio) {
		ExperimentResult sampleResult = new ExperimentResult(ratio, sampleNr);
		
		DirectlyFollowsFrequencyMatrix<String> dfSampleLog = DirectlyFollowsComputer.computeMatrix(sampleLog);
		
		context.log("  calculate sample quality measures");
		
		// Step 7, calculate the quality of the sample wrt the original log
		sampleResult.setSampleQuality(SampleMeasureComputer.computeMeasures(dfOriginalLog, dfSampleLog, ratio));
		
		// Step 8, calculate precision and recall of sample wrt to true process model
		// sampleResult.setLogQualityTrueProcess(epr.computeMeasure(context, sampleLog, trueProcessNet));
		context.log("  calculate entropy sample - true process");
		// sampleResult.setLogQualityTrueProcess(calculateRecallAndPrecision(context, sampleLogFileName, trueProcessFileName));
		
		// Step 9, discover a model from the sample
		context.log("  discover model from sample - start");
		AcceptingPetriNet discoveredSampleModel = discoverModel(context, sampleLog);
		String discoveredSampleModelFileName = basePath + "/discovered_" + ratio + "_" + sampleNr + ".pnml";
		context.log("  discover model from sample - finish");
		exportProcessModel(context, discoveredSampleModel, new File(discoveredSampleModelFileName));
		
		// Step 10, calculate precision and recall of sample wrt discovered model
		// sampleResult.setSampleLogQualityDiscoveredProcess(epr.computeMeasure(context, sampleLog, discoveredSampleModel));
		context.log("  calculate entropy sample - discovered model");
		sampleResult.setSampleLogQualityDiscoveredProcess(calculateRecallAndPrecision(context, sampleLogFileName, discoveredSampleModelFileName));
		
		// Step 11, calculate precision and recall of sample wrt discovered model
		// sampleResult.setLogQualityDiscoveredProcess(epr.computeMeasure(context, originalLog, discoveredSampleModel));
		context.log("  calculate entropy original log - discovered model");
		// sampleResult.setLogQualityDiscoveredProcess(calculateRecallAndPrecision(context, originalLogFileName, discoveredSampleModelFileName));
		
		// Add results
		return sampleResult;
	}
	
	
	@PluginVariant( variantLabel = "Experiment - main", requiredParameterLabels = {})
	public Object run(final PluginContext context, int nrOfActivities, int nrOfModels, int nrOfLogsPerModel, int logSize, int samplesPerIteration, double... ratios) throws IOException {
		if (nrOfModels < 1) {
			nrOfModels = 1;
		}
				
		for(int modelIteration = 0; modelIteration < nrOfModels ; modelIteration++) {
			
			context.log("True process: " + (modelIteration + 1) + " / " + nrOfModels);
			
			// First, create the base path, so that we can store the experiment results			
			String basePath = getExperimentDirectory(); 
			
			// Step 1a, build a model
			NAryTreeGenerator generator = new NAryTreeGenerator();
			NAryTree trueProcessNAryTree = generator.generate(20);
			ProcessTree trueProcess = NAryTreeToProcessTree.convert(trueProcessNAryTree, null);
			EfficientTree efficientTrueProcess = ProcessTree2EfficientTree.convert(trueProcess);
			AcceptingPetriNet trueProcessNet = EfficientTree2AcceptingPetriNet.convert(efficientTrueProcess);
			
			// Step 1b, store the process model on disk
			String trueProcessFileName = basePath + "trueProcess.pnml";
			exportProcessModel(context, trueProcessNet, new File(trueProcessFileName));
			
			for(int logIteration = 0 ; logIteration < nrOfLogsPerModel ; logIteration++) {
		
				context.log("Create log: " + (logIteration + 1 ) + " / " + nrOfLogsPerModel);
				
				XLog originalLog = GenerateLogAndModel.generateLog(trueProcessNAryTree, logSize);
				String originalLogFileName = basePath + "/log" + logIteration + "/log.xes";
				exportLog(context, originalLog, new File(originalLogFileName));
				
				AbstractSampleGenerator sampleGenerator = new RandomSampler(originalLog);
				
				handleTrueProcess(context, trueProcessNet, trueProcessFileName, originalLog, originalLogFileName, sampleGenerator, basePath + "/log" + logIteration + "/", samplesPerIteration, ratios);
			}
		}
        
		return null;
	}
	
	private String getExperimentDirectory() throws IOException {
		String basePath = getBasePath();
		if (!Files.isDirectory(Paths.get(basePath))) {
			throw new IOException("Base path: '" + basePath + "' does not exist!");
		}
		int nr = 1;
		while(Files.isDirectory(Paths.get(basePath + "/exp" + nr))) {
			nr++;
		}
		return basePath + "/exp" + nr + "/";		
	}

	public AcceptingPetriNet discoverModel(PluginContext context, XLog log) {
		AcceptingPetriNet net = DialogFreeInductiveMiner.mineWithInfrequentInductiveMiner(context, log);
		
		return net;
	}
	
	public String getBasePath() {
		return "./experiments/im-2/";
	}
	

	// Used to store the process model
	private ExportAcceptingPetriNetPlugin petriNetExporter = new ExportAcceptingPetriNetPlugin();
			
	protected void exportProcessModel(PluginContext context, AcceptingPetriNet net, File file) {
		
		// Rename all transitions that start with "tau " in the name to empty name
		// This is required for the tool Entropia
		for(Transition transition: net.getNet().getTransitions()) {
			if (transition.getLabel().toLowerCase().startsWith("tau ")) {
				transition.getAttributeMap().put(AttributeMap.LABEL, "");
			}
		}
			
		try {
			// We need to create the file first, since otherwise the exporter crashes.
			file.getParentFile().mkdirs();
			file.createNewFile();
			petriNetExporter.export(context, net, file);
		} catch (IOException e) {
			context.log(e);
		}
	}
	
	protected void exportLog(PluginContext context, XLog log, File file) {
		try {
			file.getParentFile().mkdirs();
			file.createNewFile();
			ExportLogXes.export(log, file);
		} catch (IOException e) {
			context.log(e);
		}
				
	}
	
	protected EntropyMeasure calculateRecallAndPrecision(PluginContext context, String logFile, String modelFile) {
		
		//java -jar jbpt-pm-entropia-1.5.jar -empr -rel=janmartijn\log1\log.xes -ret=janmartijn\trueProcess.pnml
		// add -silent for the real version
		// First argument is: precision, Second is recall
		
		context.log("Start entropia on log: " + logFile + ", and model: " + modelFile, MessageLevel.NORMAL);
		
		String cmd = "java -jar ./experiments/entropia/jbpt-pm-entropia-1.5.jar -silent -empr -rel=" + logFile + " -ret=" + modelFile;
		
		double recall = 0;
		double precision = 0;
		
		Runtime runtime = Runtime.getRuntime();
		Process process;
		try {
			process = runtime.exec(cmd);
			process.waitFor();
			BufferedReader buffer = new BufferedReader(new InputStreamReader(process.getInputStream()));
			String line = "";
			while((line = buffer.readLine()) != null) {
				if (line.contains(",")) {
					String[] elements = line.split(",");
					if (elements.length >= 2) {
						precision = Double.parseDouble(elements[0]);
						recall = Double.parseDouble(elements[1]);
						context.log("Found precision: " + precision + ", recall: " + recall);
						break;
					}
				}
			}
		} catch (IOException | InterruptedException | NumberFormatException e) {
			context.log(e);
			return new EntropyMeasureImpl(0.0, 0.0);
		}
		
		context.log("Finished entropia", MessageLevel.NORMAL);
		return new EntropyMeasureImpl(recall, precision);
		// */
	}
}
