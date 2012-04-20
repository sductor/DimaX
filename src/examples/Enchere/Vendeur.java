package examples.Enchere;

/**
 * Insert the type's description here.
 * Creation date: (22/06/2003 11:08:23)
 * @author: Tarek JARRAYA
 */
import java.util.Date;
import java.util.Vector;

import dima.basicagentcomponents.AgentName;
import dima.basiccommunicationcomponents.Message;
import dima.kernel.INAF.InteractionAgents.SingleRoleAgent;
import dima.kernel.INAF.InteractionProtocols.EnglishAuctionInitiator;

public class Vendeur extends SingleRoleAgent
{
	/**
	 *
	 */
	private static final long serialVersionUID = -4912430840789251162L;
	public Catalogue catalogue;
	/**
	 * Vendeur constructor comment.
	 * @param newId java.lang.String
	 */
	public Vendeur(final String newId) {
		super(newId);
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (08/09/2003 11:24:02)
	 * @return java.util.Vector
	 */
	public Vector getBuyers()
	{
		final Vector buyers = new Vector(this.getAquaintances().keySet());

		for(int i=0;i<buyers.size();i++) {
			buyers.set(i,new AgentName((String)buyers.get(i)));
		}

		return buyers;
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (23/06/2003 14:35:18)
	 * @return java.util.Vector
	 */
	public Catalogue getCatalogue() {
		return this.catalogue;
	}
	/**
	 * Tests wheter a proactive object is active or no ie whether the ProactiveComponent.
	 */
	@Override
	public boolean isActive()
	{
		return !this.catalogue.isEmpty();
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (08/02/2000 23:50:27)
	 * @return boolean
	 */
	@Override
	public void preActivity()
	{
		// si le vendeur ne participe pas � une ench�re en cours et qu'il lui reste
		// des produits � vendre dans le catalogue, alors il cr�e un role initiateur

		if (this.isFree())
		{
			this.setRole(new EnglishAuctionInitiator(this,this.buildNewConversationId(),this.getBuyers(),this.catalogue.getNextArticle(),new Date(3000)));
			this.activateRole();
		}
	}
	/**
	 * Initialization of a ProactiveComponent.
	 *
	 */

	@Override
	public  void proactivityInitialize()
	{
		//l'envoie du catalogue � tous les participants

		final Message m = new Message("setCatalogue",this.getCatalogue());

		this.sendAll(m);
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (23/06/2003 14:35:18)
	 * @param newCatalogue java.util.Vector
	 */
	public void setCatalogue(final Catalogue newCatalogue) {
		this.catalogue = newCatalogue;
	}
}
