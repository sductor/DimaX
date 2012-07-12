package negotiation.horizon.util;

/**
 * This class represents empty intervals. It extends Interval<Integer> but can
 * be cast to any Interval<Type>.
 * 
 * @author Vincent Letard
 */
public final class EmptyInterval extends Interval<Integer> {

	/**
	 * Serial version identifier.
	 */
	private static final long serialVersionUID = 5701292064084337525L;

	/**
	 * The unique instance of EmptyInterval. No other is needed.
	 */
	public static final Interval<?> EMPTY_INTERVAL = new EmptyInterval();

	/**
	 * Constructs an Empty Interval.
	 */
	private EmptyInterval() {
		super(0, 0, Inclusion.bothExcluded);
	}

	/**
	 * Tests whether the argument belongs to this Interval.
	 * 
	 * @param value
	 *            the value to be matched
	 * @return <code>true</code> if the value belongs to this Interval
	 * @throws EmptyIntervalException
	 *             if the action could not be performed
	 */
	@Override
	public boolean belongs(final Integer value) {
		return false;
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
	// * @throws EmptyIntervalException
	// * if attempt to perform the method compareTo is done with an
	// * EmptyInterval
	// */
	// @Override
	// public int compareTo(Interval<Integer> i) throws EmptyIntervalException {
	// throw new EmptyIntervalException();
	// }

	/**
	 * Tests this Interval and the argument for equality.
	 * 
	 * @return <code>true</code> of and only if the argument has the same
	 *         bounds, ordering and inclusion.
	 */
	@Override
	public boolean equals(final Object obj) {
		return obj.getClass().equals(EmptyInterval.class);
	}

	/**
	 * Gives the value of the lower bound of this Interval.
	 * 
	 * @return the lower bound of the interval
	 * @throws EmptyIntervalException
	 *             if the action could not be performed
	 */
	@Override
	public Integer getLower() throws EmptyIntervalException {
		throw new EmptyIntervalException();
	}

	/**
	 * Gives the value of the upper bound of this Interval.
	 * 
	 * @return the upper bound of the interval
	 * @throws EmptyIntervalException
	 *             if the action could not be performed
	 */
	@Override
	public Integer getUpper() throws EmptyIntervalException {
		throw new EmptyIntervalException();
	}

	/**
	 * An EmptyInterval is empty...
	 */
	@Override
	public boolean isEmpty() {
		return true;
	}

	/**
	 * Provides a String representation of the object.
	 */
	@Override
	public String toString() {
		return "[empty]";
	}

	/**
	 * Computes the intersection this Interval with the argument.
	 * 
	 * @param i
	 *            interval to intersect
	 * @return the resulting Interval for intersection
	 */
	@Override
	public Interval<Integer> inter(final Interval<Integer> i) {
		return i;
	}

	/**
	 * Exception which should be thrown when an operation is unsupported because
	 * of an EmptyInterval.
	 * 
	 * @author Vincent Letard
	 */
	public class EmptyIntervalException extends RuntimeException {

		/**
		 * Serial version identifier.
		 */
		private static final long serialVersionUID = 6535577052009615349L;

	}
}
