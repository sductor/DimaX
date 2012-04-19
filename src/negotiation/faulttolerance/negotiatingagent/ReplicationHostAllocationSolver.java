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
import choco.cp.solver.search.integer.valiterator.IncreasingDomain;
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
import negotiation.negotiationframework.rationality.SocialChoiceFunction.SocialChoiceType;

public class ReplicationHostAllocationSolver
extends ChocoAllocationSolver
<ReplicationCandidature, ReplicationSpecification, HostState>{
	private static final long serialVersionUID = 161669049253111527L;

	final boolean multiDim = true;

	/*
	 * Constants
	 */

	int[] replicasProc;
	int[] replicasMem;
	int hostProccapacity;
	int hostMemCapacity;	
	int[] replicasGain = null;	


	/*
	 * Variables
	 */

	IntegerVariable[] replicasValue;

	public ReplicationHostAllocationSolver(SocialChoiceType socialWelfare) {
		super(socialWelfare);
	}

	public void initiate(
			Collection<ReplicationCandidature> concerned, 
			HostState currentState){
		try {
			Model m = new CPModel();
			s = new CPSolver();

			this.concerned=concerned.toArray(new ReplicationCandidature[concerned.size()]);
			int nbVariable = concerned.size();

			instanciateConstant(nbVariable, currentState);
			instanciateVariable(m, nbVariable);
			if (multiDim)
				instanciateConstraints(m);	
			else
				instanciateConstraintKnapsack(m);

			s.read(m);	
			s.setValIntIterator(new DecreasingDomain());

		} catch (IncompleteContractException e) {
			signalException("noooooooooooooooonnnnnnnnnn",e);
		}
	}

	private void instanciateConstant(
			int nbVariable, 
			HostState currentState) throws IncompleteContractException{

		hostProccapacity =asInt(currentState.getProcChargeMax(),false);
		hostMemCapacity = asInt(currentState.getMemChargeMax(),false);
		replicasProc = new int[nbVariable];
		replicasMem = new int[nbVariable];

		for (int i = 0; i < nbVariable; i++){		
			assert this.concerned[i].getAgentInitialState().getMyMemCharge().equals(
					this.concerned[i].getAgentResultingState().getMyMemCharge());
			assert this.concerned[i].getAgentInitialState().getMyProcCharge().equals(
					this.concerned[i].getAgentResultingState().getMyProcCharge());				

			replicasMem[i] = asInt(this.concerned[i].getAgentInitialState().getMyMemCharge(),false);
			replicasProc[i] = asInt(this.concerned[i].getAgentInitialState().getMyProcCharge(),false);
		}


	}

	private void instanciateVariable(Model m, int nbVariable) throws IncompleteContractException{

		replicas = new IntegerVariable[nbVariable];
		socialWelfareValue =  Choco.makeIntVar("utility", 1, 1000000, Options.V_BOUND, Options.V_NO_DECISION);

		if (multiDim) {
			replicasValue = new IntegerVariable[nbVariable];
		} else {
			replicasGain = new int[nbVariable];
		}

		//initialisation des variables replicas
		for (int i = 0; i < nbVariable; i++){	
			replicas[i] = Choco.makeIntVar(this.concerned[i].getAgent().toString(), 0, 1, Options.V_ENUM);

			IntegerConstantVariable minUt, maxUt;
			minUt = new IntegerConstantVariable(asIntNashed(Math.min(
					this.concerned[i].getAgentInitialState().getMyReliability(),
					this.concerned[i].getAgentResultingState().getMyReliability())));
			maxUt = new IntegerConstantVariable(asIntNashed(Math.max(
					this.concerned[i].getAgentInitialState().getMyReliability(),
					this.concerned[i].getAgentResultingState().getMyReliability())));
			if (multiDim){
				replicasValue[i] = Choco.makeIntVar(
						this.concerned[i].getAgent().toString()+"__value", minUt.getValue(), maxUt.getValue(), 
						Options.V_BOUND, Options.V_NO_DECISION);
				m.addConstraint(Choco.eq(replicasValue[i],Choco.ifThenElse(Choco.eq(replicas[i],0), minUt, maxUt)));
			} else {
				replicasGain[i] = maxUt.getValue() - minUt.getValue();
			}
		}
	}

	private void instanciateConstraints(Model m) throws IncompleteContractException{

		//Contrainte de poids
		m.addConstraint(Choco.leq(Choco.scalar(replicasProc, replicas), hostProccapacity));
		m.addConstraint(Choco.leq(Choco.scalar(replicasMem, replicas), hostMemCapacity));

		//Optimisation social
		if (socialWelfare.equals(SocialChoiceType.Leximin)) {
			m.addConstraint(Choco.eq(socialWelfareValue, Choco.min(replicasValue)));
		} else {
			assert (socialWelfare.equals(SocialChoiceType.Nash) 
					|| socialWelfare.equals(SocialChoiceType.Utility));
			m.addConstraint(Choco.eq (Choco.sum(replicasValue), socialWelfareValue));
		}

		//Contrainte d'amélioration stricte
		if (socialWelfare.equals(SocialChoiceType.Leximin)) {
			int[] currentAllocation = new int[concerned.length];
			for (int i = 0; i < concerned.length; i++){
				currentAllocation[i] = asInt(this.concerned[i].getAgentInitialState().getMyReliability(),false);
			}
			m.addConstraint(Choco.leximin(currentAllocation, replicasValue));
		} else {
			int current = 0;
			for (ReplicationCandidature c : concerned){
				current+=asIntNashed(c.getAgentInitialState().getMyReliability());
			}
			m.addConstraint(Choco.gt(socialWelfareValue, current));
		}
	}

	private void instanciateConstraintKnapsack(Model m){
		if (socialWelfare.equals(SocialChoiceType.Nash) 
				|| socialWelfare.equals(SocialChoiceType.Utility)){
			IntegerVariable weightVar = 
					Choco.makeIntVar("weight", 0, 1000000, Options.V_BOUND, Options.V_NO_DECISION);
			//Optimisation social & Contrainte de poids
			m.addConstraint(Choco.knapsackProblem(socialWelfareValue, weightVar, 
					replicas, replicasGain, replicasProc));
			m.addConstraint(Choco.leq(weightVar, hostProccapacity));
		} else if (socialWelfare.equals(SocialChoiceType.Leximin)) {
			//Contrainte de poids
			m.addConstraint(Choco.leq(Choco.scalar(replicasProc, replicas), hostProccapacity));
			//Optimisation social
			m.addConstraint(Choco.eq(socialWelfareValue, Choco.min(replicasValue)));
		}
	}

	public int asIntNashed(double d){
		if (s.equals(SocialChoiceType.Nash))
			return (int) (100*Math.log(d));
		else
			return (int) (100 * d);
	}
	public int asInt(double d, boolean log){
		if (log)
			return (int) (100*Math.log(d));
		else
			return (int) (100 * d);
	}

	/*
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 */

	public static void main(String[] args){

		SocialChoiceType sw = SocialChoiceType.Leximin;

		HostState me = new HostState(new ResourceIdentifier("me",77), 100, 100, 0.5, 1);


		HostState h1 = new HostState(new ResourceIdentifier("host1",77), 100, 100, 0.5, 1);
		HostState h2 = new HostState(new ResourceIdentifier("host2",77), 100, 100, 0.5, 1);
		HostState h3 = new HostState(new ResourceIdentifier("host3",77), 100, 100, 0.5, 1);
		HostState h4 = new HostState(new ResourceIdentifier("host4",77), 100, 100, 0.5, 1);

		ReplicaState r1 = new ReplicaState(
				new ReplicaState(new AgentName("r1"), 1., 30., 50., new HashSet<HostState>(), sw, 1),
				h1);
		ReplicaState r2 = new ReplicaState(
				new ReplicaState(new AgentName("r2"), 0.5, 40., 50., new HashSet<HostState>(), sw, 1),
				h2);
		ReplicaState r3 = new ReplicaState(
				new ReplicaState(new AgentName("r3"), 0.3, 15., 20., new HashSet<HostState>(), sw, 1),
				h3);
		ReplicaState r4 = new ReplicaState(
				new ReplicaState(new AgentName("r4"), 0.4, 30., 20., new HashSet<HostState>(), sw, 1),
				h4);

		ReplicationCandidature c1 = 
				new ReplicationCandidature(me.getMyAgentIdentifier(), r1.getMyAgentIdentifier(), true, true);
		c1.setSpecification(me);
		c1.setSpecification(r1);

		ReplicationCandidature c2 = 
				new ReplicationCandidature(me.getMyAgentIdentifier(), r2.getMyAgentIdentifier(), true, true);
		c2.setSpecification(me);
		c2.setSpecification(r2);

		ReplicationCandidature c3 = 
				new ReplicationCandidature(me.getMyAgentIdentifier(), r3.getMyAgentIdentifier(), true, true);
		c3.setSpecification(me);
		c3.setSpecification(r3);

		ReplicationCandidature c4 = 
				new ReplicationCandidature(me.getMyAgentIdentifier(), r4.getMyAgentIdentifier(), true, true);
		c4.setSpecification(me);
		c4.setSpecification(r4);

		List<ReplicationCandidature> concerned = new ArrayList<ReplicationCandidature>();
		concerned.add(c1);
		concerned.add(c2);
		concerned.add(c3);
		concerned.add(c4);

		ReplicationHostAllocationSolver solver = 
				new ReplicationHostAllocationSolver(sw);
		solver.initiate(concerned, me);

		boolean best = false;

		//		System.out.println("Best : &&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&");
		//		solver.getBestSolution();//System.out.println(solver.getBestSolution());
		//		for (int i = 0; i < solver.replicas.length; i++){
		//			System.out.println(solver.s.getVar(solver.replicas[i]).getName()
		//					+" "+solver.s.getVar(solver.replicas[i]).getVal()
		//					+" value est "+solver.s.getVar(solver.replicasValue[i]).getVal());
		//		}
		//		System.out.println(solver.s.getVar(solver.socialWelfareValue).getVal());
		//		System.out.println("Best : &&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&");

		System.out.println("isfeasible : "+solver.s.isFeasible());
		while (solver.hasNext()){
			System.out.println("isfeasible : "+solver.s.isFeasible());
			for (int i = 0; i < solver.replicas.length; i++){
				System.out.println(solver.s.getVar(solver.replicas[i]).getName()
						+" "+solver.s.getVar(solver.replicas[i]).getVal()
						+" value est "+solver.s.getVar(solver.replicasValue[i]).getVal());
			}
			solver.getNextSolution();
			System.out.println(solver.s.getVar(solver.socialWelfareValue).getVal());
			System.out.println("*****************************");
		}
		System.out.println("Nb_sol : " + solver.s.getNbSolutions());
		System.out.println("%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%");
		solver.s.solve();
		if (solver.s.isFeasible()) {
			do {
				for (int i = 0; i < solver.replicas.length; i++){
					System.out.println(solver.s.getVar(solver.replicas[i]).getName()
							+" "+solver.s.getVar(solver.replicas[i]).getVal()
							+" value est "+solver.s.getVar(solver.replicasValue[i]).getVal());
				}
				System.out.println("");
			} while (solver.s.nextSolution());
		}
		System.out.println("Nb_sol : " + solver.s.getNbSolutions());


		System.out.println("Best : &&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&");
		solver.getBestSolution();//System.out.println(solver.getBestSolution());
		for (int i = 0; i < solver.replicas.length; i++){
			System.out.println(solver.s.getVar(solver.replicas[i]).getName()
					+" "+solver.s.getVar(solver.replicas[i]).getVal()
					+" value est "+solver.s.getVar(solver.replicasValue[i]).getVal());
		}
		System.out.println(solver.s.getVar(solver.socialWelfareValue).getVal());
		System.out.println("Best : &&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&");
	}
}


//			System.out.println("yooo "+Arrays.asList(replicasValue));
//			for (int i = 0; i < nbVariable; i++){
//				System.out.println("yooo2 "+replicasProc[i]);
//			}
//			System.out.println("yoo3 "+hostProccapacity);			
//			System.out.println("yoo "+s.getVar(poids).getVal());
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

//			IntegerExpressionVariable poidsP = Choco.scalar(replicasProc, replicas);
//			IntegerVariable poids = Choco.makeIntVar("poids", 1, 1000000, Options.V_BOUND, Options.V_NO_DECISION);
//			m.addConstraint(Choco.eq(poidsP, poids));
//			m.addConstraint(Choco.geq(hostProccapacity, poids));