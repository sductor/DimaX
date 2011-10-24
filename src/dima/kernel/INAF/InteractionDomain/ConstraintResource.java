/*
 * Created on 28 mai 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package dima.kernel.INAF.InteractionDomain;

import dima.kernel.INAF.InteractionTools.Operator;

/**
 * @author faci
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class ConstraintResource extends Constraint {

	public ConstraintResource()
	{
		super();
	}
	/**
	 * Constraint constructor comment.
	 */
	public ConstraintResource(final Resource o, final Operator op)
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

	public boolean isSatisfied(final Resource serv)
	{
		/* */ System.out.println("ENTRER DANS ISSATISF CONST RES....");
		if (serv.getClass().isInstance(this.objectValue)) // verifier que les deux objets sont de m�me type
		{

			/* */ System.out.println("ENTRER SATISF CONST RES OK .....");
			if(this.operator.isEqual())
				return serv.equals((Resource)this.objectValue);
			else if (this.operator.isLittleThan()) {
			/* */ System.out.println("ENTRER DANS ISSATIF DE CONSTRESOURCE....");
			return	serv.littleThan((Resource)this.objectValue);
			}
			else
			return !serv.equals((Resource)this.objectValue);
		}

		return true;
	}





}
