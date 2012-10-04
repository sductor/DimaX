package frameworks.faulttolerance.solver;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;


import dima.introspectionbasedagents.modules.distribution.NormalLaw.DispersionSymbolicValue;
import dima.introspectionbasedagents.services.loggingactivity.LogService;

import frameworks.experimentation.IfailedException;
import frameworks.faulttolerance.experimentation.ReplicationInstanceGraph;
import frameworks.faulttolerance.negotiatingagent.ReplicaState;
import frameworks.faulttolerance.solver.jmetal.core.Solution;
import frameworks.faulttolerance.solver.jmetal.util.PseudoRandom;
import frameworks.negotiation.rationality.SocialChoiceFunction.SocialChoiceType;

public class SolverTEster {

	public static void main(String[] args) throws Exception {
		Random rand = new Random(65646);
		PseudoRandom.seed=rand.nextDouble();

		boolean knitro=false;
		boolean metal=true; 
		boolean bbTest=false;
		boolean bestTest=true;
		

		double knitroOp=0;
		double knitorApprox=0.;
		double knitorfaux=0 ; 
		double knitorEstimatedTime=0 ;

		double metalOpt=0;   
		double metalfaux=0 ;   
		double metalApprox=0.; 
		double metalEstimatedTime=0 ;

		double bbOpt=0;   
		double bbfaux=0 ;   
		double bbApprox=0.; 
		double bbEstimatedTime=0 ;


		double initTime; 
		double total=0; 
		double metalBetterKnitro=0;
		LogService.logOnFile(new File("yo"),"*************************",false,false);
			
		int nbAgent=30;
		double hostCap=200;
		for (hostCap=1; hostCap<31; hostCap+=5)  {
			
//		for (int heuristic=1; heuristic<=2; heuristic++){
//			System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!"+heuristic);
//			try {
				for (int i = 0; i < 10; i++){
//					System.out.println("Heurisitic "+heuristic+" tour "+i);
					total++;
					ReplicationInstanceGraph rig = new ReplicationInstanceGraph(SocialChoiceType.Utility);

					try {
						rig.randomInitiaition(
								"to", rand.nextInt(),
								/*5+rand.nextInt(5)*/nbAgent, 1,1000,//nbAgent,nbHost
								0.5, DispersionSymbolicValue.Moyen, //criticity
								1., DispersionSymbolicValue.Nul, //agent load
								hostCap, DispersionSymbolicValue.Nul, //hostCap
								0.5, DispersionSymbolicValue.Moyen, //hostDisp
								100,100);
					} catch (IfailedException e1) {
						//				e1.printStackTrace();
					}
					rig = rig.getUnallocatedGraph();
					//			System.out.println(rig);
					//
					// Opt
					//
					KnitroAllocationSolver kas2 = null;
					double[] bestPossible = null;
					if (bestTest){
					List<ReplicaState> best = new ArrayList<ReplicaState>();
					kas2 = new KnitroAllocationSolver(rig.getSocialWelfare(),  rig.getHostsIdentifier().size()>1, true, 2, false, -1);
					kas2.myState=rig.getHostsStates().iterator().next();
					kas2.setProblem(rig);
					if(rig.getHostsIdentifier().size()==1)	kas2.intialSolution=kas2.getInitialAllocAsSolution(new double[kas2.getVariableNumber()]);
					kas2.initiateSolver();
					bestPossible = kas2.getBestTriviaSol(best,hostCap);
					}
					//
					// Knitro
					//

					SimpleSolutionType kasbestSolution=null;
					KnitroAllocationSolver kas = null;
					if (knitro){
						//		KnitroAllocationSolver kas = new KnitroAllocationSolver(rig.getSocialWelfare(), false, true, 5, false, 5);
						initTime = System.currentTimeMillis();
						kas = new KnitroAllocationSolver(rig.getSocialWelfare(),  rig.getHostsIdentifier().size()>1, true, 2, false, -1);
						kas.myState=rig.getHostsStates().iterator().next();
						kas.setProblem(rig);
						if(rig.getHostsIdentifier().size()==1)	kas.intialSolution=kas.getInitialAllocAsSolution(new double[kas.getVariableNumber()]);
						kas.initiateSolver();
						kasbestSolution = kas.solveProb(true);
						knitorEstimatedTime += System.currentTimeMillis() - initTime;
					}

					//
					// Metal
					//

					Solution jmsbestSolution=null;
					RessAllocJMetalSolver jms=null;
					if (metal){
						initTime = System.currentTimeMillis();
						jms = new RessAllocJMetalSolver(rig.getSocialWelfare(), rig.getHostsIdentifier().size()>1, true);
						jms.myState=rig.getHostsStates().iterator().next();
						jms.setProblem(rig);
						jms.initiateSolver();						
						if(rig.getHostsIdentifier().size()==1)jms.intialSolution=jms.getInitialAllocAsSolution(new double[jms.getVariableNumber()]);
						jmsbestSolution = jms.solveProb(true);
						metalEstimatedTime += System.currentTimeMillis() - initTime;
					}


					//
					// BB
					//

					HashMap<Integer, Integer> bbbestSolution=null;
					DcopBranchAndBoundSolver bb=null;
					if (bbTest){
						initTime = System.currentTimeMillis();
						bb = new DcopBranchAndBoundSolver(rig.getSocialWelfare(),  rig.getHostsIdentifier().size()>1, true);
						bb.myState=rig.getHostsStates().iterator().next();
						bb.setProblem(rig);
						bb.initiateSolver();
						if(rig.getHostsIdentifier().size()==1)bb.intialSolution=bb.getInitialAllocAsSolution(new double[jms.getVariableNumber()]);
						bbbestSolution = bb.solveProb(true);
						bbEstimatedTime += System.currentTimeMillis() - initTime;
					}
					//
					// Result
					// 

					if (knitro)System.out.println("kas best     !!!!!!!!!!!!!!!! "+kas.getSocWelfare(kasbestSolution)+"\t\t"+kas.isViable(kasbestSolution)+"\t"+kas.print(kasbestSolution));
					if (metal)System.out.println("jms best      !!!!!!!!!!!!!!! "+jms.getSocWelfare(jmsbestSolution)+"\t\t"+jms.isViable(jmsbestSolution)+"\t"+jms.print(jmsbestSolution));
					if (bbTest)System.out.println("bb best     !!!!!!!!!!!!!!!! "+bb.getSocWelfare(bbbestSolution)+"\t\t"+bb.isViable(bbbestSolution)+"\t"+bb.print(bbbestSolution));

					if (bestTest)
					System.out.println("best possible !!!!!!!!!!!!!!! "+" "+kas2.getSocWelfare(new SimpleSolutionType(bestPossible))
							+"\t\t\t"+kas2.print(new SimpleSolutionType(bestPossible)));

					if (knitro && bestTest){
						if (kas.getSocWelfare(kasbestSolution)==kas.getSocWelfare(kas.getInitialAllocAsSolution(bestPossible)))
							knitroOp++;
						if (!kas.isViable(kasbestSolution)){
							knitorfaux++;
						}

						knitorApprox+=kas.getSocWelfare(kasbestSolution)/kas.getSocWelfare(kas.getInitialAllocAsSolution(bestPossible));
					}
					if (metal && bestTest){
						if (jms.getSocWelfare(jmsbestSolution)==jms.getSocWelfare(jms.getInitialAllocAsSolution(bestPossible)))
							metalOpt++;
						if (!jms.isViable(jmsbestSolution))
							metalfaux++;

						metalApprox+=jms.getSocWelfare(jmsbestSolution)/jms.getSocWelfare(jms.getInitialAllocAsSolution(bestPossible));
					}
					if (bbTest && bestTest){
						if (bb.getSocWelfare(bbbestSolution)==kas2.getSocWelfare(kas2.getInitialAllocAsSolution(bestPossible)))
							bbOpt++;
						if (!bb.isViable(bbbestSolution)){
							bbfaux++;
						}

						bbApprox+=bb.getSocWelfare(bbbestSolution)/kas2.getSocWelfare(kas2.getInitialAllocAsSolution(bestPossible));
					}
					if (knitro && metal){
						if (jms.getSocWelfare(jmsbestSolution)>kas.getSocWelfare(kasbestSolution))
							metalBetterKnitro++;
					}
				}

				if (knitro)	System.out.println("KNITRO  :  OPT = "+knitroOp/total+" FAUX ="+knitorfaux/total
						+" APPROX = "+knitorApprox/total+" TIME "+knitorEstimatedTime/total);
//				if (metal)LogService.logOnFile(new File("yo"), "NBAGENT "+nbAgent+" JMS     :  OPT = "+metalOpt/total+" FAUX ="+metalfaux/total
//						+" APPROX = "+metalApprox/total+" TIME "+metalEstimatedTime/total+" HEURISTIC = "+heuristic,true,true);
				if (metal)LogService.logOnFile(new File("yo"), "NBAGENT "+nbAgent+" HOSTCAP "+hostCap+" JMS     :  OPT = "+metalOpt/total+" FAUX ="+metalfaux/total
				+" APPROX = "+metalApprox/total+" TIME "+metalEstimatedTime/total,true,true);
				if (bbTest)System.out.println("JMS     :  OPT = "+bbOpt/total+" FAUX ="+bbfaux/total
						+" APPROX = "+bbApprox/total+" TIME "+bbEstimatedTime/total);
	}
//			} catch (Exception e){
//				System.out.println("heuristic number "+heuristic+" has raised "+e);
//				e.printStackTrace();
//			}
		}
	}
