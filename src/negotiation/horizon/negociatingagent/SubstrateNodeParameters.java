package negotiation.horizon.negociatingagent;

public class SubstrateNodeParameters extends SingleNodeParameters {
    /**
     * Serial version identifier.
     */
    private static final long serialVersionUID = 6315192671550234677L;
    /**
     * Amount of computation capacity currently available.
     */
    private int availableProcessor;

    /**
     * Amount of ram currently available.
     */
    private int availableRAM;

    /**
     * Associates each network interface of the substrate node with its link
     * parameters.
     */
    private LinkParameters[] netIfaces;

    public SubstrateNodeParameters(final int processor, final int ram,
	    final LinkParameters[] netIfaces) {
	super(processor, ram);
	this.availableProcessor = processor;
	this.availableRAM = ram;
	this.netIfaces = netIfaces; // TODO copie par référence sûre ?
	

	assert (this.availableProcessor >= 0 && this.availableRAM >= 0);
	// TODO assertions/exceptions
    }

    public int getAvailableProcessor() {
	return availableProcessor;
    }

    public void setAvailableProcessor(int allocatedProcessor) {
	this.availableProcessor = allocatedProcessor;
    }

    public int getAvailableRAM() {
	return availableRAM;
    }

    public void setAvailableRAM(int allocatedRAM) {
	this.availableRAM = allocatedRAM;
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

}
