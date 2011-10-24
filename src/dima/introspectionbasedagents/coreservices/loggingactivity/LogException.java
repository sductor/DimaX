package dima.introspectionbasedagents.coreservices.loggingactivity;

import java.util.Date;

import dima.basicagentcomponents.AgentIdentifier;
import dimaxx.hostcontrol.LocalHost;

public class LogException extends LogNotification {

	private static final long serialVersionUID = -2335142989408051767L;

	String text, details;
	Throwable e = null;
	final String host = LocalHost.getUrl();

	public LogException(final AgentIdentifier id,final String text) {
		super(id);
		this.text = text;
	}

	public LogException(final AgentIdentifier id,final String text, final Throwable e) {
		super(id);
		this.text = text;
		this.e = e;
	}

	public LogException(final AgentIdentifier id,final String text, final String details) {
		super(id);
		this.text = text;
	}

	public LogException(
			final AgentIdentifier id,
			final String text, 
			final String details,
			final Throwable e) {
		super(id);
		this.text = text;
		this.e = e;
	}

	//
	// Accessors
	//

	/**
	 * @return the exception
	 */
	protected Throwable getException() {
		return this.e;
	}

	//
	// Methods
	//

	@Override
	public String generateLogToScreen(final boolean printDetails) {
		return "**** NEW EXCEPTION FROM AGENT " + getCaller() + " :" + "\n"
		+ (this.e == null ? "" : this.e ) + "** On Host" + this.host
		+ "(" + this.date.toString() + " - " + this.date.getTime()
		+ "):\n" + this.text
		+ (printDetails && this.details != null ? this.details : "");
	}

	@Override
	public String generateLogToWrite(final boolean printDetails) {
		return "EXCEPTION ** FROM AGENT " + getCaller() + " On Host" + this.host + "(" + this.date.toString()
		+ " - " + this.date.getTime() + "):\n" + this.text
		+ (printDetails && this.details != null ? this.details : "");
	}
}
