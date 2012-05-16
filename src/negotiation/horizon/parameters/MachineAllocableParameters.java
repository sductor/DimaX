package negotiation.horizon.parameters;

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

    public boolean isValid() {
	return this.processor >= 0 && this.ram >= 0;
    }

    public MachineAllocableParameters add(
	    MachineAllocableParameters machineParameters) {
	return new MachineAllocableParameters(this.processor
		+ machineParameters.processor, this.ram + machineParameters.ram);
    }

    public MachineAllocableParameters subtract(
	    MachineAllocableParameters machineParameters) {
	return new MachineAllocableParameters(this.processor
		- machineParameters.processor, this.ram - machineParameters.ram);
    }

    @Override
    public String toString() {
	return "[ proc =" + this.processor + ", ram =" + this.ram + "]";
    }
}
