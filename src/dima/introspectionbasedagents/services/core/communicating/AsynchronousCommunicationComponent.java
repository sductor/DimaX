package dima.introspectionbasedagents.services.core.communicating;

import java.util.Collection;

import dima.basicagentcomponents.AgentIdentifier;
import dima.basiccommunicationcomponents.Message;
import dima.basicinterfaces.DimaComponentInterface;

public interface AsynchronousCommunicationComponent extends DimaComponentInterface {

	/*
	 * 
	 */
	
	boolean isConnected(String[] args);

	boolean connect(String[] args);

	boolean disconnect(String[] args);

	/*
	 * Message sending primitives : should retunr boolean
	 */
	public void sendMessage(final AgentIdentifier ad, final AbstractMessageInterface am);

	/*
	 * Message sending primitives
	 */
	public void receive(final AbstractMessageInterface am);
}
