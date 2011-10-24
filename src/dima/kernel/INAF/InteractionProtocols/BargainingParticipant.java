package dima.kernel.INAF.InteractionProtocols;

/**
 * Insert the type's description here.
 * Creation date: (21/05/2003 16:22:34)
 * @author: Tarek JARRAYA
 */

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
import dima.ontologies.basicFIPAACLMessages.ACLCounterPropose;
import dima.ontologies.basicFIPAACLMessages.ACLInformDone;
import dima.ontologies.basicFIPAACLMessages.ACLStopNegotiation;
import dima.tools.agentInterface.NamedAction;
import dima.tools.agentInterface.NamedCondition;
import dima.tools.automata.ATN;
import dima.tools.automata.State;



public class BargainingParticipant extends AbstractRole
{
	/**
	 *
	 */
	private static final long serialVersionUID = 8560667340477513955L;
	public dima.basicagentcomponents.AgentIdentifier opponent;
	public AbstractService receivedProposal;
	public boolean decision = false;
	public EvaluationStrategy evaluationStrategy;
	public AbstractService proposal;
/**
 * BargainingParticipant constructor comment.
 */
public BargainingParticipant()
{
    super(buildATN());

    this.setRoleName("BargainingParticipant");
}
/**
 * BargainingParticipant constructor comment.
 */
public BargainingParticipant(final InteractiveAgent agent,final String convId,final AgentIdentifier init,final AbstractService serv)
{
    super(buildATN());

    this.setRoleName("BargainingParticipant");
    this.setAgent(agent);
	this.setConversationId(convId);
	this.setOpponent(init);
	this.setContract(serv);
}
/**
 * BargainingParticipant constructor comment.
 */
public BargainingParticipant(final InteractiveAgent agent,final String convId,final AgentIdentifier init,final AbstractService serv, final EvaluationStrategy strg)
{
    super(buildATN());

    this.setRoleName("BargainingParticipant");
     this.setAgent(agent);
	this.setConversationId(convId);
	this.setOpponent(init);
	this.setContract(serv);
	this.setEvaluationStrategy(strg);
}
/**
 * Insert the method's description here.
 * Creation date: (22/05/2003 18:01:35)
 */
public boolean acceptProposal()
{
	return this.decision;
}
/**
 * Insert the method's description here.
 * Creation date: (15/04/2003 12:05:31)
 */
public void addContract()
{
    final InteractiveAgent agent = this.getAgent();
}
/**
 * Insert the method's description here.
 * Creation date: (07/04/2003 10:56:50)
 */
public void addProposal()
{
    final AbstractService proposal = (AbstractService) this.getCurrentMessage().getContent();

    this.setReceivedProposal(proposal);
    this.setOpponent(this.getCurrentMessage().getSender());

    System.out.println(this.getAgent().getIdentifier() + " : " + this.getConversationId() + " : L'ajout de la proposition : " + proposal);
}
/**
 * BargainingParticipant constructor comment.
 */
public static ATN buildATN()
{
	final ATN atn = new ATN();

	//Initial State
    final State start = new State("start");
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

    //Three intermediate states : wait, evaluate and continueDecision

    final State wait = new State("wait");
    final State evaluate = new State("evaluate");
    final State continueDecision = new State("continueDecision");
    final State confirmWait = new State("confirmWait");
    s = new Vector();
    s.add(wait);
    s.add(evaluate);
    s.add(continueDecision);
    s.add(confirmWait);

    // transition from start to evaluate

    Vector v = new Vector();

    NamedCondition c = new NamedCondition("hasProposeMessage");

    NamedAction a = new NamedAction("addProposal");

    NamedAction b = new NamedAction("evaluateProposal");

    ActionExp aExp = new ActionExp(a, new Operator("AND"), b);

    Transition2 t = new Transition2(new ConditionExp(c), aExp, evaluate);

    v.add(t);

    start.setTransitionList(v);

    // transition from evaluate to confirmWait

    v = new Vector();

    c = new NamedCondition("acceptProposal");

    a = new NamedAction("sendAcceptProposal");

    t = new Transition2(c, a, confirmWait);

    v.add(t);

    // transition from evaluate to continueDecision

    c = new NamedCondition("refuseProposal");

    a = new NamedAction("decideToContinue");

    t = new Transition2(c, a, continueDecision);

    v.add(t);

    evaluate.setTransitionList(v);

    // transition from confirmWait to success

    v = new Vector();

    c = new NamedCondition("hasInformDoneMessage");

    a = new NamedAction("addContract");

    t = new Transition2(c, a, success);

    v.add(t);

    confirmWait.setTransitionList(v);

    // transition from continueDecision to wait

    v = new Vector();

    c = new NamedCondition("continueNegotiation");

    final ConditionExp cExp = new ConditionExp(c);

    a = new NamedAction("constructCounterProposal");

    b = new NamedAction("sendCounterPropose");

    aExp = new ActionExp(a, new Operator("--"), b);

    t = new Transition2(cExp, aExp, wait);

    v.add(t);

    // transitions from continueDecision to failure

    c = new NamedCondition("stopNegotiation");

    a = new NamedAction("sendStopNegotiation");

    t = new Transition2(c, a, failure);

    v.add(t);

    continueDecision.setTransitionList(v);

    // transition from wait to evaluate

    v = new Vector();

    c = new NamedCondition("hasCounterProposeMessage");

    a = new NamedAction("evaluateProposal");

    t = new Transition2(c, a, evaluate);

    v.add(t);

    // transition from wait to failure

    c = new NamedCondition("hasStopNegotiationMessage");

    a = new NamedAction("failure");

    t = new Transition2(c, a, failure);

    v.add(t);

    // transition from wait to success

    c = new NamedCondition("hasAcceptProposalMessage");

    a = new NamedAction("performContract");

    b = new NamedAction("sendInformDone");

    final Operator oper = new Operator("--");

    aExp = new ActionExp(a, oper, b);

    t = new Transition2(new ConditionExp(c), aExp, success);

    v.add(t);

    wait.setTransitionList(v);
    return atn;
}
/**
 * Insert the method's description here.
 * Creation date: (22/05/2003 17:57:30)
 */
public void constructCounterProposal()
{
    final InteractiveAgent agent = this.getAgent();

    this.getEvaluationStrategy().setProposals(agent.getServices());

    this.setProposal((AbstractService) this.getEvaluationStrategy().execute());

    System.out.println(agent.getIdentifier() + " : " + this.getConversationId() + " : decide to counter-propose....");
}
/**
 * Insert the method's description here.
 * Creation date: (22/05/2003 18:02:37)
 */
public boolean continueNegotiation()
{
	return this.decision;
}
/**
 * Insert the method's description here.
 * Creation date: (22/05/2003 17:56:59)
 */
public void decideToContinue()
{
}
/**
 * Insert the method's description here.
 * Creation date: (22/05/2003 17:56:22)
 */
public void evaluateProposal()
{
    final InteractiveAgent agent = this.getAgent();

    System.out.println(agent.getIdentifier() + " (" + this.getConversationId() + ") : evaluate the proposal....");

    final Vector p = new Vector();

    p.add(this.getReceivedProposal());

    this.getEvaluationStrategy().setProposals(p);

    try
	{
        final AbstractService proposal = (AbstractService) this.getEvaluationStrategy().execute(); //executer la strategie de n�gociation
        System.out.println(agent.getIdentifier() + " --> " + this.getOpponent() + " : ACCEPT the proposal : " + proposal);
        this.setDecision(true);
	}
    catch (final NullPointerException e)
	{
        this.setDecision(false);
	}
}
/**
 * Insert the method's description here.
 * Creation date: (22/05/2003 17:59:02)
 */
public void failure()
{
}
/**
 * Insert the method's description here.
 * Creation date: (23/05/2003 18:11:38)
 * @return boolean
 */
public boolean getDecision()
{
	return this.decision;
}
/**
 * Insert the method's description here.
 * Creation date: (23/05/2003 18:11:38)
 * @return dima.kernel.communicatingAgent.domainOfInteraction.EvaluationStrategy
 */
public EvaluationStrategy getEvaluationStrategy()
{
	return this.evaluationStrategy;
}
/**
 * Insert the method's description here.
 * Creation date: (23/05/2003 18:11:38)
 * @return Gdima.basicagentcomponents.AgentIdentifier
 */
public dima.basicagentcomponents.AgentIdentifier getOpponent()
{
	return this.opponent;
}
/**
 * Insert the method's description here.
 * Creation date: (23/05/2003 18:13:00)
 * @return dima.kernel.communicatingAgent.domainOfInteraction.AbstractService
 */
public AbstractService getProposal()
{
	return this.proposal;
}
/**
 * Insert the method's description here.
 * Creation date: (23/05/2003 18:11:38)
 * @return dima.kernel.communicatingAgent.domainOfInteraction.AbstractService
 */
public AbstractService getReceivedProposal()
{
	return this.receivedProposal;
}
/**
 * Insert the method's description here.
 * Creation date: (02/09/2003 14:49:00)
 */
public boolean hasAcceptProposalMessage()
{
	return this.readMessage("AcceptProposal");
}
/**
 * Insert the method's description here.
 * Creation date: (02/09/2003 14:48:06)
 */
public boolean hasCounterProposeMessage()
{
	return this.readMessage("CounterPropose");
}
/**
 * Insert the method's description here.
 * Creation date: (02/09/2003 14:47:38)
 */
public boolean hasInformDoneMessage()
{
	return this.readMessage("InformDone");
}
/**
 * Insert the method's description here.
 * Creation date: (02/09/2003 14:47:00)
 */
public boolean hasProposeMessage()
{
	return this.readMessage("Propose");
}
/**
 * Insert the method's description here.
 * Creation date: (02/09/2003 14:48:37)
 */
public boolean hasStopNegotiationMessage()
{
	return this.readMessage("StopNegotiation");
}
/**
 * Insert the method's description here.
 * Creation date: (02/04/03 00:38:05)
 */
public void performContract()
{
    final InteractiveAgent agent = this.getAgent();

    agent.removeService(this.getProposal());
}
/**
 * Insert the method's description here.
 * Creation date: (22/05/2003 18:01:52)
 */
public boolean refuseProposal()
{
	return !this.decision;
}
/**
 * Insert the method's description here.
 * Creation date: (22/05/2003 17:56:40)
 */
public void sendAcceptProposal()
{
    final InteractiveAgent agent = this.getAgent();

    final ACLAcceptProposal msg = new ACLAcceptProposal(this.getConversationId());

    msg.setContent(this.getReceivedProposal());

    msg.setProtocol("BargainingProtocol");

    agent.sendMessage(this.getOpponent(), msg);

    System.out.println(agent.getIdentifier() + " --> " + this.getOpponent() + " : I accept your PROPOSAL..." + this.getReceivedProposal());
}
/**
 * Insert the method's description here.
 * Creation date: (22/05/2003 17:57:48)
 */
public void sendCounterPropose()
{
    final ACLCounterPropose msg = new ACLCounterPropose(this.getConversationId());

    msg.setProtocol("BargainingProtocol");

    msg.setContent(this.getProposal());

    this.getAgent().sendMessage(this.getOpponent(), msg);

    System.out.println(this.getAgent().getIdentifier() + " --> " + this.getOpponent() + " : I propose a COUNTER PROPOSAL(" + this.getProposal() + ").");
}
/**
 * Insert the method's description here.
 * Creation date: (26/03/2003 14:28:16)
 */
public void sendInformDone()
{
    final ACLInformDone msg = new ACLInformDone(this.getConversationId());

    msg.setProtocol("BargainingProtocol");

    msg.setContent(this.getProposal());

    this.getAgent().sendMessage(this.getOpponent(), msg);

    System.out.println(this.getAgent().getIdentifier() + " --> " + this.getOpponent() + " : Inform Done....");
}
/**
 * Insert the method's description here.
 * Creation date: (22/05/2003 17:58:07)
 */
public void sendStopNegotiation()
{
    final ACLStopNegotiation msg = new ACLStopNegotiation(this.getConversationId());

    msg.setProtocol("BargainingProtocol");

    msg.setContent(this.getReceivedProposal());

    this.getAgent().sendMessage(this.getOpponent(), msg);

    System.out.println(this.getAgent().getIdentifier() + " --> " + this.getOpponent() + " : I want to stop negotiation");

}
/**
 * Insert the method's description here.
 * Creation date: (23/05/2003 18:11:38)
 * @param newDecision boolean
 */
public void setDecision(final boolean newDecision)
{
	this.decision = newDecision;
}
/**
 * Insert the method's description here.
 * Creation date: (23/05/2003 18:11:38)
 * @param newEvaluationStrategy dima.kernel.communicatingAgent.domainOfInteraction.EvaluationStrategy
 */
public void setEvaluationStrategy(final EvaluationStrategy newEvaluationStrategy)
{
	this.evaluationStrategy = newEvaluationStrategy;
}
/**
 * Insert the method's description here.
 * Creation date: (23/05/2003 18:11:38)
 * @param newOpponent Gdima.basicagentcomponents.AgentIdentifier
 */
public void setOpponent(final dima.basicagentcomponents.AgentIdentifier newOpponent)
{
	this.opponent = newOpponent;
}
/**
 * Insert the method's description here.
 * Creation date: (23/05/2003 18:13:00)
 * @param newProposal dima.kernel.communicatingAgent.domainOfInteraction.AbstractService
 */
public void setProposal(final AbstractService newProposal)
{
	this.proposal = newProposal;
}
/**
 * Insert the method's description here.
 * Creation date: (23/05/2003 18:11:38)
 * @param newReceivedProposal dima.kernel.communicatingAgent.domainOfInteraction.AbstractService
 */
public void setReceivedProposal(final AbstractService newReceivedProposal)
{
	this.receivedProposal = newReceivedProposal;
}
/**
 * Insert the method's description here.
 * Creation date: (22/05/2003 18:04:03)
 */
public boolean stopNegotiation()
{
	return !this.decision;
}
}
