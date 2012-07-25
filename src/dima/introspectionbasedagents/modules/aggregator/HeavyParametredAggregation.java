package dima.introspectionbasedagents.modules.aggregator;

import java.util.Collection;
import java.util.Comparator;
import java.util.Map;

public class HeavyParametredAggregation<Element extends Object> extends HeavyAggregation<Element>{

	public interface Agg<Element> extends
	FunctionnalCompensativeAggregator<Element>,
	UtilitaristAnalyser<Element>{}; 
	
	
	Agg<Element> myAggregator;


	public HeavyParametredAggregation(Agg<Element> myAggregator) {
		super();
		this.myAggregator = myAggregator;
	}


	public HeavyParametredAggregation(Agg<Element> myAggregator,
			Comparator<? super Element> arg0) {
		super(arg0);
		this.myAggregator = myAggregator;
	}


	public Double getNumericValue(Element e) {
		return myAggregator.getNumericValue(e);
	}


	public Element getRepresentativeElement() {
		return myAggregator.getRepresentativeElement(this);
	}


	public Double getProd() {
		double prod=1;
		for (Element d : this.keySet())
			prod*=myAggregator.getNumericValue(d);
		return prod;
	}


	public Object getRepresentativeElement(Collection elems) {
		return myAggregator.getRepresentativeElement(elems);
	}


	public int getNumberOfAggregatedElements() {
		return this.keySet().size();
	}


	public double getWeightOfAggregatedElements() {
		double weight=0;
		for (Double d : this.values())
			weight+=d;
		return weight;
	}


	public Object getRepresentativeElement(Map elems) {
		return myAggregator.getRepresentativeElement(elems);
	}


	public AbstractCompensativeAggregation fuse(Collection averages) {
		return myAggregator.fuse(averages);
	}
	
	
	

}
