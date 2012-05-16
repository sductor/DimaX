package negotiation.horizon.parameters;

/**
 * 
 * @author Vincent Letard
 */
public class HorizonMeasurableParameters
	extends
	HorizonParameters<MachineMeasurableParameters, LinkMeasurableParameters> {

    /**
     * Serial version identifier.
     */
    private static final long serialVersionUID = -384855814402430086L;

    public HorizonMeasurableParameters(
	    final MachineMeasurableParameters machineParams,
	    final InterfacesParameters<LinkMeasurableParameters> ifacesParams) {
	super(machineParams, ifacesParams);
    }
}
