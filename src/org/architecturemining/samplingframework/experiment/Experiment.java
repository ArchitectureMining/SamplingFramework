package org.architecturemining.samplingframework.experiment;

import org.processmining.framework.plugin.PluginContext;
import org.processmining.framework.plugin.annotations.Plugin;
import org.processmining.framework.plugin.annotations.PluginVariant;
import org.processmining.log.csv.CSVFile;
import org.processmining.log.csvimport.CSVImportPluginUnivocity;

public class Experiment {

	
	// Open a CSV file
	
	
	@Plugin(
			name = "Experiment - open CSV File", 
			returnLabels = {"CSV File"}, 
			returnTypes = {CSVFile.class},
			parameterLabels = { "File location" },
			userAccessible = true
	)
	@PluginVariant(variantLabel = "Experiment - load CSV File", requiredParameterLabels = {0})
	public CSVFile createXESLogFromCSV(final PluginContext context, String filename) {	
		try {
			CSVImportPluginUnivocity importer = new CSVImportPluginUnivocity();
			CSVFile f = (CSVFile) importer.importFile(context, filename);
			
			if (f == null) {
				context.log("File: + " + filename + " is not a readable CSV-file");
			} else {
				context.log(f.toString());
			}
			
		} catch (Exception e) {
			context.log(e);
		}
		
		return null;
	}
		
	@Plugin(
			name = "Experiment - runner",
			returnLabels = { "Example" },
			returnTypes = { Object.class },
			parameterLabels = {},
			userAccessible = true
			)
	@PluginVariant( variantLabel = "Experiment - main", requiredParameterLabels = {})
	public Object run(final PluginContext context) {
		return createXESLogFromCSV(context, "D:\\SurfDrive\\Onderwijs\\Afstudeerders\\MSc\\Wensveen, B\\ToAnalyseRoad2\\1.csv");
	}
	
}
