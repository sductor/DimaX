package frameworks.horizon.contracts;

import jtp.util.UnexpectedException;
import dima.basicagentcomponents.AgentIdentifier;
import frameworks.horizon.negotiatingagent.SubstrateNodeIdentifier;
import frameworks.horizon.negotiatingagent.SubstrateNodeState;
import frameworks.horizon.negotiatingagent.VirtualNetworkIdentifier;
import frameworks.horizon.negotiatingagent.VirtualNetworkIdentifier.VirtualNodeIdentifier;
import frameworks.horizon.negotiatingagent.VirtualNetworkState;
import frameworks.horizon.parameters.HorizonMeasurableParameters;
import frameworks.negotiation.contracts.AbstractContractTransition;
import frameworks.negotiation.contracts.MatchingCandidature;
import frameworks.negotiation.rationality.AgentState;

/**
 * This class represents a allocation candidature between a VirtualNode and a
 * SubstrateNode.
 * 
 * @author Vincent Letard
 */
public class HorizonCandidature extends MatchingCandidature {

	/**
	 * Serial version identifier.
	 */
	private static final long serialVersionUID = 5688344205746523199L;

	/**
	 * Initial state of the VirtualNetwork.
	 */
	private final VirtualNetworkState vnInitialState = null;
	/**
	 * Initial state of the SubstrateNode.
	 */
	private final SubstrateNodeState snInitialState = null;

	/**
	 * Constructs a new candidature between the specified VirtualNode of the
	 * VirtualNetwork and the SubstrateNode resource.
	 * 
	 * @param intiator
	 *            Agent proposing this candidature.
	 * @param agent
	 *            VirtualNetwork containing the concerned VirtualNode
	 * @param node
	 *            The VirtualNode proposed
	 * @param resource
	 *            The SubstrateNode requested
	 * @param validityTime
	 *            An expiration value
	 */
	public HorizonCandidature(final VirtualNetworkIdentifier intiator,
			final VirtualNetworkIdentifier agent,
			final VirtualNodeIdentifier node,
			final SubstrateNodeIdentifier resource, final long validityTime) {
		super(intiator, agent, resource, validityTime);
		this.setSpecification(new VirtualNetworkSpecification(node));
	}

	/**
	 * Gives the virtual node proposed in this candidature.
	 * 
	 * @return the identifier of the virtual node.
	 */
	public VirtualNodeIdentifier getNode() {
		try {
			return this.getSpecificationOf(this.getAgent()).getNode();
		} catch (final IncompleteContractException e) {
			// The agent must have filled its specification.
			throw new UnexpectedException(e);
		}
	}

	/**
	 * Gets the actual level of service provided by the resource.
	 * 
	 * @return the parameters measured at the substrate node.
	 * @throws IncompleteContractException
	 *             if these parameters are not set.
	 */
	private HorizonMeasurableParameters<SubstrateNodeIdentifier> getQoS()
			throws IncompleteContractException {
		return this.getSpecificationOf(this.getResource()).getParams();
	}

	/**
	 * Gives the initiator agent of this candidature.
	 */
	@Override
	public VirtualNetworkIdentifier getInitiator() {
		return (VirtualNetworkIdentifier) super.getInitiator();
	}

	/**
	 * Gives the agent of this candidature (the network containing the actor
	 * node).
	 */
	@Override
	public VirtualNetworkIdentifier getAgent() {
		return (VirtualNetworkIdentifier) super.getAgent();
	}

	/**
	 * Gives the resource requested in this candidature.
	 */
	@Override
	public SubstrateNodeIdentifier getResource() {
		return (SubstrateNodeIdentifier) super.getResource();
	}

	/**
	 * Gets the action specification of the specified VirtualNetwork.
	 * 
	 * @param id
	 *            The requested VirtualNetwork.
	 * @return The action specification of the VirtualNetwork in this
	 *         candidature.
	 * @throws IncompleteContractException
	 *             if the specification is not set.
	 */
	public VirtualNetworkSpecification getSpecificationOf(
			final VirtualNetworkIdentifier id) throws IncompleteContractException {
		return (VirtualNetworkSpecification) super.getSpecificationOf(id);
	}

	/**
	 * Gets the action specification of the specified resource, should not be
	 * called if the resource does not take in the candidature.
	 * 
	 * @param id
	 *            The requested SubstrateNode.
	 * @return The action specification of the SubstrateNode in this
	 *         candidature.
	 * @throws IncompleteContractException
	 *             if the specification is not set.
	 */
	public SubstrateNodeSpecification getSpecificationOf(
			final SubstrateNodeIdentifier id) throws IncompleteContractException {
		return (SubstrateNodeSpecification) super.getSpecificationOf(id);
	}

	/**
	 * Returns the AgentState of the Agent id resulting after an application of
	 * this candidature, assuming the initial state is the one specified in the
	 * data of this object.
	 */
	@Override
	public AgentState computeResultingState(final AgentIdentifier id)
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

	/**
	 * Computes the resulting state after an application of this candidature
	 * considering s as the initial state.
	 */
	@Override
	public <State extends AgentState> State computeResultingState(final State s)
			throws IncompleteContractException {
		if (s instanceof SubstrateNodeState) {
			if (s.getMyAgentIdentifier().equals(this.getResource())) {
				// That must be done in the class HorizonContract, impossible
				// here because of a lack of information
				throw new UnsupportedOperationException();
			} else {
				return s;
			}
		} else if (s instanceof VirtualNetworkState) {
			if (this.getAllParticipants().contains(s.getMyAgentIdentifier())) {
				final VirtualNodeIdentifier node = this.getNode();
				return (State) new VirtualNetworkState((VirtualNetworkState) s,
						node, this.getResource(), this.getQoS());
			} else {
				return s;
			}
		} else {
			throw new IllegalArgumentException();
		}
	}

	/**
	 * This function throws an IllegalArgumentException since the overriding
	 * functions handle the good argument types.
	 */
	@Override
	public AgentState getInitialState(final AgentIdentifier id)
			throws frameworks.negotiation.contracts.AbstractContractTransition.IncompleteContractException {
		throw new IllegalArgumentException();
	}

	/**
	 * Gives the initial VirtualNetworkState for the specified VirtualNetwork.
	 * 
	 * @param id
	 *            The requested VirtualNetwork
	 * @return the initial state of that VirtualNetwork
	 */
	public VirtualNetworkState getInitialState(final VirtualNetworkIdentifier id) {
		return this.vnInitialState;
	}

	/**
	 * Gives the initial SubstrateNodeState for the specified SubstrateNode.
	 * 
	 * @param id
	 *            The requested SubstrateNode
	 * @return the initial state of that SubstrateNode
	 */
	public SubstrateNodeState getInitialState(final SubstrateNodeIdentifier id) {
		return this.snInitialState;
	}

	@Override
	public AbstractContractTransition clone() {
		return this;
	}
}
