package negotiation.faulttolerance.candidaturewithstatus;

import negotiation.faulttolerance.experimentation.Replica;
import negotiation.faulttolerance.negotiatingagent.ReplicaCore;
import negotiation.faulttolerance.negotiatingagent.ReplicaState;
import negotiation.faulttolerance.negotiatingagent.ReplicationCandidature;
import negotiation.negotiationframework.contracts.ResourceIdentifier;
import negotiation.negotiationframework.protocoles.AbstractCommunicationProtocol.SelectionCore;
import negotiation.negotiationframework.protocoles.ReverseCFPProtocol;
import negotiation.negotiationframework.protocoles.status.StatusAgent;
import negotiation.negotiationframework.protocoles.status.StatusObservationCompetence;
import negotiation.negotiationframework.protocoles.status.StatusObservationCompetence.AgentStateStatus;
import negotiation.negotiationframework.protocoles.status.StatusProposerCore;
import negotiation.negotiationframework.protocoles.status.StatusRationalCore;
import dima.basicagentcomponents.AgentIdentifier;
import dima.introspectionbasedagents.annotations.Competence;
import dima.introspectionbasedagents.services.CompetenceException;
import dima.introspectionbasedagents.services.information.SimpleOpinionService;

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
		this.soc=new StatusObservationCompetence(myLaborantin,false, ReplicaState.class);
	}
	public StatusReplica(final AgentIdentifier id,
			final ReplicaState myState,
			final SelectionCore participantCore,
			final int simultaneousCandidature,
			final boolean dynamicCriticity,
			final int numberToContact) throws CompetenceException{
		this (id, myState, participantCore, simultaneousCandidature,dynamicCriticity);
		this.soc=new StatusObservationCompetence(numberToContact, false, ReplicaState.class);
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
		new SimpleOpinionService(),
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
