package dima.kernel.INAF.InteractionProtocols;

/**
 * Insert the type's description here.
 * Creation date: (19/03/03 22:06:31)
 * @author: Tarek JARRAYA
 */

import java.util.Date;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

import dima.basicagentcomponents.AgentIdentifier;
import dima.kernel.INAF.InteractionAgents.InteractiveAgent;
import dima.kernel.INAF.InteractionDomain.AbstractService;
import dima.kernel.INAF.InteractionDomain.EvaluationStrategy;
import dima.kernel.INAF.InteractionTools.ActionExp;
import dima.kernel.INAF.InteractionTools.ConditionExp;
import dima.kernel.INAF.InteractionTools.Operator;
import dima.kernel.INAF.InteractionTools.Transition2;
import dima.ontologies.basicFIPAACLMessages.ACLAcceptProposal;
import dima.ontologies.basicFIPAACLMessages.ACLCallForProposal;
import dima.ontologies.basicFIPAACLMessages.ACLRejectProposal;
import dima.tools.agentInterface.NamedAction;
import dima.tools.agentInterface.NamedCondition;
import dima.tools.automata.ATN;
import dima.tools.automata.State;




public class ContractNetInitiator extends AbstractRole
{
	/**
	 *
	 */
	private static final long serialVersionUID = 5079695149238710257L;
	public Date timeOut = null;
	public Vector participants = new Vector(); // les identifiants des agents participants
	public Hashtable receivedProposals = new Hashtable(); //des couples (proposer, proposal)
	public AbstractService acceptedProposal = null;
	public AgentIdentifier acceptedProposer;
	public EvaluationStrategy evaluationStrategy;
	/**
	 * InitiatorContractNetProtocol constructor comment.
	 */
	public ContractNetInitiator()
	{
		super(ContractNetInitiator.buildATN());

		this.setRoleName("ContractNetInitiator");
	}
	/**
	 * InitiatorContractNetProtocol constructor comment.
	 */
	public ContractNetInitiator(final InteractiveAgent agent,final String convId,final Vector agents,final AbstractService c,final Date time)
	{
		super(ContractNetInitiator.buildATN());
		this.setRoleName("ContractNetInitiator");

		this.setAgent(agent);
		this.setConversationId(convId);
		this.setParticipants(agents);
		this.setContract(c);
		this.setTimeOut(time);
	}
	/**
	 * InitiatorContractNetProtocol constructor comment.
	 */
	public ContractNetInitiator(final InteractiveAgent agent,final String convId,final Vector agents,final AbstractService c,final Date time, final EvaluationStrategy strg)
	{
		super(ContractNetInitiator.buildATN());
		this.setRoleName("ContractNetInitiator");

		this.setAgent(agent);
		this.setConversationId(convId);
		this.setParticipants(agents);
		this.setContract(c);
		this.setTimeOut(time);
		this.setEvaluationStrategy(strg);

	}
	/**
	 * Insert the method's description here.
	 * Creation date: (31/03/03 01:25:08)
	 * @return boolean
	 */
	public boolean acceptProposal()
	{
		return this.getAcceptedProposal()!=null;
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (12/04/2003 09:57:23)
	 */
	public void addParticipant(final AgentIdentifier agent)
	{
		this.participants.add(agent);
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (07/04/2003 10:56:50)
	 */
	public void addProposal()
	{
		final AbstractService proposal = (AbstractService) this.getCurrentMessage().getContent();
		final AgentIdentifier proposer = this.getCurrentMessage().getSender();

		this.receivedProposals.put(proposer,proposal);
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
		Vector s = new Vector();
		s.add(failure);
		final State success = new State("success");
		success.beFinal();
		s.add(success);
		atn.setFinalStates(s);

		//Two intermediate states : wait and Decision
		final State wait = new State("wait");
		final State decision = new State("decision");
		final State confirmWait = new State("confirmWait");
		s = new Vector();
		s.add(wait);
		s.add(decision);
		s.add(confirmWait);


		// transition from start to wait

		Vector v = new Vector();

		NamedCondition c = new NamedCondition("isInitialized");

		NamedAction a = new NamedAction("sendCallForProposal");

		Transition2 t = new Transition2(c, a, wait);

		v.add(t);
		start.setTransitionList(v);

		// transition from wait to wait

		v = new Vector();

		final NamedCondition c1 = new NamedCondition("hasProposeMessage");

		c = new NamedCondition("noExpiredTimeOut");

		a = new NamedAction("addProposal");

		final ConditionExp cExp = new ConditionExp(c1, new Operator("AND"), c);

		t = new Transition2(cExp, new ActionExp(a), wait);

		v.add(t);

		// transition from wait to wait

		c = new NamedCondition("hasRefuseMessage");

		a = new NamedAction("removeParticipant");

		t = new Transition2(c,a,wait);

		v.add(t);

		// transition from wait to decision

		c = new NamedCondition("expiredTimeOut");

		a = new NamedAction("evaluateProposals");

		t = new Transition2(c, a, decision);

		v.add(t);

		wait.setTransitionList(v);

		// transitions from decision to confirmWait

		v = new Vector();

		c = new NamedCondition("acceptProposal");

		a = new NamedAction("sendAcceptProposal");

		NamedAction b = new NamedAction("sendRejectProposal");

		final Operator oper = new Operator("AND");
		ActionExp aExp = new ActionExp(a, oper, b);

		t = new Transition2(new ConditionExp(c), aExp, confirmWait);

		v.add(t);

		// transition from decision to failure

		c = new NamedCondition("refuseProposal");

		a = new NamedAction("sendRejectAllProposals");

		b = new NamedAction("setFailureState");

		aExp = new ActionExp(a, oper, b);

		t = new Transition2(new ConditionExp(c), aExp, failure);

		//t = new Transition2(c, a, failure);

		v.add(t);
		decision.setTransitionList(v);

		// transition from confirmWait to success

		v = new Vector();

		c = new NamedCondition("hasInformDoneMessage");

		a = new NamedAction("setSuccessState");

		t = new Transition2(c,a, success);

		v.add(t);

		confirmWait.setTransitionList(v);
		return atn;
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (07/04/2003 10:56:03)
	 */
	public void evaluateProposals()
	{
		final InteractiveAgent agent = this.getAgent();

		System.out.println(agent.getIdentifier() + " (" + this.getConversationId() + ") : evaluate proposals....");

		this.getEvaluationStrategy().setProposals(new Vector(this.getReceivedProposals().values()));

		final AbstractService proposal = (AbstractService) this.getEvaluationStrategy().execute();

		//System.out.println(agent.getIdentifier() + " (" + getConversationId() + ") : accepted proposal, "+proposal);
		try
		{
			this.setAcceptedProposal(proposal);
			int i = 0;
			final Vector v = new Vector(this.getReceivedProposals().keySet());

			while (i < v.size())
			{
				if (proposal.equals(this.getReceivedProposals().get(v.get(i))))
				{
					this.setAcceptedProposer((AgentIdentifier) v.get(i));
					break;
				}
				i++;
			}

		}
		catch (final NullPointerException e)
		{
			System.out.println(agent.getIdentifier() + " (" + this.getConversationId() + ") : negotiation fail....");
		}
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (23/03/03 00:36:56)
	 * @param currentTime int
	 */
	public boolean expiredTimeOut()
	{
		return this.getTimeOut().before(new Date(System.currentTimeMillis()));
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (23/04/2003 00:28:05)
	 * @return dima.kernel.communicatingAgent.domainOfInteraction.AbstractService
	 */
	public AbstractService getAcceptedProposal()
	{
		return this.acceptedProposal;
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (22/04/2003 10:24:56)
	 * @return Gdima.basicagentcomponents.AgentIdentifier
	 */
	public dima.basicagentcomponents.AgentIdentifier getAcceptedProposer()
	{
		return this.acceptedProposer;
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (22/04/2003 23:50:20)
	 * @return dima.kernel.communicatingAgent.domainOfInteraction.EvaluationStrategy
	 */
	public EvaluationStrategy getEvaluationStrategy()
	{
		return this.evaluationStrategy;
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (23/03/03 00:04:11)
	 * @return java.util.List
	 */
	public Vector getParticipants()
	{
		return this.participants;
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (23/03/03 12:26:09)
	 * @return java.util.Vector
	 */
	public Hashtable getReceivedProposals()
	{
		return this.receivedProposals;
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (23/03/03 00:38:19)
	 * @return int
	 */
	public Date getTimeOut()
	{
		return this.timeOut;
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (02/09/2003 10:41:04)
	 */
	public boolean hasInformDoneMessage()
	{
		return this.readMessage("InformDone");
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (02/09/2003 10:39:51)
	 */
	public boolean hasProposeMessage()
	{
		return this.readMessage("Propose");
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (02/09/2003 10:40:15)
	 */
	public boolean hasRefuseMessage()
	{
		return this.readMessage("Refuse");
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (20/03/2003 11:21:40)
	 */
	public boolean isInitialized()
	{
		if (this.contract != null && this.timeOut != null && this.participants != null)
			return true;
		else
		{
			System.out.println("ERREUR !! "+this.getAgent()+ " : le role n'est pas correctement initialis�");
			return false;
		}
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (23/03/03 00:36:56)
	 * @param currentTime int
	 */
	public boolean noExpiredTimeOut()
	{
		return this.getTimeOut().after(new Date(System.currentTimeMillis()));
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (31/03/03 01:25:38)
	 * @return boolean
	 */
	public boolean refuseProposal()
	{
		return this.getAcceptedProposal() == null;
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (07/04/2003 10:57:24)
	 */
	public void removeParticipant()
	{
		this.removeParticipant(this.getCurrentMessage().getSender());
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (06/04/03 14:37:42)
	 * @param participantId Gdima.basicagentcomponents.AgentIdentifier
	 */
	public void removeParticipant(final AgentIdentifier participantId)
	{
		this.participants.remove(participantId);
		System.out.println(" les participants restants : "+this.participants);
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (07/04/2003 10:57:51)
	 */
	public void sendAcceptProposal()
	{
		final InteractiveAgent agent = this.getAgent();

		final ACLAcceptProposal msg = new ACLAcceptProposal(this.getConversationId());

		msg.setContent(this.getAcceptedProposal());
		msg.setProtocol("FIPAContractNetProtocol");
		agent.sendMessage(this.getAcceptedProposer(), msg);

		System.out.println(agent.getIdentifier() + " --> " + this.getAcceptedProposer() + " : I accept your Proposal...");
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (07/04/2003 10:58:31)
	 */
	public void sendCallForProposal()
	{
		final ACLCallForProposal msg = new ACLCallForProposal(this.getConversationId());

		msg.setProtocol("FIPAContractNetProtocol");
		msg.setContent(this.getContract());
		msg.setSender(this.getAgent().getIdentifier());
		msg.setReplyBy(this.getTimeOut());

		final Enumeration e = this.getParticipants().elements();

		while (e.hasMoreElements())
			this.getAgent().sendMessage((AgentIdentifier) e.nextElement(), msg);

		System.out.println(this.getAgent().getIdentifier() + " --> " + this.getParticipants() + " : Call For Proposal (" + this.getConversationId() + ") with contract "+this.getContract());
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (14/04/2003 14:07:14)
	 */
	public void sendRejectAllProposals()
	{
		final InteractiveAgent agent = this.getAgent();

		final ACLRejectProposal msg = new ACLRejectProposal(this.getConversationId());

		msg.setProtocol("FIPAContractNetProtocol");

		if (!this.getParticipants().isEmpty())
		{
			final Enumeration e = this.getParticipants().elements();

			while (e.hasMoreElements())
			{
				final AgentIdentifier agentId = (AgentIdentifier) e.nextElement();
				agent.sendMessage(agentId, msg);
				System.out.println(agent.getIdentifier() + " --> " + agentId + " : Reject your Proposal (all).....");
			}
		}

	}
	/**
	 * Insert the method's description here.
	 * Creation date: (07/04/2003 10:59:11)
	 */
	public void sendRejectProposal()
	{
		final InteractiveAgent agent = this.getAgent();

		final ACLRejectProposal msg = new ACLRejectProposal(this.getConversationId());

		msg.setProtocol("FIPAContractNetProtocol");

		final Vector others = new Vector(this.getParticipants());

		others.remove(this.getAcceptedProposer());

		if (!others.isEmpty())
		{
			final Enumeration e = others.elements();

			while (e.hasMoreElements())
			{
				final AgentIdentifier agentId = (AgentIdentifier) e.nextElement();
				agent.sendMessage(agentId, msg);
				System.out.println(agent.getIdentifier() + " --> " + agentId + " : Reject your Proposal.....");
			}
		}

	}
	/**
	 * Insert the method's description here.
	 * Creation date: (23/04/2003 00:28:05)
	 * @param newAcceptedProposal dima.kernel.communicatingAgent.domainOfInteraction.AbstractService
	 */
	public void setAcceptedProposal(final AbstractService newAcceptedProposal)
	{
		this.acceptedProposal = newAcceptedProposal;
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (22/04/2003 10:24:56)
	 * @param newAcceptedProposer Gdima.basicagentcomponents.AgentIdentifier
	 */
	public void setAcceptedProposer(final dima.basicagentcomponents.AgentIdentifier newAcceptedProposer)
	{
		this.acceptedProposer = newAcceptedProposer;
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (22/04/2003 23:50:20)
	 * @param newEvaluationStrategy dima.kernel.communicatingAgent.domainOfInteraction.EvaluationStrategy
	 */
	public void setEvaluationStrategy(final EvaluationStrategy newEvaluationStrategy)
	{
		this.evaluationStrategy = newEvaluationStrategy;
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (23/03/03 00:04:11)
	 * @param newParticipants java.util.List
	 */
	public void setParticipants(final Vector newParticipants)
	{
		this.participants = new Vector(newParticipants);
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (23/03/03 00:38:19)
	 * @param newTimeOut int
	 */
	public void setTimeOut(final Date newTimeOut)
	{
		this.timeOut = newTimeOut;
	}
}
