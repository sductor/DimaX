package negotiation.tools.aggregator;

import java.util.HashMap;

import dima.basicagentcomponents.AgentIdentifier;

public class HeavyAverageDoubleAggregator extends HashMap<AgentIdentifier,Double>{

	/**
	 *
	 */
	private static final long serialVersionUID = 8363813616387879953L;

//	protected Double sum = 0.;
//
//	public Double put(AgentIdentifier id, final Double value) {
//		this.sum += value;
//		Double previous = super.put(id, value);
//		if (previous!=null)
//			this.sum-=previous;
//	}
//
//
//	@Override
//	public boolean remove(final Object value) {
//		if (value instanceof Double) {
//			this.sum -=  (Double) value;
//			this.cardinal--;
//			return true;
//		} else
//			return false;
//	}
//
//	/*
//	 *
//	 */
//
//	@Override
//	public Double getRepresentativeElement() {
//		if (this.cardinal == 0)
//			return Double.NaN;
//		else
//			return this.sum / this.cardinal;
//	}
//
//	/*
//	 *
//	 */
//
//	public void fuseRepresentativeElement(final Double average2, final int cardinality) {
//		this.sum += average2 *cardinality;
//		this.cardinal+=cardinality;
//	}
//
//	@Override
//	public int getNumberOfAggregatedElement() {
//		return (int) this.cardinal;
//	}

}
