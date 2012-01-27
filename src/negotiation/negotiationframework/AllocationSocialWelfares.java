package negotiation.negotiationframework;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import negotiation.negotiationframework.interaction.AbstractActionSpecification;
import negotiation.negotiationframework.interaction.AbstractContractTransition;
import dima.basicagentcomponents.AgentIdentifier;
import dima.introspectionbasedagents.CompetentComponent;
import dima.support.GimaObject;

public abstract class AllocationSocialWelfares<
ActionSpec extends AbstractActionSpecification,
Contract extends AbstractContractTransition<ActionSpec>> extends GimaObject{

	/**
	 *
	 */
	private static final long serialVersionUID = 5135268337671313960L;
	public final  String socialWelfare;
	public final CompetentComponent myAgent;

	/*
	 *
	 */
	//Optimisations
	public final static String key4leximinSocialWelfare="leximin";
	public final static String key4NashSocialWelfare="nash";
	public final static String key4UtilitaristSocialWelfare="utilitarist";

	public final static String log_socialWelfareOrdering="social welfare ordering";


	//
	//
	//

	public AllocationSocialWelfares(final CompetentComponent myAgent, final String socialWelfare){
		this.socialWelfare=socialWelfare;
		this.myAgent = myAgent;
	}

	//
	//
	//

	public int getSocialPreference(
			final Collection<Contract> c1,
			final Collection<Contract> c2) {


		final Collection<ActionSpec> temp1 =
				this.getResultingAllocation(this.getInitialStates(c1, c2), c1);
		final Collection<ActionSpec> temp2 =
				this.getResultingAllocation(this.getInitialStates(c1, c2), c2);

		final Collection<ActionSpec> s1 = new ArrayList<ActionSpec>();
		final Collection<ActionSpec> s2 = new ArrayList<ActionSpec>();

		for (final ActionSpec s : temp1)
			s1.add(s);
				for (final ActionSpec s : temp2)
					s2.add(s);



						if (this.socialWelfare.equals(AllocationSocialWelfares.key4leximinSocialWelfare)){
							this.myAgent.logMonologue("comparing : \n"+c1+"\n"+c2+"\n"+s1+"\n"+s2,AllocationSocialWelfares.log_socialWelfareOrdering);
							final int pref = this.leximinWelfare(s1, s2, this.getComparator());
							this.myAgent.logMonologue("result is " +pref,AllocationSocialWelfares.log_socialWelfareOrdering);
							return pref;
						} else if (this.socialWelfare.equals(AllocationSocialWelfares.key4NashSocialWelfare))
							return this.nashWelfare(s1, s2, this.getUtilitaristEvaluator());
						else if (this.socialWelfare.equals(AllocationSocialWelfares.key4UtilitaristSocialWelfare))
							return this.utilitaristWelfare(s1, s2, this.getUtilitaristEvaluator());
						else
							throw new RuntimeException("impossible key for social welfare is : "+this.socialWelfare);
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
			final Collection<Contract> a1,
			final Collection<Contract> a2){
		final Map<AgentIdentifier, ActionSpec> result = new HashMap<AgentIdentifier, ActionSpec>();
		final Collection<Contract> allContract = new ArrayList<Contract>();
		allContract.addAll(a1);
		allContract.addAll(a2);

		for (final Contract c : allContract)
			for (final AgentIdentifier id : c.getAllParticipants())
				if (result.containsKey(id)){
					if (c.getSpecificationOf(id).isNewerThan(result.get(id))>1)
						//						System.out.println("remplacing a fresher state");
						result.put(id,c.getSpecificationOf(id));
				} else
					result.put(id,c.getSpecificationOf(id));

		//updating each contract with the freshest state
		for (final Contract cOld : allContract)
			for (final AgentIdentifier id : cOld.getAllParticipants())
				cOld.setSpecification(result.get(id));
					return result;
	}


	protected Collection<ActionSpec> getResultingAllocation(
			final Map<AgentIdentifier, ActionSpec> initialStates,
			final Collection<Contract> alloc){
		final Map<AgentIdentifier, ActionSpec> meAsMap =
				new HashMap<AgentIdentifier, ActionSpec>();
		meAsMap.putAll(initialStates);

		for (final Contract c : alloc)
			for (final AgentIdentifier id : c.getAllParticipants())
				meAsMap.put(id, c.computeResultingState(meAsMap.get(id)));

					return meAsMap.values();
	}

	//
	// Social Welfare
	//


	public  <State> int minWelfare(
			final Collection<State> a1,
			final Collection<State> a2,
			final Comparator<State> comp){
		return comp.compare(Collections.min(a1,comp),Collections.min(a2,comp));
	}

	public  <State> int leximinWelfare(
			final Collection<State> a1,
			final Collection<State> a2,
			final Comparator<State> comp){

		final LinkedList<State> alloc1 =
				new LinkedList<State>();
		final LinkedList<State> alloc2 =
				new LinkedList<State>();

		alloc1.addAll(a1);
		alloc2.addAll(a2);

		Collections.sort(alloc1, comp);
		Collections.sort(alloc2, comp);
		this.myAgent.logMonologue("s1 is "+alloc1+"\n s2 is "+alloc2,AllocationSocialWelfares.log_socialWelfareOrdering );

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
			final Collection<State> a1,
			final Collection<State> a2,
			final UtilitaristEvaluator<State> u){
		Double nash1 = 0.,nash2 = 0.;
		for (final State a : a1)
			nash1+=u.getUtilityValue(a);
				for (final State a : a2)
					nash2+=u.getUtilityValue(a);
						return nash1.compareTo(nash2);
	}

	public  <State> int nashWelfare(
			final Collection<State> a1,
			final Collection<State> a2,
			final UtilitaristEvaluator<State> u){
		Double nash1 = 1.,nash2 = 1.;
		for (final State a : a1)
			nash1*=u.getUtilityValue(a);
				for (final State a : a2)
					nash2*=u.getUtilityValue(a);
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