package dima.ontologies.basicFIPAACLMessages;

/**
 * Insert the type's description here.
 * Creation date: (27/03/2003 12:34:04)
 * @author: Tarek JARRAYA
 */
public class ACLPropose extends FIPAACLMessage {
/**
	 *
	 */
	private static final long serialVersionUID = 7873427172228822536L;

/**
 * ACLPropose constructor comment.
 */
public ACLPropose() {
	super();
	this.setPerformative("Propose");
}
/**
 * ACLPropose constructor comment.
 * @param convId java.lang.String
 */
public ACLPropose(final String convId) {
	super(convId);
	this.setPerformative("Propose");
}
/**
 * ACLPropose constructor comment.
 * @param msgContent java.lang.String
 * @param paras java.lang.Object[]
 * @param msgReceiver Gdima.basicagentcomponents.AgentIdentifier
 */
public ACLPropose(final String msgContent, final java.lang.Object[] paras) {
	super(msgContent, paras);
	this.setPerformative("Propose");
}
/**
 * ACLPropose constructor comment.
 * @param newSender java.lang.String
 * @param newReceiver java.lang.String
 * @param newMessage java.lang.String
 */
public ACLPropose(final String newSender, final String newReceiver, final String newMessage) {
	super(newSender, newReceiver, newMessage);
	this.setPerformative("Propose");
}
/**
 * ACLPropose constructor comment.
 * @param tx java.lang.String
 * @param rx java.lang.String
 * @param msg java.lang.String
 * @param irt java.lang.String
 * @param rw java.lang.String
 */
public ACLPropose(final String tx, final String rx, final String msg, final String irt, final String rw) {
	super(tx, rx, msg, irt, rw);
	this.setPerformative("Propose");
}
/**
 * ACLPropose constructor comment.
 * @param myContent java.lang.String
 * @param tx java.lang.String
 * @param rx java.lang.String
 * @param msg java.lang.String
 * @param irt java.lang.String
 * @param rw java.lang.String
 * @param t java.lang.String
 */
public ACLPropose(final String myContent, final String tx, final String rx, final String msg, final String irt, final String rw, final String t) {
	super(myContent, tx, rx, msg, irt, rw, t);
	this.setPerformative("Propose");
}

@Override
public boolean isPropose()
{
	return true;
}
}
