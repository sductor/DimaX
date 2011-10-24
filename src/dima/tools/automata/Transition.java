package dima.tools.automata;

/**
 * Insert the type's description here.
 * Creation date: (17/01/00 20:22:37)
 * @author: Gerard Rozsavolgyi
 */
 import java.util.Enumeration;
import java.util.Vector;

import dima.tools.agentInterface.AbstractAction;
import dima.tools.agentInterface.AbstractCondition;
import dima.tools.agentInterface.NamedAction;
import dima.tools.agentInterface.NamedCondition;




public class Transition extends AutomataObject {
	/**
	 *
	 */
	private static final long serialVersionUID = 2917968249996064776L;
	Vector conditions;
	Vector actions;
	State  targetState;
public Transition(final AbstractCondition condition, final AbstractAction action, final State arrivalState) {
	this.conditions = new Vector();
	if (condition!=null) this.conditions.add(condition);
	this.actions = new Vector();
	if (action!=null) this.actions.add(action);
	this.targetState = arrivalState;
}
/**
 * Transition constructor comment.
 */
public Transition(final State s) {
	super();
	this.targetState = s;
	this.conditions = new Vector();
	this.actions = new Vector();
}
public Transition(final String scondition, final String saction, final State arrivalState) {
	final NamedCondition condition = new NamedCondition(scondition);
	final NamedAction action = new NamedAction(saction);
	this.conditions = new Vector();
	if (condition!=null) this.conditions.add(condition);
	this.actions = new Vector();
	if (action!=null) this.actions.add(action);
	this.targetState = arrivalState;
}
public Transition(final Vector c, final Vector a, final State arrivalState) {
	if (c == null) this.conditions = new Vector();
	else this.conditions = new Vector(c);
	if (a == null) this.actions = new Vector();
	else this.actions = new Vector(a);
	this.targetState = arrivalState;
}
/**
 * Insert the method's description here.
 * Creation date: (17/01/00 20:57:58)
 * @param newAction java.lang.String
 */
public void addAction(final AbstractAction newAction) {
	this.actions.add(newAction);
}
/**
 * Insert the method's description here.
 * Creation date: (17/01/00 20:57:58)
 * @param newCondition automata.AbstractCondition
 */
public void addCondition(final AbstractCondition newCondition) {
	this.conditions.add(newCondition);
}
/**
 * Insert the method's description here.
 * Creation date: (17/01/00 20:57:58)
 * @return java.lang.String
 */
public Vector getActions() {
	return this.actions;
}
/**
 * Insert the method's description here.
 * Creation date: (17/01/00 20:57:58)
 * @return automata.AbstractCondition
 */
public Vector getCondition() {
	return this.conditions;
}
/**
 * Insert the method's description here.
 * Creation date: (17/01/00 20:57:58)
 * @return automata.AbstractCondition
 */
public Vector getConditions() {
	return this.conditions;
}
/**
 * Insert the method's description here.
 * Creation date: (17/01/00 20:57:58)
 * @return automata.State
 */
public State getTargetState() {
	return this.targetState;
}
/**
 * Insert the method's description here.
 * Creation date: (17/07/00 19:20:39)
 * @param param Gdima.tools.agentInterface.AbstractAction
 */
public boolean isSatisfied(final Object context) {
	final Enumeration e = this.conditions.elements();
	while (e.hasMoreElements())
		if (!((AbstractCondition)e.nextElement()).isSatisfied(context)) return false;
	return true;
}
/**
 * Insert the method's description here.
 * Creation date: (17/07/00 19:20:39)
 * @param param Gdima.tools.agentInterface.AbstractAction
 */
public void performAction(final Object context) {
	final Enumeration e = this.actions.elements();
	while (e.hasMoreElements())
		((AbstractAction)e.nextElement()).execute(context);
}
/**
 * Insert the method's description here.
 * Creation date: (17/01/00 20:57:58)
 * @param newAction java.lang.String
 */
public void setAction(final AbstractAction newAction) {
	this.actions = new Vector();
	this.actions.add(newAction);
}
/**
 * Insert the method's description here.
 * Creation date: (17/01/00 20:57:58)
 * @param newAction java.lang.String
 */
public void setActions(final Vector newActions) {
	this.actions = new Vector(newActions);
}
/**
 * Insert the method's description here.
 * Creation date: (17/01/00 20:57:58)
 * @param newCondition automata.AbstractCondition
 */
public void setCondition(final AbstractCondition newCondition) {
	this.conditions = new Vector();
	this.conditions.add(newCondition);
}
/**
 * Insert the method's description here.
 * Creation date: (17/01/00 20:57:58)
 * @param newCondition automata.AbstractCondition
 */
public void setConditions(final Vector newConditions) {
	this.conditions = new Vector(newConditions);
}
/**
 * Insert the method's description here.
 * Creation date: (17/01/00 20:57:58)
 * @param newTargetState automata.State
 */
public void setTargetState(final State newTargetState) {
	this.targetState = newTargetState;
}
/**
 * Returns a String that represents the value of this object.
 * @return a string representation of the receiver
 */
@Override
public String toString() {

	return this.getConditions().toString()+"/"+this.getActions().toString();
}
}
