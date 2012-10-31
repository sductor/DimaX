package frameworks.faulttolerance.olddcop;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import dima.basicagentcomponents.AgentIdentifier;
import dima.introspectionbasedagents.modules.distribution.NormalLaw.DispersionSymbolicValue;
import frameworks.experimentation.IfailedException;
import frameworks.faulttolerance.experimentation.ReplicationInstanceGraph;
import frameworks.faulttolerance.olddcop.dcop.CPUFreeConstraint;
import frameworks.faulttolerance.olddcop.dcop.DcopReplicationGraph;
import frameworks.faulttolerance.olddcop.dcop.MemFreeConstraint;
import frameworks.faulttolerance.olddcop.dcop.ReplicationVariable;
import frameworks.faulttolerance.solver.SolverFactory;
import frameworks.faulttolerance.solver.SolverFactory.SolverType;
import frameworks.negotiation.rationality.AgentState;
import frameworks.negotiation.rationality.SocialChoiceFunction.SocialChoiceType;

public class DCOPFactory {

	private static String simulationName;

	public enum VariableType {MemoryConsumming, CPUConsumming}; 
	private static VariableType dcoptype;

	//
	//
	//

	public static void setParameters(
			VariableType dcoptype,
			String simulationName){
		DCOPFactory.simulationName=simulationName;
		DCOPFactory.dcoptype=dcoptype;
	}

	public static boolean isClassical(){
		return DCOPFactory.getDcoptype()==VariableType.MemoryConsumming;
	}


	public static VariableType getDcoptype() {
		return dcoptype;
	}

	public static String[] getArgs() {
//		SolverFactory.setParameters(SolverType.Dpop);//Knitro);//BranchNBound);//Choco);//
		DCOPFactory.setParameters(
				VariableType.CPUConsumming, //.MemoryConsumming, //
				"yo");
		return new String[]{"conf/1.dcop","","5","500"};
	}

	public static DcopReplicationGraph constructDCOPGraph(String filename) {

		ReplicationInstanceGraph rig = new ReplicationInstanceGraph(SocialChoiceType.Utility);

		try {
			rig.randomInitiaition(
					simulationName, 566668,
					5, 3,//nbAgent,nbHost
					0.5, DispersionSymbolicValue.Moyen, //criticity
					0.25, DispersionSymbolicValue.Nul, //agent load
					1., DispersionSymbolicValue.Nul, //hostCap
					0.5, DispersionSymbolicValue.Moyen, //hostDisp
					5,4);
		} catch (IfailedException e1) {
			e1.printStackTrace();
		}
		//		System.out.println(rig);
		return constructDCOPGraph(rig);
	}

	//
	//
	//

	public static DcopReplicationGraph constructDCOPGraph(SocialChoiceType sct){
		//		if (isClassical())
		//			return new DcopCPUFreeGraph();
		//		else
		return new DcopReplicationGraph(sct);

	}

	public static DcopReplicationGraph constructDCOPGraph(final ReplicationInstanceGraph rig) {

		//		if (isClassical())
		//			return new DcopCPUFreeGraph(rig);
		//		else
		DcopReplicationGraph drg = new DcopReplicationGraph(rig);
		System.out.println("\n\n graph is :-------------------------------------- \n"+drg.toString());
		return drg;
	}


	/*
	 * 
	 */

	public  static MemFreeConstraint constructConstraint(final ReplicationVariable a, final ReplicationVariable b){
		if (DCOPFactory.getDcoptype()==VariableType.MemoryConsumming)
			return new CPUFreeConstraint(a, b);
		else
			return new MemFreeConstraint(a, b);
	}


	public  static ReplicationVariable constructVariable(final int i, final int d,AgentState s,  final SocialChoiceType g){
//		assert s!=null;
		return new ReplicationVariable(i,d,s, g);
	}

	/*
	 * 
	 */

	/*
	 * 
	 */

	public static  double evaluate(DcopReplicationGraph drg) {
		HashMap<Integer,Integer> analysedAgents=new HashMap<Integer,Integer>();		
		double result ;	
		switch (drg.getSocialWelfare()){
		case Utility :			
			result = 0;
			break;
		case Nash :
			result  = 1;
			break;
		case Leximin :
			result = Double.POSITIVE_INFINITY;
			break;
		default :
			throw new RuntimeException();
		}

		for (MemFreeConstraint c : drg.conList){
			if (c.evaluate()==Double.NEGATIVE_INFINITY)
				return Double.NEGATIVE_INFINITY;
			else {
				if (!analysedAgents.keySet().contains(c.getAgent().id)){
					analysedAgents.put(c.getAgent().id,c.getAgent().getValue());
					switch (drg.getSocialWelfare()){
					case Utility :			
						result += c.evaluate();
						break;
					case Nash :
						result *= c.evaluate();
						break;
					case Leximin :
						result += Math.min(result, c.evaluate());
						break;
					}
				} else {
					assert analysedAgents.get(c.getAgent().id).equals(c.getAgent().getValue());
				}
			}
		}
		return result;
	}




	/*
	 * 
	 */

	public static Integer identifierToInt(AgentIdentifier id){
		return ReplicationInstanceGraph.identifierToInt(id);
	}

	public static AgentIdentifier intToIdentifier(int id){
		return ReplicationInstanceGraph.intToIdentifier(simulationName,  id);
	}

	public static  <T> int subsetToInt(List<? extends T> space, Collection<? extends T> subset){
		StringBuilder v = new StringBuilder();
		
		assert space.containsAll(subset);
		for (int i = 0; i < space.size(); i++){
			if (subset.contains(space.get(i)))
				v.append('1');
			else
				v.append('0');
		}
		return Integer.parseInt(v.toString(), 2);
	}

	public static <T> List<T> intToSubset(List<T> space, int value){
		List<T> subset = new ArrayList<T>();
		String v = Integer.toBinaryString(value);
		//		System.out.println("allocating "+v+" in "+space);
		int pos = v.length()-1;
		for (int i = space.size()-1; i>=0; i--){
			//			System.out.println("i : "+i);
			if (pos<0) 
				break;
			//			System.out.println("pos "+pos+" "+v.charAt(pos));
			if (v.charAt(pos)=='1'){
				subset.add(space.get(i));
				//				System.out.println("pos "+pos+" added");
			}
			pos--;
		}
		return subset;
	}



}

