package frameworks.negotiation.horizon.parameters;

/**
 * Gathers the network functional parameters that are relative to the machines.
 * 
 * @author Vincent Letard
 */
public class MachineAllocableParameters implements MachineParameters,
AllocableParameters {

	/**
	 * Serial version identifier.
	 */
	private static final long serialVersionUID = 1670194640169706930L;

	/**
	 * Computation capacity in IPS.
	 */
	private final Integer processor;
	/**
	 * Amount of memory in ko.
	 */
	private final Integer ram;

	/**
	 * @param processor
	 *            computation capacity in Instructions Per Second
	 * @param ram
	 *            amount of memory in kilo octets
	 */
	public MachineAllocableParameters(final Integer processor, final Integer ram) {
		this.processor = processor;
		this.ram = ram;
	}

	/**
	 * @return the value of the field processor.
	 */
	public Integer getProcessor() {
		return this.processor;
	}

	/**
	 * @return the value of the field ram.
	 */
	public Integer getRam() {
		return this.ram;
	}

	/**
	 * Parameters of MachineAllocableParameters are valid if both are positive
	 * or null.
	 * 
	 * @return <code>true</code> if the object is valid according to the
	 *         contract of AllocablParameters
	 */
	@Override
	public boolean isValid() {
		return this.processor >= 0 && this.ram >= 0;
	}

	/**
	 * Performs addition operation with specified parameters.
	 * 
	 * @param machineParameters
	 *            Parameters to add.
	 * @return the resulting MachineAllocableParameters of the addition.
	 */
	public MachineAllocableParameters add(
			final MachineAllocableParameters machineParameters) {
		return new MachineAllocableParameters(this.processor
				+ machineParameters.processor, this.ram + machineParameters.ram);
	}

	/**
	 * Subtracts the parameters in the specified argument from those of the
	 * current object.
	 * 
	 * @param machineParameters
	 *            Parameters to subtract.
	 * @return The resulting MachineAllocableParameters of the subtraction.
	 */
	public MachineAllocableParameters subtract(
			final MachineAllocableParameters machineParameters) {
		return new MachineAllocableParameters(this.processor
				- machineParameters.processor, this.ram - machineParameters.ram);
	}

	/**
	 * Returns a String representation of this MachineAllocableParameters.
	 */
	@Override
	public String toString() {
		return "[ proc =" + this.processor + ", ram =" + this.ram + "]";
	}
}
