package frameworks.horizon.negotiatingagent;

import dima.basicagentcomponents.AgentName;
import frameworks.horizon.VirtualNetwork;

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

	/**
	 * Number of VirtualNodes belonging to this VirtualNetwork (Used by the
	 * constructor of the nested class VirtualNodeIdentifier).
	 */
	private int nodeNumber = 0;

	/**
	 * @param number
	 *            Number of this VirtualNetwork.
	 */
	public VirtualNetworkIdentifier(final int number) {
		super(denomination + number);
	}

	/**
	 * Constructs a new VirtualNetworkIdentifier using the specified
	 * identification String.
	 * 
	 * @param s
	 *            A String to identify the VirtualNetwork
	 */
	public VirtualNetworkIdentifier(final String s) {
		super(s);
	}

	/**
	 * Identifies a VirtualNode.
	 * 
	 * @author Vincent Letard
	 */
	public class VirtualNodeIdentifier extends AgentName implements
	HorizonIdentifier, Comparable<VirtualNodeIdentifier> {
		/**
		 * Serial version identifier.
		 */
		private static final long serialVersionUID = -6126319326448434675L;

		/**
		 * Number of this node in the VirtualNetwork.
		 */
		private final int number;
		/**
		 * Easy access to the including instance of VirtualNetworkIdentifier.
		 */
		private final VirtualNetworkIdentifier myVNId = VirtualNetworkIdentifier.this;

		/**
		 * Constructs a new VirtualNodeIdentifier for the given node number.
		 * 
		 * @param number
		 *            Number of the VirtualNode in the VirtualNetwork.
		 */
		@Deprecated
		public VirtualNodeIdentifier(final int number) {
			super(VirtualNetworkIdentifier.this.getId() + "_"
					+ VirtualNodeIdentifier.class.getSimpleName() + number);
			this.number = number;
			if (this.myVNId.nodeNumber <= number) {
				this.myVNId.nodeNumber = number + 1;
			}
		}

		/**
		 * Appropriate constructor with no argument. It computes the number of
		 * the node automatically.
		 */
		public VirtualNodeIdentifier() {
			super(VirtualNetworkIdentifier.this.getId() + "_"
					+ VirtualNodeIdentifier.class.getSimpleName()
					+ VirtualNetworkIdentifier.this.nodeNumber);
			this.number = this.myVNId.nodeNumber;
			this.myVNId.nodeNumber++;
		}

		/**
		 * Matches the given object with this VirtualNodeIdentifier for
		 * equality.
		 */
		@Override
		public boolean equals(final Object obj) {
			if (!(obj instanceof VirtualNodeIdentifier)) {
				return false;
			}
			return this.number == ((VirtualNodeIdentifier) obj).number
					&& this.myVNId.equals(((VirtualNodeIdentifier) obj).myVNId);
		}

		/**
		 * The hashCode of a VirtualNodeIdentifier is the same as its
		 * VirtualNetworkIdentifier. Thus the VirtualNodeIdentifiers of a same
		 * VirtualNetwork tend to be gathered in hash structures.
		 */
		@Override
		public int hashCode() {
			return this.myVNId.hashCode();
		}

		/**
		 * Compares the VirtualNodeIdentifiers by matching their node numbers.
		 */
		@Override
		public int compareTo(final VirtualNodeIdentifier arg0) {
			return this.number - arg0.number;
		}

		/**
		 * Gives the identifier of the network to which belongs the VirtualNode.
		 * 
		 * @return the VirtualNetworkIdentifier
		 */
		public VirtualNetworkIdentifier getMyNetwork() {
			return this.myVNId;
		}

	}
}
