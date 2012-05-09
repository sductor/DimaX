package negotiation.horizon.negociatingagent;

import negotiation.horizon.Interval;
import dima.support.GimaObject;

/**
 * LinkParameters regroup the network parameters that are relative to the links
 * between nodes, or the network interfaces.
 * 
 * @author Vincent Letard
 */
public class LinkParameters extends GimaObject {
    /**
     * Indication on the packet loss rate for the current node (percentage).
     * 
     * @uml.property name="packetLossRate"
     */
    private final Interval<Float> packetLossRate;

    /**
     * Packets routing delay in milliseconds.
     * 
     * @uml.property name="delay"
     */
    private final Interval<Integer> delay;

    /**
     * Variation of the delay in milliseconds.
     * 
     * @uml.property name="jitter"
     */
    private final Interval<Integer> jitter;

    /**
     * Bit rate of the bandwidth in kbit/s.
     * 
     * @uml.property name="bandwidth"
     */
    private final Interval<Integer> bandwidth;

    public LinkParameters(final Interval<Float> packetLossRate,
	    final Interval<Integer> delay, final Interval<Integer> jitter,
	    final Interval<Integer> bandwidth) {
	this.packetLossRate = packetLossRate;
	this.delay = delay;
	this.jitter = jitter;
	this.bandwidth = bandwidth;
    }

    /**
     * @return
     * @uml.property name="packetLossRate"
     */
    public Interval<Float> getPacketLossRate() {
	return packetLossRate;
    }

    /**
     * @return
     * @uml.property name="delay"
     */
    public Interval<Integer> getDelay() {
	return delay;
    }

    /**
     * @return
     * @uml.property name="jitter"
     */
    public Interval<Integer> getJitter() {
	return jitter;
    }

    /**
     * @return
     * @uml.property name="bandwidth"
     */
    public Interval<Integer> getBandwidth() {
	return bandwidth;
    }

    @Override
    public String toString() {
	return "(plr=" + this.packetLossRate + ", d=" + this.delay + ", j="
		+ this.jitter + ", b=" + this.bandwidth + ")";
    }
}
