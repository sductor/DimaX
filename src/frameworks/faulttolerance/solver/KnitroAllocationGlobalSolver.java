package frameworks.faulttolerance.solver;

import frameworks.negotiation.rationality.SocialChoiceFunction.SocialChoiceType;

public class KnitroAllocationGlobalSolver extends KnitroResourceAllocationSolver{




	//	static {
	////		System.setProperty("java.library.path",System.getProperty("java.library.path")+":"+LogService.getDimaXDir()+"lib/");// System.getProperty("java.library.path")+":"+
	////		System.out.println(System.getProperty("java.library.path"));
	//	}

	/*
	 * 
	 */

	/**
	 * 
	 */
	private static final long serialVersionUID = -7664321918415297517L;

	public KnitroAllocationGlobalSolver(final SocialChoiceType socialChoice,
			final boolean isAgent, final boolean isHost, final int algo, final boolean cplex,
			final int numberOfThreads) {
		super(socialChoice, isAgent, isHost, algo, cplex, numberOfThreads);
	}





	//
	//	public static void main (String[] args) throws Exception{
	//		KnitroAllocationSolver kas = new KnitroAllocationSolver();
	//		kas.n=4;
	//		kas.m=3;
	//		kas.socialChoice=SocialChoiceType.Nash;
	//		kas.initiate();
	//	}

	/** Compute the function and constraint values at x.
	 *
	 *  For more information about the arguments, refer to the KNITRO
	 *  manual, especially the section on the Callable Library.
	 */
	@Override
	protected double evaluateFC(final double[] daX, final double[] daC) {

		//constraints
		if (this.isAgent){
			//n first : survivability of agent i
			for (int agent_i = 0; agent_i < this.n; agent_i++){
				daC[this.getAgentConstraintPos(agent_i)]=this.getDispo(new RessourceAllocationSimpleSolutionType(daX), agent_i);
			}
		}

		if (this.isHost){
			//m next proc  of host i
			//m next mem  of host i
			for (int host_j = 0; host_j < this.m; host_j++){
				daC[this.getHostConstraintPos(host_j, true)]=this.getHostMemoryCharge(new RessourceAllocationSimpleSolutionType(daX), host_j);
				daC[this.getHostConstraintPos(host_j, false)]=this.getHostProcessorCharge(new RessourceAllocationSimpleSolutionType(daX), host_j);
			}
		}

		return this.getSocWelfare(new RessourceAllocationSimpleSolutionType(daX));
	}

	/** Compute the function and constraint first deriviatives at x.
	 *
	 *  For more information about the arguments, refer to the KNITRO
	 *  manual, especially the section on the Callable Library.
	 */
	@Override
	protected void evaluateGA(final double[]  daX, final double[]  daObjGrad, final double[]  daJac) {
		for (int agent_i = 0; agent_i < this.n; agent_i++){
			for (int host_j = 0; host_j < this.m; host_j++){
				//obj
				final double dRondSocWelfare = this.getDRondSocWelfare(new RessourceAllocationSimpleSolutionType(daX), agent_i, host_j);
				daObjGrad[this.getPos(agent_i,host_j)]=dRondSocWelfare;
				//surv
				if (this.isAgent){
					daJac[this.getJacSurvieConstraintPos(agent_i, host_j)]=1;
				}
				if (this.isHost){
					//mem
					daJac[this.getJacChargeConstraintPos(agent_i, host_j, true)]=this.getAgentMemorycharge(agent_i);
					//proc
					daJac[this.getJacChargeConstraintPos(agent_i, host_j, false)]=this.getAgentProcessorCharge(agent_i);
				}
				if (this.isLocal()){
					daJac[this.getLocalConstraintPos()]=dRondSocWelfare;
				}
			}
		}
	}

