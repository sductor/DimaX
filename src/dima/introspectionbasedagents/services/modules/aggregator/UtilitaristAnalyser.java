package dima.introspectionbasedagents.services.modules.aggregator;

import dima.basicinterfaces.DimaComponentInterface;

public interface UtilitaristAnalyser<Element> extends DimaComponentInterface {

	Double getNumericValue(Element e);
}
