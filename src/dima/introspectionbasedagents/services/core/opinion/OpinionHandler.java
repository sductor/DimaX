package dima.introspectionbasedagents.services.core.opinion;

import dima.introspectionbasedagents.services.core.information.ObservationService;
import dima.introspectionbasedagents.services.core.information.ObservationService.Information;
import dima.introspectionbasedagents.services.modules.aggregator.AbstractCompensativeAggregation;
import dima.introspectionbasedagents.services.modules.aggregator.AbstractDispersionAggregation;
import dima.introspectionbasedagents.services.modules.aggregator.AbstractMinMaxAggregation;
import dima.introspectionbasedagents.services.modules.aggregator.FunctionnalCompensativeAggregator;
import dima.introspectionbasedagents.services.modules.aggregator.HeavyParametredAggregation.Agg;
import dima.introspectionbasedagents.services.modules.aggregator.UtilitaristAnalyser;

public interface OpinionHandler<OpinionType extends Information>
extends 	UtilitaristAnalyser<OpinionType>, 
FunctionnalCompensativeAggregator<OpinionType>, Agg<OpinionType>{

	Class<? extends Information> getInfoType();

}
