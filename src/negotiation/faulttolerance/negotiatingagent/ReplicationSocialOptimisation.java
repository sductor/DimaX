package negotiation.faulttolerance.negotiatingagent;

import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Map;

import negotiation.negotiationframework.AllocationSocialWelfares;
import negotiation.negotiationframework.SocialChoiceFunctions;
import negotiation.negotiationframework.SocialChoiceFunctions.UtilitaristEvaluator;
import dima.basicagentcomponents.AgentIdentifier;
import dima.introspectionbasedagents.CompetentComponent;

public class ReplicationSocialOptimisation extends AllocationSocialWelfares<ReplicationSpecification, ReplicationCandidature>{



	/**
	 *
	 */
	private static final long serialVersionUID = 187944742500004532L;

	public ReplicationSocialOptimisation(final CompetentComponent myAgent, final String socialWelfare) {
		super(myAgent, socialWelfare);
	}

	/*
	 *
	 */

	public static Double getReliability(final Double dispo, final Double criti) {
		if (dispo / criti > 10)
			System.out.println("aargh " + dispo + " " + criti);
		return dispo / criti;
	}


	/**
	 * Filter to remove the host states in the social computation
	 */
	@Override
	protected Collection<ReplicationSpecification> getResultingAllocation(
			final Map<AgentIdentifier, ReplicationSpecification> initialStates,
			final Collection<ReplicationCandidature> alloc){
		final Collection<ReplicationSpecification> res = super.getResultingAllocation(initialStates,alloc);
		final Iterator<ReplicationSpecification> itState = res.iterator();
		while (itState.hasNext())
			if (!(itState.next() instanceof ReplicaState))
				itState.remove();
		return res;
	}


	@Override
	public Comparator<ReplicationSpecification> getComparator() {
		return new Comparator<ReplicationSpecification>() {
			@Override
			public int compare(final ReplicationSpecification r1,
					final ReplicationSpecification r2) {
				final ReplicaState o1 = (ReplicaState) r1;
				final ReplicaState o2 = (ReplicaState) r2;

				return Double.compare(
						o1.getMyReliability(),
						o2.getMyReliability());
			}
		};
	}

	@Override
	public UtilitaristEvaluator<ReplicationSpecification> getUtilitaristEvaluator() {
		return new UtilitaristEvaluator<ReplicationSpecification>() {
			@Override
			public Double getUtilityValue(final ReplicationSpecification o) {
				if (o instanceof ReplicaState){
					final ReplicaState s = (ReplicaState) o;
					return s.getMyReliability();
				} else if (o instanceof HostState){
					assert 1<0;
					final HostState s = (HostState) o;
					final Collection<ReplicaState> states = s.getMyAgentsCollec();
					Double result;
					if (ReplicationSocialOptimisation.this.socialWelfare.equals(SocialChoiceFunctions.key4leximinSocialWelfare)){
						result = Double.POSITIVE_INFINITY;
						for (final ReplicaState r : states)
							result = Math.min(result, r.getMyReliability());
					} else if  (ReplicationSocialOptimisation.this.socialWelfare.equals(SocialChoiceFunctions.key4NashSocialWelfare)){
						result = 1.;
						for (final ReplicaState r : states)
							result *= r.getMyReliability();
					}else if  (ReplicationSocialOptimisation.this.socialWelfare.equals(SocialChoiceFunctions.key4UtilitaristSocialWelfare)){
						result = 0.;
						for (final ReplicaState r : states)
							result += r.getMyReliability();
					} else
						throw new RuntimeException("wtf! socialWelfare="+ReplicationSocialOptimisation.this.socialWelfare);
								return result;
				} else
					throw new RuntimeException("wtf!");
			}
		};
	}

}
