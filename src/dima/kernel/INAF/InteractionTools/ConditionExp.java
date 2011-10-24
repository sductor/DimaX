package dima.kernel.INAF.InteractionTools;

/**
 * Insert the type's description here.
 * Creation date: (17/03/2003 13:18:18)
 * @author: Tarek JARRAYA
 */


import java.io.Serializable;

import dima.tools.agentInterface.NamedCondition;



public class ConditionExp implements Serializable
{
	/**
	 *
	 */
	private static final long serialVersionUID = 7210576902631538578L;
	public NamedCondition condition;
	public Operator operator;
	public ConditionExp nextConditionExp;
/**
 * ConditionExp constructor comment.
 */
public ConditionExp() {
	super();
}
/**
 * ConditionExp constructor comment.
 */
public ConditionExp(final NamedCondition c)
{
	super();
	this.setCondition(c);
}
/**
 * ConditionExp constructor comment.
 */
public ConditionExp(final NamedCondition a,final Operator op, final NamedCondition b)
{
	super();
	this.setCondition(a);
	this.setOperator(op);
	this.setNextConditionExp(new ConditionExp(b));
}
/**
 * Insert the method's description here.
 * Creation date: (17/03/2003 14:19:00)
 * @return dima.kernel.communicatingAgent.interactionTools.Condition
 */
public NamedCondition getCondition() {
	return this.condition;
}
/**
 * Insert the method's description here.
 * Creation date: (17/03/2003 14:19:01)
 * @return dima.kernel.communicatingAgent.interactionTools.ConditionExp
 */
public ConditionExp getConditionExp() {
	return this.nextConditionExp;
}
/**
 * Insert the method's description here.
 * Creation date: (17/03/2003 14:19:01)
 * @return dima.kernel.communicatingAgent.interactionTools.Operator
 */
public Operator getOperator() {
	return this.operator;
}
/**
 * Insert the method's description here.
 * Creation date: (17/03/2003 14:19:00)
 * @param newCondition dima.kernel.communicatingAgent.interactionTools.Condition
 */
public void setCondition(final NamedCondition newCondition) {
	this.condition = newCondition;
}
/**
 * Insert the method's description here.
 * Creation date: (17/03/2003 14:19:01)
 * @param newNextConditionExp dima.kernel.communicatingAgent.interactionTools.ConditionExp
 */
public void setNextConditionExp(final ConditionExp newNextConditionExp) {
	this.nextConditionExp = newNextConditionExp;
}
/**
 * Insert the method's description here.
 * Creation date: (17/03/2003 14:19:01)
 * @param newOperator dima.kernel.communicatingAgent.interactionTools.Operator
 */
public void setOperator(final Operator newOperator) {
	this.operator = newOperator;
}
}
