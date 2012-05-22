package negotiation.horizon.parameters;

import negotiation.horizon.Interval;

public class MachineMeasurableParameters implements MachineParameters {

    /**
     * Serial version identifier.
     */
    private static final long serialVersionUID = 9086156266126218206L;

    /**
     * Level of security of the node.
     */
    private final Interval<Integer> availability;

    public MachineMeasurableParameters(final Interval<Integer> availability) {
	this.availability = availability;
    }

    public Interval<Integer> getAvailability() {
	return this.availability;
    }

    @Override
    public String toString() {
	return "[ availability =" + this.availability + "]";
    }
}
