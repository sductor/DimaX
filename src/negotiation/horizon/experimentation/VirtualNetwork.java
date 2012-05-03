package negotiation.horizon.experimentation;

import negotiation.horizon.negociatingagent.HorizonContract;
import negotiation.horizon.negociatingagent.HorizonSpecification;
import negotiation.horizon.negociatingagent.VirtualNetworkIdentifier;
import negotiation.horizon.negociatingagent.VirtualNetworkState;
import negotiation.negotiationframework.SimpleNegotiatingAgent;
import negotiation.negotiationframework.protocoles.AbstractCommunicationProtocol;
import negotiation.negotiationframework.protocoles.AbstractCommunicationProtocol.ProposerCore;
import negotiation.negotiationframework.protocoles.AbstractCommunicationProtocol.SelectionCore;
import negotiation.negotiationframework.rationality.RationalCore;
import dima.introspectionbasedagents.services.CompetenceException;
import dima.introspectionbasedagents.services.information.ObservationService;

/**
 * A VirtualNetwork is an agent representing a set of virtual nodes linked
 * together to form a network.
 * 
 * @author Vincent Letard
 */
public class VirtualNetwork
	extends
	SimpleNegotiatingAgent<HorizonSpecification, VirtualNetworkState, HorizonContract> {
    /**
     * 
     */
    private static final long serialVersionUID = -6040992873742188247L;

    /**
     * Instantiates a new VirtualNetwork.
     * 
     * @param id
     * @param myInitialState
     * @param myRationality
     * @param selectionCore
     * @param proposerCore
     * @param myInformation
     * @param protocol
     * @throws CompetenceException
     */
    public VirtualNetwork(
	    final VirtualNetworkIdentifier id,
	    final VirtualNetworkState myInitialState,
	    final RationalCore<HorizonSpecification, VirtualNetworkState, HorizonContract> myRationality,
	    final SelectionCore<VirtualNetwork, HorizonSpecification, VirtualNetworkState, HorizonContract> selectionCore,
	    final ProposerCore<VirtualNetwork, HorizonSpecification, VirtualNetworkState, HorizonContract> proposerCore,
	    final ObservationService myInformation,
	    final AbstractCommunicationProtocol<HorizonSpecification, VirtualNetworkState, HorizonContract> protocol)
	    throws CompetenceException {
	super(id, myInitialState, myRationality, selectionCore, proposerCore,
		myInformation, protocol);
    }

    @Override
    public VirtualNetworkIdentifier getIdentifier() {
	assert (super.getIdentifier().getClass()
		.equals(VirtualNetworkIdentifier.class));
	return (VirtualNetworkIdentifier) super.getIdentifier();
    }
}
