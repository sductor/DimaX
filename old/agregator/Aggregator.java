package negotiation.tools.aggregator.interfaces;

import java.io.Serializable;

public interface Aggregator<Element> extends Serializable{

	public boolean add(Element n);

	public int getNumberOfAggregatedElement();

}
