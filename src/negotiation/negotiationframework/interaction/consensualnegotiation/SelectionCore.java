package negotiation.negotiationframework.interaction.consensualnegotiation;

import negotiation.negotiationframework.SimpleNegotiatingAgent;
import negotiation.negotiationframework.interaction.AbstractActionSpecification;
import negotiation.negotiationframework.interaction.AbstractContractTransition;
import dima.introspectionbasedagents.services.AgentCompetence;

public interface SelectionCore<
ActionSpec extends AbstractActionSpecification,
PersonalState extends ActionSpec, 
Contract extends AbstractContractTransition<ActionSpec>>
extends	AgentCompetence<SimpleNegotiatingAgent<ActionSpec, PersonalState, Contract>> {

	// Select contract to accept/wait/reject for a participant
	// Select contract to request/cancel for a initiator
	public ContractTrunk<Contract> select(ContractTrunk<Contract> cs);

}
