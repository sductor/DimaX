package dima.kernel.INAF.InteractionProtocols;

/**
 * Insert the type's description here.
 * Creation date: (30/05/2003 19:04:40)
 * @author: Tarek JARRAYA
 */
import java.util.Date;
import java.util.Enumeration;
import java.util.Vector;

import dima.basicagentcomponents.AgentIdentifier;
import dima.kernel.INAF.InteractionAgents.InteractiveAgent;
import dima.kernel.INAF.InteractionDomain.AbstractService;
import dima.kernel.INAF.InteractionTools.ActionExp;
import dima.kernel.INAF.InteractionTools.ConditionExp;
import dima.kernel.INAF.InteractionTools.Operator;
import dima.kernel.INAF.InteractionTools.Transition2;
import dima.ontologies.basicFIPAACLMessages.ACLAcceptProposal;
import dima.ontologies.basicFIPAACLMessages.ACLCallForProposal;
import dima.ontologies.basicFIPAACLMessages.ACLInformAuctionWinner;
import dima.ontologies.basicFIPAACLMessages.ACLInformClosedAuction;
import dima.ontologies.basicFIPAACLMessages.ACLInformFailedAuction;
import dima.ontologies.basicFIPAACLMessages.ACLInformNewPrice;
import dima.ontologies.basicFIPAACLMessages.ACLPropose;
import dima.tools.agentInterface.NamedAction;
import dima.tools.agentInterface.NamedCondition;
import dima.tools.automata.ATN;
import dima.tools.automata.State;



public class EnglishAuctionInitiator extends AbstractRole
{
	/**
	 *
	 */
	private static final long serialVersionUID = 1758222729110535954L;

	public Vector participants; // les identifiants des agents participants � l'ench�re

	public Date replayDelay; //un delai d'attente pour r�pondre au cfp envoy�.

	public AgentIdentifier acceptedProposer; // il contient l'identifiant du participant qui propose la meilleure offre

