package negotiation.negotiationframework;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.Map;

import negotiation.faulttolerance.experimentation.ReplicationSocialOptimisation;
import negotiation.negotiationframework.interaction.AbstractActionSpecification;
import negotiation.negotiationframework.interaction.AbstractContractTransition;
import negotiation.negotiationframework.interaction.ContractTransition;

import dima.basicagentcomponents.AgentIdentifier;
import dima.introspectionbasedagents.CompetentComponent;
import dima.introspectionbasedagents.annotations.ProactivityInitialisation;
import dima.support.GimaObject;

public abstract class AllocationSocialWelfares<
ActionSpec extends AbstractActionSpecification,
Contract extends AbstractContractTransition<ActionSpec>> extends GimaObject{

	public final  String socialWelfare;
	public final CompetentComponent myAgent;

	/*
	 * 
	 */

	public final static String log_socialWelfareOrdering="social welfare ordering";

	public final static String key4leximinSocialWelfare="leximin";
	public final static String key4NashSocialWelfare="nash";
	public final static String key4UtilitaristSocialWelfare="utilitarist";

	//
	//
	//

	public AllocationSocialWelfares(CompetentComponent myAgent, String socialWelfare){
		this.socialWelfare=socialWelfare;
		this.myAgent = myAgent;
	}

	//
	//
	//

	public int getSocialPreference(
			final Collection<Contract> c1,
			final Collection<Contract> c2) {


		Collection<ActionSpec> temp1 = 
				getResultingAllocation(getInitialStates(c1, c2), c1);
		Collection<ActionSpec> temp2 = 
				getResultingAllocation(getInitialStates(c1, c2), c2);

		Collection<ActionSpec> s1 = new ArrayList<ActionSpec>();
		Collection<ActionSpec> s2 = new ArrayList<ActionSpec>();

		for (ActionSpec s : temp1){
			s1.add(s);
		}
		for (ActionSpec s : temp2){
			s2.add(s);
		}



		if (socialWelfare.equals(key4leximinSocialWelfare)){
			myAgent.logMonologue("comparing : \n"+c1+"\n"+c2+"\n"+s1+"\n"+s2,log_socialWelfareOrdering);
			int pref = leximinWelfare(s1, s2, getComparator());
			myAgent.logMonologue("result is " +pref,log_socialWelfareOrdering);
			return pref;
		} else if (socialWelfare.equals(key4NashSocialWelfare)){
			return nashWelfare(s1, s2, getUtilitaristEvaluator());
		} else if (socialWelfare.equals(key4UtilitaristSocialWelfare)){
			return utilitaristWelfare(s1, s2, getUtilitaristEvaluator());
		} else {
			throw new RuntimeException("impossible key for social welfare is : "+socialWelfare);
		}
	}

	//
	// Abstract Method 
	//

	public abstract Comparator<ActionSpec> getComparator();

	public abstract UtilitaristEvaluator<ActionSpec> getUtilitaristEvaluator();

	//
	// Primitives
	//


	private	Map<AgentIdentifier, ActionSpec> getInitialStates(
			Collection<Contract> a1,
			Collection<Contract> a2){
		Map<AgentIdentifier, ActionSpec> result = new HashMap<AgentIdentifier, ActionSpec>();
		Collection<Contract> allContract = new ArrayList<Contract>();
		allContract.addAll(a1);
		allContract.addAll(a2);

		for (Contract c : allContract){
			for (AgentIdentifier id : c.getAllParticipants())
				if (result.containsKey(id)){
					if (c.getSpecificationOf(id).isNewerThan(result.get(id))){//rmplacing a fresher state	

						//						System.out.println("remplacing a fresher state");
						result.put(id,c.getSpecificationOf(id));					
					}
				} else {//adding state not present in result
					result.put(id,c.getSpecificationOf(id));
				}
		}		

		//updating each contract with the freshest state
		for (Contract cOld : allContract){
			for (AgentIdentifier id : cOld.getAllParticipants()){
					cOld.setSpecification(result.get(id));
			}
		}	
		return result;			
	}


	protected Collection<ActionSpec> getResultingAllocation(
			Map<AgentIdentifier, ActionSpec> initialStates,
			Collection<Contract> alloc){
		Map<AgentIdentifier, ActionSpec> meAsMap =
				new HashMap<AgentIdentifier, ActionSpec>();
		meAsMap.putAll(initialStates);

		for (Contract c : alloc){
			for (AgentIdentifier id : c.getAllParticipants())
				meAsMap.put(id, c.computeResultingState(meAsMap.get(id)));
		}

		return meAsMap.values();		
	}

	//
	// Social Welfare
	//


	public  <State> int minWelfare(
			Collection<State> a1,
			Collection<State> a2,
			Comparator<State> comp){
		return comp.compare(Collections.min(a1,comp),Collections.min(a2,comp));
	}

	public  <State> int leximinWelfare(
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
		myAgent.logMonologue("s1 is "+alloc1+"\n s2 is "+alloc2,log_socialWelfareOrdering );

		while (!alloc1.isEmpty() && !alloc2.isEmpty()) {
			final State minc1 = 
					alloc1.pop(); 
			final State 	minc2 = 
					alloc2.pop();
			if (comp.compare(minc1,minc2)!=0)
				return comp.compare(minc1,minc2);
		}

		if (alloc1.isEmpty() && alloc2.isEmpty())
			return 0;
		else 
			throw new RuntimeException("the allocs did not have the same size!!\n"+a1+"\n"+a2);
	}

	/*
	 * 
	 */


	public interface UtilitaristEvaluator<State> {
		public Double getUtilityValue(State s);
	}

	public  <State> int utilitaristWelfare(
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

	public  <State> int nashWelfare(
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
	//
	//

}






//try {
//	meAsMap.put(id, c.computeResultingState(meAsMap.get(id)));
//} catch (RuntimeException e) {
//	//					System.err.println("yyyyyyyyoooooooooooooo "+id+" "+c+" \n **** all alloc : "+alloc
//	//							+"\n **** current result : "+meAsMap+" ------ "+c.getAllParticipants());
//	getResultingAllocationFACTIS(initialStates,alloc);
//	throw e;
//} -->//public static <ActionSpec extends AbstractActionSpecification,Contract extends AbstractContractTransition<ActionSpec>>
//Collection<ActionSpec> getResultingAllocationFACTIS(
//		Map<AgentIdentifier, ActionSpec> initialStates,
//		Collection<Contract> alloc){
//	Map<AgentIdentifier, ActionSpec> meAsMap =
//			new HashMap<AgentIdentifier, ActionSpec>();
//	meAsMap.putAll(initialStates);
//	System.err.println("yyyyyyyyoooooooooooooo ");
//	System.err.flush();
//	System.err.println("\n\n\n\n**********************\n\n");
//	System.err.flush();
//	System.out.println("initial!!! :\n"+meAsMap);
//	System.out.flush();
//	for (Contract c : alloc){
//		System.out.println("\n anlysing : \n *"+c);
//		for (AgentIdentifier id : c.getAllParticipants())
//			try {
//				System.out.flush();
//				System.out.println(" ---> paticipant "+id);
//				System.out.flush();
//				System.out.println(" ---> c spec = "+c.getSpecificationOf(id));
//				System.out.flush();
//				System.out.println("\n initially :"+meAsMap.get(id));
//				System.out.flush();
//				System.out.println("\n finally :"+c.computeResultingState(meAsMap.get(id)));
//				System.out.flush();
//				meAsMap.put(id, c.computeResultingState(meAsMap.get(id)));
//			} catch (RuntimeException e) {
//				//					System.err.println("yyyyyyyyoooooooooooooo "+id+" "+c+" \n **** all alloc : "+alloc
//				//							+"\n **** current result : "+meAsMap+" ------ "+c.getAllParticipants());
//
//				throw e;
//			}
//	}
//
//	System.err.println("\n\n\n\n**********************\n\n");
//	return meAsMap.values();	
//}