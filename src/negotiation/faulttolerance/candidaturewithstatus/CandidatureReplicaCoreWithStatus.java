package negotiation.faulttolerance.candidaturewithstatus;

import java.util.Collection;

import negotiation.faulttolerance.negotiatingagent.ReplicaCore;
import negotiation.faulttolerance.negotiatingagent.ReplicaState;
import negotiation.faulttolerance.negotiatingagent.ReplicationCandidature;
import negotiation.negotiationframework.NegotiationParameters;
import negotiation.negotiationframework.protocoles.status.AgentStateStatus;
import dima.introspectionbasedagents.annotations.StepComposant;
import dima.introspectionbasedagents.annotations.Transient;
import dima.introspectionbasedagents.services.information.OpinionService;
import dima.introspectionbasedagents.services.information.OpinionService.Opinion;

public class CandidatureReplicaCoreWithStatus extends ReplicaCore {
	private static final long serialVersionUID = -3882932472033817195L;


	//
	// Status
	//


	@StepComposant()
	@Transient
	public boolean initialynotifyMyState4Status() {
		this.notifyMyReliability4Status();
		return true;
	}

	@StepComposant(ticker = NegotiationParameters._statusObservationFrequency)
	public void notifyMyReliability4Status() {
		// logMonologue("relia send to "+observer.getObserver(ReplicationExperimentationProtocol.reliabilityObservationKey));
		this.notify(
				this.getMyAgent().getMyCurrentState().getMyReliability(),
				ObservingStatusService.reliabilityObservationKey);
	}

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
				&& this.getStatus(s2).equals(AgentStateStatus.Wastefull)) {
			return this.getAllocationReliabilityPreference(s, c2, c1);// ATTENTION
		} else if (this.getStatus(s1).equals(AgentStateStatus.Wastefull)) {
			// n'est
			// pas
			// wastefull
			return -1;
		} else if (this.getStatus(s2).equals(AgentStateStatus.Wastefull)) {
			// n'est
			// pas
			// wastefull
			return 1;
		} else {
			// aucun contrat ne rend wastefull
			return this.getFirstLoadSecondReliabilitAllocationPreference(s, c1,
					c2);
		}
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
		final boolean empty = this.getMyAgent().getMyCurrentState()
				.getMyResourceIdentifiers().size() <= 1;
		final boolean full = this.getMyAgent().getMyCurrentState()
				.getMyResourceIdentifiers().size() == this.getMyAgent().getMyInformation()
				.getKnownAgents().size();
		final boolean fragile = this.getMyAgent().getMyCurrentState()
				.getMyReliability() <= this.getLowerThreshold();
		final boolean wastefull = this.getMyAgent().getMyCurrentState()
				.getMyReliability() > this.getHigherThreshold();

				if (wastefull && fragile) {
					throw new RuntimeException(
							"impossible! : " +
									"me: "+this.getMyAgent().getMyCurrentState().getMyReliability()+
									" low : "+this.getLowerThreshold()
									+" high "+this.getHigherThreshold()+
									(this.getMyAgent().getMyCurrentState().getMyReliability() <= this.getLowerThreshold())
									+" "+(this.getMyAgent().getMyCurrentState().getMyReliability() > this.getHigherThreshold())
									+wastefull+" "+fragile+" "+(wastefull && fragile));
				} else if (full && !wastefull) {
					return AgentStateStatus.Full;
				} else if (empty && !fragile) {
					return AgentStateStatus.Empty;
				} else if (!wastefull && !fragile) {
					return AgentStateStatus.Thrifty;
				} else if (wastefull && !empty) {
					return AgentStateStatus.Wastefull;
				} else if (fragile && !full) {
					return AgentStateStatus.Fragile;
				} else {
					throw new RuntimeException("impossible!");
				}
	}

	/*
	 *
	 *
	 *
	 */

	private Double lowerThreshold = Double.NaN;
	private Double higherThreshold = Double.NaN;

	private Double getLowerThreshold() {
		if (this.lowerThreshold.equals(Double.NaN)) {
			return Double.POSITIVE_INFINITY;
		} else {
			return this.lowerThreshold;
		}
	}

	private Double getHigherThreshold() {
		if (this.higherThreshold.equals(Double.NaN)) {
			return Double.POSITIVE_INFINITY;
		} else {
			return this.higherThreshold;
		}
	}

	void updateThreshold(){
		try {
			final Opinion<ReplicaState> o = ((OpinionService) this.getMyAgent().getMyInformation()).getGlobalOpinion(ReplicaState.class);
			//		System.out.println("updating status opinion is : "+o);
			Double mean, min, max;
			mean = o.getRepresentativeElement().getMyReliability();
			min = o.getMinElement().getMyReliability();
			max = o.getMaxElement().getMyReliability();

			this.lowerThreshold = NegotiationParameters.alpha_low * ((mean + min)/2);
			this.higherThreshold = NegotiationParameters.alpha_high * ((mean + max)/2);
		} catch (final Exception e) {
			this.getMyAgent().signalException("impossible on raisonne sur son propre ��tat il doit etre au moins pr��sent!\n"+this.getMyAgent().getMyInformation(), e);
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