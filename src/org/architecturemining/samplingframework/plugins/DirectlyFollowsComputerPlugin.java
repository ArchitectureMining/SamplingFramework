package org.architecturemining.samplingframework.plugins;

import org.architecturemining.samplingframework.algorithms.DirectlyFollowsComputer;
import org.architecturemining.samplingframework.models.DirectlyFollowsFrequencyMatrix;
import org.deckfour.xes.classification.XEventClassifier;
import org.deckfour.xes.model.XLog;
import org.processmining.framework.plugin.PluginContext;
import org.processmining.framework.plugin.annotations.Plugin;
import org.processmining.framework.plugin.annotations.PluginVariant;

@Plugin(
	name = "Directly Follows Frequency Computer",
	returnLabels = {"Directly Follows Frequency Matrix"},
	returnTypes = { DirectlyFollowsFrequencyMatrix.class },
	parameterLabels = { "Event log", "Event classifier" },
	userAccessible = true
)
public class DirectlyFollowsComputerPlugin {

	@PluginVariant(variantLabel = "Log only, default classifier", requiredParameterLabels = {0})
	public DirectlyFollowsFrequencyMatrix<String> computeMatrix(PluginContext context, XLog log) {
		return DirectlyFollowsComputer.computeMatrix(log);
	}
	
	@PluginVariant(variantLabel = "Flexible", requiredParameterLabels = {0, 1})
	public DirectlyFollowsFrequencyMatrix<String> computeMatrix(PluginContext context, XLog log, XEventClassifier classifier) {
		return DirectlyFollowsComputer.computeMatrix(log, classifier);
	}
}
