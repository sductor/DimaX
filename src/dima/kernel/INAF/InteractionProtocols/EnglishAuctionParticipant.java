package dima.kernel.INAF.InteractionProtocols;

/**
 * Insert the type's description here.
 * Creation date: (30/05/2003 19:05:14)
 * @author: Tarek JARRAYA
 */
import java.util.Date;
import java.util.Vector;

import dima.basicagentcomponents.AgentIdentifier;
import dima.basicagentcomponents.AgentName;
import dima.kernel.INAF.InteractionAgents.InteractiveAgent;
import dima.kernel.INAF.InteractionDomain.AbstractService;
import dima.kernel.INAF.InteractionDomain.BiddingStrategy;
import dima.kernel.INAF.InteractionTools.Transition2;
import dima.ontologies.basicFIPAACLMessages.ACLInformNewPrice;
import dima.ontologies.basicFIPAACLMessages.ACLPropose;
import dima.ontologies.basicFIPAACLMessages.FIPAACLMessage;
import dima.tools.agentInterface.NamedAction;
import dima.tools.agentInterface.NamedCondition;
import dima.tools.automata.ATN;
import dima.tools.automata.State;





public class EnglishAuctionParticipant extends AbstractRole
{
	/**
	 *
	 */
	private static final long serialVersionUID = -250215296778025660L;

	public BiddingStrategy biddingStrategy; //la strategie utilis�e pour d�finir les propositions tout au cours de l'ench�re

	public AbstractService proposal;  // elle contient la proposition construite par la strategie et qui sera envoy�e � l'initiateur

	public AgentIdentifier initiator; //l'identifiant de l'agent initiateur de l'ench�re (commissaire priseur)

