package dima.ontologies.basicFIPAACLMessages;

/**
 * Insert the type's description here.
 * Creation date: (23/06/2003 14:52:56)
 * @author:
 */
public class ACLInformAuctionWinner extends FIPAACLMessage {
/**
	 *
	 */
	private static final long serialVersionUID = -3410498857608299711L;
/**
 * ACLInformAuctionWinner constructor comment.
 */
public ACLInformAuctionWinner() {
    super();
    this.setPerformative("InformAuctionWinner");
}
/**
 * ACLInformAuctionWinner constructor comment.
 * @param convId java.lang.String
 */
public ACLInformAuctionWinner(final String convId) {
	super(convId);
   this.setPerformative("InformAuctionWinner");
}
/**
 * ACLInformAuctionWinner constructor comment.
 * @param msgContent java.lang.String
 * @param paras java.lang.Object[]
 * @param msgReceiver Gdima.basicagentcomponents.AgentIdentifier
 */
public ACLInformAuctionWinner(final String msgContent, final java.lang.Object[] paras) {
	super(msgContent, paras);
   this.setPerformative("InformAuctionWinner");
}
/**
 * ACLInformAuctionWinner constructor comment.
 * @param newSender java.lang.String
 * @param newReceiver java.lang.String
 * @param newMessage java.lang.String
 */
public ACLInformAuctionWinner(final String newSender, final String newReceiver, final String newMessage) {
	super(newSender, newReceiver, newMessage);
   this.setPerformative("InformAuctionWinner");
}
/**
 * ACLInformAuctionWinner constructor comment.
 * @param tx java.lang.String
 * @param rx java.lang.String
 * @param msg java.lang.String
 * @param irt java.lang.String
 * @param rw java.lang.String
 */
public ACLInformAuctionWinner(final String tx, final String rx, final String msg, final String irt, final String rw) {
	super(tx, rx, msg, irt, rw);
   this.setPerformative("InformAuctionWinner");
}
/**
 * ACLInformAuctionWinner constructor comment.
 * @param myContent java.lang.String
 * @param tx java.lang.String
 * @param rx java.lang.String
 * @param msg java.lang.String
 * @param irt java.lang.String
 * @param rw java.lang.String
 * @param t java.lang.String
 */
public ACLInformAuctionWinner(final String myContent, final String tx, final String rx, final String msg, final String irt, final String rw, final String t) {
	super(myContent, tx, rx, msg, irt, rw, t);
   this.setPerformative("InformAuctionWinner");
}
}
