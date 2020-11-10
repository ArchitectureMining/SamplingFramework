package org.architecturemining.samplingframework.experiment;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.deckfour.xes.model.XLog;
import org.processmining.acceptingpetrinet.models.AcceptingPetriNet;
import org.processmining.framework.packages.PackageManager.Canceller;
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
import org.processmining.plugins.InductiveMiner.efficienttree.EfficientTreeReduce.ReductionFailedException;
import org.processmining.plugins.InductiveMiner.efficienttree.UnknownTreeNodeException;
import org.processmining.plugins.inductiveminer2.logs.IMLog;
import org.processmining.plugins.inductiveminer2.mining.MiningParameters;
import org.processmining.plugins.inductiveminer2.plugins.InductiveMinerPlugin;
import org.processmining.plugins.inductiveminer2.variants.MiningParametersIMInfrequent;


@Plugin(
		name = "Experiment - runner",
		returnLabels = { "Example" },
		returnTypes = { Object.class },
		parameterLabels = {},
		userAccessible = true
		)
public class Experiment {

	/**
	 * Opens a CSV file and converts it to a XES log
	 * @param context
	 * @param filename
	 * @return
	 */
	public XLog createXESLogFromCSV(final PluginContext context, String filename) {	
		try {
			File file = new File(filename);
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
	
	/**
	 * Call the inductive miner
	 * @param context
	 * @return
	 */
	public AcceptingPetriNet mineWithInductiveMiner(PluginContext context, XLog xLog) {
		MiningParameters parameters = new MiningParametersIMInfrequent();
		IMLog log = parameters.getIMLog(xLog);
		
		try {
			return InductiveMinerPlugin.minePetriNet(log, parameters, new Canceller() {
				public boolean isCancelled() {
					return context.getProgress().isCancelled();
				}
			});
		} catch (UnknownTreeNodeException | ReductionFailedException e) {
			context.log(e);
		}
		
		return null;
	}
	
		
	@PluginVariant( variantLabel = "Experiment - main", requiredParameterLabels = {})
	public Object run(final PluginContext context) {
		//return createXESLogFromCSV(context, "D:/projects/ProM/Data/ToAnalyseRoad2/1.csv");
		XLog log = createXESLogFromCSV(context, "D:/projects/ProM/Data/ToAnalyseRoad2/1.csv");
		
		AcceptingPetriNet net = mineWithInductiveMiner(context, log);
		
		context.log("Places     : " + net.getNet().getPlaces().size());
		context.log("Transitions: " + net.getNet().getTransitions().size());
		
		return net;
	}
}
