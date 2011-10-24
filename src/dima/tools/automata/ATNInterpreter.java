package dima.tools.automata;

/**
 * Insert the type's description here.
 * Creation date: (18/01/00 16:32:57)
 * @author: Gerard Rozsavolgyi
 */

public class ATNInterpreter extends AutomataObject {
	/**
	 *
	 */
	private static final long serialVersionUID = -7423435022573608735L;
	ATN atn;
	Object context;
/**
 * ATNInterpreter constructor comment.
 */
public ATNInterpreter(final ATN a, final Object c) {
	super();
	this.initialize(a,c);
}
/**
 * Insert the method's description here.
 * Creation date: (28/03/2003 15:37:15)
 * @return Gdima.tools.automata.ATN
 */
public ATN getAtn() {
	return this.atn;
}
/**
 * Insert the method's description here.
 * Creation date: (28/03/2003 15:37:15)
 * @return java.lang.Object
 */
public java.lang.Object getContext() {
	return this.context;
}
/**
 * ATNInterpreter constructor comment.
 */
public void initialize(final ATN a, final Object c)
{
	this.atn = a;
	this.context = c;
}
public void run()
{
	State currentState = this.atn.getInitialState();

		while (!currentState.isFinal())
	currentState = currentState.crossTransition(this.context);

}
/**
 * Insert the method's description here.
 * Creation date: (28/03/2003 15:37:15)
 * @param newAtn Gdima.tools.automata.ATN
 */
public void setAtn(final ATN newAtn) {
	this.atn = newAtn;
}
/**
 * Insert the method's description here.
 * Creation date: (28/03/2003 15:37:15)
 * @param newContext java.lang.Object
 */
public void setContext(final java.lang.Object newContext) {
	this.context = newContext;
}
/*interpreter
	| etat |
	etat := etatInitial.
	[etat identique: etatFinal]
		whileFalse:
			[ etat := self transitionAt: etat. Processor yield.
			"etat := self determinerNouvelEtat "].

*/
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
