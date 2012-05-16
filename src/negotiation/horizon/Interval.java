package negotiation.horizon;

import java.io.Serializable;

import dima.support.GimaObject;

/**
 * Represents an interval of values of type T. Comparison method of compareTo
 * can be specified among those in the enumeration Interval.Order
 * 
 * @param <T>
 *            type of the interval, must extend Comparable<T> & Serializable.
 * 
 * @author Vincent Letard
 */
public class Interval<T extends Comparable<T> & Serializable> extends
	GimaObject implements Comparable<Interval<T>> {
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

    /**
     * Sort order chosen.
     */
    private final Order order;

    /**
     * Order to be used while comparing Interval<T>
     * 
     * @author Vincent Letard
     */
    public enum Order {
	lexInf, lexSup
    }

    /**
     * Define an Interval between inf and sup bounds.
     * 
     * @param inf
     *            Lower bound of the Interval
     * @param sup
     *            Upper bound of the Interval
     * @param order
     *            Sort ordering
     */
    public Interval(final T inf, final T sup, final Order order) {
	if (inf.compareTo(sup) > 0)
	    throw new IllegalArgumentException();
	this.inf = inf;
	this.sup = sup;
	this.order = order;
    }

    @Override
    public boolean equals(final Object obj) {
	if (obj.getClass().equals(Interval.class)) {
	    return this.inf.equals(((Interval<T>) obj).inf)
		    && this.sup.equals(((Interval<T>) obj).sup);
	} else
	    return false;
    }

    private boolean a;

    @Override
    public int compareTo(Interval<T> i) {
	int cmp;
	switch (this.order) {
	case lexInf:
	    cmp = this.inf.compareTo(i.inf);
	    if (0 != cmp)
		return cmp;
	    else
		return this.sup.compareTo(i.sup);

	case lexSup:
	    cmp = this.sup.compareTo(i.sup);
	    if (0 != cmp)
		return cmp;
	    else
		return this.inf.compareTo(i.inf);

	default:
	    throw new RuntimeException("Sort method lacking for " + this.order
		    + " order.");
	}
    }

    /**
     * Gives the value of the lower bound of this Interval.
     * 
     * @return the lower bound of the interval
     */
    public T getLower() {
	return this.inf;
    }

    /**
     * Gives the value of the upper bound of this Interval.
     * 
     * @return the upper bound of the interval
     */
    public T getUpper() {
	return this.sup;
    }

    @Override
    public String toString() {
	return "[" + this.inf.toString() + ", " + this.sup.toString() + "]";
    }
}
