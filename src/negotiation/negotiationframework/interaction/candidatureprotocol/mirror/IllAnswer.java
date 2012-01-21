package negotiation.negotiationframework.interaction.candidatureprotocol.mirror;

import negotiation.negotiationframework.interaction.AbstractContractTransition;
import negotiation.negotiationframework.interaction.consensualnegotiation.ContractTrunk;
import dima.basiccommunicationcomponents.Message;

public class IllAnswer<Contract extends AbstractContractTransition<?>> extends Message {
	private static final long serialVersionUID = -6005898857508683984L;

	private final ContractTrunk<Contract> answers;

	public IllAnswer(final ContractTrunk<Contract> answers) {
		super();
		this.answers = answers;
	}


	public ContractTrunk<Contract> getAnswers() {
		return this.answers;
	}
}
