package dima.ontologies.basicFIPAACLMessages;

/**
 * Insert the type's description here.
 * Creation date: (27/03/2003 10:51:21)
 * @author: Tarek JARRAYA
 */
public class ACLRejectProposal extends FIPAACLMessage {
/**
	 *
	 */
	private static final long serialVersionUID = -7995314427815114923L;

/**
 * ACLRefuseProposal constructor comment.
 */
public ACLRejectProposal()
{
	super();
	this.setPerformative("RejectProposal");
}
/**
 * ACLRefuseProposal constructor comment.
 */
public ACLRejectProposal(final String convId)
{
	super(convId);
	this.setPerformative("RejectProposal");
}
/**
 * ACLRefuseProposal constructor comment.
 * @param msgContent java.lang.String
 * @param paras java.lang.Object[]
 * @param msgReceiver Gdima.basicagentcomponents.AgentIdentifier
 */
public ACLRejectProposal(final String msgContent, final java.lang.Object[] paras)
{
	super(msgContent, paras);
	this.setPerformative("RejectProposal");
}
/**
 * ACLRefuseProposal constructor comment.
 * @param newSender java.lang.String
 * @param newReceiver java.lang.String
 * @param newMessage java.lang.String
 */
public ACLRejectProposal(final String newSender, final String newReceiver, final String newMessage)
{
	super(newSender, newReceiver, newMessage);
	this.setPerformative("RejectProposal");
}
/**
 * ACLRefuseProposal constructor comment.
 * @param tx java.lang.String
 * @param rx java.lang.String
 * @param msg java.lang.String
 * @param irt java.lang.String
 * @param rw java.lang.String
 */
public ACLRejectProposal(final String tx, final String rx, final String msg, final String irt, final String rw)
{
	super(tx, rx, msg, irt, rw);
	this.setPerformative("RejectProposal");
}
/**
 * ACLRefuseProposal constructor comment.
 * @param myContent java.lang.String
 * @param tx java.lang.String
 * @param rx java.lang.String
 * @param msg java.lang.String
 * @param irt java.lang.String
 * @param rw java.lang.String
 * @param t java.lang.String
 */
public ACLRejectProposal(final String myContent, final String tx, final String rx, final String msg, final String irt, final String rw, final String t)
{
	super(myContent, tx, rx, msg, irt, rw, t);
	this.setPerformative("RejectProposal");
}

@Override
public boolean isReject()
{
	return true;
}

@Override
public boolean isRejectProposal()
{
	return true;
}
}
