package dima.introspectionbasedagents.modules.aggregator;

import dima.basicinterfaces.DimaComponentInterface;

public interface UtilitaristAnalyser<Element> extends DimaComponentInterface {

	Double getNumericValue(Element e);
}