//}





	//		System.out.println("charge "+kas.getMemCharge(bestSolution, 0)+"  max  "+kas.getMemCharge(new double[]{1.,1.,1.,1.}, 0));
	//		System.out.println(print(kas.agents));





	//			

	//		System.out.println("1 1 1 0  !!!!!!!!!!!!!!!! "+kas.getSocWelfare(new double[]{1.,1.,1.,0.}));
	//		System.out.println("0 1 1 1  !!!!!!!!!!!!!!!! "+kas.getSocWelfare(new double[]{0.,1.,1.,1.}));
	//		System.out.println("1 1 0 0 !!!!!!!!!!!!!!!! "+kas.getSocWelfare(new double[]{1.,1.,0.,0.}));
	//		System.out.println("1 0 0 0 !!!!!!!!!!!!!!!! "+kas.getSocWelfare(new double[]{1.,0.,0.,0.}));
	//		System.out.println("0 0 0 0 !!!!!!!!!!!!!!!! "+kas.getSocWelfare(new double[]{0.,0.,0.,0.}));
	//		for (AgentState s : kas.getAllocation(bestSolution).values()){
	//			if (s instanceof HostState)
	//				System.out.println(s);
	//		}
	//		System.out.println(best);
	//		for (int i = 1; i < 5; i++)
	//		for (AgentState s : kas.getAllocation(kas.getBestSolution()).values()){
	//			if (s instanceof HostState)
	//				System.out.println(s);
	//		}
	//		System.out.println(kas.getSolution(kas.bestSolution));
	//		}
	//		System.out.println("alors???????"+knitroOp+" "+((double)metalOpt)/((double)total)+" "+metalBetterKnitro);
	//	}

	//}
