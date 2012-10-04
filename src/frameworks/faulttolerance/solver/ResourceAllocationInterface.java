package frameworks.faulttolerance.solver;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import javax.management.RuntimeErrorException;


import com.ziena.knitro.KnitroJava;

import dima.basicagentcomponents.AgentIdentifier;
import dima.introspectionbasedagents.modules.mappedcollections.HashedHashList;
import dima.introspectionbasedagents.modules.mappedcollections.HashedHashSet;
import dima.introspectionbasedagents.services.deployment.server.HostIdentifier;

import frameworks.faulttolerance.dcop.DCOPFactory;
import frameworks.faulttolerance.dcop.DcopSolver;
import frameworks.faulttolerance.dcop.dcop.DcopReplicationGraph;
import frameworks.faulttolerance.dcop.dcop.MemFreeConstraint;
import frameworks.faulttolerance.dcop.dcop.ReplicationVariable;
import frameworks.faulttolerance.experimentation.ReplicationInstanceGraph;
import frameworks.faulttolerance.negotiatingagent.HostState;
import frameworks.faulttolerance.negotiatingagent.ReplicaState;
import frameworks.faulttolerance.negotiatingagent.ReplicationCandidature;
import frameworks.negotiation.contracts.AbstractContractTransition.IncompleteContractException;
import frameworks.negotiation.exploration.ResourceAllocationSolver;
import frameworks.negotiation.exploration.Solver;
import frameworks.negotiation.exploration.Solver.UnsatisfiableException;
import frameworks.negotiation.rationality.AgentState;
import frameworks.negotiation.rationality.SocialChoiceFunction.SocialChoiceType;

