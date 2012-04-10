package dima.kernel.ProactiveComponents;

/**
 * Insert the type's description here.
 * Creation date: (19/07/00 14:12:25)
 * @author: Gerard Rozsavolgyi
 */
import dima.tools.automata.ATN;
import dima.tools.automata.State;
public class ATNBasedProactiveComponent extends ProactiveComponent {
	/**
	 *
	 */
	private static final long serialVersionUID = -298271599344359644L;
	public ATN atn;
	public State currentState;
	/**
	 * Insert the method's description here.
	 * Creation date: (25/07/00 20:03:30)
	 */
	public ATNBasedProactiveComponent() {
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (19/07/00 14:14:47)
	 */
	public ATNBasedProactiveComponent(final ATN a) {
		this.atn = a;
		this.currentState = this.atn.getInitialState();
	}
	/**
	 * isActive method comment.
	 */
	@Override
	public boolean isActive() {
		return !this.currentState.isFinal();}
	/**
	 * step method comment.
	 */
	@Override
	public void step() {
		this.currentState = this.currentState.crossTransition(this);
	}
}
