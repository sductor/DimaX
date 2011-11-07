package negotiation.faulttolerance.candidaturenegotiation.mirrordestruction;

import java.util.ArrayList;
import java.util.Collection;

import dima.basicagentcomponents.AgentIdentifier;
import dima.introspectionbasedagents.annotations.ProactivityInitialisation;
import dima.introspectionbasedagents.services.core.loggingactivity.LogException;
import negotiation.faulttolerance.ReplicationCandidature;
import negotiation.faulttolerance.experimentation.ReplicationSocialOptimisation;
import negotiation.faulttolerance.negotiatingagent.ReplicaState;
import negotiation.faulttolerance.negotiatingagent.ReplicaCore;

public class CandidatureReplicaCoreWithDestruction extends ReplicaCore {
	private static final long serialVersionUID = 1735540071994141334L;

	private final ReplicationSocialOptimisation myOptimiser;

	public CandidatureReplicaCoreWithDestruction(String socialWelfare) {
		myOptimiser = new ReplicationSocialOptimisation(this, socialWelfare);
	}

	/**
	 *
	 */
	//	public Double minKnownReliability = Double.NEGATIVE_INFINITY;

	// public CandidatureReplicaCoreWithMinInfo(
	// final SimpleRationalAgent<ReplicaState, ReplicationCandidature,
	// ReplicationSpecification> ag) {
	// super(ag);
	// }

	/**
	 * Compare either a set of creation candidature, or a set of destruction
	 * candidature, Si creation : compare c1 et c2 par la disponiblité
	 * résultante, Si destruction compare par load diameter : A REVOIR...
	 */
	@Override
	public int getAllocationPreference(final ReplicaState s,
			final Collection<ReplicationCandidature> c1,
			final Collection<ReplicationCandidature> c2) {
		Boolean creation = true;
		Collection<ReplicationCandidature> destructionContract=
				new ArrayList<ReplicationCandidature>();
		for (final ReplicationCandidature c : c1){
			if (c instanceof ReplicationDestructionCandidature){
				creation=false;
				destructionContract.add(((ReplicationDestructionCandidature) c).getMinContract());
			}
		}
		c1.addAll(destructionContract);
		destructionContract.clear();
		for (final ReplicationCandidature c : c2){
			if (c instanceof ReplicationDestructionCandidature){
				creation=false;
				destructionContract.add(((ReplicationDestructionCandidature) c).getMinContract());
			}
		}
		c2.addAll(destructionContract);
		destructionContract.clear();

		if (creation)
			return super.getAllocationPreference(s, c1, c2);
		else{
			try {
				int pref = myOptimiser.getSocialPreference(c1, c2);
				logMonologue("Preference : "+pref+" for \n "+c1+"\n"+c2, ReplicationSocialOptimisation.log_socialWelfareOrdering);
				return pref;
			}catch(RuntimeException e){
				String log = "pref of \n"+c1+"\n"+c2;
				for (ReplicationCandidature r : c1)
					for (AgentIdentifier id : r.getAllParticipants())
						log+="\n spec of "+id+" : "+r.getSpecificationOf(id);
							getMyAgent().signalException(log);
							throw e;
			}
		}
	}



	//	@Override
	//	public int getAllocationPreference(final ReplicaState s,
	//			final Collection<ReplicationCandidature> c1,
	//			final Collection<ReplicationCandidature> c2) {
	//		// throw new RuntimeException("a revoir");
	//
	//		Boolean creation = null;
	//		for (final ReplicationCandidature c : c1)
	//			if (creation == null)
	//				creation = c.isMatchingCreation();
	//			else if (creation != c.isMatchingCreation())
	//				throw new RuntimeException(
	//						"agent can not compare a mix of creation and destruction: check greedy selection class");
	//		for (final ReplicationCandidature c : c2)
	//			if (creation == null)
	//				creation = c.isMatchingCreation();
	//			else if (creation != c.isMatchingCreation())
	//				throw new RuntimeException(
	//						"agent can not compare a mix of creation and destruction: check greedy selection class");
	//
	//		final ReplicaState s1 = this.getMyAgent().getMyResultingState(s, c1);
	//		final ReplicaState s2 = this.getMyAgent().getMyResultingState(s, c2);
	//		Double r1, r2;
	//		r1 = s1.getMyReliability();
	//		r2 = s2.getMyReliability();
	//
	//		if (creation)
	//			return r1.compareTo(r2);
	//		else {
	//			final Double minKnowRelia = this.minKnownReliability;
	//			if (r1 <= minKnowRelia || r2 <= minKnowRelia)
	//				return r1.compareTo(r2);// =>
	//										// getAllocationReliabilityPreference(s2,
	//										// c1, c2);
	//			else
	//				// (r1 > minKnowRelia && r2 > minKnowRelia)
	//				return this.getAllocationLoadPreference(s, c1, c2);
	//
	//		}
	//	}

	//	@Override
	//	public Boolean respectRights(final ReplicaState s) {
	//		final Double minKnowRelia = this.minKnownReliability;
	//		// iStayRelia <=> (getMyCurrentState().getMyReliability()>minKnowRelia)
	//		// => (s.getMyReliability()>minKnowRelia):
	//		if (this.getMyAgent().getMyCurrentState().getMyReliability() < minKnowRelia)
	//			throw new RuntimeException();
	//		final boolean iStayRelia = !(this.getMyAgent().getMyCurrentState()
	//				.getMyReliability() > minKnowRelia)
	//				|| s.getMyReliability() > minKnowRelia;
	//		return super.respectRights(s) && iStayRelia;
	//	}

	//	public void setMinKnowRelia(final ContractTrunk<ReplicationCandidature> cs) {
	//		this.minKnownReliability = Double.POSITIVE_INFINITY;
	//
	//		for (final ReplicationCandidature cyo : cs.getAllContracts())
	//			if (cyo instanceof ReplicationCandidatureWithMinInfo) {
	//				final ReplicationCandidatureWithMinInfo c = (ReplicationCandidatureWithMinInfo) cyo;
	//				this.minKnownReliability = Math.min(this.minKnownReliability,
	//						c.getMinHostedReliability());
	//			} else
	//				throw new RuntimeException("problem with " + cyo);
	//
	//		if (this.minKnownReliability == Double.POSITIVE_INFINITY)
	//			this.minKnownReliability = Double.NEGATIVE_INFINITY;
	//	}

}
