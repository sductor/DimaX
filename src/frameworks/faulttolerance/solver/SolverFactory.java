package frameworks.faulttolerance.solver;

import frameworks.negotiation.exploration.ResourceAllocationSolver;
import frameworks.negotiation.rationality.SocialChoiceFunction.SocialChoiceType;

public class SolverFactory {

	public enum SolverType {Dpop, BranchNBound, Mixed, Knitro, Choco, GA};

	private static SolverType solvertype=SolverType.GA;//Choco;//BranchNBound;//

	/*
	 * 
	 */

	public static void setParameters(final SolverType solvertype){
		SolverFactory.solvertype=	solvertype;
	}


	/*
	 * 
	 */

	//	public static HashMap<Integer, Integer> solve(DcopReplicationGraph drg)  {
	//		switch (solvertype) {
	//		case Dpop:
	//			return new DcopDPOPSolver().solve(drg);
	//		case BranchNBound :
	//			return new DcopBranchAndBoundSolver(drg.getSocialWelfare(),false,true).solve(drg);
	//		case Mixed :
	//			if (drg.conList.size() > drg.varMap.size() * drg.varMap.size() / 4)
	//				return new DcopBranchAndBoundSolver(drg.getSocialWelfare(),false,true).solve(drg);
	//			else
	//				return new DcopDPOPSolver().solve(drg);
	//		case Knitro :
	//			ResourceAllocationInterface kas = new KnitroAllocationSolver(drg.getSocialWelfare(),true,true,1,false,-1);
	//			return kas.solve(drg);
	//		case Choco :
	//			return new ChocoReplicationAllocationSolver(drg.getSocialWelfare()).solve(drg);
	//		case GA :
	//			return new JMetalSolver(drg.getSocialWelfare(), true, true).solve(drg);
	//		default :
	//			throw new RuntimeException();
	//		}
	//	}

	public static ResourceAllocationSolver getGlobalSolver(final SocialChoiceType socialWelfare) {
		switch (SolverFactory.solvertype) {
		case Dpop:
			return null;//new DcopDPOPSolver();
			//		case BranchNBound :
			//			return new DcopBranchAndBoundSolver(socialWelfare,true,true);
		case Mixed :
			return null;
		case Knitro :
			return new KnitroAllocationGlobalSolver(socialWelfare,true,true,1,false,-1);
			//		case Choco :
			//			return new ChocoReplicationAllocationSolver(socialWelfare);
		case GA :
			return new JMetalSolver(socialWelfare, true, true);
		default :
			throw new RuntimeException();
		}
	}
	public static ResourceAllocationSolver getHostSolver(final SocialChoiceType socialWelfare) {
		switch (SolverFactory.solvertype) {
		case Dpop:
			return null;//new DcopDPOPSolver();
			//		case BranchNBound :
			//			return new DcopBranchAndBoundSolver(socialWelfare,false,true);
		case Mixed :
			return null;
		case Knitro :
			return new KnitroAllocationGlobalSolver(socialWelfare,false,true,1,false,-1);
			//		case Choco :
			//			return new ChocoReplicationAllocationSolver(socialWelfare);
		case GA :
			return new JMetalSolver(socialWelfare, false, true);
		default :
			throw new RuntimeException();
		}
	}
}
