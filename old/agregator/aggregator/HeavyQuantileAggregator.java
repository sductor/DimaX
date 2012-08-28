package negotiation.tools.aggregator;

import java.util.Comparator;
import java.util.Iterator;

public class HeavyQuantileAggregator<Element>  extends HeavyMinMaxAggregator<Element>{


	/**
	 *
	 */
	private static final long serialVersionUID = -1415568139136690016L;

	public HeavyQuantileAggregator() {
		super();
	}


	public HeavyQuantileAggregator(final Comparator<Element> e) {
		super(e);
	}


	/**
	 *
	 * @param quanValue
	 * @param quantileNumber
	 * @return the value of the k iem q-quantile
	 */
	public Element getQuantile(final int k, final int q){
		final double p = this.size()*new Double(k)/new Double(q);
		int j = (int) Math.abs(p);
		j = j -1; //La liste commence à 0
		final double g = p - j;
		if (g < 0.5)
			return this.get(j);
		else
			return this.get(j+1);
	}


	public Element getFirstTercile(){
		return this.getQuantile(1, 3);
	}

	public Element getSecondTercile(){
		return this.getQuantile(2, 3);
	}

	public Element getMediane(){
		return this.getQuantile(1,2);
	}

	public Element get(final int i) {
		if (i>=this.size() || i < 0)
			return null;

		final Iterator<Element> itSet = this.iterator();
		int j=0;
		while (j<i){
			j++;
			itSet.next();
		}
		return itSet.next();
	}

	public static void main(final String[] args){
		final HeavyQuantileAggregator<Double> me = new HeavyQuantileAggregator<Double>();
		me.add(1.);me.add(100.);me.add(4.);me.add(42.);me.add(56.7);me.add(67.);me.add(89.);me.add(3.);

		final double p = 1./3.;
		final double N = me.size();
		final int j = (int) (N*p);
		final double g = N*p - j;

		System.out.println(p+" "+N+" "+j+" "+" "+g);
		System.out.println(me);

		System.out.println(me.getFirstTercile());
		System.out.println(me.getSecondTercile());
	}
}

//public Element getFirstTercile(){
//	if (this.isEmpty())
//		return null;
//	else
//		return this.get(Math.min(0, (int) (this.size()/3)));
//}
//
//public Element getLastTercile(){
//	if (this.isEmpty())
//		return null;
//	else
//		return this.get(Math.max(this.size()-1, (int) (2*this.size()/3+1)));
//}