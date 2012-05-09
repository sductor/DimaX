package negotiation.horizon.negociatingagent;

import negotiation.horizon.Interval;
import negotiation.negotiationframework.contracts.AbstractActionSpecification;
import dima.basicagentcomponents.AgentIdentifier;

/**
 * 
 * @author Vincent Letard
 */
public class SingleNodeParameters implements AbstractActionSpecification {

    /**
     * Serial version identifier.
     */
    private static final long serialVersionUID = 8142462973752131803L;

    /**
     * Computation capacity in IPS.
     * 
     * @uml.property name="processor"
     */
    private final Interval<Integer> processor;
    /**
     * Amount of memory in ko.
     * 
     * @uml.property name="ram"
     */
    private final Interval<Integer> ram;

    /**
     * Level of security of the node.
     * 
     * @uml.property name="security"
     */
    private final Interval<Integer> security;

    private final AgentIdentifier myAgentIdentifier;

    // /**
    // * Static parameters with null values.
    // *
    // * @uml.property name="nONE"
    // * @uml.associationEnd
    // */
    // public final static SingleNodeParameters NONE = new
    // SingleNodeParameters(0,
    // 0, 0);

    /**
     * Constructs a new instance of SingleNodeParameters.
     * 
     * @param processor
     *            Amount of processor in instructions per second
     * @param ram
     *            Amount of random access memory in ko
     * @throws IllegalArgumentException
     *             if the created instance is not valid.
     */
    public SingleNodeParameters(final Interval<Integer> processor,
	    final Interval<Integer> ram, final Interval<Integer> security,
	    final AgentIdentifier myAgentIdentifier) {
	this.processor = processor;
	this.ram = ram;
	this.security = security;
	this.myAgentIdentifier = myAgentIdentifier;
	if (!this.isValid())
	    throw new IllegalArgumentException();
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

    /**
     * @return the value of the field processor.
     * @uml.property name="processor"
     */
    public Interval<Integer> getProcessor() {
	return this.processor;
    }

    /**
     * @return the value of the field ram.
     * @uml.property name="ram"
     */
    public Interval<Integer> getRam() {
	return this.ram;
    }

    /**
     * @return the value of the field security.
     * @uml.property name="security"
     */
    public Interval<Integer> getSecurity() {
	return this.security;
    }

    /**
     * A instance of SingleNodeParameters is valid iff all parameters Intervals
     * are positive.
     * 
     * @return <code>true</code> or <code>false</code> whether the instance is
     *         valid.
     */
    public boolean isValid() {
	if (processor.getLower() < 0 || ram.getLower() < 0
		|| security.getLower() < 0) {
	    return false;
	} else
	    return true;
    }

    @Override
    public AgentIdentifier getMyAgentIdentifier() {
	return this.myAgentIdentifier;
    }

    @Override
    public String toString() {
	return "(proc=" + this.processor + ", ram=" + this.ram + ", secur="
		+ this.security + ")";
    }
}
