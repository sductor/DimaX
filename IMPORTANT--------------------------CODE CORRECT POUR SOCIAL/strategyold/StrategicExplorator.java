package negotiation.negotiationframework.strategy;

import java.util.Collection;
import java.util.Iterator;

import negotiation.negotiationframework.agent.AgentState;
import negotiation.negotiationframework.information.NoInformationAvailableException;
import negotiation.negotiationframework.interaction.allocation.ActionSpecification;
import negotiation.negotiationframework.interaction.allocation.ContractTransition;
import dima.basicagentcomponents.AgentIdentifier;
import dima.basicinterfaces.DimaComponentInterface;

public interface StrategicExplorator<
ActionIdentifier extends ActionSpecification,
Contract extends ContractTransition<ActionIdentifier>,
InformedState extends AgentState> {


	public interface ContractNeighborhood<
	ActionIdentifier extends ActionSpecification,
	Contract extends ContractTransition<ActionIdentifier>> 
	extends DimaComponentInterface{
		
		public abstract Contract getEmptyContract();
		
		public abstract Contract getRandomContract(Collection<AgentIdentifier> knownAgents, Collection<ActionIdentifier> knownActions);
		
		public abstract Iterator<AgentAction<ActionIdentifier>> 
		getNeighbors(Contract c, Collection<AgentIdentifier> knownAgents, Collection<ActionIdentifier> knownActions);

	}	
	
	
	public Contract getNextContractToPropose(
			StrategicCore<Contract, InformedState> myComparator,
			Collection<AgentIdentifier> knownAgents, 
			Collection<ActionIdentifier> knownActions) throws NoInformationAvailableException;
	

}
