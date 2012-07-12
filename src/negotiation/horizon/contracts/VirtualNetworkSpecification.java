package negotiation.horizon.contracts;

import negotiation.horizon.negotiatingagent.VirtualNetworkIdentifier;
import negotiation.horizon.negotiatingagent.VirtualNetworkIdentifier.VirtualNodeIdentifier;

/**
 * Specifies the action of a VirtualNetwork by providing the virtual node to be
 * allocated.
 * 
 * @author Vincent Letard
 */
public class VirtualNetworkSpecification extends HorizonSpecification {

	/**
	 * Serial version identifier.
	 */
	private static final long serialVersionUID = -8399535995860205895L;

	/**
	 * Virtual node of the agent that will be allocated.
	 */
	private final VirtualNodeIdentifier node;

	/**
	 * Constructs a new VirtualNetworkSpecification to allocate the specified
	 * node.
	 * 
	 * @param specif
	 *            The node to allocate.
	 */
	public VirtualNetworkSpecification(final VirtualNodeIdentifier specif) {
		this.node = specif;
	}

	/**
	 * Gives the VirtualNetwork specified by this object.
	 */
	@Override
	public VirtualNetworkIdentifier getMyAgentIdentifier() {
		return this.node.getMyNetwork();
	}

	/**
	 * Gives the node to be allocated.
	 * 
	 * @return The specified node of the agent specified by this object.
	 */
	public VirtualNodeIdentifier getNode() {
		return this.node;
	}

}
