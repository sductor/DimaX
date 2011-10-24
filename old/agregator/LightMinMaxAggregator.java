package negotiation.tools.aggregator;

import negotiation.tools.aggregator.interfaces.AggregatorFunction;
import negotiation.tools.agregator2.AbstractMinMaxAggregator;

public class LightMinMaxAggregator<Element> implements AbstractMinMaxAggregator<Element>{

	/**
	 *
	 */
	private static final long serialVersionUID = -1009202828940368240L;
	protected Element max;
	protected Element min;
	AggregatorFunction<Element> f;

	public LightMinMaxAggregator(final AggregatorFunction<Element> f){
		this.min = f.getSupBound();
		this.max = f.getInfBound();
		this.f = f;
	}

	@Override
	public boolean add(final Element value) {
		this.max = this.f.compare(value, this.max)>0?value:this.max;
		this.min = this.f.compare(value, this.min)>0?this.min:value;
		return true;
	}

	@Override
	public Element getMax() {
		if (this.max.equals(this.f.getInfBound()))
			return this.f.getSupBound();
		return this.max;
	}

	@Override
	public Element getMin() {
		if (this.min == this.f.getSupBound())
				return this.f.getInfBound();
		return this.min;
	}

	public Double getEtendue() {
		return this.f.distance(this.getMax(), this.getMin());
	}
}
