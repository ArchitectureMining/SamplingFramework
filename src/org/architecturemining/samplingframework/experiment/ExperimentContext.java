package org.architecturemining.samplingframework.experiment;

import org.processmining.framework.plugin.PluginContext;
import org.processmining.framework.plugin.impl.AbstractGlobalContext;

public class ExperimentContext extends AbstractGlobalContext {

	private PluginContext mainContext;
	
	public ExperimentContext() {
		mainContext = new ExperimentPluginContext(this, "experiment");
	}
	
	protected PluginContext getMainPluginContext() {
		return mainContext;
	}

	public Class<? extends PluginContext> getPluginContextType() {
		return ExperimentPluginContext.class;
	}

}
