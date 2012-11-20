package frameworks.negotiation;

import frameworks.negotiation.contracts.AbstractContractTransition;
import frameworks.negotiation.protocoles.AbstractCommunicationProtocol;
import frameworks.negotiation.protocoles.AbstractCommunicationProtocol.ProposerCore;
import frameworks.negotiation.protocoles.AbstractCommunicationProtocol.SelectionCore;
import frameworks.negotiation.rationality.AgentState;
import frameworks.negotiation.rationality.RationalAgent;

public interface NegotiatingAgent<
PersonalState extends AgentState,
Contract extends AbstractContractTransition>
extends RationalAgent<PersonalState, Contract> {

	public AbstractCommunicationProtocol<PersonalState,Contract> getMyProtocol();

	public ProposerCore<? extends NegotiatingAgent, PersonalState, Contract> getMyProposerCore() ;

	public SelectionCore<? extends NegotiatingAgent,PersonalState, Contract> getMySelectionCore();

	void setInformation(AgentState o);



	//	public abstract Contract generateDestructionContract(final AgentIdentifier id);

	//	public abstract Contract generateCreationContract(final AgentIdentifier id);

}
