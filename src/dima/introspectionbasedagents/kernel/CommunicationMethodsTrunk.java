package dima.introspectionbasedagents.kernel;

import java.util.Collection;

import dima.basiccommunicationcomponents.AbstractMessage;
import dima.introspectionbasedagents.kernel.BasicCommunicatingMethodTrunk.UnHandledMessageException;
import dima.introspectionbasedagents.ontologies.Envelope;

public interface CommunicationMethodsTrunk extends IntrospectedMethodsTrunk {

	//Return the methods to be removed
	public abstract Collection<MethodHandler> parseMail(AbstractMessage m)
			throws UnHandledMessageException,IllegalArgumentException, Throwable;

	Collection<Envelope> getHandledEnvellope();

}