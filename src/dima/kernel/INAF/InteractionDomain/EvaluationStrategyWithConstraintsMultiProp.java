/*
 * Created on 28 mai 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package dima.kernel.INAF.InteractionDomain;

import java.io.Serializable;
import java.util.Enumeration;
import java.util.Vector;

import dima.kernel.INAF.InteractionTools.Operator;




/**
 * @author faci
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class EvaluationStrategyWithConstraintsMultiProp extends
EvaluationStrategyWithConstraints implements Serializable {

	/**
	 *
	 */
	private static final long serialVersionUID = -7407129267500380854L;

	public EvaluationStrategyWithConstraintsMultiProp()
	{
		super();
		this.constraints = new Vector();
	}
	/**
	 * EvaluateProposalsStrategyWithConstraints constructor comment.
	 * @param newProposals java.util.Vector
	 */
	public EvaluationStrategyWithConstraintsMultiProp(final Vector newProposals)
	{
		super(newProposals);
	}
	/**
	 * EvaluateProposalsStrategyWithConstraints constructor comment.
	 * @param newProposals java.util.Vector
	 */
	public EvaluationStrategyWithConstraintsMultiProp(final Vector newProposals, final Vector newConstraints)
	{
		super(newProposals);
		this.setConstraints(newConstraints);
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (22/04/2003 14:39:35)
	 * @return java.lang.Object
	 */
	public Vector execute1()
	{
		/* */ System.out.println("ENTRER DANS EXECUTE 1...");
		// une proposition est valide si et seulement si elle v�rifie toutes les contraintes
		final Vector v=new Vector();
		/* */ System.out.println("EVAL: LE NBRE DE PROPOSITIONS EST....."+this.proposals.size());
		for (int i=0;i<this.proposals.size();i++)
		{
			final Resource proposal = (Resource) this.proposals.elementAt(i);

			if (!this.satisfyConstraints1(proposal))
				// proposals.remove(proposal);
				v.add(proposal);

		}
		/* */ System.out.println("TAILLE DU VECTEUR DES PROP	REJETEES....."+v.size());

		for (int i=0;i<v.size();i++)
			this.proposals.remove(v.elementAt(i));

		switch (this.proposals.size())
		{
			case 0 :
			{	/* */ System.out.println("EVAL: AUCUNE PROPOSITION EST SATISFAISANTE....");
			return new Vector();
			}


			default : //prendre l'ensemble des propositions
			{
				/* */ System.out.println("VOICI LES PROPOSITIONS ACCEPTEES....");
				for (int i=0; i<this.proposals.size();i++)
					/* */ System.out.println("EVAL: la proposition prix et tRep"+i+"    est "+((Resource) this.proposals.elementAt(i)).getCost()+"  "+((Resource)	    this.proposals.elementAt(i)).getTpsRep());

				return this.proposals;
			}
		}
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (22/04/2003 15:03:23)
	 * @return java.util.Vector
	 */
	@Override
	public java.util.Vector getConstraints()
	{
		return this.constraints;
	}


	public boolean satisfyConstraints1(final Resource service)
	{
		/* */ System.out.println("ENTRER DANS SATISF CONSTRAINT 1....");

		final Enumeration e = this.getConstraints().elements();

		if (e.hasMoreElements())
		{
			/* */ System.out.println("AU MOINS UNE CONTRAINTE ......");
			if (!((ConstraintResource) e.nextElement()).isSatisfied(service))
				return false;

		}
		final float newBudget= ((Resource)((ConstraintResource)this.getConstraints().elementAt(0)).getObjectValue()).getCost() - service.getCost() ;
		this.getConstraints().setElementAt(new ConstraintResource(new Resource(new Float(newBudget),new Double(0)),new Operator("<")),0);
		return true;
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (22/04/2003 15:03:23)
	 * @param newConstraints java.util.Vector
	 */
	@Override
	public void setConstraints(final java.util.Vector newConstraints)
	{
		this.constraints = newConstraints;
	}

	/**
	 * Insert the method's description here.
	 * Creation date: (08/09/2003 17:13:04)
	 */
	public void addConstraint(final ConstraintResource c)
	{
		this.constraints.add(c);
	}


}
