package negotiation;

import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;


public class SocialWelfares {

	public interface UtilitaristEvaluator<State> {

		public Double getUtilityValue(State s);
	}

	public static  <State> int minWelfare(
			Collection<State> a1,
			Collection<State> a2,
			Comparator<State> comp){
		return comp.compare(Collections.min(a1,comp),Collections.min(a2,comp));
	}

	public static  <State> int leximinWelfare(
			Collection<State> a1,
			Collection<State> a2,
			Comparator<State> comp){

		final LinkedList<State> alloc1 = 
				new LinkedList<State>(); 
		final LinkedList<State> alloc2 = 
				new LinkedList<State>();

		alloc1.addAll(a1);
		alloc2.addAll(a2);

		Collections.sort(alloc1, comp);
		Collections.sort(alloc2, comp);

		while (!alloc1.isEmpty() && !alloc2.isEmpty()) {
			final State minc1 = 
					alloc1.pop(); 
			final State 	minc2 = 
					alloc2.pop();
			if (!minc1.equals(minc2))
				return comp.compare(minc1,minc2);
		}

		if (alloc1.isEmpty() && alloc2.isEmpty())
			return 0;
		else 
			throw new RuntimeException("the allocs did not have the same size!!\n"+a1+"\n"+a2);
	}

	public static  <State> int utilitaristWelfare(
			Collection<State> a1,
			Collection<State> a2,
			UtilitaristEvaluator<State> u){
		Double nash1 = 0.,nash2 = 0.;
		for (State a : a1){
			nash1+=u.getUtilityValue(a);
		}
		for (State a : a2){
			nash2+=u.getUtilityValue(a);
		}
		return nash1.compareTo(nash2);
	}

	public static  <State> int nashWelfare(
			Collection<State> a1,
			Collection<State> a2,
			UtilitaristEvaluator<State> u){
		Double nash1 = 1.,nash2 = 1.;
		for (State a : a1){
			nash1*=u.getUtilityValue(a);
		}
		for (State a : a2){
			nash2*=u.getUtilityValue(a);
		}
		return nash1.compareTo(nash2);
	}
	
//	public static  <State> int minDiameter(
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
	//
	//
	
}
