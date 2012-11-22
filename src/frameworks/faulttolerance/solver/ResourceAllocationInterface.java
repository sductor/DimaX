package frameworks.faulttolerance.solver;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import dima.basicagentcomponents.AgentIdentifier;
import dima.introspectionbasedagents.modules.faults.Assert;
import frameworks.faulttolerance.experimentation.ReplicationGraph;
import frameworks.faulttolerance.experimentation.ReplicationInstanceGraph;
import frameworks.faulttolerance.negotiatingagent.HostState;
import frameworks.faulttolerance.negotiatingagent.ReplicaState;
import frameworks.faulttolerance.negotiatingagent.ReplicationCandidature;
import frameworks.negotiation.contracts.ResourceIdentifier;
import frameworks.negotiation.exploration.ResourceAllocationSolver;
import frameworks.negotiation.exploration.Solver;
import frameworks.negotiation.rationality.SocialChoiceFunction.SocialChoiceType;

public abstract class ResourceAllocationInterface<SolutionType> extends RessourceAllocationProblem<SolutionType>
implements Solver, ResourceAllocationSolver<ReplicationCandidature, HostState>  {


	/**
	 * 
	 */
	private static final long serialVersionUID = 7664484372214592865L;

	protected abstract void initiateSolver() throws UnsatisfiableException;
	protected abstract void initiateSolverPost() throws UnsatisfiableException;

	protected abstract SolutionType solveProb(boolean opt)	throws UnsatisfiableException;

	@Override
	public abstract boolean hasNext();

	@Override
	public abstract void setTimeLimit(int millisec);

	//
	// Constructor
	//

	public ResourceAllocationInterface(final SocialChoiceType socialChoice,
			final boolean isAgent, final boolean isHost) {
		super(socialChoice, isAgent, isHost);
	}

	Map<AgentIdentifier,ReplicationCandidature> concerned=null;

	@Override
	public void setProblem(final ReplicationGraph rig, final Collection<AgentIdentifier> fixedVar) {
		try {
			assert ((ReplicationInstanceGraph)rig).assertAllocValid();
			super.setProblem(rig, fixedVar);
			this.initiateSolver();
			assert this.reverseHostId!=null;
			final double[] intialAlloc = new double[this.getVariableNumber()];

			for (int i = 0; i < this.n; i++){
				for (int j=0; j < this.m; j++){
					if (!this.isConstant(i, j)){
						final ResourceIdentifier hostIdentifier = this.getHostIdentifier(j);
						final AgentIdentifier agentIdentifier = this.getAgentIdentifier(i);
						assert rig.getAgentState(this.getAgentIdentifier(i)).hasResource(this.getHostIdentifier(j))==rig.getHostState(hostIdentifier).hasResource(agentIdentifier);
						intialAlloc[this.getPos(i,j)]=rig.getAgentState(this.getAgentIdentifier(i)).hasResource(this.getHostIdentifier(j))?1.:0.;
					}
				}
			}
			assert  this.boundValidity();
			//			assert  initialAllocOk(intialAlloc);
			this.initialSolution=this.getInitialAllocAsSolution(intialAlloc);
			assert ((ReplicationInstanceGraph)rig).assertAllocValid();
			assert this.initialAllocOk(intialAlloc);
			assert  this.boundValidity();
			this.initiateSolverPost();
			try {
				assert this.assertIsViable(this.initialSolution):rig;
			} catch (final AssertionError e){
				System.err.println(rig);
				e.printStackTrace();
			}
			assert this.initialSolution!=null;
		} catch (final Exception e) {
			e.printStackTrace();
			throw new RuntimeException();
		}
	}
	private boolean initialAllocOk(final double[] intialAlloc){
		for (int i=0; i <this.n; i++){
			for (int j=0; j<this.m; j++){
				if (!this.isConstant(i, j)){
					final ResourceIdentifier hostIdentifier = this.getHostIdentifier(j);
					final AgentIdentifier agentIdentifier = this.getAgentIdentifier(i);
					assert Assert.IIF(this.rig.getAgentState(agentIdentifier).hasResource(hostIdentifier),intialAlloc[this.getPos(i,j)]==1.0);
					assert Assert.IIF(this.rig.getHostState(hostIdentifier).hasResource(agentIdentifier), intialAlloc[this.getPos(i,j)]==1.0);
					assert Assert.IIF(!this.rig.getAgentState(agentIdentifier).hasResource(hostIdentifier), intialAlloc[this.getPos(i,j)]==0.);
					assert Assert.IIF(!this.rig.getHostState(hostIdentifier).hasResource(agentIdentifier), intialAlloc[this.getPos(i,j)]==0.);
				}
			}
		}
		if (this.initialSolution!=null){
			for (int i=0; i <this.n; i++){
				for (int j=0; j<this.m; j++){
					final ResourceIdentifier hostIdentifier = this.getHostIdentifier(j);
					final AgentIdentifier agentIdentifier = this.getAgentIdentifier(i);
					assert Assert.IIF(this.rig.getAgentState(agentIdentifier).hasResource(hostIdentifier), this.read(this.initialSolution,i,j)==1.0):agentIdentifier+" "+hostIdentifier+" "+this.printBounds()+"\n"+this.print(this.initialSolution)+"\n"+RessourceAllocationProblem.print(intialAlloc)+"\n"+this.rig;
					assert Assert.IIF(this.rig.getHostState(hostIdentifier).hasResource(agentIdentifier), this.read(this.initialSolution,i,j)==1.0);
					assert Assert.IIF(!this.rig.getAgentState(agentIdentifier).hasResource(hostIdentifier), this.read(this.initialSolution,i,j)==0.);
					assert Assert.IIF(!this.rig.getHostState(hostIdentifier).hasResource(agentIdentifier), this.read(this.initialSolution,i,j)==0.);
				}
			}
		}
		return true;
	}

	@Override
	public void setProblem(final Collection<ReplicationCandidature> concerned) {
		try {
			assert !this.isAgent && this.isHost;
			this.n=concerned.size();
			this.m=1;
			this.concerned= new HashMap<AgentIdentifier, ReplicationCandidature>();
			final Collection<ReplicaState> replicasStates = new ArrayList<ReplicaState>();
			final double[] intialAlloc = new double[this.n*1];

			this.myState=concerned.iterator().next().getResourceInitialState();

			Iterator<ReplicationCandidature> itC = concerned.iterator();
			while (itC.hasNext()){
				final ReplicationCandidature rc = itC.next();
				this.concerned.put(rc.getAgent(), rc);
				assert rc.getResourceInitialState().equals(rc.getResourceInitialState());
				assert rc.getAgentInitialState().getMyMemCharge().equals(
						rc.getAgentResultingState().getMyMemCharge());
				assert rc.getAgentInitialState().getMyProcCharge().equals(
						rc.getAgentResultingState().getMyProcCharge());
				assert rc.getResource().equals(this.myState.getMyAgentIdentifier());

				//				replicasStates.add(rc.getAgentInitialState());
				if (rc.isMatchingCreation()){
					replicasStates.add(rc.getAgentInitialState());
				}else{
					replicasStates.add(rc.getAgentResultingState());
					this.myState = rc.computeResultingState(this.myState);
					assert !this.myState.hasResource(rc.getAgent());
				}
			}


			final ReplicationInstanceGraph rig = new ReplicationInstanceGraph(null);
			rig.setAgents(replicasStates);
			rig.setState(this.myState);
			for (final AgentIdentifier id : rig.getAgentsIdentifier()){
				rig.addAcquaintance(id, this.myState.getMyAgentIdentifier());
			}


			super.setProblem(rig, new ArrayList<AgentIdentifier>());
			this.initiateSolver();

			itC = concerned.iterator();
			while (itC.hasNext()){
				final ReplicationCandidature rc = itC.next();
				//				replicasStates.add(rc.getAgentInitialState());
				if (rc.isMatchingCreation()){
					intialAlloc[this.reverseAgId.get(rc.getAgent())]=0.;
				}else{
					intialAlloc[this.reverseAgId.get(rc.getAgent())]=1.;
				}
			}


			this.initialSolution=this.getInitialAllocAsSolution(intialAlloc);
			assert this.assertIsViable(this.initialSolution);
			assert this.initialSolution!=null;
		} catch (final Exception e) {
			e.printStackTrace();
			throw new RuntimeException();
		}
	}


	@Override
	public Collection<ReplicationCandidature> getBestLocalSolution()
			throws UnsatisfiableException, ExceedLimitException {
		return this.getContractSolution(this.solveProb(true));
	}

	@Override
	public Collection<ReplicationCandidature> getNextLocalSolution() {
		//		System.out.println(myState.getMyAgentIdentifier()+ " serching new solution ");
		SolutionType solution;
		try {
			solution = this.solveProb(false);
		} catch (final UnsatisfiableException e) {
			return new ArrayList<ReplicationCandidature>();
		}

		if (this.isUpgrading(solution)) {
			this.initialSolution=solution;
		}

		return  this.getContractSolution(solution);
	}

	/**
	 * Transforme la solution actuelle du solveur en candidature accepté
	 * @return la liste des candidature de la solution du solveur différentes de l'allcoation courante
	 */
	private Collection<ReplicationCandidature> getContractSolution(final SolutionType sol){
		//		assert this.concerned.size()==sol.length;
		//		assert this.s.isFeasible()!=null && this.s.isFeasible():this.s.isFeasible()+" "+hasNext;
		//		assert hasNext:hasNext;
		final ArrayList<ReplicationCandidature> results = new ArrayList<ReplicationCandidature>();
		assert this.isViable(sol):sol;
		if (!this.isViable(sol)) {
			return results;
		} else {
			if (this.concerned!=null){
				for (int i = 0; i < this.concerned.size(); i++){
					final ReplicationCandidature c = this.concerned.get(this.getAgentIdentifier(i));
					final boolean allocated = this.read(sol,i,0)==1;
					if (c.isMatchingCreation() && allocated || !c.isMatchingCreation() && !allocated) {
						results.add(c);
					}
				}
			} else {
				for (int i = 0; i < this.n; i++){
					for (int j = 0; j < this.m; j++){
						final boolean orignallyAllocated = this.rig.getAgentState(this.getAgentIdentifier(i)).hasResource(this.getHostIdentifier(j));
						assert this.rig.getHostState(this.getHostIdentifier(j)).hasResource(this.getAgentIdentifier(i))==orignallyAllocated;
						final boolean allocatedinSol = this.read(sol,i,j)==1;
						if (orignallyAllocated != allocatedinSol) {
							final ReplicationCandidature c =
									new ReplicationCandidature(this.getMyAgentIdentifier(), this.getHostIdentifier(j), this.getAgentIdentifier(i), allocatedinSol);
							c.setInitialState(this.rig.getHostState(this.getHostIdentifier(j)));
							c.setInitialState(this.rig.getAgentState(this.getAgentIdentifier(i)));
							c.setSocialValue(this.getSocWelfare(sol));
							results.add(c);
						}

					}
				}
			}
			return results;
		}
	}
}


















