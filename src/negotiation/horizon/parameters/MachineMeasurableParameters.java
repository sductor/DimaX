package negotiation.horizon.parameters;

import negotiation.horizon.util.Interval;

/**
 * This class contains the parameters that apply to a machine and are not
 * functional though some requirement might be desired.
 * 
 * @author Vincent Letard
 */
public class MachineMeasurableParameters implements MachineParameters {

	/**
	 * Serial version identifier.
	 */
	private static final long serialVersionUID = 9086156266126218206L;

	/**
	 * Level of security of the node.
	 */
	private final Interval<Integer> availability;

	/**
	 * Constructs a new MachineMeasurableParameters from the arguments.
	 * 
	 * @param availability
	 *            Disponibility of the node.
	 */
	public MachineMeasurableParameters(final Interval<Integer> availability) {
		this.availability = availability;
	}

	/**
	 * Returns the level of availability.
	 * 
	 * @return the value of the field availability.
	 */
	public Interval<Integer> getAvailability() {
		return this.availability;
	}

	/**
	 * Returns a String representation of this MachineMeasurableParameters.
	 */
	@Override
	public String toString() {
		return "[ availability =" + this.availability + "]";
	}

	/**
	 * Tests whether the minimal requirement for this measurable parameter is
	 * respected.
	 * 
	 * @param required
	 *            Parameters to match
	 * @return <code>true</code> if the minimal requirement for the argument is
	 *         respected.
	 */
	public boolean satisfies(final MachineMeasurableParameters required) {
		return this.availability.getLower() >= required.availability.getLower();
	}
}
