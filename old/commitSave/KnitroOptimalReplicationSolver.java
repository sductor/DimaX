package frameworks.faulttolerance.solver;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.hp.hpl.mesa.rdf.jena.common.Util;
import com.ziena.knitro.KnitroJava;

import frameworks.faulttolerance.negotiatingagent.HostState;
import frameworks.faulttolerance.negotiatingagent.ReplicaState;
import frameworks.faulttolerance.negotiatingagent.ReplicationSocialOptimisation;
import frameworks.negotiation.contracts.MatchingCandidature;
import frameworks.negotiation.exploration.AllocationSolver;
import frameworks.negotiation.rationality.AgentState;
import frameworks.negotiation.rationality.SocialChoiceFunction.SocialChoiceType;

public class KnitroOptimalReplicationSolver
<Contract extends MatchingCandidature,
PersonalState extends AgentState> implements
AllocationSolver<Contract, PersonalState> {

	KnitroJava solver;
	int n, m;
	//	double[] hostProcCap, hostMemCap;
	//	double[] hostFailure;
	//	double[] agentCrit[];
	//	double[] agentMemCap;
	//	double[] agentProcCap;

	HostState[] hosts;
	ReplicaState[] agents;
	SocialChoiceType socialChoice;


	@Override
	public void initiate(Collection<Contract> concerned) {
		// TODO Auto-generated method stub
		solver.mipInitProblem(
				getNumVar(), 
				KnitroJava.KTR_OBJGOAL_MAXIMIZE, 
				KnitroJava.KTR_OBJTYPE_GENERAL,  
				nObjFnType, 
				naVarTypes, getZerosIntegerArray(getNumVar()),getOnesIntegerArray(getNumVar()),
				getNumConstraint(), getConsType(), naConsFnTypes, getConsLowerBound(), getConsUpperBound(),  
				nnzJac, naJacIxVars, naJacIxCons, 
				nnzHess, naHessIxRows, naHessIxCols, 
				daXInitial, daLambdaInitial);

	}

	@Override
	public Collection<Contract> getBestSolution() {
		return solver.solve(nEvalStatus, daObjective, daCons, daGradient, daJacValues, daHessValues);
	}

	@Override
	public boolean hasNext() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Collection<Contract> getNextSolution() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setTimeLimit(int millisec) {
		// TODO Auto-generated method stub

	}

	public int solve(){
		//---- ALLOCATE ARRAYS FOR REVERSE COMMUNICATIONS OPERATION.
		double[]  daX       = new double[n];
		double[]  daLambda  = new double[m + n];
		double[]  daObj     = new double[1];
		double[]  daC       = new double[m];
		double[]  daObjGrad = new double[n];
		double[]  daJac     = new double[nnzJ];
		double[]  daHess    = new double[nnzH];
		//---- SOLVE THE PROBLEM.  IN REVERSE COMMUNICATIONS MODE, KNITRO
		//---- RETURNS WHENEVER IT NEEDS MORE PROBLEM INFORMATION.  THE CALLING
		//---- PROGRAM MUST INTERPRET KNITRO'S RETURN STATUS AND CONTINUE
		//---- SUPPLYING PROBLEM INFORMATION UNTIL KNITRO IS COMPLETE.
		int  nKnStatus;
		int  nEvalStatus = 0;
		do
		{
			nKnStatus = solver.mipSolve (nEvalStatus, daObj, daC,
					daObjGrad, daJac, daHess);
			if (nKnStatus == KnitroJava.KTR_RC_EVALFC)
			{
				//---- KNITRO WANTS daObj AND daC EVALUATED AT THE POINT x.
				daX = solver.getCurrentX();
				daObj[0] = evaluateFC (daX, daC);
			}
			else if (nKnStatus == KnitroJava.KTR_RC_EVALGA)
			{
				//---- KNITRO WANTS daObjGrad AND daJac EVALUATED AT THE POINT x.
				daX = solver.getCurrentX();
				evaluateGA (daX, daObjGrad, daJac);
			}
			else if (nKnStatus == KnitroJava.KTR_RC_EVALH)
			{
				//---- KNITRO WANTS daHess EVALUATED AT THE POINT x.
				daX = solver.getCurrentX();
				daLambda = solver.getCurrentLambda();
				evaluateH (daX, daLambda, 1.0, daHess);
			}
			else if (nKnStatus == KnitroJava.KTR_RC_EVALH)
			{
				//---- KNITRO WANTS daHess EVALUATED AT THE POINT x
				//---- WITHOUT OBJECTIVE COMPONENT INCLUDED.
				daX = solver.getCurrentX();
				daLambda = solver.getCurrentLambda();
				evaluateH (daX, daLambda, 0.0, daHess);
			}

			//---- ASSUME THAT PROBLEM EVALUATION IS ALWAYS SUCCESSFUL.
			//---- IF A FUNCTION OR ITS DERIVATIVE COULD NOT BE EVALUATED
			//---- AT THE GIVEN (x, lambda), THEN SET nEvalStatus = 1 BEFORE
			//---- CALLING solve AGAIN.
			nEvalStatus = 0;
		}
		while (nKnStatus > 0);
	}

	protected void evaluateH(double[] daX, double[] daLambda, double d,
			double[] daHess) {
		// TODO Auto-generated method stub

	}

	protected void evaluateGA(double[] daX, double[] daObjGrad, double[] daJac) {
		// TODO Auto-generated method stub

	}

	//daX de la forme 0..m-1 (a0), m ... 2m-1 (a1), ..., (k)*m,... (k+1)*m-1 (ak),... , (n-1)*m+1... nxm (a(n-1))
	protected double evaluateFC(double[] daX, double[] daC) {

		//constraints
		//n first : survivability of agent i
		for (int agent_i = 0; agent_i < n; agent_i++){
			daC[agent_i]=0;
			for (int host_j=0; host_j < m; host_j++)
				if (allocated(daX,agent_i,host_j)){
					daC[agent_i]=1;
					break;
				}				
		}
		
		//m next proc  of host i
		//m next mem  of host i	
		for (int host_j = 0; host_j < m; host_j++){
			daC[n+host_j]=0;
			daC[n+m+host_j]=0;
			//on ajoute la charge of agent i
			for (int agent_i=0; agent_i < n; agent_i++){
				if (allocated(daX,agent_i,host_j))
					daC[n+host_j]+=agents[agent_i].getMyProcCharge();
					daC[n+m+host_j]+=agents[agent_i].getMyMemCharge();
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

	private double getDispo(double[] daX, int agent_i){
		double failProb = 1;
		for (int host_j = 0; host_j < m; host_j++)
			failProb *= daX[agent_i*m+host_j]*hosts[host_j].getFailureProb();		
		return 1 - failProb;
	}

	
	private boolean allocated(double[] daX, int agent, int host){
		return daX[agent*m+host]==1;
	}
	
	
	
	/*
	 * 
	 */

	protected int getNumVar(){
		return n*m;
	}

	protected int getNumConstraint(){
		return 2*m+n;
	}

	protected Integer[] getConsType(){
		List<Integer> l = new ArrayList<Integer>(getNumVar());
		for (int i = 0; i < getNumVar(); i++)
			l.set(i, KnitroJava.KTR_CONTYPE_LINEAR);
		return l.toArray(new Integer[getNumVar()]);
	}

	protected Double[] getConsLowerBound(){
		Double[] lb = getZerosDoubleArray(getNumConstraint());
		for (int i = 0; i < n; i++)
			lb[i] = 1.;
		return lb;
	}
	protected Double[] getConsUpperBound(){
		Double[] lb = getZerosDoubleArray(getNumConstraint());
		for (int i = 0; i < n; i++)
			lb[i] = 1.;
		for (int i = n; i < n+m; i++)
			lb[i] = hosts[i].getProcChargeMax();
		for (int i = n+m; i < n+2*m; i++)
			lb[i] = hosts[i].getMemChargeMax();
		return lb;
	}

	/*
	 * 
	 */

	protected Double[] getZerosDoubleArray(int num){
		List<Double> l = new ArrayList<Double>(num);
		for (int i = 0; i < getNumVar(); i++)
			l.set(i, 0.);
		return l.toArray(new Double[num]);
	}
	protected Double[] getOnesDoubleArray(int num){
		List<Double> l = new ArrayList<Double>(num);
		for (int i = 0; i < getNumVar(); i++)
			l.set(i, 1.);
		return l.toArray(new Double[num]);
	}

	private Integer[] getZerosIntegerArray(int num) {
		List<Integer> l = new ArrayList<Integer>(getNumVar());
		for (int i = 0; i < getNumVar(); i++)
			l.set(i, 0);
		return l.toArray(new Integer[getNumVar()]);
	}
	protected Integer[] getOnesIntegerArray(int num){
		List<Integer> l = new ArrayList<Integer>(num);
		for (int i = 0; i < getNumVar(); i++)
			l.set(i, 1);
		return l.toArray(new Integer[num]);
	}
}
