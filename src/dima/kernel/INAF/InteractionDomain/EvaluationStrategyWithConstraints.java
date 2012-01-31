package dima.kernel.INAF.InteractionDomain;

/**
 * Insert the type's description here.
 * Creation date: (22/04/2003 14:36:50)
 * @author: Tarek JARRAYA
 */

import java.util.Enumeration;
import java.util.Vector;

import dima.basicagentcomponents.AgentIdentifier;




public class EvaluationStrategyWithConstraints extends EvaluationStrategy
{
	public java.util.Vector constraints;
	/**
	 * EvaluateProposalsStrategyWithConstraints constructor comment.
	 */

	public EvaluationStrategyWithConstraints()
	{
		super();
		this.constraints = new Vector();
	}
	/**
	 * EvaluateProposalsStrategyWithConstraints constructor comment.
	 * @param newProposals java.util.Vector
	 */
	public EvaluationStrategyWithConstraints(final Vector newProposals)
	{
		super(newProposals);
	}
	/**
	 * EvaluateProposalsStrategyWithConstraints constructor comment.
	 * @param newProposals java.util.Vector
	 */
	public EvaluationStrategyWithConstraints(final Vector newProposals, final Vector newConstraints)
	{
		super(newProposals);
		this.setConstraints(newConstraints);
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (22/04/2003 14:39:35)
	 * @return java.lang.Object
	 */
	@Override
	public Object execute()
	{

		final Enumeration e = this.proposals.elements();

		// une proposition est valide si et seulement si elle v�rifie toutes les contraintes

		while (e.hasMoreElements())
		{
			final AbstractService proposal = (AbstractService) e.nextElement();

			if (!this.satisfyConstraints(proposal))
				this.proposals.remove(proposal);
		}

		switch (this.proposals.size())
		{
		case 0 :
			return null;

		case 1 :
			return this.proposals.get(0);

		default : //choisir une proposition au hasard parmi l'ensemble des propositions valid�es
			return this.proposals.get((int) Math.abs(Math.random() * this.proposals.size()));
		}
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (22/04/2003 15:03:23)
	 * @return java.util.Vector
	 */
	public java.util.Vector getConstraints()
	{
		return this.constraints;
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (06/04/03 00:00:56)
	 * @return boolean
	 * @param contract dima.kernel.communicatingAgent.domain.Contract
	 */
	public boolean satisfyConstraints(final AgentIdentifier proposer)
	{
		final Enumeration e = this.getConstraints().elements();

		while (e.hasMoreElements())
			if (!((Constraint)e.nextElement()).isSatisfied(proposer))
				return false;
		return true;
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (06/04/03 00:00:56)
	 * @return boolean
	 * @param contract dima.kernel.communicatingAgent.domain.Contract
	 */
	public boolean satisfyConstraints(final AbstractService service)
	{
		final Enumeration e = this.getConstraints().elements();

		while (e.hasMoreElements())
			if (!((Constraint) e.nextElement()).isSatisfied(service))
				return false;
		return true;
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (22/04/2003 15:03:23)
	 * @param newConstraints java.util.Vector
	 */
	public void setConstraints(final java.util.Vector newConstraints)
	{
		this.constraints = newConstraints;
	}

	/**
	 * Insert the method's description here.
	 * Creation date: (08/09/2003 17:13:04)
	 */
	public void addConstraint(final Constraint c)
	{
		this.constraints.add(c);
	}
}
