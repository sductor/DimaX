package dima.basiccommunicationcomponents;

/**
 * Insert the type's description here.
 * Creation date: (01/03/2000 21:46:23)
 * @author: Gerard Rozsavolgyi
 */
public abstract class AbstractMailBox extends CommunicationObject{
	/**
	 *
	 */
	private static final long serialVersionUID = 4046287963420672717L;
	/**
	 * AbstractMailBox constructor comment.
	 */
	public AbstractMailBox() {
		super();
	}
	/**
	 * AbstractMailBox constructor comment.
	 */
	public abstract AbstractMessage getFirstMessage();
	/**
	 * AbstractMailBox constructor comment.
	 */
	public abstract boolean hasMail();
	/**
	 * AbstractMailBox constructor comment.
	 */
	public abstract AbstractMessage readMail();
	/**
	 * AbstractMailBox constructor comment.
	 */
	public abstract  boolean writeMail(AbstractMessage m);
}
