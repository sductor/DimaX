package negotiation.faulttolerance.negotiatingagent;

import java.util.ArrayList;
import java.util.List;

import negotiation.faulttolerance.experimentation.ReplicationInstanceGraph;
import negotiation.negotiationframework.rationality.SocialChoiceFunction.SocialChoiceType;
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

public class ReplicationOptimalSolver {

	Model m = new CPModel();
	Solver s = new CPSolver();

	public ReplicationOptimalSolver(
			final ReplicationInstanceGraph rig, final SocialChoiceType _socialChoice) {
		super();
		this.rig = rig;
		this._socialChoice = _socialChoice;
	}

	/*
	 * Instance
	 */
	ReplicationInstanceGraph rig;
	SocialChoiceType _socialChoice;

	int nbAgents =this.rig.getAgentsIdentifier().size();
	int nbHosts=this.rig.getHostsIdentifier().size();
	List<ReplicaState> ags = new ArrayList(this.rig.getAgentStates());
	List<HostState> hs = new ArrayList(this.rig.getHostsStates());

	/*
	 * Constants
	 */

	IntegerConstantVariable[] agentCriticity = new IntegerConstantVariable[this.nbAgents];
	int[] repProcCharge = new int[this.nbAgents];
	int[] repMemCharge = new int[this.nbAgents];

	int[] hostLambda = new int[this.nbHosts];
	int[] hostProcCap = new int[this.nbHosts];
	int[] hostMemCap = new int[this.nbHosts];

	/*
	 * Variable
	 */
	IntegerVariable[][] hostsMatrix = new IntegerVariable[this.nbHosts][this.nbAgents];
	IntegerVariable[][] agentsMatrix = new IntegerVariable[this.nbAgents][this.nbHosts];
	IntegerVariable socialWelfare = Choco.makeIntVar("welfare", 0, 10000,
			Options.V_BOUND, Options.V_NO_DECISION, Options.V_OBJECTIVE);
	IntegerVariable[] agentsValue = new IntegerVariable[this.nbAgents];

	public void solve(){
		this.generateConstant();
		this.generateVar();
		this.generateConstraints();
		this.s.read(this.m);
		this.s.setValIntIterator(new DecreasingDomain());
		this.s.maximize(false);
	}

	private void generateConstant(){

		for (int i = 0; i < this.nbAgents; i++){
			this.agentCriticity[i]=new IntegerConstantVariable(this.asInt(this.ags.get(i).getMyCriticity(),false));
			this.repProcCharge[i]=this.asInt(this.ags.get(i).getMyProcCharge(),false);
			this.repMemCharge[i]=this.asInt(this.ags.get(i).getMyMemCharge(),false);
		}

		for (int i = 0; i < this.nbHosts; i++){
			this.hostLambda[i]=this.asInt(this.hs.get(i).getLambda(),true);
			this.hostProcCap[i]=this.asInt(this.hs.get(i).getProcChargeMax(),false);
			this.hostMemCap[i]=this.asInt(this.hs.get(i).getMemChargeMax(),false);
		}
	}

	private void generateVar(){
		for (int i = 0; i < this.nbAgents; i++){
			for (int j = 0; j < this.nbHosts; j++){
				final IntegerVariable agentIhostJ = Choco.makeIntVar("agent_"+i+"_host_"+j, 0, 1, Options.V_ENUM);
				this.agentsMatrix[i][j] = agentIhostJ;
				this.hostsMatrix[j][i] = agentIhostJ;
				assert 1<0:"implementer la prise en comte du graphe d'acéssibilité";
				
			}
		}
		for (int i = 0; i < this.nbAgents; i++){
			this.agentsValue[i] = Choco.makeIntVar("welfare", 0, 10000,
					Options.V_BOUND, Options.V_NO_DECISION);
			final IntegerExpressionVariable dispo = Choco.minus(1, Choco.scalar(this.hostLambda, this.agentsMatrix[i]));
			IntegerExpressionVariable agentVal;
			if (this.socialWelfare.equals(SocialChoiceType.Leximin)) {
				agentVal = Choco.div(dispo,this.agentCriticity[i]);
			} else {
				agentVal = Choco.mult(this.agentCriticity[i], dispo);
			}

			this.m.addConstraint(Choco.eq(this.agentsValue[i], agentVal));
		}
	}

	private void generateConstraints(){
		//Poids
		for (int i = 0; i < this.nbHosts; i++){
			this.m.addConstraint(Choco.leq(Choco.scalar(this.repProcCharge, this.hostsMatrix[i]), this.hostProcCap[i]));
			this.m.addConstraint(Choco.leq(Choco.scalar(this.repMemCharge, this.hostsMatrix[i]), this.hostMemCap[i]));
		}

		//Optimisation social
		if (this.socialWelfare.equals(SocialChoiceType.Leximin)) {
			this.m.addConstraint(Choco.eq(this.socialWelfare, Choco.min(this.agentsValue)));
		} else {
			assert (this.socialWelfare.equals(SocialChoiceType.Nash)
					|| this.socialWelfare.equals(SocialChoiceType.Utility));
			this.m.addConstraint(Choco.eq(this.socialWelfare, Choco.sum(this.agentsValue)));
		}
	}

	public int asIntNashed(final double d){
		if (this.s.equals(SocialChoiceType.Nash)) {
			return (int) (100*Math.log(d));
		} else {
			return (int) (100 * d);
		}
	}
	public int asInt(final double d, final boolean log){
		if (log) {
			return (int) (100*Math.log(d));
		} else {
			return (int) (100 * d);
		}
	}
}
