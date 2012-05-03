package negotiation.horizon.negociatingagent;

import negotiation.horizon.negociatingagent.VirtualNetworkState.VirtualNodeIdentifier;
import negotiation.negotiationframework.contracts.MatchingCandidature;
import dima.basicagentcomponents.AgentIdentifier;

public class HorizonCandidature extends
	MatchingCandidature<HorizonSpecification> {

    /**
     * Serial version iiddentifier.
     */
    private static final long serialVersionUID = 5688344205746523199L;

    private final VirtualNodeIdentifier node;

    public HorizonCandidature(final VirtualNetworkIdentifier intiator,
	    final VirtualNetworkIdentifier agent,
	    final VirtualNodeIdentifier node,
	    final SubstrateNodeIdentifier resource, final long validityTime) {
	super(intiator, agent, resource, validityTime);
	this.node = node;
    }

    public VirtualNodeIdentifier getNode() {
	return this.node;
    }

    @Override
    public VirtualNetworkIdentifier getInitiator() {
	return (VirtualNetworkIdentifier) super.getInitiator();
    }

    @Override
    public VirtualNetworkIdentifier getAgent() {
	return (VirtualNetworkIdentifier) super.getAgent();
    }

    @Override
    public SubstrateNodeIdentifier getResource() {
	return (SubstrateNodeIdentifier) super.getResource();
    }

    public VirtualNetworkState getSpecificationOf(
	    final VirtualNetworkIdentifier id)
	    throws IncompleteContractException {
	return (VirtualNetworkState) super.getSpecificationOf(id);
    }

    public SubstrateNodeState getSpecificationOf(
	    final SubstrateNodeIdentifier id)
	    throws IncompleteContractException {
	return (SubstrateNodeState) super.getSpecificationOf(id);
    }

    @Override
    public HorizonSpecification computeResultingState(AgentIdentifier id)
	    throws IncompleteContractException {
	return this.computeResultingState(this.getSpecificationOf(id));
    }

    public SubstrateNodeState computeResultingState(final SubstrateNodeState s)
	    throws IncompleteContractException {
	if (s.getMyAgentIdentifier().equals(this.getResource()))
	    return new SubstrateNodeState(s, this.getAgent(), this
		    .getSpecificationOf(this.getAgent()).getNodeParams(
			    this.node), this.isMatchingCreation());
	else
	    return s;
    }

    public VirtualNetworkState computeResultingState(final VirtualNetworkState s) {
	if (this.getAllParticipants().contains(s.getMyAgentIdentifier())) {
	    return new VirtualNetworkState(s, this.node, this.getResource());
	} else
	    return s;
    }

    @Override
    public <State extends HorizonSpecification> State computeResultingState(
	    State s) throws IncompleteContractException {
	throw new RuntimeException("Unexpected behavior");
    }
}
