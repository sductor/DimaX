package dima.kernel.communicatingAgent;

/**
 * Insert the type's description here.
 * Creation date: (31/03/00 12:25:50)
 * @author: Zahia Guessoum
 */

import dima.basicagentcomponents.AgentIdentifier;
import dima.tools.automata.ATN;
import dima.tools.automata.State;


public abstract class  ATNBasedCommunicatingAgent extends BasicCommunicatingAgent {
	/**
	 *
	 */
	private static final long serialVersionUID = -5601438209711600533L;
	public ATN atn;
	public State currentState;

/**
 * Insert the method's description here.
 * Creation date: (11/01/02 18:46:46)
 */
public ATNBasedCommunicatingAgent() {super();}
/**
 * Insert the method's description here.
 * Creation date: (11/01/02 18:46:46)
 */
public ATNBasedCommunicatingAgent(final AgentIdentifier newId) {super(newId);}
/**
 * Insert the method's description here.
 * Creation date: (11/01/02 18:46:46)
 */
public ATNBasedCommunicatingAgent(final AgentIdentifier newId, final ATN a)
  {super(newId);
  this.atn = a;
  this.currentState = this.atn.getInitialState();}
/**
 * Insert the method's description here.
 * Creation date: (21/06/2002 11:04:33)
 * @return Gdima.tools.automata.ATN
 */
public dima.tools.automata.ATN getAtn() {
	return this.atn;
}
/**
 * isActive method comment.
 */
@Override
public boolean isActive() {
	return !this.currentState.isFinal();}
/**
 * Insert the method's description here.
 * Creation date: (21/06/2002 11:04:33)
 * @param newAtn Gdima.tools.automata.ATN
 */
public void setAtn(final dima.tools.automata.ATN newAtn) {
	this.atn = newAtn;
	this.currentState = this.atn.getInitialState();
}
/**
 * step method comment.
 */
@Override
public void step() {
	this.currentState = this.currentState.crossTransition(this);
}

/**
 * Insert the method's description here.
 * Creation date: (21/06/2002 11:04:33)
 * @return Gdima.tools.automata.ATN
 */
@Override
public void noAction() {

}

/**
 * Insert the method's description here.
 * Creation date: (21/06/2002 11:04:33)
 * @return Gdima.tools.automata.ATN
 */
public boolean trueCondition() {
	return true;
}

public boolean trueCond() {
	return true;
}
}
