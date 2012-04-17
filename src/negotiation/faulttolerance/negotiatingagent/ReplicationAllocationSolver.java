package negotiation.faulttolerance.negotiatingagent;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

import choco.Choco;
import choco.Options;
import choco.cp.model.CPModel;
import choco.cp.solver.CPSolver;
import choco.cp.solver.search.integer.valiterator.DecreasingDomain;
import choco.kernel.model.Model;
import choco.kernel.model.variables.integer.IntegerConstantVariable;
import choco.kernel.model.variables.integer.IntegerExpressionVariable;
import choco.kernel.model.variables.integer.IntegerVariable;
import choco.kernel.solver.Solver;
import dima.basicagentcomponents.AgentName;
import negotiation.negotiationframework.contracts.AbstractContractTransition.IncompleteContractException;
import negotiation.negotiationframework.contracts.ResourceIdentifier;
import negotiation.negotiationframework.exploration.ChocoAllocationSolver;
import negotiation.negotiationframework.rationality.SocialChoiceFunction;

public class ReplicationAllocationSolver
extends ChocoAllocationSolver
<ReplicationCandidature, ReplicationSpecification, HostState>{
	private static final long serialVersionUID = 161669049253111527L;

	public ReplicationAllocationSolver(String socialWelfare) {
		super(socialWelfare);
	}

	final boolean multiDim = true;

	public void initiate(
			Collection<ReplicationCandidature> concerned, 
			HostState currentState){
		try {
			Model m = new CPModel();
			this.concerned=concerned.toArray(new ReplicationCandidature[concerned.size()]);

			int nbVariable = concerned.size();

			replicas = new IntegerVariable[nbVariable];
			socialWelfareValue =  Choco.makeIntVar("utility", 1, 1000000, Options.V_BOUND, Options.V_NO_DECISION);
			IntegerVariable[] replicasValue = new IntegerVariable[nbVariable];

			int[] replicasGain = new int[nbVariable];
			int[] replicasProc = new int[nbVariable];
			int[] replicasMem = new int[nbVariable];

			int hostProccapacity =(int)  (100 *currentState.getProcChargeMax());
			int hostMemCapacity = (int)  (100 *currentState.getMemChargeMax());		

			//initialisation des variables replicas
			for (int i = 0; i < nbVariable; i++){
				double min = Math.min(
						this.concerned[i].getAgentInitialState().getMyReliability(),
						this.concerned[i].getAgentResultingState().getMyReliability());
				double max =  Math.max(
						this.concerned[i].getAgentInitialState().getMyReliability(),
						this.concerned[i].getAgentResultingState().getMyReliability());

				IntegerConstantVariable minUt, maxUt;
				if (socialWelfare.equals(SocialChoiceFunction.key4NashSocialWelfare)){
					minUt = new IntegerConstantVariable((int) (100* Math.log(min)));	
					maxUt = new IntegerConstantVariable((int) (100* Math.log(max)));	
				} else {
					minUt = new IntegerConstantVariable((int) (100* (min)));
					maxUt = new IntegerConstantVariable((int) (100* (max)));
				}

				replicas[i] = Choco.makeIntVar(this.concerned[i].getAgent().toString(), 0, 1, Options.V_ENUM);
				replicasValue[i] = Choco.makeIntVar(this.concerned[i].getAgent().toString()+"__value", 1, 1000000, Options.V_BOUND, Options.V_NO_DECISION);
				//				m.addConstraint(Choco.eq(replicasValue[i],Choco.ifThenElse(Choco.eq(replicas[i],0), minUt, maxUt)));

				replicasGain[i] = maxUt.getValue() - minUt.getValue();

				assert this.concerned[i].getAgentInitialState().getMyMemCharge().equals(
						this.concerned[i].getAgentResultingState().getMyMemCharge());
				assert this.concerned[i].getAgentInitialState().getMyProcCharge().equals(
						this.concerned[i].getAgentResultingState().getMyProcCharge());				

				replicasMem[i] = (int) (100 * this.concerned[i].getAgentInitialState().getMyMemCharge());
				replicasProc[i] = (int) (100 * this.concerned[i].getAgentInitialState().getMyProcCharge());
			}


			if (multiDim){
				//Contrainte de poids
				m.addConstraint(Choco.leq(Choco.scalar(replicasProc, replicas), hostProccapacity));
				m.addConstraint(Choco.leq(Choco.scalar(replicasMem, replicas), hostMemCapacity));

				//Optimisation social
				if (socialWelfare.equals(SocialChoiceFunction.key4leximinSocialWelfare)) {
					m.addConstraint(Choco.eq(socialWelfareValue, Choco.min(replicasValue)));
				} else {
					assert (socialWelfare.equals(SocialChoiceFunction.key4NashSocialWelfare) 
							|| socialWelfare.equals(SocialChoiceFunction.key4UtilitaristSocialWelfare));
					m.addConstraint(Choco.eq (Choco.sum(replicasValue), socialWelfareValue));
				}
			} else {
				if (socialWelfare.equals(SocialChoiceFunction.key4NashSocialWelfare) || socialWelfare.equals(SocialChoiceFunction.key4UtilitaristSocialWelfare)){
					IntegerVariable weightVar = Choco.makeIntVar("weight", 1, 1000000, Options.V_BOUND, Options.V_NO_DECISION);
					//Optimisation social & Contrainte de poids
					m.addConstraint(Choco.knapsackProblem(socialWelfareValue, weightVar, replicas, replicasGain, replicasProc));
					m.addConstraint(Choco.leq(weightVar, hostProccapacity));
				} else if (socialWelfare.equals(SocialChoiceFunction.key4leximinSocialWelfare)) {
					//Contrainte de poids
					m.addConstraint(Choco.leq(Choco.scalar(replicasProc, replicas), hostProccapacity));
					//Optimisation social
					m.addConstraint(Choco.eq(socialWelfareValue, Choco.min(replicasValue)));
				}
			}


			//Contrainte d'amélioration stricte
			if (socialWelfare.equals(SocialChoiceFunction.key4leximinSocialWelfare)) {

				int[] currentAllocation = new int[concerned.size()];

				for (int i = 0; i < concerned.size(); i++){
					currentAllocation[i] = ((int) (100 * this.concerned[i].getAgentInitialState().getMyReliability()));
				}

				m.addConstraint(Choco.leximin(currentAllocation, replicasValue));
			} else {
				//Contrainte d'amélioration stricte
				List<Double> currentAllocation = new ArrayList<Double>();
				for (ReplicationCandidature c : concerned){
					currentAllocation.add(c.getAgentInitialState().getMyReliability());
				}			

				int current = 0;
				if (socialWelfare.equals(SocialChoiceFunction.key4NashSocialWelfare)) {
					for (double v : currentAllocation){
						current+=(int) (100* Math.log(v));
					}
				} else if (socialWelfare.equals(SocialChoiceFunction.key4UtilitaristSocialWelfare)) {	
					for (double v : currentAllocation){
						current+=(int) (100 * v);
					}
				}else {
					throw new RuntimeException("impossible key for social welfare is : "+socialWelfare);
				}
				m.addConstraint(Choco.gt(socialWelfareValue, current));
			}

			s = new CPSolver();
			s.read(m);	
			s.setValIntIterator(new DecreasingDomain());


			//			System.out.println("yooo "+Arrays.asList(replicasValue));
			//			for (int i = 0; i < nbVariable; i++){
			//				System.out.println("yooo2 "+replicasProc[i]);
			//			}
			//			System.out.println("yoo3 "+hostProccapacity);			
			//			System.out.println("yoo "+s.getVar(poids).getVal());

		} catch (IncompleteContractException e) {
			signalException("noooooooooooooooonnnnnnnnnn",e);
		}
	}

	public static void main(String[] args){
		ReplicationAllocationSolver solver = new ReplicationAllocationSolver(SocialChoiceFunction.key4UtilitaristSocialWelfare);
		HostState h = new HostState(new ResourceIdentifier("host",77), 100, 100, 0.5, 1);
		ReplicaState r1 = new ReplicaState(new AgentName("r1"), 0.9, 30., 50., new HashSet<HostState>(), 1);
		ReplicaState r2 = new ReplicaState(new AgentName("r2"), 0.6, 40., 50., new HashSet<HostState>(), 1);
		ReplicaState r3 = new ReplicaState(new AgentName("r3"), 0.3, 15., 20., new HashSet<HostState>(), 1);
		ReplicaState r4 = new ReplicaState(new AgentName("r4"), 0.4, 30., 20., new HashSet<HostState>(), 1);

		ReplicationCandidature c1 = 
				new ReplicationCandidature(h.getMyAgentIdentifier(), r1.getMyAgentIdentifier(), true, true);
		c1.setSpecification(h);
		c1.setSpecification(r1);

		ReplicationCandidature c2 = 
				new ReplicationCandidature(h.getMyAgentIdentifier(), r2.getMyAgentIdentifier(), true, true);
		c2.setSpecification(h);
		c2.setSpecification(r2);

		ReplicationCandidature c3 = 
				new ReplicationCandidature(h.getMyAgentIdentifier(), r3.getMyAgentIdentifier(), true, true);
		c3.setSpecification(h);
		c3.setSpecification(r3);

		ReplicationCandidature c4 = 
				new ReplicationCandidature(h.getMyAgentIdentifier(), r4.getMyAgentIdentifier(), true, true);
		c4.setSpecification(h);
		c4.setSpecification(r4);

		List<ReplicationCandidature> concerned = new ArrayList<ReplicationCandidature>();
		concerned.add(c1);
		concerned.add(c2);
		concerned.add(c3);
		concerned.add(c4);

		solver.initiate(concerned, h);
		System.out.println(solver.getAllSolution());
		System.out.println(Arrays.asList(solver.replicas));
		for (IntegerVariable i : Arrays.asList(solver.replicas)){
			System.out.println(solver.s.getVar(i).getVal());
		}
	}

	//	public static void main(String[] args){
	//		Model m = new CPModel();
	//		IntegerVariable obj1 = Choco.makeIntVar("obj1", 0, 5, Options.V_ENUM);
	//		IntegerVariable obj2 = Choco.makeIntVar("obj2", 0, 7, Options.V_ENUM);
	//		IntegerVariable obj3 = Choco.makeIntVar("obj3", 0, 10, Options.V_ENUM);
	//		IntegerVariable c = Choco.makeIntVar("cost", 1, 1000000, Options.V_BOUND);
	//		int capacity = 34;
	//		int[] volumes = new int[]{7, 5, 3};
	//		int[] volumes2 = new int[]{3, 5, 7};
	//		int[] energy = new int[]{6, 4, 2};
	//		m.addConstraint(Choco.leq(Choco.scalar(volumes, new IntegerVariable[]{obj1, obj2, obj3})
	//				, capacity));
	//		m.addConstraint(Choco.leq(Choco.scalar(volumes2, new IntegerVariable[]{obj1, obj2, obj3})
	//				, capacity));
	//		m.addConstraint(Choco.eq(Choco.scalar(energy, new IntegerVariable[]{obj1, obj2, obj3}),
	//				c));
	//		Solver s = new CPSolver();
	//		s.read(m);
	//		s.setValIntIterator(new DecreasingDomain());
	//		s.maximize(s.getVar(c), false);
	//			System.out.println(s.getVar(obj1).getVal());
	//			System.out.println(s.getVar(obj2).getVal());
	//			System.out.println(s.getVar(obj3).getVal());
	//	}
}

//			IntegerExpressionVariable poidsP = Choco.scalar(replicasProc, replicas);
//			IntegerVariable poids = Choco.makeIntVar("poids", 1, 1000000, Options.V_BOUND, Options.V_NO_DECISION);
//			m.addConstraint(Choco.eq(poidsP, poids));
//			m.addConstraint(Choco.geq(hostProccapacity, poids));