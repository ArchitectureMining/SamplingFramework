package org.architecturemining.samplingframework.algorithms;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import org.architecturemining.samplingframework.models.EntropyMeasure;
import org.architecturemining.samplingframework.models.impl.EntropyMeasureImpl;
import org.processmining.framework.plugin.PluginContext;
import org.processmining.framework.plugin.events.Logger.MessageLevel;

public class EntropyComputer {

	public static EntropyMeasure calculateRecallAndPrecision(PluginContext context, String logFile, String modelFile) {
		
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
