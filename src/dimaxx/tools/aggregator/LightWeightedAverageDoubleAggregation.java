package dimaxx.tools.aggregator;


public class LightWeightedAverageDoubleAggregation 
extends LightAverageDoubleAggregation {
	private static final long serialVersionUID = 5702510745579722877L;

	protected Double weight = 0.;


	public boolean add(final Double value, final Double weight) {		
		super.add(value * weight);
		this.weight += weight;
		return true;
	}

	public boolean remove(final Double value, final Double weight) {
		super.remove(value * weight);
		this.weight -= weight;
		return true;
	}

	public boolean add(final Double value, final int weight) {
		super.add(value * weight);
		this.weight += weight;
		return true;
	}

	public boolean remove(final Double value, final int weight) {
		super.remove(value * weight);
		this.weight -= weight;
		return true;
	}
	
	/*
	 *
	 */

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

	@Override
	public Double getSum() {
		if (this.cardinal == 0)
			return Double.NaN;
		else
			return this.sum / this.weight;
	}

	/*
	 *
	 */

	@Override
	public void fuse(AbstractCompensativeAggregation<? extends Double> average2) {
		this.cardinal += average2.getNumberOfAggregatedElements();
		if (average2 instanceof LightWeightedAverageDoubleAggregation) {
			final LightWeightedAverageDoubleAggregation av2 = (LightWeightedAverageDoubleAggregation) average2;
			this.sum += av2.getRepresentativeElement() * av2.weight;
			this.weight += av2.weight;
			this.cardinal += av2.cardinal;
		} else
			this.sum += average2.getRepresentativeElement();
	}
}
