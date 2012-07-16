package dima.introspectionbasedagents.services.core.observingagent;


import dima.basicagentcomponents.AgentIdentifier;
import dima.basiccommunicationcomponents.Message;

public class ShowYourPocket extends Message{
	private static final long serialVersionUID = 7497815504780690043L;

	/**
	 *
	 */

	final AgentIdentifier asker;
	final String callingMethod;

	public ShowYourPocket(final AgentIdentifier asker, final String callingMethod) {
		super();
		this.asker = asker;
		this.callingMethod = callingMethod;
	}
	public AgentIdentifier getAsker() {
		return this.asker;
	}
	public String getCallingMethod() {
		return this.callingMethod;
	}


}
