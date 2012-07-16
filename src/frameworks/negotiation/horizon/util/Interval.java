package frameworks.negotiation.horizon.util;

import dima.support.GimaObject;
import frameworks.negotiation.horizon.util.EmptyInterval.EmptyIntervalException;

/**
 * Represents an interval of values of type T. Comparison method of compareTo
 * can be specified among those in the enumeration Interval.Order. Note that
 * these Intervals include their both bounds.
 * 
 * @param <T>
 *            type of the interval, must extend Comparable<T> & Serializable.
 * 
 * @author Vincent Letard
 */
public class Interval<T extends Comparable<T>> extends GimaObject {
	/**
	 * Serial version identifier.
	 */
	private static final long serialVersionUID = -2768862502506003799L;

	/**
	 * Lower bound.
	 */
	private final T inf;

	/**
	 * Upper bound.
	 */
	private final T sup;

	// /**
	// * Sort order chosen.
	// */
	// private final Order order;

	/**
	 * Inclusion of bounds.
	 */
	private final Inclusion inclusion;

	/**
	 * Order to be used while comparing Interval<T>
	 * 
	 * @author Vincent Letard
	 */
	public enum Order {
		/**
		 * Lexicographic, lower bound most important.
		 */
		lexInf,
		/**
		 * Lexicographic, upper bound most important.
		 */
		lexSup,
		/**
		 * Strict order (any non disjoint intervals are not distinguishable)
		 */
		strict
	}

	/**
	 * Possible inclusions of the bounds.
	 * 
	 * @author Vincent Letard
	 */
	public enum Inclusion {
		/**
		 * Both bounds are included.
		 */
		bothIncluded,
		/**
		 * Both bounds are excluded.
		 */
		bothExcluded,
		/**
		 * Lower excluded, upper included.
		 */
		infExcluded,
		/**
		 * Lower included, upper excluded.
		 */
		supExcluded
	}

	/**
	 * Define an Interval between inf and sup bounds with the specified sort
	 * order and inclusion of bounds.
	 * 
	 * @param <Type>
	 *            Type of values of the Interval
	 * @param inf
	 *            Lower bound
	 * @param sup
	 *            Upper bound
	 * @param inclusion
	 *            Inclusion of the bounds
	 * @throws IllegalArgumentException
	 *             if the bounds are not valid (inf > sup)
	 * @return the new Interval built
	 */
	public static <Type extends Comparable<Type>> Interval<Type> newInterval(
			final Type inf, final Type sup, final Inclusion inclusion)
					throws IllegalArgumentException {
		final int cmp = inf.compareTo(sup);
		if (cmp > 0) {
			throw new IllegalArgumentException();
		} else if (cmp == 0 && inclusion != Inclusion.bothIncluded) {
			return (Interval<Type>) EmptyInterval.EMPTY_INTERVAL;
		} else {
			return new Interval<Type>(inf, sup, inclusion);
		}
	}

	/**
	 * Private constructor designed be used by the factory method newInterval.
	 * 
	 * @param inf
	 *            Lower bound of the Interval
	 * @param sup
	 *            Upper bound of the Interval
	 * @param inclusion
	 *            Inclusion of bounds
	 */
	protected Interval(final T inf, final T sup, final Inclusion inclusion) {
		if (inf.compareTo(sup) > 0) {
			throw new IllegalArgumentException(inf + ">" + sup);
		}
		this.inf = inf;
		this.sup = sup;
		// this.order = order;
		this.inclusion = inclusion;
	}

	/**
	 * Tests whether this Interval is empty.
	 * 
	 * @return <code>true</code> if the interval is empty.
	 */
	public boolean isEmpty() {
		return false;
	}

	/**
	 * Tests this Interval and the argument for equality.
	 * 
	 * @return <code>true</code> of and only if the argument has the same
	 *         bounds, ordering and inclusion.
	 */
	@Override
	public boolean equals(final Object obj) {
		if (obj.getClass().equals(Interval.class)) {
			final Interval<T> compared = (Interval<T>) obj;
			return this.inf.equals(compared.inf)
					&& this.sup.equals(compared.sup)
					// && this.order == compared.order
					&& this.inclusion == compared.inclusion;
		} else {
			return false;
		}
	}

	// /**
	// * Compares two Intervals according to the sort order chosen (must be the
	// * same for the current Interval and the argument).
	// *
	// * @param i
	// * Interval to match
	// * @return respectively -1, 0 or 1 whether the argument is greater,
	// equals,
	// * or less than the current Interval.
	// * @throws IllegalArgumentException
	// * if the sort ordering of the argument is not the same as for
	// * this Interval
	// * @throws EmptyIntervalException
	// * if attempt to perform the method compareTo is done with an
	// * EmptyInterval
	// */
	// @Override
	// public int compareTo(Interval<T> i) throws IllegalArgumentException,
	// EmptyIntervalException {
	// if (this.order != i.order)
	// throw new IllegalArgumentException();
	//
	// int cmp;
	// switch (this.order) {
	// case lexInf:
	// cmp = this.inf.compareTo(i.inf);
	// if (0 != cmp)
	// return cmp;
	// else
	// return this.sup.compareTo(i.sup);
	//
	// case lexSup:
	// cmp = this.sup.compareTo(i.sup);
	// if (0 != cmp)
	// return cmp;
	// else
	// return this.inf.compareTo(i.inf);
	// case strict:
	// cmp = this.sup.compareTo(i.inf);
	// if (cmp < 0)
	// return cmp;
	// cmp = this.inf.compareTo(i.sup);
	// if (cmp > 0)
	// return cmp;
	// return 0;
	// default:
	// throw new RuntimeException("Sort method lacking for " + this.order
	// + " order.");
	// }
	// }

