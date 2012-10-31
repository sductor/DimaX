package frameworks.negotiation.protocoles;

import java.util.Collection;

import dima.introspectionbasedagents.services.UnrespectedCompetenceSyntaxException;
import frameworks.negotiation.contracts.AbstractContractTransition;
import frameworks.negotiation.contracts.ContractTrunk;
import frameworks.negotiation.rationality.AgentState;

public class InactiveCommunicationProtocole<
PersonalState extends AgentState,
Contract extends AbstractContractTransition>
extends AbstractCommunicationProtocol<PersonalState, Contract> {

	public InactiveCommunicationProtocole()
			throws UnrespectedCompetenceSyntaxException {
		super(new ContractTrunk<Contract>());
	}

	@Override
	protected boolean ImAllowedToNegotiate(ContractTrunk<Contract> contracts) {
		return false;
	}

	protected void answer() {
		//do nothing
	}
	@Override
	protected void answerAccepted(Collection<Contract> toAccept) {
		//do nothing
	}

	@Override
	protected void answerRejected(Collection<Contract> toReject) {
		//do nothing
	}

	@Override
	protected void putOnWait(Collection<Contract> toPutOnWait) {
		//do nothing
	}

}
