package frameworks.faulttolerance.solver;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
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
import dima.basicagentcomponents.AgentIdentifier;
import dima.introspectionbasedagents.modules.mappedcollections.HashedHashSet;
import frameworks.faulttolerance.dcop.DCOPFactory;
import frameworks.faulttolerance.dcop.DcopSolver;
import frameworks.faulttolerance.dcop.dcop.DcopReplicationGraph;
import frameworks.faulttolerance.dcop.dcop.MemFreeConstraint;
import frameworks.faulttolerance.dcop.dcop.ReplicationVariable;
import frameworks.faulttolerance.experimentation.ReplicationExperimentationParameters;
import frameworks.faulttolerance.negotiatingagent.HostState;
import frameworks.faulttolerance.negotiatingagent.ReplicaState;
import frameworks.faulttolerance.negotiatingagent.ReplicationCandidature;
import frameworks.negotiation.contracts.AbstractContractTransition.IncompleteContractException;
import frameworks.negotiation.contracts.ResourceIdentifier;
import frameworks.negotiation.exploration.ChocoAllocationSolver;
import frameworks.negotiation.rationality.SocialChoiceFunction.SocialChoiceType;

public class ChocoReplicationAllocationSolver 
extends ChocoAllocationSolver
<ReplicationCandidature, HostState>
implements DcopSolver{




	public ChocoReplicationAllocationSolver(SocialChoiceType socialWelfare) {
		super(socialWelfare);
	}

	/***********************/
	//
	// Global Optimisation solver
	//
	/***********************/

	/*
	 * Constants
	 */

	/*
	 * Variable
	 */

	IntegerVariable[][] hostsMatrix;
	IntegerVariable[][] agentsMatrix;

	//

	HashMap<MemFreeConstraint,IntegerVariable> varValue;

	@Override
	public HashMap<Integer, Integer> solve(DcopReplicationGraph drg) {

		try{

			//instanciating

			final Model m = new CPModel();
			if (this.s!=null) {
				this.s.clear();
			}
			this.s = new CPSolver();

			instanciateGlobalSolver(drg,m);

			this.s.read(m);
			this.s.setValIntIterator(new DecreasingDomain());

			//solving

			this.s.setObjective(this.s.getVar(this.socialWelfareValue));
			Boolean feasible = this.s.maximize(true);

			//returning result;

			HashMap<Integer, Integer> result = new HashMap<Integer, Integer>(); 
			if (feasible!=null && feasible){
				HashedHashSet<ReplicationVariable, AgentIdentifier> solution= new HashedHashSet<ReplicationVariable, AgentIdentifier>();
				for (MemFreeConstraint c : drg.conList){
					if (this.s.getVar(varValue.get(c)).getVal()==1){
						solution.add(c.getAgent(), c.getHost().getAgentIdentifier());
						solution.add(c.getHost(), c.getAgent().getAgentIdentifier());
					}
				}
				for (ReplicationVariable v : drg.varMap.values()){
					if (feasible!=null && feasible){
						result.put(v.id, DCOPFactory.subsetToInt(v.getNeighborsIdentifiers(), solution.get(v)));
					}
				}
			}else
				for (ReplicationVariable v : drg.varMap.values()){
					result.put(v.id,-1);
				}
			return  result;
		} catch (IncompleteContractException e) {
			throw new RuntimeException();
		}
	}

	private void instanciateGlobalSolver(DcopReplicationGraph drg, Model m)  throws IncompleteContractException{

		/*
		 * Instanciating constants
		 */

		int nbAgents;
		int nbHosts;

		List<ReplicaState> ags = new ArrayList<ReplicaState>(drg.getAgentStates());
		List<HostState> hs =  new ArrayList<HostState>(drg.getHostsStates());

		nbAgents =ags.size();
		nbHosts = hs.size();

		
		boolean[][] accesibilityGraph;

		IntegerConstantVariable[] agentCriticity;
		int[] repProcCharge;
		int[] repMemCharge;

		int[] hostLambda;
		int[] hostProcCap;
		int[] hostMemCap;

		accesibilityGraph = new boolean[nbAgents][nbHosts];
		for (int i = 0; i < nbAgents; i++){
			for (int j = 0; j < nbHosts; j++){
				final AgentIdentifier agId = ags.get(i).getMyAgentIdentifier();
				final ResourceIdentifier hId = hs.get(j).getMyAgentIdentifier();
				accesibilityGraph[i][j] =  drg.getAccessibleAgents(hId).contains(agId);
			}
		}

		agentCriticity = new IntegerConstantVariable[nbAgents];
		repProcCharge = new int[nbAgents];
		repMemCharge = new int[nbAgents];

		hostLambda = new int[nbHosts];
		hostProcCap = new int[nbHosts];
		hostMemCap = new int[nbHosts];

		for (int i = 0; i < nbAgents; i++){
			agentCriticity[i]=Choco.constant(asInt(ags.get(i).getMyCriticity(),true));
			repProcCharge[i]=asInt(ags.get(i).getMyProcCharge(),false);
			repMemCharge[i]=asInt(ags.get(i).getMyMemCharge(),false);
			//			System.out.println("crit proc rep de agent "+i+" "+this.agentCriticity[i]+" "+this.repProcCharge[i]+" "+this.repMemCharge[i]+"\n"+ags[i]);
		}

		for (int j = 0; j < nbHosts; j++){
			hostLambda[j]=asInt(hs.get(j).getLambda(),true);
			hostProcCap[j]=asInt(hs.get(j).getProcChargeMax(),false);
			hostMemCap[j]=asInt(hs.get(j).getMemChargeMax(),false);
			//			System.out.println("lambde proc rep de host "+i+" "+this.hostLambda[i]+" "+this.hostProcCap[i]+" "+this.hostMemCap[i]+"\n"+hs[i]);
		}

		/*
		 * Instanciating variables
		 */

		//matrice d'allocation
		this.hostsMatrix = new IntegerVariable[nbHosts][nbAgents];
		this.agentsMatrix = new IntegerVariable[nbAgents][nbHosts];
		for (int i = 0; i < nbAgents; i++){
			for (int j = 0; j < nbHosts; j++){
				final AgentIdentifier agId = ags.get(i).getMyAgentIdentifier();
				final ResourceIdentifier hId = hs.get(j).getMyAgentIdentifier();
				final IntegerVariable agentIhostJ;
				if (accesibilityGraph[i][j]){
					agentIhostJ = Choco.makeIntVar(
							"agent_"+agId+"_host_"+hId, 0, 1, Options.V_ENUM);
				} else {
					agentIhostJ = Choco.constant(0);
				}
				this.agentsMatrix[i][j] = agentIhostJ;
				this.hostsMatrix[j][i] = agentIhostJ;
			}
		}

		//utilité des agents
		IntegerExpressionVariable[] agentsValue = new IntegerExpressionVariable[nbAgents];
		for (int i = 0; i < nbAgents; i++){
			IntegerExpressionVariable relia_i= Choco.constant(1);
			for (int j = 0; j < nbHosts; j++){
				if (accesibilityGraph[i][j]){
					final AgentIdentifier agId = ags.get(i).getMyAgentIdentifier();
					final ResourceIdentifier hId = hs.get(j).getMyAgentIdentifier();
					IntegerVariable lambda_i_j = Choco.makeIntVar(
							"agent_"+agId+"_host_"+hId, 1, asInt(hs.get(j).getLambda(),false), Options.V_ENUM, Options.V_NO_DECISION);
					m.addConstraint(
							Choco.ifThenElse(
									Choco.eq(agentsMatrix[i][j], 0),
									Choco.eq(lambda_i_j, 1),
									Choco.eq(lambda_i_j, asInt(hs.get(j).getLambda(),false))));
					relia_i = Choco.mult(relia_i, lambda_i_j);
				}
			}

			relia_i = Choco.minus(1, relia_i);
			if (this.socialWelfare==SocialChoiceType.Utility)
				relia_i = Choco.mult(asInt(ags.get(i).getMyCriticity(),false), relia_i);
			else if (this.socialWelfare==SocialChoiceType.Leximin)
				relia_i = Choco.div(asInt(ags.get(i).getMyCriticity(),false), relia_i);

			agentsValue[i] = relia_i;
		}

		//utilité social
		socialWelfareValue = Choco.makeIntVar("welfare", 0, 21474836,
			Options.V_BOUND, Options.V_NO_DECISION, Options.V_OBJECTIVE);

		/*
		 * Instanciating constraints
		 */

		//Poids
		for (int j = 0; j < nbHosts; j++){
			if (ReplicationExperimentationParameters.multiDim) {
				m.addConstraint(Choco.leq(Choco.scalar(repProcCharge, hostsMatrix[j]), hostProcCap[j]));
			}
			m.addConstraint(Choco.leq(Choco.scalar(repMemCharge, hostsMatrix[j]), hostMemCap[j]));
		}

		//Survie
		for (int i = 0; i < nbAgents; i++){
			m.addConstraint(Choco.gt(Choco.sum(this.agentsMatrix[i]),0));
		}

		//Optimisation social
		if (socialWelfare.equals(SocialChoiceType.Leximin)) {
			m.addConstraint(Choco.eq(socialWelfareValue, Choco.min(agentsValue)));
		} else if  (socialWelfare.equals(SocialChoiceType.Utility)) {
			m.addConstraint(Choco.eq(socialWelfareValue, Choco.sum(agentsValue)));
		} else {
			assert socialWelfare.equals(SocialChoiceType.Nash):socialWelfare;
			IntegerExpressionVariable nashValue = Choco.constant(1);
			for (int i = 1; i < nbAgents; i++){
				nashValue = Choco.mult(nashValue, agentsValue[i]);
			}
			m.addConstraint(Choco.eq(socialWelfareValue, nashValue));			
		}
	}


	/***********************/
	//
	// Local constraint solver
	//
	/***********************/


	/*
	 * Methods
	 */

	@Override
	public void initiate(Collection<ReplicationCandidature> concerned) {

		try{
			final Model m = new CPModel();
			if (this.s!=null) {
				this.s.clear();
			}
			this.s = new CPSolver();

			this.concerned=concerned.toArray(new ReplicationCandidature[concerned.size()]);
			final int nbVariable = concerned.size();

			this.instanciateLocalSolver(m);

			this.s.read(m);
			this.s.setValIntIterator(new DecreasingDomain());

		} catch (IncompleteContractException e) {
			throw new RuntimeException();
		}
	}

	private void instanciateLocalSolver(Model m) throws IncompleteContractException {

		/*
		 * Constants
		 */

		int[] replicasProc;
		int[] replicasMem;
		int hostProccapacity;
		int hostMemCapacity;
		int[] replicasGain = null;

		final int nbVariable = concerned.length;

		assert nbVariable>0;

		hostProccapacity =asInt(this.concerned[0].getResourceInitialState().getProcChargeMax(),false);
		hostMemCapacity = asInt(this.concerned[0].getResourceInitialState().getMemChargeMax(),false);
		
		replicasMem = new int[nbVariable];
		if (ReplicationExperimentationParameters.multiDim) 
			replicasProc = new int[nbVariable];

		for (int i = 0; i < nbVariable; i++){
			assert this.concerned[i].getResourceInitialState().equals(this.concerned[0].getResourceInitialState());
			assert this.concerned[i].getAgentInitialState().getMyMemCharge().equals(
					this.concerned[i].getAgentResultingState().getMyMemCharge());
			assert this.concerned[i].getAgentInitialState().getMyProcCharge().equals(
					this.concerned[i].getAgentResultingState().getMyProcCharge());

			replicasMem[i] = asInt(this.concerned[i].getAgentInitialState().getMyMemCharge(),false);
			if (ReplicationExperimentationParameters.multiDim) 
				replicasProc[i] = asInt(this.concerned[i].getAgentInitialState().getMyProcCharge(),false);
		}

		/*
		 * Variables
		 */

		IntegerVariable[] replicasValue;

		this.candidatureAllocation = new IntegerVariable[nbVariable];
		this.socialWelfareValue =  Choco.makeIntVar("utility", 1, 21474836, Options.V_BOUND, Options.V_NO_DECISION);

		if (ReplicationExperimentationParameters.multiDim){

			replicasValue = new IntegerVariable[nbVariable];

		} else {

			replicasGain = new int[nbVariable];

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

				replicasValue[i] = Choco.makeIntVar(
						this.concerned[i].getAgent().toString()+"__value", minUt.getValue(), maxUt.getValue(),
						Options.V_BOUND, Options.V_NO_DECISION);
				m.addConstraint(Choco.eq(
						replicasValue[i],
						Choco.ifThenElse(
								Choco.eq(this.candidatureAllocation[i],0),
								minUt, maxUt)));

			} else {

				replicasGain[i] = maxUt.getValue() - minUt.getValue();

			}

		}


		/*
		 * Constraints
		 */

		if (ReplicationExperimentationParameters.multiDim){

			//Contrainte de poids

			m.addConstraint(Choco.leq(Choco.scalar(replicasProc, this.candidatureAllocation), hostProccapacity));
			m.addConstraint(Choco.leq(Choco.scalar(replicasMem, this.candidatureAllocation), hostMemCapacity));


			//Optimisation social


			if (this.socialWelfare.equals(SocialChoiceType.Leximin)) {
				m.addConstraint(Choco.eq(this.socialWelfareValue, Choco.min(replicasValue)));
			} else {
				assert this.socialWelfare.equals(SocialChoiceType.Nash)
				|| this.socialWelfare.equals(SocialChoiceType.Utility);
				m.addConstraint(Choco.eq (Choco.sum(replicasValue), this.socialWelfareValue));
			}

		} else {

			if (this.socialWelfare.equals(SocialChoiceType.Nash)
					|| this.socialWelfare.equals(SocialChoiceType.Utility)){

				//Optimisation social & Contrainte de poids

				final IntegerVariable weightVar = Choco.constant(hostProccapacity);
				Choco.makeIntVar("weight", 0, 21474836, Options.V_BOUND, Options.V_NO_DECISION);
				m.addConstraint(Choco.knapsackProblem(this.socialWelfareValue, weightVar,
						this.candidatureAllocation, replicasGain, replicasProc));
				m.addConstraint(Choco.leq(weightVar, hostProccapacity));


			} else  {
				assert (this.socialWelfare.equals(SocialChoiceType.Leximin)); 


				//Contrainte de poids

				m.addConstraint(Choco.leq(Choco.scalar(replicasMem, this.candidatureAllocation), hostMemCapacity));

				//Optimisation social

				m.addConstraint(Choco.eq(this.socialWelfareValue, Choco.min(replicasValue)));
			}
		}

		//Contrainte d'amélioration stricte


		if (this.socialWelfare.equals(SocialChoiceType.Leximin)) {

			final int[] currentAllocation = new int[this.concerned.length];
			
			for (int i = 0; i < this.concerned.length; i++){
				currentAllocation[i] = asInt(this.concerned[i].getAgentInitialState().getMyReliability(),false);
			}
			
			m.addConstraint(Choco.leximin(currentAllocation, replicasValue));

		} else {

			assert (this.socialWelfare.equals(SocialChoiceType.Nash)|| this.socialWelfare.equals(SocialChoiceType.Utility));

			int currentSocialValue = 0;
			
			for (final ReplicationCandidature c : this.concerned){
				currentSocialValue+=asIntNashed(c.getAgentInitialState().getMyReliability(),this.socialWelfare);
			}
			
			m.addConstraint(Choco.lt(currentSocialValue, this.socialWelfareValue));

		}
	}

	/*
	 * Primitives
	 */

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

}
