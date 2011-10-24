package negotiation.faulttolerance.experimentation;

import java.util.Date;

import negotiation.experimentationframework.ExperimentationResults;
import negotiation.faulttolerance.negotiatingagent.HostState;
import negotiation.negotiationframework.interaction.ResourceIdentifier;
import dima.basicagentcomponents.AgentIdentifier;

public class ReplicationHostResult implements ExperimentationResults {

	/**
	 *
	 */
	private static final long serialVersionUID = 4602509671903952286L;

	private final long creation;

	final ResourceIdentifier id;
	final Double charge;
	// final Double lambda;

	final boolean isFaulty;
	boolean lastInfo;

	public ReplicationHostResult(final HostState s,
			final Date agentCreationTime, final boolean last) {
		super();
		this.creation = new Date().getTime() - agentCreationTime.getTime();
		this.charge = s.getMyCharge();
		// this.lambda = s.lambda;
		this.isFaulty = s.isFaulty();
		this.id = s.getMyAgentIdentifier();
		this.lastInfo = last;
	}

	@Override
	public long getUptime() {
		return this.creation;
	}

	@Override
	public AgentIdentifier getId() {
		return this.id;
	}

	@Override
	public boolean hasDied() {
		return this.lastInfo;
	}

	@Override
	public boolean isHost() {
		return true;
	}
}
