package dima.ontologies.basicFIPAACLMessages;

/**
 * Insert the type's description here.
 * Creation date: (22/03/03 10:14:45)
 * @author: Tarek JARRAYA
 */
//import dima.kernel.communicatingAgent.interaction.ContractNetInitiator;

public class ACLCallForProposal extends FIPAACLMessage
{

/**
	 *
	 */
	private static final long serialVersionUID = -6702682077547205490L;

/**
 * ACLCallForProposal constructor comment.
 */
public ACLCallForProposal()
{
	super();
	this.setPerformative("CallForProposal");

}
/**
 * ACLCallForProposal constructor comment.
 */
public ACLCallForProposal(final String convId)
{
	super(convId);
	this.setPerformative("CallForProposal");
}
/**
 * ACLCallForProposal constructor comment.
 * @param msgContent java.lang.String
 * @param paras java.lang.Object[]
 * @param msgReceiver Gdima.basicagentcomponents.AgentIdentifier
 */
public ACLCallForProposal(final String msgContent, final java.lang.Object[] paras)
{
	super(msgContent, paras);
	this.setPerformative("CallForProposal");
}
/**
 * ACLCallForProposal constructor comment.
 * @param newSender java.lang.String
 * @param newReceiver java.lang.String
 * @param newMessage java.lang.String
 */
public ACLCallForProposal(final String newSender, final String newReceiver, final String newMessage) {
	super(newSender, newReceiver, newMessage);
	this.setPerformative("CallForProposal");
}
/**
 * ACLCallForProposal constructor comment.
 * @param tx java.lang.String
 * @param rx java.lang.String
 * @param msg java.lang.String
 * @param irt java.lang.String
 * @param rw java.lang.String
 */
public ACLCallForProposal(final String tx, final String rx, final String msg, final String irt, final String rw) {
	super(tx, rx, msg, irt, rw);
	this.setPerformative("CallForProposal");
}
/**
 * ACLCallForProposal constructor comment.
 * @param myContent java.lang.String
 * @param tx java.lang.String
 * @param rx java.lang.String
 * @param msg java.lang.String
 * @param irt java.lang.String
 * @param rw java.lang.String
 * @param t java.lang.String
 */
public ACLCallForProposal(final String myContent, final String tx, final String rx, final String msg, final String irt, final String rw, final String t) {
	super(myContent, tx, rx, msg, irt, rw, t);
	this.setPerformative("CallForProposal");
}

@Override
public boolean isCallForProposal()
{
	return true;
}

@Override
public boolean isCFP()
{
	return true;
}
}
