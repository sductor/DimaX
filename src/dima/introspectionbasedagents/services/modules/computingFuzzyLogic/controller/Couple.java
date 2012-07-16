package dima.introspectionbasedagents.services.modules.computingFuzzyLogic.controller;

import java.io.Serializable;

/**
 *
 * @author Sylvain Ductor
 */
public class Couple implements Comparable<Couple>, Serializable {

	/**
	 *
	 */
	private static final long serialVersionUID = -8705265824307448859L;
	private final double x;
	private final double y;

	public Couple(final double a, final double b) {
		this.x = a;
		this.y = b;
	}

	public Couple(final Double a, final Double b) {
		this.x = a.doubleValue();
		this.y = b.doubleValue();
	}

	public double getX() {
		return this.x;
	}

	public double getY() {
		return this.y;
	}

	/*
	 * public boolean inferieur(Couple b){ return
	 * Double.compare(this.getY(),b.getX()); }
	 */

	@Override
	public int compareTo(final Couple b) {// Pour trier sur les abscisses
		// croissantes
		final Double x1 = new Double(this.getX());
		final Double x2 = new Double(b.getX());

		return Double.compare(x1, x2);
	}

	@Override
	public String toString() {
		return "(" + this.x + "," + this.y + ")";
	}
}
