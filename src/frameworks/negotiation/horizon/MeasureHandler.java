package frameworks.negotiation.horizon;

import dima.introspectionbasedagents.services.BasicAgentCompetence;
import frameworks.negotiation.horizon.negotiatingagent.SubstrateNodeIdentifier;
import frameworks.negotiation.horizon.parameters.HorizonMeasurableParameters;

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
	private final HorizonMeasurableParameters<SubstrateNodeIdentifier> currentParams;

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
