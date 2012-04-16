package negotiation.negotiationframework.rationality;

import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.Map;

import dima.basicagentcomponents.AgentIdentifier;
import dima.support.GimaObject;

import negotiation.negotiationframework.contracts.AbstractActionSpecification;
import negotiation.negotiationframework.contracts.AbstractContractTransition;
import negotiation.negotiationframework.contracts.AbstractContractTransition.IncompleteContractException;
import negotiation.negotiationframework.contracts.ReallocationContract;


public abstract class SocialChoiceFunction<
ActionSpec extends AbstractActionSpecification,
Contract extends AbstractContractTransition<ActionSpec>> extends GimaObject{
	private static final long serialVersionUID = 5135268337671313960L;
	 

			//Optimisations
			public final static String key4leximinSocialWelfare="leximin";
			public final static String key4NashSocialWelfare="nash";
			public final static String key4UtilitaristSocialWelfare="utilitarist";

	public final  String socialWelfare;
	//	public final CompetentComponent myAgent;

	public final static String log_socialWelfareOrdering="social welfare ordering";

	//
	//
	//

	public SocialChoiceFunction(
			//			final CompetentComponent myAgent,
			final String socialWelfare){
		this.socialWelfare=socialWelfare;
		//		this.myAgent = myAgent;
	}

	//
	// Abstract Method
	//

	public abstract Comparator<ActionSpec> getComparator();

	public abstract UtilitaristEvaluator<ActionSpec> getUtilitaristEvaluator();

	//
	// Methods
	//

	public double getUtility(final Collection<Contract> cs){

		try {
			final Collection<ActionSpec> as = this.cleanStates(ReallocationContract.getResultingAllocation(cs));

			if (this.socialWelfare.equals(SocialChoiceFunction.key4leximinSocialWelfare)) {
				return SocialChoiceFunction.getMinValue(as,  this.getComparator(), this.getUtilitaristEvaluator());
			} else if (this.socialWelfare.equals(SocialChoiceFunction.key4NashSocialWelfare)) {
				return SocialChoiceFunction.getNashValue(as, this.getUtilitaristEvaluator());
			} else if (this.socialWelfare.equals(SocialChoiceFunction.key4UtilitaristSocialWelfare)) {
				return SocialChoiceFunction.getUtilitaristValue(as, this.getUtilitaristEvaluator());
			} else {
				throw new RuntimeException("impossible key for social welfare is : "+this.socialWelfare);
			}

		} catch (final IncompleteContractException e) {
			throw new RuntimeException();
		}
	}

	public int getSocialPreference(
			final Collection<Contract> c1,
			final Collection<Contract> c2) {

		try {
			final Map<AgentIdentifier, ActionSpec> initialStates =ReallocationContract.getInitialStates(c1, c2);

			final Collection<ActionSpec> s1 =
					this.cleanStates(ReallocationContract.getResultingAllocation(initialStates, c1));
			final Collection<ActionSpec> s2 =
					this.cleanStates(ReallocationContract.getResultingAllocation(initialStates, c2));

			assert s1.size()==s2.size();

			if (this.socialWelfare.equals(SocialChoiceFunction.key4leximinSocialWelfare)){
				//			this.myAgent.logMonologue("comparing : \n"+c1+"\n"+c2+"\n"+s1+"\n"+s2,AllocationSocialWelfares.log_socialWelfareOrdering);
				final int pref = SocialChoiceFunction.leximinWelfare(s1, s2, this.getComparator());
				//			this.myAgent.logMonologue("result is " +pref,AllocationSocialWelfares.log_socialWelfareOrdering);
				return pref;
			} else if (this.socialWelfare.equals(SocialChoiceFunction.key4NashSocialWelfare)) {
				return SocialChoiceFunction.nashWelfare(s1, s2, this.getUtilitaristEvaluator());
			} else if (this.socialWelfare.equals(SocialChoiceFunction.key4UtilitaristSocialWelfare)) {
				return SocialChoiceFunction.utilitaristWelfare(s1, s2, this.getUtilitaristEvaluator());
			} else {
				throw new RuntimeException("impossible key for social welfare is : "+this.socialWelfare);
			}
		} catch (final IncompleteContractException e) {
			throw new RuntimeException();
		}
	}

	protected Collection<ActionSpec> cleanStates(
			final Collection<ActionSpec> res) {
		return res;
	}

	//
	// Social Welfare
	//


	public static <State> int minWelfare(
			final Collection<State> a1,
			final Collection<State> a2,
			final Comparator<State> comp){
		return comp.compare(Collections.min(a1,comp),Collections.min(a2,comp));
	}

	public static  <State> int leximinWelfare(
			final Collection<State> a1,
			final Collection<State> a2,
			final Comparator<State> comp){

		assert a1.size()==a2.size();

		final LinkedList<State> alloc1 =
				new LinkedList<State>();
		final LinkedList<State> alloc2 =
				new LinkedList<State>();

		alloc1.addAll(a1);
		alloc2.addAll(a2);

		Collections.sort(alloc1, comp);
		Collections.sort(alloc2, comp);

		while (!alloc1.isEmpty() && !alloc2.isEmpty()) {
			final State minc1 =	alloc1.pop();
			final State minc2 =	alloc2.pop();
			if (comp.compare(minc1,minc2)!=0) {
				return comp.compare(minc1,minc2);
			}
		}

		//		if (alloc1.isEmpty() && alloc2.isEmpty()) -> géré par l'assert
		return 0;
	}

	public static <State> int utilitaristWelfare(
			final Collection<State> a1,
			final Collection<State> a2,
			final UtilitaristEvaluator<State> u){
		return getUtilitaristValue(a1, u).compareTo(getUtilitaristValue(a2, u));
	}

	public static  <State> int nashWelfare(
			final Collection<State> a1,
			final Collection<State> a2,
			final UtilitaristEvaluator<State> u){
		return getNashValue(a1, u).compareTo(getNashValue(a2, u));
	}

	//	public  <State> int minDiameter(
	//			Collection<State> a1,
	//			Collection<State> a2,
	//			UtilitaristEvaluator<State> u){
	//		Double minA1 = 1.,nash2 = 1.;
	//		for (State a : a1){
	//			nash1*=u.getUtilityValue(a);
	//		}
	//		for (State a : a2){
	//			nash2*=u.getUtilityValue(a);
	//		}
	//		return nash1.compareTo(nash2);
	//	}


	/*
	 * 
	 */
	public static  <State> Double getMinValue(
			final Collection<State> as,
			final Comparator<State> comp,
			final UtilitaristEvaluator<State> u){
		return u.getUtilityValue(Collections.min(as,comp));
	}


	public static  <State> Double getNashValue(
			final Collection<State> as,
			final UtilitaristEvaluator<State> u){
		Double nash = 1.;
		for (final State a : as) {
			nash*=u.getUtilityValue(a);
		}
		return nash;
	}

	public static  <State> Double getUtilitaristValue(
			final Collection<State> as,
			final UtilitaristEvaluator<State> u){
		Double sum = 1.;
		for (final State a : as) {
			sum+=u.getUtilityValue(a);
		}
		return sum;
	}


	//
	// Subclasses
	//

	public interface UtilitaristEvaluator<State> {
		public Double getUtilityValue(State s);
	}
}
