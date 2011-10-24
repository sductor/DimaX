package examples.Enchere;

/**
 * Insert the type's description here.
 * Creation date: (25/06/2003 13:39:26)
 * @author: Tarek JARRAYA
 */
import dima.kernel.INAF.InteractionDomain.BiddingStrategy;

public class MaStrategy extends BiddingStrategy
{
	public float hightPrice;
	public float taux;
/**
 * MaStrategy constructor comment.
 */
public MaStrategy() {
	super();
}
/**
 * MaStrategy constructor comment.
 */
public MaStrategy(final float t)
{
	super();
	this.setTaux(t);
}
/**
 * Insert the method's description here.
 * Creation date: (25/06/2003 13:39:26)
 */
@Override
public Object execute()
{
	final Article proposal = new Article(this.currentBetterProposal.getIdentifier());

	final float currentPrice = ((Article)this.currentBetterProposal).getPrice();

	if(currentPrice>=this.hightPrice)
		return null;
	else
	{
		final float price = (1+this.taux)*currentPrice; //ajouter taux % du prix actuel

		if (price < this.hightPrice)
			proposal.setPrice(price);
		else
			proposal.setPrice(this.hightPrice);

		return proposal;
	}
}
/**
 * Insert the method's description here.
 * Creation date: (26/06/2003 18:35:54)
 * @return float
 */
public float getHightPrice()
{
	return this.hightPrice;
}
/**
 * Insert the method's description here.
 * Creation date: (27/06/2003 10:44:32)
 * @return java.lang.Float
 */
public float getTaux() {
	return this.taux;
}
/**
 * Insert the method's description here.
 * Creation date: (26/06/2003 18:35:54)
 * @param newHightPrice float
 */
public void setHightPrice(final float newHightPrice)
{
	this.hightPrice = newHightPrice;
}
/**
 * Insert the method's description here.
 * Creation date: (27/06/2003 10:44:32)
 * @param newTaux java.lang.Float
 */
public void setTaux(final float newTaux) {
	this.taux = newTaux;
}
}
