package negotiation.negotiationframework.exploration;

import java.util.Collection;

import negotiation.negotiationframework.contracts.MatchingCandidature;

import choco.Choco;
import choco.Options;
import choco.cp.model.CPModel;
import choco.cp.solver.CPSolver;
import choco.cp.solver.search.integer.valiterator.DecreasingDomain;
import choco.kernel.model.Model;
import choco.kernel.model.variables.integer.IntegerVariable;
import choco.kernel.solver.Solver;


public class AllocationSolver {

	public AllocationSolver(){
	
	}
	
	public void initiate(Collection<MatchingCandidature> concerned, AgentState currentState){
			Model m = new CPModel();
		IntegerVariable obj1 = Choco.makeIntVar("obj1", 0, 5,       Options.V_ENUM);
		IntegerVariable	obj2 = Choco.makeIntVar("obj2", 0, 7,       Options.V_ENUM);
		IntegerVariable	obj3 = Choco.makeIntVar("obj3", 0, 10,      Options.V_ENUM);
		IntegerVariable	c =    Choco.makeIntVar("cost", 1, 1000000, Options.V_BOUND);

		int  capacity = 34;
		int[] volumes = new int[]{7, 5, 3};
		int[] energy  = new int[]{6, 4, 2};
		m.addConstraint(Choco.leq(Choco.scalar(volumes, new IntegerVariable[]{obj1, obj2, obj3}), capacity));
		m.addConstraint(Choco.eq (Choco.scalar(energy,  new IntegerVariable[]{obj1, obj2, obj3}), c));
		Solver s = new CPSolver();
		s.read(m);

		s.maximize(s.getVar(c), false);
		s.setValIntIterator(new DecreasingDomain());
		s.solve()
	}
	public  Collection<MatchingCandidature<?>> getAllsolution(){
		
	}
	public MatchingCandidature<?> getBestSolution(){
		
	}
}
