package negotiation.horizon.parameters;

import jtp.util.UnexpectedException;
import negotiation.horizon.EmptyIntervalException;
import negotiation.horizon.Interval;

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
	return packetLossRate;
    }

    /**
     * @return the value of the field delay
     */
    public Interval<Integer> getDelay() {
	return delay;
    }

    /**
     * @return the value of the field jitter
     */
    public Interval<Integer> getJitter() {
	return jitter;
    }

    @Override
    public String toString() {
	return "(plr=" + this.packetLossRate + ", d=" + this.delay + ", j="
		+ this.jitter + ")";
    }

    public boolean satisfies(final LinkMeasurableParameters value) {
	try {
	    return this.delay.getUpper() <= value.delay.getUpper()
		    && this.jitter.getUpper() <= value.jitter.getUpper()
		    && this.packetLossRate.getUpper() <= value.packetLossRate
			    .getUpper();
	} catch (EmptyIntervalException e) {
	    assert false;
	    throw new UnexpectedException(e);
	}
    }
}
