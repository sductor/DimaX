package negotiation.negotiationframework.proposercores.collaborative;

import negotiation.faulttolerance.negotiatingagent.HostState;
import negotiation.negotiationframework.contracts.AbstractActionSpecification;
import negotiation.negotiationframework.contracts.AbstractContractTransition;
import negotiation.negotiationframework.contracts.ContractTrunk;
import dima.basiccommunicationcomponents.Message;

public class IllAnswer<State extends AbstractActionSpecification, Contract extends AbstractContractTransition<?>> extends Message {
	private static final long serialVersionUID = -6005898857508683984L;

	private final ContractTrunk<Contract> answers;
	private final State agentState;

	public IllAnswer(ContractTrunk<Contract> answers, State agentState) {
		super();
		this.answers = answers;
		this.agentState = agentState;
	}

	public ContractTrunk<Contract> getAnswers() {
		return this.answers;
	}


	public State getAgentState() {
		return agentState;
	}


}
