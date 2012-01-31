package examples.Enchere;

/**
 * Insert the type's description here.
 * Creation date: (23/06/2003 10:50:12)
 * @author: Tarek JARRAYA
 */
import dima.kernel.INAF.InteractionDomain.AbstractService;

public class Article extends AbstractService
{
	public float price;
	/**
	 * Article constructor comment.
	 * @param id java.lang.String
	 */
	public Article(final String id) {
		super(id);
	}
	/**
	 * Article constructor comment.
	 * @param id java.lang.String
	 */
	public Article(final String id, final float newPrice)
	{
		super(id);
		this.setPrice(newPrice);
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (23/06/2003 11:38:58)
	 * @return float
	 */
	public float getPrice() {
		return this.price;
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (23/06/2003 11:38:58)
	 * @param newPrice float
	 */
	public void setPrice(final float newPrice) {
		this.price = newPrice;
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (25/06/2003 19:52:37)
	 */
	@Override
	public String toString()
	{
		return new String("l'article : "+this.getIdentifier()+",le prix :"+this.getPrice());
	}

	/**
	 * Insert the method's description here.
	 * Creation date: (30/08/2003 20:20:06)
	 * @return boolean
	 * @param s Gdima.kernel.INAF2.InteractionDomain.AbstractService
	 */
	@Override
	public boolean isBetterThan(final AbstractService s)
	{
		return this.getPrice() > ((Article)s).getPrice();
	}
}
