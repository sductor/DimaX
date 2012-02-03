package negotiation.negotiationframework.strategy.exploration;

import java.util.Collection;

import negotiation.negotiationframework.interaction.contracts.AbstractActionSpecification;
import negotiation.negotiationframework.interaction.contracts.AbstractContractTransition;
import negotiation.negotiationframework.strategy.evaluation.AbstractStrategicEvaluationModule;
import dima.basicagentcomponents.AgentIdentifier;
import dima.basicinterfaces.DimaComponentInterface;
import dima.introspectionbasedagents.services.information.NoInformationAvailableException;

public interface AbstractStrategicExplorationModule
<Contract extends AbstractContractTransition<ActionSpec>,
ActionSpec extends AbstractActionSpecification>
extends DimaComponentInterface{//ProposerCore<PersonalState, Action, ActionSpec>{

	Contract getNextContractToPropose(
			AbstractStrategicEvaluationModule<Contract, ActionSpec> myComparator,
			final Collection<AgentIdentifier> knownAgents,
			final Collection<String> knownActions )
					throws NoInformationAvailableException;

}
