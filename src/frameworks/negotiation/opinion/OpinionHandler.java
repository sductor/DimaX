package frameworks.negotiation.opinion;

import dima.introspectionbasedagents.modules.aggregator.AbstractCompensativeAggregation;
import dima.introspectionbasedagents.modules.aggregator.AbstractDispersionAggregation;
import dima.introspectionbasedagents.modules.aggregator.AbstractMinMaxAggregation;
import dima.introspectionbasedagents.modules.aggregator.FunctionnalCompensativeAggregator;
import dima.introspectionbasedagents.modules.aggregator.UtilitaristAnalyser;
import dima.introspectionbasedagents.modules.aggregator.HeavyParametredAggregation.Agg;
import dima.introspectionbasedagents.services.information.ObservationService;
import dima.introspectionbasedagents.services.information.ObservationService.Information;

public interface OpinionHandler<OpinionType extends Information>
extends 	UtilitaristAnalyser<OpinionType>, 
FunctionnalCompensativeAggregator<OpinionType>, Agg<OpinionType>{

	Class<? extends Information> getInfoType();

}
