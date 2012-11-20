package frameworks.horizon.parameters;

import jtp.util.UnexpectedException;
import frameworks.horizon.util.EmptyInterval.EmptyIntervalException;
import frameworks.horizon.util.Interval;

/**
 * Gathers non functional parameters on the links.
 * 
 * @author Vincent Letard
 */
public class LinkMeasurableParameters implements LinkParameters {

	/**
	 * Serial version identifier.
	 */
	private static final long serialVersionUID = -9169815383093912070L;

	/**
	 * Indication on the packet loss rate for the current node (percentage).
	 */
	private final Interval<Float> packetLossRate;

	/**
	 * Packets routing delay in milliseconds.
	 */
	private final Interval<Integer> delay;

	/**
	 * Variation of the delay in milliseconds.
	 */
	private final Interval<Integer> jitter;

	/**
	 * @param packetLossRate
	 *            percentage of packetLoss
	 * @param delay
	 *            interval in milliseconds
	 * @param jitter
	 *            variation in milliseconds
	 */
	public LinkMeasurableParameters(final Interval<Float> packetLossRate,
			final Interval<Integer> delay, final Interval<Integer> jitter) {
		this.packetLossRate = packetLossRate;
		this.delay = delay;
		this.jitter = jitter;
	}

	/**
	 * @return the value of the field packetLossRate
	 */
	public Interval<Float> getPacketLossRate() {
		return this.packetLossRate;
	}

	/**
	 * @return the value of the field delay
	 */
	public Interval<Integer> getDelay() {
		return this.delay;
	}

	/**
	 * @return the value of the field jitter
	 */
	public Interval<Integer> getJitter() {
		return this.jitter;
	}

	/**
	 * Returns a String representation of this LinkMeasurableParameters.
	 */
	@Override
	public String toString() {
		return "(plr=" + this.packetLossRate + ", d=" + this.delay + ", j="
				+ this.jitter + ")";
	}

	/**
	 * Tests whether the parameters of this object are sufficient to satisfy the
	 * requested ones.
	 * 
	 * @param value
	 *            Levels of parameters to reach.
	 * @return <code>true</code> if the requested levels of parameters are
	 *         effectively satisfied here.
	 */
	public boolean satisfies(final LinkMeasurableParameters value) {
		try {
			return this.delay.getUpper() <= value.delay.getUpper()
					&& this.jitter.getUpper() <= value.jitter.getUpper()
					&& this.packetLossRate.getUpper() <= value.packetLossRate
					.getUpper();
		} catch (final EmptyIntervalException e) {
			assert false;
			throw new UnexpectedException(e);
		}
	}
}
