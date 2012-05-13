package negotiation.horizon.negotiatingagent;

import negotiation.horizon.experimentation.VirtualNetwork;
import dima.basicagentcomponents.AgentName;

/**
 * Extension of an AgentUniqueIdentifier for clarity with VirtualNetworks.
 * 
 * @author Vincent Letard
 */
public class VirtualNetworkIdentifier extends AgentName implements
	HorizonIdentifier {

    /**
     * Serial version identifier.
     */
    private static final long serialVersionUID = -5453984972912794576L;

    /**
     * Name used to form the VirtualNetworkIdentifier name.
     */
    private static final String denomination = VirtualNetwork.class
	    .getSimpleName();

    public VirtualNetworkIdentifier(final int number) {
	super(denomination + number);
    }

    public VirtualNetworkIdentifier(final String s) {
	super(s);
    }

    /**
     * Extension of an AgentUniqueIdentifier for clarity with VirtualNodes.
     * 
     * @author Vincent Letard
     */
    public class VirtualNodeIdentifier extends AgentName implements
	    HorizonIdentifier {
	/**
	 * Serial version identifier.
	 */
	private static final long serialVersionUID = -6126319326448434675L;

	public VirtualNodeIdentifier(final int number) {
	    super(VirtualNetworkIdentifier.this.getId() + "_VirtualNode"
		    + number);
	}

	@Override
	public boolean equals(final Object obj) {
	    return VirtualNetworkIdentifier.this.equals(obj);
	}

	@Override
	public int hashCode() {
	    return VirtualNetworkIdentifier.this.hashCode();
	}
    }
}
