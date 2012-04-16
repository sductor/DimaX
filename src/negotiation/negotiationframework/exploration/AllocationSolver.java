package negotiation.negotiationframework.exploration;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import negotiation.faulttolerance.negotiatingagent.HostState;
import negotiation.faulttolerance.negotiatingagent.ReplicationCandidature;
import negotiation.negotiationframework.contracts.AbstractActionSpecification;
import negotiation.negotiationframework.contracts.AbstractContractTransition.IncompleteContractException;
import negotiation.negotiationframework.contracts.InformedCandidature;
import negotiation.negotiationframework.contracts.MatchingCandidature;
import negotiation.negotiationframework.rationality.SocialChoiceFunction;

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

	Solver s;
	Model m; 
	List<? extends ReplicationCandidature> concerned;
	
	public void initiate(List<? extends ReplicationCandidature> concerned, HostState currentState, final String socialWelfare) throws IncompleteContractException{
		m = new CPModel();
		this.concerned=concerned;
		
		int nbVariable = concerned.size();
		
		IntegerVariable[]	replicas = new IntegerVariable[nbVariable];
		IntegerVariable	c =  Choco.makeIntVar("utility", 1, 1000000, Options.V_BOUND, Options.V_NO_DECISION);
		
		int[] replicasMinUtil = new int[nbVariable];
		int[] replicasGain = new int[nbVariable];
		int[] replicasProc = new int[nbVariable];
		int[] replicasMem = new int[nbVariable];
		
		int hostProccapacity =(int)  (100 *currentState.getProcChargeMax());
		int hostMemCapacity = (int)  (100 *currentState.getMemChargeMax());		
		
		for (int i = 0; i < nbVariable; i++){
			replicas[i] = Choco.makeIntVar(concerned.get(i).getAgent().toString(), 0, 1, Options.V_ENUM);
			int minUt = (int) (100 *Math.min(
					concerned.get(i).getAgentInitialState().getMyReliability(),
					concerned.get(i).getAgentResultingState().getMyReliability()));
			int maxUt = (int) (100 * Math.max(
					concerned.get(i).getAgentInitialState().getMyReliability(),
					concerned.get(i).getAgentResultingState().getMyReliability()));
			replicasMinUtil[i] = minUt;
			replicasGain[i] = maxUt-minUt;
			assert concerned.get(i).getAgentInitialState().getMyMemCharge().equals(concerned.get(i).getAgentResultingState().getMyMemCharge());
			replicasMem[i] = (int) (100 * concerned.get(i).getAgentInitialState().getMyMemCharge());
			assert concerned.get(i).getAgentInitialState().getMyProcCharge().equals(concerned.get(i).getAgentResultingState().getMyProcCharge());
			replicasProc[i] = (int) (100 * concerned.get(i).getAgentInitialState().getMyProcCharge());
		}
		
		
		m.addConstraint(Choco.leq(Choco.scalar(replicasProc, replicas), hostProccapacity));
		m.addConstraint(Choco.leq(Choco.scalar(replicasMem, replicas), hostMemCapacity));

		if (socialWelfare.equals(SocialChoiceFunction.key4leximinSocialWelfare)) {
			throw new RuntimeException("todo "+socialWelfare);
		} else if (socialWelfare.equals(SocialChoiceFunction.key4NashSocialWelfare)) {
			throw new RuntimeException("todo : "+socialWelfare);
		} else if (socialWelfare.equals(SocialChoiceFunction.key4UtilitaristSocialWelfare)) {
			m.addConstraint(Choco.eq (Choco.scalar(replicasGain,  replicas), c));
		} else {
			throw new RuntimeException("impossible key for social welfare is : "+socialWelfare);
		}
		
		s = new CPSolver();
		s.read(m);		
		s.maximize(s.getVar(c), false);
		s.setValIntIterator(new DecreasingDomain());
	}
	
	public  Collection<MatchingCandidature<?>> getAllSolution(){
		
	}
	
	public MatchingCandidature<?> getBestSolution(){
		s.solve();	
	}
	
	private Collection<MatchingCandidature<?>> generateSolution(){
		ArrayList<MatchingCandidature<?>> results = new ArrayList<MatchingCandidature<?>>();
		for (MatchingCandidature<?> c : concerned){
			s.getVar(v)
		}
	}
}
