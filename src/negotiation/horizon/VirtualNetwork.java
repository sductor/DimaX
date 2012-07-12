package negotiation.horizon;

import negotiation.horizon.contracts.HorizonContract;
import negotiation.horizon.negotiatingagent.VirtualNetworkCore;
import negotiation.horizon.negotiatingagent.VirtualNetworkIdentifier;
import negotiation.horizon.negotiatingagent.VirtualNetworkState;
import negotiation.negotiationframework.SimpleNegotiatingAgent;
import negotiation.negotiationframework.protocoles.AbstractCommunicationProtocol;
import negotiation.negotiationframework.protocoles.AbstractCommunicationProtocol.ProposerCore;
import negotiation.negotiationframework.protocoles.AbstractCommunicationProtocol.SelectionCore;
import dima.introspectionbasedagents.services.CompetenceException;
import dima.introspectionbasedagents.services.information.ObservationService;

/**
 * A VirtualNetwork is an agent representing a set of virtual nodes linked
 * together to form a network.
 * 
 * @author Vincent Letard
 */
public class VirtualNetwork extends
SimpleNegotiatingAgent<VirtualNetworkState, HorizonContract> {

	/**
	 * Serial version identifier.
	 */
	private static final long serialVersionUID = -6040992873742188247L;

	/**
	 * Instantiates a new VirtualNetwork.
	 * 
	 * @param id
	 *            Identifier of the agent.
	 * @param myInitialState
	 *            Initial AgentState.
	 * @param myRationality
	 *            The RationalCore.
	 * @param selectionCore
	 *            A generic SelectionCore.
	 * @param proposerCore
	 *            An adapted not yet implemented ProposerCore
	 * @param myInformation
	 *            "sensors" of the Agent
	 * @param protocol
	 *            An appropriate negotiation protocol (associated with the
	 *            ProposerCore)
	 * @throws CompetenceException
	 *             if an error occurred in the construction of the object.
	 */
	public VirtualNetwork(
			final VirtualNetworkIdentifier id,
			final VirtualNetworkState myInitialState,
			final VirtualNetworkCore myRationality,
			final SelectionCore<VirtualNetwork, VirtualNetworkState, HorizonContract> selectionCore,
			final ProposerCore<VirtualNetwork, VirtualNetworkState, HorizonContract> proposerCore,
			final ObservationService myInformation,
			final AbstractCommunicationProtocol<HorizonContract> protocol)
					throws CompetenceException {
		super(id, myInitialState, myRationality, selectionCore, proposerCore,
				myInformation, protocol);
	}

	/**
	 * Returns the identifier of this agent.
	 */
	@Override
	public VirtualNetworkIdentifier getIdentifier() {
		assert super.getIdentifier().getClass()
		.equals(VirtualNetworkIdentifier.class);
		return (VirtualNetworkIdentifier) super.getIdentifier();
	}
}
