package negotiation.horizon.parameters;

import negotiation.horizon.negotiatingagent.HorizonIdentifier;

/**
 * 
 * @author Vincent Letard
 */
public class HorizonMeasurableParameters<Identifier extends HorizonIdentifier>
	extends
	HorizonParameters<Identifier, MachineMeasurableParameters, LinkMeasurableParameters> {

    /**
     * Serial version identifier.
     */
    private static final long serialVersionUID = -384855814402430086L;

    public HorizonMeasurableParameters(
	    final MachineMeasurableParameters machineParams,
	    final InterfacesParameters<Identifier, LinkMeasurableParameters> ifacesParams) {
	super(machineParams, ifacesParams);
    }
}
