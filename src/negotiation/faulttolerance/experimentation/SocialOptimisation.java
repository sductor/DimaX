package negotiation.faulttolerance.experimentation;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;

import negotiation.SocialWelfares;
import negotiation.SocialWelfares.UtilitaristEvaluator;
import negotiation.faulttolerance.ReplicationCandidature;
import negotiation.faulttolerance.ReplicationSpecification;
import negotiation.faulttolerance.negotiatingagent.ReplicaState;
import negotiation.negotiationframework.interaction.Allocation;

public class SocialOptimisation {

	public static Double getReliability(Double dispo, Double criti) {
		if (dispo / criti > 10)
			System.out.println("aargh " + dispo + " " + criti);
		return dispo / criti;
	}

	public static int getSocialPreference(String socialWelfare,
			final Collection<ReplicationCandidature> c1,
			final Collection<ReplicationCandidature> c2) {



		Collection<ReplicationSpecification> temp1 = 
				Allocation.getResultingAllocation(Allocation.getInitialStates(c1, c2), c1);
		Collection<ReplicationSpecification> temp2 = 
				Allocation.getResultingAllocation(Allocation.getInitialStates(c1, c2), c2);

		Collection<ReplicaState> s1 = new ArrayList<ReplicaState>();
		Collection<ReplicaState> s2 = new ArrayList<ReplicaState>();

		for (ReplicationSpecification s : temp1){
			if (s instanceof ReplicaState)
				s1.add((ReplicaState) s);
		}
		for (ReplicationSpecification s : temp2){
			if (s instanceof ReplicaState)
				s2.add((ReplicaState) s);
		}



		if (socialWelfare.equals(ReplicationExperimentationParameters.key4leximinSocialWelfare)){
			final Comparator<ReplicaState> c = new Comparator<ReplicaState>() {
				@Override
				public int compare(final ReplicaState o1,
						final ReplicaState o2) {
					Double 
					init1 = o1.getMyReliability(),
					init2 = o1.getMyReliability();

					if (o1.equals(o2)) {
						return Double.compare(
								o1.getMyReliability(), 
								o2.getMyReliability());
					} else
						return Double.compare(init1, init2);
				}
			};
			return SocialWelfares.leximinWelfare(s1, s2, c);
		} else {			
			final UtilitaristEvaluator<ReplicaState> utility = 
					new UtilitaristEvaluator<ReplicaState>() {
				@Override
				public Double getUtilityValue(ReplicaState s) {
					return s.getMyReliability();
				}
			};


			if (socialWelfare.equals(ReplicationExperimentationParameters.key4NashSocialWelfare)){
				return SocialWelfares.nashWelfare(s1, s2, utility);
			} else if (socialWelfare.equals(ReplicationExperimentationParameters.key4UtilitaristSocialWelfare)){
				return SocialWelfares.utilitaristWelfare(s1, s2, utility);
			} else {
				throw new RuntimeException("impossible key for social welfare is : "+socialWelfare);
			}

		}
	}
}
