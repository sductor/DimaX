package dima.introspectionbasedagents.coreservices.loggingactivity;

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

	public LogWarning(final AgentIdentifier id,final String text, final String details) {
		super(id,text,details);
	}

	public LogWarning(final AgentIdentifier id,final String text, final String details, final Throwable e) {
		super(id,text,details,e);
	}

	//
	// Methods
	//

	@Override
	public String generateLogToScreen(final boolean printDetails) {
		return "**** NEW WARNING FROM AGENT " + getCaller() + " :" + "\n"
		+ (this.e == null ? this.e : "") + "** On Host" + this.host
		+ "(" + this.date.toString() + " - " + this.date.getTime()
		+ "):\n" + this.text
		+ (printDetails && this.details != null ? this.details : "");
	}

	@Override
	public String generateLogToWrite(final boolean printDetails) {
		return "WARNING ** On Host" + this.host + "(" + this.date.toString()
		+ " - " + this.date.getTime() + "):\n" + this.text
		+ (printDetails && this.details != null ? this.details : "");
	}
}
