package dima.kernel.INAF.InteractionTools;

/**
 * Insert the type's description here.
 * Creation date: (17/03/2003 13:53:35)
 * @author: Tarek JARRAYA
 */


import java.io.Serializable;

import dima.tools.agentInterface.NamedAction;



public class ActionExp implements Serializable
{
	/**
	 *
	 */
	private static final long serialVersionUID = 6033511277278354906L;
	public NamedAction action;
	public Operator operator;
	public ActionExp actionExp;
	/**
	 * ActionExp constructor comment.
	 */
	public ActionExp() {
		super();
	}
	/**
	 * ActionExp constructor comment.
	 */
	public ActionExp(final NamedAction a)
	{
		super();
		this.setAction(a);
	}
	/**
	 * ActionExp constructor comment.
	 */
	public ActionExp(final NamedAction a, final Operator o, final ActionExp b)
	{
		super();
		this.setAction(a);
		this.setOperator(o);
		this.setActionExp(b);
	}
	/**
	 * ActionExp constructor comment.
	 */
	public ActionExp(final NamedAction a, final Operator o, final NamedAction b)
	{
		super();
		this.setAction(a);
		this.setOperator(o);
		this.setActionExp(b);
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (20/03/2003 10:12:49)
	 * @return dima.kernel.communicatingAgent.interactionTools.Action
	 */
	public NamedAction getAction() {
		return this.action;
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (20/03/2003 10:17:54)
	 * @return dima.kernel.communicatingAgent.interactionTools.ActionExp
	 */
	public ActionExp getActionExp() {
		return this.actionExp;
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (20/03/2003 10:12:49)
	 * @return dima.kernel.communicatingAgent.interactionTools.Operator
	 */
	public Operator getOperator() {
		return this.operator;
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (20/03/2003 10:12:49)
	 * @param newAction dima.kernel.communicatingAgent.interactionTools.Action
	 */
	public void setAction(final NamedAction newAction) {
		this.action = newAction;
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (20/03/2003 10:17:54)
	 * @param newActionExp dima.kernel.communicatingAgent.interactionTools.ActionExp
	 */
	public void setActionExp(final ActionExp newActionExp)
	{
		this.actionExp = newActionExp;
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (20/03/2003 10:17:54)
	 * @param newActionExp dima.kernel.communicatingAgent.interactionTools.ActionExp
	 */
	public void setActionExp(final NamedAction newAction)
	{
		this.actionExp = new ActionExp(newAction);
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (20/03/2003 10:12:49)
	 * @param newOperator dima.kernel.communicatingAgent.interactionTools.Operator
	 */
	public void setOperator(final Operator newOperator) {
		this.operator = newOperator;
	}
}
