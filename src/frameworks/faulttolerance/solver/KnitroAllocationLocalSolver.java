package frameworks.faulttolerance.solver;

import frameworks.faulttolerance.negotiatingagent.ReplicaState;
import frameworks.negotiation.rationality.SocialChoiceFunction.SocialChoiceType;

public class KnitroAllocationLocalSolver extends KnitroResourceAllocationSolver {

	public KnitroAllocationLocalSolver(SocialChoiceType socialChoice,
			boolean isAgent, boolean isHost, int algo, boolean cplex,
			int numberOfThreads) {
		super(socialChoice, isAgent, isHost, algo, cplex, numberOfThreads);
	}

	//
	// Fields
	//

	double initialValue;
	double[] allocatedValue;
	double[] unallocatedValue;

	/*
	 * 
	 */

	@Override
	protected void initiateSolverPost() throws UnsatisfiableException {
		assert m==1;
		super.initiateSolverPost();
		for (int i = 0; i < n; i++){
			unallocatedValue[i] = this.rig.getAgentState(this.getAgentIdentifier(i)).allocate(myState, false).getMyReliability();
			allocatedValue[i] = this.rig.getAgentState(this.getAgentIdentifier(i)).allocate(myState, true).getMyReliability();
		}
	}

	/*
	 * 
	 */



	@Override
	protected double evaluateFC(double[] daX, double[] daC) {	
		daC[getHostConstraintPos(1, true)]=getHostMemoryCharge(new RessourceAllocationSimpleSolutionType(daX), 1);
		daC[getHostConstraintPos(1, false)]=getHostProcessorCharge(new RessourceAllocationSimpleSolutionType(daX), 1);
		double result = 0;
		for (int i = 0; i < n; i++){
			result+= daX[i]*(allocatedValue[i]-unallocatedValue[i])+unallocatedValue[i];
		}
		daC[getLocalConstraintPos()]=result;

		return result;
	}
	@Override
	protected void evaluateGA(double[] daX, double[] daObjGrad, double[] daJac) {
		daJac[getLocalConstraintPos()]=0;
		for (int agent_i = 0; agent_i < n; agent_i++){
			//obj
			double dRondIndWelfare = (allocatedValue[agent_i]-unallocatedValue[agent_i]);
			daObjGrad[getPos(agent_i,1)]=dRondIndWelfare;
			//mem
			daJac[getJacChargeConstraintPos(agent_i, 1, true)]=getAgentMemorycharge(agent_i);
			//proc
			daJac[getJacChargeConstraintPos(agent_i, 1, false)]=getAgentProcessorCharge(agent_i);
			daJac[getLocalConstraintPos()]+=dRondIndWelfare;
		}
	}

	@Override
	protected void evaluateH(double[] daX, double[] daLambda, double[] daHess,
			boolean b) {
		// Nothing to do		
	}

	@Override
	protected int setNumNonNulJAc() {
		System.out.println(2*n);
		return 2*n+1;
	}//ET LOCAL ALORS?????

	@Override
	protected int setNumNonNulHess() {
		return 0;
	}

	@Override
	protected void setHessian(int[] hIndexRow, int[] hIndexCol) {
		// rien à faire
	}

	@Override
	protected void setJacobian(int[] jacIndexVars, int[] jacIndexCons) {
		for (int i = 0; i < n; i++){
			//mem de j : la case getPos(i,j) suivant les case de survie correspond à la contrainte de mem du jeme hote
			jacIndexVars[getJacChargeConstraintPos(i, 1,true)]=getPos(i, 1);
			jacIndexCons[getJacChargeConstraintPos(i, 1,true)]=getHostConstraintPos(1, true);

			//proc de j :la case getPos(i,j) suivant les case de survie et de mémoire correspond à la contrainte de proc du jeme hote
			jacIndexVars[getJacChargeConstraintPos(i, 1,false)]=getPos(i, 1);
			jacIndexCons[getJacChargeConstraintPos(i, 1,false)]=getHostConstraintPos(1, false);
		}
	}
}
