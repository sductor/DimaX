package dima.introspectionbasedagents.services.modules.aggregator;

import dima.basicinterfaces.DimaComponentInterface;

public interface AbstractMinMaxAggregation<Element> extends
DimaComponentInterface {

	// public abstract boolean add(final Element value);

	public abstract Element getMaxElement();

	public abstract Element getMinElement();

	// public Double getEtendue();
}
