package dima.kernel.ProactiveComponents;

/**
 * Insert the type's description here.
 * Creation date: (25/07/00 19:57:12)
 * @author: Michel Quenault (Miq)
 */
public class CycledATNBasedProactiveComponent extends ATNBasedProactiveComponent {

	/**
	 *
	 */
	private static final long serialVersionUID = -2669987078657844427L;
	/**
	 * Insert the method's description here.
	 * Creation date: (25/07/00 20:04:03)
	 */
	public CycledATNBasedProactiveComponent() {}
	/**
	 * CycledATNBasedProactiveComponent constructor comment.
	 * @param a Gdima.tools.automata.ATN
	 */
	public CycledATNBasedProactiveComponent(final dima.tools.automata.ATN a) {
		super(a);
	}
	/**
	 * isActive method comment.
	 */
	@Override
	public boolean isActive() {
		return true;
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (25/07/00 19:53:20)
	 */
	public void setInitialState() {
		this.currentState = this.atn.getInitialState();
	}
	/**
	 * step method comment.
	 */
	@Override
	public void step() {
		if (this.currentState.isFinal()) this.setInitialState();
		this.currentState = this.currentState.crossTransition(this);
	}
}