	public AgentIdentifier acceptedProposer; // il contient l'identifiant du participant qui propose la meilleure offre
	public java.util.Date timeOut;
/**
 * EnglishAuctionParticipant constructor comment.
 */
public EnglishAuctionParticipant()
{
	super(buildATN()); //construire l'ATN du r�le
	this.setRoleName("EnglishAuctionParticipant");
	this.setAcceptedProposer(new AgentName(""));
}
/**
 * EnglishAuctionParticipant constructor comment.
 */
public EnglishAuctionParticipant(final InteractiveAgent buyer,final String convId,final AgentIdentifier initiatorId,final AbstractService contract,final Date delay)
{

	super(buildATN()); //construire l'ATN du r�le

	this.setRoleName("EnglishAuctionParticipant");

	this.setAgent(buyer); //initialiser l'identifiant de l'acheteur
	this.setInitiator(initiatorId); //initialiser l'identifiant du vendeur
	this.setConversationId(convId);
	this.setContract(contract); //instancier le contract mis en ench�re
	this.setTimeOut(delay);

	this.setAcceptedProposer(new AgentName(""));
}
/**
 * EnglishAuctionParticipant constructor comment.
 */
public EnglishAuctionParticipant(final InteractiveAgent buyer,final String convId,final AgentIdentifier initiatorId,final AbstractService contract,final Date delay,final BiddingStrategy strategy)
{

	super(buildATN()); //construire l'ATN du r�le

	this.setRoleName("EnglishAuctionParticipant");

	this.setAgent(buyer); //initialiser l'identifiant de l'acheteur
	this.setInitiator(initiatorId); //initialiser l'identifiant du vendeur
	this.setConversationId(convId);
	this.setContract(contract); //instancier le contract mis en ench�re
	this.setTimeOut(delay);
	this.setBiddingStrategy(strategy); // la strategie utilis�e par construire les propositions

	this.setAcceptedProposer(new AgentName(""));
}
/**
 * Insert the method's description here.
 * Creation date: (25/06/2003 16:46:27)
 */
public boolean acceptToPropose()
{
	return this.getProposal()!=null;
}
/**
 * Insert the method's description here.
 * Creation date: (08/09/2003 11:54:00)
 */
public static ATN buildATN()
{
	final ATN atn = new ATN();

	//Initial State
	final State start = new State("start");
	start.beInitial();
	atn.setInitialState(start);

	//Two Final states

	final State failure = new State("failure");
	failure.beFinal();
	final State success = new State("success");
	success.beFinal();

	final Vector f = new Vector();
	f.add(success);
	f.add(failure);

	atn.setFinalStates(f);

	//Two intermediate states : wait and endAuction

	final State wait = new State("wait");

	final State endAuction = new State("endAuction");

	// transitions between start and wait

	Vector v = new Vector();

	NamedCondition c = new NamedCondition("hasCFPMessage");

	NamedAction a = new NamedAction("decideToPropose");

	Transition2 t = new Transition2(c,a,wait);

	v.add(t);

	start.setTransitionList(v);

	// transitions from wait to start

	v = new Vector();

	c = new NamedCondition("hasInformNewPriceMessage");

	a = new NamedAction("upDateBetterProposal");

	t = new Transition2(c,a,start);

	v.add(t);

	// transitions from wait to endAuction

	c = new NamedCondition("hasInformClosedAuctionMessage");

	t = new Transition2(c,endAuction);

	v.add(t);

	// transitions from wait to wait

	c = new NamedCondition("acceptToPropose");

	a = new NamedAction("sendPropose");

	t = new Transition2(c,a,wait);

	v.add(t);

	wait.setTransitionList(v);

	// transitions from endAuction to success

	v = new Vector();

	c = new NamedCondition("hasAcceptProposalMessage");

	a = new NamedAction("sendConfirm");

	t = new Transition2(c,a,success);

	v.add(t);

	// transition from endAuction to failure

	c = new NamedCondition("hasInformAuctionWinnerMessage");

	t = new Transition2(c,failure);

	v.add(t);

	endAuction.setTransitionList(v);


	return atn;
}
/**
 * Insert the method's description here.
 * Creation date: (22/06/2003 22:27:59)
 */
public void constructProposal()
{

	//EnglishAuctionParticipant role = (EnglishAuctionParticipant) this.getContext();

	//InteractiveAgent agent = role.getAgent();

    //FIPAACLMessage cfp = role.getCurrentMessage();

    //AbstractService currentProp = (AbstractService)cfp.getContent();
    //role.getBiddingStrategy().setCurrentBetterProposal(currentProp);

    //role.setProposal((AbstractService)role.getBiddingStrategy().execute());
}
/**
 * Insert the method's description here.
 * Creation date: (22/06/2003 22:27:26)
 */
public void decideToPropose()
{
	if (! this.getAcceptedProposer().equals(this.getAgent().getIdentifier()))
	{
		final FIPAACLMessage cfp = this.getCurrentMessage();

		final AbstractService currentProp = (AbstractService)cfp.getContent();

		this.getBiddingStrategy().setCurrentBetterProposal(currentProp);

		// Il faut d�finir une strategie de decision :
		// La strategie par d�faut : on augmente le prix avec un taux fixe T, il faut que le prix du produit
		// ne d�passe pas le budget B accord� � cette ench�re

		this.setProposal((AbstractService)this.getBiddingStrategy().execute());
	}
}
/**
 * Insert the method's description here.
 * Creation date: (02/09/2003 16:34:32)
 * @return Gdima.basicagentcomponents.AgentIdentifier
 */
public dima.basicagentcomponents.AgentIdentifier getAcceptedProposer() {
	return this.acceptedProposer;
}
/**
 * Insert the method's description here.
 * Creation date: (24/06/2003 15:07:31)
 * @return Gdima.kernel.INAF.InteractionDomain.BiddingStrategy
 */
public BiddingStrategy getBiddingStrategy() {
	return this.biddingStrategy;
}
/**
 * Insert the method's description here.
 * Creation date: (25/06/2003 13:11:00)
 * @return Gdima.basicagentcomponents.AgentIdentifier
 */
public AgentIdentifier getInitiator() {
	return this.initiator;
}
/**
 * Insert the method's description here.
 * Creation date: (24/06/2003 15:21:55)
 * @return Gdima.kernel.INAF.InteractionDomain.Article
 */
public AbstractService getProposal() {
	return this.proposal;
}
/**
 * Insert the method's description here.
 * Creation date: (09/09/2003 13:08:57)
 * @return java.util.Date
 */
public java.util.Date getTimeOut() {
	return this.timeOut;
}
/**
 * Insert the method's description here.
 * Creation date: (01/09/2003 09:49:15)
 * @return boolean
 */
public boolean hasAcceptProposalMessage()
{
	return this.readMessage("AcceptProposal");
}
/**
 * Insert the method's description here.
 * Creation date: (01/09/2003 09:45:37)
 * @return boolean
 */
public boolean hasCFPMessage()
{
	return this.readMessage("CallForProposal");
}
/**
 * Insert the method's description here.
 * Creation date: (01/09/2003 09:50:45)
 * @return boolean
 */
public boolean hasInformAuctionWinnerMessage()
{
	return this.readMessage("InformAuctionWinner");
}
/**
 * Insert the method's description here.
 * Creation date: (01/09/2003 09:48:03)
 * @return boolean
 */
public boolean hasInformClosedAuctionMessage()
{
	return this.readMessage("InformClosedAuction");
}
/**
 * Insert the method's description here.
 * Creation date: (01/09/2003 09:47:01)
 * @return boolean
 */
public boolean hasInformNewPriceMessage()
{
	return this.readMessage("InformNewPrice");
}
/**
 * Insert the method's description here.
 * Creation date: (25/06/2003 16:52:01)
 */
public void initialize()
{
	this.setRoleName("EnglishAuctionParticipant");

	//Initial State
	final State start = new State("start");
	start.beInitial();
	this.setInitialState(start);

	//Two Final states

	final State failure = new State("failure");
	failure.beFinal();
	final State success = new State("success");
	success.beFinal();

	final Vector f = new Vector();
	f.add(success);
	f.add(failure);

	this.setFinalStates(f);

	//Two intermediate states : wait and endAuction

	final State wait = new State("wait");

	final State endAuction = new State("endAuction");

	// transitions between start and wait

	Vector v = new Vector();

	NamedCondition c = new NamedCondition("hasCFPMessage",this);

	NamedAction a = new NamedAction("decideToPropose",this);

	Transition2 t = new Transition2(c,a,wait);

	v.add(t);

	start.setTransitionList(v);

	// transitions from wait to start

	v = new Vector();

	c = new NamedCondition("hasInformNewPriceMessage",this);

	a = new NamedAction("upDateBetterProposal",this);

	t = new Transition2(c,a,start);

	v.add(t);

	// transitions from wait to endAuction

	c = new NamedCondition("hasInformClosedAuctionMessage",this);

	t = new Transition2(c,endAuction);

	v.add(t);

	// transitions from wait to wait

	c = new NamedCondition("acceptToPropose",this);

	a = new NamedAction("sendPropose",this);

	t = new Transition2(c,a,wait);

	v.add(t);

	wait.setTransitionList(v);

	// transitions from endAuction to success

	v = new Vector();

	c = new NamedCondition("hasAcceptProposalMessage",this);

	a = new NamedAction("sendConfirm",this);

	t = new Transition2(c,a,success);

	v.add(t);

	// transition from endAuction to failure

	c = new NamedCondition("hasInformAuctionWinnerMessage",this);

	t = new Transition2(c,failure);

	v.add(t);

	endAuction.setTransitionList(v);

}
/**
 * Insert the method's description here.
 * Creation date: (22/06/2003 22:28:30)
 */
public void sendConfirm()
{

}
/**
 * Insert the method's description here.
 * Creation date: (22/06/2003 22:28:18)
 */
public void sendPropose()
{
    final ACLPropose msg = new ACLPropose(this.getConversationId());

    msg.setProtocol("EnglishAuctionProtocol");

    msg.setContent(this.getProposal());

    this.getAgent().sendMessage(this.getInitiator(), msg);

    System.out.println(this.getAgent().getIdentifier() + " --> " + this.getInitiator() + " : I propose (" + this.getProposal() + ")...");
    this.setProposal(null);
}
/**
 * Insert the method's description here.
 * Creation date: (02/09/2003 16:34:32)
 * @param newAcceptedProposer Gdima.basicagentcomponents.AgentIdentifier
 */
public void setAcceptedProposer(final dima.basicagentcomponents.AgentIdentifier newAcceptedProposer) {
	this.acceptedProposer = newAcceptedProposer;
}
/**
 * Insert the method's description here.
 * Creation date: (24/06/2003 15:07:31)
 * @param newBiddingStrategy Gdima.kernel.INAF.InteractionDomain.BiddingStrategy
 */
public void setBiddingStrategy(final BiddingStrategy newBiddingStrategy) {
	this.biddingStrategy = newBiddingStrategy;
}
/**
 * Insert the method's description here.
 * Creation date: (25/06/2003 13:11:00)
 * @param newInitiator Gdima.basicagentcomponents.AgentIdentifier
 */
public void setInitiator(final AgentIdentifier newInitiator) {
	this.initiator = newInitiator;
}
/**
 * Insert the method's description here.
 * Creation date: (24/06/2003 15:21:55)
 * @param newProposal Gdima.kernel.INAF.InteractionDomain.Article
 */
public void setProposal(final AbstractService newProposal)
{
	this.proposal = newProposal;
}
/**
 * Insert the method's description here.
 * Creation date: (09/09/2003 13:08:57)
 * @param newTimeOut java.util.Date
 */
public void setTimeOut(final java.util.Date newTimeOut) {
	this.timeOut = newTimeOut;
}
/**
 * Insert the method's description here.
 * Creation date: (22/06/2003 20:07:42)
 */
public void upDateBetterProposal()
{
	final ACLInformNewPrice msg = (ACLInformNewPrice)this.getCurrentMessage();

	final Vector v = new Vector((Vector)msg.getContent());

	this.setAcceptedProposer((AgentIdentifier)v.get(0));

	this.setContract((AbstractService)v.get(1));


}
}
