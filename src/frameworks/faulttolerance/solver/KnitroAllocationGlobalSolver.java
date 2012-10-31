package frameworks.faulttolerance.solver;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Random;

import com.ziena.knitro.KnitroJava;

import dima.basicagentcomponents.AgentIdentifier;
import dima.introspectionbasedagents.modules.distribution.NormalLaw.DispersionSymbolicValue;
import dima.introspectionbasedagents.modules.mappedcollections.HashedHashSet;
import dima.introspectionbasedagents.services.loggingactivity.LogService;
import frameworks.experimentation.IfailedException;
import frameworks.faulttolerance.experimentation.ReplicationGraph;
import frameworks.faulttolerance.experimentation.ReplicationInstanceGraph;
import frameworks.faulttolerance.negotiatingagent.HostState;
import frameworks.faulttolerance.negotiatingagent.ReplicationCandidature;
import frameworks.faulttolerance.negotiatingagent.ReplicationSocialOptimisation;
import frameworks.faulttolerance.olddcop.DcopSolver;
import frameworks.negotiation.contracts.ResourceIdentifier;
import frameworks.negotiation.exploration.ResourceAllocationSolver;
import frameworks.negotiation.exploration.Solver;
import frameworks.negotiation.exploration.Solver.UnsatisfiableException;
import frameworks.negotiation.rationality.AgentState;
import frameworks.negotiation.rationality.SocialChoiceFunction.SocialChoiceType;

public class KnitroAllocationGlobalSolver extends KnitroResourceAllocationSolver{




	//	static {
	////		System.setProperty("java.library.path",System.getProperty("java.library.path")+":"+LogService.getDimaXDir()+"lib/");// System.getProperty("java.library.path")+":"+
	////		System.out.println(System.getProperty("java.library.path"));
	//	}

	/*
	 * 
	 */

	public KnitroAllocationGlobalSolver(SocialChoiceType socialChoice,
			boolean isAgent, boolean isHost, int algo, boolean cplex,
			int numberOfThreads) {
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
	protected double evaluateFC(double[] daX, double[] daC) {

		//constraints
		if (isAgent){
			//n first : survivability of agent i
			for (int agent_i = 0; agent_i < n; agent_i++){
				daC[getAgentConstraintPos(agent_i)]=getDispo(new RessourceAllocationSimpleSolutionType(daX), agent_i);					
			}
		}

		if (isHost){
			//m next proc  of host i
			//m next mem  of host i	
			for (int host_j = 0; host_j < m; host_j++){
				daC[getHostConstraintPos(host_j, true)]=getHostMemoryCharge(new RessourceAllocationSimpleSolutionType(daX), host_j);
				daC[getHostConstraintPos(host_j, false)]=getHostProcessorCharge(new RessourceAllocationSimpleSolutionType(daX), host_j);
			}
		}	

		return getSocWelfare(new RessourceAllocationSimpleSolutionType(daX));
	}

	/** Compute the function and constraint first deriviatives at x.
	 *
	 *  For more information about the arguments, refer to the KNITRO
	 *  manual, especially the section on the Callable Library.
	 */
	protected void evaluateGA(double[]  daX, double[]  daObjGrad, double[]  daJac) {
		for (int agent_i = 0; agent_i < n; agent_i++){
			for (int host_j = 0; host_j < m; host_j++){
				//obj
				double dRondSocWelfare = getDRondSocWelfare(new RessourceAllocationSimpleSolutionType(daX), agent_i, host_j);
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
	protected void evaluateH(double[]  daX, double[]  daLambda, double[]  daHess, boolean withObj) {
		int pos=0;
		if (socialChoice.equals(SocialChoiceType.Utility)){
			for (int agent_i = 0; agent_i < n; agent_i++){
				for (int host_j = 0; host_j < m; host_j++){
					for (int host_jp = host_j; host_jp < m; host_jp++){
						daHess[pos]=0;
						double dRond2SocWelfare = getDRondDeuxSocWelfare(new RessourceAllocationSimpleSolutionType(daX), agent_i, host_j, agent_i, host_jp);
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
							double dRond2SocWelfare = getDRondDeuxSocWelfare(new RessourceAllocationSimpleSolutionType(daX), agent_i, host_j, agent_ip, host_jp);
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

	protected int setNumNonNulJAc() {
		int jvarNonNul=0;
		if (isAgent)
			jvarNonNul += (m*n);
		if (isHost)
			jvarNonNul += 2*(m*n);
		return jvarNonNul;
	}//ET LOCAL ALORS?????

	protected int setNumNonNulHess() {
		int hvarNonNul=0;
		if (socialChoice.equals(SocialChoiceType.Utility)){
			hvarNonNul=n*((m*m-m)/2+m);//pour chaque agent on a un carré sans diag de card (m*m-m)/2 et on rajoute la diag de m elem
		} else if (socialChoice.equals(SocialChoiceType.Nash)){
			hvarNonNul=n*m*n*m;
		} else {
			assert (socialChoice.equals(SocialChoiceType.Leximin));
			throw new RuntimeException();
		}
		return hvarNonNul;
	}

	protected void setHessian(int[] hIndexRow, int[] hIndexCol) {
		if (socialChoice.equals(SocialChoiceType.Utility)){
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
	}

	protected  void setJacobian(int[] jacIndexVars, int[] jacIndexCons) {
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
