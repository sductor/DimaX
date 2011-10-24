package dima.introspectionbasedagents.ontologies;

import dima.basicinterfaces.DimaComponentInterface;
import dima.introspectionbasedagents.CommunicatingCompetentComponent;
import dima.introspectionbasedagents.ontologies.FIPAACLOntologie.FipaACLMessage;
import dima.introspectionbasedagents.ontologies.FIPAACLOntologie.Performative;
import dima.introspectionbasedagents.services.BasicAgentCommunicatingCompetence;
import dima.introspectionbasedagents.services.UnrespectedCompetenceSyntaxException;

/**
 * @see negotiation.negotiationframework.interaction.consensualnegotiation.NegotiationProtocol
 *
 * @author Ductor Sylvain
 *
 */
public class Protocol<Agent extends CommunicatingCompetentComponent> extends BasicAgentCommunicatingCompetence<Agent> {
	private static final long serialVersionUID = -6952844743512562269L;

	public interface ProtocolRole extends DimaComponentInterface{
		//public void check validity
		public void setProtocol(Protocol p);
	}

	public @interface role {
		Class<? extends ProtocolRole> value();
	}

	
	public Protocol() {
	}
	
//	public void sendMessage(AgentIdentifier id, AbstractMessageInterface m){
//		this.com.sendMessage(id, m);
//	}

//	public void sendMessage(Collection<AgentIdentifier> ids, AbstractMessageInterface m){
//		for (AgentIdentifier id : ids)
//			this.com.sendMessage(id, m);
//	}


	//
	// Generic error routine
	//

	public Protocol(Agent a) throws UnrespectedCompetenceSyntaxException {
		super(a);
	}

	public void answerNotUnderstood(final FipaACLMessage m, final String text) {
		m.setPerformative(Performative.NotUnderstood);
		m.setAdditionalInformation(text);
		this.sendMessage(m.getReplyTo(),m);
	}

	public void answerFailure(final FipaACLMessage m, final String text) {
		m.setPerformative(Performative.Failure);
		m.setAdditionalInformation(text);
		this.sendMessage(m.getReplyTo(),m);
	}

	public void answerFailure(final FipaACLMessage m, final Exception e) {
		m.setPerformative(Performative.Failure);
		m.setAttachedException(e);
		this.sendMessage(m.getReplyTo(),m);
	}

	public void answerFailure(final FipaACLMessage m, final String text,
			final Exception e) {
		m.setPerformative(Performative.Failure);
		m.setAdditionalInformation(text);
		m.setAttachedException(e);
		this.sendMessage(m.getReplyTo(),m);
	}


	//	//
	//	// Protocol graph
	//	//
	//
	//	public @interface root {
	//		Class<?>[] caller();
	//	};
	//
	//	public @interface child {
	//		Class<?>[] callers();
	//		String[] parents();
	//	};

	//	/**
	//	 * This methods allow to ensure that a specific message is associated to a
	//	 * specific association It needs
	//	 *
	//	 * @param m
	//	 * @return
	//	 */
	//	public static boolean checkMessageValidity(final FipaACLMessage m) {
	//
	//		return true;
	//	}
	// static Map<String, Annotation> replyTree = new HashMap<String,
	// Annotation>();
	// public abstract Annotation getEnvellope(String methodName) throws
	// UnknownName{}
}
