package negotiation.negotiationframework.strategy;

import negotiation.negotiationframework.agent.AgentState;
import negotiation.negotiationframework.information.NoInformationAvailableException;
import negotiation.negotiationframework.interaction.allocation.ContractTransition;
import dima.basicinterfaces.DimaComponentInterface;

public interface StrategicCore
<Contract extends ContractTransition<?>,
InformedState extends AgentState> 
extends DimaComponentInterface{
	
	public int strategiclyCompare(Contract c1, Contract c2) throws NoInformationAvailableException;	
	
}





//	public int compareAcceptationConfidence(Contract c1, Contract c2) throws MissingInformationException;
//	
//	public int compareContractUtility(Contract c1, Contract c2) throws MissingInformationException;