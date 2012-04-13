package negotiation.negotiationframework.protocoles.collaborative;

import dima.basicagentcomponents.AgentIdentifier;
import dima.introspectionbasedagents.services.CompetenceException;
import dima.introspectionbasedagents.services.information.ObservationService;
import negotiation.negotiationframework.SimpleNegotiatingAgent;
import negotiation.negotiationframework.contracts.AbstractActionSpecification;
import negotiation.negotiationframework.contracts.AbstractContractTransition;
import negotiation.negotiationframework.protocoles.AbstractCommunicationProtocol;
import negotiation.negotiationframework.protocoles.AbstractCommunicationProtocol.ProposerCore;
import negotiation.negotiationframework.protocoles.AbstractCommunicationProtocol.SelectionCore;
import negotiation.negotiationframework.rationality.RationalCore;

public class CollaborativeAgent<
ActionSpec extends AbstractActionSpecification,
PersonalState extends ActionSpec,
Contract extends AbstractContractTransition<ActionSpec>>
extends SimpleNegotiatingAgent<ActionSpec, PersonalState, Contract>  {

	public final int proposalComplexity = 1; //0 no host proposal, 1 host proposal, 2 agent fill the possible and provide requeste
	public final CandidatureRootTable<Contract, ActionSpec> crt;
	
	public CollaborativeAgent(
			AgentIdentifier id,
			PersonalState myInitialState,
			RationalCore<ActionSpec, PersonalState, Contract> myRationality,
			SelectionCore<ActionSpec, PersonalState, Contract> selectionCore,
			ProposerCore<? extends SimpleNegotiatingAgent, ActionSpec, PersonalState, Contract> proposerCore,
			ObservationService myInformation,
			AbstractCommunicationProtocol<ActionSpec, PersonalState, Contract> protocol)
			throws CompetenceException {
		super(id, myInitialState, myRationality, selectionCore, proposerCore,
				myInformation, protocol);
		}

}
