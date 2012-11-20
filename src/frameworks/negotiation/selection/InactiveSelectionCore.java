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

	/**
	 * 
	 */
	private static final long serialVersionUID = 4298182739752070762L;

	@Override
	public void select(final ContractTrunk<Contract> cs, final PersonalState currentState,
			final Collection<Contract> toAccept, final Collection<Contract> toReject,
			final Collection<Contract> toPutOnWait) {
		//do nothing

	}

}
