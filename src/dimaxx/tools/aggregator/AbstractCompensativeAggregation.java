package dimaxx.tools.aggregator;

import dima.basicinterfaces.DimaComponentInterface;

public interface AbstractCompensativeAggregation<Element> extends
DimaComponentInterface {

	public abstract Element getRepresentativeElement();

	public int getNumberOfAggregatedElements();
}
