/**
 * 
 */
package org.architecturemining.samplingframework.runtime;

import java.lang.reflect.InvocationTargetException;
import java.time.LocalDateTime;

import org.architecturemining.samplingframework.experiment.AbstractExperiment;
import org.architecturemining.samplingframework.experiment.GeneratingExperiment;
import org.architecturemining.samplingframework.experiment.RunOnExistingDirectory;
import org.architecturemining.samplingframework.experiment.SampleExistingLog;
import org.processmining.framework.boot.Boot;
import org.processmining.framework.plugin.PluginContextID;
import org.processmining.framework.plugin.annotations.Bootable;
import org.processmining.framework.plugin.annotations.Plugin;
import org.processmining.framework.plugin.events.Logger;
import org.processmining.framework.util.CommandLineArgumentList;

/**
 * @author janma
 *
 */
public class ExperimentRunner implements Logger {

	public AbstractExperiment getExperimentOnExistingRoadLog() {
		return new RunOnExistingDirectory("D:\\projects\\ProM\\workspace\\SamplingFramework\\experiments\\road");
	}
	
	public AbstractExperiment getExperimentOnExistingSepsisLog() {
		return new RunOnExistingDirectory("D:\\projects\\ProM\\workspace\\SamplingFramework\\experiments\\Sepsis");
	}
	
	public AbstractExperiment getExperiment() {
		try {
			return new SampleExistingLog(
					"D:\\projects\\ProM\\workspace\\SamplingFramework\\experiments\\Logs\\Sepsis\\original.xes",
					"D:\\projects\\ProM\\workspace\\SamplingFramework\\experiments\\Logs\\Sepsis\\results"
					);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return null;
	}
	
	public AbstractExperiment getExperimentRoad() {
		try {
			return new SampleExistingLog(
					"D:\\projects\\ProM\\workspace\\SamplingFramework\\experiments\\Logs\\Road\\original.xes",
					"D:\\projects\\ProM\\workspace\\SamplingFramework\\experiments\\Logs\\Road\\results"
					);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return null;
	}
	
	
	public AbstractExperiment getExperimentWithIM() {
		return new GeneratingExperiment();
	}
	
	public AbstractExperiment getExperimentBpiChallenge2012() {
		try {
			return new SampleExistingLog(
					"D:\\projects\\ProM\\workspace\\SamplingFramework\\experiments\\logs\\bpi2012\\BPI_Challenge_2012.xes", 
					"D:\\projects\\ProM\\workspace\\SamplingFramework\\experiments\\logs\\bpi2012\\results");
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return null;
	}
	
	public AbstractExperiment getExperimentBPIChallenge2018() {
		try {
			return new SampleExistingLog(
					"D:\\projects\\ProM\\workspace\\SamplingFramework\\experiments\\logs\\bpi2018\\BPI_Challenge_2018.xes", 
					"D:\\projects\\ProM\\workspace\\SamplingFramework\\experiments\\logs\\bpi2018\\results");
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return null;
	}
	
	
	@Plugin( name="Experiment Runner", parameterLabels = {}, returnLabels = {}, returnTypes = {}, userAccessible = false)
	@Bootable
	public Object main(CommandLineArgumentList commandlineArguments) {
		// if (Boot.VERBOSE != Level.NONE) {
			System.out.println("Starting experiment...");
		// }
		
		ExperimentContext globalContext = new ExperimentContext();
		globalContext.getMainPluginContext().getLoggingListeners().add(this);
	
		getExperiment().run(globalContext.getMainPluginContext());
		
		
		System.exit(0);
		
		return null;
	}
	
	@Override
	public void log(String message, PluginContextID contextID, MessageLevel messageLevel) {
		System.out.print(messageLevel.toString().toUpperCase());
		System.out.print(" [");
		System.out.print(LocalDateTime.now());
		System.out.print("] - ");
		System.out.print(contextID.toString());
		System.out.print(" - ");
		System.out.println(message);

	}

	@Override
	public void log(Throwable t, PluginContextID contextID) {
		log(t.getMessage(), contextID, MessageLevel.ERROR);
		t.printStackTrace();
	}
	
	

	/**
	 * @param args
	 */
	public static void main(String[] args) throws Throwable {
		// Create an experiment context that can be used to call the different plugins.
		
		try {
			Boot.boot(ExperimentRunner.class, ExperimentPluginContext.class, args);
		} catch(InvocationTargetException e) {
			throw e.getCause();
		}
	}
}
