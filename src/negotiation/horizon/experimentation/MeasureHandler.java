package negotiation.horizon.experimentation;

import negotiation.horizon.negotiatingagent.SubstrateNodeIdentifier;
import negotiation.horizon.parameters.HorizonMeasurableParameters;
import dima.introspectionbasedagents.services.BasicAgentCompetence;

public class MeasureHandler extends BasicAgentCompetence<SubstrateNode> {

    /**
     * Serial version identifier.
     */
    private static final long serialVersionUID = -641940585284016641L;

    private HorizonMeasurableParameters<SubstrateNodeIdentifier> currentParams;

    public MeasureHandler(
	    final HorizonMeasurableParameters<SubstrateNodeIdentifier> params) {
	this.currentParams = params;
    }

    public HorizonMeasurableParameters<SubstrateNodeIdentifier> performMeasures() {
	return this.currentParams;
    }
}
