package frameworks.negotiation.negotiationframework.selection;

import java.util.Collection;

import dima.introspectionbasedagents.services.AgentModule;

import frameworks.negotiation.negotiationframework.NegotiatingAgent;
import frameworks.negotiation.negotiationframework.contracts.AbstractContractTransition;
import frameworks.negotiation.negotiationframework.rationality.AgentState;

public interface SelectionModule<
Agent extends NegotiatingAgent<PersonalState, Contract>,
PersonalState extends AgentState, 
Contract extends AbstractContractTransition>
extends AgentModule<Agent>{

	public abstract Collection<Contract> selection(PersonalState currentState,
			final Collection<Contract> contractsToExplore);

}