package negotiation.horizon.negociatingagent;

import negotiation.negotiationframework.SimpleNegotiatingAgent;
import negotiation.negotiationframework.agent.RationalCore;
import negotiation.negotiationframework.interaction.consensualnegotiation.AbstractProposerCore;
import negotiation.negotiationframework.interaction.selectioncores.AbstractSelectionCore;
import dima.basicagentcomponents.AgentIdentifier;
import dima.introspectionbasedagents.services.CompetenceException;
import dima.introspectionbasedagents.services.library.information.ObservationService;

public class VirtualNetwork extends
		SimpleNegotiatingAgent<HorizonSpecification, VirtualNetworkState, HorizonContract> {
	/**
	 * 
	 */
	private static final long serialVersionUID = -6040992873742188247L;

	public VirtualNetwork(
			AgentIdentifier id,
			VirtualNetworkState myInitialState,
			RationalCore<HorizonSpecification, VirtualNetworkState, HorizonContract> myRationality,
			AbstractSelectionCore<HorizonSpecification, VirtualNetworkState, HorizonContract> selectionCore,
			AbstractProposerCore<? extends SimpleNegotiatingAgent, HorizonSpecification, VirtualNetworkState, HorizonContract> proposerCore,
			ObservationService myInformation) throws CompetenceException {
		super(id, myInitialState, myRationality, selectionCore, proposerCore,
				myInformation);
		// TODO Auto-generated constructor stub
	}

}
