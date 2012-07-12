package examples.introspectionExamples;

import dima.basicagentcomponents.AgentIdentifier;
import dima.basicagentcomponents.AgentName;
import dima.introspectionbasedagents.annotations.Competence;
import dima.introspectionbasedagents.annotations.MessageHandler;
import dima.introspectionbasedagents.annotations.StepComposant;
import dima.introspectionbasedagents.annotations.Transient;
import dima.introspectionbasedagents.services.CompetenceException;
import dima.introspectionbasedagents.services.loggingactivity.LogService;
import dima.introspectionbasedagents.shells.BasicCompetentAgent;


public class SimpleAgent extends BasicCompetentAgent {

	private static final long serialVersionUID = 2884537638583904242L;

	int myId;
	int nbAgent;
	int nbTour;

	/*
	 *
	 */

	@Competence()
	public final SayingAliveToPeerCompetence sayAliveToPeer =
	new SayingAliveToPeerCompetence();
	@Competence()
	public final SayingAliveCompetence sayAlive =
	new SayingAliveCompetence();

	/*
	 *
	 */

	public SimpleAgent(final int id, final int nbA, final int nbTour)
			throws CompetenceException {
		super(SimpleAgent.getsimpleId(id));
		this.myId = id;
		this.nbAgent = nbA;
		this.nbTour = nbTour;
		if (this.nbAgent < 2) {
			this.logMonologue(
					"JE SUIS TOUT SEUL!! Ca va pas le faire!!!",LogService.onScreen);
		} else {
			this.sayAliveToPeer.setMyPeer(this.getMyAlivePeer());
		}
	}

	/*
	 *
	 */

	public static AgentIdentifier getsimpleId(final int num) {
		return new AgentName("SimpleAgentNumber_" + num);
	}

	public AgentIdentifier getMyMessagePeer() {
		return SimpleAgent
				.getsimpleId((this.myId + 1) % this.nbAgent);
	}

	public AgentIdentifier getMyAlivePeer() {
		if (this.myId!=0) {
			return SimpleAgent
					.getsimpleId((this.myId - 1) % this.nbAgent);
		} else {
			return SimpleAgent
					.getsimpleId(this.nbAgent - 1);
		}
	}
	/*
	 *
	 */


	//In case there is a manager that send a message to all the agents to order them to begin their execution
	//	@MessageHandler()
	//	@FipaACLEnvellopeHandler(
	//			performative = Performative.Inform,
	//			protocol = CreationProtocol.class,
	//			attachementSignature = { Date.class })
	//	@Override
	//public boolean applicationStart(final FipaACLMessage init) {

	@StepComposant()
	@Transient
	protected boolean applicationStart(){
		this.wwait(1000);
		if (this.myId == 0) {
			final SimpleMessage m = new SimpleMessage();
			this.sendMessage(SimpleAgent.getsimpleId(1), m);
			this.logMonologue("First message sended:\n" + m,LogService.onScreen);
			this.logMonologue("notifying "+m,LogService.onScreen);
			this.notify(m);
		} else {
			this.sayAlive.setActive(false);
		}
		return true;
	}

	@MessageHandler
	protected synchronized void parseTheSimpleMessages(final SimpleMessage m) {

		this.logMonologue(
				"=> I've received :"
						+ m +  "\n        next agent : "
						+ SimpleAgent.getsimpleId((this.myId + 1) % this.nbAgent),LogService.onScreen);

		if (this.myId == 0) {
			this.nbTour--;
			if (this.nbTour == 0) {
				this.logMonologue(
						"AGENT 0 : Experiment Over!",LogService.onScreen);
				System.exit(1);// this will exit all the JVMs
				return;
			} else {
				m.incrementeTour();
				this.logMonologue("notifying "+m,LogService.onScreen);
				this.notify(m);
			}
		}

		m.incremente();
		this.sendMessage(this.getMyMessagePeer(), m);
	}

	//	@MessageHandler()
	//	public void receiveAliveMessage(final SayAliveMessage m){
	//		logMonologue("**YOOOO! (2) =D ----------> "+m.getSender()+" IS STILL ALIVE");
	//	}
}
