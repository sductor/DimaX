package negotiation.horizon.negociatingagent;

import dima.basicagentcomponents.AgentIdentifier;
import dima.introspectionbasedagents.services.CompetenceException;
import dima.introspectionbasedagents.services.library.information.ObservationService;
import negotiation.negotiationframework.SimpleNegotiatingAgent;
import negotiation.negotiationframework.agent.RationalCore;
import negotiation.negotiationframework.interaction.consensualnegotiation.AbstractProposerCore;
import negotiation.negotiationframework.interaction.selectioncores.AbstractSelectionCore;

public class SubstrateNode extends
		SimpleNegotiatingAgent<HorizonSpecification, SubstrateNodeState, HorizonContract> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -9069889310850887134L;

	public SubstrateNode(
			AgentIdentifier id,
			SubstrateNodeState myInitialState,
			RationalCore<HorizonSpecification, SubstrateNodeState, HorizonContract> myRationality,
			AbstractSelectionCore<HorizonSpecification, SubstrateNodeState, HorizonContract> selectionCore,
			AbstractProposerCore<? extends SimpleNegotiatingAgent, HorizonSpecification, SubstrateNodeState, HorizonContract> proposerCore,
			ObservationService myInformation) throws CompetenceException {
		super(id, myInitialState, myRationality, selectionCore, proposerCore,
				myInformation);
		// TODO Auto-generated constructor stub
	}

}