	public Date timeOut;	//il est calcul� � partir de la date courante incr�ment�e de la valeur de l'attribut replay delay.
	/**
	 * EnglishAuctionInitiator constructor comment.
	 */
	public EnglishAuctionInitiator()
	{
		super(EnglishAuctionInitiator.buildATN()); // construire l'ATN du r�le
		this.setRoleName("EnglishAuctionInitiator");
	}
	/**
	 * EnglishAuctionInitiator constructor comment.
	 */
	public EnglishAuctionInitiator(final InteractiveAgent agent,final String convId,final Vector buyers,final AbstractService contract,final Date delay)
	{
		super(EnglishAuctionInitiator.buildATN()); // construire l'ATN du r�le
		this.setRoleName("EnglishAuctionInitiator");


		this.setAgent(agent);
		this.setConversationId(convId);
		this.setParticipants(buyers);
		this.setContract(contract); //instancier l'article mis en ench�re
		this.setReplayDelay(delay); // un delai d'attente pour r�pondre au cfp envoy�
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (25/06/2003 16:54:22)
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

		//Three intermediate states : wait, bidding and endAuction

		final State wait = new State("wait");
		final State bidding = new State("bidding");
		final State endAuction = new State("endAuction");

		// transition from start to wait

		Vector v = new Vector();

		NamedCondition c = new NamedCondition("isInitialized");

		NamedAction a = new NamedAction("sendCallForProposal");

		Transition2 t = new Transition2(c,a,wait);

		v.add(t);

		start.setTransitionList(v);

		// transition from wait to bidding

		v = new Vector();

		NamedCondition c1 = new NamedCondition("hasProposeMessage");

		NamedCondition c2 = new NamedCondition("validProposal");

		a = new NamedAction("upDateBetterProposal");

		ConditionExp condExp = new ConditionExp(c1,new Operator("AND"),c2);

		t = new Transition2(condExp,new ActionExp(a),bidding);

		v.add(t);

		// transition from wait to endAuction

		c = new NamedCondition("expiredReplayDelay");

		a = new NamedAction("sendInformClosedAuction");

		t = new Transition2(c,a,endAuction);

		v.add(t);

		wait.setTransitionList(v);

		// transitions from bidding to bidding

		v = new Vector();

		c1 = new NamedCondition("hasProposeMessage");

		c2 = new NamedCondition("validProposal");

		a = new NamedAction("upDateBetterProposal");

		condExp = new ConditionExp(c1,new Operator("AND"),c2);

		t = new Transition2(condExp,new ActionExp(a),bidding);

		v.add(t);

		// transitions from bidding to wait

		c = new NamedCondition("hasNoProposal");

		NamedAction a1 = new NamedAction("sendInformNewPrice");
		NamedAction a2 = new NamedAction("sendCallForProposal");

		ActionExp aExp = new ActionExp(a1,new Operator("AND"),a2);

		t = new Transition2(new ConditionExp(c),aExp,wait);

		v.add(t);

		bidding.setTransitionList(v);

		// transitions from endAuction to success

		v = new Vector();

		c = new NamedCondition("hasWinner");

		a1 = new NamedAction("sendInformAuctionWinner");

		a2 = new NamedAction("sendAcceptProposal");

		aExp = new ActionExp(a1,new Operator("AND"),a2);

		t = new Transition2(new ConditionExp(c),aExp,success);

		v.add(t);

		// transition from endAuction to failure

		c = new NamedCondition("noWinner");

		a = new NamedAction("sendInformFailedAuction");

		t = new Transition2(c,a,failure);

		v.add(t);

		endAuction.setTransitionList(v);

		return atn;
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (22/06/2003 22:35:48)
	 */
	public boolean expiredReplayDelay()
	{
		return this.getTimeOut().before(new Date(System.currentTimeMillis()));
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (24/06/2003 14:49:58)
	 * @return Gdima.basicagentcomponents.AgentIdentifier
	 */
	public AgentIdentifier getAcceptedProposer() {
		return this.acceptedProposer;
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (23/06/2003 15:40:50)
	 * @return java.util.Vector
	 */
	public Vector getParticipants() {
		return this.participants;
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (23/06/2003 15:17:24)
	 * @return java.util.Date
	 */
	public Date getReplayDelay()
	{
		return this.replayDelay;
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (25/06/2003 17:20:15)
	 * @return java.util.Date
	 */
	public java.util.Date getTimeOut() {
		return this.timeOut;
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (27/06/2003 23:04:24)
	 * @return boolean
	 */
	public boolean hasNoProposal()
	{
		return !this.hasMessage("Propose");
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (01/09/2003 08:42:05)
	 * @return boolean
	 */
	public boolean hasProposeMessage()
	{
		return this.readMessage("Propose");
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (22/06/2003 22:38:25)
	 * @return boolean
	 */
	public boolean hasWinner()
	{
		return this.acceptedProposer != null;
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (22/06/2003 22:35:00)
	 */
	public boolean isInitialized()
	{
		return true;
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (22/06/2003 22:39:05)
	 * @return boolean
	 */
	public boolean noWinner()
	{
		return this.acceptedProposer == null;
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (22/06/2003 22:24:48)
	 */
	public void sendAcceptProposal()
	{
		final InteractiveAgent agent = this.getAgent();

		final ACLAcceptProposal msg = new ACLAcceptProposal(this.getConversationId());

		msg.setContent(this.getContract());
		msg.setProtocol("EnglishAuctionProtocol");
		agent.sendMessage(this.getAcceptedProposer(), msg);

		System.out.println(agent.getIdentifier() + " --> " + this.getAcceptedProposer() + " : I accept your Proposal...");
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (22/06/2003 22:26:43)
	 */
	public void sendCallForProposal()
	{
		final ACLCallForProposal msg = new ACLCallForProposal(this.getConversationId());

		msg.setProtocol("EnglishAuctionProtocol");
		msg.setContent(this.getContract());
		msg.setSender(this.getAgent().getIdentifier());

		final Date d = new Date(System.currentTimeMillis() + this.getReplayDelay().getTime());

		msg.setReplyBy(d);

		this.setTimeOut(d);

		final Enumeration e = this.getParticipants().elements();

		while (e.hasMoreElements()) {
			this.getAgent().sendMessage((AgentIdentifier) e.nextElement(), msg);
		}

		System.out.println(this.getAgent().getIdentifier() + " --> " + this.getParticipants() + " : Call For Proposal (" + this.getContract() + ")...");
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (01/09/2003 18:33:34)
	 */
	public void sendInformAuctionWinner()
	{
		final ACLInformAuctionWinner msg = new ACLInformAuctionWinner(this.getConversationId());

		msg.setProtocol("EnglishAuctionProtocol");
		msg.setSender(this.getAgent().getIdentifier());

		final Vector v = new Vector();
		v.add(this.getContract());
		v.add(this.getAcceptedProposer());
		msg.setContent(v); // le message contient l'identifiant du winner et de sa proposition

		final Enumeration e = this.getParticipants().elements();

		while (e.hasMoreElements()) {
			this.getAgent().sendMessage((AgentIdentifier) e.nextElement(), msg);
		}
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (22/06/2003 22:24:12)
	 */
	public void sendInformClosedAuction()
	{
		final ACLInformClosedAuction msg = new ACLInformClosedAuction(this.getConversationId());

		msg.setProtocol("EnglishAuctionProtocol");
		msg.setSender(this.getAgent().getIdentifier());

		final Enumeration e = this.getParticipants().elements();

		while (e.hasMoreElements()) {
			this.getAgent().sendMessage((AgentIdentifier) e.nextElement(), msg);
		}

		System.out.println(this.getAgent().getIdentifier() + " --> " + this.getParticipants() + " : Auction is Closed...");
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (22/06/2003 22:25:27)
	 */
	public void sendInformFailedAuction()
	{
		final ACLInformFailedAuction msg = new ACLInformFailedAuction(this.getConversationId());

		msg.setProtocol("EnglishAuctionProtocol");
		msg.setSender(this.getAgent().getIdentifier());

		final Enumeration e = this.getParticipants().elements();

		while (e.hasMoreElements()) {
			this.getAgent().sendMessage((AgentIdentifier) e.nextElement(), msg);
		}

		System.out.println(this.getAgent().getIdentifier() + " --> " + this.getParticipants() + " : Auction is Failed...");
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (22/06/2003 22:23:29)
	 */
	public void sendInformNewPrice()
	{
		final ACLInformNewPrice msg = new ACLInformNewPrice(this.getConversationId());

		msg.setProtocol("EnglishAuctionProtocol");
		msg.setSender(this.getAgent().getIdentifier());

		final Vector v = new Vector();
		v.add(this.getAcceptedProposer());
		v.add(this.getContract());
		msg.setContent(v); //le message contient l'identifiant de l'acheteur et de sa proposition

		final Enumeration e = this.getParticipants().elements();

		while (e.hasMoreElements()) {
			if (!((AgentIdentifier)e.nextElement()).equals(this.getAcceptedProposer()))
			{
				this.getAgent().sendMessage((AgentIdentifier) e.nextElement(),msg);
				System.out.println(" � vous de proposer : "+e.nextElement());
			}
		}

		System.out.println(this.getAgent().getIdentifier() + " --> " + this.getParticipants() + " : The current accepted proposal is :"+this.getContract()+" the sender : "+this.getAcceptedProposer());
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (24/06/2003 14:49:58)
	 * @param newAcceptedProposer Gdima.basicagentcomponents.AgentIdentifier
	 */
	public void setAcceptedProposer(final AgentIdentifier newAcceptedProposer) {
		this.acceptedProposer = newAcceptedProposer;
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (23/06/2003 15:40:50)
	 * @param newParticipants java.util.Vector
	 */
	public void setParticipants(final Vector newParticipants) {
		this.participants = newParticipants;
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (23/06/2003 15:17:24)
	 * @param newReplayDelay java.util.Date
	 */
	public void setReplayDelay(final Date newReplayDelay) {
		this.replayDelay = newReplayDelay;
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (25/06/2003 17:20:15)
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
		final ACLPropose msg = (ACLPropose)this.getCurrentMessage();

		this.setContract((AbstractService)msg.getContent());

		this.setAcceptedProposer(msg.getSender());
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (25/06/2003 17:42:38)
	 * @return boolean
	 */
	public boolean validProposal()
	{
		if (((AbstractService)this.getCurrentMessage().getContent()).isBetterThan(this.getContract()))
		{
			this.setContract((AbstractService)this.getCurrentMessage().getContent());
			return true;
		} else {
			return false;
		}
	}
}
