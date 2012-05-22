package negotiation.horizon.parameters;

import negotiation.horizon.negotiatingagent.HorizonIdentifier;

public class HorizonAllocableParameters<Identifier extends HorizonIdentifier>
	extends
	HorizonParameters<Identifier, MachineAllocableParameters, LinkAllocableParameters>
	implements AllocableParameters {

    /**
     * Serial version identifier.
     */
    private static final long serialVersionUID = 2458579262649743840L;

    public HorizonAllocableParameters(
	    MachineAllocableParameters machineParams,
	    InterfacesParameters<Identifier, LinkAllocableParameters> ifacesParam) {
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

    public HorizonAllocableParameters<Identifier> add(
	    HorizonAllocableParameters<Identifier> params) {
	return new HorizonAllocableParameters<Identifier>(this
		.getMachineParameters().add(params.getMachineParameters()),
		InterfacesParameters.add(this.getInterfacesParameters(), params
			.getInterfacesParameters()));
    }

    public HorizonAllocableParameters<Identifier> subtract(
	    HorizonAllocableParameters<Identifier> params) {
	return new HorizonAllocableParameters<Identifier>(
		this.getMachineParameters().subtract(
			params.getMachineParameters()), InterfacesParameters
			.subtract(this.getInterfacesParameters(), params
				.getInterfacesParameters()));
    }
}
