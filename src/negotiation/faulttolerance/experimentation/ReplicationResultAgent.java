package negotiation.faulttolerance.experimentation;

import java.util.Date;

import negotiation.experimentationframework.ExperimentationResults;
import negotiation.faulttolerance.negotiatingagent.ReplicaState;
import negotiation.negotiationframework.proposercores.status.AgentStateStatus;
import dima.basicagentcomponents.AgentIdentifier;

public class ReplicationResultAgent implements ExperimentationResults {

	/**
	 *
	 */
	private static final long serialVersionUID = -1872609884691513136L;

	private final long creation;

	final AgentIdentifier myAgentIdentifier;

	final Double criticity;
	final Double disponibility;
	final Double reliability;

	//	public void setiAmDead(final boolean iAmDead) {
	//		this.iAmDead = iAmDead;
	//	}

	boolean lastInfo;

	final AgentStateStatus status;

	public ReplicationResultAgent(final ReplicaState s,
			final Date agentCreationTime, final AgentStateStatus status) {
		super();
		this.creation = new Date().getTime() - agentCreationTime.getTime();
		this.myAgentIdentifier = s.getMyAgentIdentifier();
		this.criticity = s.getMyCriticity();
		this.disponibility = s.getMyDisponibility();
		this.reliability = s.getMyReliability();
		this.lastInfo = s.getMyDisponibility() == 0;
		this.status = status;
	}

	public ReplicationResultAgent(final ReplicaState s,
			final Date agentCreationTime) {
		super();
		this.creation = new Date().getTime() - agentCreationTime.getTime();
		this.myAgentIdentifier = s.getMyAgentIdentifier();
		this.criticity = s.getMyCriticity();
		this.disponibility = s.getMyDisponibility();
		this.reliability = s.getMyReliability();
		this.lastInfo = s.getMyDisponibility() == 0;
		this.status = null;
	}

	@Override
	public AgentIdentifier getId() {
		return this.myAgentIdentifier;
	}

	public double getCriticity() {
		return this.criticity;
	}

	public double getDisponibility() {
		return this.disponibility;
	}

	public double getReliability() {
		return this.reliability;
	}

	@Override
	public boolean isLastInfo() {
		return this.lastInfo;
	}

	@Override
	public long getUptime() {
		return this.creation;
	}

	public AgentStateStatus getStatus() {
		return this.status;
	}

	@Override
	public boolean isHost() {
		return false;
	}

	@Override
	public void setLastInfo() {
		this.lastInfo=true;
	}

}
