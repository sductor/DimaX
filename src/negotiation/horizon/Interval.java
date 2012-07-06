package negotiation.horizon;

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
public class Interval<T extends Comparable<T>> extends GimaObject implements
	Comparable<Interval<T>> {
    /**
     * Serial version identifier.
     */
    private static final long serialVersionUID = -2768862502506003799L;

    private final static Interval<?> EMPTY_INTERVAL = new Interval<Integer>();

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
	lexInf, lexSup, strict
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

    private Interval() {
	this.inf = null;
	this.sup = null;
	this.order = null;
    }

    public boolean isEmpty() {
	return this == EMPTY_INTERVAL;
    }

    @Override
    public boolean equals(final Object obj) {
	if (obj.getClass().equals(Interval.class)) {
	    final Interval compared = (Interval) obj;
	    if (this == EMPTY_INTERVAL) {
		if (compared == EMPTY_INTERVAL)
		    return true;
		else
		    return false;
	    } else if (compared == EMPTY_INTERVAL) {
		return false;
	    }
	    return this.inf.equals(compared.inf)
		    && this.sup.equals(compared.sup);
	} else
	    return false;
    }

    @Override
    public int compareTo(Interval<T> i) {
	if (this == EMPTY_INTERVAL || i == EMPTY_INTERVAL)
	    return 0;

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
	case strict:
	    cmp = this.sup.compareTo(i.inf);
	    if (cmp < 0)
		return cmp;
	    cmp = this.inf.compareTo(i.sup);
	    if (cmp > 0)
		return cmp;
	    return 0;
	default:
	    throw new RuntimeException("Sort method lacking for " + this.order
		    + " order.");
	}
    }

    /**
     * Gives the value of the lower bound of this Interval.
     * 
     * @return the lower bound of the interval
     * @throws EmptyIntervalException
     */
    public T getLower() throws EmptyIntervalException {
	if (this == EMPTY_INTERVAL)
	    throw new EmptyIntervalException();
	return this.inf;
    }

    public boolean belongs(final T value) {
	if (this == EMPTY_INTERVAL)
	    return false;
	else
	    return value.compareTo(this.inf) > 0
		    && value.compareTo(this.sup) < 0;
    }

    /**
     * Gives the value of the upper bound of this Interval.
     * 
     * @return the upper bound of the interval
     * @throws EmptyIntervalException
     */
    public T getUpper() throws EmptyIntervalException {
	if (this == EMPTY_INTERVAL)
	    throw new EmptyIntervalException();
	return this.sup;
    }

    @Override
    public String toString() {
	if (this == EMPTY_INTERVAL)
	    return "[ empty ]";
	return "[" + this.inf.toString() + ", " + this.sup.toString() + "]";
    }

    public static <T extends Comparable<T>> Interval<T> inter(Interval<T> i1,
	    Interval<T> i2) {
	if (i1 == EMPTY_INTERVAL || i2 == EMPTY_INTERVAL
		|| i1.sup.compareTo(i2.inf) < 0 || i1.inf.compareTo(i2.sup) > 0) {
	    return (Interval<T>) EMPTY_INTERVAL;
	} else if (!i1.order.equals(i2.order)) {
	    throw new IllegalArgumentException();
	} else if (i1.inf.compareTo(i2.inf) < 0) {
	    if (i1.sup.compareTo(i2.sup) > 0)
		return i2;
	    else
		return new Interval<T>(i2.inf, i1.sup, i1.order);
	} else {
	    if (i1.sup.compareTo(i2.sup) < 0)
		return i1;
	    else
		return new Interval<T>(i1.inf, i2.sup, i1.order);
	}
    }
}
