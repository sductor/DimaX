package frameworks.faulttolerance.dcop.dcop;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Vector;

import dima.basicagentcomponents.AgentIdentifier;
import dima.introspectionbasedagents.modules.distribution.NormalLaw.DispersionSymbolicValue;

import frameworks.experimentation.IfailedException;
import frameworks.faulttolerance.dcop.DCOPFactory;
import frameworks.faulttolerance.experimentation.ReplicationInstanceGraph;
import frameworks.faulttolerance.negotiatingagent.HostState;
import frameworks.negotiation.contracts.ResourceIdentifier;
import frameworks.negotiation.rationality.AgentState;
import frameworks.negotiation.rationality.SocialChoiceFunction.SocialChoiceType;

public abstract class DcopReplicationGraph extends DcopAbstractGraph {


	public DcopReplicationGraph() {
		super();
	}


	public DcopReplicationGraph(String inFilename) {
		super(inFilename);
	}


	//
	// Primitives
	//

	public String toString(){
		String result = "Variables :\n";
		for (ReplicationVariable var : varMap.values()){
			result+=var.id+" -- "+var.getDomain()+" -- "+var.getValue()+"\n";
		}
		result+="\n Contraints :\n";
		for (ReplicationConstraint c : conList){
			result+="("+c.first.id+","+c.second.id+") --> \n";
			for (int i = 0; i < c.d1; i++){
				for (int j = 0; j < c.d2; j++){
					result+="   ------ "+Integer.toBinaryString(i)+" -- "+Integer.toBinaryString(j);//+"  =  "+c.f[i][j]+"\n";
				}
			}
		}
		return result;
	}

	//
	// Internals
	//


//
//	private boolean consistant(
//			AgentIdentifier idAgent, ResourceIdentifier idHost,
//			int x, int y, 
//			ReplicationInstanceGraph rig) {
//		Collection<AgentIdentifier> replicas = getReplicas(idHost, y, rig);
//		Collection<ResourceIdentifier> hosts = getHosts(idAgent, x, rig);
//		return (replicas.contains(idAgent) && hosts.contains(idHost)) || (!replicas.contains(idAgent) && !hosts.contains(idHost));
//	}




//
//	private boolean symetrie(ReplicationInstanceGraph rig) {
//		for (HostState h : rig.getHostsStates()) {
//			ResourceIdentifier idHost = h.getMyAgentIdentifier();
//			for (AgentIdentifier idAgent : rig.getAccessibleAgent(idHost)){
//				ReplicationConstraint c = DCOPFactory.constructConstraint(varMap.get(idAgent), varMap.get(idHost));
//				assert conList.contains(c);
//			}
//		}
//		return true;
//	}
//
//
//	private Collection<AgentIdentifier> getReplicas(ResourceIdentifier h, int y, ReplicationInstanceGraph rig){
//		String allocHost = Integer.toBinaryString(y);
//		List<AgentIdentifier> hostAcc = new ArrayList<AgentIdentifier>(rig.getAccessibleAgent(h));
//		Collection<AgentIdentifier> result = new HashSet<AgentIdentifier>();
//		for (int i = 0; i < allocHost.length(); i++){
//			if (allocHost.charAt(i)=='1'){
//				result.add(hostAcc.get(i));
//			}
//		}
////		System.out.println("replicas de "+h+" avec "+allocHost+" sont "+result);
//		return result;
//	}
//
//
//	private Collection<ResourceIdentifier> getHosts(AgentIdentifier a, int x, ReplicationInstanceGraph rig){
//		String allocAgent = Integer.toBinaryString(x);
//		List<ResourceIdentifier> hostAcc = new ArrayList<ResourceIdentifier>(rig.getAccessibleHost(a));
//		Collection<ResourceIdentifier> result = new HashSet<ResourceIdentifier>();
//		for (int i = 0; i < allocAgent.length(); i++)
//			if (allocAgent.charAt(i)=='1')
//				result.add(hostAcc.get(i));
////		System.out.println("hosts de "+a+" avec "+allocAgent+" sont "+result);
//		return result;
//	}
//	
//	
}
