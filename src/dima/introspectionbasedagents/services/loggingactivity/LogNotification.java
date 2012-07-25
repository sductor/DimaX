package dima.introspectionbasedagents.services.loggingactivity;

import java.util.Date;

import dima.basicagentcomponents.AgentIdentifier;
import dima.basiccommunicationcomponents.Message;

/**
 * This is the basic class for all log notification
 *
 * @author Ductor Sylvain
 *
 */
public abstract class LogNotification extends Message{
	private static final long serialVersionUID = 2651499802673480005L;


	protected Date date = new Date();
	protected String caller;

	protected LogNotification(final AgentIdentifier caller) {
		super();
		this.caller = caller.toString();
	}

	public Date getCreationDate() {
		return this.date;
	}

	public abstract String generateLogToScreen();

	public abstract String generateLogToWrite();

	public String getCaller() {
		return this.caller;
	}
}
