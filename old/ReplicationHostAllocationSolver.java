package frameworks.faulttolerance.solver;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;


import choco.Choco;
import choco.Options;
import choco.cp.model.CPModel;
import choco.cp.solver.CPSolver;
import choco.cp.solver.search.integer.valiterator.DecreasingDomain;
import choco.kernel.model.Model;
import choco.kernel.model.variables.integer.IntegerConstantVariable;
import choco.kernel.model.variables.integer.IntegerVariable;
import dima.basicagentcomponents.AgentName;
import frameworks.faulttolerance.experimentation.ReplicationExperimentationParameters;
import frameworks.faulttolerance.negotiatingagent.HostState;
import frameworks.faulttolerance.negotiatingagent.ReplicaState;
import frameworks.faulttolerance.negotiatingagent.ReplicationCandidature;
import frameworks.negotiation.contracts.ResourceIdentifier;
import frameworks.negotiation.contracts.AbstractContractTransition.IncompleteContractException;
import frameworks.negotiation.exploration.ChocoAllocationSolver;
import frameworks.negotiation.rationality.SocialChoiceFunction.SocialChoiceType;

public class ReplicationHostAllocationSolver
extends ChocoAllocationSolver
<ReplicationCandidature, HostState>{
	private static final long serialVersionUID = 161669049253111527L;


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

	public ReplicationHostAllocationSolver(final SocialChoiceType socialWelfare) {
		super(socialWelfare);
	}

	@Override
	public void initiate(final Collection<ReplicationCandidature> concerned){
		try {
			final Model m = new CPModel();
			if (this.s!=null) {
				this.s.clear();
			}
			this.s = new CPSolver();

			this.concerned=concerned.toArray(new ReplicationCandidature[concerned.size()]);
			final int nbVariable = concerned.size();

			this.instanciateConstant(nbVariable);
			this.instanciateVariable(m, nbVariable);
			if (ReplicationExperimentationParameters.multiDim){
				this.instanciateConstraints(m);
			} else {
				this.instanciateConstraintKnapsack(m);
			}

			this.s.read(m);
			this.s.setValIntIterator(new DecreasingDomain());

		} catch (final IncompleteContractException e) {
			this.signalException("noooooooooooooooonnnnnnnnnn",e);
		}
	}

	private void instanciateConstant(final int nbVariable) throws IncompleteContractException{

		assert nbVariable>0;

		this.hostProccapacity =asInt(this.concerned[0].getResourceInitialState().getProcChargeMax(),false);
		this.hostMemCapacity = asInt(this.concerned[0].getResourceInitialState().getMemChargeMax(),false);
		this.replicasProc = new int[nbVariable];
		this.replicasMem = new int[nbVariable];

		for (int i = 0; i < nbVariable; i++){
			assert this.concerned[i].getResourceInitialState().equals(this.concerned[0].getResourceInitialState());
			assert this.concerned[i].getAgentInitialState().getMyMemCharge().equals(
					this.concerned[i].getAgentResultingState().getMyMemCharge());
			assert this.concerned[i].getAgentInitialState().getMyProcCharge().equals(
					this.concerned[i].getAgentResultingState().getMyProcCharge());

			this.replicasMem[i] = asInt(this.concerned[i].getAgentInitialState().getMyMemCharge(),false);
			this.replicasProc[i] = asInt(this.concerned[i].getAgentInitialState().getMyProcCharge(),false);
		}
	}

	private void instanciateVariable(final Model m, final int nbVariable) throws IncompleteContractException{

		this.candidatureAllocation = new IntegerVariable[nbVariable];
		this.socialWelfareValue =  Choco.makeIntVar("utility", 1, 1000000, Options.V_BOUND, Options.V_NO_DECISION);

		if (ReplicationExperimentationParameters.multiDim){
			this.replicasValue = new IntegerVariable[nbVariable];
		} else {
			this.replicasGain = new int[nbVariable];
		}

		//initialisation des variables replicas
		for (int i = 0; i < nbVariable; i++){
			this.candidatureAllocation[i] = Choco.makeIntVar(this.concerned[i].getAgent().toString(), 0, 1, Options.V_ENUM);

			IntegerConstantVariable minUt, maxUt;
			minUt = new IntegerConstantVariable(asIntNashed(Math.min(
					this.concerned[i].getAgentInitialState().getMyReliability(),
					this.concerned[i].getAgentResultingState().getMyReliability()),this.socialWelfare));
			maxUt = new IntegerConstantVariable(asIntNashed(Math.max(
					this.concerned[i].getAgentInitialState().getMyReliability(),
					this.concerned[i].getAgentResultingState().getMyReliability()),this.socialWelfare));
			if (ReplicationExperimentationParameters.multiDim){
				this.replicasValue[i] = Choco.makeIntVar(
						this.concerned[i].getAgent().toString()+"__value", minUt.getValue(), maxUt.getValue(),
						Options.V_BOUND, Options.V_NO_DECISION);
				m.addConstraint(Choco.eq(
						this.replicasValue[i],
						Choco.ifThenElse(
								Choco.eq(this.candidatureAllocation[i],0),
								minUt, maxUt)));
			} else {
				this.replicasGain[i] = maxUt.getValue() - minUt.getValue();
			}
		}
	}

	private void instanciateConstraints(final Model m) throws IncompleteContractException{

		//Contrainte de poids
		m.addConstraint(Choco.leq(Choco.scalar(this.replicasProc, this.candidatureAllocation), this.hostProccapacity));
		m.addConstraint(Choco.leq(Choco.scalar(this.replicasMem, this.candidatureAllocation), this.hostMemCapacity));

		//Optimisation social
		if (this.socialWelfare.equals(SocialChoiceType.Leximin)) {
			m.addConstraint(Choco.eq(this.socialWelfareValue, Choco.min(this.replicasValue)));
		} else {
			assert this.socialWelfare.equals(SocialChoiceType.Nash)
			|| this.socialWelfare.equals(SocialChoiceType.Utility);
			m.addConstraint(Choco.eq (Choco.sum(this.replicasValue), this.socialWelfareValue));
		}

		//Contrainte d'amélioration stricte
		if (this.socialWelfare.equals(SocialChoiceType.Leximin)) {
			final int[] currentAllocation = new int[this.concerned.length];
			for (int i = 0; i < this.concerned.length; i++){
				currentAllocation[i] = asInt(this.concerned[i].getAgentInitialState().getMyReliability(),false);
			}
			m.addConstraint(Choco.leximin(currentAllocation, this.replicasValue));
		} else {
			int current = 0;
			for (final ReplicationCandidature c : this.concerned){
				current+=asIntNashed(c.getAgentInitialState().getMyReliability(),this.socialWelfare);
			}
			try {
				m.addConstraint(Choco.gt(this.socialWelfareValue, current));
			} catch (final ArrayIndexOutOfBoundsException e) {
				this.signalException(this.socialWelfareValue+" "+current,e);
			}
		}
	}

	private void instanciateConstraintKnapsack(final Model m){
		if (this.socialWelfare.equals(SocialChoiceType.Nash)
				|| this.socialWelfare.equals(SocialChoiceType.Utility)){
			final IntegerVariable weightVar =
					Choco.makeIntVar("weight", 0, 1000000, Options.V_BOUND, Options.V_NO_DECISION);
			//Optimisation social & Contrainte de poids
			m.addConstraint(Choco.knapsackProblem(this.socialWelfareValue, weightVar,
					this.candidatureAllocation, this.replicasGain, this.replicasProc));
			m.addConstraint(Choco.leq(weightVar, this.hostProccapacity));
		} else if (this.socialWelfare.equals(SocialChoiceType.Leximin)) {
			//Contrainte de poids
			m.addConstraint(Choco.leq(Choco.scalar(this.replicasProc, this.candidatureAllocation), this.hostProccapacity));
			//Optimisation social
			m.addConstraint(Choco.eq(this.socialWelfareValue, Choco.min(this.replicasValue)));
		}
	}


	public static int asIntNashed(final double d, final SocialChoiceType _socialChoice){
		if (_socialChoice.equals(SocialChoiceType.Nash)) {
			return asInt(d, true);
		} else {
			assert _socialChoice.equals(SocialChoiceType.Leximin)
			|| _socialChoice.equals(SocialChoiceType.Utility):_socialChoice;
			return asInt(d, false);
		}
	}
	public static int asInt(final double d, final boolean log){
		if (log) {
			return (int) Math.log(100*(d+0.01));
		} else {
			return (int) (100 * (d+0.01));
		}
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

	public static void main(final String[] args) throws IncompleteContractException{

		final SocialChoiceType sw = SocialChoiceType.Utility;

		//me
		HostState me = new HostState(new ResourceIdentifier("me",77), 100., 100., 0.5, 1);

		//already allocated

		ReplicaState ra = new ReplicaState(new AgentName("ra"), 0.2, 30., 80., sw, 1);
		ReplicaState rb = new ReplicaState(new AgentName("rb"), 0.2, 30., 20., sw, 1);

		final ReplicationCandidature ca =
				new ReplicationCandidature(me.getMyAgentIdentifier(), ra.getMyAgentIdentifier(), true, true);
		ca.setInitialState(me);
		ca.setInitialState(ra);

		ra = ca.getAgentResultingState();
		me = ca.getResourceResultingState();

		final ReplicationCandidature cb =
				new ReplicationCandidature(me.getMyAgentIdentifier(), rb.getMyAgentIdentifier(), true, true);
		cb.setInitialState(me);
		cb.setInitialState(rb);

		rb = cb.getAgentResultingState();
		me = cb.getResourceResultingState();


		// other hosts
		ReplicaState r1 = new ReplicaState(new AgentName("r1"), 1., 30., 50., sw, 1);
		ReplicaState r2 = new ReplicaState(new AgentName("r2"), 0.5, 40., 50.,  sw, 1);
		ReplicaState r3 = new ReplicaState(new AgentName("r3"), 0.3, 15., 20., sw, 1);
		ReplicaState r4 = new ReplicaState(new AgentName("r4"), 0.4, 30., 20., sw, 1);

		final HostState h1 = new HostState(new ResourceIdentifier("host1",77), 100, 100, 0.5, 1).allocate(r1);
		final HostState h2 = new HostState(new ResourceIdentifier("host2",77), 100, 100, 0.5, 1).allocate(r2);
		final HostState h3 = new HostState(new ResourceIdentifier("host3",77), 100, 100, 0.5, 1).allocate(r3);
		final HostState h4 = new HostState(new ResourceIdentifier("host4",77), 100, 100, 0.5, 1).allocate(r4);

		r1 = r1.allocate(h1);
		r2 = r2.allocate(h2);
		r3 = r3.allocate(h3);
		r4 = r4.allocate(h4);

		final ReplicationCandidature c1 =
				new ReplicationCandidature(me.getMyAgentIdentifier(), r1.getMyAgentIdentifier(), true, true);
		c1.setInitialState(me);
		c1.setInitialState(r1);

		final ReplicationCandidature c2 =
				new ReplicationCandidature(me.getMyAgentIdentifier(), r2.getMyAgentIdentifier(), true, true);
		c2.setInitialState(me);
		c2.setInitialState(r2);

		final ReplicationCandidature c3 =
				new ReplicationCandidature(me.getMyAgentIdentifier(), r3.getMyAgentIdentifier(), true, true);
		c3.setInitialState(me);
		c3.setInitialState(r3);

		final ReplicationCandidature c4 =
				new ReplicationCandidature(me.getMyAgentIdentifier(), r4.getMyAgentIdentifier(), true, true);
		c4.setInitialState(me);
		c4.setInitialState(r4);

		final ReplicationCandidature cad =
				new ReplicationCandidature(me.getMyAgentIdentifier(), ra.getMyAgentIdentifier(), false, true);
		cad.setInitialState(me);
		cad.setInitialState(ra);

		final ReplicationCandidature cbd =
				new ReplicationCandidature(me.getMyAgentIdentifier(), rb.getMyAgentIdentifier(), false, true);
		cbd.setInitialState(me);
		cbd.setInitialState(rb);

		final List<ReplicationCandidature> concerned = new ArrayList<ReplicationCandidature>();
		concerned.add(c1);
		concerned.add(c2);
		concerned.add(c3);
		concerned.add(c4);
		concerned.add(cad);
		concerned.add(cbd);

		final ReplicationHostAllocationSolver solver =
				new ReplicationHostAllocationSolver(sw);
		solver.initiate(concerned);

		final boolean best = false;

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
			for (int i = 0; i < solver.candidatureAllocation.length; i++){
				System.out.println(solver.s.getVar(solver.candidatureAllocation[i]).getName()
						+" "+solver.s.getVar(solver.candidatureAllocation[i]).getVal()
						+" value est "+solver.s.getVar(solver.replicasValue[i]).getVal());
			}
			final Collection<ReplicationCandidature> sol = solver.getNextSolution();
			System.out.println(sol);
			System.out.println(solver.s.getVar(solver.socialWelfareValue).getVal());
			for (final ReplicationCandidature c : sol){
				me = c.getResourceResultingState();
			}
			if (!me.isValid()) {
				System.err.println("aaaaaaahhhh\n"+me);
			}
			System.out.println("*****************************");
		}
		System.out.println("Nb_sol : " + solver.s.getNbSolutions());
		//		System.out.println("%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%");
		//		solver.s.solve();
		//		if (solver.s.isFeasible()) {
		//			do {
		//				for (int i = 0; i < solver.replicas.length; i++){
		//					System.out.println(solver.s.getVar(solver.replicas[i]).getName()
		//							+" "+solver.s.getVar(solver.replicas[i]).getVal()
		//							+" value est "+solver.s.getVar(solver.replicasValue[i]).getVal());
		//				}
		//				System.out.println("");
		//			} while (solver.s.nextSolution());
		//		}
		//		System.out.println("Nb_sol : " + solver.s.getNbSolutions());


		System.out.println("Best : &&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&");
		System.out.println(solver.getBestSolution());//System.out.println(solver.getBestSolution());
		for (int i = 0; i < solver.candidatureAllocation.length; i++){
			System.out.println(solver.s.getVar(solver.candidatureAllocation[i]).getName()
					+" "+solver.s.getVar(solver.candidatureAllocation[i]).getVal()
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