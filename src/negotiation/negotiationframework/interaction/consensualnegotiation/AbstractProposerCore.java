package negotiation.negotiationframework.interaction.consensualnegotiation;

import java.util.Set;

import negotiation.negotiationframework.SimpleNegotiatingAgent;
import negotiation.negotiationframework.interaction.contracts.AbstractActionSpecification;
import negotiation.negotiationframework.interaction.contracts.AbstractContractTransition;
import dima.introspectionbasedagents.services.AgentCompetence;
import dima.introspectionbasedagents.shells.NotReadyException;

public interface AbstractProposerCore
<Agent extends SimpleNegotiatingAgent<ActionSpec, PersonalState, Contract>,
ActionSpec extends AbstractActionSpecification,
PersonalState extends ActionSpec,
Contract extends AbstractContractTransition<ActionSpec>>
extends AgentCompetence<Agent>{

	public Set<? extends Contract> getNextContractsToPropose()
			throws NotReadyException;

}