package dima.introspectionbasedagents.services.modules.computingFuzzyLogic.defuzzyficator;

import dima.introspectionbasedagents.services.modules.computingFuzzyLogic.controller.FuzzySubSet;

public class GravityCenter extends SimpsonIntegral implements Defuzzificateur {

	/**
	 *
	 */
	private static final long serialVersionUID = -2251127584527928225L;
	FuzzySubSet fonction;

	@Override
	public double defuzz(final FuzzySubSet a) {
		this.fonction = a;
		return this.simpson1(10, this.fonction.ensembleDefinition.getX(),
				this.fonction.ensembleDefinition.getY())
				/ this.simpson2(10, this.fonction.ensembleDefinition.getX(),
						this.fonction.ensembleDefinition.getY());
	}

	@Override
	protected double F1(final double x) {
		return x * this.fonction.getDegre(x);
	}

	@Override
	protected double F2(final double x) {
		return this.fonction.getDegre(x);
	}

}
