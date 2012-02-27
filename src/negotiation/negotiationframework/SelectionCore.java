package negotiation.negotiationframework;

import negotiation.negotiationframework.contracts.AbstractActionSpecification;
import negotiation.negotiationframework.contracts.AbstractContractTransition;
import negotiation.negotiationframework.contracts.ContractTrunk;
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