	/**
	 * Gives the value of the lower bound of this Interval.
	 * 
	 * @return the lower bound of the interval
	 * @throws EmptyIntervalException
	 *             if the action could not be performed
	 */
	public T getLower() throws EmptyIntervalException {
		return this.inf;
	}

	/**
	 * Tests whether the argument belongs to this Interval.
	 * 
	 * @param value
	 *            the value to be matched
	 * @return <code>true</code> if the value belongs to this Interval
	 */
	public boolean belongs(final T value) {
		switch (this.inclusion) {
		case bothIncluded:
			return value.compareTo(this.inf) >= 0
			&& value.compareTo(this.sup) <= 0;
		case bothExcluded:
			return value.compareTo(this.inf) > 0
					&& value.compareTo(this.sup) < 0;
		case infExcluded:
			return value.compareTo(this.inf) > 0
					&& value.compareTo(this.sup) <= 0;
		case supExcluded:
			return value.compareTo(this.inf) >= 0
			&& value.compareTo(this.sup) < 0;
		default:
			throw new RuntimeException();
		}
	}

	/**
	 * Gives the value of the upper bound of this Interval.
	 * 
	 * @return the upper bound of the interval
	 * @throws EmptyIntervalException
	 *             if the action could not be performed
	 */
	public T getUpper() throws EmptyIntervalException {
		return this.sup;
	}

	/**
	 * Provides a String representation of the object.
	 */
	@Override
	public String toString() {
		switch (this.inclusion) {
		case bothExcluded:
			return "]" + this.inf.toString() + ", " + this.sup.toString() + "[";
		case bothIncluded:
			return "[" + this.inf.toString() + ", " + this.sup.toString() + "]";
		case infExcluded:
			return "]" + this.inf.toString() + ", " + this.sup.toString() + "]";
		case supExcluded:
			return "[" + this.inf.toString() + ", " + this.sup.toString() + "[";
		default:
			throw new RuntimeException();
		}
	}

	/**
	 * Computes the intersection this Interval with the argument.
	 * 
	 * @param i
	 *            interval to intersect
	 * @return the resulting Interval for intersection
	 */
	public Interval<T> inter(final Interval<T> i) {
		if (i instanceof EmptyInterval) {
			return this;
			// } else if (!this.order.equals(i.order)) {
			// throw new IllegalArgumentException();
		} else if (this.sup.compareTo(i.inf) < 0
				|| this.inf.compareTo(i.sup) > 0) {
			return (Interval<T>) EmptyInterval.EMPTY_INTERVAL;
		} else if (this.inf.compareTo(i.inf) < 0) {
			if (this.sup.compareTo(i.sup) > 0) {
				return i;
			} else {
				if (this.inclusion == Inclusion.bothExcluded
						|| this.inclusion == Inclusion.supExcluded) {
					if (i.inclusion == Inclusion.bothExcluded
							|| i.inclusion == Inclusion.infExcluded) {
						return new Interval<T>(i.inf, this.sup,
								Inclusion.bothExcluded);
					} else {
						return new Interval<T>(i.inf, this.sup,
								Inclusion.supExcluded);
					}
				} else if (i.inclusion == Inclusion.bothExcluded
						|| i.inclusion == Inclusion.infExcluded) {
					return new Interval<T>(i.inf, this.sup,
							Inclusion.infExcluded);
				} else {
					return new Interval<T>(i.inf, this.sup,
							Inclusion.bothIncluded);
				}
			}
		} else {
			if (this.sup.compareTo(i.sup) < 0) {
				return this;
			} else {
				if (this.inclusion == Inclusion.bothExcluded
						|| this.inclusion == Inclusion.infExcluded) {
					if (i.inclusion == Inclusion.bothExcluded
							|| i.inclusion == Inclusion.supExcluded) {
						return new Interval<T>(this.inf, i.sup,
								Inclusion.bothExcluded);
					} else {
						return new Interval<T>(this.inf, i.sup,
								Inclusion.infExcluded);
					}
				} else if (i.inclusion == Inclusion.bothExcluded
						|| i.inclusion == Inclusion.supExcluded) {
					return new Interval<T>(this.inf, i.sup,
							Inclusion.supExcluded);
				} else {
					return new Interval<T>(this.inf, i.sup,
							Inclusion.bothIncluded);
				}
			}
		}
	}
}
