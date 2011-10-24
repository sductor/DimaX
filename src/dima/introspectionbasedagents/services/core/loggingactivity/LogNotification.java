package dima.introspectionbasedagents.services.core.loggingactivity;

import java.io.Serializable;
import java.util.Date;

import dima.basicagentcomponents.AgentIdentifier;

/**
 * This is the basic class for all log notification
 *
 * @author Ductor Sylvain
 *
 */
public abstract class LogNotification implements Serializable{
	private static final long serialVersionUID = 2651499802673480005L;

	
	protected Date date = new Date();
	protected String caller;	
	
	protected LogNotification(AgentIdentifier caller) {
		super();
		this.caller = caller.toString();
	}

	public Date getCreationDate() {
		return this.date;
	}

	public abstract String generateLogToScreen(boolean printDetails);

	public abstract String generateLogToWrite(boolean printDetails);

	public String getCaller() {
		return caller;
	}
}
