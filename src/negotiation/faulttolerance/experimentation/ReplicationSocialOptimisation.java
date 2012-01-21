package negotiation.faulttolerance.experimentation;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Map;

import dima.basicagentcomponents.AgentIdentifier;
import dima.introspectionbasedagents.CompetentComponent;
import dima.introspectionbasedagents.services.core.loggingactivity.LogException;

import negotiation.faulttolerance.ReplicationCandidature;
import negotiation.faulttolerance.ReplicationSpecification;
import negotiation.faulttolerance.negotiatingagent.HostState;
import negotiation.faulttolerance.negotiatingagent.ReplicaState;
import negotiation.negotiationframework.AllocationSocialWelfares;

public class ReplicationSocialOptimisation extends AllocationSocialWelfares<ReplicationSpecification, ReplicationCandidature>{



	public ReplicationSocialOptimisation(CompetentComponent myAgent, String socialWelfare) {
		super(myAgent, socialWelfare);
	}

	/*
	 * 
	 */

	public static Double getReliability(Double dispo, Double criti) {
		if (dispo / criti > 10)
			System.out.println("aargh " + dispo + " " + criti);
		return dispo / criti;
	}


	protected Collection<ReplicationSpecification> getResultingAllocation(
			Map<AgentIdentifier, ReplicationSpecification> initialStates,
			Collection<ReplicationCandidature> alloc){
		Collection<ReplicationSpecification> res = super.getResultingAllocation(initialStates,alloc);
		Iterator<ReplicationSpecification> itState = res.iterator();
		while (itState.hasNext()){
			if (!(itState.next() instanceof ReplicaState))
				itState.remove();
		}
		return res;
	}


	@Override
	public Comparator<ReplicationSpecification> getComparator() {
		return new Comparator<ReplicationSpecification>() {
			@Override
			public int compare(final ReplicationSpecification r1,
					final ReplicationSpecification r2) {
				ReplicaState o1 = (ReplicaState) r1;
				ReplicaState o2 = (ReplicaState) r2;

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
			public Double getUtilityValue(ReplicationSpecification o) {
				if (o instanceof ReplicaState){
					ReplicaState s = (ReplicaState) o;
					return s.getMyReliability();
				} else if (o instanceof HostState){
					HostState s = (HostState) o;
					Collection<ReplicaState> states = s.getMyAgentsCollec();
					Double result;
					if (socialWelfare.equals(AllocationSocialWelfares.key4leximinSocialWelfare)){
						result = Double.POSITIVE_INFINITY;
						for (ReplicaState r : states)
							result = Math.min(result, r.getMyReliability());
					} else if  (socialWelfare.equals(AllocationSocialWelfares.key4NashSocialWelfare)){
						result = 1.;
						for (ReplicaState r : states)
							result *= r.getMyReliability();						
					}else if  (socialWelfare.equals(AllocationSocialWelfares.key4UtilitaristSocialWelfare)){
						result = 0.;
						for (ReplicaState r : states)
							result += r.getMyReliability();						
					} else 
						throw new RuntimeException("wtf! socialWelfare="+socialWelfare);
					return result;
				} else 
					throw new RuntimeException("wtf!");
			}
		};
	}

}
