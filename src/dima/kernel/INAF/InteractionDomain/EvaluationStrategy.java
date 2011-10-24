package dima.kernel.INAF.InteractionDomain;

/**
 * Insert the type's description here.
 * Creation date: (22/04/2003 11:46:00)
 * @author: Tarek JARRAYA
 */

import java.util.Vector;

public abstract class EvaluationStrategy extends AbstractStrategy
{
    public Vector proposals;
/**
 * EvaluateProposalsStategy constructor comment.
 */

public EvaluationStrategy()
{
	super();
	this.proposals = new Vector();
}
/**
 * EvaluateProposalsStategy constructor comment.
 */
public EvaluationStrategy(final Vector newProposals)
{
	super();
	this.setProposals(newProposals);
}
/**
 * Insert the method's description here.
 * Creation date: (22/04/2003 23:16:04)
 * @return Vector
 */
public Vector getProposals() {
	return this.proposals;
}
/**
 * Insert the method's description here.
 * Creation date: (22/04/2003 23:16:04)
 * @param newProposals Vector
 */
public void setProposals(final Vector newProposals) {
	this.proposals = new Vector(newProposals);
}
}
