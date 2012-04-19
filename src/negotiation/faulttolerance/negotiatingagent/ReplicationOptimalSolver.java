package negotiation.faulttolerance.negotiatingagent;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.sun.corba.se.spi.ior.MakeImmutable;

import negotiation.faulttolerance.experimentation.ReplicationInstanceGraph;
import negotiation.negotiationframework.rationality.SocialChoiceFunction.SocialChoiceType;
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

public class ReplicationOptimalSolver {

	Model m = new CPModel();
	Solver s = new CPSolver();

	public ReplicationOptimalSolver(
			ReplicationInstanceGraph rig, SocialChoiceType _socialChoice) {
		super();
		this.rig = rig;
		this._socialChoice = _socialChoice;
	}

	/*
	 * Instance
	 */
	ReplicationInstanceGraph rig;
	SocialChoiceType _socialChoice;
	
	int nbAgents =rig.getAgentsIdentifier().size();
	int nbHosts=rig.getHostsIdentifier().size();
	List<ReplicaState> ags = new ArrayList(rig.getAgentStates());
	List<HostState> hs = new ArrayList(rig.getHostsStates());

	/*
	 * Constants
	 */

	IntegerConstantVariable[] agentCriticity = new IntegerConstantVariable[nbAgents];
	int[] repProcCharge = new int[nbAgents];
	int[] repMemCharge = new int[nbAgents];

	int[] hostLambda = new int[nbHosts];
	int[] hostProcCap = new int[nbHosts];
	int[] hostMemCap = new int[nbHosts];

	/*
	 * Variable
	 */
	IntegerVariable[][] hostsMatrix = new IntegerVariable[nbHosts][nbAgents];
	IntegerVariable[][] agentsMatrix = new IntegerVariable[nbAgents][nbHosts];
	IntegerVariable socialWelfare = Choco.makeIntVar("welfare", 0, 10000, 
			Options.V_BOUND, Options.V_NO_DECISION, Options.V_OBJECTIVE);
	IntegerVariable[] agentsValue = new IntegerVariable[nbAgents];

	public void solve(){
		generateConstant();
		generateVar();
		generateConstraints();
		s.read(m);
		s.setValIntIterator(new DecreasingDomain());
		s.maximize(false);
	}

	private void generateConstant(){

		for (int i = 0; i < nbAgents; i++){
			agentCriticity[i]=new IntegerConstantVariable(asInt(ags.get(i).getMyCriticity(),false));
			repProcCharge[i]=asInt(ags.get(i).getMyProcCharge(),false);
			repMemCharge[i]=asInt(ags.get(i).getMyMemCharge(),false);
		}

		for (int i = 0; i < nbHosts; i++){
			hostLambda[i]=asInt(hs.get(i).getLambda(),true);
			hostProcCap[i]=asInt(hs.get(i).getProcChargeMax(),false);
			hostMemCap[i]=asInt(hs.get(i).getMemChargeMax(),false);
		}
	}

	private void generateVar(){
		for (int i = 0; i < nbAgents; i++){
			for (int j = 0; j < nbHosts; j++){
				IntegerVariable agentIhostJ = Choco.makeIntVar("agent_"+i+"_host_"+j, 0, 1, Options.V_ENUM);
				agentsMatrix[i][j] = agentIhostJ;
				hostsMatrix[j][i] = agentIhostJ;
			} 
		}
		for (int i = 0; i < nbAgents; i++){
			agentsValue[i] = Choco.makeIntVar("welfare", 0, 10000, 
					Options.V_BOUND, Options.V_NO_DECISION);
			IntegerExpressionVariable dispo = Choco.minus(1, Choco.scalar(hostLambda, agentsMatrix[i]));
			IntegerExpressionVariable agentVal;
			if (socialWelfare.equals(SocialChoiceType.Leximin))
				agentVal = Choco.div(dispo,agentCriticity[i]);
			else 
				agentVal = Choco.mult(agentCriticity[i], dispo);
				
			m.addConstraint(Choco.eq(agentsValue[i], agentVal));
		}
	}

	private void generateConstraints(){
		//Poids
		for (int i = 0; i < nbHosts; i++){
			m.addConstraint(Choco.leq(Choco.scalar(repProcCharge, hostsMatrix[i]), hostProcCap[i]));
			m.addConstraint(Choco.leq(Choco.scalar(repMemCharge, hostsMatrix[i]), hostMemCap[i]));
		}
		
		//Optimisation social
		if (socialWelfare.equals(SocialChoiceType.Leximin)) {
			m.addConstraint(Choco.eq(socialWelfare, Choco.min(agentsValue)));
		} else {
			assert (socialWelfare.equals(SocialChoiceType.Nash) 
					|| socialWelfare.equals(SocialChoiceType.Utility));
			m.addConstraint(Choco.eq(socialWelfare, Choco.sum(agentsValue)));
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
}
