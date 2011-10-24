package dima.introspectionbasedagents.shells;

import java.lang.reflect.InvocationTargetException;
import java.util.Collection;

import dima.basiccommunicationcomponents.AbstractMessage;
import dima.introspectionbasedagents.ontologies.Envelope;
import dima.introspectionbasedagents.shells.BasicCommunicatingMethodTrunk.UnHandledMessageException;

public interface CommunicationMethodsTrunk extends IntrospectedMethodsTrunk {

	//Return the methods to be removed
	public abstract Collection<MethodHandler> parseMail(AbstractMessage m) 
	throws UnHandledMessageException,IllegalArgumentException, InvocationTargetException;

	Collection<Envelope> getHandledEnvellope();

}