/******                           *******/
/******  NEGO to Drg Interface    *******/
//	/******                           *******/
//
//	public static DcopReplicationGraph toDrg(Collection<ReplicationCandidature> concerned, SocialChoiceType socialWelfare) {
//		assert concerned.size()<31;
//		try {
//			HashMap<Integer, ReplicationVariable> varMap;
//			Vector<MemFreeConstraint> conList;
//			varMap = new HashMap<Integer, ReplicationVariable>();
//			conList = new Vector<MemFreeConstraint>();
//
//			ReplicationCandidature hostC = concerned.iterator().next();
//			ReplicationVariable hostVar = DCOPFactory.constructVariable(
//					DCOPFactory.identifierToInt(hostC.getResource()),
//					(int)Math.pow(2, concerned.size()),
//					hostC.getResourceInitialState(),
//					socialWelfare);
//			varMap.put(hostVar.id, hostVar);
//			for (ReplicationCandidature c : concerned){
//				ReplicationVariable agentVar;
//				agentVar = DCOPFactory.constructVariable(
//						DCOPFactory.identifierToInt(c.getAgent()),
//						2,
//						c.getAgentInitialState(),
//						socialWelfare);
//
//				assert !varMap.containsKey(DCOPFactory.identifierToInt(c.getAgent()));
//				varMap.put(agentVar.id, agentVar);
//				MemFreeConstraint con = DCOPFactory.constructConstraint(agentVar, hostVar);
//				conList.add(con);
//			}
//
//			DcopReplicationGraph drg = new DcopReplicationGraph(varMap, conList, socialWelfare);
//
//
//			if (DCOPFactory.isClassical()){
//				drg.instanciateConstraintsValues();
//			}
//			return drg;
//		} catch (IncompleteContractException e) {
//			throw new RuntimeException(e);
//		}
//	}
//
//	/**
//	 * Transforme la solution actuelle du solveur en candidature accepté
//	 * @return la liste des candidature de la solution du solveur différentes de l'allcoation courante
//	 */
//	public static Collection<ReplicationCandidature> getContractSolution(
//			DcopReplicationGraph drg, Collection<ReplicationCandidature> concerned,HashMap<Integer,Integer> sol){
//		assert concerned.size()==sol.size();
//		//		assert this.s.isFeasible()!=null && this.s.isFeasible():this.s.isFeasible()+" "+hasNext;
//		//		assert hasNext:hasNext;
//		final ArrayList<ReplicationCandidature> results = new ArrayList<ReplicationCandidature>();
//		AgentIdentifier host = concerned.iterator().next().getResource();
//		for (ReplicationCandidature c : concerned){
//			ReplicationVariable agent = drg.getVar(DCOPFactory.identifierToInt(c.getAgent()));
//			boolean allocated = agent.hasAllocatedRessource(host, sol.get(agent.id));
//			if (c.isMatchingCreation() && allocated || !c.isMatchingCreation() && !allocated) {
//				results.add(c);
//			}
//		}
//		return results;
//	}


//}



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