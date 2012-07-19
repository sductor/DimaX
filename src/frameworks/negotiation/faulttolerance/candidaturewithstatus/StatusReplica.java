package frameworks.negotiation.faulttolerance.candidaturewithstatus;

import dima.basicagentcomponents.AgentIdentifier;
import dima.introspectionbasedagents.annotations.Competence;
import dima.introspectionbasedagents.services.CompetenceException;
import dima.introspectionbasedagents.services.core.opinion.SimpleOpinionService;
import frameworks.negotiation.faulttolerance.Replica;
import frameworks.negotiation.faulttolerance.negotiatingagent.ReplicaCore;
import frameworks.negotiation.faulttolerance.negotiatingagent.ReplicaState;
import frameworks.negotiation.faulttolerance.negotiatingagent.ReplicaStateOpinionHandler;
import frameworks.negotiation.faulttolerance.negotiatingagent.ReplicationCandidature;
import frameworks.negotiation.negotiationframework.contracts.ResourceIdentifier;
import frameworks.negotiation.negotiationframework.protocoles.ReverseCFPProtocol;
import frameworks.negotiation.negotiationframework.protocoles.AbstractCommunicationProtocol.SelectionCore;
import frameworks.negotiation.negotiationframework.protocoles.status.StatusAgent;
import frameworks.negotiation.negotiationframework.protocoles.status.StatusObservationCompetence;
import frameworks.negotiation.negotiationframework.protocoles.status.StatusProposerCore;
import frameworks.negotiation.negotiationframework.protocoles.status.StatusRationalCore;
import frameworks.negotiation.negotiationframework.protocoles.status.StatusObservationCompetence.AgentStateStatus;

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
			final AgentIdentifier myLaborantin) throws CompetenceException{
		this (id, myState, participantCore, simultaneousCandidature,dynamicCriticity);
		this.soc=new StatusObservationCompetence(myLaborantin,true, ReplicaState.class);
	}
	public StatusReplica(final AgentIdentifier id,
			final ReplicaState myState,
			final SelectionCore participantCore,
			final int simultaneousCandidature,
			final boolean dynamicCriticity,
			final int numberToContact) throws CompetenceException{
		this (id, myState, participantCore, simultaneousCandidature,dynamicCriticity);
		this.soc=new StatusObservationCompetence(numberToContact, true, ReplicaState.class);
	}

	private StatusReplica(final AgentIdentifier id,
			final ReplicaState myState,
			final SelectionCore participantCore,
			final int simultaneousCandidature,
			final boolean dynamicCriticity)
					throws CompetenceException {
		super(id,
				myState,
				new StatusRationalCore<ReplicaState, ReplicationCandidature>(new ReplicaCore(true, true)),
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
		new ReverseCFPProtocol(),
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

}
