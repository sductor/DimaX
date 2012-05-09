package negotiation.horizon.negociatingagent;

import negotiation.horizon.experimentation.VirtualNetwork;
import dima.basicagentcomponents.AgentName;

/**
 * Extension of an AgentUniqueIdentifier for clarity with VirtualNetworks.
 * 
 * @author Vincent Letard
 */
public class VirtualNetworkIdentifier extends AgentName {

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
}
