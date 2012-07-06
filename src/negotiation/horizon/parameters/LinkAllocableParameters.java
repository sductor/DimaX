package negotiation.horizon.parameters;

/**
 * LinkParameters regroup the network parameters that are relative to the links
 * between nodes, or the network interfaces.
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

    public LinkAllocableParameters(final int bandwidth) {
	this.bandwidth = bandwidth;
    }

    /**
     * @return the value of the field bandwidth
     */
    public int getBandwidth() {
	return bandwidth;
    }

    @Override
    public String toString() {
	return "(b=" + this.bandwidth + ")";
    }

    public boolean isValid() {
	return this.bandwidth >= 0;
    }

    public LinkAllocableParameters add(
	    final LinkAllocableParameters linkAllocableParameters) {
	return new LinkAllocableParameters(this.bandwidth
		+ linkAllocableParameters.bandwidth);
    }

    public LinkAllocableParameters subtract(
	    final LinkAllocableParameters linkAllocableParameters) {
	return new LinkAllocableParameters(this.bandwidth
		- linkAllocableParameters.bandwidth);
    }
}
