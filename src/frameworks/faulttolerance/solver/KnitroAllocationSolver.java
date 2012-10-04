package frameworks.faulttolerance.solver;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import com.ziena.knitro.KnitroJava;

import dima.basicagentcomponents.AgentIdentifier;
import dima.introspectionbasedagents.modules.distribution.NormalLaw.DispersionSymbolicValue;
import dima.introspectionbasedagents.modules.mappedcollections.HashedHashSet;
import dima.introspectionbasedagents.services.loggingactivity.LogService;
import frameworks.experimentation.IfailedException;
import frameworks.faulttolerance.dcop.DcopSolver;
import frameworks.faulttolerance.experimentation.ReplicationGraph;
import frameworks.faulttolerance.experimentation.ReplicationInstanceGraph;
import frameworks.faulttolerance.negotiatingagent.HostState;
import frameworks.faulttolerance.negotiatingagent.ReplicaState;
import frameworks.faulttolerance.negotiatingagent.ReplicationCandidature;
import frameworks.faulttolerance.negotiatingagent.ReplicationSocialOptimisation;
import frameworks.negotiation.contracts.ResourceIdentifier;
import frameworks.negotiation.exploration.ResourceAllocationSolver;
import frameworks.negotiation.exploration.Solver;
import frameworks.negotiation.exploration.Solver.UnsatisfiableException;
import frameworks.negotiation.rationality.AgentState;
import frameworks.negotiation.rationality.SocialChoiceFunction.SocialChoiceType;

public class KnitroAllocationSolver extends ResourceAllocationInterface<SimpleSolutionType>{

	
	public KnitroAllocationSolver(SocialChoiceType socialChoice,
			boolean isAgent, boolean isHost, int algo, boolean cplex,
			int numberOfThreads) {
		super(socialChoice, isAgent, isHost);
		this.algo = algo;
		this.numberOfThreads = numberOfThreads;
		this.cplex = cplex;
		this.parallel = numberOfThreads>1;
	}

//	static {
////		System.setProperty("java.library.path",System.getProperty("java.library.path")+":"+LogService.getDimaXDir()+"lib/");// System.getProperty("java.library.path")+":"+
////		System.out.println(System.getProperty("java.library.path"));
//	}

	/*
	 * 
	 */

	/******   Configuration       *******/
	boolean debug = false;
	protected final int algo;
	protected final int numberOfThreads;
	protected final boolean cplex;
	protected final boolean parallel;
	public int finishingStatus = -1;

	private void configureKnitro() {
		boolean ok=true;
		//		ok = ok && solver.setIntParamByName ("gradopt", 3);
		//		ok = ok && solver.setIntParamByName ("hessopt", 3);
		//		ok = ok && solver.setIntParamByName ("mip_method", 0); //0 auto 1 bb 2 hqg
		ok = ok && solver.setIntParamByName ("honorbnds", 0);
		//		0		 allow bounds to be violated during the optimization
		//		1		 enforce bounds satisfaction of all iterates
		//		2		 enforce bounds satisfaction of initial point

		//		0		 let KNITRO choose the method
		ok = ok && solver.setIntParamByName ("mip_knapsack", 2); //(0=no, 1=ineqs, 2=ineqs+eqs)

		//		1		 branch and bound method
		//		2		 hybrid method for convex nonlinear models
		ok = ok && solver.setIntParamByName ("algorithm", algo);	
		ok = ok && solver.setIntParamByName ("hessian_no_f", 1);
		//allow to ask user for no f hessian
		if (cplex){
			ok = ok &&  solver.setIntParamByName ("lpsolver", 2);
			//use cplex lp solver
		}
		if (parallel){
			ok = ok &&  solver.setIntParamByName ("ms_enable", 0);	
			ok = ok &&  solver.setIntParamByName ("ms_terminate", 0);	
			//			0			 terminate after ms_maxsolves
			//			1			 terminate at first local optimum (if before ms_maxsolves)
			//			2			 terminate at first feasible solution (if before ms_maxsolves)
			//			ok = ok &&  solver.setIntParamByName ("materminate", 1);
			//			0			 terminate after all algorithms have completed
			//			1			 terminate at first local optimum
			//			2			 terminate at first feasible solution
			ok = ok &&  solver.setIntParamByName ("par_numthreads", numberOfThreads);
			ok = ok &&  solver.setIntParamByName ("par_concurrent_evals", 1);
			//fa ga et ha can be executed by different threads

		}
		if (debug){
			ok = ok && solver.setIntParamByName("debug", 1);
			ok = ok && solver.setIntParamByName("mip_debug", 1);
			//			ok = ok && solver.setIntParamByName ("mip_outlev", 1);
			ok = ok && solver.setIntParamByName ("outlev", 6);
			ok = ok && solver.setIntParamByName ("mip_outinterval", 1);
			ok = ok && solver.setIntParamByName ("outmode", 2);
			//				0				 screen
			//				1				 file
			//2 both
			ok = ok && solver.setCharParamByName ("outdir", "/droop/Boulot/workspace/DimaX/log/") ;
		} else {
			ok = ok && solver.setIntParamByName("debug", 0);
			ok = ok && solver.setIntParamByName("mip_debug", 0);
			//			ok = ok && solver.setIntParamByName ("mip_outlev", 1);
			ok = ok && solver.setIntParamByName ("outlev", 0);
			ok = ok && solver.setIntParamByName ("mip_outinterval", 0);
			ok = ok && solver.setIntParamByName ("outmode", 1);
			//				0				 screen
			//				1				 file
			//2 both
			ok = ok && solver.setCharParamByName ("outdir", "/droop/Boulot/workspace/DimaX/log/") ;

		}
		assert ok;
	}

