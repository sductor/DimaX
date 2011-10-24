package dima.ontologies.basicFIPAACLMessages;

/**
 * Insert the type's description here.
 * Creation date: (22/06/2003 22:29:51)
 * @author:
 */
public class ACLInformNewPrice extends FIPAACLMessage {
/**
	 *
	 */
	private static final long serialVersionUID = -8318216340429025417L;
/**
 * ACLInformNewPrice constructor comment.
 */
public ACLInformNewPrice()
{
	super();
	this.setPerformative("InformNewPrice");
}
/**
 * ACLInformNewPrice constructor comment.
 * @param convId java.lang.String
 */
public ACLInformNewPrice(final String convId)
{
	super(convId);
	this.setPerformative("InformNewPrice");
}
/**
 * ACLInformNewPrice constructor comment.
 * @param msgContent java.lang.String
 * @param paras java.lang.Object[]
 * @param msgReceiver Gdima.basicagentcomponents.AgentIdentifier
 */
public ACLInformNewPrice(final String msgContent, final java.lang.Object[] paras) {
	super(msgContent, paras);
	this.setPerformative("InformNewPrice");
}
/**
 * ACLInformNewPrice constructor comment.
 * @param newSender java.lang.String
 * @param newReceiver java.lang.String
 * @param newMessage java.lang.String
 */
public ACLInformNewPrice(final String newSender, final String newReceiver, final String newMessage) {
	super(newSender, newReceiver, newMessage);
	this.setPerformative("InformNewPrice");
}
/**
 * ACLInformNewPrice constructor comment.
 * @param tx java.lang.String
 * @param rx java.lang.String
 * @param msg java.lang.String
 * @param irt java.lang.String
 * @param rw java.lang.String
 */
public ACLInformNewPrice(final String tx, final String rx, final String msg, final String irt, final String rw) {
	super(tx, rx, msg, irt, rw);
	this.setPerformative("InformNewPrice");
}
/**
 * ACLInformNewPrice constructor comment.
 * @param myContent java.lang.String
 * @param tx java.lang.String
 * @param rx java.lang.String
 * @param msg java.lang.String
 * @param irt java.lang.String
 * @param rw java.lang.String
 * @param t java.lang.String
 */
public ACLInformNewPrice(final String myContent, final String tx, final String rx, final String msg, final String irt, final String rw, final String t) {
	super(myContent, tx, rx, msg, irt, rw, t);
	this.setPerformative("InformNewPrice");
}
}
