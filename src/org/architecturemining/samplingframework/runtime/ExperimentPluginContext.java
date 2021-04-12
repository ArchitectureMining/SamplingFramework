package org.architecturemining.samplingframework.runtime;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import org.processmining.contexts.cli.CLIProgressBar;
import org.processmining.framework.plugin.GlobalContext;
import org.processmining.framework.plugin.PluginContext;
import org.processmining.framework.plugin.PluginExecutionResult;
import org.processmining.framework.plugin.ProMFuture;
import org.processmining.framework.plugin.impl.AbstractPluginContext;

// This implementation follows org.processmining.contexts.cli.AbstractPluginContext;

public class ExperimentPluginContext extends AbstractPluginContext {
	
	private final Executor executor;

	public ExperimentPluginContext(GlobalContext context, String label) {
		super(context, label);
		
		executor = Executors.newCachedThreadPool();
		progress = new CLIProgressBar(this);
	}
	
	protected ExperimentPluginContext(ExperimentPluginContext parent, String label) {
		super(parent, label);
		
		if (parent == null) {
			executor = Executors.newCachedThreadPool();
		} else {
			executor = parent.getExecutor();
		}
	}
	
	@Override
	public ProMFuture<?> getFutureResult(int i) {
		System.out.println("Call FutureResult(" + i + ")");
		return super.getFutureResult(i);
	}
	
	@Override
	public void setFuture(PluginExecutionResult futureToBe) {
		System.out.println("Call setFuture");
		System.out.println("Future To Be null? " + (futureToBe == null));
		super.setFuture(futureToBe);
	}

	@Override
	public Executor getExecutor() {
		return executor;
	}

	@Override
	protected synchronized PluginContext createTypedChildContext(String label) {
		System.out.println("Created typed child for: " + label);
		return new ExperimentPluginContext(this, label);
	}
	
	@Override
	public ExperimentContext getGlobalContext() {
		return (ExperimentContext) super.getGlobalContext();
	}
	
	@Override
	public ExperimentPluginContext getRootContext() {
		return (ExperimentPluginContext) super.getRootContext();
	}

}
