package dima.tools.automata;

/**
 * Superclass of all kind of automata related objects.
 * Creation date: (17/01/00 19:36:14)
 * @author: Gerard Rozsavolgyi
 */
public abstract class AutomataObject extends dima.support.GimaObject {
/**
	 *
	 */
	private static final long serialVersionUID = 5209237558843873584L;
/**
 * AutomataObject constructor comment.
 */
public AutomataObject() {
	super();
}
/**
 * Returns a String that represents the value of this object.
 * @return a string representation of the receiver
 */
@Override
public String toString() {
	// Insert code to print the receiver here.
	// This implementation forwards the message to super. You may replace or supplement this.
	return super.toString();
}
}
