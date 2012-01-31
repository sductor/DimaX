package dimaxx.tools.aggregator;

import java.util.Collection;
import java.util.Map;

import dima.basicinterfaces.DimaComponentInterface;

public interface FunctionnalCompensativeAggregator<Element> extends DimaComponentInterface {

	public abstract Element getRepresentativeElement(Collection<? extends Element> elems);

	public abstract Element getRepresentativeElement(Map<? extends Element, Double> elems);

	public AbstractCompensativeAggregation<Element> fuse(Collection<? extends AbstractCompensativeAggregation<? extends Element>> averages);
}
