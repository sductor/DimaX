package dima.kernel.BasicAgents;

import dima.basicagentcomponents.AgentIdentifier;
import dima.tools.automata.ATN;
import dima.tools.automata.State;

																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																public abstract class ATNBasedReactiveAgent extends  BasicReactiveAgent  {
	/**
																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																	 *
																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																	 */
																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																	private static final long serialVersionUID = -1887475275092318791L;
	public ATN atn;
	public State currentState;
/**
 * Insert the method's description here.
 * Creation date: (25/07/00 20:03:30)
 */
public ATNBasedReactiveAgent() {super();
}
/**
 * Insert the method's description here.
 * Creation date: (25/07/00 20:03:30)
 */
public ATNBasedReactiveAgent(final AgentIdentifier newId) {
	super(newId);
	}
/**
 * Insert the method's description here.
 * Creation date: (19/07/00 14:14:47)
 */
public ATNBasedReactiveAgent(final ATN a) {
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
