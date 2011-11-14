package dima.introspectionbasedagents.services.core.loggingactivity;

import java.io.Serializable;

import dima.basicagentcomponents.AgentIdentifier;
import dima.basiccommunicationcomponents.Message;

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

	Message m;
	MessageStatus status;

	//
	// Constructor
	//

	public LogCommunication(final AgentIdentifier agent,final Message m, final MessageStatus s) {
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
		+ this.date.getTime() + "):\n" + " * " + getCaller()
		+ " :\n * New Message " + this.status + " *\n"
		+ this.m.toString();
		return result;
	}

	@Override
	public String generateLogToWrite() {
		final String result = "*** On " + this.date + " ("
		+ this.date.getTime() + "):\n" + " * " + getCaller()
		+ " :\n * New Message " + this.status + " *\n"
		+ this.m.toString();
		return result;
	}
}
