package dimaxx.tools.computingFuzzyLogic.controller;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import dimaxx.tools.computingFuzzyLogic.defuzzyficator.Defuzzificateur;





/**
 *
 * @author Sylvain Ductor
 */
public class FuzzySubSet extends FonctionAppartenance implements Serializable {

	/**
	 *
	 */
	private static final long serialVersionUID = -2677376113439257390L;
	public String nomEnsemble = "";
	public String nomSousEnsemble = "";
	public String unite = "";

	/**
	 * Constructeurs
	 */

	public FuzzySubSet(final TreeSet<Couple> f, final Couple def,
			final String nomE, final String nomS, final String unit) {
		super(f, def);
		this.nomEnsemble = nomE;
		this.nomSousEnsemble = nomS;
		this.unite = unit;
	}

	public FuzzySubSet(final TreeSet<Couple> f, final String nomE,
			final String nomS, final String unit) {
		super(f);
		this.nomEnsemble = nomE;
		this.nomSousEnsemble = nomS;
		this.unite = unit;
	}

	public FuzzySubSet(final TreeSet<Couple> f, final Couple def) {
		super(f, def);
	}

	public FuzzySubSet(final TreeSet<Couple> f) {
		super(f);
	}

	/**
	 * Interface
	 */

	public FuzzySubSet sousEnsembleInter(final FuzzySubSet b) {
		final TreeSet<Double> norm = this.normalise(b);
		final TreeSet<Couple> fMin = new TreeSet<Couple>();
		boolean aMin;// Indique si la courbe de A est couramment en dessous de
		// la courbe de B
		double x0, x1, ya0, yb0, ya1, yb1;
		Couple pa0, pb0, pa1, pb1;

		if (norm.size() == 0) {
			System.out.println("ERREUR # Le sef n'est pas défini");
			System.exit(-1);
		}

		final Iterator<Double> itAb = norm.iterator();
		x0 = itAb.next();
		ya0 = this.getDegre(x0);
		yb0 = b.getDegre(x0);
		pa0 = new Couple(x0, ya0);
		pb0 = new Couple(x0, yb0);

		aMin = ya0 < yb0;

		if (aMin)
			fMin.add(pa0);
		else
			fMin.add(pb0);

		while (itAb.hasNext()) {
			x1 = itAb.next();
			ya1 = this.getDegre(x1);
			yb1 = b.getDegre(x1);
			pa1 = new Couple(x1, ya1);
			pb1 = new Couple(x1, yb1);

			final Segment d1 = new Segment(pa0, pa1);
			final Segment d2 = new Segment(pb0, pb1);

			if (d1.estIntersecte(d2)) {
				final Couple z = d1.getIntersection(d2);

				if (z.getX() == pa1.getX()) {// 2eme extrémité commune
					// On ne fait rien : le prochain segment aura une 1ere
					// extrémité commune
				} else if (z.getX() == pa0.getX())
					aMin = pa1.getY() < pb1.getY();
				else {// Intersection stricte
					fMin.add(z);
					aMin = !aMin;
				}
			}

			if (aMin)
				fMin.add(pa1);
			else
				fMin.add(pb1);

			x0 = x1;
			pa0 = pa1;
			pb0 = pb1;
		}

		return new FuzzySubSet(fMin, this.nomEnsemble + " & " + b.nomEnsemble,
				this.nomSousEnsemble + " & " + b.nomSousEnsemble, this.unite
				+ " & " + b.unite);
	}

