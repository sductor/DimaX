package dima.ontologies.basicFIPAACLMessages;

/**
 * Insert the type's description here.
 * Creation date: (22/06/2003 22:33:09)
 * @author:
 */
public class ACLInformClosedAuction extends FIPAACLMessage {
	/**
	 *
	 */
	private static final long serialVersionUID = 5105992416919218344L;
	/**
	 * ACLInformClosedAuction constructor comment.
	 */
	public ACLInformClosedAuction() {
		super();
		this.setPerformative("InformClosedAuction");
	}
	/**
	 * ACLInformClosedAuction constructor comment.
	 * @param convId java.lang.String
	 */
	public ACLInformClosedAuction(final String convId) {
		super(convId);
		this.setPerformative("InformClosedAuction");
	}
	/**
	 * ACLInformClosedAuction constructor comment.
	 * @param msgContent java.lang.String
	 * @param paras java.lang.Object[]
	 * @param msgReceiver Gdima.basicagentcomponents.AgentIdentifier
	 */
	public ACLInformClosedAuction(final String msgContent, final java.lang.Object[] paras) {
		super(msgContent, paras);
		this.setPerformative("InformClosedAuction");
	}
	/**
	 * ACLInformClosedAuction constructor comment.
	 * @param newSender java.lang.String
	 * @param newReceiver java.lang.String
	 * @param newMessage java.lang.String
	 */
	public ACLInformClosedAuction(final String newSender, final String newReceiver, final String newMessage) {
		super(newSender, newReceiver, newMessage);
		this.setPerformative("InformClosedAuction");
	}
	/**
	 * ACLInformClosedAuction constructor comment.
	 * @param tx java.lang.String
	 * @param rx java.lang.String
	 * @param msg java.lang.String
	 * @param irt java.lang.String
	 * @param rw java.lang.String
	 */
	public ACLInformClosedAuction(final String tx, final String rx, final String msg, final String irt, final String rw) {
		super(tx, rx, msg, irt, rw);
		this.setPerformative("InformClosedAuction");
	}
	/**
	 * ACLInformClosedAuction constructor comment.
	 * @param myContent java.lang.String
	 * @param tx java.lang.String
	 * @param rx java.lang.String
	 * @param msg java.lang.String
	 * @param irt java.lang.String
	 * @param rw java.lang.String
	 * @param t java.lang.String
	 */
	public ACLInformClosedAuction(final String myContent, final String tx, final String rx, final String msg, final String irt, final String rw, final String t) {
		super(myContent, tx, rx, msg, irt, rw, t);
		this.setPerformative("InformClosedAuction");
	}
}
