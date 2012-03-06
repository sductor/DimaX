package negotiation.horizon.negociatingagent;

import negotiation.negotiationframework.SimpleNegotiatingAgent;
import negotiation.negotiationframework.AbstractCommunicationProtocol.ProposerCore;
import negotiation.negotiationframework.contracts.ContractTrunk;
import negotiation.negotiationframework.rationality.RationalCore;
import negotiation.negotiationframework.selectioncores.AbstractSelectionCore;
import dima.basicagentcomponents.AgentIdentifier;
import dima.introspectionbasedagents.services.CompetenceException;
import dima.introspectionbasedagents.services.information.ObservationService;

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
	    ProposerCore<? extends SimpleNegotiatingAgent<HorizonSpecification, VirtualNetworkState, HorizonContract>, HorizonSpecification, VirtualNetworkState, HorizonContract> proposerCore,
	    ObservationService myInformation) throws CompetenceException {
	super(id, myInitialState, myRationality, selectionCore, proposerCore,
		myInformation,
		new ContractTrunk<HorizonContract, HorizonSpecification, VirtualNetworkState>());
	// TODO Auto-generated constructor stub
    }

}
