package dima.introspectionbasedagents.modules.distribution;

/**
 * Enum of symbolic value between 0 and 1
 * @author Sylvain Ductor
 *
 */
public enum ZeroOneSymbolicValue {
	Nul(0.),
	TresFaible(0.1),
	Faible(0.25),
	Moyen(0.5),
	Fort(0.75),
	Max(1);

	private double numericValue;

	private ZeroOneSymbolicValue(final double numericValue) {
		this.numericValue = numericValue;
	}

	public double getNumericValue() {
		return this.numericValue;
	}
}
