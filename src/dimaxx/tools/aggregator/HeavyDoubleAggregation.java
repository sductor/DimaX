package dimaxx.tools.aggregator;

import java.util.Collection;
import java.util.Map;

public class HeavyDoubleAggregation extends HeavyAggregation<Double> {
	private static final long serialVersionUID = 1216370115881769666L;

	//
	// Fields
	//

	private final LightWeightedAverageDoubleAggregation myAverageInfo = new LightWeightedAverageDoubleAggregation();


	//
	// Methods
	//

	//	@Override
	//	public Double add(Double e) {
	//		this.myAverageInfo.add(e, 1.);
	//		return super.add(e);
	//	}

	@Override
	public Double put(final Double o, final Double weight) {
		this.myAverageInfo.add(o, weight);
		return super.put(o, weight);
	}

	//	@Override
	//	public Double remove(Double o) {
	//		this.myAverageInfo.remove((Double) o, this.get(o));
	//		return super.remove(o);
	//	}

	/*
	 *
	 */

	@Override
	public Double getRepresentativeElement() {
		return this.myAverageInfo.getRepresentativeElement();
	}

	public void fuse(final AbstractCompensativeAggregation<? extends Double> average2) {
		this.myAverageInfo.fuse(average2);
	}

	/*
	 * 
	 */

	@Override
	public Double getNumericValue(final Double e) {
		return e;
	}

	public Double getSum() {
		return this.myAverageInfo.getSum();
	}

	@Override
	public double getWeightOfAggregatedElements() {
		return this.myAverageInfo.getWeightOfAggregatedElement();
	}

	@Override
	public Double getRepresentativeElement(final Collection<? extends Double> elems) {
		double result = 0;
		for (final Double e : elems)
			result += e;
				return result;
	}

	@Override
	public AbstractCompensativeAggregation<Double> fuse(
			final Collection<? extends AbstractCompensativeAggregation<? extends Double>> averages) {
		final HeavyDoubleAggregation result = new HeavyDoubleAggregation();
		result.fuse(this);
		for (final AbstractCompensativeAggregation<? extends Double> average : averages)
			result.fuse(average);
				return result;
	}

	@Override
	public Double getRepresentativeElement(final Map<? extends Double, Double> elems) {
		final LightWeightedAverageDoubleAggregation result = new LightWeightedAverageDoubleAggregation();
		for (final Double e : elems.keySet())
			result.add(e,elems.get(e));
				return result.getRepresentativeElement();
	}

}
