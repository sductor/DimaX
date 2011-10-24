package dima.basiccommunicationcomponents;

/**
 * Insert the type's description here.
 * Creation date: (23/05/01 15:56:45)
 * @author: Zahia Guessoum
 */

public class MessageIdentifier extends CommunicationObject {
	/**
	 *
	 */
	private static final long serialVersionUID = -8450036815590587568L;
	public java.lang.String identifier;
/**
 * MessageIdentifier constructor comment.
 */
public MessageIdentifier() {
	super();
}
/**
 * MessageIdentifier constructor comment.
 */
public MessageIdentifier(final java.lang.String id) {
	super();
	 this.identifier=id;
}
/**
 * MessageIdentifier constructor comment.
 */
public boolean equals(final MessageIdentifier a) {
	return this.identifier.equals(a.getIdentifier());
}
/**
 * Insert the method's description here.
 * Creation date: (23/05/01 15:58:20)
 * @return java.lang.String
 */
public java.lang.String getIdentifier() {
	return this.identifier;
}
/**
 * Insert the method's description here.
 * Creation date: (23/05/01 15:58:20)
 * @param newIdentifier java.lang.String
 */
public void setIdentifier(final java.lang.String newIdentifier) {
	this.identifier = newIdentifier;
}
}
