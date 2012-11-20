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

	/**
	 * 
	 */
	private static final long serialVersionUID = 8607152727363967066L;

	@MessageHandler
	@FipaACLEnvelope(performative = Performative.Failure, protocol = DebugProtocol.class)
	public void receiveFailure(final FipaACLMessage m){
		final AbstractMessageInterface ami = this.handleFailure(m);
		this.propagateFailure(ami);
	}

	public AbstractMessageInterface handleFailure(final FipaACLMessage m) {
		final AbstractMessageInterface ami = m.getInreplyto();
		this.signalException("RECEIVE FAILURE !!! send of message has raised exception : "+ami+"\n"+ami.getProtocolTrace(), ami.getLocalStackTrace());
		return ami;
	}

	public void propagateFailure(final AbstractMessageInterface ami) {
		if (ami.getDebugInReplyTo()!=null){
			final AbstractMessageInterface jeSuisTonPere = ami.getDebugInReplyTo();
			DebugProtocol.answerFailure(this.getMyAgent(), jeSuisTonPere,ami.getLocalStackTrace());
		}
	}

	/*
	 * 
	 */

	@MessageHandler
	@FipaACLEnvelope(performative = Performative.NotUnderstood, protocol = DebugProtocol.class)
	public void receiveNotUnderstood(final FipaACLMessage m){
		final AbstractMessageInterface ami = m.getInreplyto();
		this.signalException("send of message has raised exception : "+ami, ami.getLocalStackTrace());
	}

	/*
	 * 
	 */

	public static void answerNotUnderstood(final CommunicatingCompetentComponent ccc, final AbstractMessageInterface ami, final String text) {
		final FipaACLMessage m =  new FipaACLMessage(Performative.NotUnderstood, DebugProtocol.class);
		m.setAdditionalInformation(text);
		m.setInreplyto(ami);
		AgentIdentifier repylTo;
		if (ami instanceof FipaACLMessage) {
			repylTo = ((FipaACLMessage)ami).getReplyTo();
		} else {
			repylTo= ami.getSender();
		}
		ccc.sendMessage(repylTo,m);
	}

	public static void  answerFailure(final CommunicatingCompetentComponent ccc, final AbstractMessageInterface ami, final String text) {
		final FipaACLMessage m =  new FipaACLMessage(Performative.Failure, DebugProtocol.class);
		m.setAdditionalInformation(text);
		m.setInreplyto(ami);
		AgentIdentifier repylTo;
		if (ami instanceof FipaACLMessage) {
			repylTo = ((FipaACLMessage)ami).getReplyTo();
		} else {
			repylTo= ami.getSender();
		}
		ccc.sendMessage(repylTo,m);
	}

	public static void answerFailure(final CommunicatingCompetentComponent ccc, final AbstractMessageInterface ami, final Throwable e) {
		final FipaACLMessage m =  new FipaACLMessage(Performative.Failure, DebugProtocol.class);
		m.setAttachedException(e);
		m.setInreplyto(ami);
		AgentIdentifier repylTo;
		if (ami instanceof FipaACLMessage) {
			repylTo = ((FipaACLMessage)ami).getReplyTo();
		} else {
			repylTo= ami.getSender();
		}
		ccc.sendMessage(repylTo,m);
	}

	public static void answerFailure(
			final CommunicatingCompetentComponent ccc, final AbstractMessageInterface ami,
			final String text,	final Throwable e) {
		final FipaACLMessage m =  new FipaACLMessage(Performative.Failure, DebugProtocol.class);
		m.setAdditionalInformation(text);
		m.setAttachedException(e);
		m.setInreplyto(ami);
		AgentIdentifier repylTo;
		if (ami instanceof FipaACLMessage) {
			repylTo = ((FipaACLMessage)ami).getReplyTo();
		} else {
			repylTo= ami.getSender();
		}
		ccc.sendMessage(repylTo,m);
	}
}