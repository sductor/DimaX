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
import frameworks.faulttolerance.experimentation.ReplicationInstanceGraph;
import frameworks.faulttolerance.negotiatingagent.HostState;
import frameworks.negotiation.contracts.ResourceIdentifier;
import frameworks.negotiation.rationality.AgentState;
import frameworks.negotiation.rationality.SocialChoiceFunction.SocialChoiceType;

public abstract class DcopReplicationGraph extends DcopAbstractGraph {


	public DcopReplicationGraph(
			final String simulationName, long randSeed,
			final int nbAgents, int nbHosts,
			final Double agentCriticityMean,final DispersionSymbolicValue agentCriticityDispersion,
			final Double agentLoadMean,final DispersionSymbolicValue agentLoadDispersion,
			final Double hostCapacityMean,final DispersionSymbolicValue hostCapacityDispersion,
			final Double hostFaultProbabilityMean,final DispersionSymbolicValue hostDisponibilityDispersion,
			boolean completGraph, int agentAccessiblePerHost) throws IfailedException{
		super();
		varMap = new HashMap<Integer, Variable>();
		conList = new Vector<Constraint>();

		ReplicationInstanceGraph rig = new ReplicationInstanceGraph();

		rig.initiate(
				simulationName, randSeed, 
				nbAgents, nbHosts, 
				agentCriticityMean, agentCriticityDispersion, agentLoadMean, agentLoadDispersion, 
				hostCapacityMean, hostCapacityDispersion, hostFaultProbabilityMean, hostDisponibilityDispersion, 
				SocialChoiceType.Utility, completGraph, agentAccessiblePerHost);
		System.out.println(rig.toString());


		for (AgentState a : rig.getAgentStates()) {
			AgentIdentifier id = a.getMyAgentIdentifier();
			Variable v = new Variable(
					identifierToInt(id,nbAgents), 
					(int) Math.pow(2, rig.getAccessibleHost(id).size()),
					this);
			assert !varMap.containsKey(identifierToInt(id,nbAgents));
			assert rig.getAccessibleHost(id).size()<31;
			varMap.put(v.id, v);
		}
		for (HostState h : rig.getHostsStates()){
			ResourceIdentifier id = h.getMyAgentIdentifier();
			Variable v = new Variable(
					identifierToInt(id,nbAgents), 
					(int) Math.pow(2, rig.getAccessibleAgent(id).size()),
					this);
			assert rig.getAccessibleAgent(id).size()<31;
			assert !varMap.containsKey(identifierToInt(id,nbAgents));
			varMap.put(v.id, v);
		}

		for (AgentState a : rig.getAgentStates()) {
			AgentIdentifier idAgent = a.getMyAgentIdentifier();
			Variable agentVar = varMap.get(identifierToInt(idAgent,nbAgents));
			for (ResourceIdentifier idHost : rig.getAccessibleHost(idAgent)){
				Variable hostVar = varMap.get(identifierToInt(idHost,nbAgents));
				Constraint c = new Constraint(agentVar, hostVar);
				conList.add(c);

				instanciateConstraintsValues(rig, idAgent, idHost, agentVar,
						hostVar, c);
			}
		}
//		assert symetrie(rig);
		System.out.println(toString());
	}


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
		for (Variable var : varMap.values()){
			result+=var.id+" -- "+var.domain+" -- "+var.value+"\n";
		}
		result+="\n Contraints :\n";
		for (Constraint c : conList){
			result+="("+c.first.id+","+c.second.id+") --> \n";
			for (int i = 0; i < c.d1; i++){
				for (int j = 0; j < c.d2; j++){
					result+="   ------ "+Integer.toBinaryString(i)+" -- "+Integer.toBinaryString(j)+"  =  "+c.f[i][j]+"\n";
				}
			}
		}
		return result;
	}

	//
	// Internals
	//

	private void instanciateConstraintsValues(ReplicationInstanceGraph rig,
			AgentIdentifier idAgent, ResourceIdentifier idHost,
			Variable agentVar, Variable hostVar, Constraint c) {
		for (int x = 0; x < agentVar.domain; x++){
			for (int y = 0; y < hostVar.domain; y++){
				if (!respectAgentRight(idAgent,x,rig)|| !respectHostRight(idHost,y,rig) ||  !consistant(idAgent,idHost,x,y,rig))
					c.f[x][y]=Double.NEGATIVE_INFINITY;
				else
					c.f[x][y]= valeur(idAgent,x,rig);
			}
		}
	}


	private boolean consistant(
			AgentIdentifier idAgent, ResourceIdentifier idHost,
			int x, int y, 
			ReplicationInstanceGraph rig) {
		Collection<AgentIdentifier> replicas = getReplicas(idHost, y, rig);
		Collection<ResourceIdentifier> hosts = getHosts(idAgent, x, rig);
		return (replicas.contains(idAgent) && hosts.contains(idHost)) || (!replicas.contains(idAgent) && !hosts.contains(idHost));
	}


	private boolean respectHostRight(ResourceIdentifier idHost, int y, 
			ReplicationInstanceGraph rig) {
		double memMax = rig.getHostState(idHost).getMemChargeMax();
		double procMax = rig.getHostState(idHost).getProcChargeMax();
		double currentMem=0, currentProc=0;
		for (AgentIdentifier idA : getReplicas(idHost, y, rig)){
			currentMem+=rig.getAgentState(idA).getMyMemCharge();
			currentProc+=rig.getAgentState(idA).getMyProcCharge();
			if (currentMem>memMax || currentProc > procMax)
				return false;
		}
		return true;
	}


	private boolean respectAgentRight(AgentIdentifier idAgent, int x, 
			ReplicationInstanceGraph rig) {
		return x!=0;
	}


	//on retourn la prob divisé par le nombre de réplicas : la somme sur tous les hotes donne la bonne valeur
	private double valeur(AgentIdentifier idAgent, int x, 
			ReplicationInstanceGraph rig) {
		Collection<ResourceIdentifier> hosts = getHosts(idAgent, x, rig);
		double failProb = 1;
		for (ResourceIdentifier idH : hosts){
			failProb*=rig.getHostState(idH).getFailureProb();
		}

		return (rig.getAgentState(idAgent).getMyCriticity()*(1-failProb))/hosts.size();
	}


	private boolean symetrie(ReplicationInstanceGraph rig) {
		for (HostState h : rig.getHostsStates()) {
			ResourceIdentifier idHost = h.getMyAgentIdentifier();
			for (AgentIdentifier idAgent : rig.getAccessibleAgent(idHost)){
				Constraint c = new Constraint(varMap.get(idAgent), varMap.get(idHost));
				assert conList.contains(c);
			}
		}
		return true;
	}


	private Collection<AgentIdentifier> getReplicas(ResourceIdentifier h, int y, ReplicationInstanceGraph rig){
		String allocHost = Integer.toBinaryString(y);
		List<AgentIdentifier> hostAcc = new ArrayList<AgentIdentifier>(rig.getAccessibleAgent(h));
		Collection<AgentIdentifier> result = new HashSet<AgentIdentifier>();
		for (int i = 0; i < allocHost.length(); i++){
			if (allocHost.charAt(i)=='1'){
				result.add(hostAcc.get(i));
			}
		}
		System.out.println("replicas de "+h+" avec "+allocHost+" sont "+result);
		return result;
	}


	private Collection<ResourceIdentifier> getHosts(AgentIdentifier a, int x, ReplicationInstanceGraph rig){
		String allocAgent = Integer.toBinaryString(x);
		List<ResourceIdentifier> hostAcc = new ArrayList<ResourceIdentifier>(rig.getAccessibleHost(a));
		Collection<ResourceIdentifier> result = new HashSet<ResourceIdentifier>();
		for (int i = 0; i < allocAgent.length(); i++)
			if (allocAgent.charAt(i)=='1')
				result.add(hostAcc.get(i));
		System.out.println("hosts de "+a+" avec "+allocAgent+" sont "+result);
		return result;
	}
	private Integer identifierToInt(AgentIdentifier id, int n){
		if (id instanceof ResourceIdentifier)
			return 5*n*(1+new Integer(id.toString().split("-=-")[1]));
		else
			return new Integer(id.toString().split("-=-")[1]);
	}
}
