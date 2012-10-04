package frameworks.faulttolerance.solver;

import java.util.HashMap;

import frameworks.faulttolerance.dcop.DCOPFactory;
import frameworks.faulttolerance.dcop.dcop.CPUFreeConstraint;
import frameworks.faulttolerance.dcop.dcop.DcopReplicationGraph;
import frameworks.faulttolerance.dcop.dcop.MemFreeConstraint;
import frameworks.faulttolerance.dcop.dcop.MemFreeConstraint;
import frameworks.faulttolerance.dcop.dcop.ReplicationVariable;
import frameworks.negotiation.exploration.ResourceAllocationSolver;
import frameworks.negotiation.exploration.Solver.ExceedLimitException;
import frameworks.negotiation.exploration.Solver.UnsatisfiableException;
import frameworks.negotiation.rationality.AgentState;
import frameworks.negotiation.rationality.SocialChoiceFunction.SocialChoiceType;

public class SolverFactory {

	public enum SolverType {Dpop, BranchNBound, Mixed, Knitro, Choco};

	private static SolverType solvertype=SolverType.Knitro;//Choco;//BranchNBound;//

	/*
	 * 
	 */
	
	public static void setParameters(SolverType solvertype){
		SolverFactory.solvertype=	solvertype;
	}


	/*
	 * 
	 */
		
	public static HashMap<Integer, Integer> solve(DcopReplicationGraph drg)  {
		switch (solvertype) {
		case Dpop:
			return new DcopDPOPSolver().solve(drg);
		case BranchNBound :
			return new DcopBranchAndBoundSolver(drg.getSocialWelfare()).solve(drg);
		case Mixed :	
			if (drg.conList.size() > drg.varMap.size() * drg.varMap.size() / 4)
				return new DcopBranchAndBoundSolver(drg.getSocialWelfare()).solve(drg);
			else
				return new DcopDPOPSolver().solve(drg);
		case Knitro :
			ResourceAllocationInterface kas = new KnitroAllocationSolver(drg.getSocialWelfare(),true,true,1,false,-1);
			return kas.solve(drg);
		case Choco :
			return new ChocoReplicationAllocationSolver(drg.getSocialWelfare()).solve(drg);
		default :
			throw new RuntimeException();
		}
	}


	public static ResourceAllocationSolver getLocalSolver(SocialChoiceType socialWelfare) {
		switch (solvertype) {
		case Dpop:
			return null;//new DcopDPOPSolver();
		case BranchNBound :
			return new DcopBranchAndBoundSolver(socialWelfare);
		case Mixed :	
				return null;
		case Knitro :
			return new KnitroAllocationSolver(socialWelfare,false,true,1,false,-1);
		case Choco :
			return new ChocoReplicationAllocationSolver(socialWelfare);
		default :
			throw new RuntimeException();
		}
	}
}
