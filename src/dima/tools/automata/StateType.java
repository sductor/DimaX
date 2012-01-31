package dima.tools.automata;

/**
 * Insert the type's description here.
 * Creation date: (18/01/00 16:43:25)
 * @author: Gerard Rozsavolgyi
 */
public class StateType extends AutomataObject {
	/**
	 *
	 */
	private static final long serialVersionUID = 5454909132560495028L;
	String type; // Possibles types are : 'initial' 'final' 'ordinary'
	/**
	 * StateType constructor comment.
	 */
	public StateType(final String t) {
		super();
		this.type = t;
	}
	/**
	 *
	 */
	public static StateType finalType() {
		return new StateType("final");
	}
	/**
	 *
	 */
	public static StateType initialType() {
		return new StateType("initial");
	}
	/**
	 *
	 */
	public boolean isFinal() {

		return this.type.equals("final");
	}
	/**
	 *
	 */
	public boolean isInitial() {

		return this.type.equals("initial");
	}
	/**
	 *
	 */
	public static StateType normalType() {
		return new StateType("normal");
	}
	/**
	 * Returns a String that represents the value of this object.
	 * @return a string representation of the receiver
	 */
	@Override
	public String toString() {

		return this.type;
	}
}
