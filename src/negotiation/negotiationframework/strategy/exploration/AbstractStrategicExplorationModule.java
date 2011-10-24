package negotiation.negotiationframework.strategy.exploration;

import java.util.Collection;

import negotiation.negotiationframework.interaction.AbstractActionSpecification;
import negotiation.negotiationframework.interaction.AbstractContractTransition;
import negotiation.negotiationframework.strategy.evaluation.AbstractStrategicEvaluationModule;
import dima.basicagentcomponents.AgentIdentifier;
import dima.basicinterfaces.DimaComponentInterface;
import dima.introspectionbasedagents.services.library.information.NoInformationAvailableException;

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
