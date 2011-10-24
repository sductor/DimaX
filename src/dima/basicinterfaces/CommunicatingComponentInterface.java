package dima.basicinterfaces;

import dima.basicagentcomponents.AgentIdentifier;
import dima.basiccommunicationcomponents.Message;



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
