package dima.ontologies.basicFIPAACLMessages;

/**
 * Insert the type's description here.
 * Creation date: (24/06/2003 11:22:26)
 * @author:
 */
public class ACLInformFailedAuction extends FIPAACLMessage {
/**
	 *
	 */
	private static final long serialVersionUID = -1737736089056240994L;
/**
 * ACLInformFailedAuction constructor comment.
 */
public ACLInformFailedAuction()
{
	super();
	this.setPerformative("InformFailedAuction");
}
/**
 * ACLInformFailedAuction constructor comment.
 * @param convId java.lang.String
 */
public ACLInformFailedAuction(final String convId)
{
	super(convId);
	this.setPerformative("InformFailedAuction");
}
/**
 * ACLInformFailedAuction constructor comment.
 * @param msgContent java.lang.String
 * @param paras java.lang.Object[]
 * @param msgReceiver Gdima.basicagentcomponents.AgentIdentifier
 */
public ACLInformFailedAuction(final String msgContent, final java.lang.Object[] paras)
{
	super(msgContent, paras);
	this.setPerformative("InformFailedAuction");
}
/**
 * ACLInformFailedAuction constructor comment.
 * @param newSender java.lang.String
 * @param newReceiver java.lang.String
 * @param newMessage java.lang.String
 */
public ACLInformFailedAuction(final String newSender, final String newReceiver, final String newMessage)
{
	super(newSender, newReceiver, newMessage);
	this.setPerformative("InformFailedAuction");
}
/**
 * ACLInformFailedAuction constructor comment.
 * @param tx java.lang.String
 * @param rx java.lang.String
 * @param msg java.lang.String
 * @param irt java.lang.String
 * @param rw java.lang.String
 */
public ACLInformFailedAuction(final String tx, final String rx, final String msg, final String irt, final String rw)
{
	super(tx, rx, msg, irt, rw);
	this.setPerformative("InformFailedAuction");
}
/**
 * ACLInformFailedAuction constructor comment.
 * @param myContent java.lang.String
 * @param tx java.lang.String
 * @param rx java.lang.String
 * @param msg java.lang.String
 * @param irt java.lang.String
 * @param rw java.lang.String
 * @param t java.lang.String
 */
public ACLInformFailedAuction(final String myContent, final String tx, final String rx, final String msg, final String irt, final String rw, final String t)
{
	super(myContent, tx, rx, msg, irt, rw, t);
	this.setPerformative("InformFailedAuction");
}
}
