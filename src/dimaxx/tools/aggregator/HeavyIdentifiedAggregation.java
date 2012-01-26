package dimaxx.tools.aggregator;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import dima.support.GimaObject;


/**
 * Collection of elements mapped to their weight Delegate all aggregation
 * functions.
 * 
 * @author Sylvain Ductor
 * 
 * @param <Element>
 */
public abstract class HeavyIdentifiedAggregation<Element extends Comparable> extends HeavyAggregation<Element>
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
	public Double put(Element o, Double weight) {
		return elements.put(new ElementIdentifier(o), weight);
	}

//	public Double remove(Element o) {
//		return elements.remove(o);
//	}

	public Double getWeightOf(Element o) {
		return elements.get(o);
	}
	
	public Double add(Element e) {
		return this.put(e, 1.);
	}

	public boolean isEmpty() {
		return elements.isEmpty();
	}
	
	/*
	 * 
	 */

	@Override
	public Element getMaxElement() {
		return elements.lastKey().e;
	}

	@Override
	public Element getMinElement() {
		return elements.firstKey().e;
	}

	@Override
	public double getVariance() {
		throw new RuntimeException();
//		return FunctionalDispersionAgregator.getVariance(this, elements.keySet());
	}

	@Override
	public double getEcartType() {
		return FunctionalDispersionAgregator.getEcartType(this, elements.values());
	}

	@Override
	public double getVariationCoefficient() {
		return FunctionalDispersionAgregator.getVariationCoefficient(this, elements.values());
	}

	/*
	 * 
	 */

	@Override
	public int getNumberOfAggregatedElements() {
		return elements.size();
	}

	/*
	 * 
	 */

	@Override
	public Element getQuantile(int k, int q) {
		return FunctionalQuantileNMinMaxAggregator.getQuantile(
				new ArrayList<ElementIdentifier<Element>>(elements.keySet()), k, q).e;
	}

	@Override
	public Element getFirstTercile() {
		return FunctionalQuantileNMinMaxAggregator
				.getFirstTercile(new ArrayList<ElementIdentifier<Element>>(elements.keySet())).e;
	}

	@Override
	public Element getSecondTercile() {
		return FunctionalQuantileNMinMaxAggregator
				.getSecondTercile(new ArrayList<ElementIdentifier<Element>>(elements.keySet())).e;
	}

	@Override
	public Element getMediane() {
		return FunctionalQuantileNMinMaxAggregator
				.getMediane(new ArrayList<ElementIdentifier<Element>>(elements.keySet())).e;
	}
	
}

class ElementIdentifier<Element extends Comparable> implements Comparable<ElementIdentifier<Element>>{
	
	static int nbClass=0;
	final int nbObject;
	final Element e;
	
	ElementIdentifier(Element e) {
		super();
		this.nbObject = nbClass;
		nbClass++;
		this.e=e;
	}
	
	public boolean equals(Object o){
		if (o instanceof ElementIdentifier)
			return this.e.equals(((ElementIdentifier) o).e) && ((ElementIdentifier) o).nbObject==this.nbObject;
		else
			return false;
		}
	
	public int hashcode(){
		return e.hashCode();
	}

	@Override
	public int compareTo(ElementIdentifier<Element> that) {
		return this.e.compareTo(that.e);
	}

}