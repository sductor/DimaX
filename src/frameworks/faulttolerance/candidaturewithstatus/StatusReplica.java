package frameworks.faulttolerance.candidaturewithstatus;

import dima.basicagentcomponents.AgentIdentifier;
import dima.introspectionbasedagents.annotations.Competence;
import dima.introspectionbasedagents.services.CompetenceException;
import frameworks.faulttolerance.Replica;
import frameworks.faulttolerance.negotiatingagent.ReplicaCore;
import frameworks.faulttolerance.negotiatingagent.ReplicaState;
import frameworks.faulttolerance.negotiatingagent.ReplicaStateOpinionHandler;
import frameworks.faulttolerance.negotiatingagent.ReplicationCandidature;
import frameworks.negotiation.contracts.ResourceIdentifier;
import frameworks.negotiation.opinion.SimpleOpinionService;
import frameworks.negotiation.protocoles.AbstractCommunicationProtocol.SelectionCore;
import frameworks.negotiation.protocoles.status.StatusAgent;
import frameworks.negotiation.protocoles.status.StatusAgentRationalCore;
import frameworks.negotiation.protocoles.status.StatusObservationCompetence;
import frameworks.negotiation.protocoles.status.StatusObservationCompetence.AgentStateStatus;
import frameworks.negotiation.protocoles.status.StatusProposerCore;
import frameworks.negotiation.protocoles.status.StatusProtocol;

public class StatusReplica extends Replica<ReplicationCandidature> implements StatusAgent<ReplicaState, ReplicationCandidature> {


	//
	// Competences
	//

	/**
	 * 
	 */
	private static final long serialVersionUID = -1107324270380354817L;
	@Competence
	public  StatusObservationCompetence soc;


	//
	// Constructor
	//

	public StatusReplica(final AgentIdentifier id,
			final ReplicaState myState,
			final SelectionCore participantCore,
			final int simultaneousCandidature,
			final boolean dynamicCriticity,
			final AgentIdentifier myLaborantin,
			final double alpha_low, final double alpha_high) throws CompetenceException{
		this (id, myState, participantCore, simultaneousCandidature,dynamicCriticity);
		this.soc=new StatusObservationCompetence(myLaborantin,true, ReplicaState.class, alpha_low, alpha_high);
	}
	public StatusReplica(final AgentIdentifier id,
			final ReplicaState myState,
			final SelectionCore participantCore,
			final int simultaneousCandidature,
			final boolean dynamicCriticity,
			final int numberToContact,
			final double alpha_low, final double alpha_high) throws CompetenceException{
		this (id, myState, participantCore, simultaneousCandidature,dynamicCriticity);
		this.soc=new StatusObservationCompetence(numberToContact, true, ReplicaState.class, alpha_low, alpha_high);
	}

	private StatusReplica(final AgentIdentifier id,
			final ReplicaState myState,
			final SelectionCore participantCore,
			final int simultaneousCandidature,
			final boolean dynamicCriticity)
					throws CompetenceException {
		super(id,
				myState,
				new StatusAgentRationalCore<ReplicaState, ReplicationCandidature>(new ReplicaCore(false, true)),
				participantCore,
				new StatusProposerCore<ReplicaState, ReplicationCandidature>(simultaneousCandidature) {

			/**
			 * 
			 */
			private static final long serialVersionUID = 7020056484100065704L;

			@Override
			public ReplicationCandidature constructDestructionCandidature(
					final ResourceIdentifier id) {
				return new ReplicationCandidature(id, this.getIdentifier(), false, true);
			}

			@Override
			public ReplicationCandidature constructCandidature(
					final ResourceIdentifier id) {
				return new ReplicationCandidature(id, this.getIdentifier(), true, true);
			}
		},
		new SimpleOpinionService(new ReplicaStateOpinionHandler(myState.getSocialWelfare(),id)),
		new StatusProtocol(null),
		dynamicCriticity);
	}

	//
	// Delegated
	//

	@Override
	public boolean stateStatusIs(final ReplicaState state, final AgentStateStatus status) {
		return this.soc.stateStatusIs(state, status);
	}
	@Override
	public AgentStateStatus getMyStatus() {
		return this.soc.getMyStatus();
	}
	@Override
	public AgentStateStatus getStatus(final ReplicaState s) {
		return this.soc.getStatus(s);
	}
	@Override
	public void updateThreshold() {
		this.soc.updateThreshold();
	}
	//	@Override
	//	public ReplicationCandidature generateDestructionContract(AgentIdentifier id) {
	//		return new ReplicationCandidature((ResourceIdentifier) id, this.getIdentifier(), false, true);
	//	}
	//	@Override
	//	public ReplicationCandidature generateCreationContract(AgentIdentifier id) {
	//		return new ReplicationCandidature((ResourceIdentifier) id, this.getIdentifier(), true, true);
	//	}

}
