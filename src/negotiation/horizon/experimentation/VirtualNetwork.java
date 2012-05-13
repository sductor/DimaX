package negotiation.horizon.experimentation;

import negotiation.horizon.negotiatingagent.HorizonCandidature;
import negotiation.horizon.negotiatingagent.HorizonParameters;
import negotiation.horizon.negotiatingagent.VirtualNetworkIdentifier;
import negotiation.horizon.negotiatingagent.VirtualNetworkState;
import negotiation.negotiationframework.SimpleNegotiatingAgent;
import negotiation.negotiationframework.contracts.ReallocationContract;
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
	SimpleNegotiatingAgent<HorizonParameters<VirtualNetworkIdentifier>, VirtualNetworkState, ReallocationContract<HorizonCandidature, HorizonParameters<VirtualNetworkIdentifier>>> {

    /**
     * Serial version identifier.
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
	    final RationalCore<HorizonParameters<VirtualNetworkIdentifier>, VirtualNetworkState, ReallocationContract<HorizonCandidature, HorizonParameters>> myRationality,
	    final SelectionCore<VirtualNetwork, HorizonParameters<VirtualNetworkIdentifier>, VirtualNetworkState, ReallocationContract<HorizonCandidature, HorizonParameters>> selectionCore,
	    final ProposerCore<VirtualNetwork, HorizonParameters<VirtualNetworkIdentifier>, VirtualNetworkState, ReallocationContract<HorizonCandidature, HorizonParameters>> proposerCore,
	    final ObservationService myInformation,
	    final AbstractCommunicationProtocol<HorizonParameters<VirtualNetworkIdentifier>, VirtualNetworkState, ReallocationContract<HorizonCandidature, HorizonParameters>> protocol)
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
