package negotiation.horizon.parameters;

import negotiation.horizon.negotiatingagent.HorizonIdentifier;
import dima.support.GimaObject;

/**
 * Gathers the parameters by type (functional/ non functional) in one object.
 * 
 * @param <Identifier>
 *            The identifier indexing the links for InterfacesParameters.
 * @param <MParam>
 *            The type of parameters of the machines.
 * @param <LParam>
 *            The type of parameters of the links.
 * @author Vincent Letard
 */
public abstract class HorizonParameters<Identifier extends HorizonIdentifier, MParam extends MachineParameters, LParam extends LinkParameters>
extends GimaObject {

	/**
	 * Serial version identifier.
	 */
	private static final long serialVersionUID = -7266253903347536222L;

	/**
	 * Parameters of the links starting from the machine.
	 */
	private final InterfacesParameters<Identifier, LParam> ifacesParams;
	/**
	 * Parameters of the machine.
	 */
	private final MParam machineParams;

	/**
	 * @param machineParams
	 *            Parameters of the machine.
	 * @param ifacesParam
	 *            Parameters of the links starting from the machine.
	 */
	protected HorizonParameters(final MParam machineParams,
			final InterfacesParameters<Identifier, LParam> ifacesParam) {
		this.machineParams = machineParams;
		this.ifacesParams = ifacesParam;
	}

	/**
	 * @return the parameters of the links starting from this node.
	 */
	public InterfacesParameters<Identifier, LParam> getInterfacesParameters() {
		return this.ifacesParams;
	}

	/**
	 * @return the parameters of the node
	 */
	public MParam getMachineParameters() {
		return this.machineParams;
	}

	/**
	 * Returns a string representation of an object of type HorizonParameters.
	 */
	@Override
	public String toString() {
		return "machine : " + this.getMachineParameters() + "; interfaces : "
				+ this.getInterfacesParameters();
	}
}
