package negotiation.horizon.experimentation;

import negotiation.horizon.parameters.HorizonMeasurableParameters;
import dima.introspectionbasedagents.services.BasicAgentCompetence;

public class MeasureHandler extends BasicAgentCompetence<SubstrateNode> {

    /**
     * Serial version identifier.
     */
    private static final long serialVersionUID = -641940585284016641L;

    private HorizonMeasurableParameters currentParams;

    public MeasureHandler(final HorizonMeasurableParameters params) {
	this.currentParams = params;
    }

    public HorizonMeasurableParameters getMeasurableParameters() {
	return this.currentParams;
    }
}
