package negotiation.negotiationframework.interaction;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import dima.basicagentcomponents.AgentIdentifier;

public class Allocation
//<
//ActionSpec extends AbstractActionSpecification,
//Contract extends AbstractContractTransition<ActionSpec>>
//extends HashSet<Contract>
{

	public static <ActionSpec extends AbstractActionSpecification,Contract extends AbstractContractTransition<ActionSpec>>
	Map<AgentIdentifier, ActionSpec> getInitialStates(
			Collection<Contract> a1,
			Collection<Contract> a2){
		Map<AgentIdentifier, ActionSpec> result = new HashMap<AgentIdentifier, ActionSpec>();
		for (Contract c : a1){
			for (AgentIdentifier id : c.getAllParticipants())
				result.put(id,c.getSpecificationOf(id));
		}
		for (Contract c : a2){
			for (AgentIdentifier id : c.getAllParticipants())
				if (!result.containsKey(id) || c.getSpecificationOf(id).isNewerThan(result.get(id)))
				result.put(id,c.getSpecificationOf(id));
		}
		return result;			
	}

	public static <ActionSpec extends AbstractActionSpecification,Contract extends AbstractContractTransition<ActionSpec>>
	Collection<ActionSpec> getResultingAllocation(
			Map<AgentIdentifier, ActionSpec> initialStates,
			Collection<Contract> alloc){
		Map<AgentIdentifier, ActionSpec> meAsMap =
				new HashMap<AgentIdentifier, ActionSpec>();
		meAsMap.putAll(initialStates);

		for (Contract c : alloc){
			for (AgentIdentifier id : c.getAllParticipants())
				try {
					meAsMap.put(id, c.computeResultingState(meAsMap.get(id)));
				} catch (RuntimeException e) {
//					System.err.println("yyyyyyyyoooooooooooooo "+id+" "+c+" \n **** all alloc : "+alloc
//							+"\n **** current result : "+meAsMap+" ------ "+c.getAllParticipants());
					getResultingAllocationFACTIS(initialStates,alloc);
					throw e;
				}
		}

		return meAsMap.values();		
	}
	
	public static <ActionSpec extends AbstractActionSpecification,Contract extends AbstractContractTransition<ActionSpec>>
	Collection<ActionSpec> getResultingAllocationFACTIS(
			Map<AgentIdentifier, ActionSpec> initialStates,
			Collection<Contract> alloc){
		Map<AgentIdentifier, ActionSpec> meAsMap =
				new HashMap<AgentIdentifier, ActionSpec>();
		meAsMap.putAll(initialStates);
		System.err.println("yyyyyyyyoooooooooooooo ");
		System.err.flush();
		System.err.println("\n\n\n\n**********************\n\n");
		System.err.flush();
		System.out.println("initial!!! :\n"+meAsMap);
		System.out.flush();
		for (Contract c : alloc){
			System.out.println("\n anlysing : \n *"+c);
			for (AgentIdentifier id : c.getAllParticipants())
				try {
					System.out.flush();
					System.out.println(" ---> paticipant "+id);
					System.out.flush();
					System.out.println(" ---> c spec = "+c.getSpecificationOf(id));
					System.out.flush();
					System.out.println("\n initially :"+meAsMap.get(id));
					System.out.flush();
					System.out.println("\n finally :"+c.computeResultingState(meAsMap.get(id)));
					System.out.flush();
					meAsMap.put(id, c.computeResultingState(meAsMap.get(id)));
				} catch (RuntimeException e) {
//					System.err.println("yyyyyyyyoooooooooooooo "+id+" "+c+" \n **** all alloc : "+alloc
//							+"\n **** current result : "+meAsMap+" ------ "+c.getAllParticipants());
					
					throw e;
				}
		}

		System.err.println("\n\n\n\n**********************\n\n");
		return meAsMap.values();	
	}
}
