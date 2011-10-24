package dima.ontologies.basicFIPAACLMessages;

/**
 * Insert the type's description here.
 * Creation date: (27/03/2003 11:03:12)
 * @author: Tarek JARRAYA
 */
public class ACLRefuse extends FIPAACLMessage
{

/**
	 *
	 */
	private static final long serialVersionUID = -4396617158788738421L;

/**
 * ACLRefuse constructor comment.
 */
public ACLRefuse() {
	super();
	this.setPerformative("Refuse");
}
/**
 * ACLRefuse constructor comment.
 * @param convId java.lang.String
 */
public ACLRefuse(final String convId)
{
	super(convId);
	this.setPerformative("Refuse");
}
/**
 * ACLRefuse constructor comment.
 * @param msgContent java.lang.String
 * @param paras java.lang.Object[]
 * @param msgReceiver Gdima.basicagentcomponents.AgentIdentifier
 */
public ACLRefuse(final String msgContent, final java.lang.Object[] paras) {
	super(msgContent, paras);
		this.setPerformative("Refuse");
}
/**
 * ACLRefuse constructor comment.
 * @param newSender java.lang.String
 * @param newReceiver java.lang.String
 * @param newMessage java.lang.String
 */
public ACLRefuse(final String newSender, final String newReceiver, final String newMessage) {
	super(newSender, newReceiver, newMessage);
		this.setPerformative("Refuse");
}
/**
 * ACLRefuse constructor comment.
 * @param tx java.lang.String
 * @param rx java.lang.String
 * @param msg java.lang.String
 * @param irt java.lang.String
 * @param rw java.lang.String
 */
public ACLRefuse(final String tx, final String rx, final String msg, final String irt, final String rw) {
	super(tx, rx, msg, irt, rw);
		this.setPerformative("Refuse");
}
/**
 * ACLRefuse constructor comment.
 * @param myContent java.lang.String
 * @param tx java.lang.String
 * @param rx java.lang.String
 * @param msg java.lang.String
 * @param irt java.lang.String
 * @param rw java.lang.String
 * @param t java.lang.String
 */
public ACLRefuse(final String myContent, final String tx, final String rx, final String msg, final String irt, final String rw, final String t) {
	super(myContent, tx, rx, msg, irt, rw, t);
		this.setPerformative("Refuse");
}

@Override
public boolean isRefuse()
{
	return true;
}

@Override
public boolean isRefuseProposal()
{
	return true;
}
}
