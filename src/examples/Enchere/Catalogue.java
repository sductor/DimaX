package examples.Enchere;

/**
 * Insert the type's description here.
 * Creation date: (27/06/2003 23:20:21)
 * @author: Tarek JARRAYA
 */
import java.util.Vector;

public class Catalogue
{
	public Vector articles;
/**
 * Catalogue constructor comment.
 */


public Catalogue(final Vector arts)
{
	super();
	this.articles = new Vector(arts);
}
/**
 * Insert the method's description here.
 * Creation date: (27/06/2003 23:21:57)
 * @return java.util.Vector
 */
public java.util.Vector getArticles() {
	return this.articles;
}
/**
 * Insert the method's description here.
 * Creation date: (23/06/2003 10:57:49)
 */
public Article getNextArticle()
{
	return (Article)this.articles.remove(0);
}
/**
 * Insert the method's description here.
 * Creation date: (31/08/2003 21:37:28)
 * @return boolean
 */
public boolean isEmpty()
{
	return this.articles.isEmpty();
}
}
