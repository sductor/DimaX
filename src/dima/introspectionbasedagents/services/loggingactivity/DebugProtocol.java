package dima.introspectionbasedagents.services.loggingactivity;

import dima.basicagentcomponents.AgentIdentifier;
import dima.introspectionbasedagents.annotations.MessageHandler;
import dima.introspectionbasedagents.kernel.BasicCompetentAgent;
import dima.introspectionbasedagents.kernel.CommunicatingCompetentComponent;
import dima.introspectionbasedagents.ontologies.Protocol;
import dima.introspectionbasedagents.ontologies.FIPAACLOntologie.FipaACLEnvelopeClass.FipaACLEnvelope;
import dima.introspectionbasedagents.ontologies.FIPAACLOntologie.FipaACLMessage;
import dima.introspectionbasedagents.ontologies.FIPAACLOntologie.Performative;
import dima.introspectionbasedagents.services.communicating.AbstractMessageInterface;

public class DebugProtocol extends Protocol<BasicCompetentAgent> {

	@MessageHandler
	@FipaACLEnvelope(performative = Performative.Failure, protocol = DebugProtocol.class)
	public void receiveFailure(FipaACLMessage m){
		AbstractMessageInterface ami = handleFailure(m);
		propagateFailure(ami);
	}

	public AbstractMessageInterface handleFailure(FipaACLMessage m) {
		AbstractMessageInterface ami = m.getInreplyto();
		signalException("RECEIVE FAILURE !!! send of message has raised exception : "+ami+"\n"+ami.getProtocolTrace(), ami.getLocalStackTrace());
		return ami;
	}

	public void propagateFailure(AbstractMessageInterface ami) {
		if (ami.getDebugInReplyTo()!=null){
			AbstractMessageInterface jeSuisTonPere = ami.getDebugInReplyTo();
			answerFailure(getMyAgent(), jeSuisTonPere,ami.getLocalStackTrace());
		}
	}

	/*
	 * 
	 */

	@MessageHandler
	@FipaACLEnvelope(performative = Performative.NotUnderstood, protocol = DebugProtocol.class)
	public void receiveNotUnderstood(FipaACLMessage m){
		AbstractMessageInterface ami = m.getInreplyto();
		signalException("send of message has raised exception : "+ami, ami.getLocalStackTrace());
	}
	
	/*
	 * 
	 */

	public static void answerNotUnderstood(final CommunicatingCompetentComponent ccc, final AbstractMessageInterface ami, final String text) {
		FipaACLMessage m =  new FipaACLMessage(Performative.NotUnderstood, DebugProtocol.class);
		m.setAdditionalInformation(text);
		m.setInreplyto(ami);
		AgentIdentifier repylTo;
		if (ami instanceof FipaACLMessage)
			repylTo = ((FipaACLMessage)ami).getReplyTo();
		else
			repylTo= ami.getSender();
		ccc.sendMessage(repylTo,m);
	}

	public static void  answerFailure(final CommunicatingCompetentComponent ccc, final AbstractMessageInterface ami, final String text) {
		FipaACLMessage m =  new FipaACLMessage(Performative.Failure, DebugProtocol.class);
		m.setAdditionalInformation(text);
		m.setInreplyto(ami);
		AgentIdentifier repylTo;
		if (ami instanceof FipaACLMessage)
			repylTo = ((FipaACLMessage)ami).getReplyTo();
		else
			repylTo= ami.getSender();
		ccc.sendMessage(repylTo,m);
	}

	public static void answerFailure(final CommunicatingCompetentComponent ccc, final AbstractMessageInterface ami, final Throwable e) {
		FipaACLMessage m =  new FipaACLMessage(Performative.Failure, DebugProtocol.class);
		m.setAttachedException(e);
		m.setInreplyto(ami);
		AgentIdentifier repylTo;
		if (ami instanceof FipaACLMessage)
			repylTo = ((FipaACLMessage)ami).getReplyTo();
		else
			repylTo= ami.getSender();
		ccc.sendMessage(repylTo,m);
	}

	public static void answerFailure(
			final CommunicatingCompetentComponent ccc, final AbstractMessageInterface ami,  
			final String text,	final Throwable e) {
		FipaACLMessage m =  new FipaACLMessage(Performative.Failure, DebugProtocol.class);
		m.setAdditionalInformation(text);
		m.setAttachedException(e);
		m.setInreplyto(ami);
		AgentIdentifier repylTo;
		if (ami instanceof FipaACLMessage)
			repylTo = ((FipaACLMessage)ami).getReplyTo();
		else
			repylTo= ami.getSender();
		ccc.sendMessage(repylTo,m);
	}
}