package dimaxx.tools.aggregator;

import dima.basicinterfaces.DimaComponentInterface;

public interface UtilitaristAnalyser<Element> extends DimaComponentInterface {

	Double getNumericValue(Element e);
}
