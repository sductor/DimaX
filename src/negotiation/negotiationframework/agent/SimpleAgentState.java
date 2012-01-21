package negotiation.negotiationframework.agent;

import java.util.Date;

import dima.basicagentcomponents.AgentIdentifier;

public abstract class SimpleAgentState implements AgentState {
	private static final long serialVersionUID = -1317496111744783996L;

	final AgentIdentifier myAgent;
	private Long creationTime;

	public SimpleAgentState(final AgentIdentifier myAgent) {
		super();
		this.myAgent = myAgent;
		this.creationTime = new Date().getTime();
	}

	public SimpleAgentState(final AgentIdentifier myAgent,
			final Long creationTime) {
		super();
		this.myAgent = myAgent;
		this.creationTime = creationTime;
	}

	@Override
	public AgentIdentifier getMyAgentIdentifier() {
		return this.myAgent;
	}

	@Override
	public Long getCreationTime() {
		return this.creationTime;
	}

	@Override
	public long getUptime() {
		return new Date().getTime() - this.creationTime;
	}

	@Override
	public void resetUptime() {
		this.creationTime = new Date().getTime();
	}

	@Override
	public String toString() {
		return "State of agent " + this.myAgent;// +" generated on "+creationTime;
	}

	@Override
	public boolean isNewerThan(final AgentState that) {
		return this.creationTime> ((SimpleAgentState)that).creationTime;
	}
}
