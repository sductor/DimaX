package frameworks.negotiation.negotiationframework;

import frameworks.negotiation.negotiationframework.contracts.AbstractContractTransition;
import frameworks.negotiation.negotiationframework.protocoles.AbstractCommunicationProtocol;
import frameworks.negotiation.negotiationframework.protocoles.AbstractCommunicationProtocol.ProposerCore;
import frameworks.negotiation.negotiationframework.protocoles.AbstractCommunicationProtocol.SelectionCore;
import frameworks.negotiation.negotiationframework.rationality.AgentState;
import frameworks.negotiation.negotiationframework.rationality.RationalAgent;

public interface NegotiatingAgent<
PersonalState extends AgentState,
Contract extends AbstractContractTransition>
extends RationalAgent<PersonalState, Contract> {

	public AbstractCommunicationProtocol<Contract> getMyProtocol();

	public ProposerCore<? extends NegotiatingAgent, PersonalState, Contract> getMyProposerCore() ;

	public SelectionCore<? extends NegotiatingAgent,PersonalState, Contract> getMySelectionCore();
}
