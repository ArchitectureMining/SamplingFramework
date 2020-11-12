package org.architecturemining.samplingframework.experiment;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.architecturemining.samplingframework.algorithms.DirectlyFollowsComputer;
import org.architecturemining.samplingframework.models.DirectlyFollowsFrequencyMatrix;
import org.deckfour.xes.model.XLog;
import org.processmining.framework.plugin.PluginContext;
import org.processmining.framework.plugin.PluginDescriptor;
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
	
	private void printPluginOverview(final PluginContext context, String idpattern) {
		for(PluginDescriptor plugin : context.getPluginManager().getAllPlugins(true)) {
			if (plugin.getID().toString().contains(idpattern)) {
				System.out.println("-----------");
				System.out.println("ID  : " + plugin.getID());
				System.out.println("Name: " + plugin.getName());
				for(int i = 0 ; i < plugin.getNumberOfMethods(); i++) {
					System.out.println("  " + i + ": " + plugin.getMethodLabel(i));
				}
			}
		}
	}
	
	public void processFile(PluginContext context, String fileName) {
		XLog log = createXESLogFromCSV(context, fileName);
		
		DirectlyFollowsFrequencyMatrix<String> matrix = DirectlyFollowsComputer.computeMatrix(log);
		
		
		/*
		AcceptingPetriNet net = DialogFreeInductiveMiner.mineWithInfrequentInductiveMiner(context, log);
		
		context.log("Places     : " + net.getNet().getPlaces().size());
		context.log("Transitions: " + net.getNet().getTransitions().size());
		*/
	}
	
		
	@PluginVariant( variantLabel = "Experiment - main", requiredParameterLabels = {})
	public Object run(final PluginContext context) {
		
		processFile(context, "D:/projects/ProM/Data/ToAnalyseRoad2/1.csv");
		
		return null;
	}
}