	public FuzzySubSet sousEnsembleUnion(final FuzzySubSet b) {
		final TreeSet<Double> norm = this.normalise(b);
		final TreeSet<Couple> fMax = new TreeSet<Couple>();
		boolean aMax;// Indique si la courbe de A est couramment en dessous de
		// la courbe de B
		double x0, x1, ya0, yb0, ya1, yb1;
		Couple pa0, pb0, pa1, pb1;

		if (norm.size() == 0) {
			System.out.println("ERREUR # Le sef n'est pas défini");
			System.exit(-1);
		}

		final Iterator<Double> itAb = norm.iterator();
		x0 = itAb.next();
		ya0 = this.getDegre(x0);
		yb0 = b.getDegre(x0);
		pa0 = new Couple(x0, ya0);
		pb0 = new Couple(x0, yb0);

		aMax = yb0 < ya0;

		if (aMax)
			fMax.add(pa0);
		else
			fMax.add(pb0);

		while (itAb.hasNext()) {
			x1 = itAb.next();
			ya1 = this.getDegre(x1);
			yb1 = b.getDegre(x1);
			pa1 = new Couple(x1, ya1);
			pb1 = new Couple(x1, yb1);

			final Segment d1 = new Segment(pa0, pa1);
			final Segment d2 = new Segment(pb0, pb1);

			if (d1.estIntersecte(d2)) {
				final Couple z = d1.getIntersection(d2);

				if (z.getX() == pa1.getX()) {// 2eme extrémité commune
					// On ne fait rien : le prochain segment aura une 1ere
					// extrémité commune
				} else if (z.getX() == pa0.getX())
					aMax = pb1.getY() < pa1.getY();
				else {// Intersection stricte
					fMax.add(z);
					aMax = !aMax;
				}
			}

			if (aMax)
				fMax.add(pa1);
			else
				fMax.add(pb1);

			x0 = x1;
			pa0 = pa1;
			pb0 = pb1;
		}

		return new FuzzySubSet(fMax, this.nomEnsemble + " | " + b.nomEnsemble,
				this.nomSousEnsemble + " | " + b.nomSousEnsemble, this.unite
				+ " | " + b.unite);
	}

	public double prodCart(final List<FuzzySubSet> s,
			final List<Double> abcisses) {
		double y = 1;

		final Iterator<FuzzySubSet> itS = s.iterator();
		final Iterator<Double> itO = abcisses.iterator();
		while (itS.hasNext() && itO.hasNext())
			y = Math.min(itS.next().getDegre(itO.next()), y);

		return y;
	}

	public FuzzySubSet soustraction(final double alpha) {

		final Iterator<Couple> itF = this.fonctionAppartenance.iterator();
		final Set<Couple> fResult = new HashSet<Couple>();

		while (itF.hasNext()) {
			Couple p = itF.next();
			final double x = p.getX();
			final double y = alpha - p.getY();
			p = new Couple(x, y);
			fResult.add(p);
		}

		final TreeSet<Couple> f = new TreeSet<Couple>(fResult);
		return new FuzzySubSet(f, this.ensembleDefinition, this.nomEnsemble,
				this.nomSousEnsemble, this.unite);
	}

	public double deFuzzification(final Defuzzificateur deFuzz) {
		return deFuzz.defuzz(this);
	}

	/**
	 * Affichage
	 */

	public void printValue() {
		String s = "";
		final Iterator<Couple> itF = this.fonctionAppartenance.iterator();
		while (itF.hasNext()) {
			final Couple p = itF.next();
			s = s + " ; " + p.toString();
		}

		System.out.println("-> Les point d'inflexion du " + this.toString()
				+ " sont :");
		System.out.println("  * [" + s.subSequence(3, s.length()) + "]");
	}

	@Override
	public String toString() {
		if (!this.nomEnsemble.equals("") || !this.nomSousEnsemble.equals(""))
			return "sef (" + this.nomEnsemble + ", " + this.nomSousEnsemble
					+ ")";
		else
			return "sef anonyme";
	}
}

class FonctionAppartenance implements Serializable {

	/**
	 *
	 */
	private static final long serialVersionUID = -2014614953692250164L;
	public TreeSet<Couple> fonctionAppartenance;// Coordonnée trier dans les x
	// positifs
	public Couple ensembleDefinition;// couple; !!!verifier énoncer!!!

	protected class Segment implements Serializable {
		/**
		 *
		 */
		private static final long serialVersionUID = 5639309085583446717L;
		// Extrémités
		Couple p;
		Couple q;

		// Equation
		double a;
		double b;

