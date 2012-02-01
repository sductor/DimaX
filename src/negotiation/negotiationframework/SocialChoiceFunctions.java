package negotiation.negotiationframework;

import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;


public class SocialChoiceFunctions {

	//Optimisations
	public final static String key4leximinSocialWelfare="leximin";
	public final static String key4NashSocialWelfare="nash";
	public final static String key4UtilitaristSocialWelfare="utilitarist";

	
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
			if (comp.compare(minc1,minc2)!=0)
				return comp.compare(minc1,minc2);
		}

		//		if (alloc1.isEmpty() && alloc2.isEmpty()) -> géré par l'assert
		return 0;
	}

	public static <State> int utilitaristWelfare(
			final Collection<State> a1,
			final Collection<State> a2,
			final UtilitaristEvaluator<State> u){
		Double sum1 = 0.,sum2 = 0.;
		for (final State a : a1){
			sum1+=u.getUtilityValue(a);
		}
		for (final State a : a2){
			sum2+=u.getUtilityValue(a);
		}
		return sum1.compareTo(sum2);
	}

	public static  <State> int nashWelfare(
			final Collection<State> a1,
			final Collection<State> a2,
			final UtilitaristEvaluator<State> u){
		Double nash1 = 1.,nash2 = 1.;
		for (final State a : a1){
			nash1*=u.getUtilityValue(a);
		}
		for (final State a : a2){
			nash2*=u.getUtilityValue(a);
		}
		return nash1.compareTo(nash2);
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
	
	//
	// Subclasses
	//
	
	public interface UtilitaristEvaluator<State> {
		public Double getUtilityValue(State s);
	}
}