	@Override
	protected double read(SimpleSolutionType var, int agent, int host) {
		assert var!=null;
		return var.getSol()[getPos(agent, host)];
	}

	@Override
	public SimpleSolutionType getInitialAllocAsSolution(double[] intialAlloc) {
		return new SimpleSolutionType(intialAlloc);
	}

	@Override
	public void setTimeLimit(int millisec) {
		solver.setIntParamByName("mip_maxtime_cpu", millisec/1000);
	}

	/******     Solver    *******/
	protected KnitroJava solver;
	int numVar;
	int numConstraint;
	int jvarNonNul;
	int hvarNonNul;


	protected void initiateSolver() throws UnsatisfiableException  {
		numVar = getVariableNumber();
		try {
			solver = new KnitroJava();
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
		configureKnitro();

		double[] varlb = new double[numVar];
		double[] varub = new double[numVar];
		int[] vartype = new int[numVar];
		for (int i = 0; i < n; i++){
			for (int j = 0; j < m; j++){
				varlb[getPos(i,j)]=getAllocationLowerBound(i, j);
				varub[getPos(i,j)]=getAllocationUpperBound(i, j);
				vartype[getPos(i,j)]=KnitroJava.KTR_VARTYPE_INTEGER;
			}
		}

		//constraints 

		numConstraint=getConstraintNumber();

		double[] conslb = new double[numConstraint];
		double[] consub = new double[numConstraint];

		int[] consType= new int[numConstraint];
		int[] consFnType= new int[numConstraint];

		if (isAgent){
			for (int agent_i = 0; agent_i < n; agent_i++){
				conslb[getAgentConstraintPos(agent_i)] = 1.;
				consub[getAgentConstraintPos(agent_i)] = m;
				consType[getAgentConstraintPos(agent_i)]=KnitroJava.KTR_CONTYPE_LINEAR;
				consFnType[getAgentConstraintPos(agent_i)]=KnitroJava.KTR_FNTYPE_CONVEX;
			}
		}

		if (isHost){
			for (int host_j = 0; host_j < m; host_j++){		
				conslb[getHostConstraintPos(host_j, true)] = 0.;
				consub[getHostConstraintPos(host_j, true)] = getHostMaxMemory(host_j);
				consType[getHostConstraintPos(host_j, true)]=KnitroJava.KTR_CONTYPE_LINEAR;
				consFnType[getHostConstraintPos(host_j, true)]=KnitroJava.KTR_FNTYPE_CONVEX;

				conslb[getHostConstraintPos(host_j, false)] = 0.;
				consub[getHostConstraintPos(host_j, false)] = getHostMaxProcessor(host_j);
				consType[getHostConstraintPos(host_j, false)]=KnitroJava.KTR_CONTYPE_LINEAR;
				consFnType[getHostConstraintPos(host_j, false)]=KnitroJava.KTR_FNTYPE_CONVEX;
			}
		}

		if (isLocal()){
			assert intialSolution!=null;
			conslb[getLocalConstraintPos()]=getSocWelfare(intialSolution);
			consub[getLocalConstraintPos()] = KnitroJava.KTR_INFBOUND;
			consType[getLocalConstraintPos()]=KnitroJava.KTR_CONTYPE_GENERAL;
			consFnType[getLocalConstraintPos()]=socialChoice.equals(SocialChoiceType.Nash)?KnitroJava.KTR_FNTYPE_NONCONVEX:KnitroJava.KTR_FNTYPE_UNCERTAIN;
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
				KnitroJava.KTR_FNTYPE_UNCERTAIN,//socialChoice.equals(SocialChoiceType.Nash)?KnitroJava.KTR_FNTYPE_CONVEX:KnitroJava.KTR_FNTYPE_CONVEX,
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

	protected SimpleSolutionType solveProb(boolean opt) throws UnsatisfiableException {
		initiateSolver();
		boolean ok;
		if (opt){

			ok = solver.setIntParamByName ("mip_terminate", 0);
			//		0				 terminate at optimal solutionisGlobal()?0:1
			//				1				  terminate at first integer feasible solution
			ok = ok && solver.setIntParamByName ("bar_feasible", 2);
			//			 0			  no special emphasis on feasibility
			//			 1			  iterates must honor inequalities
			//			 2			  emphasize first getting feasible before optimizing
			//			 3			  implement both options 1 and 2 above
		} else {

			ok = solver.setIntParamByName ("mip_terminate", 1);
			//		0				 terminate at optimal solutionisGlobal()?0:1
			//				1				  terminate at first integer feasible solution
			ok = ok && solver.setIntParamByName ("bar_feasible", 2);
			//			 0			  no special emphasis on feasibility
			//			 1			  iterates must honor inequalities
			//			 2			  emphasize first getting feasible before optimizing
			//			 3			  implement both options 1 and 2 above
		}
		assert ok;

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
			//			for (int agent_i = 0; agent_i < n; agent_i++){
			//				for (int host_j = 0; host_j < m; host_j++){
			//					assert  solver.getCurrentX()[getPos(agent_i,host_j)]==1. || daX[getPos(agent_i,host_j)]==0.:daX[getPos(agent_i,host_j)];
			//				}
			//			}
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
				evaluateH (daX, daLambda, daHess, true);
			}
			else if (nKnStatus == KnitroJava.KTR_RC_EVALH_NO_F)
			{
				//---- KNITRO WANTS daHess EVALUATED AT THE POINT x
				//---- WITHOUT OBJECTIVE COMPONENT INCLUDED.
				daX = solver.getCurrentX();
				daLambda = solver.getCurrentLambda();
				evaluateH (daX, daLambda, daHess, false);
			}

			//						System.out.println("continue "+asMatrix(daX,n));
			//---- ASSUME THAT PROBLEM EVALUATION IS ALWAYS SUCCESSFUL.
			//---- IF A FUNCTION OR ITS DERIVATIVE COULD NOT BE EVALUATED
			//---- AT THE GIVEN (x, lambda), THEN SET nEvalStatus = 1 BEFORE
			//---- CALLING solve AGAIN.
			nEvalStatus = 0;

		}
		while (nKnStatus > 0);



		//---- DISPLAY THE RESULTS.
		if (debug){
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
		}
		finishingStatus=nKnStatus;
		if (opt) solver.destroyInstance();
		return new SimpleSolutionType(daX);
	}

	@Override
	public boolean hasNext() {
		if (finishingStatus== KnitroJava.KTR_RC_INFEAS_XTOL
				|| finishingStatus==  KnitroJava.KTR_RC_OPTIMAL
				|| finishingStatus== KnitroJava.KTR_RC_INFEASIBLE 
				|| finishingStatus== KnitroJava.KTR_RC_INFEAS_NO_IMPROVE 
				|| finishingStatus== KnitroJava.KTR_RC_INFEAS_MULTISTART 
				|| finishingStatus== KnitroJava.KTR_RC_INFEAS_CON_BOUNDS 
				|| finishingStatus== KnitroJava.KTR_RC_INFEAS_VAR_BOUNDS
				|| finishingStatus== KnitroJava.KTR_RC_TIME_LIMIT   ) {
			solver.destroyInstance();
			return false;
		} else {
			return true;
		}
	}

	protected boolean hasFailed() {
		switch (finishingStatus){
		case KnitroJava.KTR_RC_OPTIMAL:
		case KnitroJava.KTR_RC_ITER_LIMIT:
		case KnitroJava.KTR_RC_NEAR_OPT:
		case KnitroJava.KTR_RC_FEAS_XTOL:
		case KnitroJava.KTR_RC_FEAS_NO_IMPROVE:
		case KnitroJava.KTR_RC_FEAS_FTOL:
		case KnitroJava.KTR_RC_TIME_LIMIT:
			return false;
		default:
			return true;
		}
	}
	/******                   *******/
	/******                   *******/ 
	/******         NOYAU     *******/
	/******                   *******/

	/** Compute the function and constraint values at x.
	 *
	 *  For more information about the arguments, refer to the KNITRO
	 *  manual, especially the section on the Callable Library.
	 */
	private double evaluateFC(double[] daX, double[] daC) {

		//constraints
		if (isAgent){
			//n first : survivability of agent i
			for (int agent_i = 0; agent_i < n; agent_i++){
				daC[getAgentConstraintPos(agent_i)]=getAgentSurvie(new SimpleSolutionType(daX), agent_i);					
			}
		}

		if (isHost){
			//m next proc  of host i
			//m next mem  of host i	
			for (int host_j = 0; host_j < m; host_j++){
				daC[getHostConstraintPos(host_j, true)]=getHostMemoryCharge(new SimpleSolutionType(daX), host_j);
				daC[getHostConstraintPos(host_j, false)]=getHostProcessorCharge(new SimpleSolutionType(daX), host_j);
			}
		}	

		return getSocWelfare(new SimpleSolutionType(daX));
	}

	/** Compute the function and constraint first deriviatives at x.
	 *
	 *  For more information about the arguments, refer to the KNITRO
	 *  manual, especially the section on the Callable Library.
	 */
	private void evaluateGA(double[]  daX, double[]  daObjGrad, double[]  daJac) {
		for (int agent_i = 0; agent_i < n; agent_i++){
			for (int host_j = 0; host_j < m; host_j++){
				//obj
				double dRondSocWelfare = getDRondSocWelfare(new SimpleSolutionType(daX), agent_i, host_j);
				daObjGrad[getPos(agent_i,host_j)]=dRondSocWelfare;
				//surv
				if (isAgent){
					daJac[getJacSurvieConstraintPos(agent_i, host_j)]=1;
				}
				if (isHost){
					//mem
					daJac[getJacChargeConstraintPos(agent_i, host_j, true)]=getAgentMemorycharge(agent_i);
					//proc
					daJac[getJacChargeConstraintPos(agent_i, host_j, false)]=getAgentProcessorCharge(agent_i);
				}
				if (isLocal()){
					daJac[getLocalConstraintPos()]=dRondSocWelfare;
				}
			}
		}
	}

	/** Compute the Hessian of the Lagrangian at x and lambda.
	 *
	 *  For more information about the arguments, refer to the KNITRO
	 *  manual, especially the section on the Callable Library.
	 */
	private void evaluateH(double[]  daX, double[]  daLambda, double[]  daHess, boolean withObj) {
		int pos=0;
		if (socialChoice.equals(SocialChoiceType.Utility)){
			for (int agent_i = 0; agent_i < n; agent_i++){
				for (int host_j = 0; host_j < m; host_j++){
					for (int host_jp = host_j; host_jp < m; host_jp++){
						daHess[pos]=0;
						double dRond2SocWelfare = getDRondDeuxSocWelfare(new SimpleSolutionType(daX), agent_i, host_j, agent_i, host_jp);
						if (withObj)
							daHess[pos]+=dRond2SocWelfare;

						if (isLocal()){
							daHess[pos]+=daLambda[getLocalConstraintPos()] * dRond2SocWelfare;								
						}

						pos++;
					}
				}
			}
		} else if (socialChoice.equals(SocialChoiceType.Nash)){
			for (int agent_i = 0; agent_i < n;  agent_i++){
				for (int agent_ip =  agent_i;  agent_ip < n;  agent_ip++){
					for (int host_j = 0; host_j < m; host_j++){
						for (int host_jp = host_j; host_jp < m; host_jp++){
							daHess[pos]=0;
							double dRond2SocWelfare = getDRondDeuxSocWelfare(new SimpleSolutionType(daX), agent_i, host_j, agent_ip, host_jp);
							if (withObj)
								daHess[pos]+=dRond2SocWelfare;

							if (isLocal()){
								daHess[pos]+=daLambda[getLocalConstraintPos()] * dRond2SocWelfare;								
							}

							pos++;
						}
					}
				}
			}


		} else {
			assert (socialChoice.equals(SocialChoiceType.Leximin));
			throw new RuntimeException();
		}
	}

	/******                   *******/




	private int getHostConstraintPos(int host_j, boolean isMemory) {
		assert isHost;
		if (isMemory)
			return host_j;
		else
			return m+host_j;

	}

	private int getAgentConstraintPos(int agent_i) {
		assert isAgent;
		if (isHost)
			return 2*m+agent_i;
		else
			return agent_i;
	}

	private int getLocalConstraintPos() {
		assert isHost && !isAgent;
		return 2*m;		
	}

	private void setNumNonNulJAc() {
		jvarNonNul=0;
		if (isAgent)
			jvarNonNul += (m*n);
		if (isHost)
			jvarNonNul += 2*(m*n);
	}


	private int getJacSurvieConstraintPos(int agent_i, int host_j) {
		assert isAgent;
		int start=0;
		if (isHost)
			start=2*n*m;
		return start+getPos( agent_i, host_j);
	}

	private int getJacChargeConstraintPos(int agent_i, int host_j,
			boolean isMemory) {
		assert isHost;
		int start;
		if (isMemory)
			start=0;
		else
			start = getVariableNumber();
		return start+getPos( agent_i, host_j);
	}
	
	
	/**
	 *
	 */

	protected double[] getBestTriviaSol(List<ReplicaState> best, double hostCap) {
		double[] r = super.getBestTriviaSol(best, hostCap);
		solver.destroyInstance();
		return r;
	}
	
}



//public static void main(String[] args) throws Exception {
//	Random rand = new Random(65646);
//	for (int i = 0; i < 200; i++){
//		ReplicationInstanceGraph rig = new ReplicationInstanceGraph(SocialChoiceType.Utility);
//
//		try {
//			rig.randomInitiaition(
//					"to", rand.nextInt(),
//					5+rand.nextInt(5), 1,1000,//nbAgent,nbHost
//					0.5, DispersionSymbolicValue.Moyen, //criticity
//					0.25, DispersionSymbolicValue.Nul, //agent load
//					1., DispersionSymbolicValue.Nul, //hostCap
//					0.5, DispersionSymbolicValue.Moyen, //hostDisp
//					100,100);
//		} catch (IfailedException e1) {
//			e1.printStackTrace();
//		}
//		rig = rig.getUnallocatedGraph();
//		//			System.out.println(rig);
//		//		KnitroAllocationSolver kas = new KnitroAllocationSolver(rig.getSocialWelfare(), false, true, 5, false, 5);
//		KnitroAllocationSolver kas = new KnitroAllocationSolver(rig.getSocialWelfare(), false, true, 2, false, -1);
//		kas.myState=rig.getHostsStates().iterator().next();
//		kas.setProblem(rig);
//		kas.initiateSolver();
//		KnitroSolution bestSolution = kas.solveProb(true);
//
//		//		System.out.println("charge "+kas.getMemCharge(bestSolution, 0)+"  max  "+kas.getMemCharge(new double[]{1.,1.,1.,1.}, 0));
//		//		System.out.println(print(kas.agents));
//		List<ReplicaState> best = new ArrayList<ReplicaState>();
//		double[] bestPossible = kas.getBestTriviaSol(best,4);
//		System.out.println("best found    !!!!!!!!!!!!!!!! "+kas.getSocWelfare(bestSolution)+" \t \t"+kas.print(bestSolution));
//		System.out.println("best possible !!!!!!!!!!!!!!!! "+" "+kas.getSocWelfare(kas.new KnitroSolution(bestPossible))
//				+" \t \t"+kas.print(kas.new KnitroSolution(bestPossible)));
//		//		System.out.println("1 1 1 0  !!!!!!!!!!!!!!!! "+kas.getSocWelfare(new double[]{1.,1.,1.,0.}));
//		//		System.out.println("0 1 1 1  !!!!!!!!!!!!!!!! "+kas.getSocWelfare(new double[]{0.,1.,1.,1.}));
//		//		System.out.println("1 1 0 0 !!!!!!!!!!!!!!!! "+kas.getSocWelfare(new double[]{1.,1.,0.,0.}));
//		//		System.out.println("1 0 0 0 !!!!!!!!!!!!!!!! "+kas.getSocWelfare(new double[]{1.,0.,0.,0.}));
//		//		System.out.println("0 0 0 0 !!!!!!!!!!!!!!!! "+kas.getSocWelfare(new double[]{0.,0.,0.,0.}));
//		//		for (AgentState s : kas.getAllocation(bestSolution).values()){
//		//			if (s instanceof HostState)
//		//				System.out.println(s);
//		//		}
//		//		System.out.println(best);
//		//		for (int i = 1; i < 5; i++)
//		//		for (AgentState s : kas.getAllocation(kas.getBestSolution()).values()){
//		//			if (s instanceof HostState)
//		//				System.out.println(s);
//		//		}
//		//		System.out.println(kas.getSolution(kas.bestSolution));
//	}
//}
