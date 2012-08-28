package negotiation.tools.aggregator;

import negotiation.tools.agregator2.AbstractCompensativeAggregator;

public class HeavyDoubleAverageNQuantileAgregator  extends HeavyQuantileAggregator<Double>
implements AbstractCompensativeAggregator<Double>{

	/**
	 *
	 */
	private static final long serialVersionUID = 6289788464314730501L;

	public Double getSum(){
		Double total=0.;
		for (final Double d : this)
			total+=d;
		return total;
	}

	@Override
	public Double getRepresentativeElement() {
		return this.getSum()/this.getNumberOfAggregatedElement();
	}

	@Override
	public int getNumberOfAggregatedElement() {
		return this.size();
	}
}
