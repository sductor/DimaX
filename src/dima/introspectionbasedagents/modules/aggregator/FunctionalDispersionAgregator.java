package dima.introspectionbasedagents.modules.aggregator;

import java.util.Collection;
import java.util.Map;

public class FunctionalDispersionAgregator {


	public static <Element, Analyser extends UtilitaristAnalyser<Element> & FunctionnalCompensativeAggregator<Element>>
	double getVariance(final Analyser f,final Collection<Element> elements) {
		double result = 0.;
		final double moyenne = f.getNumericValue(f.getRepresentativeElement(elements));
		for (final Element e : elements) {
			result += Math.pow(
					f.getNumericValue(e) - moyenne,
					2);
		}
		return result / elements.size();
	}

	public static <Element, Analyser extends UtilitaristAnalyser<Element> & FunctionnalCompensativeAggregator<Element>>
	double getEcartType(final Analyser f,
			final Collection<Element> elements) {
		return Math
				.sqrt(FunctionalDispersionAgregator.getVariance(f, elements));
	}

	public static <Element, Analyser extends UtilitaristAnalyser<Element> & FunctionnalCompensativeAggregator<Element>>
	double getVariationCoefficient(
			final Analyser f, final Collection<Element> elements) {
		return FunctionalDispersionAgregator.getEcartType(f, elements)
				/ f.getNumericValue(f.getRepresentativeElement(elements));
	}

	/*
	 *
	 */

	public static <Element, Analyser extends UtilitaristAnalyser<Element> & FunctionnalCompensativeAggregator<Element>>
	double getVariance(final Analyser f,final Map<Element, Double> elements) {
		double result = 0.;
		for (final Element e : elements.keySet()) {
			result += elements.get(e) * Math.pow(
					f.getNumericValue(e)
					- f.getNumericValue(f.getRepresentativeElement(elements.keySet())),
					2);
		}
		return result / elements.size();
	}

	public static <Element, Analyser extends UtilitaristAnalyser<Element> & FunctionnalCompensativeAggregator<Element>>
	double getEcartType(final Analyser f,final Map<Element, Double> elements) {
		return Math
				.sqrt(FunctionalDispersionAgregator.getVariance(f, elements.keySet()));
	}

	public static <Element, Analyser extends UtilitaristAnalyser<Element> & FunctionnalCompensativeAggregator<Element>>
	double getVariationCoefficient(final Analyser f, final Map<Element, Double> elements) {
		return FunctionalDispersionAgregator.getEcartType(f, elements)
				/ f.getNumericValue(f.getRepresentativeElement(elements.keySet()));
	}
}
