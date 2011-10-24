package negotiation.tools.aggregator;

import java.util.Comparator;
import java.util.TreeMap;
import java.util.TreeSet;

import negotiation.tools.aggregator.interfaces.HeavyAggregator;
import negotiation.tools.agregator2.AbstractMinMaxAggregator;



public class HeavyMinMaxAggregator<Element>
extends TreeMap<Element,Double>
implements HeavyAggregator<Element>, AbstractMinMaxAggregator<Element>{

	/**
	 *
	 */
	private static final long serialVersionUID = -2028316200931062920L;

	public HeavyMinMaxAggregator(){
	}

	public HeavyMinMaxAggregator(final Comparator<Element> e){
		super(e);
	}

	@Override
	public boolean add(final Element value) {
		return super.add(value);
	}

	@Override
	public boolean remove(final Object value) {
		return super.remove(value);
	}
	@Override
	public Element getMax() {
		return this.last();
	}

	@Override
	public Element getMin() {
		return this.first();
	}

}
