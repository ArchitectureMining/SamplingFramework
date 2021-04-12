package org.architecturemining.samplingframework.experiment;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.architecturemining.samplingframework.algorithms.DialogFreeInductiveMiner;
import org.deckfour.xes.factory.XFactoryRegistry;
import org.deckfour.xes.in.XParser;
import org.deckfour.xes.in.XesXmlParser;
import org.deckfour.xes.model.XLog;
import org.processmining.acceptingpetrinet.models.AcceptingPetriNet;
import org.processmining.acceptingpetrinet.plugins.ExportAcceptingPetriNetPlugin;
import org.processmining.framework.plugin.PluginContext;
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
import org.processmining.plugins.log.exporting.ExportLogXes;

public abstract class AbstractExperiment {
	
	public abstract Object run(PluginContext context);
	
	
	public XLog loadXESLog(String fileName) throws Exception {
		XParser parser = new XesXmlParser(XFactoryRegistry.instance().currentDefault());
		
		List<XLog> logs = parser.parse(new File(fileName));
		if (logs.size() > 0) {
			return logs.get(0);
		}
		return null;
	}
	
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

	public AcceptingPetriNet discoverModel(PluginContext context, XLog log) {
		AcceptingPetriNet net = DialogFreeInductiveMiner.mineWithInfrequentInductiveMiner(context, log);
		
		return net;
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
		
		public void exportLog(PluginContext context, XLog log, File file) {
			try {
				file.getParentFile().mkdirs();
				file.createNewFile();
				ExportLogXes.export(log, file);
			} catch (IOException e) {
				context.log(e);
			}
					
		}
		
		
}
