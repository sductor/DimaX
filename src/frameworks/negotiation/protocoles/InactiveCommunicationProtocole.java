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

	/**
	 * 
	 */
	private static final long serialVersionUID = 5400537485916147533L;

	public InactiveCommunicationProtocole()
			throws UnrespectedCompetenceSyntaxException {
		super(new ContractTrunk<Contract>());
	}

	@Override
	protected boolean ImAllowedToNegotiate(final ContractTrunk<Contract> contracts) {
		return false;
	}

	@Override
	protected void answer() {
		//do nothing
	}
	@Override
	protected void answerAccepted(final Collection<Contract> toAccept) {
		//do nothing
	}

	@Override
	protected void answerRejected(final Collection<Contract> toReject) {
		//do nothing
	}

	@Override
	protected void putOnWait(final Collection<Contract> toPutOnWait) {
		//do nothing
	}

}
