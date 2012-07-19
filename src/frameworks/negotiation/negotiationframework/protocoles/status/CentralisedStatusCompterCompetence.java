package frameworks.negotiation.negotiationframework.protocoles.status;

import java.util.ArrayList;
import java.util.Collection;

import dima.basicagentcomponents.AgentIdentifier;
import dima.introspectionbasedagents.annotations.Competence;
import dima.introspectionbasedagents.annotations.MessageHandler;
import dima.introspectionbasedagents.annotations.StepComposant;
import dima.introspectionbasedagents.services.BasicAgentCompetence;
import dima.introspectionbasedagents.services.BasicCommunicatingCompetence;
import dima.introspectionbasedagents.services.UnrespectedCompetenceSyntaxException;
import dima.introspectionbasedagents.services.core.information.NoInformationAvailableException;
import dima.introspectionbasedagents.services.core.opinion.Believer;
import dima.introspectionbasedagents.services.core.opinion.OpinionService;
import dima.introspectionbasedagents.services.core.opinion.SimpleOpinionService;
import dima.introspectionbasedagents.services.core.opinion.OpinionService.Opinion;
import dima.introspectionbasedagents.services.modules.aggregator.HeavyDoubleAggregation;
import dima.introspectionbasedagents.shells.CommunicatingCompetentComponent;
import frameworks.negotiation.faulttolerance.experimentation.ReplicationLaborantin;
import frameworks.negotiation.negotiationframework.NegotiationParameters;
import frameworks.negotiation.negotiationframework.rationality.AgentState;

public class CentralisedStatusCompterCompetence extends BasicCommunicatingCompetence<Believer> {
	
	private final Collection<AgentIdentifier> acquaintances = new ArrayList<AgentIdentifier>();
	private final Class<? extends AgentState> stateTypeToDiffuse;
	
	public CentralisedStatusCompterCompetence(
			Class<? extends AgentState> stateTypeToDiffuse)
			throws UnrespectedCompetenceSyntaxException {
		super();
		this.stateTypeToDiffuse = stateTypeToDiffuse;
	
	}
	

	@MessageHandler
	void updateAgent4StatusObservation(final StatusMessage n) {
		getMyAgent().getMyOpinion().add(n.getTransmittedState());
	}

	@StepComposant(ticker=NegotiationParameters._timeToCollect)
	void diffuseInfo(){
		try {
			Opinion o = getMyAgent().getMyOpinion().getGlobalOpinion(stateTypeToDiffuse);
			this.sendMessage(acquaintances, 
					new StatusMessage(o));	
//		logMonologue(o.getNumberOfAggregatedElements()+" "
//		+o.getAggregatedAgents()+"\n"+o.getMaxElement()+"\n"+o.getMinElement()+"\n"+o.getRepresentativeElement());
		} catch (NoInformationAvailableException e) {}
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
