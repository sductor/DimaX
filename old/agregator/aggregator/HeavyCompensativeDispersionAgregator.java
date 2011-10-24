package negotiation.tools.aggregator;

import java.util.Collection;

import negotiation.tools.aggregator.interfaces.DispersionAggregator;
import negotiation.tools.agregator2.AbstractCompensativeAggregator;
import negotiation.tools.agregator2.AbstractMinMaxAggregator;
import negotiation.tools.agregator2.FunctionalDispersionAgregator.DispersionAnalysableAggregator;

public abstract class HeavyCompensativeDispersionAgregator<Element extends Object>
extends HeavyQuantileAggregator<Element>
implements AbstractMinMaxAggregator<Element>, AbstractCompensativeAggregator<Element>, DispersionAggregator<Element>{


	public  boolean remove(final Element value);

	public  void add(final Element value, double weight);

	public  void remove(final Element value, double weight);

	public void fuseRepresentativeElement(final Element average2, int weight);
	
	
	public  double getVariance() {
		double result = 0.;
		for (final Element e : this)
			result += Math.pow(getNumericValue(e) - getNumericValue(getRepresentativeElement()), 2);
		return result / this.size();
	}

	public  double getEcartType() {
		return Math.sqrt(getVariance());
	}

	public double getVariationCoefficient() {
		return this.getEcartType()/ getNumericValue(getRepresentativeElement());
	}

	@Override
	public abstract double getNumericValue(Element e);

	@Override
	public abstract Element getRepresentativeElement();

	@Override
	public int getNumberOfAggregatedElement() {
		// TODO Auto-generated method stub
		return 0;
	}
}
