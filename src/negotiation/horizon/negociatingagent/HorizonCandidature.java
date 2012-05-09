package negotiation.horizon.negociatingagent;

import negotiation.horizon.negociatingagent.VirtualNetworkState.VirtualNodeIdentifier;
import negotiation.negotiationframework.contracts.MatchingCandidature;
import negotiation.negotiationframework.rationality.AgentState;
import dima.basicagentcomponents.AgentIdentifier;

public class HorizonCandidature extends
	MatchingCandidature<SingleNodeParameters> {

    /**
     * Serial version identifier.
     */
    private static final long serialVersionUID = 5688344205746523199L;

    /**
     * @uml.property name="node"
     * @uml.associationEnd
     */
    private final VirtualNodeIdentifier node;

    public HorizonCandidature(final VirtualNetworkIdentifier intiator,
	    final VirtualNetworkIdentifier agent,
	    final VirtualNodeIdentifier node,
	    final SubstrateNodeIdentifier resource, final long validityTime) {
	super(intiator, agent, resource, validityTime);
	this.node = node;
    }

    /**
     * @return
     * @uml.property name="node"
     */
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

    // public SingleNodeParameters getSpecificationOf(
    // final VirtualNetworkIdentifier id)
    // throws IncompleteContractException {
    // return super.getSpecificationOf(id);
    // }

    // public SubstrateNodeState getSpecificationOf(
    // final SubstrateNodeIdentifier id)
    // throws IncompleteContractException {
    // return (SubstrateNodeState) super.getSpecificationOf(id);
    // }

    @Override
    public AgentState computeResultingState(AgentIdentifier id)
	    throws IncompleteContractException {
	return this.computeResultingState(this.getSpecificationOf(id));
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
			this.getAgent(), this.getSpecificationOf(this
				.getResource()), this.isMatchingCreation());
	    else
		return s;
	} else if (s instanceof VirtualNetworkState) {
	    if (this.getAllParticipants().contains(s.getMyAgentIdentifier())) {
		return (State) new VirtualNetworkState((VirtualNetworkState) s,
			this.node, this.getResource(), this
				.getSpecificationOf(this.getAgent()));
	    } else
		return s;
	} else
	    throw new IllegalArgumentException();
    }
}
