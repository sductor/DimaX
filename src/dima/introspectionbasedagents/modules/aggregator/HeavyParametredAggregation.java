package dima.introspectionbasedagents.modules.aggregator;

import java.util.Collection;
import java.util.Comparator;
import java.util.Map;

public class HeavyParametredAggregation<Element extends Object> extends HeavyAggregation<Element>{

	/**
	 * 
	 */
	private static final long serialVersionUID = 650833063798649323L;


	public interface Agg<Element> extends
	FunctionnalCompensativeAggregator<Element>,
	UtilitaristAnalyser<Element>{};


	Agg<Element> myAggregator;


	public HeavyParametredAggregation(final Agg<Element> myAggregator) {
		super();
		this.myAggregator = myAggregator;
	}


	public HeavyParametredAggregation(final Agg<Element> myAggregator,
			final Comparator<? super Element> arg0) {
		super(arg0);
		this.myAggregator = myAggregator;
	}


	@Override
	public Double getNumericValue(final Element e) {
		return this.myAggregator.getNumericValue(e);
	}


	@Override
	public Element getRepresentativeElement() {
		return this.myAggregator.getRepresentativeElement(this);
	}


	@Override
	public Double getProd() {
		double prod=1;
		for (final Element d : this.keySet()) {
			prod*=this.myAggregator.getNumericValue(d);
		}
		return prod;
	}


	@Override
	public Object getRepresentativeElement(final Collection elems) {
		return this.myAggregator.getRepresentativeElement(elems);
	}


	@Override
	public int getNumberOfAggregatedElements() {
		return this.keySet().size();
	}


	@Override
	public double getWeightOfAggregatedElements() {
		double weight=0;
		for (final Double d : this.values()) {
			weight+=d;
		}
		return weight;
	}


	@Override
	public Object getRepresentativeElement(final Map elems) {
		return this.myAggregator.getRepresentativeElement(elems);
	}


	@Override
	public AbstractCompensativeAggregation fuse(final Collection averages) {
		return this.myAggregator.fuse(averages);
	}




}