		public Segment(final Couple x0, final Couple y0) {
			this.p = x0;
			this.q = y0;

			this.a = (this.p.getY() - this.q.getY())
					/ (this.p.getX() - this.q.getX());
			this.b = this.p.getY() - this.a * this.p.getX();
		}

		public double degre(final double x) {
			return this.a * x + this.b;
		}

		public boolean estIntersecte(final Segment d2) {// Les segments sont
			// normalisé
			// (extrémité de mm
			// abscisse)
			if (this.p.getX() != d2.p.getX() || this.q.getX() != d2.q.getX()) {
				System.out
				.println("ERREUR # test d'intersection de segment non normalisés");
				System.exit(-1);
			}
			return Double.compare(this.p.getY(), d2.p.getY()) != Double
					.compare(this.q.getY(), d2.q.getY());
		}

		public Couple getIntersection(final Segment d2) {// Une intersection
			// existe
			if (this.p.getX() != d2.p.getX() || this.q.getX() != d2.q.getX()) {
				System.out
				.println("ERREUR # calcul d'intersection de segment non normalisés");
				System.exit(-1);
			}
			if (!this.estIntersecte(d2)) {
				System.out
				.println("ERREUR # calcul d'intersection d esegment disjoint");
				System.exit(-1);
			}
			final double x = (d2.b - this.b) / (this.a - d2.a);
			final double y = FonctionAppartenance.this.getDegre(x);

			return new Couple(x, y);
		}

		public boolean equals(final Segment d2) {// support égaux
			return this.a == d2.a && this.b == d2.b;
		}
	}

	/**
	 * Constructeurs
	 */

	public FonctionAppartenance(final TreeSet<Couple> f) {
		// Collections.sort(f);
		this.fonctionAppartenance = new TreeSet<Couple>(f);
		this.ensembleDefinition = new Couple(f.first().getX(), f.last().getX());
		// this.clean();
	}

	public FonctionAppartenance(final TreeSet<Couple> f, final Couple def) {
		// Collections.sort(f);
		this.fonctionAppartenance = new TreeSet<Couple>(f);
		this.ensembleDefinition = def;
		// this.clean();
	}

	/**
	 * Interface
	 */

	public double getDegre(final double x) {
		final Couple p = new Couple(x, 0);

		if (x <= this.fonctionAppartenance.first().getX())
			return this.fonctionAppartenance.first().getY();
		else if (x >= this.fonctionAppartenance.last().getX())
			return this.fonctionAppartenance.last().getY();
		else {
			final SortedSet<Couple> ssMin = this.fonctionAppartenance
					.headSet(p);
			final SortedSet<Couple> ssMax = this.fonctionAppartenance
					.tailSet(p);
			final Segment d = new Segment(ssMin.last(), ssMax.first());

			return d.degre(x);
		}
	}

	public Couple valeurMinNonNulle() {
		Couple min = new Couple(0, 1);

		final Iterator<Couple> itValeurs = this.fonctionAppartenance.iterator();
		while (itValeurs.hasNext()) {
			final Couple neo = itValeurs.next();
			if (neo.getY() > 0 && min.getY() > neo.getY())
				min = neo;
		}
		return min;
	}

	// Retourne le point minimum de la fonction
	public Couple valeurMin() {
		Couple min = new Couple(0, 1);

		final Iterator<Couple> itValeurs = this.fonctionAppartenance.iterator();
		while (itValeurs.hasNext()) {
			final Couple neo = itValeurs.next();
			if (min.getY() > neo.getY())
				min = neo;
		}
		return min;
	}

	// Retourne le point maximum de la fonction
	public Couple valeurMax() {
		Couple max = new Couple(0, 0);

		final Iterator<Couple> itValeurs = this.fonctionAppartenance.iterator();
		while (itValeurs.hasNext()) {
			final Couple neo = itValeurs.next();
			if (max.getY() <= neo.getY())
				max = neo;
		}
		return max;
	}

	/**
	 * Primitives
	 */

