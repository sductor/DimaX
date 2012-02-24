package negotiation.negotiationframework.interaction.candidatureprotocol.mirror;

import negotiation.faulttolerance.negotiatingagent.HostState;
import negotiation.negotiationframework.interaction.consensualnegotiation.ContractDataBase;
import negotiation.negotiationframework.interaction.contracts.AbstractActionSpecification;
import negotiation.negotiationframework.interaction.contracts.AbstractContractTransition;
import dima.basiccommunicationcomponents.Message;

public class IllAnswer<State extends AbstractActionSpecification, Contract extends AbstractContractTransition<?>> extends Message {
	private static final long serialVersionUID = -6005898857508683984L;

	private final ContractDataBase<Contract> answers;
	private final State agentState;

	public IllAnswer(ContractDataBase<Contract> answers, State agentState) {
		super();
		this.answers = answers;
		this.agentState = agentState;
	}

	public ContractDataBase<Contract> getAnswers() {
		return this.answers;
	}


	public State getAgentState() {
		return agentState;
	}


}
