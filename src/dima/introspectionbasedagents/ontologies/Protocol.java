package dima.introspectionbasedagents.ontologies;

import dima.basicagentcomponents.AgentIdentifier;
import dima.basicinterfaces.DimaComponentInterface;
import dima.introspectionbasedagents.kernel.CommunicatingCompetentComponent;
import dima.introspectionbasedagents.ontologies.FIPAACLOntologie.FipaACLMessage;
import dima.introspectionbasedagents.ontologies.FIPAACLOntologie.Performative;
import dima.introspectionbasedagents.services.BasicCommunicatingCompetence;
import dima.introspectionbasedagents.services.UnrespectedCompetenceSyntaxException;
import dima.introspectionbasedagents.services.communicating.AbstractMessageInterface;

/**
 * @see frameworks.negotiation.negotiationframework.protocoles.AbstractCommunicationProtocol
 *
 * @author Ductor Sylvain
 *
 */
public class Protocol<Agent extends CommunicatingCompetentComponent> extends BasicCommunicatingCompetence<Agent> {
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

	public Protocol(final Agent a) throws UnrespectedCompetenceSyntaxException {
		super(a);
	}


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
