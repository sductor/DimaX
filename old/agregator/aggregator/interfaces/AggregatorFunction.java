package negotiation.tools.aggregator.interfaces;

import java.util.Comparator;

public interface AggregatorFunction<Element> extends Comparator<Element>{

	Element getInfBound();
	Element getSupBound();
	Double distance(Element a, Element b);
}