	// Retourne la des abscisses d'inflexion de A et B
	protected TreeSet<Double> normalise(final FonctionAppartenance b) {
		final TreeSet<Couple> union = new TreeSet<Couple>(
				this.fonctionAppartenance);
		union.addAll(b.fonctionAppartenance);

		final Set<Double> abscisses = new HashSet<Double>();

		final Iterator<Couple> itSet = union.iterator();
		while (itSet.hasNext()) {
			final Couple p = itSet.next();
			final double x = p.getX();
			abscisses.add(x);
		}

		return new TreeSet<Double>(abscisses);
	}

	/*
	 * //Supprime les doublon et les extrémités inutiles private void clean(){
	 * List<Couple> f_clear = new ArrayList<Couple>(); Segment init = new
	 * Segment(new Couple(0,0), new Couple(1,0)); Segment d0 = init; Couple p,
	 * q;
	 *
	 * if (fonctionAppartenance.size() > 1){ //Suppression des doublons et
	 * rognure au début d0 = init; Iterator<Couple> itF =
	 * fonctionAppartenance.iterator(); p = itF.next(); while (itF.hasNext()){ q
	 * = itF.next(); Segment d = new Segment(p,q); if
	 * (!d.equals(d0)){f_clear.add(p);}
	 *
	 * p = q; d0 = d; } //Mise à jour fonctionAppartenance = f_clear; }
	 *
	 * if (fonctionAppartenance.size() > 1){ //rognure à la fin p =
	 * fonctionAppartenance.get(f_clear.size() - 1); q =
	 * fonctionAppartenance.get(f_clear.size() - 2);
	 *
	 * d0 = new Segment(p,q); if
	 * (d0.equals(init)){fonctionAppartenance.remove(fonctionAppartenance.size()
	 * - 1);} }
	 *
	 * }
	 *
	 * public double getDegre(double x){ / Couple p0 = (Couple)
	 * fonctionAppartenance.get(0); if (x < p0.getX()){return 0;} else if (x ==
	 * p0.getX()){return p0.getY();} else{//x est strictement supérieur à p0
	 * Couple p1 = p0; Iterator<Couple> itF = fonctionAppartenance.iterator();
	 * while (itF.hasNext() && (p1.getX() < x)){ p0 = p1; p1 = (Couple)
	 * itF.next();
	 *
	 * if (x < p1.getX()){ Segment d = new Segment(p0,p1); return d.degre(x); }
	 * else if (x == p1.getX()){return p1.getY();} }
	 *
	 * return 0; }
	 *
	 * protected Map<Double,Couple> normalise(FonctionAppartenance b){ /
	 * List<Couple> fa = this.fonctionAppartenance; List<Couple> fb =
	 * b.fonctionAppartenance;
	 *
	 * HashMap<Double,Couple> union = new HashMap<Double,Couple>();
	 *
	 * Couple pa1; Couple pb1;
	 *
	 * int i = 0; int j = 0;
	 *
	 * while (i < fa.size() && j < fb.size()){// #### ||? &&!!!
	 * System.out.println("    -> i = "+i+", j = "+j); try{pa1 = (Couple)
	 * fa.get(i);} catch (Exception IndexOutOfBoundsException){pa1 = new
	 * Couple(((Couple) fb.get(j)).getX(),0);}
	 *
	 * try{pb1 = (Couple) fb.get(j);} catch (Exception
	 * IndexOutOfBoundsException){pb1 = new Couple(((Couple)
	 * fa.get(i)).getX(),0);}
	 *
	 *
	 *
	 * if (pa1.getX() < pb1.getX()){ double x = pa1.getX(); Couple p = new
	 * Couple(this.getDegre(x),b.getDegre(x)); union.put(x, p);
	 * System.out.println
	 * ("            Ajout de "+p.toString()+" à l'abscisse "+x); i++; } else{
	 * double x = pb1.getX(); Couple p = new
	 * Couple(this.getDegre(x),b.getDegre(x)); union.put(x, p);
	 * System.out.println
	 * ("            Ajout de "+p.toString()+" à l'abscisse "+x); j++; } }
	 *
	 * return union;
	 */
}
