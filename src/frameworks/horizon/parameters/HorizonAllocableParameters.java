package frameworks.horizon.parameters;

import frameworks.horizon.negotiatingagent.HorizonIdentifier;

/**
 * Gathers the AllocableParameters of a node in one object.
 * 
 * @param <Identifier>
 *            The identifier indexing the links for InterfacesParameters.
 * @author Vincent Letard
 */
public class HorizonAllocableParameters<Identifier extends HorizonIdentifier>
extends
HorizonParameters<Identifier, MachineAllocableParameters, LinkAllocableParameters>
implements AllocableParameters {

	/**
	 * Serial version identifier.
	 */
	private static final long serialVersionUID = 2458579262649743840L;

	/**
	 * @param machineParams
	 *            the parameters of the machine
	 * @param ifacesParam
	 *            parameters of the links starting from the machine
	 */
	public HorizonAllocableParameters(
			final MachineAllocableParameters machineParams,
			final InterfacesParameters<Identifier, LinkAllocableParameters> ifacesParam) {
		super(machineParams, ifacesParam);
	}

	/**
	 * @see frameworks.horizon.parameters.AllocableParameters#isValid()
	 */
	@Override
	public boolean isValid() {
		if (!this.getMachineParameters().isValid()) {
			return false;
		}
		for (final LinkAllocableParameters p : this.getInterfacesParameters()
				.values()) {
			if (!p.isValid()) {
				return false;
			}
		}
		return true;
	}

	/**
	 * Adds the specified parameters to those of this object.
	 * 
	 * @param params
	 *            parameters to add
	 * @return the result of the addition.
	 */
	public HorizonAllocableParameters<Identifier> add(
			final HorizonAllocableParameters<Identifier> params) {
		return new HorizonAllocableParameters<Identifier>(this
				.getMachineParameters().add(params.getMachineParameters()),
				InterfacesParameters.add(this.getInterfacesParameters(), params
						.getInterfacesParameters()));
	}

	/**
	 * Subtracts the specified parameters from those of this object.
	 * 
	 * @param params
	 *            parameters to subtract
	 * @return the result of the subtraction.
	 */
	public HorizonAllocableParameters<Identifier> subtract(
			final HorizonAllocableParameters<Identifier> params) {
		return new HorizonAllocableParameters<Identifier>(
				this.getMachineParameters().subtract(
						params.getMachineParameters()), InterfacesParameters
						.subtract(this.getInterfacesParameters(), params
								.getInterfacesParameters()));
	}
}
