package dimaxx.experimentation;

import java.util.Date;

import negotiation.negotiationframework.protocoles.status.AgentStateStatus;

import dima.basicagentcomponents.AgentIdentifier;

public class SimpleExperimentationResults implements ExperimentationResults {

	final AgentIdentifier myAgentIdentifier;

	private final long creation;
	boolean lastInfo;
	
	public SimpleExperimentationResults(
			final AgentIdentifier id,
			final Date agentCreationTime,
			boolean lastInfo) {
		super();
		this.creation = new Date().getTime() - agentCreationTime.getTime();
		this.myAgentIdentifier = id;
		this.lastInfo = lastInfo;
	}

	@Override
	public AgentIdentifier getId() {
		return this.myAgentIdentifier;
	}

	@Override
	public boolean isLastInfo() {
		return this.lastInfo;
	}

	@Override
	public long getUptime() {
		return this.creation;
	}

	@Override
	public void setLastInfo() {
		this.lastInfo=true;
	}
}
