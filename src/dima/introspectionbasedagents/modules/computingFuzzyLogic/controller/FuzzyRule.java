package dima.introspectionbasedagents.modules.computingFuzzyLogic.controller;

import java.io.Serializable;
import java.util.Iterator;
import java.util.List;
import java.util.TreeSet;

import dima.introspectionbasedagents.modules.computingFuzzyLogic.implicator.Implicateur;
import dima.introspectionbasedagents.services.loggingactivity.LogService;

/**
 *
 * @author Sylvain Ductor
 */
public class FuzzyRule implements Serializable {

	/**
	 *
	 */
	private static final long serialVersionUID = 5393119981983682808L;

	public String nom;

	public List<FuzzySubSet> CaracPremisses;
	public FuzzySubSet CaracConclusion;

	Implicateur imp;

	/**
	 * Construteur
	 */

	public FuzzyRule(final String n,
			final List<FuzzySubSet> ensemblesPremisses,
			final FuzzySubSet ensembleConclusion, final Implicateur alors) {
		this.nom = n;
		this.CaracPremisses = ensemblesPremisses;
		this.CaracConclusion = ensembleConclusion;
		this.imp = alors;
	}

	/**
	 * Interface
	 */

	public FuzzySubSet MP(final List<Double> observations) {

		// System.out.println("##Produit cartesien des premisses : ");
		final FuzzySubSet premisse = this.prodCartObs(this.CaracPremisses,
				observations);
		// premisse.printValue();
		// System.out.println();

		// System.out.println("##Calcul implication : ");
		final FuzzySubSet conclusion = this.imp.r(premisse,
				this.CaracConclusion);
		// conclusion.printValue();
		// System.out.println();

		return conclusion;
	}

	/**
	 * Primitives
	 */
	private FuzzySubSet appliqueObs(final double x0, final FuzzySubSet a) {
		// System.out.println("-> Calculs de l' observation de " + x0
		// + " sur le sef " + a.nomEnsemble + ", " + a.nomSousEnsemble);
		final double inf = a.ensembleDefinition.getX();
		final double sup = a.ensembleDefinition.getY();
		final double a0 = a.getDegre(x0);
		final TreeSet<Couple> fa0 = new TreeSet<Couple>();

		final Couple p1 = new Couple(inf, a0);
		final Couple p2 = new Couple(sup, a0);

		fa0.add(p1);
		fa0.add(p2);
		// System.out.println("  * Les points obtenus sont : [" + p1.toString()
		// + "; " + p2.toString() + "]");
		// System.out.println();
		return new FuzzySubSet(fa0, a.ensembleDefinition, a.nomEnsemble, "["
				+ a.nomSousEnsemble + "=" + x0 + "]", a.unite);
	}

	private FuzzySubSet prodCartObs(final List<FuzzySubSet> s,
			final List<Double> observations) {

		if (s.size() != observations.size() || s.size() == 0) {
			LogService.writeException(this, "ERREUR # premisse : "
					+ s.size() + " != #nombre obs : " + observations.size()
					+ "... exiting");
			System.exit(-1);
		}

		final Iterator<FuzzySubSet> itS = s.iterator();
		final Iterator<Double> itO = observations.iterator();

		if (!itS.hasNext() || !itO.hasNext()) {
			LogService.writeException(this,
					"Erreur dans la construction... exiting");
			System.exit(-1);
		}
		FuzzySubSet eProd = this.appliqueObs(itO.next().doubleValue(), itS
				.next());

		while (itS.hasNext() && itO.hasNext()) {
			eProd = eProd.sousEnsembleInter(this.appliqueObs(itO.next()
					.doubleValue(), itS.next()));
		}

		return eProd;
	}

	@Override
	public String toString() {
		return "Regle " + this.nom + " : " + this.CaracPremisses + " => "
				+ this.CaracConclusion;
	}
}
