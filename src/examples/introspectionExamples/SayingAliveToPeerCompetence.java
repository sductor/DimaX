package examples.introspectionExamples;

import dima.basicagentcomponents.AgentIdentifier;
import dima.introspectionbasedagents.annotations.MessageHandler;
import dima.introspectionbasedagents.annotations.StepComposant;
import dima.introspectionbasedagents.ontologies.Protocol;
import dima.introspectionbasedagents.ontologies.FIPAACLOntologie.FipaACLEnvelopeClass.FipaACLEnvelope;
import dima.introspectionbasedagents.ontologies.FIPAACLOntologie.FipaACLMessage;
import dima.introspectionbasedagents.ontologies.FIPAACLOntologie.Performative;
import dima.introspectionbasedagents.services.BasicAgentCommunicatingCompetence;
import dima.introspectionbasedagents.services.loggingactivity.LogService;
import dima.introspectionbasedagents.shells.BasicCompetentAgent;

public class SayingAliveToPeerCompetence extends BasicAgentCommunicatingCompetence<BasicCompetentAgent> {

	private static final long serialVersionUID = -9105012202356519768L;

	private AgentIdentifier myPeer=null;

	public class SayingAliveProtocol extends Protocol<BasicCompetentAgent>{

		/**
		 *
		 */
		private static final long serialVersionUID = 6620352657422762931L;

		//		public SayingAliveProtocol(BasicCompetentAgent a) {
		//			super(a);
		//		}

	}

	//	public SayingAliveToPeerCompetence(final BasicCompetentAgent ag) {
	//		super(ag);
	//	}

	/*
	 *
	 */

	public void setMyPeer(final AgentIdentifier myPeer) {
		this.myPeer = myPeer;
	}

	/*
	 *
	 */

	@StepComposant(ticker=1000)
	public void sayAlive() {
		this.getMyAgent().logMonologue("I'M STILL ALIVE",LogService.onScreen);
		this.sayAliveToPeer();
	}

	@MessageHandler()
	@FipaACLEnvelope(performative=Performative.Inform, protocol=SayingAliveProtocol.class )
	public void receiveAliveMessage(final FipaACLMessage m){
		this.logMonologue("**YOOOO! (2) =D ----------> "+m.getSender()+" IS STILL ALIVE",LogService.onScreen);
	}

	/*
	 *
	 */

	public void sayAliveToPeer(){
		final FipaACLMessage m = new FipaACLMessage(Performative.Inform, "I'm alive! =)", SayingAliveProtocol.class);
		if (this.myPeer!=null)
			this.sendMessage(this.myPeer, m);
	}
}
