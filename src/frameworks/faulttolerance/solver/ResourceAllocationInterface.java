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
import dima.introspectionbasedagents.modules.faults.Assert;
import dima.introspectionbasedagents.modules.mappedcollections.HashedHashList;
import dima.introspectionbasedagents.modules.mappedcollections.HashedHashSet;
import dima.introspectionbasedagents.services.deployment.server.HostIdentifier;
import dima.introspectionbasedagents.services.loggingactivity.LogWarning;

import frameworks.faulttolerance.experimentation.ReplicationGraph;
import frameworks.faulttolerance.experimentation.ReplicationInstanceGraph;
import frameworks.faulttolerance.negotiatingagent.HostState;
import frameworks.faulttolerance.negotiatingagent.ReplicaState;
import frameworks.faulttolerance.negotiatingagent.ReplicationCandidature;
import frameworks.faulttolerance.olddcop.DCOPFactory;
import frameworks.faulttolerance.olddcop.DcopSolver;
import frameworks.faulttolerance.olddcop.dcop.DcopReplicationGraph;
import frameworks.faulttolerance.olddcop.dcop.MemFreeConstraint;
import frameworks.faulttolerance.olddcop.dcop.ReplicationVariable;
import frameworks.negotiation.contracts.ReallocationContract;
import frameworks.negotiation.contracts.AbstractContractTransition.IncompleteContractException;
import frameworks.negotiation.contracts.ResourceIdentifier;
import frameworks.negotiation.exploration.ResourceAllocationSolver;
import frameworks.negotiation.exploration.Solver;
import frameworks.negotiation.exploration.Solver.UnsatisfiableException;
import frameworks.negotiation.rationality.AgentState;
import frameworks.negotiation.rationality.SocialChoiceFunction.SocialChoiceType;

public abstract class ResourceAllocationInterface<SolutionType> extends RessourceAllocationProblem<SolutionType>
implements Solver, ResourceAllocationSolver<ReplicationCandidature, HostState>  {


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

	Map<AgentIdentifier,ReplicationCandidature> concerned=null;

	@Override
	public void setProblem(ReplicationGraph rig, Collection<AgentIdentifier> fixedVar) {
		try {
			assert ((ReplicationInstanceGraph)rig).assertAllocValid();
			super.setProblem(rig, fixedVar);
			initiateSolver();
			assert reverseHostId!=null;
			double[] intialAlloc = new double[getVariableNumber()];

			for (int i = 0; i < n; i++){
				for (int j=0; j < m; j++){
					if (!isConstant(i, j)){
						ResourceIdentifier hostIdentifier = getHostIdentifier(j);
						AgentIdentifier agentIdentifier = getAgentIdentifier(i);
						assert rig.getAgentState(getAgentIdentifier(i)).hasResource(getHostIdentifier(j))==rig.getHostState(hostIdentifier).hasResource(agentIdentifier);
						intialAlloc[getPos(i,j)]=rig.getAgentState(getAgentIdentifier(i)).hasResource(getHostIdentifier(j))?1.:0.;
					}
				}
			}
			assert  boundValidity();
			//			assert  initialAllocOk(intialAlloc);
			initialSolution=getInitialAllocAsSolution(intialAlloc);
			assert ((ReplicationInstanceGraph)rig).assertAllocValid();
			assert initialAllocOk(intialAlloc);
			assert  boundValidity();
			try {
				assert assertIsViable(initialSolution):rig;
			} catch (AssertionError e){
				System.err.println(rig);
				e.printStackTrace();
			}
			assert initialSolution!=null;
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException();
		}
	}
	private boolean initialAllocOk(double[] intialAlloc){
		for (int i=0; i <n; i++){
			for (int j=0; j<m; j++){
				if (!isConstant(i, j)){
				ResourceIdentifier hostIdentifier = getHostIdentifier(j);
				AgentIdentifier agentIdentifier = getAgentIdentifier(i);
				assert Assert.IIF(rig.getAgentState(agentIdentifier).hasResource(hostIdentifier),intialAlloc[getPos(i,j)]==1.0);
				assert Assert.IIF(rig.getHostState(hostIdentifier).hasResource(agentIdentifier), intialAlloc[getPos(i,j)]==1.0);
				assert Assert.IIF(!rig.getAgentState(agentIdentifier).hasResource(hostIdentifier), intialAlloc[getPos(i,j)]==0.);
				assert Assert.IIF(!rig.getHostState(hostIdentifier).hasResource(agentIdentifier), intialAlloc[getPos(i,j)]==0.);
			}
			}
		}
		if (initialSolution!=null){
			for (int i=0; i <n; i++){
				for (int j=0; j<m; j++){
					ResourceIdentifier hostIdentifier = getHostIdentifier(j);
					AgentIdentifier agentIdentifier = getAgentIdentifier(i);
					assert Assert.IIF(rig.getAgentState(agentIdentifier).hasResource(hostIdentifier), read(initialSolution,i,j)==1.0):agentIdentifier+" "+hostIdentifier+" "+printBounds()+"\n"+print(initialSolution)+"\n"+print(intialAlloc)+"\n"+rig;
					assert Assert.IIF(rig.getHostState(hostIdentifier).hasResource(agentIdentifier), read(initialSolution,i,j)==1.0);
					assert Assert.IIF(!rig.getAgentState(agentIdentifier).hasResource(hostIdentifier), read(initialSolution,i,j)==0.);
					assert Assert.IIF(!rig.getHostState(hostIdentifier).hasResource(agentIdentifier), read(initialSolution,i,j)==0.);
				}
			}
		}
		return true;
	}

	@Override
	public void setProblem(Collection<ReplicationCandidature> concerned) {
		try {
			assert !isAgent && isHost;
			n=concerned.size();
			m=1;
			this.concerned= new HashMap<AgentIdentifier, ReplicationCandidature>();					
			Collection<ReplicaState> replicasStates = new ArrayList<ReplicaState>();
			Collection<HostState> hostsStates = new ArrayList<HostState>();
			double[] intialAlloc = new double[n*1];

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


			super.setProblem(rig, new ArrayList<AgentIdentifier>());
			initiateSolver();
			initialSolution=getInitialAllocAsSolution(intialAlloc);
			assert isViable(initialSolution);
			assert initialSolution!=null;
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
			initialSolution=solution;

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
			if (concerned!=null){
				for (int i = 0; i < this.concerned.size(); i++){
					ReplicationCandidature c = concerned.get(getAgentIdentifier(i));
					boolean allocated = read(sol,i,0)==1; 
					if (c.isMatchingCreation() && allocated || !c.isMatchingCreation() && !allocated) {
						results.add(c);
					}
				}
			} else {
				for (int i = 0; i < n; i++){
					for (int j = 0; j < m; j++){
						boolean orignallyAllocated = rig.getAgentState(getAgentIdentifier(i)).hasResource(getHostIdentifier(j));
						assert rig.getHostState(getHostIdentifier(j)).hasResource(getAgentIdentifier(i))==orignallyAllocated;
						boolean allocatedinSol = read(sol,i,0)==1; 
						if (orignallyAllocated != allocatedinSol) {
							ReplicationCandidature c = 
									new ReplicationCandidature(getMyAgentIdentifier(), getHostIdentifier(j), getAgentIdentifier(i), allocatedinSol);
							c.setInitialState(rig.getHostState(getHostIdentifier(j)));
							c.setInitialState(rig.getAgentState(getAgentIdentifier(i)));
							results.add(c);
						}

					}
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