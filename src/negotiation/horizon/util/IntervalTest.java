package negotiation.horizon.util;

import junit.framework.TestCase;
import negotiation.horizon.util.Interval.Inclusion;

/**
 * TestCase for the class Interval
 * 
 * @author Vincent Letard
 */
public class IntervalTest extends TestCase {

	/**
	 * @param name
	 *            Name of the test to perform.
	 */
	public IntervalTest(final String name) {
		super(name);
	}

	/*
	 * Predefined bounds.
	 */
	public static final int bound1 = 0, bound2 = 1, bound3 = 2, bound4 = 3,
			bound5 = 4;

	// public void testCompareToLexInf() {
	// final Interval<Integer> med = Interval.newInterval(bound2, bound4,
	// Order.lexInf, Inclusion.bothIncluded), hi = Interval
	// .newInterval(bound2, bound5, Order.lexInf,
	// Inclusion.bothIncluded), big = Interval.newInterval(
	// bound1, bound5, Order.lexInf, Inclusion.bothIncluded), lo = Interval
	// .newInterval(bound1, bound4, Order.lexInf,
	// Inclusion.bothIncluded), up = Interval.newInterval(
	// bound5, bound5, Order.lexInf, Inclusion.bothIncluded), down = Interval
	// .newInterval(bound1, bound1, Order.lexInf,
	// Inclusion.bothIncluded);
	// assertTrue(med.compareTo(big) > 0);
	// assertTrue(med.compareTo(hi) < 0);
	// assertTrue(med.compareTo(lo) > 0);
	// assertTrue(med.compareTo(med) == 0);
	// }

	// public void testCompareToLexSup() {
	// final Interval<Integer> med = Interval.newInterval(bound2, bound4,
	// Order.lexSup, Inclusion.bothIncluded), hi = Interval
	// .newInterval(bound2, bound5, Order.lexSup,
	// Inclusion.bothIncluded), big = Interval.newInterval(
	// bound1, bound5, Order.lexSup, Inclusion.bothIncluded), lo = Interval
	// .newInterval(bound1, bound4, Order.lexSup,
	// Inclusion.bothIncluded), up = Interval.newInterval(
	// bound5, bound5, Order.lexSup, Inclusion.bothIncluded), down = Interval
	// .newInterval(bound1, bound1, Order.lexSup,
	// Inclusion.bothIncluded);
	// assertTrue(med.compareTo(big) < 0);
	// assertTrue(med.compareTo(hi) < 0);
	// assertTrue(med.compareTo(lo) > 0);
	// assertTrue(med.compareTo(med) == 0);
	// }

	// public void testCompareToStrict() {
	// final Interval<Integer> med = Interval.newInterval(bound2, bound4,
	// Order.strict, Inclusion.bothIncluded), hi = Interval
	// .newInterval(bound2, bound5, Order.strict,
	// Inclusion.bothIncluded), big = Interval.newInterval(
	// bound1, bound5, Order.strict, Inclusion.bothIncluded), lo = Interval
	// .newInterval(bound1, bound4, Order.strict,
	// Inclusion.bothIncluded), up = Interval.newInterval(
	// bound5, bound5, Order.strict, Inclusion.bothIncluded), down = Interval
	// .newInterval(bound1, bound1, Order.strict,
	// Inclusion.bothIncluded);
	// assertTrue(med.compareTo(big) == 0);
	// assertTrue(med.compareTo(hi) == 0);
	// assertTrue(med.compareTo(lo) == 0);
	// assertTrue(med.compareTo(med) == 0);
	// assertTrue(med.compareTo(up) < 0);
	// assertTrue(med.compareTo(down) > 0);
	// }

	/**
	 * Checks the behavior of the method isEmpty().
	 */
	public void testIsEmpty() {
		final Interval<Integer> i = Interval.newInterval(bound1, bound1,
				Inclusion.bothIncluded);
		assertTrue(EmptyInterval.EMPTY_INTERVAL.isEmpty());
		assertTrue(!i.isEmpty());
		assertTrue(Interval.newInterval(bound1, bound1, Inclusion.bothExcluded)
				.isEmpty());
		assertTrue(Interval.newInterval(bound1, bound1, Inclusion.infExcluded)
				.isEmpty());
		assertTrue(Interval.newInterval(bound1, bound1, Inclusion.supExcluded)
				.isEmpty());
	}

	/**
	 * Tests the results of intersection of Intervals.
	 */
	public void testInter() {
		final Interval<Integer> med = Interval.newInterval(bound2, bound4,
				Inclusion.bothIncluded), hi = Interval.newInterval(bound2,
						bound5, Inclusion.bothIncluded), lo = Interval.newInterval(
								bound1, bound4, Inclusion.bothIncluded), up = Interval
								.newInterval(bound5, bound5, Inclusion.bothIncluded), down = Interval
								.newInterval(bound1, bound1, Inclusion.bothIncluded);
		assertTrue(up.inter(down).isEmpty());
		assertTrue(med.inter(med).equals(med));
		assertTrue(hi.inter(lo).equals(med));
		assertTrue(up.inter(hi).equals(up));
	}

	/**
	 * Tests the results of the method belongs()
	 */
	public void testBelongs() {
		final Interval<Integer> hi = Interval.newInterval(bound2, bound5,
				Inclusion.bothIncluded), down = Interval.newInterval(bound1,
						bound1, Inclusion.bothIncluded);
		final Interval<Integer> empty = (Interval<Integer>) EmptyInterval.EMPTY_INTERVAL;
		assertTrue(!empty.belongs(3));
		assertTrue(!hi.belongs(bound1));
		assertTrue(hi.belongs(bound5));
		assertTrue(down.belongs(bound1));
	}
}
