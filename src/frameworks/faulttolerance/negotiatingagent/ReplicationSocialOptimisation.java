package frameworks.faulttolerance.negotiatingagent;

import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;

import frameworks.negotiation.contracts.AbstractContractTransition.IncompleteContractException;
import frameworks.negotiation.rationality.AgentState;
import frameworks.negotiation.rationality.SocialChoiceFunction;


public class ReplicationSocialOptimisation
extends SocialChoiceFunction<ReplicationCandidature>{



	/**
	 *
	 */
	private static final long serialVersionUID = 187944742500004532L;

	public ReplicationSocialOptimisation(
			//			final CompetentComponent myAgent,
			final SocialChoiceType socialWelfare) {
		super(//myAgent,
				socialWelfare);
	}

	/*
	 *
	 */

	public static Double getReliability(final double utility, final double rights, final SocialChoiceType socialWelfare) {
		if (socialWelfare.equals(SocialChoiceType.Leximin)){
			assert utility / rights < 10;
			return utility / rights;
		} else if (socialWelfare.equals(SocialChoiceType.Utility)){
			return utility * rights;
		} else if (socialWelfare.equals(SocialChoiceType.Nash)){
			return utility;
		} else {
			throw new RuntimeException();
		}
	}


	/**
	 * Filter to remove the host states in the social computation
	 * @throws IncompleteContractException
	 */
	@Override
	protected <State extends AgentState> Collection<State> cleanStates(
			final Collection<State> res) {

		final Iterator<State> itState = res.iterator();
		while (itState.hasNext()) {
			final State s = itState.next();
			assert s instanceof ReplicaState || s instanceof HostState:s;
			if (!(s instanceof ReplicaState)) {
				itState.remove();
			}
		}
		assert this.cleanStateVerif(res);
		return res;
	}

	private boolean cleanStateVerif(final Collection<? extends AgentState> res){
		final Iterator<? extends AgentState> itState = res.iterator();
		while (itState.hasNext()) {
			final AgentState s = itState.next();
			assert s instanceof ReplicaState || s instanceof HostState:s;
		}
		return true;
	}

	@Override
	public Comparator<ReplicaState> getComparator() {
		return new Comparator<ReplicaState>() {
			@Override
			public int compare(final ReplicaState r1,
					final ReplicaState r2) {
				final ReplicaState o1 = r1;
				final ReplicaState o2 = r2;

				return Double.compare(
						o1.getMyReliability(),
						o2.getMyReliability());
			}
		};
	}

	@Override
	public UtilitaristEvaluator<ReplicaState> getUtilitaristEvaluator() {
		return new UtilitaristEvaluator<ReplicaState>() {
			@Override
			public Double getUtilityValue(final ReplicaState o) {
				final ReplicaState s = o;
				return s.getMyReliability();

				//				if (o instanceof ReplicaState){
				//				} else
				//					throw new RuntimeException("wtf!");
				//				else if (o instanceof HostState){
				//					assert 1<0;
				//					final HostState s = (HostState) o;
				//					final Collection<ReplicaState> states = s.getMyAgentsCollec();
				//					Double result;
				//					if (ReplicationSocialOptimisation.this.socialWelfare.equals(SocialChoiceFunctions.key4leximinSocialWelfare)){
				//						result = Double.POSITIVE_INFINITY;
				//						for (final ReplicaState r : states)
				//							result = Math.min(result, r.getMyReliability());
				//					} else if  (ReplicationSocialOptimisation.this.socialWelfare.equals(SocialChoiceFunctions.key4NashSocialWelfare)){
				//						result = 1.;
				//						for (final ReplicaState r : states)
				//							result *= r.getMyReliability();
				//					}else if  (ReplicationSocialOptimisation.this.socialWelfare.equals(SocialChoiceFunctions.key4UtilitaristSocialWelfare)){
				//						result = 0.;
				//						for (final ReplicaState r : states)
				//							result += r.getMyReliability();
				//					} else
				//						throw new RuntimeException("wtf! socialWelfare="+ReplicationSocialOptimisation.this.socialWelfare);
				//								return result;
				//				} else
				//					throw new RuntimeException("wtf!");
			}
		};
	}

	//	private boolean cleanStateVerif(Collection<? extends AgentState> res){
	//		final Iterator<? extends AgentState> itState = res.iterator();
	//		while (itState.hasNext()) {
	//			AgentState s = itState.next();
	//			assert s instanceof ReplicaState || s instanceof HostState:s;
	//		}
	//		return true;
	//	}
}
