package frameworks.faulttolerance.solver;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import com.ziena.knitro.KnitroJava;

import dima.basicagentcomponents.AgentIdentifier;
import dima.introspectionbasedagents.modules.faults.Assert;
import dima.introspectionbasedagents.modules.mappedcollections.HashedHashList;
import dima.introspectionbasedagents.modules.mappedcollections.HashedHashSet;
import dima.introspectionbasedagents.services.deployment.server.HostIdentifier;

import frameworks.faulttolerance.dcop.DCOPFactory;
import frameworks.faulttolerance.dcop.DcopSolver;
import frameworks.faulttolerance.dcop.dcop.DcopReplicationGraph;
import frameworks.faulttolerance.dcop.dcop.MemFreeConstraint;
import frameworks.faulttolerance.dcop.dcop.ReplicationVariable;
import frameworks.faulttolerance.experimentation.ReplicationGraph;
import frameworks.faulttolerance.experimentation.ReplicationInstanceGraph;
import frameworks.faulttolerance.negotiatingagent.HostState;
import frameworks.faulttolerance.negotiatingagent.ReplicaState;
import frameworks.faulttolerance.negotiatingagent.ReplicationCandidature;
import frameworks.faulttolerance.negotiatingagent.ReplicationSocialOptimisation;
import frameworks.negotiation.contracts.ResourceIdentifier;
import frameworks.negotiation.contracts.AbstractContractTransition.IncompleteContractException;
import frameworks.negotiation.exploration.ResourceAllocationSolver;
import frameworks.negotiation.exploration.Solver;
import frameworks.negotiation.rationality.AgentState;
import frameworks.negotiation.rationality.SocialChoiceFunction.SocialChoiceType;

