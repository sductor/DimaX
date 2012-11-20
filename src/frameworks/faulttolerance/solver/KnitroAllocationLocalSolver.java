package frameworks.faulttolerance.solver;

import frameworks.negotiation.rationality.SocialChoiceFunction.SocialChoiceType;

public class KnitroAllocationLocalSolver extends KnitroResourceAllocationSolver {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7802524017704264019L;

	public KnitroAllocationLocalSolver(final SocialChoiceType socialChoice,
			final boolean isAgent, final boolean isHost, final int algo, final boolean cplex,
			final int numberOfThreads) {
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
		assert this.m==1;
		super.initiateSolverPost();
		for (int i = 0; i < this.n; i++){
			this.unallocatedValue[i] = this.rig.getAgentState(this.getAgentIdentifier(i)).allocate(this.myState, false).getMyReliability();
			this.allocatedValue[i] = this.rig.getAgentState(this.getAgentIdentifier(i)).allocate(this.myState, true).getMyReliability();
		}
	}

	/*
	 * 
	 */



	@Override
	protected double evaluateFC(final double[] daX, final double[] daC) {
		daC[this.getHostConstraintPos(1, true)]=this.getHostMemoryCharge(new RessourceAllocationSimpleSolutionType(daX), 1);
		daC[this.getHostConstraintPos(1, false)]=this.getHostProcessorCharge(new RessourceAllocationSimpleSolutionType(daX), 1);
		double result = 0;
		for (int i = 0; i < this.n; i++){
			result+= daX[i]*(this.allocatedValue[i]-this.unallocatedValue[i])+this.unallocatedValue[i];
		}
		daC[this.getLocalConstraintPos()]=result;

		return result;
	}
	@Override
	protected void evaluateGA(final double[] daX, final double[] daObjGrad, final double[] daJac) {
		daJac[this.getLocalConstraintPos()]=0;
		for (int agent_i = 0; agent_i < this.n; agent_i++){
			//obj
			final double dRondIndWelfare = this.allocatedValue[agent_i]-this.unallocatedValue[agent_i];
			daObjGrad[this.getPos(agent_i,1)]=dRondIndWelfare;
			//mem
			daJac[this.getJacChargeConstraintPos(agent_i, 1, true)]=this.getAgentMemorycharge(agent_i);
			//proc
			daJac[this.getJacChargeConstraintPos(agent_i, 1, false)]=this.getAgentProcessorCharge(agent_i);
			daJac[this.getLocalConstraintPos()]+=dRondIndWelfare;
		}
	}

	@Override
	protected void evaluateH(final double[] daX, final double[] daLambda, final double[] daHess,
			final boolean b) {
		// Nothing to do
	}

	@Override
	protected int setNumNonNulJAc() {
		System.out.println(2*this.n);
		return 2*this.n+1;
	}//ET LOCAL ALORS?????

	@Override
	protected int setNumNonNulHess() {
		return 0;
	}

	@Override
	protected void setHessian(final int[] hIndexRow, final int[] hIndexCol) {
		// rien à faire
	}

	@Override
	protected void setJacobian(final int[] jacIndexVars, final int[] jacIndexCons) {
		for (int i = 0; i < this.n; i++){
			//mem de j : la case getPos(i,j) suivant les case de survie correspond à la contrainte de mem du jeme hote
			jacIndexVars[this.getJacChargeConstraintPos(i, 1,true)]=this.getPos(i, 1);
			jacIndexCons[this.getJacChargeConstraintPos(i, 1,true)]=this.getHostConstraintPos(1, true);

			//proc de j :la case getPos(i,j) suivant les case de survie et de mémoire correspond à la contrainte de proc du jeme hote
			jacIndexVars[this.getJacChargeConstraintPos(i, 1,false)]=this.getPos(i, 1);
			jacIndexCons[this.getJacChargeConstraintPos(i, 1,false)]=this.getHostConstraintPos(1, false);
		}
	}
}
