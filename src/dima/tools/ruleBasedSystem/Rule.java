package dima.tools.ruleBasedSystem;

import java.util.Vector;

import dima.tools.agentInterface.NamedAction;
import dima.tools.agentInterface.NamedCondition;



/**
 * Rule (list of conditions to verify, and corresponding list of actions to do). Used by classifier
 * Creation date: (22/05/00 17:25:52)
 * @author: Michel Quenault (Miq)
 */
public class Rule extends RuleBaseObject {
	/**
	 *
	 */
	private static final long serialVersionUID = -1425132089444144776L;
	private java.util.Vector conditions; // list of conditions to match to validate the rule
	private java.util.Vector actions; // list of actions to do if the rule is validate
/**
 * Insert the method's description here.
 * Creation date: (16/06/00 18:37:24)
 * @param con Gdima.tools.agentInterface.AbstractCondition
 * @param act Gdima.tools.agentInterface.AbstractAction
 */
public Rule() {
	super();

}
/**
 * Insert the method's description here.
 * Creation date: (16/06/00 18:37:24)
 * @param con Gdima.tools.agentInterface.AbstractCondition
 * @param act Gdima.tools.agentInterface.AbstractAction
 */
public Rule(final NamedCondition con, final NamedAction act) {
	super();
	this.conditions = new Vector();
	this.conditions.add(con);
	this.actions = new Vector();
	this.actions.add(act);
}
/**
 * Insert the method's description here.
 * Creation date: (22/05/00 19:24:36)
 * @param r Gdima.tools.classifier.Rule
 */
public Rule(final Rule r) {
	super();
	this.actions = r.actions;
	this.conditions = r.conditions;
}
/**
 * Insert the method's description here.
 * Creation date: (16/06/00 18:37:24)
 * @param con Gdima.tools.agentInterface.AbstractCondition
 * @param act Gdima.tools.agentInterface.AbstractAction
 */
public Rule(final String c, final String a) {
	super();
	final NamedCondition con = new NamedCondition (c);
	final NamedAction act = new NamedAction (a);
	this.conditions = new Vector();
	this.conditions.add(con);
	this.actions = new Vector();
	this.actions.add(act);
}
/**
 * Insert the method's description here.
 * Creation date: (16/06/00 18:37:24)
 * @param con Gdima.tools.agentInterface.AbstractCondition
 * @param act Gdima.tools.agentInterface.AbstractAction
 */
public Rule(final Vector con, final NamedAction act) {
	super();
	this.conditions = con;
	this.actions = new Vector();
	this.actions.add(act);
}
/**
 * Insert the method's description here.
 * Creation date: (16/06/00 18:37:24)
 * @param con Gdima.tools.agentInterface.AbstractCondition
 * @param act Gdima.tools.agentInterface.AbstractAction
 */
public Rule(final Vector con, final Vector act) {
	super();
	this.conditions = con;
	this.actions = act;
}
/**
 * Insert the method's description here.
 * Creation date: (16/06/00 15:49:25)
 */
public void executeWithoutTest() {
	final java.util.Enumeration e = this.actions.elements();
	while (e.hasMoreElements())
		((NamedAction)e.nextElement()).execute();
}
/**
 * Insert the method's description here.
 * Creation date: (16/06/00 15:49:25)
 */
public void executeWithoutTest(final Object ctxt) {
	final java.util.Enumeration e = this.actions.elements();
	while (e.hasMoreElements())
		((NamedAction)e.nextElement()).execute(ctxt);
}
/**
 * Insert the method's description here.
 * Creation date: (16/06/00 15:49:25)
 */
public void executeWithTest() {
	if (!this.isSatisfied()) return;
	// here, all conditions are respected
	this.executeWithoutTest();
}
/**
 * Insert the method's description here.
 * Creation date: (16/06/00 15:49:25)
 */
public void executeWithTest(final Object ctxt) {
	if (!this.isSatisfied(ctxt)) return;
	// here, all conditions are respected
	this.executeWithoutTest(ctxt);
}
/**
 * Insert the method's description here.
 * Creation date: (16/06/00 16:21:16)
 * @return boolean
 */
public boolean isSatisfied() {
	final java.util.Enumeration e = this.conditions.elements();
	while (e.hasMoreElements()) // for each condition in field conditions
		if (!((NamedCondition)e.nextElement()).isSatisfied()) return false; // is it in current condition set
	return true;
}
/**
 * Insert the method's description here.
 * Creation date: (16/06/00 16:21:16)
 * @return boolean
 */
public boolean isSatisfied(final Object ctxt) {
	final java.util.Enumeration e = this.conditions.elements();
	while (e.hasMoreElements()) // for each condition in field conditions
		if (!((NamedCondition)e.nextElement()).isSatisfied(ctxt))
		  return false; // is it in current condition set
	return true;
}
/**
 * Insert the method's description here.
 * Creation date: (25/05/00 16:59:36)
 * @return java.lang.String
 */
@Override
public String toString() {
	String s = new String("If ");
	java.util.Enumeration e = this.conditions.elements();
	s += e.nextElement().toString();
	while (e.hasMoreElements()) {
		s += " & ";
		s += e.nextElement().toString();
	}
	s += " then ";
	e = this.actions.elements();
	s += e.nextElement().toString();
	while (e.hasMoreElements()) {
		s += " & ";
		s += e.nextElement().toString();
	}
	return s;
}
}
