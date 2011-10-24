package dima.kernel.INAF.InteractionProtocols;

/**
 * Insert the type's description here.
 * Creation date: (21/05/2003 16:22:01)
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
import dima.ontologies.basicFIPAACLMessages.ACLPropose;
import dima.ontologies.basicFIPAACLMessages.ACLStopNegotiation;
import dima.tools.agentInterface.NamedAction;
import dima.tools.agentInterface.NamedCondition;
import dima.tools.automata.ATN;
import dima.tools.automata.State;




public class BargainingInitiator extends AbstractRole
{
	/**
	 *
	 */
	private static final long serialVersionUID = -618592594163447355L;
	public AbstractService proposal;
	public AgentIdentifier opponent;
	public EvaluationStrategy evaluationStrategy;
	public AbstractService receivedProposal;
	public boolean decision = true;
/**
 * BargainingInitiator constructor comment.
 */
public BargainingInitiator()
{
    super(buildATN());

    this.setRoleName("BargainingInitiator");
}

public BargainingInitiator(final InteractiveAgent a, final String conv_id, final AgentIdentifier
		identifier, final AbstractService service, final EvaluationStrategy strategy)
		{
			this.setAgent(a);
			this.setConversationId(conv_id);
			this.getAgent().setId(identifier);
			this.setContract(service);
			this.setEvaluationStrategy(strategy);

		}
/**
 * Insert the method's description here.
 * Creation date: (22/05/2003 18:00:21)
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
    //BargainingInitiator role = (BargainingInitiator) this.getContext();
    final InteractiveAgent agent = this.getAgent();
}
/**
 * Insert the method's description here.
 * Creation date: (07/04/2003 10:56:50)
 */
public void addProposal()
{
    //BargainingInitiator role = (BargainingInitiator) this.getContext();
    final AbstractService proposal = (AbstractService) this.getCurrentMessage().getContent();
    this.setReceivedProposal(proposal);
    System.out.println(this.getAgent().getIdentifier() + " : " + this.getConversationId() + " : L'ajout de la proposition : " + proposal);
}
/**
 * Insert the method's description here.
 * Creation date: (09/09/2003 13:14:18)
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

    // transition from start to wait

    Vector v = new Vector();

    NamedCondition c = new NamedCondition("isInitialized");

    NamedAction a = new NamedAction("constructProposal");

    NamedAction b = new NamedAction("sendPropose");

    Operator oper = new Operator("--");

    ActionExp aExp = new ActionExp(a, oper, b);

    Transition2 t = new Transition2(new ConditionExp(c), aExp, wait);

    v.add(t);
    start.setTransitionList(v);

    // transition from wait to evaluate

    v = new Vector();

    c = new NamedCondition("hasProposeMessage");

    a = new NamedAction("addProposal");

    b = new NamedAction("evaluateProposal");

    aExp = new ActionExp(a, new Operator("--"), b);

    t = new Transition2(new ConditionExp(c), aExp, evaluate);

    v.add(t);

    // transition from wait to fail

    c = new NamedCondition("hasStopNegotiationMessage");

    a = new NamedAction("failure");

    t = new Transition2(c, a, failure);

    v.add(t);

    // transition from wait to success

    c = new NamedCondition("hasAcceptProposalMessage");

    a = new NamedAction("performContract");

    b = new NamedAction("sendInformDone");

    oper = new Operator("AND");

    aExp = new ActionExp(a, oper, b);

    t = new Transition2(new ConditionExp(c), aExp, success);

    v.add(t);

    wait.setTransitionList(v);

    // transitions from evaluate to confirmWait

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

    // transition from continueDecision to failure

    v = new Vector();

    c = new NamedCondition("stopNegotiation");

    a = new NamedAction("sendStopNegotiation");

    t = new Transition2(c, a, success);

    v.add(t);

    // transition from continueDecision to wait

    c = new NamedCondition("continueNegotiation");

    final ConditionExp cExp = new ConditionExp(c);

    a = new NamedAction("constructCounterProposal");

    b = new NamedAction("sendCounterPropose");

    aExp = new ActionExp(a, new Operator("AND"), b);

    t = new Transition2(cExp, aExp, wait);

    v.add(t);

    continueDecision.setTransitionList(v);

	return atn;
}
/**
 * Insert the method's description here.
 * Creation date: (22/05/2003 17:55:33)
 */
