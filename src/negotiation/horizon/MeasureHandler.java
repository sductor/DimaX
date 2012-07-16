package negotiation.horizon;

import negotiation.horizon.negotiatingagent.SubstrateNodeIdentifier;
import negotiation.horizon.parameters.HorizonMeasurableParameters;
import dima.introspectionbasedagents.services.BasicAgentCompetence;

/**
 * Competence whose function is to perform measures on the physical machine.
 * Actually, this competence does nothing more than providing the parameters
 * given in the constructor.
 * 
 * @author Vincent Letard
 */
public class MeasureHandler extends BasicAgentCompetence<SubstrateNode> {

    /**
     * Serial version identifier.
     */
    private static final long serialVersionUID = -641940585284016641L;

    /**
     * Current measured parameters of the machine.
     */
    private HorizonMeasurableParameters<SubstrateNodeIdentifier> currentParams;

    /**
     * @param params
     *            initial parameters
     */
    public MeasureHandler(
	    final HorizonMeasurableParameters<SubstrateNodeIdentifier> params) {
	this.currentParams = params;
    }

    /**
     * "Performs" the measures. For now returns only the parameters given to the
     * constructor.
     * 
     * @return the "measured" parameters.
     */
    public HorizonMeasurableParameters<SubstrateNodeIdentifier> performMeasures() {
	return this.currentParams;
    }
}
