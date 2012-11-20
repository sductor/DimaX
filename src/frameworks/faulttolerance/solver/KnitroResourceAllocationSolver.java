package frameworks.faulttolerance.solver;

import java.util.List;

import com.ziena.knitro.KnitroJava;

import frameworks.faulttolerance.negotiatingagent.ReplicaState;
import frameworks.negotiation.rationality.SocialChoiceFunction.SocialChoiceType;

public abstract class KnitroResourceAllocationSolver extends
ResourceAllocationInterface<RessourceAllocationSimpleSolutionType> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3466528047679701590L;

	public KnitroResourceAllocationSolver(final SocialChoiceType socialChoice,
			final boolean isAgent, final boolean isHost, final int algo, final boolean cplex,
			final int numberOfThreads) {
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
		ok = ok && this.solver.setIntParamByName ("honorbnds", 0);
		//		0		 allow bounds to be violated during the optimization
		//		1		 enforce bounds satisfaction of all iterates
		//		2		 enforce bounds satisfaction of initial point

		//		0		 let KNITRO choose the method
		ok = ok && this.solver.setIntParamByName ("mip_knapsack", 2); //(0=no, 1=ineqs, 2=ineqs+eqs)

		//		1		 branch and bound method
		//		2		 hybrid method for convex nonlinear models
		ok = ok && this.solver.setIntParamByName ("algorithm", this.algo);
		ok = ok && this.solver.setIntParamByName ("hessian_no_f", 1);
		//allow to ask user for no f hessian
		if (this.cplex){
			ok = ok &&  this.solver.setIntParamByName ("lpsolver", 2);
			//use cplex lp solver
		}
		if (this.parallel){
			ok = ok &&  this.solver.setIntParamByName ("ms_enable", 0);
			ok = ok &&  this.solver.setIntParamByName ("ms_terminate", 0);
			//			0			 terminate after ms_maxsolves
			//			1			 terminate at first local optimum (if before ms_maxsolves)
			//			2			 terminate at first feasible solution (if before ms_maxsolves)
			//			ok = ok &&  solver.setIntParamByName ("materminate", 1);
			//			0			 terminate after all algorithms have completed
			//			1			 terminate at first local optimum
			//			2			 terminate at first feasible solution
			ok = ok &&  this.solver.setIntParamByName ("par_numthreads", this.numberOfThreads);
			ok = ok &&  this.solver.setIntParamByName ("par_concurrent_evals", 1);
			//fa ga et ha can be executed by different threads

		}
		if (this.debug){
			ok = ok && this.solver.setIntParamByName("debug", 1);
			ok = ok && this.solver.setIntParamByName("mip_debug", 1);
			//			ok = ok && solver.setIntParamByName ("mip_outlev", 1);
			ok = ok && this.solver.setIntParamByName ("outlev", 6);
			ok = ok && this.solver.setIntParamByName ("mip_outinterval", 1);
			ok = ok && this.solver.setIntParamByName ("outmode", 2);
			//				0				 screen
			//				1				 file
			//2 both
			ok = ok && this.solver.setCharParamByName ("outdir", "/droop/Boulot/workspace/DimaX/log/") ;
		} else {
			ok = ok && this.solver.setIntParamByName("debug", 0);
			ok = ok && this.solver.setIntParamByName("mip_debug", 0);
			//			ok = ok && solver.setIntParamByName ("mip_outlev", 1);
			ok = ok && this.solver.setIntParamByName ("outlev", 0);
			ok = ok && this.solver.setIntParamByName ("mip_outinterval", 0);
			ok = ok && this.solver.setIntParamByName ("outmode", 1);
			//				0				 screen
			//				1				 file
			//2 both
			ok = ok && this.solver.setCharParamByName ("outdir", "/droop/Boulot/workspace/DimaX/log/") ;

		}
		assert ok;
	}

	@Override
	protected double readVariable(final RessourceAllocationSimpleSolutionType var, final int varPos) {
		assert var!=null;
		return var.getSol()[varPos];
	}

	@Override
	public RessourceAllocationSimpleSolutionType getInitialAllocAsSolution(final double[] intialAlloc) {
		return new RessourceAllocationSimpleSolutionType(intialAlloc);
	}

	@Override
	public void setTimeLimit(final int millisec) {
		this.solver.setIntParamByName("mip_maxtime_cpu", millisec/1000);
	}

	/*
	 * 
	 */

	private int numConstraint;
	private int numVar;
	private int jvarNonNul;
	private int hvarNonNul;

	@Override
	protected void initiateSolverPost() throws UnsatisfiableException  {
		this.numVar = this.getVariableNumber();
		try {
			this.solver = new KnitroJava();
		} catch (final Exception e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
		this.configureKnitro();

		final double[] varlb = new double[this.numVar];
		final double[] varub = new double[this.numVar];
		final int[] vartype = new int[this.numVar];
		for (int i = 0; i < this.n; i++){
			for (int j = 0; j < this.m; j++){
				varlb[this.getPos(i,j)]=this.getAllocationLowerBound(i, j);
				varub[this.getPos(i,j)]=this.getAllocationUpperBound(i, j);
				vartype[this.getPos(i,j)]=KnitroJava.KTR_VARTYPE_INTEGER;
			}
		}

		//constraints

		this.numConstraint=this.getConstraintNumber();

		final double[] conslb = new double[this.numConstraint];
		final double[] consub = new double[this.numConstraint];

		final int[] consType= new int[this.numConstraint];
		final int[] consFnType= new int[this.numConstraint];

		if (this.isAgent){
			for (int agent_i = 0; agent_i < this.n; agent_i++){
				conslb[this.getAgentConstraintPos(agent_i)] = 1.;
				consub[this.getAgentConstraintPos(agent_i)] = this.m;
				consType[this.getAgentConstraintPos(agent_i)]=KnitroJava.KTR_CONTYPE_LINEAR;
				consFnType[this.getAgentConstraintPos(agent_i)]=KnitroJava.KTR_FNTYPE_CONVEX;
			}
		}

		if (this.isHost){
			for (int host_j = 0; host_j < this.m; host_j++){
				conslb[this.getHostConstraintPos(host_j, true)] = 0.;
				consub[this.getHostConstraintPos(host_j, true)] = this.getHostMaxMemory(host_j);
				consType[this.getHostConstraintPos(host_j, true)]=KnitroJava.KTR_CONTYPE_LINEAR;
				consFnType[this.getHostConstraintPos(host_j, true)]=KnitroJava.KTR_FNTYPE_CONVEX;

				conslb[this.getHostConstraintPos(host_j, false)] = 0.;
				consub[this.getHostConstraintPos(host_j, false)] = this.getHostMaxProcessor(host_j);
				consType[this.getHostConstraintPos(host_j, false)]=KnitroJava.KTR_CONTYPE_LINEAR;
				consFnType[this.getHostConstraintPos(host_j, false)]=KnitroJava.KTR_FNTYPE_CONVEX;
			}
		}

		if (this.isLocal()){
			assert this.initialSolution!=null;
			conslb[this.getLocalConstraintPos()]=this.getSocWelfare(this.initialSolution);
			consub[this.getLocalConstraintPos()] = KnitroJava.KTR_INFBOUND;
			consType[this.getLocalConstraintPos()]=KnitroJava.KTR_CONTYPE_GENERAL;
			consFnType[this.getLocalConstraintPos()]=KnitroJava.KTR_FNTYPE_CONVEX;
		}

		//jacobienne
		this.jvarNonNul=this.setNumNonNulJAc();
		final int[] jacIndexVars = new int[this.jvarNonNul];
		final int[] jacIndexCons = new int[this.jvarNonNul];
		//on considere trois vecteurs et on 'ecrase' verticalement :
		//il ne s'agit pas d'une lecture ligne d'abore mais d'une porjection sur laxe des vare pour chaquer type de contrainte
		this.setJacobian(jacIndexVars, jacIndexCons);



		//
		//		System.out.println("jv  :"+print(jacIndexVars));
		//		System.out.println("jc  :"+print(jacIndexCons));
		//hessienne
		this.hvarNonNul=this.setNumNonNulHess();
		final int[] hIndexRow = new int[this.hvarNonNul];
		final int[] hIndexCol = new int[this.hvarNonNul];

		this.setHessian(hIndexRow, hIndexCol);

		//		System.out.println("hr : "+print(hIndexRow));
		//		System.out.println("hc : "+print(hIndexCol));

		if (this.solver.mipInitProblem(
				this.numVar,
				KnitroJava.KTR_OBJGOAL_MAXIMIZE,
				KnitroJava.KTR_OBJTYPE_GENERAL,
				KnitroJava.KTR_FNTYPE_UNCERTAIN,//socialChoice.equals(SocialChoiceType.Nash)?KnitroJava.KTR_FNTYPE_CONVEX:KnitroJava.KTR_FNTYPE_CONVEX,
				vartype, varlb, varub,
				this.numConstraint, consType, consFnType,conslb, consub,
				this.jvarNonNul, jacIndexVars, jacIndexCons,
				this.hvarNonNul, hIndexRow, hIndexCol,
				null,null)==false)  {
			System.err.println ("Error initializing the problem, "
					+ "KNITRO status = "
					+ this.solver.getKnitroStatusCode());
			return;
		}
	}

	@Override
	protected void initiateSolver() throws UnsatisfiableException {
		// TODO Auto-generated method stub

	}
	@Override
	protected RessourceAllocationSimpleSolutionType solveProb(final boolean opt) throws UnsatisfiableException {
		this.initiateSolver();
		boolean ok;
		if (opt){

			ok = this.solver.setIntParamByName ("mip_terminate", 0);
			//		0				 terminate at optimal solutionisGlobal()?0:1
			//				1				  terminate at first integer feasible solution
			ok = ok && this.solver.setIntParamByName ("bar_feasible", 2);
			//			 0			  no special emphasis on feasibility
			//			 1			  iterates must honor inequalities
			//			 2			  emphasize first getting feasible before optimizing
			//			 3			  implement both options 1 and 2 above
		} else {

			ok = this.solver.setIntParamByName ("mip_terminate", 1);
			//		0				 terminate at optimal solutionisGlobal()?0:1
			//				1				  terminate at first integer feasible solution
			ok = ok && this.solver.setIntParamByName ("bar_feasible", 2);
			//			 0			  no special emphasis on feasibility
			//			 1			  iterates must honor inequalities
			//			 2			  emphasize first getting feasible before optimizing
			//			 3			  implement both options 1 and 2 above
		}
		assert ok;

		//---- ALLOCATE ARRAYS FOR REVERSE COMMUNICATIONS OPERATION.
		double[]  daX       = new double[this.numVar];
		double[]  daLambda  = new double[this.numVar+this.numConstraint];
		final double[]  daObj     = new double[1];
		final double[]  daC       = new double[this.numConstraint];
		final double[]  daObjGrad = new double[this.numVar];
		final double[]  daJac     = new double[this.jvarNonNul];
		final double[]  daHess    = new double[this.hvarNonNul];


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
			nKnStatus = this.solver.mipSolve (nEvalStatus, daObj, daC,
					daObjGrad, daJac, daHess);
			if (nKnStatus == KnitroJava.KTR_RC_EVALFC)
			{
				//				System.out.println("KTR_RC_EVALFC");
				//---- KNITRO WANTS daObj AND daC EVALUATED AT THE POINT x.
				daX = this.solver.getCurrentX();
				daObj[0] = this.evaluateFC (daX, daC);
			}
			else if (nKnStatus == KnitroJava.KTR_RC_EVALGA)
			{
				//				System.out.println("KTR_RC_EVALGA");
				//---- KNITRO WANTS daObjGrad AND daJac EVALUATED AT THE POINT x.
				daX = this.solver.getCurrentX();
				this.evaluateGA (daX, daObjGrad, daJac);
			}
			else if (nKnStatus == KnitroJava.KTR_RC_EVALH)
			{
				//				System.out.println("KTR_RC_EVALH");
				//---- KNITRO WANTS daHess EVALUATED AT THE POINT x.
				daX = this.solver.getCurrentX();
				daLambda = this.solver.getCurrentLambda();
				this.evaluateH (daX, daLambda, daHess, true);
			}
			else if (nKnStatus == KnitroJava.KTR_RC_EVALH_NO_F)
			{
				//---- KNITRO WANTS daHess EVALUATED AT THE POINT x
				//---- WITHOUT OBJECTIVE COMPONENT INCLUDED.
				daX = this.solver.getCurrentX();
				daLambda = this.solver.getCurrentLambda();
				this.evaluateH (daX, daLambda, daHess, false);
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
		if (this.debug){
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
					+ this.solver.getMipAbsGap());
			System.out.println ("  integrality gap (rel) = "
					+ this.solver.getMipRelGap());
			System.out.println ("  solution feasibility violation (abs)    = "
					+ this.solver.getAbsFeasError());
			System.out.println ("           KKT optimality violation (abs) = "
					+ this.solver.getAbsOptError());
			System.out.println ("  number MIP nodes processed   = "
					+ this.solver.getMipNumNodes());
			System.out.println ("  number MIP subproblem solves = "
					+ this.solver.getMipNumSolves());
		}
		this.finishingStatus=nKnStatus;
		if (opt) {
			this.solver.destroyInstance();
		}
		return new RessourceAllocationSimpleSolutionType(daX);
	}

	@Override
	public boolean hasNext() {
		if (this.finishingStatus== KnitroJava.KTR_RC_INFEAS_XTOL
				|| this.finishingStatus==  KnitroJava.KTR_RC_OPTIMAL
				|| this.finishingStatus== KnitroJava.KTR_RC_INFEASIBLE
				|| this.finishingStatus== KnitroJava.KTR_RC_INFEAS_NO_IMPROVE
				|| this.finishingStatus== KnitroJava.KTR_RC_INFEAS_MULTISTART
				|| this.finishingStatus== KnitroJava.KTR_RC_INFEAS_CON_BOUNDS
				|| this.finishingStatus== KnitroJava.KTR_RC_INFEAS_VAR_BOUNDS
				|| this.finishingStatus== KnitroJava.KTR_RC_TIME_LIMIT   ) {
			this.solver.destroyInstance();
			return false;
		} else {
			return true;
		}
	}

	protected boolean hasFailed() {
		switch (this.finishingStatus){
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

	protected int getHostConstraintPos(final int host_j, final boolean isMemory) {
		assert this.isHost;
		if (isMemory) {
			return host_j;
		} else {
			return this.m+host_j;
		}
	}

	protected int getAgentConstraintPos(final int agent_i) {
		assert this.isAgent;
		if (this.isHost) {
			return 2*this.m+agent_i;
		} else {
			return agent_i;
		}
	}

	protected int getLocalConstraintPos() {
		assert this.isHost && !this.isAgent;
		return 2*this.m;
	}

	protected int getJacSurvieConstraintPos(final int agent_i, final int host_j) {
		assert this.isAgent;
		int start=0;
		if (this.isHost) {
			start=2*this.n*this.m;
		}
		return start+this.getPos( agent_i, host_j);
	}

	protected int getJacChargeConstraintPos(final int agent_i, final int host_j,
			final boolean isMemory) {
		assert this.isHost;
		int start;
		if (isMemory) {
			start=0;
		} else {
			start = this.getVariableNumber();
		}
		return start+this.getPos( agent_i, host_j);
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
	@Override
	protected double[] getBestTriviaSol(final List<ReplicaState> best, final double hostCap) {
		final double[] r = super.getBestTriviaSol(best, hostCap);
		this.solver.destroyInstance();
		return r;
	}
	/******                   *******/

}