public void constructCounterProposal()
{
    //BargainingInitiator role = (BargainingInitiator) this.getContext();
    final InteractiveAgent agent = this.getAgent();
    this.getEvaluationStrategy().setProposals(agent.getServices());
    this.setProposal((AbstractService) this.getEvaluationStrategy().execute());
    System.out.println(agent.getIdentifier() + " : " + this.getConversationId() + " : decide to propose....");
}
/**
 * Insert the method's description here.
 * Creation date: (22/05/2003 17:55:33)
 */
public void constructProposal()
{
    //BargainingInitiator role = (BargainingInitiator) this.getContext();
    final InteractiveAgent agent = this.getAgent();
    this.getEvaluationStrategy().setProposals(agent.getServices());
    this.setProposal((AbstractService) this.getEvaluationStrategy().execute());
    System.out.println(agent.getIdentifier() + " : " + this.getConversationId() + " : decide to propose...." + this.getProposal());
}
/**
 * Insert the method's description here.
 * Creation date: (22/05/2003 18:01:16)
 */
public boolean continueNegotiation()
{
	return this.decision;
}
/**
 * Insert the method's description here.
 * Creation date: (22/05/2003 17:54:27)
 */
public void decideToContinue()
{
    //BargainingInitiator role = (BargainingInitiator) this.getContext();
   this.setDecision(true);
}
/**
 * Insert the method's description here.
 * Creation date: (22/05/2003 17:53:04)
 */
public void evaluateProposal()
{
    //BargainingInitiator role = (BargainingInitiator) this.getContext();
    final InteractiveAgent agent = this.getAgent();

    System.out.println(agent.getIdentifier() + " (" + this.getConversationId() + ") : evaluate the proposal....");

    final Vector p = new Vector();

    p.add(this.getReceivedProposal());

    this.getEvaluationStrategy().setProposals(p);

    try
    {
        final AbstractService proposal = (AbstractService) this.getEvaluationStrategy().execute(); //executer la strategie de n�gociation
        System.out.println(agent.getIdentifier() + " --> " + this.getOpponent() + " : ACCEPT proposal....");
        this.setDecision(true);
    }
    catch (final NullPointerException e)
    {
        this.setDecision(false);
    }

}
/**
 * Insert the method's description here.
 * Creation date: (22/05/2003 17:53:23)
 */

public void failure()
{

}
/**
 * Insert the method's description here.
 * Creation date: (24/05/2003 11:47:31)
 * @return boolean
 */
public boolean getDecision()
{
	return this.decision;
}
/**
 * Insert the method's description here.
 * Creation date: (23/05/2003 11:37:01)
 * @return dima.kernel.communicatingAgent.domainOfInteraction.EvaluationStrategy
 */
public EvaluationStrategy getEvaluationStrategy()
{
	return this.evaluationStrategy;
}
/**
 * Insert the method's description here.
 * Creation date: (23/05/2003 10:39:15)
 * @return Gdima.basicagentcomponents.AgentIdentifier
 */
public dima.basicagentcomponents.AgentIdentifier getOpponent()
{
	return this.opponent;
}
/**
 * Insert the method's description here.
 * Creation date: (23/05/2003 10:37:37)
 * @return dima.kernel.communicatingAgent.domainOfInteraction.AbstractService
 */
public AbstractService getProposal()
{
	return this.proposal;
}
/**
 * Insert the method's description here.
 * Creation date: (23/05/2003 12:38:26)
 * @return dima.kernel.communicatingAgent.domainOfInteraction.AbstractService
 */
public AbstractService getReceivedProposal()
{
	return this.receivedProposal;
}
/**
 * Insert the method's description here.
 * Creation date: (02/09/2003 14:59:42)
 */
public boolean hasAcceptProposalMessage()
{
	return this.readMessage("AcceptProposal");
}
/**
 * Insert the method's description here.
 * Creation date: (02/09/2003 15:00:08)
 */
public boolean hasInformDoneMessage()
{
	return this.readMessage("InformDone");
}
/**
 * Insert the method's description here.
 * Creation date: (02/09/2003 14:58:48)
 */
public boolean hasProposeMessage()
{
	return this.readMessage("Propose");
}
/**
 * Insert the method's description here.
 * Creation date: (02/09/2003 14:59:10)
 */
public boolean hasStopNegotiationMessage()
{
	return this.readMessage("StopNegotiation");
}
/**
 * Insert the method's description here.
 * Creation date: (22/05/2003 18:00:06)
 */
