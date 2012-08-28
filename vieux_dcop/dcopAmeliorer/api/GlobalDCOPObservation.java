package vieux.dcopAmeliorer.api;

import java.util.HashMap;

import vieux.dcopAmeliorer.algo.BasicAlgorithm;


import dima.basicagentcomponents.AgentIdentifier;
import dima.introspectionbasedagents.annotations.MessageHandler;
import dima.introspectionbasedagents.modules.aggregator.HeavyDoubleAggregation;
import dima.introspectionbasedagents.services.observingagent.NotificationMessage;
import dima.introspectionbasedagents.services.observingagent.NotificationEnvelopeClass.NotificationEnvelope;
import frameworks.experimentation.ExperimentationParameters;
import frameworks.experimentation.ExperimentationResults;
import frameworks.experimentation.Laborantin;
import frameworks.experimentation.ObservingGlobalService;

public class GlobalDCOPObservation extends ObservingGlobalService<Laborantin>{

	public GlobalDCOPObservation(final ExperimentationParameters rep) {
		super(rep);
	}


	/**
	 *
	 */
	private static final long serialVersionUID = -2038579825092274298L;

	HashMap<AgentIdentifier,Boolean> appIsStable = new HashMap<AgentIdentifier, Boolean>();

	HeavyDoubleAggregation[] agentsValueEvolution;


	//
	// Accessors
	//

	@Override
	public boolean simulationHasEnded(){
		for (final Boolean b : this.appIsStable.values()){
			if (b == false) {
				return false;
			}
		}
		return true;
	}

	@MessageHandler
	@NotificationEnvelope(BasicAlgorithm.stabilityNotificationKey)
	public void receiveStability(final NotificationMessage<Boolean> m){
		this.appIsStable.put(m.getSender(), m.getNotification());
	}

	//
	// Method
	//

	@Override
	public void initiate() {
		this.agentsValueEvolution = new HeavyDoubleAggregation[ObservingGlobalService.getNumberOfTimePoints()];
		for (int i = 0; i < ObservingGlobalService.getNumberOfTimePoints(); i++) {
			this.agentsValueEvolution[i] = new HeavyDoubleAggregation();
		}

	}


	@Override
	protected void updateInfo(final ExperimentationResults notification) {
		final DcopExperimentationResult dexp = (DcopExperimentationResult) notification;
		final int i = ObservingGlobalService.getTimeStep(dexp);
		if (i < ObservingGlobalService.getNumberOfTimePoints()) {
			this.agentsValueEvolution[i].add((double)dexp.value);
		}

	}


	@Override
	protected void writeResult() {
		System.out.println("yooooooooooooooo");
		//		LogService.logOnFile(getMyAgent().getSimulationParameters().getResultPath(), ObservingGlobalService
		//				.getQuantileTimeEvolutionObs(getMyAgent().getSimulationParameters(),"reliability",
		//						this.agentsValueEvolution, 0.75 * (
		//								getMyAgent().getNbAgents() /
		//								getMyAgent().getNbAgents()),
		//								getMyAgent().getNbAgents()), true,
		//								false);
	}
}