public abstract class ResourceAllocationInterface<SolutionType> extends RessourceAllocationProblem<SolutionType>
implements Solver, DcopSolver, ResourceAllocationSolver<ReplicationCandidature, HostState>  {


	protected abstract void initiateSolver() throws UnsatisfiableException;

	protected abstract SolutionType solveProb(boolean opt)	throws UnsatisfiableException;

	@Override
	public abstract boolean hasNext();

	@Override
	public abstract void setTimeLimit(int millisec);

	//
	// Constructor
	//

	public ResourceAllocationInterface(SocialChoiceType socialChoice,
			boolean isAgent, boolean isHost) {
		super(socialChoice, isAgent, isHost);
	}

	/******                    *******/
	/******  DCOP Interface    *******/ 
	/******                    *******/

	DcopReplicationGraph drg;

	@Override
	public HashMap<Integer, Integer> solve(DcopReplicationGraph drg){
		this.drg=drg;

		for (ReplicationVariable var : drg.varMap.values()){
			if (var.fixed)
				fixedVar.add(var.getAgentIdentifier());
		}
		setProblem(drg);
		//		System.out.println("Agents are \n"+Arrays.asList(agents));
		//		System.out.println("Hosts  Are \n"+Arrays.asList(hosts));
		//		System.out.println("fixed are \n"+fixedVar);

		try {
			initiateSolver();
			return getDCOPSolution(solveProb(true));
		} catch (UnsatisfiableException e) {
			return getFailedSolution(drg);
		}
	}

	private HashMap<Integer, Integer> getDCOPSolution(SolutionType solvSol) {
		if (solvSol==null || solvSol!=null)
			return getFailedSolution(drg);

		HashedHashSet<AgentIdentifier,AgentIdentifier>  map = new HashedHashSet<AgentIdentifier,AgentIdentifier> ();

		for (int i = 0; i < n; i++){
			for (int j=0; j < m; j++){
				if(!(read(solvSol,i,j)==1.0 || read(solvSol,i,j)==0.0))
					return getFailedSolution(drg);
				if (read(solvSol,i,j)==1.0){
					map.add(getAgentIdentifier(i), getHostIdentifier(j));
					map.add(getHostIdentifier(j), getAgentIdentifier(i));
				}
			}
		}		

		HashMap<Integer, Integer> result = new HashMap<Integer, Integer>();
		for (Integer id : drg.varMap.keySet()){
			AgentIdentifier agid = DCOPFactory.intToIdentifier(id);
			result.put(id,drg.varMap.get(id).getValue(map.get(agid)));
		}
		return result;
	}

	public HashMap<Integer, Integer> getFailedSolution(DcopReplicationGraph drg){

		HashMap<Integer, Integer> result = new HashMap<Integer, Integer>();
		for (Integer id : drg.varMap.keySet()){
			result.put(id,0);
		}
		return result;
	}

	/******                    *******/
	/******  NEGO Interface    *******/ 
	/******                    *******/

	Map<AgentIdentifier,ReplicationCandidature> concerned;

	@Override
	public void initiate(Collection<ReplicationCandidature> concerned) {
		try {
			assert !isAgent && isHost;
			n=concerned.size();
			m=1;
			this.concerned= new HashMap<AgentIdentifier, ReplicationCandidature>();					
			Collection<ReplicaState> replicasStates = new ArrayList<ReplicaState>();
			Collection<HostState> hostsStates = new ArrayList<HostState>();
			double[] intialAlloc = new double[n];

			this.myState=concerned.iterator().next().getResourceInitialState();
			hostsStates.add(myState);

			Iterator<ReplicationCandidature> itC = concerned.iterator();
			int i =0;
			while (itC.hasNext()){
				ReplicationCandidature rc = itC.next();
				this.concerned.put(rc.getAgent(), rc);
				assert rc.getResourceInitialState().equals(rc.getResourceInitialState());
				assert rc.getAgentInitialState().getMyMemCharge().equals(
						rc.getAgentResultingState().getMyMemCharge());
				assert rc.getAgentInitialState().getMyProcCharge().equals(
						rc.getAgentResultingState().getMyProcCharge());
				assert rc.getResource().equals(myState.getMyAgentIdentifier());

				if (rc.isMatchingCreation()){
					replicasStates.add(rc.getAgentInitialState());
					intialAlloc[i]=0.;
				}else{
					replicasStates.add(rc.getAgentResultingState());
					intialAlloc[i]=1.;
				}
				i++;
			}		


			ReplicationInstanceGraph rig = new ReplicationInstanceGraph(null);
			rig.setAgents(replicasStates);
			rig.setHosts(hostsStates);
			for (AgentIdentifier id : rig.getAgentsIdentifier()){
				rig.addAcquaintance(id, myState.getMyAgentIdentifier());
			}
			setProblem(rig);
			initiateSolver();
			intialSolution=getInitialAllocAsSolution(intialAlloc);
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException();
		}
	}


	@Override
	public Collection<ReplicationCandidature> getBestLocalSolution()
			throws UnsatisfiableException, ExceedLimitException {
		return getContractSolution(solveProb(true));
	}

	@Override
	public Collection<ReplicationCandidature> getNextLocalSolution() {
		//		System.out.println(myState.getMyAgentIdentifier()+ " serching new solution ");
		SolutionType solution;
		try {
			solution = solveProb(false);
		} catch (UnsatisfiableException e) {
			return new ArrayList<ReplicationCandidature>();
		}

		if (isUpgrading(solution))
			intialSolution=solution;

		return  getContractSolution(solution);
	}

	/**
	 * Transforme la solution actuelle du solveur en candidature accepté
	 * @return la liste des candidature de la solution du solveur différentes de l'allcoation courante
	 */
	private Collection<ReplicationCandidature> getContractSolution(SolutionType sol){
		//		assert this.concerned.size()==sol.length;
		//		assert this.s.isFeasible()!=null && this.s.isFeasible():this.s.isFeasible()+" "+hasNext;
		//		assert hasNext:hasNext;
		final ArrayList<ReplicationCandidature> results = new ArrayList<ReplicationCandidature>();

		if (!isViable(sol))
			return results;
		else {
			for (int i = 0; i < this.concerned.size(); i++){
				ReplicationCandidature c = concerned.get(getAgentIdentifier(i));
				boolean allocated = read(sol,i,0)==1; 
				if (c.isMatchingCreation() && allocated || !c.isMatchingCreation() && !allocated) {
					results.add(c);
				}
			}
			return results;
		}
	}

	/******                           *******/
	/******  NEGO to Drg Interface    *******/ 
	/******                           *******/

	public static DcopReplicationGraph toDrg(Collection<ReplicationCandidature> concerned, SocialChoiceType socialWelfare) {
		assert concerned.size()<31;
		try {
			HashMap<Integer, ReplicationVariable> varMap;
			Vector<MemFreeConstraint> conList;		
			varMap = new HashMap<Integer, ReplicationVariable>();
			conList = new Vector<MemFreeConstraint>();

			ReplicationCandidature hostC = concerned.iterator().next();
			ReplicationVariable hostVar = DCOPFactory.constructVariable(
					DCOPFactory.identifierToInt(hostC.getResource()), 
					(int)Math.pow(2, concerned.size()),
					hostC.getResourceInitialState(),
					socialWelfare);
			varMap.put(hostVar.id, hostVar);
			for (ReplicationCandidature c : concerned){
				ReplicationVariable agentVar;
				agentVar = DCOPFactory.constructVariable(
						DCOPFactory.identifierToInt(c.getAgent()), 
						2,
						c.getAgentInitialState(),
						socialWelfare);

				assert !varMap.containsKey(DCOPFactory.identifierToInt(c.getAgent()));
				varMap.put(agentVar.id, agentVar);
				MemFreeConstraint con = DCOPFactory.constructConstraint(agentVar, hostVar);
				conList.add(con);
			}

			DcopReplicationGraph drg = new DcopReplicationGraph(varMap, conList, socialWelfare);


			if (DCOPFactory.isClassical()){
				drg.instanciateConstraintsValues();
			}		
			return drg;
		} catch (IncompleteContractException e) {
			throw new RuntimeException(e);
		}
	}	

	/**
	 * Transforme la solution actuelle du solveur en candidature accepté
	 * @return la liste des candidature de la solution du solveur différentes de l'allcoation courante
	 */
	public static Collection<ReplicationCandidature> getContractSolution(
			DcopReplicationGraph drg, Collection<ReplicationCandidature> concerned,HashMap<Integer,Integer> sol){
		assert concerned.size()==sol.size();
		//		assert this.s.isFeasible()!=null && this.s.isFeasible():this.s.isFeasible()+" "+hasNext;
		//		assert hasNext:hasNext;
		final ArrayList<ReplicationCandidature> results = new ArrayList<ReplicationCandidature>();
		AgentIdentifier host = concerned.iterator().next().getResource();
		for (ReplicationCandidature c : concerned){
			ReplicationVariable agent = drg.getVar(DCOPFactory.identifierToInt(c.getAgent()));
			boolean allocated = agent.hasAllocatedRessource(host, sol.get(agent.id));
			if (c.isMatchingCreation() && allocated || !c.isMatchingCreation() && !allocated) {
				results.add(c);
			}
		}
		return results;
	}
}



//	public HashMap<AgentIdentifier,AgentIdentifier> getAllocation(double[] solvSol) {
//		HashMap<AgentIdentifier,AgentState> result = new HashMap<AgentIdentifier, AgentState>();
//		for (int i = 0; i < n; i++){
//			result.put(agents[i].getMyAgentIdentifier(), agents[i]);
//		}
//		for (int j=0; j < m; j++){
//			result.put(hosts[j].getMyAgentIdentifier(), hosts[j]);
//		}
//
//		for (int i = 0; i < n; i++){
//			for (int j=0; j < m; j++){
//				assert solvSol[getPos(i,j)]==1.0 || solvSol[getPos(i,j)]==0.0;
//				result.put(getAgentIdentifier(i), ((ReplicaState)result.get(getAgentIdentifier(i))).allocate(hosts[j],solvSol[getPos(i,j)]==1.0));
//				result.put(getHostIdentifier(j), ((HostState)result.get(getHostIdentifier(j))).allocate(agents[i],solvSol[getPos(i,j)]==1.0));
//			}
//		}
//
//		for (AgentState s : result.values()){
//			if ((s instanceof ReplicaState && isAgent) || (s instanceof HostState && isHost))
//				assert s.isValid();
//		}
//		return result;
//	}