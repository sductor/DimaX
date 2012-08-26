package frameworks.negotiation.protocoles.status;

import java.util.ArrayList;
import java.util.Collection;


import dima.basicagentcomponents.AgentIdentifier;
import dima.introspectionbasedagents.annotations.Competence;
import dima.introspectionbasedagents.annotations.MessageHandler;
import dima.introspectionbasedagents.annotations.StepComposant;
import dima.introspectionbasedagents.annotations.Transient;
import dima.introspectionbasedagents.kernel.CommunicatingCompetentComponent;
import dima.introspectionbasedagents.modules.aggregator.HeavyDoubleAggregation;
import dima.introspectionbasedagents.services.BasicAgentCompetence;
import dima.introspectionbasedagents.services.BasicCommunicatingCompetence;
import dima.introspectionbasedagents.services.UnrespectedCompetenceSyntaxException;
import dima.introspectionbasedagents.services.information.NoInformationAvailableException;
import frameworks.faulttolerance.experimentation.ReplicationLaborantin;
import frameworks.negotiation.NegotiationParameters;
import frameworks.negotiation.opinion.Believer;
import frameworks.negotiation.opinion.NoOpinionHandlerException;
import frameworks.negotiation.opinion.OpinionService;
import frameworks.negotiation.opinion.SimpleOpinionService;
import frameworks.negotiation.opinion.OpinionService.Opinion;
import frameworks.negotiation.rationality.AgentState;

public class CentralisedStatusCompterCompetence extends BasicCommunicatingCompetence<Believer> {
	
	private final Collection<AgentIdentifier> acquaintances = new ArrayList<AgentIdentifier>();
	private final Class<? extends AgentState> stateTypeToDiffuse;
	private final Long simulationTime;
	
	public CentralisedStatusCompterCompetence(
			Class<? extends AgentState> stateTypeToDiffuse,
			Long maxSimulationTime)
			throws UnrespectedCompetenceSyntaxException {
		super();
		this.stateTypeToDiffuse = stateTypeToDiffuse;
		this.simulationTime=maxSimulationTime;
	
	}
	

	@MessageHandler
	void updateAgent4StatusObservation(final StatusMessage n) {
		getMyAgent().getMyOpinion().add(n.getTransmittedState());
	}

	@Transient@StepComposant(ticker=NegotiationParameters.opinionDiffusionFrequency)
	boolean diffuseInfo(){
		try {
			Opinion o = getMyAgent().getMyOpinion().getGlobalOpinion(stateTypeToDiffuse);
			this.sendMessage(acquaintances, 
					new StatusMessage(o));	
//		logMonologue(o.getNumberOfAggregatedElements()+" "
//		+o.getAggregatedAgents()+"\n"+o.getMaxElement()+"\n"+o.getMinElement()+"\n"+o.getRepresentativeElement());
		} catch (NoInformationAvailableException e) {} 
		catch (NoOpinionHandlerException e) {
			throw new RuntimeException("impossible!!!!");
		}
		return getMyAgent().getUptime()>simulationTime;
	}

	public Collection<AgentIdentifier> getAcquaintances() {
		return acquaintances;
	}

	public boolean addAcquaintance(AgentIdentifier id) {
		return acquaintances.add(id);
	}

	public boolean removeAcquaintance(AgentIdentifier id) {
		return acquaintances.remove(id);
	}
}
