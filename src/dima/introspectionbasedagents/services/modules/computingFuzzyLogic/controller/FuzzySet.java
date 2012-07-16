package dima.introspectionbasedagents.services.modules.computingFuzzyLogic.controller;

import java.io.Serializable;
import java.util.List;

/**
 *
 * @author Sylvain Ductor
 */
public class FuzzySet implements Serializable {

	/**
	 *
	 */
	private static final long serialVersionUID = -8094993489360232568L;
	public String nom;
	public String unite;
	public Couple borne;

	private final List<FuzzySubSet> partition;

	public FuzzySet(final String n, final String u, final Couple b,
			final List<FuzzySubSet> s) {
		this.nom = n;
		this.unite = u;
		this.borne = b;
		this.partition = s;
	}

	public FuzzySubSet getSEF(final int n) {
		return this.partition.get(n);
	}

	public int getNbSEF() {
		return this.partition.size();
	}

	public List<FuzzySubSet> getSubSets() {
		return this.partition;
	}
}
