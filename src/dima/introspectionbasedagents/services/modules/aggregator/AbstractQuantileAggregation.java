package dima.introspectionbasedagents.services.modules.aggregator;


public interface AbstractQuantileAggregation<Element extends Object> {

	public Element getQuantile(final int k, final int q);

	public Element getFirstTercile();

	public Element getSecondTercile();

	public Element getMediane();
}
