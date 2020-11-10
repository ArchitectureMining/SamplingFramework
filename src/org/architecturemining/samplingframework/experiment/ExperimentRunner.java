/**
 * 
 */
package org.architecturemining.samplingframework.experiment;

import java.lang.reflect.InvocationTargetException;
import java.time.LocalDateTime;

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

	@Plugin( name="Experiment Runner", parameterLabels = {}, returnLabels = {}, returnTypes = {}, userAccessible = false)
	@Bootable
	public Object main(CommandLineArgumentList commandlineArguments) {
		// if (Boot.VERBOSE != Level.NONE) {
			System.out.println("Starting experiment...");
		// }
		
		ExperimentContext globalContext = new ExperimentContext();
		globalContext.getMainPluginContext().getLoggingListeners().add(this);
	
		Experiment exp = new Experiment();
		
		exp.run(globalContext.getMainPluginContext());
				
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
