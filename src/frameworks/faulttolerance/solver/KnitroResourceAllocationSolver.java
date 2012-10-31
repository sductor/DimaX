package frameworks.faulttolerance.solver;

import java.util.List;

import com.ziena.knitro.KnitroJava;

import frameworks.faulttolerance.negotiatingagent.ReplicaState;
import frameworks.negotiation.exploration.Solver.UnsatisfiableException;
import frameworks.negotiation.rationality.SocialChoiceFunction.SocialChoiceType;

public abstract class KnitroResourceAllocationSolver extends
		ResourceAllocationInterface<RessourceAllocationSimpleSolutionType> {

	public KnitroResourceAllocationSolver(SocialChoiceType socialChoice,
			boolean isAgent, boolean isHost, int algo, boolean cplex,
			int numberOfThreads) {
		super(socialChoice, isAgent, isHost);
		this.algo = algo;
		this.numberOfThreads = numberOfThreads;
		this.cplex = cplex;
		this.parallel = numberOfThreads>1;
	}
	/******   Configuration       *******/
	boolean debug = false;
	protected final int algo;
	protected final int numberOfThreads;
	protected final boolean cplex;
	protected final boolean parallel;
	public int finishingStatus = -1;
	/******     Solver    *******/
	protected KnitroJava solver;

	protected void configureKnitro() {
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
	protected double readVariable(RessourceAllocationSimpleSolutionType var, int varPos) {
		assert var!=null;
		return var.getSol()[varPos];
	}

	@Override
	public RessourceAllocationSimpleSolutionType getInitialAllocAsSolution(double[] intialAlloc) {
		return new RessourceAllocationSimpleSolutionType(intialAlloc);
	}

	@Override
	public void setTimeLimit(int millisec) {
		solver.setIntParamByName("mip_maxtime_cpu", millisec/1000);
	}
	
	/*
	 * 
	 */

	private int numConstraint;
	private int numVar;
	private int jvarNonNul;
	private int hvarNonNul;

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
			assert initialSolution!=null;
			conslb[getLocalConstraintPos()]=getSocWelfare(initialSolution);
			consub[getLocalConstraintPos()] = KnitroJava.KTR_INFBOUND;
			consType[getLocalConstraintPos()]=KnitroJava.KTR_CONTYPE_GENERAL;
			consFnType[getLocalConstraintPos()]=socialChoice.equals(SocialChoiceType.Nash)?KnitroJava.KTR_FNTYPE_NONCONVEX:KnitroJava.KTR_FNTYPE_UNCERTAIN;
		}

		//jacobienne
		jvarNonNul=setNumNonNulJAc();
		int[] jacIndexVars = new int[jvarNonNul];
		int[] jacIndexCons = new int[jvarNonNul];
		//on considere trois vecteurs et on 'ecrase' verticalement : 
		//il ne s'agit pas d'une lecture ligne d'abore mais d'une porjection sur laxe des vare pour chaquer type de contrainte
		setJacobian(jacIndexVars, jacIndexCons);



		//
		//		System.out.println("jv  :"+print(jacIndexVars));
		//		System.out.println("jc  :"+print(jacIndexCons));
		//hessienne
		hvarNonNul=setNumNonNulHess();
		int[] hIndexRow = new int[hvarNonNul];
		int[] hIndexCol = new int[hvarNonNul];
		
		setHessian(hIndexRow, hIndexCol);

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

	protected RessourceAllocationSimpleSolutionType solveProb(boolean opt) throws UnsatisfiableException {
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
		return new RessourceAllocationSimpleSolutionType(daX);
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

	protected int getHostConstraintPos(int host_j, boolean isMemory) {
		assert isHost;
		if (isMemory)
			return host_j;
		else
			return m+host_j;
	}

	protected int getAgentConstraintPos(int agent_i) {
		assert isAgent;
		if (isHost)
			return 2*m+agent_i;
		else
			return agent_i;
	}

	protected int getLocalConstraintPos() {
		assert isHost && !isAgent;
		return 2*m;		
	}

	protected int getJacSurvieConstraintPos(int agent_i, int host_j) {
		assert isAgent;
		int start=0;
		if (isHost)
			start=2*n*m;
		return start+getPos( agent_i, host_j);
	}

	protected int getJacChargeConstraintPos(int agent_i, int host_j,
			boolean isMemory) {
		assert isHost;
		int start;
		if (isMemory)
			start=0;
		else
			start = getVariableNumber();
		return start+getPos( agent_i, host_j);
	}

	
	/*
	 * 
	 */

	protected abstract int setNumNonNulJAc();

	protected abstract int setNumNonNulHess();

	protected abstract void setHessian(int[] hIndexRow, int[] hIndexCol);

	protected abstract  void setJacobian(int[] jacIndexVars, int[] jacIndexCons);

	/*
	 * 
	 */

	protected abstract void evaluateH(double[] daX, double[] daLambda, double[] daHess,		boolean b); 
	
	protected abstract void evaluateGA(double[] daX, double[] daObjGrad, double[] daJac) ; 
	
	protected abstract  double evaluateFC(double[] daX, double[] daC);

	
	/**
	 *
	 */
	protected double[] getBestTriviaSol(List<ReplicaState> best, double hostCap) {
		double[] r = super.getBestTriviaSol(best, hostCap);
		solver.destroyInstance();
		return r;
	}
	/******                   *******/

}