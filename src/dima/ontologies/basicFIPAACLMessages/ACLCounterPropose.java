package dima.ontologies.basicFIPAACLMessages;

/**
 * Insert the type's description here.
 * Creation date: (23/05/2003 18:02:55)
 * @author:
 */
public class ACLCounterPropose extends FIPAACLMessage {
	/**
	 *
	 */
	private static final long serialVersionUID = -1821106546016476169L;
	/**
	 * ACLCounterProposal constructor comment.
	 */
	public ACLCounterPropose()
	{
		super();
		this.setPerformative("CounterPropose");
	}
	/**
	 * ACLCounterProposal constructor comment.
	 * @param convId java.lang.String
	 */
	public ACLCounterPropose(final String convId) {
		super(convId);
		this.setPerformative("CounterPropose");
	}
	/**
	 * ACLCounterProposal constructor comment.
	 * @param msgContent java.lang.String
	 * @param paras java.lang.Object[]
	 * @param msgReceiver Gdima.basicagentcomponents.AgentIdentifier
	 */
	public ACLCounterPropose(final String msgContent, final java.lang.Object[] paras) {
		super(msgContent, paras);
		this.setPerformative("CounterPropose");
	}
	/**
	 * ACLCounterProposal constructor comment.
	 * @param newSender java.lang.String
	 * @param newReceiver java.lang.String
	 * @param newMessage java.lang.String
	 */
	public ACLCounterPropose(final String newSender, final String newReceiver, final String newMessage) {
		super(newSender, newReceiver, newMessage);
		this.setPerformative("CounterPropose");
	}
	/**
	 * ACLCounterProposal constructor comment.
	 * @param tx java.lang.String
	 * @param rx java.lang.String
	 * @param msg java.lang.String
	 * @param irt java.lang.String
	 * @param rw java.lang.String
	 */
	public ACLCounterPropose(final String tx, final String rx, final String msg, final String irt, final String rw) {
		super(tx, rx, msg, irt, rw);
		this.setPerformative("CounterPropose");
	}
	/**
	 * ACLCounterProposal constructor comment.
	 * @param myContent java.lang.String
	 * @param tx java.lang.String
	 * @param rx java.lang.String
	 * @param msg java.lang.String
	 * @param irt java.lang.String
	 * @param rw java.lang.String
	 * @param t java.lang.String
	 */
	public ACLCounterPropose(final String myContent, final String tx, final String rx, final String msg, final String irt, final String rw, final String t) {
		super(myContent, tx, rx, msg, irt, rw, t);
		this.setPerformative("CounterPropose");
	}
}
