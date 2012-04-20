package dima.kernel.INAF.InteractionTools;

/**
 * Insert the type's description here.
 * Creation date: (17/03/03 20:22:37)
 * @author: Tarek JARRAYA
 */

import java.io.Serializable;
import java.util.Enumeration;
import java.util.Vector;

import dima.tools.agentInterface.NamedAction;
import dima.tools.agentInterface.NamedCondition;
import dima.tools.automata.AutomataObject;
import dima.tools.automata.State;




public class Transition2 extends AutomataObject implements Serializable
{
	/**
	 *
	 */
	private static final long serialVersionUID = -2062320491412139365L;
	public ConditionExp conditionExp;
	public ActionExp actionExp;
	public 	State  targetState;
	/**
	 * Insert the method's description here.
	 * Creation date: (21/03/2003 16:53:51)
	 */
	public Transition2()
	{
		super();
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (21/03/2003 16:53:51)
	 */
	public Transition2(final ConditionExp newConditionExp, final ActionExp newActionExp, final State arrivalState)
	{
		super();
		this.setConditionExp(newConditionExp);
		this.setActionExp(newActionExp);
		this.setTargetState(arrivalState);
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (21/03/2003 16:53:51)
	 */
	public Transition2(final NamedCondition c, final NamedAction a, final State arrivalState)
	{
		super();
		this.setConditionExp(new ConditionExp(c));
		this.setActionExp(new ActionExp(a));
		this.setTargetState(arrivalState);
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (21/03/2003 16:53:51)
	 */
	public Transition2(final NamedCondition c, final State arrivalState)
	{
		super();
		this.setConditionExp(new ConditionExp(c));
		this.setTargetState(arrivalState);
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (21/03/2003 16:53:51)
	 */
	public Transition2(final State state)
	{
		this.setTargetState(state);
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (21/03/2003 16:53:24)
	 * @return dima.kernel.communicatingAgent.interactionTools.ActionExp
	 */
	public ActionExp getActionExp() {
		return this.actionExp;
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (23/03/03 21:03:37)
	 * @return java.util.Vector
	 */
	public Vector getActions()
	{
		final Vector actions = new Vector();

		ActionExp exp = this.getActionExp();

		while (exp != null)
		{
			actions.add(exp.getAction());

			exp = exp.getActionExp();
		}

		return actions;
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (21/03/2003 16:53:24)
	 * @return dima.kernel.communicatingAgent.interactionTools.ConditionExp
	 */
	public ConditionExp getConditionExp() {
		return this.conditionExp;
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (27/03/2003 18:01:31)
	 * @return java.util.Vector
	 */
	public Vector getConditions()
	{
		final Vector conditions = new Vector();

		ConditionExp exp = this.getConditionExp();

		while (exp != null)
		{
			conditions.add(exp.getCondition());

			exp = exp.getConditionExp();
		}

		return conditions;
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (21/03/2003 16:54:36)
	 * @return Gdima.tools.automata.State
	 */
	public dima.tools.automata.State getTargetState() {
		return this.targetState;
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (23/03/03 20:49:47)
	 * @return boolean
	 */
	public boolean isSatisfied()
	{
		// System.out.println("ENTRER DANS SATISFIED ....");
		final Enumeration e = this.getConditions().elements();

		while(e.hasMoreElements()) {
			// System.out.println("ENTRER DANS NAMED CONDITION .....");
			if(!((NamedCondition)e.nextElement()).isSatisfied()) {
				return false;
			}
		}
		return true;
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (23/03/03 20:49:47)
	 * @return boolean
	 */
	public boolean isSatisfied(final Object context)
	{

		// System.out.println("ENTRER DANS SATISFIED CONTEXT...");
		// System.out.println(context);
		final Enumeration e = this.getConditions().elements();
		while(e.hasMoreElements()) {
			if(!((NamedCondition)e.nextElement()).isSatisfied(context)) {
				return false;
			}
		}
		return true;
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (23/03/03 20:50:28)
	 */
	public void performActions()
	{
		final Enumeration e = this.getActions().elements();

		while(e.hasMoreElements()) {
			((NamedAction)e.nextElement()).execute();
		}
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (21/03/2003 16:53:24)
	 * @param newActionExp dima.kernel.communicatingAgent.interactionTools.ActionExp
	 */
	public void setActionExp(final ActionExp newActionExp) {
		this.actionExp = newActionExp;
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (21/03/2003 16:53:24)
	 * @param newConditionExp dima.kernel.communicatingAgent.interactionTools.ConditionExp
	 */
	public void setConditionExp(final ConditionExp newConditionExp) {
		this.conditionExp = newConditionExp;
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (21/03/2003 16:54:36)
	 * @param newTargetState Gdima.tools.automata.State
	 */
	public void setTargetState(final dima.tools.automata.State newTargetState) {
		this.targetState = newTargetState;
	}

	/**
	 * Insert the method's description here.
	 * Creation date: (23/03/03 20:50:28)
	 */
	public void performActions(final Object cxt)
	{
		final Enumeration e = this.getActions().elements();

		while(e.hasMoreElements()) {
			((NamedAction)e.nextElement()).execute(cxt);
		}
	}
}
