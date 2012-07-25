package dima.introspectionbasedagents.modules.aggregator;

import dima.basicinterfaces.DimaComponentInterface;

public interface AbstractCompensativeAggregation<Element> extends
DimaComponentInterface {

	public abstract Element getRepresentativeElement();

	public Double getProd();

	public int getNumberOfAggregatedElements();

	public double getWeightOfAggregatedElements();

}
