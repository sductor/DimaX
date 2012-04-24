package negotiation.faulttolerance.candidaturewithstatus;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import negotiation.faulttolerance.negotiatingagent.HostState;
import negotiation.faulttolerance.negotiatingagent.ReplicaState;
import negotiation.faulttolerance.negotiatingagent.ReplicationCandidature;
import negotiation.faulttolerance.negotiatingagent.ReplicationSpecification;
import negotiation.negotiationframework.SimpleNegotiatingAgent;
import negotiation.negotiationframework.contracts.ContractTrunk;
import negotiation.negotiationframework.contracts.ResourceIdentifier;
import negotiation.negotiationframework.protocoles.AbstractCommunicationProtocol.ProposerCore;
import negotiation.negotiationframework.protocoles.AtMostKCandidaturesProposer;
import negotiation.negotiationframework.protocoles.status.AgentStateStatus;
import negotiation.negotiationframework.protocoles.status.DestructionOrder;
import dima.introspectionbasedagents.services.UnrespectedCompetenceSyntaxException;
import dima.introspectionbasedagents.shells.NotReadyException;

public class CandidatureReplicaProposerWithStatus extends AtMostKCandidaturesProposer<ReplicationSpecification, ReplicaState, ReplicationCandidature>
implements
ProposerCore<
SimpleNegotiatingAgent<ReplicationSpecification, ReplicaState, ReplicationCandidature>,
ReplicationSpecification, ReplicaState, ReplicationCandidature> {
	public CandidatureReplicaProposerWithStatus(int k)
			throws UnrespectedCompetenceSyntaxException {
		super(k);
	}


	private static final long serialVersionUID = -5315491050460219982L;


	@Override
	public Set<ReplicationCandidature> getNextContractsToPropose()
			throws NotReadyException {

		final Set<ReplicationCandidature> candidatures = new HashSet<ReplicationCandidature>();

		if (this.stateStatusIs(this.getMyAgent().getMyCurrentState(),
				AgentStateStatus.Wastefull)) {
			final List<HostState> replicas = new ArrayList<HostState>();
			replicas.addAll((Collection<? extends HostState>) this.getMyAgent().getMyResources());
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

				if (this.getMyAgent().getMyResultingState(nextState,
						destructionCandidature).isValid()
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
				AgentStateStatus.Fragile)) {
			candidatures.addAll(super.getNextContractsToPropose());
		}

		return candidatures;
	}

	protected boolean stateStatusIs(final ReplicaState state,
			final AgentStateStatus status) {
		return ((CandidatureReplicaCoreWithStatus) this.getMyAgent()
				.getMyCore()).getStatus(state).equals(status);
	}

	@Override
	public ReplicationCandidature constructCandidature(final ResourceIdentifier id) {
		return new ReplicationCandidature(id,this.getMyAgent().getIdentifier(),true,true);

	}


	@Override
	public boolean IWantToNegotiate(final ReplicaState s,
			final ContractTrunk<ReplicationCandidature, ReplicationSpecification, ReplicaState> contracts) {
		((CandidatureReplicaCoreWithStatus)this.getMyAgent().getMyCore()).updateThreshold();
		//		System.out.println(super.IWantToNegotiate(s)+" "+this.getStatus(s)+(super.IWantToNegotiate(s)
		//				&& (this.getStatus(s).equals(AgentStateStatus.Fragile) || this
		//						.getStatus(s).equals(AgentStateStatus.Wastefull))));
		return super.IWantToNegotiate(s,contracts)
				&& (((CandidatureReplicaCoreWithStatus)this.getMyAgent().getMyCore()).getStatus(s).equals(AgentStateStatus.Fragile) ||
						((CandidatureReplicaCoreWithStatus)this.getMyAgent().getMyCore())
						.getStatus(s).equals(AgentStateStatus.Wastefull));
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