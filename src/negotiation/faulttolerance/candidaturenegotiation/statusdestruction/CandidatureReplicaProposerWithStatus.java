package negotiation.faulttolerance.candidaturenegotiation.statusdestruction;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import negotiation.faulttolerance.negotiatingagent.HostState;
import negotiation.faulttolerance.negotiatingagent.ReplicaState;
import negotiation.faulttolerance.negotiatingagent.ReplicationCandidature;
import negotiation.faulttolerance.negotiatingagent.ReplicationSpecification;
import negotiation.negotiationframework.SimpleNegotiatingAgent;
import negotiation.negotiationframework.interaction.candidatureprotocol.CandidatureReplicaProposer;
import negotiation.negotiationframework.interaction.candidatureprotocol.status.AgentStateStatus;
import negotiation.negotiationframework.interaction.candidatureprotocol.status.DestructionOrder;
import negotiation.negotiationframework.interaction.consensualnegotiation.AbstractProposerCore;
import dima.introspectionbasedagents.NotReadyException;

public class CandidatureReplicaProposerWithStatus extends CandidatureReplicaProposer
implements
AbstractProposerCore<
SimpleNegotiatingAgent<ReplicationSpecification, ReplicaState, ReplicationCandidature>,
ReplicationSpecification, ReplicaState, ReplicationCandidature> {
	private static final long serialVersionUID = -5315491050460219982L;


	@Override
	public Set<ReplicationCandidature> getNextContractsToPropose()
			throws NotReadyException {

		final Set<ReplicationCandidature> candidatures = new HashSet<ReplicationCandidature>();

		if (this.stateStatusIs(this.getMyAgent().getMyCurrentState(),
				AgentStateStatus.Wastefull)) {
			final List<HostState> replicas = new ArrayList<HostState>(
					this.getMyAgent().getMyCurrentState().getMyReplicas());
			Collections.shuffle(replicas);


			ReplicaState nextState = this.getMyAgent().getMyCurrentState();


			while (this.stateStatusIs(nextState, AgentStateStatus.Wastefull)
					&& !replicas.isEmpty()){

				final ReplicationCandidature destructionCandidature =
						new DestructionOrder(
						replicas.remove(0).getMyAgentIdentifier(), this.getMyAgent().getIdentifier(),true);
				final HostState host = this.getMyAgent().getMyInformation()
						.getInformation(
								HostState.class,
								destructionCandidature.getResource());
				destructionCandidature.setSpecification(host);

				if (this.getMyAgent().respectMyRights(
						this.getMyAgent().getMyResultingState(nextState,
								destructionCandidature))
								|| this.stateStatusIs(
										this.getMyAgent().getMyResultingState(nextState,
												destructionCandidature),
												AgentStateStatus.Fragile)){
					//on ne fait rien et on fait sauter cette candidature de destruction
				} else {

					candidatures.add(destructionCandidature);

					nextState = this.getMyAgent().getMyResultingState(nextState,
							destructionCandidature);


				}
			}

			//			logMonologue("Wastefull!!! Proposing :\n"+candidatures);
			// Application direct des demandes de destruction qui seront
			// directement ex��cuter par les h��tes
			//			for (final ReplicationCandidature c : candidatures)
			//				this.getMyAgent().execute(c);

		} else if (this.stateStatusIs(this.getMyAgent().getMyCurrentState(),
				AgentStateStatus.Fragile))
			candidatures.addAll(super.getNextContractsToPropose());

		return candidatures;
	}

	protected boolean stateStatusIs(final ReplicaState state,
			final AgentStateStatus status) {
		return ((CandidatureReplicaCoreWithStatus) this.getMyAgent()
				.getMyCore()).getStatus(state).equals(status);
	}

}



//@Override
//public Collection<ReplicationCandidature> getNextContractsToPropose()
//		throws NotReadyException {
//
//	final Collection<ReplicationCandidature> candidatures = new ArrayList<ReplicationCandidature>();
//
//	if (this.stateStatusIs(this.getMyAgent().getMyCurrentState(),
//			AgentStateStatus.Wastefull)) {
//		final List<ResourceIdentifier> replicas = new ArrayList<ResourceIdentifier>(
//				this.getMyAgent().getMyCurrentState().getMyReplicas());
//		Collections.shuffle(replicas);
//		ReplicaState nextState = this.getMyAgent().getMyCurrentState();
//		ReplicationCandidature destructionCandidature = new DestructionOrder(
//				replicas.remove(0), this.getMyAgent().getIdentifier(),true);
//		HostState host = this
//				.getMyAgent()
//				.getMyInformation()
//				.getInformation(HostState.class,
//						destructionCandidature.getResource());
//		destructionCandidature.setSpecification(host);
//
//		while (this.stateStatusIs(nextState, AgentStateStatus.Wastefull)
//				&& this.getMyAgent().respectMyRights(
//						this.getMyAgent().getMyResultingState(nextState,
//								destructionCandidature))) {
//
//			candidatures.add(destructionCandidature);
//
//			nextState = this.getMyAgent().getMyResultingState(nextState,
//					destructionCandidature);
//
//			destructionCandidature = new DestructionOrder(
//					replicas.remove(0), this.getMyAgent().getIdentifier(),true);
//			host = this
//					.getMyAgent()
//					.getMyInformation()
//					.getInformation(HostState.class,
//							destructionCandidature.getResource());
//			destructionCandidature.setSpecification(host);
//		}
//
////		logMonologue("Wastefull!!! Proposing :\n"+candidatures);
//		// Application direct des demandes de destruction qui seront
//		// directement ex��cuter par les h��tes
//		//			for (final ReplicationCandidature c : candidatures)
//		//				this.getMyAgent().execute(c);
//
//	} else if (this.stateStatusIs(this.getMyAgent().getMyCurrentState(),
//			AgentStateStatus.Fragile))
//		candidatures.addAll(super.getNextContractsToPropose());
//
//	return candidatures;
//}