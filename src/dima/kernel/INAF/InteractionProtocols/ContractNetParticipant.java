package dima.kernel.INAF.InteractionProtocols;

/**
 * Insert the type's description here.
 * Creation date: (26/03/2003 15:19:12)
 * @author: Tarek JARRAYA
 */

import java.util.Date;
import java.util.Vector;

import dima.basicagentcomponents.AgentIdentifier;
import dima.basicagentcomponents.AgentName;
import dima.kernel.INAF.InteractionAgents.InteractiveAgent;
import dima.kernel.INAF.InteractionDomain.AbstractService;
import dima.kernel.INAF.InteractionDomain.EvaluationStrategy;
import dima.kernel.INAF.InteractionTools.ActionExp;
import dima.kernel.INAF.InteractionTools.ConditionExp;
import dima.kernel.INAF.InteractionTools.Operator;
import dima.kernel.INAF.InteractionTools.Transition2;
import dima.ontologies.basicFIPAACLMessages.ACLInformDone;
import dima.ontologies.basicFIPAACLMessages.ACLPropose;
import dima.ontologies.basicFIPAACLMessages.ACLRefuse;
import dima.ontologies.basicFIPAACLMessages.FIPAACLMessage;
import dima.tools.agentInterface.NamedAction;
import dima.tools.agentInterface.NamedCondition;
import dima.tools.automata.ATN;
import dima.tools.automata.State;




