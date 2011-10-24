package dima.ontologies.basicFIPAACLMessages;

/**
 * Insert the type's description here.
 * Creation date: (27/03/2003 13:29:18)
 * @author: Tarek JARRAYA
 */
public class ACLInformDone extends FIPAACLMessage {
/**
	 *
	 */
	private static final long serialVersionUID = -4886226183380264118L;
/**
 * Informdone constructor comment.
 */
public ACLInformDone() {
	super();
	this.setPerformative("InformDone");
}
/**
 * Informdone constructor comment.
 * @param convId java.lang.String
 */
public ACLInformDone(final String convId)
{
	super(convId);
	this.setPerformative("InformDone");
}
/**
 * Informdone constructor comment.
 * @param msgContent java.lang.String
 * @param paras java.lang.Object[]
 * @param msgReceiver Gdima.basicagentcomponents.AgentIdentifier
 */
public ACLInformDone(final String msgContent, final java.lang.Object[] paras)
{
	super(msgContent, paras);
	this.setPerformative("InformDone");
}
/**
 * Informdone constructor comment.
 * @param newSender java.lang.String
 * @param newReceiver java.lang.String
 * @param newMessage java.lang.String
 */
public ACLInformDone(final String newSender, final String newReceiver, final String newMessage)
{
	super(newSender, newReceiver, newMessage);
	this.setPerformative("InformDone");
}
/**
 * Informdone constructor comment.
 * @param tx java.lang.String
 * @param rx java.lang.String
 * @param msg java.lang.String
 * @param irt java.lang.String
 * @param rw java.lang.String
 */
public ACLInformDone(final String tx, final String rx, final String msg, final String irt, final String rw)
{
	super(tx, rx, msg, irt, rw);
	this.setPerformative("InformDone");
}
/**
 * Informdone constructor comment.
 * @param myContent java.lang.String
 * @param tx java.lang.String
 * @param rx java.lang.String
 * @param msg java.lang.String
 * @param irt java.lang.String
 * @param rw java.lang.String
 * @param t java.lang.String
 */
public ACLInformDone(final String myContent, final String tx, final String rx, final String msg, final String irt, final String rw, final String t)
{
	super(myContent, tx, rx, msg, irt, rw, t);
	this.setPerformative("InformDone");
}
}
