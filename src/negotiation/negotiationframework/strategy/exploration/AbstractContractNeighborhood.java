package negotiation.negotiationframework.strategy.exploration;

import java.util.Collection;
import java.util.Iterator;

import negotiation.negotiationframework.interaction.contracts.AbstractActionSpecification;
import negotiation.negotiationframework.interaction.contracts.AbstractContractTransition;
import dima.basicagentcomponents.AgentIdentifier;
import dima.basicinterfaces.DimaComponentInterface;

public interface AbstractContractNeighborhood<
Action extends AbstractContractTransition<ActionSpec>,//remplacer matching candidature par abstractaction pour plus de généricité
ActionSpec extends AbstractActionSpecification>
extends DimaComponentInterface{

	public abstract AllocationTransition<Action, ActionSpec> getEmptyContract();

	public abstract AllocationTransition<Action, ActionSpec> getRandomContract(Collection<AgentIdentifier> knownAgents, Collection<String> knownActions);

	public abstract Iterator<Action> getNeighbors(
			AllocationTransition<Action, ActionSpec> c,
			Collection<AgentIdentifier> knownAgents,
			Collection<String> knownActions);

}