public class ContractNetParticipant extends AbstractRole
{
	/**
	 *
	 */
	private static final long serialVersionUID = 4021808296679470974L;
	public AgentIdentifier initiator = new AgentName();
	public AbstractService proposal = null; // le service que propose l'agent en reponse au CFP re�u
	public Date timeOut = null;
	public EvaluationStrategy decisionStrategy;
	/**
	 * ContractNetParticipant constructor comment.
	 */
	public ContractNetParticipant()
	{

		super(ContractNetParticipant.buildATN());
		this.setRoleName("ContractNetParticipant");
		/* */ System.out.println("CONSTRUCTEUR DE CNET PARTICIPANT .....");
	}
	/**
	 * ContractNetParticipant constructor comment.
	 */
	public ContractNetParticipant(final InteractiveAgent agent,final String convId,final AgentIdentifier init,final AbstractService serv, final Date time)
	{
		super(ContractNetParticipant.buildATN());
		this.setRoleName("ContractNetParticipant");

		this.setAgent(agent);
		this.setConversationId(convId);
		this.setInitiator(init);
		this.setContract(serv);
		this.setTimeOut(time);
	}
	/**
	 * ContractNetParticipant constructor comment.
	 */
	public ContractNetParticipant(final InteractiveAgent agent,final String convId,final AgentIdentifier init,final AbstractService serv, final Date time, final EvaluationStrategy strg)
	{
		super(ContractNetParticipant.buildATN());
		this.setRoleName("ContractNetParticipant");

		this.setAgent(agent);
		this.setConversationId(convId);
		this.setInitiator(init);
		this.setContract(serv);
		this.setTimeOut(time);
		this.setEvaluationStrategy(strg);
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (02/04/03 00:35:02)
	 * @return boolean
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
		Vector s = new Vector();
		s.add(failure);
		final State success = new State("success");
		success.beFinal();
		s.add(success);
		atn.setFinalStates(s);

		//Two intermediate states : wait and Decision
		final State wait = new State("wait");
		final State decision = new State("decision");
		s = new Vector();
		s.add(wait);
		s.add(decision);

		// transition from start to decision

		NamedCondition c = new NamedCondition("hasCFPMessage");

		NamedAction a = new NamedAction("initialize");

		NamedAction b = new NamedAction("decideToPropose");

		Operator oper = new Operator("AND");

		Transition2 t = new Transition2(new ConditionExp(c), new ActionExp(a, oper, b), decision);

		Vector v = new Vector();

		v.add(t);

		start.setTransitionList(v);

		// transition from decision to wait

		c = new NamedCondition("acceptToPropose");

		a = new NamedAction("sendPropose");

		t = new Transition2(c, a, wait);

		v = new Vector();

		v.add(t);

		// transition from decision to failure

		c = new NamedCondition("refuseToPropose");

		a = new NamedAction("sendRefuse");

		b = new NamedAction("setFailureState");

		oper = new Operator("AND");
		ActionExp aExp = new ActionExp(a, oper, b);

		t = new Transition2(new ConditionExp(c), aExp, failure);

		v.add(t);

		decision.setTransitionList(v);

		// transition from wait to success

		c = new NamedCondition("hasAcceptProposalMessage");

		a = new NamedAction("performContract");

		b = new NamedAction("sendInformDone");

		final NamedAction d = new NamedAction("setSuccessState");

		oper = new Operator("AND");
		aExp = new ActionExp(a, oper, b);
		final ActionExp aExp1 = new ActionExp(d,oper, aExp);

		t = new Transition2(new ConditionExp(c), aExp, success);

		v = new Vector();

		v.add(t);

		// transition from wait to failure

		c = new NamedCondition("hasRejectProposalMessage");

		a = new NamedAction("setFailureState");

		t = new Transition2(c, a, failure);

		v.add(t);
		wait.setTransitionList(v);
		return atn;
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (26/03/2003 14:26:10)
	 */
	public void decideToPropose()
	{
		final InteractiveAgent agent = this.getAgent();

		this.getDecisionStrategy().setProposals(agent.getServices());

		this.setProposal((AbstractService) this.getDecisionStrategy().execute());

		agent.removeService(this.getProposal());
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (02/04/03 00:37:37)
	 */
	public void failure()
	{
		//if(getProposal() != null)
		//{
		//getAgent().addService(getProposal());
		//}
		this.setFailureState();
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (23/04/2003 11:37:55)
	 * @return dima.kernel.communicatingAgent.domainOfInteraction.EvaluationStrategy
	 */
	public EvaluationStrategy getDecisionStrategy()
	{
		return this.decisionStrategy;
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (27/03/2003 11:27:17)
	 * @return dima.kernel.communicatingAgent.interaction.ContractNetInitiator
	 */
	public AgentIdentifier getInitiator()
	{
		return this.initiator;
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (27/03/2003 12:58:39)
	 * @return dima.kernel.communicatingAgent.domain.Contract
	 */
	public AbstractService getProposal()
	{
		return this.proposal;
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (05/04/03 16:33:05)
	 * @return int
	 */
	public Date getTimeOut()
	{
		return this.timeOut;
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (02/09/2003 10:32:33)
	 * @return boolean
	 */
	public boolean hasAcceptProposalMessage()
	{
		return this.readMessage("AcceptProposal");
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (02/09/2003 10:31:32)
	 * @return boolean
	 */
	public boolean hasCFPMessage()
	{
		return this.readMessage("CallForProposal");
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (02/09/2003 10:33:12)
	 * @return boolean
	 */
	public boolean hasRejectProposalMessage()
	{
		return this.readMessage("RejectProposal");
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (14/04/2003 14:22:28)
	 */
	public void initialize()
	{
		final InteractiveAgent agent = this.getAgent();

		final FIPAACLMessage msg = this.getCurrentMessage();

		this.setContract((AbstractService)msg.getContent());
		this.setInitiator(msg.getSender());
		this.setTimeOut(msg.getReplyBy());
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (02/04/03 00:38:05)
	 */
	public void performContract()
	{
		//getAgent().performContract(getContract(),getProposal());
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (02/04/03 00:35:27)
	 * @return boolean
	 */
	public boolean refuseToPropose()
	{
		return this.getProposal()==null;
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (26/03/2003 14:28:16)
	 */
	public void sendInformDone()
	{
		final ACLInformDone msg = new ACLInformDone(this.getConversationId());

		msg.setProtocol("FIPAContractNetProtocol");
		msg.setContent(this.getProposal());

		this.getAgent().sendMessage(this.getInitiator(), msg);

		System.out.println(this.getAgent().getIdentifier() + " --> " + this.getInitiator() + " : Inform Done....");
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (26/03/2003 14:27:23)
	 */
	public void sendPropose()
	{
		final ACLPropose msg = new ACLPropose(this.getConversationId());

		msg.setProtocol("FIPAContractNetProtocol");
		msg.setContent(this.getProposal());

		this.getAgent().sendMessage(this.getInitiator(), msg);

		System.out.println(this.getAgent().getIdentifier() + " --> " + this.getInitiator() + " : I propose (" + this.getProposal() + ")...");
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (26/03/2003 14:27:50)
	 */
	public void sendRefuse()
	{
		final ACLRefuse msg = new ACLRefuse(this.getConversationId());

		msg.setProtocol("FIPAContractNetProtocol");

		this.getAgent().sendMessage(this.getInitiator(), msg);

		System.out.println(this.getAgent().getIdentifier() + " --> " + this.getInitiator() + " : I refuse to propose...");
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (23/04/2003 11:37:55)
	 * @param newDecisionStrategy dima.kernel.communicatingAgent.domainOfInteraction.EvaluationStrategy
	 */
	public void setEvaluationStrategy(final EvaluationStrategy newDecisionStrategy)
	{
		this.decisionStrategy = newDecisionStrategy;
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (27/03/2003 11:27:17)
	 * @param newInitiator dima.kernel.communicatingAgent.interaction.ContractNetInitiator
	 */
	public void setInitiator(final AgentIdentifier newInitiator)
	{
		this.initiator = newInitiator;
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (27/03/2003 12:58:39)
	 * @param newProposal dima.kernel.communicatingAgent.domain.Contract
	 */
	public void setProposal(final AbstractService newProposal)
	{
		this.proposal = newProposal;
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (05/04/03 16:33:05)
	 * @param newTimeOut int
	 */
	public void setTimeOut(final Date newTimeOut)
	{
		this.timeOut = newTimeOut;
	}
}
