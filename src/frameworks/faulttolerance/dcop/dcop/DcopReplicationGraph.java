<<<<<<< HEAD
<<<<<<< HEAD
package frameworks.faulttolerance.dcop.dcop;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
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
import frameworks.faulttolerance.dcop.DcopFactory;
import frameworks.faulttolerance.dcop.DcopFactory.DCOPType;
import frameworks.faulttolerance.experimentation.ReplicationInstanceGraph;
import frameworks.faulttolerance.negotiatingagent.HostState;
import frameworks.negotiation.contracts.ResourceIdentifier;
import frameworks.negotiation.rationality.AgentState;
import frameworks.negotiation.rationality.SocialChoiceFunction.SocialChoiceType;

public abstract class DcopReplicationGraph<Value> extends DcopAbstractGraph<Value> {


	public DcopReplicationGraph(
			final String simulationName, long randSeed,
			final int nbAgents, int nbHosts,
			final Double agentCriticityMean,final DispersionSymbolicValue agentCriticityDispersion,
			final Double agentLoadMean,final DispersionSymbolicValue agentLoadDispersion,
			final Double hostCapacityMean,final DispersionSymbolicValue hostCapacityDispersion,
			final Double hostFaultProbabilityMean,final DispersionSymbolicValue hostDisponibilityDispersion,
			boolean completGraph, int agentAccessiblePerHost) throws IfailedException{
		super();
		varMap = new HashMap<Integer, AbstractVariable<Value>>();
		conList = new Vector<AbstractConstraint<Value>>();

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
			AbstractVariable v = DcopFactory.constructVariable(
					identifierToInt(id,nbAgents), 
					(int) Math.pow(2, rig.getAccessibleHost(id).size()),
					this);
			assert !varMap.containsKey(identifierToInt(id,nbAgents));
			assert rig.getAccessibleHost(id).size()<31;
			varMap.put(v.id, v);
		}
		for (HostState h : rig.getHostsStates()){
			ResourceIdentifier id = h.getMyAgentIdentifier();
			AbstractVariable v = DcopFactory.constructVariable(
					identifierToInt(id,nbAgents), 
					(int) Math.pow(2, rig.getAccessibleAgent(id).size()),
					this);
			assert rig.getAccessibleAgent(id).size()<31;
			assert !varMap.containsKey(identifierToInt(id,nbAgents));
			varMap.put(v.id, v);
		}

