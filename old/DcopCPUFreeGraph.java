package frameworks.faulttolerance.dcop.dcop;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.HashSet;
import java.util.Vector;

import frameworks.faulttolerance.dcop.DCOPFactory;
import frameworks.faulttolerance.dcop.algo.Algorithm;
import frameworks.faulttolerance.dcop.algo.topt.AsyncHelper;
import frameworks.faulttolerance.dcop.algo.topt.DPOPTreeNode;
import frameworks.faulttolerance.dcop.algo.topt.RewardMatrix;
import frameworks.faulttolerance.dcop.algo.topt.TreeNode;
import frameworks.faulttolerance.experimentation.ReplicationGraph;
import frameworks.faulttolerance.experimentation.ReplicationInstanceGraph;
import frameworks.negotiation.contracts.ResourceIdentifier;

import dima.basicagentcomponents.AgentIdentifier;
import dima.introspectionbasedagents.modules.distribution.NormalLaw.DispersionSymbolicValue;

import frameworks.experimentation.IfailedException;


public class DcopCPUFreeGraph extends DcopReplicationGraph{


	public DcopCPUFreeGraph() {
		super();
	}


	public DcopCPUFreeGraph(ReplicationGraph rig) {
		super(rig);
		instanciateConstraintsValues();
	}


	@Deprecated
	public DcopCPUFreeGraph(String inFilename) {
		// We assume in the input file, there is at most one link between two
		// variables
		try {
			BufferedReader reader = new BufferedReader(new FileReader(
					inFilename));
			varMap = new HashMap<Integer, ReplicationVariable>();
			conList = new Vector<ReplicationConstraint>();
			String line;
			ReplicationConstraint c = null;
			while ((line = reader.readLine()) != null) {
				if (line.startsWith("VARIABLE")) {
					String[] ss = line.split(" ");
					assert ss.length >= 4:Arrays.asList(ss);
					int	id = Integer.parseInt(ss[1]);
					int	domain = Integer.parseInt(ss[3]);
					ReplicationVariable v = DCOPFactory.constructVariable(id, domain,null,this);
					varMap.put(v.id, v);
				} else if (line.startsWith("CONSTRAINT")) {
					String[] ss = line.split(" ");
					assert ss.length >= 3;
					int first = Integer.parseInt(ss[1]);
					int second = Integer.parseInt(ss[2]);
					assert varMap.containsKey(first);
					assert varMap.containsKey(second);
					c = DCOPFactory.constructConstraint(varMap.get(first), varMap.get(second));
					conList.add(c);
				} else if (line.startsWith("F")) {
					assert c != null;
					String[] ss = line.split(" ");
					assert ss.length >= 4;
					int x = Integer.parseInt(ss[1]);
					int y = Integer.parseInt(ss[2]);
					int v = Integer.parseInt(ss[3]);
					((CPUFreeConstraint)c).f[x][y] = v;
				}
			}
			for (ReplicationConstraint cc : conList)
				((CPUFreeConstraint)cc).cache();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}
	/*
	 * 
	 */
	
	public String toString(){
		String result = "";
		for (ReplicationConstraint c : conList){
			result+="("+c.first.id+","+c.second.id+") --> \n";
			for (int i = 0; i < c.d1; i++){
				for (int j = 0; j < c.d2; j++){
					result+="   ------ "+Integer.toBinaryString(i)+" -- "+Integer.toBinaryString(j);//+"  =  "+c.f[i][j]+"\n";
				}
			}
		}
		return super.toString()+result;
	}
}












//
//private boolean respectHostRight(ResourceIdentifier idHost, int y, ReplicationGraph rig) {
//	double memMax = rig.getHostState(idHost).getMemChargeMax();
//	double procMax = rig.getHostState(idHost).getProcChargeMax();
//	double currentMem=0, currentProc=0;
//	for (AgentIdentifier idA : getReplicas(idHost, y, rig)){
//		currentMem+=rig.getAgentState(idA).getMyMemCharge();
//		currentProc+=rig.getAgentState(idA).getMyProcCharge();
//		if (currentMem>memMax || currentProc > procMax)
//			return false;
//	}
//	return true;
//}
//
//
//private boolean respectAgentRight(AgentIdentifier idAgent, int x, 
//		ReplicationGraph rig) {
//	return x!=0;
//}
//
//
////on retourn la prob divisé par le nombre de réplicas : la somme sur tous les hotes donne la bonne valeur
//private double valeur(AgentIdentifier idAgent, int x, 
//		ReplicationGraph rig) {
//	Collection<ResourceIdentifier> hosts = getHosts(idAgent, x, rig);
//	double failProb = 1;
//	for (ResourceIdentifier idH : hosts){
//		failProb*=rig.getHostState(idH).getFailureProb();
//	}
//
//	return (rig.getAgentState(idAgent).getMyCriticity()*(1-failProb))/hosts.size();
//}
//
//
//private boolean consistant(
//		AgentIdentifier idAgent, ResourceIdentifier idHost,
//		int x, int y, 
//		ReplicationGraph rig) {
//	Collection<AgentIdentifier> replicas = getReplicas(idHost, y, rig);
//	Collection<ResourceIdentifier> hosts = getHosts(idAgent, x, rig);
//	return (replicas.contains(idAgent) && hosts.contains(idHost)) || (!replicas.contains(idAgent) && !hosts.contains(idHost));
//}
//
//
//
//
//private Collection<AgentIdentifier> getReplicas(ResourceIdentifier h, int y, ReplicationGraph rig){
//	System.err.println("faux voir freememvar");
//	String allocHost = Integer.toBinaryString(y);
//	List<AgentIdentifier> hostAcc = new ArrayList<AgentIdentifier>(rig.getAccessibleAgents(h));
//	Collection<AgentIdentifier> result = new HashSet<AgentIdentifier>();
//	for (int i = 0; i < allocHost.length(); i++){
//		if (allocHost.charAt(i)=='1'){
//			result.add(hostAcc.get(i));
//		}
//	}
//	//		System.out.println("replicas de "+h+" avec "+allocHost+" sont "+result);
//	return result;
//}
//
//
//private Collection<ResourceIdentifier> getHosts(AgentIdentifier a, int x, ReplicationGraph rig){
//	System.err.println("faux voir freememvar");
//	String allocAgent = Integer.toBinaryString(x);
//	List<ResourceIdentifier> hostAcc = new ArrayList<ResourceIdentifier>(rig.getAccessibleHosts(a));
//	Collection<ResourceIdentifier> result = new HashSet<ResourceIdentifier>();
//	for (int i = 0; i < allocAgent.length(); i++)
//		if (allocAgent.charAt(i)=='1')
//			result.add(hostAcc.get(i));
//	//		System.out.println("hosts de "+a+" avec "+allocAgent+" sont "+result);
//	return result;
//}
//	