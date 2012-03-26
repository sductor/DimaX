package examples.dcop.api;

import java.util.HashMap;

import dima.basicagentcomponents.AgentIdentifier;
import dima.introspectionbasedagents.annotations.MessageHandler;
import dima.introspectionbasedagents.services.UnrespectedCompetenceSyntaxException;
import dima.introspectionbasedagents.services.loggingactivity.LogService;
import dima.introspectionbasedagents.services.observingagent.NotificationMessage;
import dima.introspectionbasedagents.services.observingagent.NotificationEnvelopeClass.NotificationEnvelope;
import dima.introspectionbasedagents.shells.BasicCompetentAgent;
import dimaxx.experimentation.ExperimentationParameters;
import dimaxx.experimentation.ExperimentationResults;
import dimaxx.experimentation.Laborantin;
import dimaxx.experimentation.ObservingGlobalService;
import dimaxx.experimentation.ObservingSelfService.ActivityLog;
import dimaxx.tools.aggregator.HeavyDoubleAggregation;
import examples.dcop.algo.BasicAlgorithm;

public class GlobalDCOPObservation extends ObservingGlobalService<DCOPLaborantin>{

	HashMap<AgentIdentifier,Boolean> appIsStable = new HashMap<AgentIdentifier, Boolean>();

	HeavyDoubleAggregation[] agentsValueEvolution;

	/* Quantile */
	//
	// Constructor
	//

	public GlobalDCOPObservation(DCOPLaborantin ag)
			throws UnrespectedCompetenceSyntaxException {
		super(ag);
	}

	//
	// Accessors
	//

	public boolean appIsStable(){
		for (Boolean b : appIsStable.values()){
			if (b == false)
				return false;
		}
		return true;
	}
	//
	// Method
	//

	@MessageHandler
	@NotificationEnvelope(BasicAlgorithm.stabilityNotificationKey)
	public void receiveStability(final NotificationMessage<Boolean> m){
		appIsStable.put(m.getSender(), m.getNotification());
	}

	@Override
	public void initiate() {
		this.agentsValueEvolution = new HeavyDoubleAggregation[ObservingGlobalService.getNumberOfTimePoints()];
		for (int i = 0; i < ObservingGlobalService.getNumberOfTimePoints(); i++) {
			this.agentsValueEvolution[i] = new HeavyDoubleAggregation();
		}

	}

	@Override
	protected void setObservation() {
		for (final BasicCompetentAgent ag : this.getMyAgent().getAgents()) {
			ag.addObserver(this.getIdentifier(), ActivityLog.class);
		}
	}

	@Override
	protected void updateInfo(ExperimentationResults notification) {
		final DcopExperimentationResult dexp = (DcopExperimentationResult) notification;
		int i = ObservingGlobalService.getTimeStep(dexp);
		if (i < ObservingGlobalService.getNumberOfTimePoints()) {
			this.agentsValueEvolution[i].add((double)dexp.value);
		}
		
	}


	@Override
	protected void writeResult() {
		LogService.logOnFile(getMyAgent().getSimulationParameters().getResultPath(), ObservingGlobalService
				.getQuantileTimeEvolutionObs(getMyAgent().getSimulationParameters(),"reliability",
						this.agentsValueEvolution, 0.75 * (
								getMyAgent().getNbAgents() / 
								getMyAgent().getNbAgents()),
								getMyAgent().getNbAgents()), true,
								false);
	}
}