	/** Compute the Hessian of the Lagrangian at x and lambda.
	 *
	 *  For more information about the arguments, refer to the KNITRO
	 *  manual, especially the section on the Callable Library.
	 */
	@Override
	protected void evaluateH(final double[]  daX, final double[]  daLambda, final double[]  daHess, final boolean withObj) {
		int pos=0;
		if (this.socialChoice.equals(SocialChoiceType.Utility)){
			for (int agent_i = 0; agent_i < this.n; agent_i++){
				for (int host_j = 0; host_j < this.m; host_j++){
					for (int host_jp = host_j; host_jp < this.m; host_jp++){
						daHess[pos]=0;
						final double dRond2SocWelfare = this.getDRondDeuxSocWelfare(new RessourceAllocationSimpleSolutionType(daX), agent_i, host_j, agent_i, host_jp);
						if (withObj) {
							daHess[pos]+=dRond2SocWelfare;
						}

						if (this.isLocal()){
							daHess[pos]+=daLambda[this.getLocalConstraintPos()] * dRond2SocWelfare;
						}

						pos++;
					}
				}
			}
		} else if (this.socialChoice.equals(SocialChoiceType.Nash)){
			for (int agent_i = 0; agent_i < this.n;  agent_i++){
				for (int agent_ip =  agent_i;  agent_ip < this.n;  agent_ip++){
					for (int host_j = 0; host_j < this.m; host_j++){
						for (int host_jp = host_j; host_jp < this.m; host_jp++){
							daHess[pos]=0;
							final double dRond2SocWelfare = this.getDRondDeuxSocWelfare(new RessourceAllocationSimpleSolutionType(daX), agent_i, host_j, agent_ip, host_jp);
							if (withObj) {
								daHess[pos]+=dRond2SocWelfare;
							}

							if (this.isLocal()){
								daHess[pos]+=daLambda[this.getLocalConstraintPos()] * dRond2SocWelfare;
							}

							pos++;
						}
					}
				}
			}


		} else {
			assert this.socialChoice.equals(SocialChoiceType.Leximin);
			throw new RuntimeException();
		}
	}

	@Override
	protected int setNumNonNulJAc() {
		int jvarNonNul=0;
		if (this.isAgent) {
			jvarNonNul += this.m*this.n;
		}
		if (this.isHost) {
			jvarNonNul += 2*this.m*this.n;
		}
		return jvarNonNul;
	}//ET LOCAL ALORS?????

	@Override
	protected int setNumNonNulHess() {
		int hvarNonNul=0;
		if (this.socialChoice.equals(SocialChoiceType.Utility)){
			hvarNonNul=this.n*((this.m*this.m-this.m)/2+this.m);//pour chaque agent on a un carré sans diag de card (m*m-m)/2 et on rajoute la diag de m elem
		} else if (this.socialChoice.equals(SocialChoiceType.Nash)){
			hvarNonNul=this.n*this.m*this.n*this.m;
		} else {
			assert this.socialChoice.equals(SocialChoiceType.Leximin);
			throw new RuntimeException();
		}
		return hvarNonNul;
	}

	@Override
	protected void setHessian(final int[] hIndexRow, final int[] hIndexCol) {
		if (this.socialChoice.equals(SocialChoiceType.Utility)){
			int pos=0;
			for (int i = 0; i < this.n; i++){
				for (int j = 0; j < this.m; j++){
					for (int jp = j; jp < this.m; jp++){
						hIndexRow[pos]=this.getPos(i, j);
						hIndexCol[pos]=this.getPos(i, jp);
						pos++;
					}
				}
			}
		} else if (this.socialChoice.equals(SocialChoiceType.Nash)){
			int pos=0;
			for (int i = 0; i < this.n; i++){
				for (int ip = i; ip < this.n; ip++){
					for (int j = 0; j < this.m; j++){
						for (int jp = j; jp < this.m; jp++){
							hIndexRow[pos]=this.getPos(i, j);
							hIndexCol[pos]=this.getPos(ip, jp);
							pos++;
						}
					}
				}
			}


		} else {
			assert this.socialChoice.equals(SocialChoiceType.Leximin);
			throw new RuntimeException();
		}
	}

	@Override
	protected  void setJacobian(final int[] jacIndexVars, final int[] jacIndexCons) {
		for (int i = 0; i < this.n; i++){
			for (int j = 0; j < this.m; j++){
				if (this.isHost){
					//mem de j : la case getPos(i,j) suivant les case de survie correspond à la contrainte de mem du jeme hote
					jacIndexVars[this.getJacChargeConstraintPos(i, j,true)]=this.getPos(i, j);
					jacIndexCons[this.getJacChargeConstraintPos(i, j,true)]=this.getHostConstraintPos(j, true);

					//proc de j :la case getPos(i,j) suivant les case de survie et de mémoire correspond à la contrainte de proc du jeme hote
					jacIndexVars[this.getJacChargeConstraintPos(i, j,false)]=this.getPos(i, j);
					jacIndexCons[this.getJacChargeConstraintPos(i, j,false)]=this.getHostConstraintPos(j, false);
				}
				if (this.isAgent){
					//survie : la case getPos(i,j) correspond a la survie du ieme agent
					jacIndexVars[this.getJacSurvieConstraintPos(i,j)]=this.getPos(i, j);
					jacIndexCons[this.getJacSurvieConstraintPos(i,j)]=this.getAgentConstraintPos(i);
				}
			}
		}
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
