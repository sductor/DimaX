package negotiation.negotiationframework;

import negotiation.negotiationframework.contracts.AbstractContractTransition;
import negotiation.negotiationframework.protocoles.AbstractCommunicationProtocol;
import negotiation.negotiationframework.protocoles.AbstractCommunicationProtocol.ProposerCore;
import negotiation.negotiationframework.protocoles.AbstractCommunicationProtocol.SelectionCore;
import negotiation.negotiationframework.rationality.AgentState;
import negotiation.negotiationframework.rationality.RationalAgent;

public interface NegotiatingAgent<
PersonalState extends AgentState,
Contract extends AbstractContractTransition>
extends RationalAgent<PersonalState, Contract> {
	public AbstractCommunicationProtocol<Contract> getMyProtocol();

	public ProposerCore<? extends SimpleNegotiatingAgent, PersonalState, Contract> getMyProposerCore() ;

	public SelectionCore<? extends SimpleNegotiatingAgent,PersonalState, Contract> getMySelectionCore();
}
