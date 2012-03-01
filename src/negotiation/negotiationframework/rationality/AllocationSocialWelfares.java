package negotiation.negotiationframework.rationality;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

import negotiation.negotiationframework.contracts.AbstractActionSpecification;
import negotiation.negotiationframework.contracts.AbstractContractTransition;
import negotiation.negotiationframework.rationality.SocialChoiceFunctions.UtilitaristEvaluator;
import dima.basicagentcomponents.AgentIdentifier;
import dima.support.GimaObject;

public abstract class AllocationSocialWelfares<
ActionSpec extends AbstractActionSpecification,
Contract extends AbstractContractTransition<ActionSpec>> extends GimaObject{
	private static final long serialVersionUID = 5135268337671313960L;

	public final  String socialWelfare;
	//	public final CompetentComponent myAgent;

	public final static String log_socialWelfareOrdering="social welfare ordering";

	//
	//
	//

	public AllocationSocialWelfares(
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

						if (this.socialWelfare.equals(SocialChoiceFunctions.key4leximinSocialWelfare)){
							//			this.myAgent.logMonologue("comparing : \n"+c1+"\n"+c2+"\n"+s1+"\n"+s2,AllocationSocialWelfares.log_socialWelfareOrdering);
							final int pref = SocialChoiceFunctions.leximinWelfare(s1, s2, this.getComparator());
							//			this.myAgent.logMonologue("result is " +pref,AllocationSocialWelfares.log_socialWelfareOrdering);
							return pref;
						} else if (this.socialWelfare.equals(SocialChoiceFunctions.key4NashSocialWelfare))
							return SocialChoiceFunctions.nashWelfare(s1, s2, this.getUtilitaristEvaluator());
						else if (this.socialWelfare.equals(SocialChoiceFunctions.key4UtilitaristSocialWelfare))
							return SocialChoiceFunctions.utilitaristWelfare(s1, s2, this.getUtilitaristEvaluator());
						else
							throw new RuntimeException("impossible key for social welfare is : "+this.socialWelfare);
	}


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
		for (final Contract cOld : allContract){
			for (final AgentIdentifier id : cOld.getAllParticipants())
					cOld.setSpecification(result.get(id));
		}
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