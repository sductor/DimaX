package dima.introspectionbasedagents.services.modules.aggregator;


public class LightAverageDoubleAggregation implements
AbstractCompensativeAggregation<Double>, AbstractMinMaxAggregation<Double> {
	private static final long serialVersionUID = 5702510745579722877L;

	protected Double sum = 0.;
	protected Integer cardinal = 0;
	protected Double prod=1.;
	protected Double min=Double.POSITIVE_INFINITY;
	protected Double max=Double.NEGATIVE_INFINITY;

	public boolean add(final Double value) {
		this.sum += value;
		this.cardinal++;
		this.prod *=value;
		this.min = Math.min(this.min,value);
		this.max = Math.max(this.max,value);
		return true;
	}

	public boolean remove(final Double value) {
		this.sum -= value;
		this.cardinal--;
		this.prod /=value;
		if (this.min.equals(value)) {
			this.min.equals(Double.NaN);
		}
		if (this.max.equals(value)) {
			this.max.equals(Double.NaN);
		}
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

	@Override
	public double getWeightOfAggregatedElements() {
		return this.getNumberOfAggregatedElements();
	}

	@Override
	public Double getProd() {
		if (this.cardinal == 0) {
			return Double.NaN;
		} else {
			return this.prod;
		}
	}

	/**
	 * 
	 * @return the min of the values, +infty if no value, or NaN if a remove call as removed the actual min
	 */
	@Override
	public Double getMinElement(){
		return this.min;
	}
	/**
	 * 
	 * @return the max of the values, +infty if no value, or NaN if a remove call as removed the actual max
	 */
	@Override
	public Double getMaxElement(){
		return this.max;
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
