package frameworks.negotiation.selection;

import java.util.Collection;

import dima.introspectionbasedagents.services.BasicAgentCompetence;
import frameworks.negotiation.NegotiatingAgent;
import frameworks.negotiation.contracts.AbstractContractTransition;
import frameworks.negotiation.contracts.ContractTrunk;
import frameworks.negotiation.protocoles.AbstractCommunicationProtocol.SelectionCore;
import frameworks.negotiation.rationality.AgentState;

public class InactiveSelectionCore<
Agent extends NegotiatingAgent<PersonalState, Contract>,
PersonalState extends AgentState,
Contract extends AbstractContractTransition>
extends	BasicAgentCompetence<NegotiatingAgent<PersonalState, Contract>>
implements SelectionCore<NegotiatingAgent<PersonalState,Contract>, PersonalState, Contract>{

	@Override
	public void select(ContractTrunk<Contract> cs, PersonalState currentState,
			Collection<Contract> toAccept, Collection<Contract> toReject,
			Collection<Contract> toPutOnWait) {
		//do nothing
		
	}

}
