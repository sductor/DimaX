package dima.kernel.INAF.InteractionDomain;

/**
 * Insert the type's description here.
 * Creation date: (22/04/2003 15:15:08)
 * @author: Tarek JARRAYA
 */

import java.io.Serializable;
import java.util.Vector;

public class RandomEvaluationStrategy extends EvaluationStrategy implements Serializable
{
/**
	 *
	 */
	private static final long serialVersionUID = 4418990514159045924L;
/**
 * RandomEvaluationStrategy constructor comment.
 */
public RandomEvaluationStrategy()
{
    super();
}
/**
 * RandomEvaluationStrategy constructor comment.
 * @param newProposals java.util.Hashtable
 */
public RandomEvaluationStrategy(final Vector newProposals)
{
    super(newProposals);
}
/**
 * Insert the method's description here.
 * Creation date: (22/04/2003 15:15:08)
 */
@Override
public Object execute()
{
    switch (this.proposals.size())
        {
        case 0 :
            //System.out.println(agent.getIdentifier()+" ("+role.getConversationId()+") : negotiation fail....");
            return null;

        case 1 :
            return this.proposals.get(0);
            //System.out.println(agent.getIdentifier()+" ("+role.getConversationId()+") : accept the proposal ("+role.getAcceptedProposal().getAssociatedService()+ ") of "+role.getAcceptedProposal().getContractingPartie());

        default : //choisir une proposition au hasard parmi l'ensemble des propositions valid�es
            return this.proposals.get((int) Math.abs(Math.random() * this.proposals.size()));
            //System.out.println(agent.getIdentifier()+" ("+role.getConversationId()+") : accept the proposal ("+role.getAcceptedProposal().getAssociatedService()+ ") of "+role.getAcceptedProposal().getContractingPartie());
    }

}
}
