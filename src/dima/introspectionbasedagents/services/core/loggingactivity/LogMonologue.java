package dima.introspectionbasedagents.services.core.loggingactivity;

import dima.basicagentcomponents.AgentIdentifier;
import dimaxx.hostcontrol.LocalHost;

public class LogMonologue extends LogNotification {

	/**
	 *
	 */
	private static final long serialVersionUID = -5475469040879130633L;
	String text, details = null;
	final String host = LocalHost.getUrl();

	public LogMonologue(final AgentIdentifier id, final String text) {
		super(id);
		this.text = text;
	}

	public LogMonologue(final AgentIdentifier id, final String text, final String details) {
		super(id);
		this.text = text;
		this.details = details;
	}

	@Override
	public String generateLogToScreen(final boolean printDetails) {
		return "**** NEW MONOLOGUE FROM AGENT " + getCaller() + " :" + "\n"
		+ "** On Host" + this.host + "(" + this.date.toString() + " - "
		+ this.date.getTime() + "):\n" + this.text
		+ (printDetails ? this.details : "");
	}

	@Override
	public String generateLogToWrite(final boolean printDetails) {
		return "** On Host" + this.host + "(" + this.date.toString() + " - "
		+ this.date.getTime() + "):\n" + this.text
		+ (printDetails ? this.details : "");
	}
}
