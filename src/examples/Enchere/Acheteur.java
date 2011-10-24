package examples.Enchere;

/**
 * Insert the type's description here.
 * Creation date: (22/06/2003 11:13:31)
 * @author: Tarek JARRAYA
 */
import dima.kernel.INAF.InteractionAgents.SingleRoleAgent;
import dima.kernel.INAF.InteractionProtocols.AbstractRole;
import dima.kernel.INAF.InteractionProtocols.EnglishAuctionParticipant;
import dima.ontologies.basicFIPAACLMessages.FIPAACLMessage;

public class Acheteur extends SingleRoleAgent
{
	/**
	 *
	 */
	private static final long serialVersionUID = 4468697631373188732L;
	public Catalogue catalogue;
/**
 * Acheteur constructor comment.
 * @param newId java.lang.String
 */
public Acheteur(final String newId) {
	super(newId);
}
/**
 * Insert the method's description here.
 * Creation date: (08/09/2003 19:04:47)
 * @return Gdima.kernel.INAF2.InteractionProtocols.AbstractRole
 * @param m Gdima.basicFIPAACLMessages.FIPAACLMessage
 */
@Override
public AbstractRole initParticipantRole(final FIPAACLMessage m)
{
	final Article art = (Article)m.getContent();

	System.out.println(this.getIdentifier()+" --> "+m.getSender()+" : Yes, I accept to participate to ("+m.getConversationId()+")");

	final MaStrategy stg = new MaStrategy((float)Math.random()/10);  // cr�er la strategie avec un taux d'incrementation

	stg.setHightPrice(art.getPrice() + (float)Math.random()*50);	//associer � un chaque ench�re le prix max qu'il peut proposer (Budget)

	System.out.println(this.getIdentifier()+" (PRIVATE) : ma strategie a un taux d'incrementation de :"+stg.getTaux()+" et un plafond de : "+stg.getHightPrice());

	return new EnglishAuctionParticipant(this,m.getConversationId(),m.getSender(),art,m.getReplyBy(),stg);
}
/**
 * isActive method comment.
 */
@Override
public boolean isActive() {
	return true;
}
/**
 * Insert the method's description here.
 * Creation date: (23/06/2003 14:32:36)
 */
public void setCatalogue(final Catalogue c)
{
	this.catalogue = new Catalogue(c.getArticles());
}
}
