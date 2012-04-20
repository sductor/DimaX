package dima.basicinterfaces;

import dima.basicagentcomponents.AgentIdentifier;

public interface AbstractMessageInterface {

//	public AgentIdentifier getReceiver();

	public AgentIdentifier getSender();

	public void setReceiver(AgentIdentifier id);

	public void setSender(AgentIdentifier identifier);


}
