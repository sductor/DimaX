package frameworks.faulttolerance.experimentation;

import java.util.Date;

import dima.basicagentcomponents.AgentIdentifier;
import frameworks.experimentation.ExperimentationResults;
import frameworks.faulttolerance.negotiatingagent.ReplicaState;
import frameworks.faulttolerance.negotiatingagent.ReplicationSocialOptimisation;
import frameworks.negotiation.protocoles.status.StatusObservationCompetence.AgentStateStatus;
import frameworks.negotiation.rationality.SocialChoiceFunction.SocialChoiceType;

public class ReplicationResultAgent implements ExperimentationResults {

	/**
	 *
	 */
	private static final long serialVersionUID = -1872609884691513136L;

	private final long creation;

	final AgentIdentifier myAgentIdentifier;

	final Double criticity;
	final Double disponibility;
	private final long lastModifTime;
	int nbOfModif;

	final int numberOfAllocatedResources;
	//	public void setiAmDead(final boolean iAmDead) {
	//		this.iAmDead = iAmDead;
	//	}

	boolean lastInfo;

	final AgentStateStatus status;

	public ReplicationResultAgent(final ReplicaState s,
			final long lastModifTime,
			final int initialStateCounter,
			final Date agentCreationTime, final AgentStateStatus status) {
		super();
		this.creation = new Date().getTime() - agentCreationTime.getTime();
		this.myAgentIdentifier = s.getMyAgentIdentifier();
		this.criticity = s.getMyCriticity();
		this.disponibility = s.getMyDisponibility();
		this.lastInfo = s.getMyDisponibility() == 0;
		this.lastModifTime=lastModifTime;
		this.nbOfModif = s.getStateCounter()-initialStateCounter;
		this.numberOfAllocatedResources=s.getMyResourceIdentifiers().size();
		this.status = status;
	}

	public ReplicationResultAgent(final ReplicaState s,
			final long lastModifTime,
			final Date agentCreationTime) {
		super();
		this.creation = new Date().getTime() - agentCreationTime.getTime();
		this.myAgentIdentifier = s.getMyAgentIdentifier();
		this.criticity = s.getMyCriticity();
		this.disponibility = s.getMyDisponibility();
		this.lastInfo = s.getMyDisponibility() == 0;
		this.lastModifTime=lastModifTime;
		this.numberOfAllocatedResources=s.getMyResourceIdentifiers().size();
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

	public double getReliability(final SocialChoiceType welfare) {
		return ReplicationSocialOptimisation.getReliability(this.getDisponibility(), this.getCriticity(), welfare);
	}

	@Override
	public boolean isLastInfo() {
		return this.lastInfo;
	}
	public long getLastModifTime() {
		return this.lastModifTime;
	}
	@Override
	public long getUptime() {
		return this.creation;
	}

	public AgentStateStatus getStatus() {
		return this.status;
	}


	public boolean isHost() {
		return false;
	}

	@Override
	public void setLastInfo() {
		this.lastInfo=true;
	}

	public int getNumberOfAllocatedResources() {
		return this.numberOfAllocatedResources;
	}

}
