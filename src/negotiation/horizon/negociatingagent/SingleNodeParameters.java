package negotiation.horizon.negociatingagent;

import java.io.Serializable;

public class SingleNodeParameters implements Serializable {
    /**
     * Computation capacity in IPS.
     */
    private final int processor;
    /**
     * Amount of memory in ko.
     */
    private final int ram;

    public SingleNodeParameters(final int processor, final int ram) {
	assert (processor > 0 && ram > 0); // TODO Exception plutôt ici ?
	this.processor = processor;
	this.ram = ram;
    }

    public int getProcessor() {
	return processor;
    }

    public int getRam() {
	return ram;
    }
}
