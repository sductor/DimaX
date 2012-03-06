package negotiation.horizon.negociatingagent;

import negotiation.negotiationframework.SimpleNegotiatingAgent;
import negotiation.negotiationframework.contracts.ContractTrunk;
import negotiation.negotiationframework.contracts.ResourceIdentifier;
import negotiation.negotiationframework.protocoles.InactiveProposerCore;
import negotiation.negotiationframework.rationality.RationalCore;
import negotiation.negotiationframework.selectioncores.AbstractSelectionCore;
import dima.introspectionbasedagents.services.CompetenceException;
import dima.introspectionbasedagents.services.information.ObservationService;

/**
 * A SubstrateNode is an agent representing a physical computer, able to
 * virtualize some virtual nodes.
 * 
 * @author Vincent Letard
 */
public class SubstrateNode extends
	SimpleNegotiatingAgent<HorizonSpecification, SubstrateNodeState, HorizonContract> {

    /**
     * Serial version identifier.
     */
    private static final long serialVersionUID = -9069889310850887134L;

    /**
     * Constructs a new SubstrateNode.
     * 
     * @param id
     * @param myInitialState
     * @param myRationality
     * @param selectionCore
     * @param myInformation
     * @throws CompetenceException
     */
    public SubstrateNode(
	    ResourceIdentifier id,
	    SubstrateNodeState myInitialState,
	    RationalCore<HorizonSpecification, SubstrateNodeState, HorizonContract> myRationality,
	    AbstractSelectionCore<HorizonSpecification, SubstrateNodeState, HorizonContract> selectionCore,
	    ObservationService myInformation) throws CompetenceException {
	super(
		id,
		myInitialState,
		myRationality,
		selectionCore,
		new InactiveProposerCore<HorizonSpecification, SubstrateNodeState, HorizonContract>(),
		myInformation, new ContractTrunk());
	// TODO Auto-generated constructor stub
    }

    @Override
    public ResourceIdentifier getIdentifier() {
	assert (super.getId() instanceof ResourceIdentifier);
	return (ResourceIdentifier) super.getIdentifier();
    }
}
