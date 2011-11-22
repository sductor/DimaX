package negotiation.faulttolerance.candidaturenegotiation.statusdestruction;

import java.util.Collection;

import dima.introspectionbasedagents.services.library.information.OpinionService;
import dima.introspectionbasedagents.services.library.information.OpinionService.Opinion;

import negotiation.faulttolerance.ReplicationCandidature;
import negotiation.faulttolerance.experimentation.ReplicationExperimentationParameters;
import negotiation.faulttolerance.experimentation.ReplicationExperimentationProtocol;
import negotiation.faulttolerance.negotiatingagent.ReplicaState;
import negotiation.faulttolerance.negotiatingagent.ReplicaCore;
import negotiation.negotiationframework.interaction.candidatureprotocol.status.AgentStateStatus;

public class CandidatureReplicaCoreWithStatus extends ReplicaCore {
	private static final long serialVersionUID = -3882932472033817195L;



	// public CandidatureReplicaCoreWithStatus(
	// final SimpleNegotiatingAgent<ReplicaState, ReplicationCandidature,
	// ReplicationSpecification> ag) {
	// super(ag);
	// }

	@Override
	public int getAllocationPreference(final ReplicaState s,
			final Collection<ReplicationCandidature> c1,
			final Collection<ReplicationCandidature> c2) {
		final ReplicaState s1 = this.getMyAgent().getMyResultingState(s, c1);
		final ReplicaState s2 = this.getMyAgent().getMyResultingState(s, c2);

		if (this.getStatus(s1).equals(AgentStateStatus.Wastefull)
				&& this.getStatus(s2).equals(AgentStateStatus.Wastefull))
			return this.getAllocationReliabilityPreference(s, c2, c1);// ATTENTION
		// : on
		// inverse
		// ici
		// pcq
		// ��tant
		// wastefull
		// il
		// cherche
		// a
		// diminuer
		// sa
		// reliability;
		else if (this.getStatus(s1).equals(AgentStateStatus.Wastefull))// s2
			// n'est
			// pas
			// wastefull
			return -1;
		else if (this.getStatus(s2).equals(AgentStateStatus.Wastefull))// s1
			// n'est
			// pas
			// wastefull
			return 1;
		else
			// aucun contrat ne rend wastefull
			return this.getFirstLoadSecondReliabilitAllocationPreference(s, c1,
					c2);
	}

	@Override
	public boolean IWantToNegotiate(final ReplicaState s) {
		updateThreshold();
//		System.out.println(super.IWantToNegotiate(s)+" "+this.getStatus(s)+(super.IWantToNegotiate(s)
//				&& (this.getStatus(s).equals(AgentStateStatus.Fragile) || this
//						.getStatus(s).equals(AgentStateStatus.Wastefull))));
		return super.IWantToNegotiate(s)
		&& (this.getStatus(s).equals(AgentStateStatus.Fragile) || this
				.getStatus(s).equals(AgentStateStatus.Wastefull));
	}

	public AgentStateStatus getMyStatus() {
//		if (this.getStatus(this.getMyAgent().getMyCurrentState()).equals(AgentStateStatus.Wastefull))
//			logException("yooooooohoooooo!");
		return this.getStatus(this.getMyAgent().getMyCurrentState());
	}

	/*
	 *
	 *
	 *
	 */

	public AgentStateStatus getStatus(final ReplicaState s) {
		boolean empty = this.getMyAgent().getMyCurrentState()
		.getMyReplicas().size() <= 1;
		boolean full = this.getMyAgent().getMyCurrentState()
		.getMyReplicas().size() == this.getMyAgent().getMyInformation()
		.getKnownAgents().size();
		boolean fragile = this.getMyAgent().getMyCurrentState()
		.getMyReliability() <= this.getLowerThreshold();
		boolean wastefull = this.getMyAgent().getMyCurrentState()
		.getMyReliability() > this.getHigherThreshold();

		if (wastefull && fragile)
			throw new RuntimeException(
					"impossible! : " +
					"me: "+this.getMyAgent().getMyCurrentState().getMyReliability()+
					" low : "+this.getLowerThreshold()
					+" high "+this.getHigherThreshold()+
					(this.getMyAgent().getMyCurrentState().getMyReliability() <= this.getLowerThreshold())
					+" "+(this.getMyAgent().getMyCurrentState().getMyReliability() > this.getHigherThreshold())
					+wastefull+" "+fragile+" "+(wastefull && fragile));
		else if (full && !wastefull)
			return AgentStateStatus.Full;
		else if (empty && !fragile)
			return AgentStateStatus.Empty;
		else if (!wastefull && !fragile)
			return AgentStateStatus.Thrifty;
		else if (wastefull && !empty)
			return AgentStateStatus.Wastefull;
		else if (fragile && !full)
			return AgentStateStatus.Fragile;
		else
			throw new RuntimeException("impossible!");
	}

	/*
	 *
	 *
	 *
	 */

	private Double lowerThreshold = Double.NaN;
	private Double higherThreshold = Double.NaN;

	private Double getLowerThreshold() {
		if (this.lowerThreshold.equals(Double.NaN))
			return Double.POSITIVE_INFINITY;
		else
			return this.lowerThreshold;
	}

	private Double getHigherThreshold() {
		if (this.higherThreshold.equals(Double.NaN))
			return Double.POSITIVE_INFINITY;
		else
			return this.higherThreshold;
	}

	private void updateThreshold(){
		try {
			Opinion<ReplicaState> o = ((OpinionService) getMyAgent().getMyInformation()).getGlobalOpinion(ReplicaState.class);
//		System.out.println("updating status opinion is : "+o);
			Double mean, min, max;
			mean = o.getRepresentativeElement().getMyReliability();
			min = o.getMinElement().getMyReliability();
			max = o.getMaxElement().getMyReliability();

			lowerThreshold = ReplicationExperimentationParameters.alpha_low * ((mean + min)/2);
			higherThreshold = ReplicationExperimentationParameters.alpha_high * ((mean + max)/2);
		} catch (Exception e) {
			getMyAgent().signalException("impossible on raisonne sur son propre ��tat il doit etre au moins pr��sent!\n"+getMyAgent().getMyInformation(), e);
			throw new RuntimeException();
		}
	}
}


//
//public void beNotified(final SystemInformationMessage notification) {
//	this.lowerThreshold = notification.lowerThreshold;
//	this.higherThreshold = notification.higherThreshold;
//	// logMonologue("update!!!!!!!!!!\n * myReliab="+getMyAgent().getMyCurrentState().getMyReliability()+notification+"\n * my status "+getMyStatus());
//}