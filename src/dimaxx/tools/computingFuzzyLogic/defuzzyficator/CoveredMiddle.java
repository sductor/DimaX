package dimaxx.tools.computingFuzzyLogic.defuzzyficator;

import dimaxx.tools.computingFuzzyLogic.controller.FuzzySubSet;

public class CoveredMiddle implements Defuzzificateur {

	/**
	 *
	 */
	private static final long serialVersionUID = -8836982230942041657L;

	@Override
	public double defuzz(final FuzzySubSet a) {
		return (a.valeurMax().getX() - a.valeurMinNonNulle().getX()) / 2.;
	}

}
