package negotiation.negotiationframework.interaction.consensualnegotiation;

import java.util.Collection;

import dima.introspectionbasedagents.NotReadyException;
import dima.introspectionbasedagents.services.AgentCompetence;

import negotiation.negotiationframework.SimpleNegotiatingAgent;
import negotiation.negotiationframework.interaction.AbstractActionSpecification;
import negotiation.negotiationframework.interaction.AbstractContractTransition;

public interface AbstractProposerCore
<Agent extends SimpleNegotiatingAgent<ActionSpec, PersonalState, Contract>,
ActionSpec extends AbstractActionSpecification,
PersonalState extends ActionSpec, 
Contract extends AbstractContractTransition<ActionSpec>>
extends AgentCompetence<Agent>{
	
	public Collection<? extends Contract> getNextContractsToPropose()
			throws NotReadyException;

}