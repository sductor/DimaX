package dimaxx.tools.aggregator;

import java.util.List;

public class FunctionalQuantileNMinMaxAggregator {

	/**
	 * ASSUME THAT THE LIST HAS BEEN SORTED
	 * 
	 * @param quanValue
	 * @param quantileNumber
	 * @return the value of the k iem q-quantile
	 */
	public static <Element> Element getQuantile(List<Element> e,
			final int k, final int q) {
		final double p = e.size() * new Double(k) / new Double(q);
		int j = (int) Math.abs(p);
		j = j - 1; // La liste commence à 0
		final double g = p - j;
		if (g < 0.5)
			return e.get(j);
		else
			return e.get(j + 1);
	}

	/**
	 * ASSUME THAT THE LIST HAS BEEN SORTED
	 * 
	 * @param e
	 * @return
	 */
	public static <Element> Element getMin(List<Element> e) {
		return e.get(0);
	}

	/**
	 * ASSUME THAT THE LIST HAS BEEN SORTED
	 * 
	 * @param e
	 * @return
	 */
	public static <Element> Element getMax(List<Element> e) {
		return e.get(e.size());
	}

	/**
	 * ASSUME THAT THE LIST HAS BEEN SORTED
	 * 
	 * @param e
	 * @return
	 */
	public static <Element> Element getFirstTercile(
			List<Element> e) {
		return FunctionalQuantileNMinMaxAggregator.getQuantile(e, 1, 3);
	}

	/**
	 * ASSUME THAT THE LIST HAS BEEN SORTED
	 * 
	 * @param e
	 * @return
	 */
	public static <Element> Element getSecondTercile(
			List<Element> e) {
		return FunctionalQuantileNMinMaxAggregator.getQuantile(e, 2, 3);
	}

	/**
	 * ASSUME THAT THE LIST HAS BEEN SORTED
	 * 
	 * @param e
	 * @return
	 */
	public static <Element> Element getMediane(List<Element> e) {
		return FunctionalQuantileNMinMaxAggregator.getQuantile(e, 1, 2);
	}

	// public Element get(final int i) {
	// if (i>=this.size() || i < 0)
	// return null;
	//
	// final Iterator<Element> itSet = this.iterator();
	// int j=0;
	// while (j<i){
	// j++;
	// itSet.next();
	// }
	// return itSet.next();
	// }

}
