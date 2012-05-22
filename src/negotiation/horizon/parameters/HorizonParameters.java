package negotiation.horizon.parameters;

import negotiation.horizon.negotiatingagent.HorizonIdentifier;
import dima.support.GimaObject;

public abstract class HorizonParameters<Identifier extends HorizonIdentifier, MParam extends MachineParameters, LParam extends LinkParameters>
	extends GimaObject {

    /**
     * Serial version identifier.
     */
    private static final long serialVersionUID = -7266253903347536222L;

    private final InterfacesParameters<Identifier, LParam> ifacesParams;
    private final MParam machineParams;

    protected HorizonParameters(final MParam machineParams,
	    final InterfacesParameters<Identifier, LParam> ifacesParam) {
	this.machineParams = machineParams;
	this.ifacesParams = ifacesParam;
    }

    public InterfacesParameters<Identifier, LParam> getInterfacesParameters() {
	return this.ifacesParams;
    }

    public MParam getMachineParameters() {
	return this.machineParams;
    }

    @Override
    public String toString() {
	return "machine : " + this.getMachineParameters() + "; interfaces : "
		+ this.getInterfacesParameters();
    }
}
