package frameworks.negotiation.horizon.parameters;

/**
 * Gathers the link parameters of a whole network.
 * 
 * @author Vincent Letard
 */
public class NetworkLinkParameters implements LinkParameters {
	/**
	 * Serial version identifier.
	 */
	private static final long serialVersionUID = 2427947466031820192L;
	/**
	 * AllocableParameters.
	 */
	private final LinkAllocableParameters lap;
	/**
	 * MeasurableParameters.
	 */
	private final LinkMeasurableParameters lmp;

	/**
	 * @param allocableParams
	 *            AllocableParameters of links
	 * @param measurableParams
	 *            MeasurableParameters of links
	 */
	public NetworkLinkParameters(final LinkAllocableParameters allocableParams,
			final LinkMeasurableParameters measurableParams) {
		this.lap = allocableParams;
		this.lmp = measurableParams;
	}

	/**
	 * @return the AllocableParameters
	 */
	public LinkAllocableParameters getAllocableParams() {
		return this.lap;
	}

	/**
	 * @return the MeasurableParameters
	 */
	public LinkMeasurableParameters getMeasurableParams() {
		return this.lmp;
	}
}