		for (AgentState a : rig.getAgentStates()) {
			AgentIdentifier idAgent = a.getMyAgentIdentifier();
			AbstractVariable agentVar = varMap.get(identifierToInt(idAgent,nbAgents));
			for (ResourceIdentifier idHost : rig.getAccessibleHost(idAgent)){
				AbstractVariable hostVar = varMap.get(identifierToInt(idHost,nbAgents));
				AbstractConstraint c = DcopFactory.constructConstraint(agentVar, hostVar);
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
		for (AbstractVariable var : varMap.values()){
			result+=var.id+" -- "+var.domain+" -- "+var.value+"\n";
		}
		result+="\n Contraints :\n";
		for (AbstractConstraint c : conList){
			result+="("+c.getFirst().id+","+c.getSecond().id+") --> \n";
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

	private void instanciateConstraintsValues(ReplicationInstanceGraph rig,
			AgentIdentifier idAgent, ResourceIdentifier idHost,
			AbstractVariable agentVar, AbstractVariable hostVar, AbstractConstraint c) {
		if (DcopFactory.type==DCOPType.Classical){
		for (int x = 0; x < agentVar.domain; x++){
			for (int y = 0; y < hostVar.domain; y++){
				if (!respectAgentRight(idAgent,x,rig)|| !respectHostRight(idHost,y,rig) ||  !consistant(idAgent,idHost,x,y,rig))
					((ClassicalConstraint)c).f[x][y]=Double.NEGATIVE_INFINITY;
				else
					((ClassicalConstraint)c).f[x][y]= valeur(idAgent,x,rig);
			}
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
				AbstractConstraint c =  DcopFactory.constructConstraint(varMap.get(idAgent), varMap.get(idHost));
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
=======
=======
>>>>>>> dcopX
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
import java.util.Random;
import java.util.HashSet;
import java.util.Vector;

import javax.management.RuntimeErrorException;

import org.jivesoftware.smackx.jingle.nat.TransportCandidate.Fixed;

import dima.basicagentcomponents.AgentIdentifier;
import dima.introspectionbasedagents.modules.distribution.NormalLaw.DispersionSymbolicValue;
import dima.support.GimaObject;

import frameworks.experimentation.IfailedException;
import frameworks.faulttolerance.dcop.DCOPFactory;
import frameworks.faulttolerance.dcop.algo.topt.AsyncHelper;
import frameworks.faulttolerance.dcop.algo.topt.DPOPTreeNode;
import frameworks.faulttolerance.dcop.algo.topt.RewardMatrix;
import frameworks.faulttolerance.dcop.algo.topt.TreeNode;
import frameworks.faulttolerance.experimentation.ReplicationGraph;
import frameworks.faulttolerance.negotiatingagent.HostState;
import frameworks.faulttolerance.negotiatingagent.ReplicaState;
import frameworks.faulttolerance.solver.SolverFactory;
import frameworks.negotiation.contracts.ResourceIdentifier;
import frameworks.negotiation.exploration.Solver.ExceedLimitException;
import frameworks.negotiation.exploration.Solver.UnsatisfiableException;
import frameworks.negotiation.rationality.AgentState;
import frameworks.negotiation.rationality.SocialChoiceFunction.SocialChoiceType;



public class DcopReplicationGraph extends GimaObject implements ReplicationGraph{

	public HashMap<Integer, ReplicationVariable> varMap;
	public Vector<MemFreeConstraint> conList;
	final SocialChoiceType socialWelfare;

	public DcopReplicationGraph(SocialChoiceType socialWelfare) {
		varMap = new HashMap<Integer, ReplicationVariable>();
		conList = new Vector<MemFreeConstraint>();
		this.socialWelfare=socialWelfare;
	}

	public DcopReplicationGraph(HashMap<Integer, ReplicationVariable> varMap,
			Vector<MemFreeConstraint> conList, SocialChoiceType socialWelfare) {
		super();
		this.varMap = varMap;
		this.conList = conList;
		this.socialWelfare = socialWelfare;
	}

	public DcopReplicationGraph(ReplicationGraph rig) {
		super();
		varMap = new HashMap<Integer, ReplicationVariable>();
		conList = new Vector<MemFreeConstraint>();
		this.socialWelfare=rig.getSocialWelfare();
		for (AgentState a : rig.getAgentStates()) {
			AgentIdentifier id = a.getMyAgentIdentifier();
			ReplicationVariable v = DCOPFactory.constructVariable(
					DCOPFactory.identifierToInt(id), 
					(int) Math.pow(2, rig.getAccessibleHosts(id).size()),
					rig.getAgentState(id),
					socialWelfare);
			assert !varMap.containsKey(DCOPFactory.identifierToInt(id));
			assert rig.getAccessibleHosts(id).size()<31;
			varMap.put(v.id, v);
		}

		for (HostState h : rig.getHostsStates()){
			ResourceIdentifier id = h.getMyAgentIdentifier();
			ReplicationVariable v = DCOPFactory.constructVariable(
					DCOPFactory.identifierToInt(id), 
					(int) Math.pow(2, rig.getAccessibleAgents(id).size()),
					rig.getHostState(id),
					socialWelfare);
			assert rig.getAccessibleAgents(id).size()<31;
			assert !varMap.containsKey(DCOPFactory.identifierToInt(id));
			varMap.put(v.id, v);
		}

		for (AgentState a : rig.getAgentStates()) {
			AgentIdentifier idAgent = a.getMyAgentIdentifier();
			ReplicationVariable agentVar = varMap.get(DCOPFactory.identifierToInt(idAgent));
			for (ResourceIdentifier idHost : rig.getAccessibleHosts(idAgent)){
				ReplicationVariable hostVar = varMap.get(DCOPFactory.identifierToInt(idHost));
				MemFreeConstraint c = DCOPFactory.constructConstraint(agentVar, hostVar);
				conList.add(c);
			}
			assert agentVar.getDomain()==(int)Math.pow(2, agentVar.getNeighbors().size()):
				agentVar.id+" "+agentVar.getDomain()+" "+agentVar.getNeighbors();
		}
		
		if (DCOPFactory.isClassical()){
			instanciateConstraintsValues();
		}
	}

	public void instanciateConstraintsValues() {
		for (MemFreeConstraint c : conList){
			for (int x = 0; x < c.first.getDomain(); x++){
				for (int y = 0; y < c.second.getDomain(); y++){
					c.getHost().backupValue();
					c.getAgent().backupValue();
					c.getHost().setValue(y);
					c.getAgent().setValue(x);
					((CPUFreeConstraint)c).f[x][y]=Math.max(c.getAgent().evaluate(), c.getHost().evaluate());
					c.getHost().recoverValue();
					c.getAgent().recoverValue();
				}
			}
			((CPUFreeConstraint)c).cache();
		}
	}


	@Deprecated
	public DcopReplicationGraph(String inFilename, SocialChoiceType socialWelfare) {
		// We assume in the input file, there is at most one link between two
		// variables
		this.socialWelfare=socialWelfare;
		try {
			BufferedReader reader = new BufferedReader(new FileReader(
					inFilename));
			varMap = new HashMap<Integer, ReplicationVariable>();
			conList = new Vector<MemFreeConstraint>();
			String line;
			MemFreeConstraint c = null;
			while ((line = reader.readLine()) != null) {
				if (line.startsWith("VARIABLE")) {
					String[] ss = line.split(" ");
					assert ss.length >= 4:Arrays.asList(ss);
					int	id = Integer.parseInt(ss[1]);
					int	domain = Integer.parseInt(ss[3]);
					ReplicationVariable v = DCOPFactory.constructVariable(id, domain,null,socialWelfare);
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
			for (MemFreeConstraint cc : conList)
				((CPUFreeConstraint)cc).cache();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}


	public double evaluate(){
		return DCOPFactory.evaluate(this);
	}
	public  HashMap<Integer, Integer> solve(){
			return SolverFactory.solve(this);
	}


	public SocialChoiceType getSocialWelfare() {
		return socialWelfare;
	}

	public ReplicationVariable getVar(int i) {
		return varMap.get(i);
	}

	public boolean checkValues() {
		for (ReplicationVariable v : varMap.values()){
			if (v.getValue() == -1)
				return false;
		}
		return true;
	}

	public double evaluate(HashMap<Integer, Integer> sol) {
		this.backup();
		for (ReplicationVariable v : varMap.values()){
			v.setValue(sol.get(v.id));
		}
		double value = this.evaluate();
		this.recover();
		return value;
	}

	public boolean sameSolution(HashMap<Integer, Integer> sol) {
		if (sol == null)
			return false;
		for (ReplicationVariable v : varMap.values()) {
			if (!sol.containsKey(v.id))
				return false;
			if (v.getValue() != sol.get(v.id))
				return false;
		}
		return true;
	}

	public void clear() {
		for (ReplicationVariable v : varMap.values())
			v.clear();
	}

	public void backup() {
		for (ReplicationVariable v : varMap.values())
			v.backupValue();
	}

	public void recover() {
		for (ReplicationVariable v : varMap.values())
			v.recoverValue();
	}


	public HashMap<Integer, Integer> getSolution() {
		HashMap<Integer, Integer> map = new HashMap<Integer, Integer>();
		for (ReplicationVariable v : varMap.values())
			map.put(v.id, v.getValue());
				return map;
	}

	public String toString(){
		String result = "DCOP GRAPH \nVariables :\n";
		for (ReplicationVariable var : varMap.values()){
			result+="id : "+var.id+", "+var.getAgentIdentifier()
					+"\n ----> initialValue "+var.getInitialValue()+", "+var.getAllocatedRessources(var.getInitialValue())
					+"\n ----> voisinage "+var.getDomain()+", "+var.getNeighborsIdentifiers()
					+"\n ----> currentValue "+var.getValue()+(var.getValue()!=-1?", "+var.getAllocatedRessources():" uninstaciated")
					+"\n ----> fixed?"+var.fixed
					+"\n";
		}
		result+="\n Contraints links :\n";
		for (MemFreeConstraint c : conList){
			result+="("+c.first.id+","+c.second.id+") \n";
			if (DCOPFactory.isClassical()){
				for (int i = 0; i < c.d1; i++){
					for (int j = 0; j < c.d2; j++){
						result+="   ------>  f("+Integer.toBinaryString(i)+" , "+Integer.toBinaryString(j)+")  =  "+((CPUFreeConstraint)c).f[i][j]+"\n";
					}
				}
			}
		}
		return result;
	}

	//
	// implementing ReplicationGraph
	//

	@Override
	public Collection<AgentIdentifier> getAgentsIdentifier() {
		Collection<AgentIdentifier> result = new HashSet<AgentIdentifier>();
		for (Integer i : varMap.keySet()){
			AgentIdentifier id = DCOPFactory.intToIdentifier(i);
			if (!(id instanceof ResourceIdentifier))
				result.add(id);
		}
		return result;
	}

	@Override
	public Collection<ResourceIdentifier> getHostsIdentifier() {
		Collection<ResourceIdentifier> result = new HashSet<ResourceIdentifier>();
		for (Integer i : varMap.keySet()){
			AgentIdentifier id = DCOPFactory.intToIdentifier(i);
			if (id instanceof ResourceIdentifier)
				result.add((ResourceIdentifier) id);
		}
		return result;
	}

	@Override
	public Collection<ReplicaState> getAgentStates() {
		Collection<ReplicaState> result = new HashSet<ReplicaState>();
		for (Integer i : varMap.keySet()){
			AgentIdentifier id = DCOPFactory.intToIdentifier(i);
			if (!(id instanceof ResourceIdentifier)){
				assert getAgentState(id)!=null;
				result.add(getAgentState(id));
			}
		}
		return result;
	}

	@Override
	public Collection<HostState> getHostsStates() {
		Collection<HostState> result = new HashSet<HostState>();
		for (Integer i : varMap.keySet()){
			AgentIdentifier id = DCOPFactory.intToIdentifier(i);
			if (id instanceof ResourceIdentifier){
				assert getHostState((ResourceIdentifier) id)!=null;
				result.add(getHostState((ResourceIdentifier) id));
			}
		}
		return result;
	}

	@Override
	public ReplicaState getAgentState(AgentIdentifier id) {
		return (ReplicaState) varMap.get(DCOPFactory.identifierToInt(id)).getState();
	}

	@Override
	public HostState getHostState(ResourceIdentifier id) {
		return (HostState) varMap.get(DCOPFactory.identifierToInt(id)).getState();
	}

	@Override
	public Collection<ResourceIdentifier> getAccessibleHosts(AgentIdentifier id) {
		Collection<ResourceIdentifier> result = new ArrayList<ResourceIdentifier>();
		for (AgentIdentifier id2 : varMap.get(DCOPFactory.identifierToInt(id)).getNeighborsIdentifiers()){
			assert (id2 instanceof ResourceIdentifier);
			result.add((ResourceIdentifier) id2);
		}
		return result;
	}

	@Override
	public Collection<AgentIdentifier> getAccessibleAgents(ResourceIdentifier id) {
		Collection<AgentIdentifier> result = new ArrayList<AgentIdentifier>();
		for (AgentIdentifier id2 : varMap.get(DCOPFactory.identifierToInt(id)).getNeighborsIdentifiers()){
			assert !(id2 instanceof ResourceIdentifier);
			result.add(id2);
		}
		return result;
	}
}
<<<<<<< HEAD
>>>>>>> dcopX
=======
>>>>>>> dcopX
