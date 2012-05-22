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

    private int nodeNumber = 0;

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
	    HorizonIdentifier, Comparable<VirtualNodeIdentifier> {
	/**
	 * Serial version identifier.
	 */
	private static final long serialVersionUID = -6126319326448434675L;

	private final int number;
	private final VirtualNetworkIdentifier myVNId = VirtualNetworkIdentifier.this;

	@Deprecated
	public VirtualNodeIdentifier(final int number) {
	    super(VirtualNetworkIdentifier.this.getId() + "_"
		    + VirtualNodeIdentifier.class.getSimpleName() + number);
	    this.number = number;
	    if (myVNId.nodeNumber <= number)
		myVNId.nodeNumber = number + 1;
	}

	public VirtualNodeIdentifier() {
	    super(VirtualNetworkIdentifier.this.getId() + "_"
		    + VirtualNodeIdentifier.class.getSimpleName()
		    + VirtualNetworkIdentifier.this.nodeNumber);
	    this.number = myVNId.nodeNumber;
	    myVNId.nodeNumber++;
	}

	@Override
	public boolean equals(final Object obj) {
	    if (!(obj instanceof VirtualNodeIdentifier))
		return false;
	    return this.number == ((VirtualNodeIdentifier) obj).number
		    && this.myVNId.equals(((VirtualNodeIdentifier) obj).myVNId);
	}

	@Override
	public int hashCode() {
	    return myVNId.hashCode();
	}

	@Override
	public int compareTo(VirtualNodeIdentifier arg0) {
	    return this.number - arg0.number;
	}

	public VirtualNetworkIdentifier getMyNetwork() {
	    return this.myVNId;
	}
    }
}
