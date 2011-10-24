package dima.introspectionbasedagents.services.core.loggingactivity;

import dima.basicagentcomponents.AgentIdentifier;
import dimaxx.hostcontrol.LocalHost;

public class LogWarning extends LogException {
	private static final long serialVersionUID = -2335142989408051767L;


	public LogWarning(final AgentIdentifier id,final String text) {
		super(id,text);
	}

	public LogWarning(final AgentIdentifier id,final String text, final Throwable e) {
		super(id,text,e);
	}

	//
	// Methods
	//

	@Override
	public String generateLogToScreen() {
		return "**** NEW WARNING FROM AGENT " + getCaller() + " :" + "\n"
		+ (this.e == null ? this.e : "") + "** On Host" + this.host
		+ "(" + this.date.toString() + " - " + this.date.getTime()
		+ "):\n" + this.text;
	}

	@Override
	public String generateLogToWrite() {
		return "WARNING ** On Host" + this.host + "(" + this.date.toString()
		+ " - " + this.date.getTime() + "):\n" + this.text;
	}
}