public abstract class ResourceAllocationProblem 
implements Solver, DcopSolver, ResourceAllocationSolver<ReplicationCandidature, HostState>{

	protected final SocialChoiceType socialChoice;
	protected final boolean isAgent;	
	protected final boolean isHost;

	public ResourceAllocationProblem(SocialChoiceType socialChoice,
			boolean isAgent, boolean isHost) {
		this.socialChoice = socialChoice;
		this.isAgent = isAgent;
		this.isHost = isHost;
	}

	/******                    *******/
	/******  Problem Interface    *******/ 
	/******                    *******/

	protected int n;
	protected int m;

	private AgentIdentifier[] agId;
	private ResourceIdentifier[] hostId;

	private double[] agCrit;
	private double[] hostLambda;

	private double[] agProcCharge;
	private double[] agMemCharge;
	private double[] hostProcMax;
	private double[] hostMemMax;

	private double[] agInitLambda;
	private double[] hostInitProcCharge;
	private double[] hostInitMemCharge;

	private ReplicationGraph rig;
	private Collection<AgentIdentifier> fixedVar=new ArrayList<AgentIdentifier>();
	private double intialSocialWelfare = 0.;

	public void setProblem(ReplicationGraph rig) {

		this.rig = rig;

		List<ReplicaState> agentStates = new ArrayList<ReplicaState>(rig.getAgentStates());
		List<HostState> hostsStates = new ArrayList<HostState>(rig.getHostsStates());
		n=agentStates.size();
		m=hostsStates.size();
		assert n>0;
		assert m>0;
		assert Assert.Imply(isLocal(), fixedVar.isEmpty());

		agId= new AgentIdentifier[n];
		hostId= new ResourceIdentifier[m] ;

		agCrit= new double[n];
		hostLambda= new double[n];

		agProcCharge= new double[n];
		agMemCharge= new double[n];
		hostProcMax= new double[m];
		hostMemMax= new double[m];

		if (isLocal()){
			agInitLambda= new double[n];
			hostInitProcCharge= new double[m];
			hostInitMemCharge= new double[m];
		}

		for (int i = 0; i < n; i++){
			assert agentStates.get(i)!=null;
			agId[i]=agentStates.get(i).getMyAgentIdentifier();
			agCrit[i]=agentStates.get(i).getMyCriticity();
			agProcCharge[i]=agentStates.get(i).getMyProcCharge();
			agMemCharge[i]=agentStates.get(i).getMyMemCharge();
			if (isLocal()){
				assert m==1;
				ReplicaState neOS=agentStates.get(i).allocate(myState, false);
				agInitLambda[i]=neOS.getMyFailureProb();
			}			
		}

		for (int j = 0; j < m; j++){
			hostId[j]=hostsStates.get(j).getMyAgentIdentifier();
			hostLambda[j]=hostsStates.get(j).getFailureProb();
			hostProcMax[j]=hostsStates.get(j).getProcChargeMax();
			hostMemMax[j]=hostsStates.get(j).getMemChargeMax();
			if (isLocal()){
				assert m==1;
				HostState neoS=hostsStates.get(j);
				for (ReplicaState rep : agentStates)
					neoS = neoS.allocate(rep,false);
						hostInitMemCharge[j]=neoS.getCurrentMemCharge();
						hostInitProcCharge[j]=neoS.getCurrentProcCharge();
			}			
		}

		//		assert this.socialChoice==rig.getSocialWelfare();
		//		System.out.println(hosts);
		//		System.out.println(agents);
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

	/*
	 * Accessor
	 */


	protected AgentIdentifier getAgentIdentifier(int i) {
		return agId[i];
	}

	protected ResourceIdentifier getHostIdentifier(int j) {
		return hostId[j];
	}

	/*
	 * 
	 */

	protected double getAgentCriticality(int i) {
		return agCrit[i];
	}

	protected double getHostFailureProbability(int j) {
		return hostLambda[j];
	}

	/*
	 * 
	 */

	protected double getAgentMemorycharge(int i) {
		return agMemCharge[i];
	}

	protected double getAgentProcessorCharge(int i) {
		return agProcCharge[i];
	}

	protected double getHostMaxMemory(int j) {
		return hostMemMax[j];
	}

	protected double getHostMaxProcessor(int j) {
		return hostProcMax[j];
	}

	/*
	 * 
	 */

	protected double getHostCurrentMemory(int j) {
		if (!isLocal())
			return 0.;
		else
			return hostInitMemCharge[j];
	}
	protected double getHostCurrentProcessor(int j) {
		if (!isLocal())
			return 0.;
		else
			return hostInitProcCharge[j];
	}

	protected double getAgentInitialFailureProbability(int i) {
		if (!isLocal())
			return 1.;
		else
			return agInitLambda[i];
	}

	/*
	 * Variables
	 */

	protected int getAllocationLowerBound(int agent_i, int host_j) throws UnsatisfiableException{
		if (isLocal()){

			assert rig.getAccessibleHosts(getAgentIdentifier(agent_i)).contains(getHostIdentifier(host_j))
			&& rig.getAccessibleAgents(getHostIdentifier(host_j)).contains(getAgentIdentifier(agent_i));
			return 0;

		} else if (rig.getAccessibleAgents(getHostIdentifier(host_j)).contains(getAgentIdentifier(agent_i))){

			assert rig.getAccessibleHosts(getAgentIdentifier(agent_i)).contains(getHostIdentifier(host_j));

			if (!fixedVar.contains(getAgentIdentifier(agent_i)) && !fixedVar.contains(getHostIdentifier(host_j))){
				return 0;

			} else if (fixedVar.contains(getAgentIdentifier(agent_i)) && fixedVar.contains(getHostIdentifier(host_j))){
				if (
						(rig.getHostState(getHostIdentifier(host_j)).hasResource(getAgentIdentifier(agent_i))&& 
								rig.getAgentState(getAgentIdentifier(agent_i)).hasResource(getHostIdentifier(host_j)))
								||
								(!rig.getHostState(getHostIdentifier(host_j)).hasResource(getAgentIdentifier(agent_i)) && 
										!rig.getAgentState(getAgentIdentifier(agent_i)).hasResource(getHostIdentifier(host_j)))){
					throw new UnsatisfiableException();
				} else
					return rig.getHostState(getHostIdentifier(host_j)).hasResource(getAgentIdentifier(agent_i))?1:0;

			}else if (fixedVar.contains(getAgentIdentifier(agent_i))){
				return rig.getAgentState(getAgentIdentifier(agent_i)).hasResource(getHostIdentifier(host_j))?1:0;

			} else {//	
				assert fixedVar.contains(getHostIdentifier(host_j));
				return rig.getHostState(getHostIdentifier(host_j)).hasResource(getAgentIdentifier(agent_i))?1:0;
			}
		} else {
			assert !rig.getAccessibleHosts(getAgentIdentifier(agent_i)).contains(getHostIdentifier(host_j));
			return 0;
		}
	}

	protected int getAllocationUpperBound(int agent_i, int host_j) throws UnsatisfiableException{
		if (isLocal()){

			assert rig.getAccessibleHosts(getAgentIdentifier(agent_i)).contains(getHostIdentifier(host_j))
			&& rig.getAccessibleAgents(getHostIdentifier(host_j)).contains(getAgentIdentifier(agent_i));
			return 1;

		} else if (rig.getAccessibleAgents(getHostIdentifier(host_j)).contains(getAgentIdentifier(agent_i))){

			assert rig.getAccessibleHosts(getAgentIdentifier(agent_i)).contains(getHostIdentifier(host_j));

			if (!fixedVar.contains(getAgentIdentifier(agent_i)) && !fixedVar.contains(getHostIdentifier(host_j))){
				return 1;

			} else if (fixedVar.contains(getAgentIdentifier(agent_i)) && fixedVar.contains(getHostIdentifier(host_j))){
				if (
						(rig.getHostState(getHostIdentifier(host_j)).hasResource(getAgentIdentifier(agent_i))&& 
								rig.getAgentState(getAgentIdentifier(agent_i)).hasResource(getHostIdentifier(host_j)))
								||
								(!rig.getHostState(getHostIdentifier(host_j)).hasResource(getAgentIdentifier(agent_i)) && 
										!rig.getAgentState(getAgentIdentifier(agent_i)).hasResource(getHostIdentifier(host_j)))){
					throw new UnsatisfiableException();
				} else
					return rig.getHostState(getHostIdentifier(host_j)).hasResource(getAgentIdentifier(agent_i))?1:0;

			}else if (fixedVar.contains(getAgentIdentifier(agent_i))){
				return rig.getAgentState(getAgentIdentifier(agent_i)).hasResource(getHostIdentifier(host_j))?1:0;

			} else {//	
				assert fixedVar.contains(getHostIdentifier(host_j));
				return rig.getHostState(getHostIdentifier(host_j)).hasResource(getAgentIdentifier(agent_i))?1:0;
			}
		} else {
			assert !rig.getAccessibleHosts(getAgentIdentifier(agent_i)).contains(getHostIdentifier(host_j));
			return 0;
		}
	}

	protected int getPos(int agent_i, int host_j) {
		return agent_i*m+host_j;
	}

	/*
	 * Objectives & Constraints
	 */

	protected double getSocWelfare(double[] daX) {
		//objective
		double f;
		switch (socialChoice){
		case Utility :
			f = 0;
			break;
		case Nash :
			f=1;
			break;
		case Leximin :
			f=Double.MAX_VALUE;
			break;
		default :
			throw new RuntimeException();
		}

		for (int agent_i = 0; agent_i < n; agent_i++){
			double relia=getIndividualWelfare(daX, agent_i);
			switch (socialChoice){
			case Utility :
				f += relia;
				break;
			case Nash :
				f *= relia;
				break;
			case Leximin :
				f =Math.min(f, relia);
				break;
			default :
				throw new RuntimeException();
			}
		}
		return f;
	}

	protected double getAgentSurvie(double[] daX, int agent_i) {
		double result=0;
		for (int host_j=0; host_j < m; host_j++){
			result+=daX[getPos(agent_i,host_j)];
			//			assert result<=m:result+" "+m+" "+print(daX);
		}
		return result;
	}

	protected double getMemCharge(double[] daX, int host_j) {
		double c=getHostCurrentMemory(host_j);
		for (int agent_i=0; agent_i < n; agent_i++){
			//			assert daX[getPos(agent_i,host_j)]==1. || daX[getPos(agent_i,host_j)]==0.:daX[getPos(agent_i,host_j)];
			c+=daX[getPos(agent_i,host_j)]*getAgentMemorycharge(agent_i);
		}
		return c;
	}

	protected double getProcCharge(double[] daX, int host_j) {
		double c=getHostCurrentProcessor(host_j);
		for (int agent_i=0; agent_i < n; agent_i++){
			//			assert daX[getPos(agent_i,host_j)]==1. || daX[getPos(agent_i,host_j)]==0.:daX[getPos(agent_i,host_j)];
			c+=daX[getPos(agent_i,host_j)]*getAgentProcessorCharge(agent_i);
		}
		return c;
	}

	private double getDispo(double[] daX, int agent_i) {
		double failProb = getAgentInitialFailureProbability(agent_i);
		for (int host_j = 0; host_j < m; host_j++){
			//					assert daX[getPos(agent_i,host_j)]==1. || daX[getPos(agent_i,host_j)]==0.:daX[getPos(agent_i,host_j)];
			failProb *= Math.pow(
					getHostFailureProbability(host_j),
					daX[getPos(agent_i,host_j)]);	
		}	
		return 1 - failProb;
	}

	private double getIndividualWelfare(double[] daX, int agent_i) {
		return ReplicationSocialOptimisation.getReliability(getDispo(daX, agent_i), getAgentCriticality(agent_i), socialChoice);
	}

	/*
	 * Derivative
	 */

	protected double getDRondSocWelfare(double[] daX, int agent_i, int host_j) {
		double r;
		if (socialChoice.equals(SocialChoiceType.Utility)){
			r=-Math.log(getHostFailureProbability(host_j))*getIndividualWelfare(daX,agent_i);
		}  else if (socialChoice.equals(SocialChoiceType.Nash)) {
			r=-Math.log(getHostFailureProbability(host_j));
			for (int j = 0; j < m; j++){
				r *= Math.pow(
						getHostFailureProbability(j),
						daX[getPos(agent_i,j)]);
			}
			for (int i = 0; i < n; i++){
				if (i!=agent_i){
					r*=getIndividualWelfare(daX,i);
				}
			}
		}  else {
			assert (socialChoice.equals(SocialChoiceType.Leximin));
			throw new RuntimeException();
		}
		return r;
	}

	protected double getDRondDeuxSocWelfare(double[] daX, int agent_i,
			int host_j, int agent_ip, int host_jp) {
		double r;
		if (socialChoice.equals(SocialChoiceType.Utility)){
			assert agent_i==agent_ip;
			r=-Math.log(getHostFailureProbability(host_jp))*Math.log(getHostFailureProbability(host_j))*getIndividualWelfare(daX,agent_i);
		}  else if (socialChoice.equals(SocialChoiceType.Nash)) {
			r=-Math.log(getHostFailureProbability(host_j));
			for (int j = 0; j < m; j++){
				r *= Math.pow(
						getHostFailureProbability(j),
						daX[getPos(agent_i,j)]);
			}			
			r*=-Math.log(getHostFailureProbability(host_jp));
			for (int j = 0; j < m; j++){
				r *= Math.pow(
						getHostFailureProbability(j),
						daX[getPos(agent_ip,j)]);
			}
			for (int i = 0; i < n; i++){
				if (i!=agent_i && i!=agent_ip){
					r*=getIndividualWelfare(daX,i);
				}
			}

		}  else {
			assert (socialChoice.equals(SocialChoiceType.Leximin));
			throw new RuntimeException();
		}
		return r;
	}

	protected double getDRondSurvie(int consideredAgent, int agent_i, int host_j){
		assert consideredAgent==agent_i;
		if (consideredAgent==agent_i)
			return 1;
		else
			return 0;
	}

	protected double getDRondMemory(int consideredHost, int agent_i, int host_j){
		assert consideredHost==host_j;
		if (consideredHost==host_j)
			return getAgentMemorycharge(agent_i);
		else
			return 0;
	}

	protected double getDRondProcessor(int consideredHost, int agent_i, int host_j){
		assert consideredHost==host_j;
		if (consideredHost==host_j)
			return getAgentProcessorCharge(agent_i);
		else
			return 0;
	}
	/******                    *******/
	/******  DCOP Interface    *******/ 
	/******                    *******/

	DcopReplicationGraph drg;

	@Override
	public HashMap<Integer, Integer> solve(DcopReplicationGraph drg){
		initiate(drg);
		try {
			initiateSolver();
			return getDCOPSolution(solveProb(true));
		} catch (UnsatisfiableException e) {
			return getFailedSolution(drg);
		}
	}

	private HashMap<Integer, Integer> getDCOPSolution(double[] solvSol) {
		if (solvSol==null)
			return getFailedSolution(drg);
		HashedHashSet<AgentIdentifier,AgentIdentifier>  map = new HashedHashSet<AgentIdentifier,AgentIdentifier> ();

		for (int i = 0; i < n; i++){
			for (int j=0; j < m; j++){
				if(!(solvSol[getPos(i,j)]==1.0 || solvSol[getPos(i,j)]==0.0))
					return getFailedSolution(drg);
				if (solvSol[getPos(i,j)]==1.0){
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

	private void initiate(DcopReplicationGraph drg) {

		this.drg=drg;

		for (ReplicationVariable var : drg.varMap.values()){
			if (var.fixed)
				fixedVar.add(var.getAgentIdentifier());
		}
		setProblem(drg);
		//		System.out.println("Agents are \n"+Arrays.asList(agents));
		//		System.out.println("Hosts  Are \n"+Arrays.asList(hosts));
		//		System.out.println("fixed are \n"+fixedVar);
	}

	public static HashMap<Integer, Integer> getFailedSolution(DcopReplicationGraph drg){

		HashMap<Integer, Integer> result = new HashMap<Integer, Integer>();
		for (Integer id : drg.varMap.keySet()){
			result.put(id,0);
		}
		return result;
	}

	/******                    *******/
	/******  NEGO Interface    *******/ 
	/******                    *******/

	HostState myState;
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
			double[] initialAlloc = new double[n];

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
					initialAlloc[i]=0.;
				}else{
					replicasStates.add(rc.getAgentResultingState());
					initialAlloc[i]=1.;
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
			intialSocialWelfare=getSocWelfare(initialAlloc);
			initiateSolver();
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
		System.out.println(myState.getMyAgentIdentifier()+ " serching new solution ");
		double[] solution;
		try {
			solution = solveProb(false);
		} catch (UnsatisfiableException e) {
			return new ArrayList<ReplicationCandidature>();
		}
		double newSocWelf = getSocWelfare(solution);
		assert newSocWelf>=intialSocialWelfare;
		intialSocialWelfare=newSocWelf;
		return  getContractSolution(solution);
	}

	protected boolean isLocal() {
		assert Assert.Imply(!isAgent && isHost,m==1):isAgent +" "+isHost+" "+m;
		return !isAgent && isHost;
	}

	protected double getMinimalWelfare(){
		return intialSocialWelfare;
	}

	/**
	 * Transforme la solution actuelle du solveur en candidature accepté
	 * @return la liste des candidature de la solution du solveur différentes de l'allcoation courante
	 */
	private Collection<ReplicationCandidature> getContractSolution(double[] sol){
		assert this.concerned.size()==sol.length;
		//		assert this.s.isFeasible()!=null && this.s.isFeasible():this.s.isFeasible()+" "+hasNext;
		//		assert hasNext:hasNext;
		final ArrayList<ReplicationCandidature> results = new ArrayList<ReplicationCandidature>();

		for (int i = 0; i < sol.length; i++){
			ReplicationCandidature c = concerned.get(getAgentIdentifier(i));
			boolean allocated = sol[i]==1; 
			if (c.isMatchingCreation() && allocated || !c.isMatchingCreation() && !allocated) {
				results.add(c);
			}
		}
		return results;
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

	/******                    *******/
	/******  Solver Interface  *******/ 
	/**
	 * @throws UnsatisfiableException ****                    *******/


	protected abstract void initiateSolver() throws UnsatisfiableException ;
	protected abstract double[] solveProb(boolean opt) throws UnsatisfiableException;


	/******                    *******/
	/******                    *******/ 
	/******                    *******/

	protected double[] getBestTriviaSol(List<ReplicaState> best, int accNumb) {
		List<ReplicaState> agsToSort = new ArrayList<ReplicaState>(rig.getAgentStates());
		List<ReplicaState> agentStates = new ArrayList<ReplicaState>(rig.getAgentStates());
		Comparator<ReplicaState> c = new Comparator<ReplicaState>() {

			@Override
			public int compare(ReplicaState o1, ReplicaState o2) {
				return o1.getMyCriticity().compareTo(o2.getMyCriticity());
			}
		}; 
		Collections.sort(agsToSort, Collections.reverseOrder(c));
		//		System.out.println(ags);
		for (int i =0; i < accNumb; i++){
			best.add(agsToSort.get(i));
		}

		double[] bestPossible = new double[n*m];
		for (int i = 0; i < n*m; i++){
			if (best.contains(agentStates.get(i)))
				bestPossible[i]=1.;
			else
				bestPossible[i]=0.;
		}
		return bestPossible;
	}

	/******                    *******/
	/******     Routines        *******/ 
	/******                    *******/

	public static String asMatrix(ArrayList vect, int nbCol) {
		String result ="[ ";
		for (int i = 0; i < vect.size(); i++){
			if (i%nbCol==0)
				result+="\n ";
			result+=vect.get(i)+" ";
		}
		return result;
	}

	public static String print(int[] vect) {
		String result ="[ ";
		for (int i = 0; i < vect.length; i++){
			result+=vect[i]+" ";
		}
		result+="]";
		return result;
	}

	public static String print(double[] vect) {
		String result ="[ ";
		for (int i = 0; i < vect.length; i++){
			result+=vect[i]+" ";
		}
		result+="]";
		return result;
	}

	public static String print(ReplicaState[] vect) {
		String result ="[ ";
		for (int i = 0; i < vect.length; i++){
			result+=vect[i].getMyAgentIdentifier()+" ";
		}
		result+="]";
		return result;
	}



}
