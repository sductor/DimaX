package dima.introspectionbasedagents.services.loggingactivity;

import dima.basicagentcomponents.AgentIdentifier;
import dimaxx.hostcontrol.LocalHost;

public class LogMonologue extends LogNotification {

	/**
	 *
	 */
	private static final long serialVersionUID = -5475469040879130633L;
	final String text;
	final String host = LocalHost.getUrl();

	public LogMonologue(final AgentIdentifier id, final String text) {
		super(id);
		this.text = text;
	}

	@Override
	public String generateLogToScreen() {
		return "**** NEW MONOLOGUE FROM AGENT " + this.getCaller() + " :" + "\n"
				+ "** On Host" + this.host + "(" + this.date.toString() + " - "
				+ this.date.getTime() + "):\n" + this.text;
	}

	@Override
	public String generateLogToWrite() {
		return "** On Host" + this.host + "(" + this.date.toString() + " - "
				+ this.date.getTime() + "):\n" + this.text;
	}
}
