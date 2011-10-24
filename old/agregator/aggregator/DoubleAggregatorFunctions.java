package negotiation.tools.aggregator;

import negotiation.tools.aggregator.interfaces.AggregatorFunction;

class DoubleAggregatorFunctions implements AggregatorFunction<Double>{

	@Override
	public Double distance(final Double a, final Double b) {
		return Math.abs(a - b);
	}

	@Override
	public Double getInfBound() {
		return Double.NEGATIVE_INFINITY;
	}

	@Override
	public Double getSupBound() {
		return Double.POSITIVE_INFINITY;
	}

	@Override
	public int compare(final Double o1, final Double o2) {
		return Double.compare(o1, o2);
	}

}