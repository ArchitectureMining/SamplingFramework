package org.architecturemining.samplingframework.experiment;

import org.processmining.framework.plugin.PluginContext;
import org.processmining.framework.plugin.PluginDescriptor;
import org.processmining.framework.plugin.impl.AbstractGlobalContext;

public class ExperimentContext extends AbstractGlobalContext {

	private PluginContext mainContext;
	
	public ExperimentContext() {
		mainContext = new ExperimentPluginContext(this, "experiment");
	}
	
	@Override
	protected PluginContext getMainPluginContext() {
		return mainContext;
	}

	@Override
	public Class<? extends PluginContext> getPluginContextType() {
		return ExperimentPluginContext.class;
	}
	
	@Override
	public void invokePlugin(PluginDescriptor plugin, int index, Object... objects) {
		System.out.println("Call global invoke");
		super.invokePlugin(plugin, index, objects);
	}

}
