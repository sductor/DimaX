package negotiation.negotiationframework.protocoles.collaborative;

import dima.basicagentcomponents.AgentIdentifier;
import dima.introspectionbasedagents.services.CompetenceException;
import dima.introspectionbasedagents.services.information.ObservationService;
import negotiation.negotiationframework.SimpleNegotiatingAgent;
import negotiation.negotiationframework.contracts.AbstractActionSpecification;
import negotiation.negotiationframework.contracts.AbstractContractTransition;
import negotiation.negotiationframework.contracts.MatchingCandidature;
import negotiation.negotiationframework.protocoles.AbstractCommunicationProtocol;
import negotiation.negotiationframework.protocoles.AbstractCommunicationProtocol.ProposerCore;
import negotiation.negotiationframework.protocoles.AbstractCommunicationProtocol.SelectionCore;
import negotiation.negotiationframework.rationality.RationalCore;

public abstract class CollaborativeAgent<
ActionSpec extends AbstractActionSpecification,
PersonalState extends ActionSpec,
Contract extends MatchingCandidature<ActionSpec>>
extends SimpleNegotiatingAgent<ActionSpec, PersonalState, Contract>  {

	private final int proposalComplexity = 1; //0 no host proposal, 1 host proposal, 2 agent fill the possible and provide requeste
	private final CandidatureRootTable<Contract, ActionSpec> crt = 
			new CandidatureRootTable<Contract, ActionSpec>();
	
	public CollaborativeAgent(
			AgentIdentifier id,
			PersonalState myInitialState,
			RationalCore<ActionSpec, PersonalState, Contract> myRationality,
			SelectionCore<? extends SimpleNegotiatingAgent,ActionSpec, PersonalState, Contract> selectionCore,
			ProposerCore<? extends SimpleNegotiatingAgent, ActionSpec, PersonalState, Contract> proposerCore,
			ObservationService myInformation,
			AbstractCommunicationProtocol<ActionSpec, PersonalState, Contract> protocol)
			throws CompetenceException {
		super(id, myInitialState, myRationality, selectionCore, proposerCore,
				myInformation, protocol);
		}

	public int getProposalComplexity() {
		return proposalComplexity;
	}
	
	public CandidatureRootTable<Contract, ActionSpec> getCrt() {
		return crt;
	}
}
