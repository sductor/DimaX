package dimaxx.tools.aggregator;


public class LightAverageDoubleAggregation implements
AbstractCompensativeAggregation<Double> {
	private static final long serialVersionUID = 5702510745579722877L;

	protected Double sum = 0.;
	protected Integer cardinal = 0;
	protected Double prod=1.;

	public boolean add(final Double value) {
		this.sum += value;
		this.cardinal++;
		this.prod *=value;
		return true;
	}

	public boolean remove(final Double value) {
		this.sum -= value;
		this.cardinal--;
		this.prod /=value;
		return true;
	}

	/*
	 *
	 */

	@Override
	public Double getRepresentativeElement() {
		if (this.cardinal == 0) {
			return Double.NaN;
		} else {
			return this.sum / this.cardinal;
		}
	}

	@Override
	public int getNumberOfAggregatedElements() {
		return this.cardinal;
	}

	public Double getSum() {
		if (this.cardinal == 0) {
			return Double.NaN;
		} else {
			return this.sum;
		}
	}

	public Double getProd() {
		if (this.cardinal == 0) {
			return Double.NaN;
		} else {
			return this.prod;
		}
	}
	/*
	 *
	 */

	public void fuse(final AbstractCompensativeAggregation<? extends Double> average2) {
		this.cardinal += average2.getNumberOfAggregatedElements();
		if (average2 instanceof LightAverageDoubleAggregation) {
			final LightAverageDoubleAggregation av2 = (LightAverageDoubleAggregation) average2;
			this.sum += av2.getRepresentativeElement() * av2.cardinal;
			this.cardinal += av2.cardinal;
		} else {
			this.sum += average2.getRepresentativeElement();
		}
	}
}
