package dima.introspectionbasedagents.services.core.communicating;

import dima.basicagentcomponents.AgentIdentifier;
import dima.basiccommunicationcomponents.Message;
import dima.basicinterfaces.ActiveComponentInterface;
import dima.basicinterfaces.IdentifiedComponentInterface;



public interface CommunicatingComponentInterface extends IdentifiedComponentInterface, ActiveComponentInterface {

	/*
	 * Message sending primitives
	 */
	public void sendMessage(final AgentIdentifier ad, final Message am);

	/*
	 * Message sending primitives
	 */
	public void receive(final Message am);
}
