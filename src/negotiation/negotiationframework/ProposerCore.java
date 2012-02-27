package negotiation.negotiationframework;

import java.util.Set;

import negotiation.negotiationframework.contracts.AbstractActionSpecification;
import negotiation.negotiationframework.contracts.AbstractContractTransition;
import dima.introspectionbasedagents.services.AgentCompetence;
import dima.introspectionbasedagents.shells.NotReadyException;

public interface ProposerCore
<Agent extends SimpleNegotiatingAgent<ActionSpec, PersonalState, Contract>,
ActionSpec extends AbstractActionSpecification,
PersonalState extends ActionSpec,
Contract extends AbstractContractTransition<ActionSpec>>
extends AgentCompetence<Agent>{

	public Set<? extends Contract> getNextContractsToPropose()
			throws NotReadyException;

}