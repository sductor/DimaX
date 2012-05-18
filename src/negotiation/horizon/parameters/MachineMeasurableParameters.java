package negotiation.horizon.parameters;

public class MachineMeasurableParameters implements MachineParameters {

    /**
     * Serial version identifier.
     */
    private static final long serialVersionUID = 9086156266126218206L;

    /**
     * Level of security of the node.
     */
    private final Integer availability;

    public MachineMeasurableParameters(final Integer availability) {
	this.availability = availability;
    }

    public Integer getAvailability() {
	return this.availability;
    }

    @Override
    public String toString() {
	return "[ availability =" + this.availability + "]";
    }
}
