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

    /**
     * Static parameters with null values.
     */
    public final static SingleNodeParameters NONE = new SingleNodeParameters(0,
	    0);

    /**
     * Constructs a new instance of SingleNodeParameters.
     * 
     * @param processor
     *            Amount of processor in instructions per second
     * @param ram
     *            Amount of random access memory in ko
     */
    public SingleNodeParameters(final int processor, final int ram) {
	this.processor = processor;
	this.ram = ram;
    }

    // public SingleNodeParameters(
    // final Collection<SingleNodeParameters> positives,
    // final Collection<SingleNodeParameters> negatives) {
    // int proc = 0, ram = 0;
    // for (SingleNodeParameters pos : positives) {
    // proc += pos.getProcessor();
    // ram += pos.getRam();
    // }
    // for (SingleNodeParameters neg : negatives) {
    // proc -= neg.getProcessor();
    // ram -= neg.getRam();
    // }
    // this.processor = proc;
    // this.ram = ram;
    // }

    public int getProcessor() {
	return processor;
    }

    public int getRam() {
	return ram;
    }

    /**
     * A instance of SingleNodeParameters is valid iff processor and ram are
     * both positive.
     * 
     * @return <code>true</code> or <code>false</code> whether the instance is
     *         valid.
     */
    public boolean isValid() {
	if (processor < 0 || ram < 0) {
	    return false;
	} else
	    return true;
    }
}
