package negotiation.horizon.experimentation;

import negotiation.horizon.negotiatingagent.HorizonCandidature;
import negotiation.horizon.negotiatingagent.HorizonIdentifier;
import negotiation.horizon.negotiatingagent.HorizonParameters;
import negotiation.horizon.negotiatingagent.SubstrateNodeIdentifier;
import negotiation.horizon.negotiatingagent.SubstrateNodeState;
import negotiation.negotiationframework.SimpleNegotiatingAgent;
import negotiation.negotiationframework.contracts.ReallocationContract;
import negotiation.negotiationframework.protocoles.AbstractCommunicationProtocol;
import negotiation.negotiationframework.protocoles.AbstractCommunicationProtocol.ProposerCore;
import negotiation.negotiationframework.protocoles.AbstractCommunicationProtocol.SelectionCore;
import negotiation.negotiationframework.rationality.RationalCore;
import dima.introspectionbasedagents.annotations.Competence;
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
	SimpleNegotiatingAgent<HorizonParameters<HorizonIdentifier>, SubstrateNodeState, ReallocationContract<HorizonCandidature, HorizonParameters<HorizonIdentifier>>> {

    /**
     * Serial version identifier.
     */
    private static final long serialVersionUID = -9069889310850887134L;

    @Competence
    private final LinkHandler linkHandler;

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
	    final RationalCore<HorizonParameters<SubstrateNodeIdentifier>, SubstrateNodeState, ReallocationContract<HorizonCandidature, HorizonParameters<SubstrateNodeIdentifier>>> myRationality,
	    final SelectionCore<SubstrateNode, HorizonParameters<SubstrateNodeIdentifier>, SubstrateNodeState, ReallocationContract<HorizonCandidature, HorizonParameters<SubstrateNodeIdentifier>>> selectionCore,
	    final ProposerCore<SubstrateNode, HorizonParameters<SubstrateNodeIdentifier>, SubstrateNodeState, ReallocationContract<HorizonCandidature, HorizonParameters<SubstrateNodeIdentifier>>> proposerCore,
	    final ObservationService myInformation,
	    final AbstractCommunicationProtocol<HorizonParameters<SubstrateNodeIdentifier>, SubstrateNodeState, ReallocationContract<HorizonCandidature, HorizonParameters<SubstrateNodeIdentifier>>> protocol)
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
