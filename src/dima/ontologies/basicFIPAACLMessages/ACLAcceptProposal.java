package dima.ontologies.basicFIPAACLMessages;

/**
 * Insert the type's description here.
 * Creation date: (27/03/2003 09:20:48)
 * @author: Tarek JARRAYA
 */


public class ACLAcceptProposal extends FIPAACLMessage
{

	/**
	 *
	 */
	private static final long serialVersionUID = 5286181713294022495L;

	/**
	 * ACLAcceptProposal constructor comment.
	 */
	public ACLAcceptProposal() {
		super();
		this.setPerformative("AcceptProposal");
	}
	/**
	 * ACLAcceptProposal constructor comment.
	 */
	public ACLAcceptProposal(final String conId)
	{
		super(conId);
		this.setPerformative("AcceptProposal");
	}
	/**
	 * ACLAcceptProposal constructor comment.
	 * @param msgContent java.lang.String
	 * @param paras java.lang.Object[]
	 * @param msgReceiver Gdima.basicagentcomponents.AgentIdentifier
	 */
	public ACLAcceptProposal(final String msgContent, final java.lang.Object[] paras, final dima.basicagentcomponents.AgentIdentifier msgReceiver)
	{
		super(msgContent, paras);
		this.setPerformative("AcceptProposal");
	}
	/**
	 * ACLAcceptProposal constructor comment.
	 * @param newSender java.lang.String
	 * @param newReceiver java.lang.String
	 * @param newMessage java.lang.String
	 */
	public ACLAcceptProposal(final String newSender, final String newReceiver, final String newMessage) {
		super(newSender, newReceiver, newMessage);
		this.setPerformative("AcceptProposal");
	}
	/**
	 * ACLAcceptProposal constructor comment.
	 * @param tx java.lang.String
	 * @param rx java.lang.String
	 * @param msg java.lang.String
	 * @param irt java.lang.String
	 * @param rw java.lang.String
	 */
	public ACLAcceptProposal(final String tx, final String rx, final String msg, final String irt, final String rw) {
		super(tx, rx, msg, irt, rw);
		this.setPerformative("AcceptProposal");
	}
	/**
	 * ACLAcceptProposal constructor comment.
	 * @param myContent java.lang.String
	 * @param tx java.lang.String
	 * @param rx java.lang.String
	 * @param msg java.lang.String
	 * @param irt java.lang.String
	 * @param rw java.lang.String
	 * @param t java.lang.String
	 */
	public ACLAcceptProposal(final String myContent, final String tx, final String rx, final String msg, final String irt, final String rw, final String t)
	{
		super(myContent, tx, rx, msg, irt, rw, t);
		this.setPerformative("AcceptProposal");
	}

	@Override
	public boolean isAccept ()
	{
		return true;
	}

	@Override
	public boolean isAcceptProposal ()
	{
		return true;
	}
}
