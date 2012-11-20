package frameworks.negotiation.selection;

import java.util.Collection;

import dima.introspectionbasedagents.services.AgentModule;
import frameworks.negotiation.NegotiatingAgent;
import frameworks.negotiation.contracts.AbstractContractTransition;
import frameworks.negotiation.rationality.AgentState;

public interface SelectionModule<
Agent extends NegotiatingAgent<PersonalState, Contract>,
PersonalState extends AgentState,
Contract extends AbstractContractTransition>
extends AgentModule<Agent>{

	public abstract Collection<Contract> selection(PersonalState currentState,
			final Collection<Contract> contractsToExplore);

}