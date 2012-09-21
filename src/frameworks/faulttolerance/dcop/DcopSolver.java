package frameworks.faulttolerance.dcop;

import java.util.HashMap;

import dima.basicinterfaces.DimaComponentInterface;


import frameworks.faulttolerance.dcop.dcop.DcopReplicationGraph;
import frameworks.negotiation.exploration.Solver;
import frameworks.negotiation.exploration.Solver.ExceedLimitException;
import frameworks.negotiation.exploration.Solver.UnsatisfiableException;

public interface DcopSolver extends DimaComponentInterface{

	public HashMap<Integer, Integer> solve(DcopReplicationGraph drg) throws UnsatisfiableException, ExceedLimitException;
}
