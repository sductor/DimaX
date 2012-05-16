package negotiation.horizon.negotiatingagent;

import negotiation.horizon.negotiatingagent.VirtualNetworkIdentifier.VirtualNodeIdentifier;
import negotiation.negotiationframework.contracts.MatchingCandidature;
import negotiation.negotiationframework.rationality.AgentState;
import dima.basicagentcomponents.AgentIdentifier;

public class HorizonCandidature extends
	MatchingCandidature<HorizonSpecification> {

    /**
     * Serial version identifier.
     */
    private static final long serialVersionUID = 5688344205746523199L;

    private VirtualNetworkState vnInitialState = null;
    private SubstrateNodeState snInitialState = null;

    public HorizonCandidature(final VirtualNetworkIdentifier intiator,
	    final VirtualNetworkIdentifier agent,
	    final SubstrateNodeIdentifier resource, final long validityTime) {
	super(intiator, agent, resource, validityTime);
    }

    private VirtualNodeIdentifier getNode() throws IncompleteContractException {
	return this.getSpecificationOf(this.getAgent()).getNode();
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

    public VirtualNetworkSpecification getSpecificationOf(
	    VirtualNetworkIdentifier id) throws IncompleteContractException {
	return (VirtualNetworkSpecification) super.getSpecificationOf(id);
    }

    public SubstrateNodeSpecification getSpecificationOf(
	    SubstrateNodeIdentifier id) throws IncompleteContractException {
	return (SubstrateNodeSpecification) super.getSpecificationOf(id);
    }

    @Override
    public AgentState computeResultingState(AgentIdentifier id)
	    throws IncompleteContractException {
	return this.computeResultingState(this.getInitialState(id));
    }

    // public SubstrateNodeState computeResultingState(final SubstrateNodeState
    // s)
    // throws IncompleteContractException {
    // if (s.getMyAgentIdentifier().equals(this.getResource()))
    // return new SubstrateNodeState(s, this.getAgent(), this
    // .getSpecificationOf(this.getAgent()).getNodeParams(
    // this.node), this.isMatchingCreation());
    // else
    // return s;
    // }
    //
    // public VirtualNetworkState computeResultingState(final
    // VirtualNetworkState s) {
    // if (this.getAllParticipants().contains(s.getMyAgentIdentifier())) {
    // return new VirtualNetworkState(s, this.node, this.getResource());
    // } else
    // return s;
    // }

    @Override
    public <State extends AgentState> State computeResultingState(State s)
	    throws IncompleteContractException {
	if (s instanceof SubstrateNodeState) {
	    if (s.getMyAgentIdentifier().equals(this.getResource()))
		return (State) new SubstrateNodeState((SubstrateNodeState) s,
			this.getAgent(), this.getInitialState(this.getAgent())
				.getNodeParams(this.getNode())
				.getAllocableParams(), this
				.isMatchingCreation());
	    else
		return s;
	} else if (s instanceof VirtualNetworkState) {
	    if (this.getAllParticipants().contains(s.getMyAgentIdentifier())) {
		final VirtualNodeIdentifier node = this.getNode();
		return (State) new VirtualNetworkState((VirtualNetworkState) s,
			node, this.getResource(), this.getInitialState(
				this.getAgent()).getNodeParams(node));
	    } else
		return s;
	} else
	    throw new IllegalArgumentException();
    }

    @Override
    public <State extends AgentState> State getInitialState(AgentIdentifier id)
	    throws negotiation.negotiationframework.contracts.AbstractContractTransition.IncompleteContractException {
	throw new IllegalArgumentException();
    }

    public VirtualNetworkState getInitialState(final VirtualNetworkIdentifier id) {
	return this.vnInitialState;
    }

    public SubstrateNodeState getInitialState(final SubstrateNodeIdentifier id) {
	return this.snInitialState;
    }
}
