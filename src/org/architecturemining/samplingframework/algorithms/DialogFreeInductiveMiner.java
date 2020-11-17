package org.architecturemining.samplingframework.algorithms;

import org.deckfour.xes.model.XLog;
import org.processmining.acceptingpetrinet.models.AcceptingPetriNet;
import org.processmining.framework.packages.PackageManager.Canceller;
import org.processmining.framework.plugin.PluginContext;
import org.processmining.framework.plugin.annotations.Plugin;
import org.processmining.framework.plugin.annotations.PluginVariant;
import org.processmining.plugins.InductiveMiner.efficienttree.EfficientTree;
import org.processmining.plugins.InductiveMiner.efficienttree.EfficientTree2AcceptingPetriNet;
import org.processmining.plugins.InductiveMiner.efficienttree.UnknownTreeNodeException;
import org.processmining.plugins.inductiveminer2.logs.IMLog;
import org.processmining.plugins.inductiveminer2.mining.MiningParameters;
import org.processmining.plugins.inductiveminer2.plugins.InductiveMinerPlugin;
import org.processmining.plugins.inductiveminer2.variants.MiningParametersIMInfrequent;


@Plugin(
		name = "Dialog free inductive miner",
		returnLabels = { "Accepting Petri net" },
		returnTypes = { AcceptingPetriNet.class },
		parameterLabels = {"Event log" },
		userAccessible = true
		)
public class DialogFreeInductiveMiner {

	/**
	 * Call the inductive miner
	 * @param context
	 * @return
	 */
	@PluginVariant(variantLabel = "Default Dialog free inductive miner", requiredParameterLabels = {0} )
	public static AcceptingPetriNet mineWithInfrequentInductiveMiner(PluginContext context, XLog xLog) {
		MiningParameters parameters = new MiningParametersIMInfrequent();
		IMLog log = parameters.getIMLog(xLog);
		
		try {
			EfficientTree tree = InductiveMinerPlugin.mineTree(log, parameters, new Canceller() { // InductiveMinerPlugin.minePetriNet(log, parameters, new Canceller() {
				public boolean isCancelled() {
					return context.getProgress().isCancelled();
				}
			});
			
			// EfficientTreeReduce.reduce(tree, new EfficientTreeReduceParametersForPetriNet(false));
			AcceptingPetriNet net = EfficientTree2AcceptingPetriNet.convert(tree);
			
			return net;
			
		} catch (UnknownTreeNodeException e) { // catch (UnknownTreeNodeException | ReductionFailedException e) {
			context.log(e);
		}
		
		return null;
	}
	
}
