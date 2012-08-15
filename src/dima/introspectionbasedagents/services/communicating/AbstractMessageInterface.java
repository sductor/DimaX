package dima.introspectionbasedagents.services.communicating;

import dima.basicagentcomponents.AgentIdentifier;
import dima.introspectionbasedagents.services.loggingactivity.AbstractDebugableMessageInterface;

public interface AbstractMessageInterface extends AbstractDebugableMessageInterface{

	//	public AgentIdentifier getReceiver();

	public AgentIdentifier getSender();

	public void setReceiver(AgentIdentifier id);

	public void setSender(AgentIdentifier identifier);

	public Object getContent();

	public AbstractMessageInterface clone();



}
