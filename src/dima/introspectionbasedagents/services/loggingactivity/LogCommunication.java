package dima.introspectionbasedagents.services.loggingactivity;

import dima.basicagentcomponents.AgentIdentifier;
import dima.introspectionbasedagents.services.communicating.AbstractMessageInterface;

public class LogCommunication extends LogNotification {

	//
	// Subclass
	//

	/**
	 *
	 */
	private static final long serialVersionUID = -5161352058234382277L;

	public enum MessageStatus {
		MessageSended, MessageReceived, MessageLocallySended, MessageLocallyReceived
	}

	//
	// Field
	//

	AbstractMessageInterface m;
	MessageStatus status;

	//
	// Constructor
	//

	public LogCommunication(final AgentIdentifier agent,final AbstractMessageInterface m, final MessageStatus s) {
		super(agent);
		this.m = m;
		this.status = s;
	}

	//
	// Primitives
	//

	@Override
	public String generateLogToScreen() {
		final String result = "*** On " + this.date + " ("
				+ this.date.getTime() + "):\n" + " * From Agent " + this.getCaller()
				+ " :\n * New Message " + this.status + " *\n"
				+ this.m.toString();
		return result;
	}

	@Override
	public String generateLogToWrite() {
		final String result = "*** On " + this.date + " ("
				+ this.date.getTime() + "):\n" + " * " + this.getCaller()
				+ " :\n * New Message " + this.status + " *\n"
				+ this.m.toString();
		return result;
	}
}
