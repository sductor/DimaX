package negotiation.horizon.experimentation;

import negotiation.horizon.negociatingagent.HorizonContract;
import negotiation.horizon.negociatingagent.HorizonSpecification;
import negotiation.horizon.negociatingagent.SubstrateNodeIdentifier;
import negotiation.horizon.negociatingagent.SubstrateNodeState;
import negotiation.negotiationframework.SimpleNegotiatingAgent;
import negotiation.negotiationframework.protocoles.AbstractCommunicationProtocol;
import negotiation.negotiationframework.protocoles.AbstractCommunicationProtocol.ProposerCore;
import negotiation.negotiationframework.protocoles.AbstractCommunicationProtocol.SelectionCore;
import negotiation.negotiationframework.rationality.RationalCore;
import dima.introspectionbasedagents.services.CompetenceException;
import dima.introspectionbasedagents.services.information.ObservationService;

/**
 * A SubstrateNode is an agent representing a physical computer, able to
 * virtualize some virtual nodes.
 * 
 * @author Vincent Letard
 */
public class SubstrateNode
	extends
	SimpleNegotiatingAgent<HorizonSpecification, SubstrateNodeState, HorizonContract> {

    /**
     * Serial version identifier.
     */
    private static final long serialVersionUID = -9069889310850887134L;

    /**
     * Instantiates a new SubstrateNode.
     * 
     * @param id
     * @param myInitialState
     * @param myRationality
     * @param selectionCore
     * @param myInformation
     * @throws CompetenceException
     */
    public SubstrateNode(
	    final SubstrateNodeIdentifier id,
	    final SubstrateNodeState myInitialState,
	    final RationalCore<HorizonSpecification, SubstrateNodeState, HorizonContract> myRationality,
	    final SelectionCore<SubstrateNode, HorizonSpecification, SubstrateNodeState, HorizonContract> selectionCore,
	    final ProposerCore<SubstrateNode, HorizonSpecification, SubstrateNodeState, HorizonContract> proposerCore,
	    final ObservationService myInformation,
	    final AbstractCommunicationProtocol<HorizonSpecification, SubstrateNodeState, HorizonContract> protocol)
	    throws CompetenceException {
	super(id, myInitialState, myRationality, selectionCore, proposerCore,
		myInformation, protocol);
    }

    @Override
    public SubstrateNodeIdentifier getIdentifier() {
	assert (super.getIdentifier().getClass()
		.equals(SubstrateNodeIdentifier.class));
	return (SubstrateNodeIdentifier) super.getIdentifier();
    }
}
