package negotiation.tools.aggregator;

public class AverageWeightedDoubleAgregator extends AverageDoubleAggregator {
	private static final long serialVersionUID = -4220754384753629477L;

	protected Double weight = 0.;

	public boolean add(final Double value, final Double weight) {
		super.add(value*weight);
		this.weight +=weight;
		return true;
	}


	@Override
	public Double getRepresentativeElement() {
		if (this.weight == 0)
			return Double.NaN;
		else
			return this.sum / this.weight;
	}

	public double getWeightOfAggregatedElement() {
		return this.weight;
	}

}
