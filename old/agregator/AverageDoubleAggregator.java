package negotiation.tools.aggregator;

import negotiation.tools.agregator2.AbstractCompensativeAggregator;

public class AverageDoubleAggregator implements AbstractCompensativeAggregator<Double>{
	private static final long serialVersionUID = 5702510745579722877L;

	protected Double sum = 0.;
	protected double cardinal = 0.;

	@Override
	public boolean add(final Double value) {
		this.sum += value;
		this.cardinal++;
		return true;
	}

	public boolean remove(final Double value) {
			this.sum -=  (Double) value;
			this.cardinal--;
			return true;
	}

	/*
	 *
	 */

	@Override
	public Double getRepresentativeElement() {
		if (this.cardinal == 0)
			return Double.NaN;
		else
			return this.sum / this.cardinal;
	}

	public Double getSum() {
		if (this.cardinal == 0)
			return Double.NaN;
		else
			return this.sum;
	}
	/*
	 *
	 */

	public void fuseRepresentativeElement(final Double average2, final int cardinality) {
		this.sum += average2 *cardinality;
		this.cardinal+=cardinality;
	}

	@Override
	public int getNumberOfAggregatedElement() {
		return (int) this.cardinal;
	}

}
