package frameworks.negotiation.horizon.parameters;

import frameworks.negotiation.horizon.negotiatingagent.HorizonIdentifier;

/**
 * Gathers the non functional parameters of a node in one object.
 * 
 * @param <Identifier>
 *            The identifier indexing the links for InterfacesParameters.
 * @author Vincent Letard
 */
public class HorizonMeasurableParameters<Identifier extends HorizonIdentifier>
extends
HorizonParameters<Identifier, MachineMeasurableParameters, LinkMeasurableParameters> {

	/**
	 * Serial version identifier.
	 */
	private static final long serialVersionUID = -384855814402430086L;

	/**
	 * @param machineParams
	 *            parameters of the machine
	 * @param ifacesParams
	 *            parameters of the links starting from the machine
	 */
	public HorizonMeasurableParameters(
			final MachineMeasurableParameters machineParams,
			final InterfacesParameters<Identifier, LinkMeasurableParameters> ifacesParams) {
		super(machineParams, ifacesParams);
	}
}
