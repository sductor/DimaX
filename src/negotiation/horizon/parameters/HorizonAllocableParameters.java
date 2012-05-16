package negotiation.horizon.parameters;

public class HorizonAllocableParameters extends
	HorizonParameters<MachineAllocableParameters, LinkAllocableParameters>
	implements AllocableParameters {

    /**
     * Serial version identifier.
     */
    private static final long serialVersionUID = 2458579262649743840L;

    public HorizonAllocableParameters(MachineAllocableParameters machineParams,
	    InterfacesParameters<LinkAllocableParameters> ifacesParam) {
	super(machineParams, ifacesParam);
    }

    @Override
    public boolean isValid() {
	if (!this.getMachineParameters().isValid())
	    return false;
	for (LinkAllocableParameters p : this.getInterfacesParameters()
		.values()) {
	    if (!p.isValid())
		return false;
	}
	return true;
    }

    public HorizonAllocableParameters add(HorizonAllocableParameters params) {
	return new HorizonAllocableParameters(this.getMachineParameters().add(
		params.getMachineParameters()), InterfacesParameters.add(this
		.getInterfacesParameters(), params.getInterfacesParameters()));
    }

    public HorizonAllocableParameters subtract(HorizonAllocableParameters params) {
	return new HorizonAllocableParameters(this.getMachineParameters()
		.subtract(params.getMachineParameters()), InterfacesParameters
		.subtract(this.getInterfacesParameters(), params
			.getInterfacesParameters()));
    }
}
