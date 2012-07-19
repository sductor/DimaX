package dima.introspectionbasedagents.services.modules.aggregator;

import dima.basicinterfaces.DimaComponentInterface;

public interface AbstractDispersionAggregation extends DimaComponentInterface {
	public double getVariance();

	public double getEcartType();

	public double getVariationCoefficient();

}