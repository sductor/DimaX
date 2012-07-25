package dima.introspectionbasedagents.modules.aggregator;

import java.util.ArrayList;
import java.util.TreeMap;


/**
 * Collection of elements mapped to their weight Delegate all aggregation
 * functions.
 *
 * @author Sylvain Ductor
 *
 * @param <Element>
 */
public abstract class HeavyIdentifiedAggregation<Element extends Comparable>
extends HeavyAggregation<Element>
implements
AbstractCompensativeAggregation<Element>,
FunctionnalCompensativeAggregator<Element>, AbstractDispersionAggregation,
AbstractMinMaxAggregation<Element>, AbstractQuantileAggregation<Element>,
UtilitaristAnalyser<Element> {
	private static final long serialVersionUID = 4326518157900844655L;

	//Allow to enter twice the same element (e.g. twice the same double) without overriding the first one
	TreeMap<ElementIdentifier<Element>, Double> elements;

	public HeavyIdentifiedAggregation() {
		super();
	}

	//TODO Générer les comparator, map associés aux identifiedElement
	//	public HeavyAggregation(Comparator<? super Element> arg0) {
	//		elements = new TreeMap<ElementIdentifier<Element>, Double>(arg0);
	//	}
	//
	//	public HeavyAggregation(Map<? extends Element, ? extends Double> arg0) {
	//		elements = new TreeMap<ElementIdentifier<Element>, Double>(arg0);
	//	}
	//
	//	public HeavyAggregation(SortedMap<Element, ? extends Double> arg0) {
	//		elements = new TreeMap<ElementIdentifier<Element>, Double>(arg0);
	//	}

	/*
	 *
	 */
	@Override
	public Double put(final Element o, final Double weight) {
		return this.elements.put(new ElementIdentifier(o), weight);
	}

	//	public Double remove(Element o) {
	//		return elements.remove(o);
	//	}

	public Double getWeightOf(final Element o) {
		return this.elements.get(o);
	}

	@Override
	public Double add(final Element e) {
		return this.put(e, 1.);
	}

	@Override
	public boolean isEmpty() {
		return this.elements.isEmpty();
	}

	/*
	 *
	 */

	@Override
	public Element getMaxElement() {
		return this.elements.lastKey().e;
	}

	@Override
	public Element getMinElement() {
		return this.elements.firstKey().e;
	}

	@Override
	public double getVariance() {
		throw new RuntimeException();
		//		return FunctionalDispersionAgregator.getVariance(this, elements.keySet());
	}

	@Override
	public double getEcartType() {
		throw new RuntimeException();
//		return FunctionalDispersionAgregator.getEcartType(this, this.elements.values());
	}

	@Override
	public double getVariationCoefficient() {
		throw new RuntimeException();
//		return FunctionalDispersionAgregator.getVariationCoefficient(this, this.elements.values());
	}

	/*
	 *
	 */

	@Override
	public int getNumberOfAggregatedElements() {
		return this.elements.size();
	}

	/*
	 *
	 */

	@Override
	public Element getQuantile(final int k, final int q) {
		return FunctionalQuantileNMinMaxAggregator.getQuantile(
				new ArrayList<ElementIdentifier<Element>>(this.elements.keySet()), k, q).e;
	}

	@Override
	public Element getFirstTercile() {
		return FunctionalQuantileNMinMaxAggregator
				.getFirstTercile(new ArrayList<ElementIdentifier<Element>>(this.elements.keySet())).e;
	}

	@Override
	public Element getSecondTercile() {
		return FunctionalQuantileNMinMaxAggregator
				.getSecondTercile(new ArrayList<ElementIdentifier<Element>>(this.elements.keySet())).e;
	}

	@Override
	public Element getMediane() {
		return FunctionalQuantileNMinMaxAggregator
				.getMediane(new ArrayList<ElementIdentifier<Element>>(this.elements.keySet())).e;
	}

}

class ElementIdentifier<Element extends Comparable> implements Comparable<ElementIdentifier<Element>>{

	static int nbClass=0;
	final int nbObject;
	final Element e;

	ElementIdentifier(final Element e) {
		super();
		this.nbObject = ElementIdentifier.nbClass;
		ElementIdentifier.nbClass++;
		this.e=e;
	}

	@Override
	public boolean equals(final Object o){
		if (o instanceof ElementIdentifier) {
			return this.e.equals(((ElementIdentifier) o).e) && ((ElementIdentifier) o).nbObject==this.nbObject;
		} else {
			return false;
		}
	}

	public int hashcode(){
		return this.e.hashCode();
	}

	@Override
	public int compareTo(final ElementIdentifier<Element> that) {
		return this.e.compareTo(that.e);
	}

}