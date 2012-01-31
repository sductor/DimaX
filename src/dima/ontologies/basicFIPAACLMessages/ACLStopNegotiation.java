package dima.ontologies.basicFIPAACLMessages;

/**
 * Insert the type's description here.
 * Creation date: (23/05/2003 17:27:55)
 * @author:
 */
public class ACLStopNegotiation extends FIPAACLMessage {
	/**
	 *
	 */
	private static final long serialVersionUID = -135125892623001207L;
	/**
	 * ACLStopNegotiation constructor comment.
	 */
	public ACLStopNegotiation()
	{
		super();
		this.setPerformative("StopNegotiation");
	}
	/**
	 * ACLStopNegotiation constructor comment.
	 * @param convId java.lang.String
	 */
	public ACLStopNegotiation(final String convId) {
		super(convId);
		this.setPerformative("StopNegotiation");
	}
	/**
	 * ACLStopNegotiation constructor comment.
	 * @param msgContent java.lang.String
	 * @param paras java.lang.Object[]
	 * @param msgReceiver Gdima.basicagentcomponents.AgentIdentifier
	 */
	public ACLStopNegotiation(final String msgContent, final java.lang.Object[] paras) {
		super(msgContent, paras);
		this.setPerformative("StopNegotiation");
	}
	/**
	 * ACLStopNegotiation constructor comment.
	 * @param newSender java.lang.String
	 * @param newReceiver java.lang.String
	 * @param newMessage java.lang.String
	 */
	public ACLStopNegotiation(final String newSender, final String newReceiver, final String newMessage) {
		super(newSender, newReceiver, newMessage);
		this.setPerformative("StopNegotiation");
	}
	/**
	 * ACLStopNegotiation constructor comment.
	 * @param tx java.lang.String
	 * @param rx java.lang.String
	 * @param msg java.lang.String
	 * @param irt java.lang.String
	 * @param rw java.lang.String
	 */
	public ACLStopNegotiation(final String tx, final String rx, final String msg, final String irt, final String rw) {
		super(tx, rx, msg, irt, rw);
		this.setPerformative("StopNegotiation");
	}
	/**
	 * ACLStopNegotiation constructor comment.
	 * @param myContent java.lang.String
	 * @param tx java.lang.String
	 * @param rx java.lang.String
	 * @param msg java.lang.String
	 * @param irt java.lang.String
	 * @param rw java.lang.String
	 * @param t java.lang.String
	 */
	public ACLStopNegotiation(final String myContent, final String tx, final String rx, final String msg, final String irt, final String rw, final String t) {
		super(myContent, tx, rx, msg, irt, rw, t);
		this.setPerformative("StopNegotiation");
	}
}
