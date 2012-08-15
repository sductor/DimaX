package frameworks.negotiation.horizon.parameters;

/**
 * LinkAllocableParameters gathers the network functional parameters that are
 * relative to the links between nodes (network interfaces of the machine).
 * 
 * @author Vincent Letard
 */
public class LinkAllocableParameters implements LinkParameters,
AllocableParameters {

	/**
	 * Serial version identifier.
	 */
	private static final long serialVersionUID = -7447312075383257237L;

	/**
	 * Bit rate of the bandwidth in kbit/s.
	 */
	private final int bandwidth;

	/**
	 * @param bandwidth
	 *            the bit rate in kbit/s
	 */
	public LinkAllocableParameters(final int bandwidth) {
		this.bandwidth = bandwidth;
	}

	/**
	 * @return the value of the field bandwidth
	 */
	public int getBandwidth() {
		return this.bandwidth;
	}

	/**
	 * Returns a string representation of this object.
	 */
	@Override
	public String toString() {
		return "(b=" + this.bandwidth + ")";
	}

	/**
	 * An object of type LinkAllocableParameters is valid if its bandwidth is
	 * positive or null.
	 * 
	 * @return <code>true</code> if the object is valid according to the
	 *         contract of AllocableParameters
	 */
	@Override
	public boolean isValid() {
		return this.bandwidth >= 0;
	}

	/**
	 * Adds the values of bandwidth of this object and the one specified in
	 * argument.
	 * 
	 * @param linkAllocableParameters
	 *            Parameter to add
	 * @return a LinkAllocableParameters corresponding to the sum of the
	 *         parameters.
	 */
	public LinkAllocableParameters add(
			final LinkAllocableParameters linkAllocableParameters) {
		return new LinkAllocableParameters(this.bandwidth
				+ linkAllocableParameters.bandwidth);
	}

	/**
	 * Subtracts the value of bandwidth of the specified object from the one of
	 * the current object.
	 * 
	 * @param linkAllocableParameters
	 *            Parameter to subtract
	 * @return a LinkAllocableParameters corresponding to the subtraction of the
	 *         parameters.
	 */
	public LinkAllocableParameters subtract(
			final LinkAllocableParameters linkAllocableParameters) {
		return new LinkAllocableParameters(this.bandwidth
				- linkAllocableParameters.bandwidth);
	}
}
