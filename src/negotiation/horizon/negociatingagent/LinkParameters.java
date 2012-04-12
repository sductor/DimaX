package negotiation.horizon.negociatingagent;

import java.io.Serializable;

public class LinkParameters implements Serializable {
    /**
     * Indication on the packet loss rate for the current node (percentage).
     */
    private final float packetLossRate;

    /**
     * Packets routing delay in milliseconds.
     */
    private final int delay;

    /**
     * Variation of the delay in milliseconds.
     */
    private final int jitter;

    /**
     * Bit rate of the bandwidth in kbit/s.
     */
    private final int bandwidth;

    public LinkParameters(final float packetLossRate, final int delay,
	    final int jitter, final int bandwidth) {
	this.packetLossRate = packetLossRate;
	this.delay = delay;
	this.jitter = jitter;
	this.bandwidth = bandwidth;
    }

    public float getPacketLossRate() {
	return packetLossRate;
    }

    public int getDelay() {
	return delay;
    }

    public int getJitter() {
	return jitter;
    }

    public int getBandwidth() {
	return bandwidth;
    }

    @Override
    public String toString(){
	return "(plr=" + this.packetLossRate + ", d=" + this.delay + ", j="
		+ this.jitter + ", b=" + this.bandwidth + ")";
    }
}
