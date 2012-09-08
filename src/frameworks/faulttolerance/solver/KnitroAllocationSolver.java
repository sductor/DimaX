package frameworks.faulttolerance.solver;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import com.ziena.knitro.KnitroJava;

import dima.basicagentcomponents.AgentIdentifier;
import dima.introspectionbasedagents.modules.distribution.NormalLaw.DispersionSymbolicValue;
import dima.introspectionbasedagents.modules.mappedcollections.HashedHashList;
import dima.introspectionbasedagents.modules.mappedcollections.HashedHashSet;
import dima.introspectionbasedagents.services.deployment.server.HostIdentifier;

import frameworks.experimentation.IfailedException;
import frameworks.faulttolerance.dcop.DCOPFactory;
import frameworks.faulttolerance.dcop.DcopSolver;
import frameworks.faulttolerance.dcop.dcop.DcopReplicationGraph;
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

public class KnitroAllocationSolver 
implements Solver, DcopSolver, ResourceAllocationSolver<ReplicationCandidature, HostState>{

	int n,m;
	HostState[] hosts;
	ReplicaState[] agents;
	private ReplicationGraph rig;
	HashedHashSet<AgentIdentifier,AgentIdentifier> fixedVar;

	SocialChoiceType socialChoice;
	boolean isAgent;
	boolean isHost;

	public KnitroAllocationSolver(
			SocialChoiceType socialWelfare,
			boolean isAgent,
			boolean isHost) {
		socialChoice=socialWelfare;
		this.isAgent=isAgent;
		this.isHost=isHost;
	}


	/******                    *******/
	/******  SOLVER  Interface *******/ 
	/******                    *******/

	double[] bestSolution;
	Collection<double[]> allSolutions;
	int numberOfSolutionLimit;

	@Override
	public void computeBestSolution() {
		try {
			initiate();
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
		bestSolution =solveProb();

		//---- BE CERTAIN THE NATIVE OBJECT INSTANCE IS DESTROYED.
		solver.destroyInstance();
	}

	@Override
	public boolean hasNext() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void addNextSolution() {
		// TODO Auto-generated method stub

	}

	@Override
	public void addAllSolution() {
		// TODO Auto-generated method stub

	}

	@Override
	public void setTimeLimit(int millisec) {
		solver.setIntParamByName("mip_maxtime_cpu", millisec/1000);

	}

	@Override
	public void setNumberOfSolutionLimit(int numberOfSolution) {
		this.numberOfSolutionLimit=numberOfSolution;
	}

	//
	//
	//

	public void setProblem(ReplicationGraph rig, 
			HashedHashSet<AgentIdentifier,AgentIdentifier> fixedVar,
			boolean clearStates){
		this.rig=rig;
		List<ReplicaState> agentStates = new ArrayList(rig.getAgentStates());
		List<HostState> hostsStates = new ArrayList(rig.getHostsStates());
		n=agentStates.size();
		m=hostsStates.size();
		this.agents=new ReplicaState[n];
		for (int i = 0; i < n; i++){
			if (clearStates && !fixedVar.containsKey(agentStates.get(i).getMyAgentIdentifier()))
				this.agents[i]=agentStates.get(i).freeAllResources();
			else
				this.agents[i]=agentStates.get(i);				
		}
		this.hosts=new HostState[m];
		for (int j = 0; j < m; j++){
			if (clearStates && !fixedVar.containsKey(hostsStates.get(j).getMyAgentIdentifier()))
				this.hosts[j]=hostsStates.get(j).freeAllResources();
			else
				this.hosts[j]=hostsStates.get(j);
		}

		assert this.socialChoice==rig.getSocialWelfare();
		this.fixedVar=fixedVar;

		//		System.out.println(hosts);
		//		System.out.println(agents);
	}

	public HashMap<AgentIdentifier,AgentState> getAllocation(double[] solvSol){
		HashMap<AgentIdentifier,AgentState> result = new HashMap<AgentIdentifier, AgentState>();
		for (int i = 0; i < n; i++){
			result.put(agents[i].getMyAgentIdentifier(), agents[i]);
		}
		for (int j=0; j < m; j++){
			result.put(hosts[j].getMyAgentIdentifier(), hosts[j]);
		}

		for (int i = 0; i < n; i++){
			for (int j=0; j < m; j++){
				assert solvSol[getPos(i,j)]==1.0 || solvSol[getPos(i,j)]==0.0;
				result.put(getAgent(i), ((ReplicaState)result.get(getAgent(i))).allocate(hosts[j],solvSol[getPos(i,j)]==1.0));
				result.put(getHost(j), ((HostState)result.get(getHost(j))).allocate(agents[i],solvSol[getPos(i,j)]==1.0));
			}
		}
		return result;
	}


	private void configureKnitro(boolean debug){

		//---- SET KNITRO PARAMETERS FOR MINLP SOLUTION.
		if (solver.setIntParamByName ("mip_method", 1) == false)
		{
			System.err.println ("Error setting parameter 'mip_method'");
			return;
		}
		if (solver.setIntParamByName ("algorithm", 0) == false)
		{
			//			0			 let KNITRO choose the algorithm
			//			1			 Interior/Direct (barrier) algorithm
			//			2			 Interior/CG (barrier) algorithm
			//			3			 Active Set algorithm
			//			5			 Run multiple algorithms

			System.err.println ("Error setting parameter 'algorithm'");
			return ;
		}        
		if (solver.setIntParamByName ("hessian_no_f", 1) == false)
		{
			System.err.println ("Error setting parameter 'hessian_no_f'");
			return ;
		}


		if (solver.setIntParamByName ("bar_feasible", 2) == false)
		{
			//			 0			  no special emphasis on feasibility
			//			 1			  iterates must honor inequalities
			//			 2			  emphasize first getting feasible before optimizing
			//			 3			  implement both options 1 and 2 above

			System.err.println ("Error setting parameter 'bar_feasible'");
			return ;
		}



		if (solver.setIntParamByName ("mip_terminate", 0) == false)
		{
			//				0				 terminate at optimal solutionisGlobal()?0:1
			//				1				  terminate at first integer feasible solution

			System.err.println ("Error setting parameter 'mip_terminate'");
			return ;
		}

		if (debug){
			if (solver.setIntParamByName("debug", 1) == false)
			{
				System.err.println ("Error setting parameter 'debug'");
				return;
			}
			if (solver.setIntParamByName("mip_debug", 1) == false)
			{
				System.err.println ("Error setting parameter 'mip_debug'");
				return;
			}
			//			if (solver.setIntParamByName ("mip_outlev", 1) == false)
			//			{
			//				System.err.println ("Error setting parameter 'mip_outlev'");
			//				return ;
			//			}     
			if (solver.setIntParamByName ("outlev", 6) == false)
			{
				System.err.println ("Error setting parameter 'outlev'");
				return ;
			}
			if (solver.setIntParamByName ("mip_outinterval", 1) == false)
			{
				System.err.println ("Error setting parameter 'mip_outinterval'");
				return ;
			}
			if (solver.setIntParamByName ("outmode", 2) == false)
			{
				//				0				 screen
				//				1				 file
				//2 both
				System.err.println ("Error setting parameter 'outmode'");
				return ;
			}
			if (solver.setCharParamByName ("outdir", "/droop/Boulot/workspace/DimaX/log/") == false)
			{
				System.err.println ("Error setting parameter 'mip_terminate'");
				return ;
			}
		}
	}


	/******                    *******/
	/******  DCOP Interface    *******/ 
	/******                    *******/

	public void initiate(DcopReplicationGraph drg) {

		this.rig=drg;


		HashedHashSet<AgentIdentifier,AgentIdentifier> fixedVar = new HashedHashSet<AgentIdentifier,AgentIdentifier>();
		for (ReplicationVariable var : drg.varMap.values()){
			if (var.fixed)
				fixedVar.put(var.getAgentIdentifier(), (Set<AgentIdentifier>) var.getAllocatedRessources());
		}
		setProblem(rig, fixedVar, true);
		System.out.println("Agents are \n"+Arrays.asList(agents));
		System.out.println("Hosts  Are \n"+Arrays.asList(hosts));
		System.out.println("fixed are \n"+fixedVar);
	}

	private HashMap<Integer, Integer> getSolution(double[] solvSol) {
		if (solvSol==null)
			return getFailedSolution();
		HashedHashSet<AgentIdentifier,AgentIdentifier>  map = new HashedHashSet<AgentIdentifier,AgentIdentifier> ();

		for (int i = 0; i < n; i++){
			for (int j=0; j < m; j++){
				if(!(solvSol[getPos(i,j)]==1.0 || solvSol[getPos(i,j)]==0.0))
					return getFailedSolution();
				if (solvSol[getPos(i,j)]==1.0){
					map.add(getAgent(i), getHost(j));
					map.add(getHost(j), getAgent(i));
				}
			}
		}		

		HashMap<Integer, Integer> result = new HashMap<Integer, Integer>();
		for (Integer id : ((DcopReplicationGraph)rig).varMap.keySet()){
			AgentIdentifier agid = DCOPFactory.intToIdentifier(id);
			result.put(id,((DcopReplicationGraph)rig).varMap.get(id).getValue(map.get(agid)));
		}
		return result;
	}

	private HashMap<Integer, Integer> getFailedSolution(){

		HashMap<Integer, Integer> result = new HashMap<Integer, Integer>();
		for (Integer id : ((DcopReplicationGraph)rig).varMap.keySet()){
			AgentIdentifier agid = DCOPFactory.intToIdentifier(id);
			result.put(id,0);
		}
		return result;
	}

	@Override
	public HashMap<Integer, Integer> solve(DcopReplicationGraph drg) throws UnsatisfiableException,
	ExceedLimitException{
		initiate(drg);
		computeBestSolution();
		return getSolution(bestSolution);
	}



	/******                    *******/
	/******  NEGO Interface    *******/ 
	/******                    *******/

	HostState myState;
	Collection<ReplicationCandidature> concerned;
	@Override
	public void initiate(Collection<ReplicationCandidature> concerned) {
		try {
			Collection<ReplicaState> replicasStates = new ArrayList<ReplicaState>();
			Collection<HostState> hostsStates = new ArrayList<HostState>();
			HashedHashSet<AgentIdentifier, ResourceIdentifier> accHosts = new HashedHashSet<AgentIdentifier, ResourceIdentifier>();
			this.concerned=concerned;
			this.myState=concerned.iterator().next().getResourceInitialState();
			Iterator<ReplicationCandidature> itC = concerned.iterator();
			while (itC.hasNext()){
				ReplicationCandidature rc = itC.next();
				assert rc.getResourceInitialState().equals(rc.getResourceInitialState());
				assert rc.getAgentInitialState().getMyMemCharge().equals(
						rc.getAgentResultingState().getMyMemCharge());
				assert rc.getAgentInitialState().getMyProcCharge().equals(
						rc.getAgentResultingState().getMyProcCharge());

				if (rc.isMatchingCreation())
					replicasStates.add(rc.getAgentInitialState());
				else
					replicasStates.add(rc.getAgentResultingState());

				accHosts.add(rc.getAgent(), myState.getMyAgentIdentifier());
			}		

			ReplicationInstanceGraph rig = new ReplicationInstanceGraph(null);
			rig.setAgents(replicasStates);
			rig.setHosts(hostsStates);
			for (AgentIdentifier id : rig.getAgentsIdentifier()){
				for (ResourceIdentifier id2 : accHosts.get(id))
					rig.addAcquaintance(id, id2);
			}
			setProblem(rig, new HashedHashSet<AgentIdentifier, AgentIdentifier>(),false);
		} catch (IncompleteContractException e) {
			e.printStackTrace();
			throw new RuntimeException();
		}
	}
	@Override
	public Collection<ReplicationCandidature> computeBestLocalSolution()
			throws UnsatisfiableException, ExceedLimitException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Collection<Collection<ReplicationCandidature>> computeAllLocalSolutions() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Collection<ReplicationCandidature> getNextSolution() {
		// TODO Auto-generated method stub
		return null;
	}

	/******                   *******/
	/******  SOLVER           *******/ 
	/******                   *******/

	KnitroJava solver;
	int numVar, numConstraint;
	int jvarNonNul, hvarNonNul;


	private void initiate() throws Exception{
		numVar = n*m;
		solver = new KnitroJava();
		configureKnitro(true);

		double[] varlb = new double[numVar];
		double[] varub = new double[numVar];
		int[] vartype = new int[numVar];
		int var=0;	
		for (int i = 0; i < n; i++){
			for (int j = 0; j < m; j++){
				if (fixedVar.containsKey(getAgent(i))){
					if (fixedVar.get(getAgent(i)).contains(getHost(j))){
						varlb[var]=1.;
						varub[var]=1.;
					} else {
						varlb[var]=0.;
						varub[var]=0.;						
					}
				}	else if (fixedVar.containsKey(getHost(j))){
					if (fixedVar.get(getHost(j)).contains(getAgent(i))){
						varlb[var]=1.;
						varub[var]=1.;
					} else {
						varlb[var]=0.;
						varub[var]=0.;						
					}					
				} else {
					varlb[var]=0.;
					varub[var]=rig.getAccessibleHosts(agents[i].getMyAgentIdentifier()).contains(hosts[j].getMyAgentIdentifier())?1.:0.;
				}
				vartype[var]=KnitroJava.KTR_VARTYPE_INTEGER;
				var++;
			}
		}

		//constraints 

		setNumConstraint();

		double[] conslb = new double[numConstraint];
		double[] consub = new double[numConstraint];
		if (isAgent){
			for (int agent_i = 0; agent_i < n; agent_i++){
				conslb[getAgentConstraintPos(agent_i)] = 1.;
				consub[getAgentConstraintPos(agent_i)] = KnitroJava.KTR_INFBOUND;//m;
			}
		}

		if (isHost){
			for (int host_j = 0; host_j < m; host_j++){		
				conslb[getHostConstraintPos(host_j, true)] = 0.;
				conslb[getHostConstraintPos(host_j, false)] = 0.;
				consub[getHostConstraintPos(host_j, true)] = hosts[host_j].getMemChargeMax();
				consub[getHostConstraintPos(host_j, false)] = hosts[host_j].getProcChargeMax();
			}
		}

		int[] consType= new int[numConstraint];
		int[] consFnType= new int[numConstraint];
		for (int cons = 0; cons < numConstraint; cons++){
			consType[cons]=KnitroJava.KTR_CONTYPE_LINEAR;
			consFnType[cons]=KnitroJava.KTR_FNTYPE_CONVEX;
		}

		//jacobienne
		setNumNonNulJAc();

		int[] jacIndexVars = new int[jvarNonNul];
		int[] jacIndexCons = new int[jvarNonNul];
		//on considere trois vecteurs et on 'ecrase' verticalement : 
		//il ne s'agit pas d'une lecture ligne d'abore mais d'une porjection sur laxe des vare pour chaquer type de contrainte
		for (int i = 0; i < n; i++){
			for (int j = 0; j < m; j++){

				if (isHost){
					//mem de j : la case getPos(i,j) suivant les case de survie correspond à la contrainte de mem du jeme hote
					jacIndexVars[getJacChargeConstraintPos(i, j,true)]=getPos(i, j);
					jacIndexCons[getJacChargeConstraintPos(i, j,true)]=getHostConstraintPos(j, true);

					//proc de j :la case getPos(i,j) suivant les case de survie et de mémoire correspond à la contrainte de proc du jeme hote
					jacIndexVars[getJacChargeConstraintPos(i, j,false)]=getPos(i, j);
					jacIndexCons[getJacChargeConstraintPos(i, j,false)]=getHostConstraintPos(j, false);
				}

				if (isAgent){
					//survie : la case getPos(i,j) correspond a la survie du ieme agent
					jacIndexVars[getJacSurvieConstraintPos(i,j)]=getPos(i, j);
					jacIndexCons[getJacSurvieConstraintPos(i,j)]=getAgentConstraintPos(i);
				}
			}
		}



		//
		//		System.out.println("jv  :"+print(jacIndexVars));
		//		System.out.println("jc  :"+print(jacIndexCons));
		//hessienne
		int[] hIndexRow, hIndexCol;	

		if (socialChoice.equals(SocialChoiceType.Utility)){
			hvarNonNul=n*((m*m-m)/2+m);//pour chaque agent on a un carré sans diag de card (m*m-m)/2 et on rajoute la diag de m elem
			hIndexRow = new int[hvarNonNul];
			hIndexCol = new int[hvarNonNul];
			int pos=0;
			for (int i = 0; i < n; i++){
				for (int j = 0; j < m; j++){
					for (int jp = j; jp < m; jp++){
						hIndexRow[pos]=getPos(i, j);
						hIndexCol[pos]=getPos(i, jp);
						pos++;
					}
				}
			}
		} else if (socialChoice.equals(SocialChoiceType.Nash)){

			hvarNonNul=n*m*n*m;
			hIndexRow = new int[hvarNonNul];
			hIndexCol = new int[hvarNonNul];

			int pos=0;
			for (int i = 0; i < n; i++){
				for (int ip = i; ip < n; ip++){
					for (int j = 0; j < m; j++){
						for (int jp = j; jp < m; jp++){
							hIndexRow[pos]=getPos(i, j);
							hIndexCol[pos]=getPos(ip, jp);
							pos++;
						}
					}
				}
			}


		} else {
			assert (socialChoice.equals(SocialChoiceType.Leximin));
			throw new RuntimeException();
		}
		//		System.out.println("hr : "+print(hIndexRow));
		//		System.out.println("hc : "+print(hIndexCol));

		if (solver.mipInitProblem(
				numVar, 
				KnitroJava.KTR_OBJGOAL_MAXIMIZE, 
				KnitroJava.KTR_OBJTYPE_GENERAL,  
				socialChoice.equals(SocialChoiceType.Nash)?KnitroJava.KTR_FNTYPE_NONCONVEX:KnitroJava.KTR_FNTYPE_UNCERTAIN,
						vartype, varlb, varub,
						numConstraint, consType, consFnType,conslb, consub,  
						jvarNonNul, jacIndexVars, jacIndexCons, 
						hvarNonNul, hIndexRow, hIndexCol, 
						null,null)==false)  {
			System.err.println ("Error initializing the problem, "
					+ "KNITRO status = "
					+ solver.getKnitroStatusCode());
			return;
		}
	}
	//
	//	public static void main (String[] args) throws Exception{
	//		KnitroAllocationSolver kas = new KnitroAllocationSolver();
	//		kas.n=4;
	//		kas.m=3;
	//		kas.socialChoice=SocialChoiceType.Nash;
	//		kas.initiate();
	//	}

	private double[] solveProb(){
		//---- ALLOCATE ARRAYS FOR REVERSE COMMUNICATIONS OPERATION.
		double[]  daX       = new double[numVar];
		double[]  daLambda  = new double[numVar+numConstraint];
		double[]  daObj     = new double[1];
		double[]  daC       = new double[numConstraint];
		double[]  daObjGrad = new double[numVar];
		double[]  daJac     = new double[jvarNonNul];
		double[]  daHess    = new double[hvarNonNul];


		//---- SOLVE THE PROBLEM.  IN REVERSE COMMUNICATIONS MODE, KNITRO
		//---- RETURNS WHENEVER IT NEEDS MORE PROBLEM INFORMATION.  THE CALLING
		//---- PROGRAM MUST INTERPRET KNITRO'S RETURN STATUS AND CONTINUE
		//---- SUPPLYING PROBLEM INFORMATION UNTIL KNITRO IS COMPLETE.
		int  nKnStatus;
		int  nEvalStatus = 0;
		do
		{
			//			System.out.println("solving");
			nKnStatus = solver.mipSolve (nEvalStatus, daObj, daC,
					daObjGrad, daJac, daHess);
			if (nKnStatus == KnitroJava.KTR_RC_EVALFC)
			{
				//				System.out.println("KTR_RC_EVALFC");
				//---- KNITRO WANTS daObj AND daC EVALUATED AT THE POINT x.
				daX = solver.getCurrentX();
				daObj[0] = evaluateFC (daX, daC);
			}
			else if (nKnStatus == KnitroJava.KTR_RC_EVALGA)
			{
				//				System.out.println("KTR_RC_EVALGA");
				//---- KNITRO WANTS daObjGrad AND daJac EVALUATED AT THE POINT x.
				daX = solver.getCurrentX();
				evaluateGA (daX, daObjGrad, daJac);
			}
			else if (nKnStatus == KnitroJava.KTR_RC_EVALH)
			{
				//				System.out.println("KTR_RC_EVALH");
				//---- KNITRO WANTS daHess EVALUATED AT THE POINT x.
				daX = solver.getCurrentX();
				daLambda = solver.getCurrentLambda();
				evaluateH (daX, daLambda, 1.0, daHess);
			}
			//			else if (nKnStatus == KnitroJava.KTR_RC_EVALH)
			//			{
			//				System.out.println("KTR_RC_EVALH");
			//				//---- KNITRO WANTS daHess EVALUATED AT THE POINT x
			//				//---- WITHOUT OBJECTIVE COMPONENT INCLUDED.
			//				daX = solver.getCurrentX();
			//				daLambda = solver.getCurrentLambda();
			//				evaluateH (daX, daLambda, 0.0, daHess);
			//			}
			//			System.out.println("continue "+asMatrix(daX,n));
			//---- ASSUME THAT PROBLEM EVALUATION IS ALWAYS SUCCESSFUL.
			//---- IF A FUNCTION OR ITS DERIVATIVE COULD NOT BE EVALUATED
			//---- AT THE GIVEN (x, lambda), THEN SET nEvalStatus = 1 BEFORE
			//---- CALLING solve AGAIN.
			nEvalStatus = 0;
		}
		while (nKnStatus > 0);


		boolean hasFailed=false;

		//---- DISPLAY THE RESULTS.
		System.out.print ("KNITRO finished, status " + nKnStatus + ": ");
		switch (nKnStatus)
		{
		case KnitroJava.KTR_RC_OPTIMAL:
			System.out.println ("converged to optimality.");
			break;
		case KnitroJava.KTR_RC_ITER_LIMIT:
			System.out.println ("reached the maximum number of allowed iterations.");
			break;
		case KnitroJava.KTR_RC_NEAR_OPT:
		case KnitroJava.KTR_RC_FEAS_XTOL:
		case KnitroJava.KTR_RC_FEAS_NO_IMPROVE:
		case KnitroJava.KTR_RC_FEAS_FTOL:
			System.out.println ("could not improve upon the current iterate.");
			break;
		case KnitroJava.KTR_RC_TIME_LIMIT:
			System.out.println ("reached the maximum CPU time allowed.");
			break;
		default:
			System.out.println ("failed.");
			hasFailed=true;
		}

		//---- EXAMPLES OF OBTAINING SOLUTION INFORMATION.
		System.out.println ("  optimal value = " + daObj[0]);
		System.out.println ("  integrality gap (abs) = "
				+ solver.getMipAbsGap());
		System.out.println ("  integrality gap (rel) = "
				+ solver.getMipRelGap());
		System.out.println ("  solution feasibility violation (abs)    = "
				+ solver.getAbsFeasError());
		System.out.println ("           KKT optimality violation (abs) = "
				+ solver.getAbsOptError());
		System.out.println ("  number MIP nodes processed   = "
				+ solver.getMipNumNodes());
		System.out.println ("  number MIP subproblem solves = "
				+ solver.getMipNumSolves());
		if (hasFailed) daX=null;
		return daX;
	}

	/******                   *******/
	/******                   *******/ 
	/******         NOYAU     *******/
	/******                   *******/


	//----------------------------------------------------------------
	//   METHOD evaluateFC
	//----------------------------------------------------------------
	/** Compute the function and constraint values at x.
	 *
	 *  For more information about the arguments, refer to the KNITRO
	 *  manual, especially the section on the Callable Library.
	 */
	//daX de la forme 0..m-1 (a0), m ... 2m-1 (a1), ..., (k)*m,... (k+1)*m-1 (ak),... , (n-1)*m+1... nxm (a(n-1))
	private double evaluateFC(double[] daX, double[] daC) {

		//constraints
		if (isAgent){
			//n first : survivability of agent i
			for (int agent_i = 0; agent_i < n; agent_i++){
				daC[getAgentConstraintPos(agent_i)]=0;
				for (int host_j=0; host_j < m; host_j++){
					daC[getAgentConstraintPos(agent_i)]+=daX[getPos(agent_i,host_j)];
					assert daC[getAgentConstraintPos(agent_i)]<=m;
				}					
			}
		}

		if (isHost){
			//m next proc  of host i
			//m next mem  of host i	
			for (int host_j = 0; host_j < m; host_j++){
				daC[getHostConstraintPos(host_j, true)]=getMemCharge(daX, host_j);
				daC[getHostConstraintPos(host_j, false)]=getProcCharge(daX, host_j);
			}
		}

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
			double relia=ReplicationSocialOptimisation.getReliability(getDispo(daX, agent_i), agents[agent_i].getMyCriticity(), socialChoice);
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

	//----------------------------------------------------------------
	//   METHOD evaluateGA
	//----------------------------------------------------------------
	/** Compute the function and constraint first deriviatives at x.
	 *
	 *  For more information about the arguments, refer to the KNITRO
	 *  manual, especially the section on the Callable Library.
	 */
	private void  evaluateGA (double[]  daX,
			double[]  daObjGrad,
			double[]  daJac){
		for (int agent_i = 0; agent_i < n; agent_i++){
			for (int host_j = 0; host_j < m; host_j++){
				//obj
				daObjGrad[getPos(agent_i,host_j)]=-Math.log(hosts[host_j].getLambda())*agents[agent_i].getMyCriticity()*getDispo(daX,agent_i);
				//surv
				if (isAgent){
					daJac[getJacSurvieConstraintPos(agent_i, host_j)]=1;
				}
				if (isHost){
					//mem
					daJac[getJacChargeConstraintPos(agent_i, host_j, true)]=agents[agent_i].getMyMemCharge();
					//proc
					daJac[getJacChargeConstraintPos(agent_i, host_j, false)]=agents[agent_i].getMyProcCharge();
				}
			}
		}
	}


	//----------------------------------------------------------------
	//   METHOD evaluateH
	//----------------------------------------------------------------
	/** Compute the Hessian of the Lagrangian at x and lambda.
	 *
	 *  For more information about the arguments, refer to the KNITRO
	 *  manual, especially the section on the Callable Library.
	 */
	private void  evaluateH (double[]  daX,
			double[]  daLambda,
			double    dSigma,
			double[]  daHess)
	{
		if (socialChoice.equals(SocialChoiceType.Utility)){
			int pos=0;
			for (int agent_i = 0; agent_i < n; agent_i++){
				for (int host_j = 0; host_j < m; host_j++){
					for (int host_jp = host_j; host_jp < m; host_jp++){
						daHess[pos]=
								Math.log(hosts[host_j].getLambda())*Math.log(hosts[host_jp].getLambda())*
								agents[agent_i].getMyCriticity()*getDispo(daX,agent_i);
						pos++;
					}
				}
			}
		}  else {
			assert (socialChoice.equals(SocialChoiceType.Leximin));
			throw new RuntimeException();
		}
	}

	/******                   *******/
	/******                   *******/ 
	/******    PRIMITIVES     *******/
	/******                   *******/

	private boolean isGlobal(){
		return isAgent && isHost;
	}

	private int getPos(int agent_i, int host_j){
		return agent_i*m+host_j;
	}
	private void setNumConstraint(){
		numConstraint=0;
		if (isAgent)
			numConstraint+=n;
		if (isHost)
			numConstraint+=2*m;
	}

	private int getHostConstraintPos(int host_j, boolean isMemory){
		assert isHost;
		if (isMemory)
			return host_j;
		else
			return m+host_j;

	}
	private int getAgentConstraintPos(int agent_i){
		assert isAgent;
		if (isHost)
			return 2*m+agent_i;
		else
			return agent_i;
	}


	public void setNumNonNulJAc() {
		jvarNonNul=0;
		if (isAgent)
			jvarNonNul += (m*n);
		if (isHost)
			jvarNonNul += 2*(m*n);
	}

	private int getJacSurvieConstraintPos( int agent_i, int host_j){
		assert isAgent;
		int start=0;
		if (isHost)
			start=2*n*m;
		return start+getPos( agent_i, host_j);
	}

	private int getJacChargeConstraintPos(int agent_i, int host_j, boolean isMemory){
		assert isHost;
		int start;
		if (isMemory)
			start=0;
		else
			start = n*m;
		return start+getPos( agent_i, host_j);
	}

	/*
	 * 
	 */

	private AgentIdentifier getAgent(int i){
		return agents[i].getMyAgentIdentifier();
	}

	private ResourceIdentifier getHost(int j){
		return hosts[j].getMyAgentIdentifier();
	}

	private double getMemCharge(double[] daX,  int host_j) {
		double c=hosts[host_j].getCurrentMemCharge();
		for (int agent_i=0; agent_i < n; agent_i++)
			c+=daX[getPos(agent_i,host_j)]*agents[agent_i].getMyMemCharge();
		return c;
	}
	private double getProcCharge(double[] daX, int host_j) {
		double c=hosts[host_j].getCurrentProcCharge();
		for (int agent_i=0; agent_i < n; agent_i++)
			c+=daX[getPos(agent_i,host_j)]*agents[agent_i].getMyProcCharge();
		return c;
	}

	private double getDispo(double[] daX, int agent_i){
		double failProb = agents[agent_i].getMyFailureProb();
		for (int host_j = 0; host_j < m; host_j++)
			failProb *= daX[getPos(agent_i,host_j)]*hosts[host_j].getFailureProb();		
		return 1 - failProb;
	}

	/*
	 * 
	 */

	private String asMatrix(double[] vect, int nbCol){
		String result ="[ ";
		for (int i = 0; i < vect.length; i++){
			if (i%nbCol==0)
				result+="\n ";
			result+=vect[i]+" ";
		}
		return result;
	}
	private String asMatrix(ArrayList vect, int nbCol){
		String result ="[ ";
		for (int i = 0; i < vect.size(); i++){
			if (i%nbCol==0)
				result+="\n ";
			result+=vect.get(i)+" ";
		}
		return result;
	}

	private String print(int[] vect){
		String result ="[ ";
		for (int i = 0; i < vect.length; i++){
			result+=vect[i]+" ";
		}
		result+="]";
		return result;
	}

	private String print(double[] vect){
		String result ="[ ";
		for (int i = 0; i < vect.length; i++){
			result+=vect[i]+" ";
		}
		result+="]";
		return result;
	}


	//	public static void main(String[] args){
	//
	//		ReplicationInstanceGraph rig = new ReplicationInstanceGraph(SocialChoiceType.Utility);
	//
	//		try {
	//			rig.randomInitiaition(
	//					"to", 566668,
	//					9, 5,100,//nbAgent,nbHost
	//					0.5, DispersionSymbolicValue.Moyen, //criticity
	//					0.25, DispersionSymbolicValue.Nul, //agent load
	//					1., DispersionSymbolicValue.Nul, //hostCap
	//					0.5, DispersionSymbolicValue.Moyen, //hostDisp
	//					SocialChoiceType.Utility,2,3);
	//		} catch (IfailedException e1) {
	//			e1.printStackTrace();
	//		}
	//		rig = rig.getUnallocatedGraph();
	//		System.out.println(rig);
	//		KnitroAllocationSolver kas = new KnitroAllocationSolver(SocialChoiceType.Utility, true, true);
	//		kas.setProblem(rig, new HashedHashSet<AgentIdentifier, AgentIdentifier>(),false);
	//		kas.computeBestSolution();
	//		System.out.println(kas.getAllocation(kas.bestSolution));
	////		System.out.println(kas.getSolution(kas.bestSolution));
	//	}
}
