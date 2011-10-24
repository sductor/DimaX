package dima.basicinterfaces;

import dima.basicagentcomponents.AgentIdentifier;

public interface CommunicationComponentInterface {

	public void sendMessage(AgentIdentifier id, AbstractMessageInterface m);

}
