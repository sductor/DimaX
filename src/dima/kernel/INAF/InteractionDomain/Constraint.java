package dima.kernel.INAF.InteractionDomain;

/**
 * Insert the type's description here.
 * Creation date: (01/04/03 01:36:50)
 * @author: Tarek JARRAYA
 */

import dima.basicagentcomponents.AgentIdentifier;
import dima.kernel.INAF.InteractionTools.Operator;

public class Constraint
{
	public Operator operator;
	public Object objectValue;
	/**
	 * Constraint constructor comment.
	 */

	public Constraint()
	{
		super();
	}
	/**
	 * Constraint constructor comment.
	 */
	public Constraint(final Object o, final Operator op)
	{
		super();
		this.setObjectValue(o);
		this.setOperator(op);
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (04/04/2003 18:43:18)
	 * @return java.lang.Object
	 */
	public java.lang.Object getObjectValue()
	{
		return this.objectValue;
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (04/04/2003 18:43:18)
	 * @return dima.kernel.communicatingAgent.interactionTools.Operator
	 */
	public Operator getOperator()
	{
		return this.operator;
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (01/04/03 01:38:32)
	 */
	public boolean isSatisfied(final AgentIdentifier proposer)
	{
		if (this.objectValue instanceof AbstractService) {
			if(this.operator.isEqual()) {
				return proposer.equals(this.objectValue);
			} else {
				return !proposer.equals(this.objectValue);
			}
		}
		return true;
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (01/04/03 01:38:32)
	 */
	public boolean isSatisfied(final AbstractService serv)
	{
		if (serv.getClass().isInstance(this.objectValue)) {
			if(this.operator.isEqual()) {
				return serv.equals((AbstractService)this.objectValue);
			} else {
				return !serv.equals((AbstractService)this.objectValue);
			}
		}

		return true;
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (04/04/2003 18:43:18)
	 * @param newObjectValue java.lang.Object
	 */
	public void setObjectValue(final java.lang.Object newObjectValue)
	{
		this.objectValue = newObjectValue;
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (04/04/2003 18:43:18)
	 * @param newOperator dima.kernel.communicatingAgent.interactionTools.Operator
	 */
	public void setOperator(final Operator newOperator)
	{
		this.operator = newOperator;
	}
}