public boolean isInitialized()
{
	return this.opponent != null;
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
 * Creation date: (22/05/2003 18:00:40)
 */
public boolean refuseProposal()
{
	return !this.decision;
}
/**
 * Insert the method's description here.
 * Creation date: (22/05/2003 17:54:03)
 */
public void sendAcceptProposal()
{
	//BargainingInitiator role = (BargainingInitiator)this.getContext();

	final InteractiveAgent agent = this.getAgent();

	final ACLAcceptProposal msg = new ACLAcceptProposal(this.getConversationId());

	msg.setContent(this.getReceivedProposal());

	msg.setProtocol("BargainingProtocol");

	agent.sendMessage(this.getOpponent(),msg);

	System.out.println(agent.getIdentifier()+" --> "+this.getOpponent()+" : I accept your Proposal("+this.getReceivedProposal()+")...");
}
/**
 * Insert the method's description here.
 * Creation date: (22/05/2003 17:55:59)
 */
public void sendCounterPropose()
{
	//BargainingInitiator role = (BargainingInitiator)this.getContext();

	final ACLCounterPropose msg = new ACLCounterPropose(this.getConversationId());

	msg.setProtocol("BargainingProtocol");

	msg.setContent(this.getProposal());

	this.getAgent().sendMessage(this.getOpponent(),msg);

	System.out.println(this.getAgent().getIdentifier()+" --> "+this.getOpponent()+" : I propose ("+this.getProposal()+").");
}
/**
 * Insert the method's description here.
 * Creation date: (26/03/2003 14:28:16)
 */
public void sendInformDone()
{
	//BargainingInitiator role = (BargainingInitiator)this.getContext();

	final ACLInformDone msg = new ACLInformDone(this.getConversationId());

	msg.setProtocol("BargainingProtocol");

	msg.setContent(this.getProposal());

	this.getAgent().sendMessage(this.getOpponent(),msg);

	System.out.println(this.getAgent().getIdentifier()+" --> "+this.getOpponent()+" : Inform Done....");
}
/**
 * Insert the method's description here.
 * Creation date: (22/05/2003 17:52:45)
 */
public void sendPropose()
{
	final ACLPropose msg = new ACLPropose(this.getConversationId());

	msg.setProtocol("BargainingProtocol");

	msg.setContent(this.getProposal());

	this.getAgent().sendMessage(this.getOpponent(),msg);

	System.out.println(this.getAgent().getIdentifier()+" --> "+this.getOpponent()+" : I propose you ("+this.getProposal()+").");
}
/**
 * Insert the method's description here.
 * Creation date: (22/05/2003 17:55:03)
 */
public void sendStopNegotiation()
{
	final ACLStopNegotiation msg = new ACLStopNegotiation(this.getConversationId());

	msg.setProtocol("BargainingProtocol");
	msg.setContent(this.getReceivedProposal());

	this.getAgent().sendMessage(this.getOpponent(),msg);

	System.out.println(this.getAgent().getIdentifier()+" --> "+this.getOpponent()+" : I want to stop negotiation");
}
/**
 * Insert the method's description here.
 * Creation date: (24/05/2003 11:47:31)
 * @param newDecision boolean
 */
public void setDecision(final boolean newDecision)
{
	this.decision = newDecision;
}
/**
 * Insert the method's description here.
 * Creation date: (23/05/2003 11:37:01)
 * @param newEvaluationStrategy dima.kernel.communicatingAgent.domainOfInteraction.EvaluationStrategy
 */
public void setEvaluationStrategy(final EvaluationStrategy newEvaluationStrategy)
{
	this.evaluationStrategy = newEvaluationStrategy;
}
/**
 * Insert the method's description here.
 * Creation date: (23/05/2003 10:39:15)
 * @param newOpponent Gdima.basicagentcomponents.AgentIdentifier
 */
public void setOpponent(final dima.basicagentcomponents.AgentIdentifier newOpponent)
{
	this.opponent = newOpponent;
}
/**
 * Insert the method's description here.
 * Creation date: (23/05/2003 10:37:37)
 * @param newProposal dima.kernel.communicatingAgent.domainOfInteraction.AbstractService
 */
public void setProposal(final AbstractService newProposal)
{
	this.proposal = newProposal;
}
/**
 * Insert the method's description here.
 * Creation date: (23/05/2003 12:38:26)
 * @param newReceivedProposal dima.kernel.communicatingAgent.domainOfInteraction.AbstractService
 */
public void setReceivedProposal(final AbstractService newReceivedProposal)
{
	this.receivedProposal = newReceivedProposal;
}
/**
 * Insert the method's description here.
 * Creation date: (22/05/2003 18:00:57)
 */
public boolean stopNegotiation()
{
	return !this.decision;
}
}
