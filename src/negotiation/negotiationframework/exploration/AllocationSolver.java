package negotiation.negotiationframework.exploration;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import negotiation.faulttolerance.negotiatingagent.HostState;
import negotiation.faulttolerance.negotiatingagent.ReplicationCandidature;
import negotiation.negotiationframework.contracts.AbstractActionSpecification;
import negotiation.negotiationframework.contracts.InformedCandidature;
import negotiation.negotiationframework.contracts.MatchingCandidature;

import choco.Choco;
import choco.Options;
import choco.cp.model.CPModel;
import choco.cp.solver.CPSolver;
import choco.cp.solver.search.integer.valiterator.DecreasingDomain;
import choco.kernel.model.Model;
import choco.kernel.model.ModelException;
import choco.kernel.model.variables.Operator;
import choco.kernel.model.variables.integer.IntegerExpressionVariable;
import choco.kernel.model.variables.integer.IntegerVariable;
import choco.kernel.model.variables.real.RealExpressionVariable;
import choco.kernel.model.variables.real.RealVariable;
import choco.kernel.solver.Solver;
import dima.introspectionbasedagents.services.BasicAgentModule;


public class AllocationSolver<
Contract extends MatchingCandidature<ActionSpec>,
ActionSpec extends AbstractActionSpecification,
PersonalState extends ActionSpec> extends BasicAgentModule {

	public AllocationSolver(){
	
	}
	
	public void initiate(List<? extends ReplicationCandidature> concerned, HostState currentState){
		Model m = new CPModel();
		ArrayList<IntegerVariable>	replicas = new ArrayList<IntegerVariable>();
		ArrayList<Double> replicasMinUtil = new ArrayList<Double>();
		ArrayList<Double> replicasGain = new ArrayList<Double>();
		ArrayList<Double> replicasProc = new ArrayList<Double>();
		ArrayList<Double> replicasMem = new ArrayList<Double>();
		
		for (ReplicationCandidature c : concerned){
			replicas.add(Choco.makeIntVar(c.getAgent().toString(), 0, 1, Options.V_ENUM));
			double minUt = Math.min(
					c.getAgentInitialState().getMyReliability(),
					c.getAgentResultingState().getMyReliability());
			double maxUt = Math.max(
					c.getAgentInitialState().getMyReliability(),
					c.getAgentResultingState().getMyReliability());
			replicasMinUtil.add(minUt);
			replicasGain.add(maxUt-minUt);
			assert c.getAgentInitialState().getMyMemCharge().equals(c.getAgentResultingState().getMyMemCharge());
			replicasMem.add(c.getAgentInitialState().getMyMemCharge());
			assert c.getAgentInitialState().getMyProcCharge().equals(c.getAgentResultingState().getMyProcCharge());
			replicasProc.add(c.getAgentInitialState().getMyProcCharge());
		}
		
		double hostProccapacity = currentState.getProcChargeMax();
		double hostMemCapacity = currentState.getMemChargeMax();		
		IntegerVariable	c =  Choco.makeIntVar("cost", 1, 1000000, Options.V_BOUND);
		
		m.addConstraint(Choco.leq(Choco.scalar(replicasProc.toArray(), replicas.toArray(), hostProccapacity)));
		m.addConstraint(Choco.leq(Choco.scalar(replicasProc.toArray(), replicas.toArray(), hostMemCapacity)));
		m.addConstraint(Choco.eq (Choco.scalar(energy,  new IntegerVariable[]{obj1, obj2, obj3}), c));
		
		
		IntegerVariable obj1 = 
		IntegerVariable	obj2 = Choco.makeIntVar("obj2", 0, 7,       Options.V_ENUM);
		IntegerVariable	obj3 = Choco.makeIntVar("obj3", 0, 10,      Options.V_ENUM);

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
	public  Collection<MatchingCandidature<?>> getAllSolution(){
		
	}
	public MatchingCandidature<?> getBestSolution(){
		
	}
    public static RealExpressionVariable scalar(double[] lc, RealVariable[] lv) {
        if (lc.length != lv.length) {
            throw new ModelException("scalar: parameters length are differents");
        }
        RealVariable[] tmp = new RealVariable[lc.length + lv.length];
        for (int i = 0; i < lc.length; i++) {
            tmp[i] = constant(lc[i]);
        }
        arraycopy(lv, 0, tmp, lc.length, lv.length);
        return new RealExpressionVariable(null, Operator.SCALAR, tmp);
    }

}
