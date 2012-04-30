package negotiation.faulttolerance.experimentation;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import negotiation.faulttolerance.negotiatingagent.HostState;
import negotiation.faulttolerance.negotiatingagent.ReplicaState;
import negotiation.negotiationframework.contracts.ResourceIdentifier;
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
import dima.basicagentcomponents.AgentIdentifier;
import dima.introspectionbasedagents.services.BasicAgentModule;
import dima.introspectionbasedagents.services.CompetenceException;
import dima.introspectionbasedagents.services.UnrespectedCompetenceSyntaxException;
import dima.introspectionbasedagents.services.loggingactivity.LogService;
import dimaxx.experimentation.ExperimentationParameters;
import dimaxx.experimentation.IfailedException;
import dimaxx.experimentation.Laborantin.NotEnoughMachinesException;

public class ReplicationOptimalSolver extends BasicAgentModule<ReplicationLaborantin> {

	Model m = new CPModel();

	public ReplicationOptimalSolver(ReplicationLaborantin l) {
		super(l);
		this._socialChoice = l.getSimulationParameters()._socialWelfare;

		ReplicationInstanceGraph rig = l.getSimulationParameters().rig;
		nbAgents =rig.getAgentsIdentifier().size();
		nbHosts=rig.getHostsIdentifier().size();
		ags = rig.getAgentStates().toArray(new ReplicaState[nbAgents]);
		hs = rig.getHostsStates().toArray(new HostState[nbHosts]);
		accesibilityGraph = new boolean[nbAgents][nbHosts];
		
//		charge = new IntegerExpressionVariable[this.nbHosts];
//		numberOfRep = new IntegerExpressionVariable[this.nbAgents];

		for (int i = 0; i < this.nbAgents; i++){
			for (int j = 0; j < this.nbHosts; j++){
				AgentIdentifier agId = ags[i].getMyAgentIdentifier();
				ResourceIdentifier hId = hs[j].getMyAgentIdentifier();
				assert ags[i].getMyResourceIdentifiers().isEmpty();
				assert hs[j].getMyResourceIdentifiers().isEmpty();
				assert rig.getAccessibleAgent(hId).contains(agId)==rig.getAccessibleHost(agId).contains(hId):"\n"+rig.getAccessibleAgent(hId)+"\n"+rig.getAccessibleHost(agId);
				accesibilityGraph[i][j] =  rig.getAccessibleAgent(hId).contains(agId);
				assert ReplicationExperimentationParameters.completGraph?accesibilityGraph[i][j]==true:true;
			}
		}
	}

	private ReplicationOptimalSolver(ReplicationExperimentationParameters p) {
		super();
		this._socialChoice = p._socialWelfare;

		ReplicationInstanceGraph rig = p.rig;
		nbAgents =rig.getAgentsIdentifier().size();
		nbHosts=rig.getHostsIdentifier().size();
		ags = rig.getAgentStates().toArray(new ReplicaState[nbAgents]);
		hs = rig.getHostsStates().toArray(new HostState[nbHosts]);
		accesibilityGraph = new boolean[nbAgents][nbHosts];
		
//		charge = new IntegerExpressionVariable[this.nbHosts];
//		numberOfRep = new IntegerExpressionVariable[this.nbAgents];

		for (int i = 0; i < this.nbAgents; i++){
			for (int j = 0; j < this.nbHosts; j++){
				AgentIdentifier agId = ags[i].getMyAgentIdentifier();
				ResourceIdentifier hId = hs[j].getMyAgentIdentifier();
				assert ags[i].getMyResourceIdentifiers().isEmpty();
				assert hs[j].getMyResourceIdentifiers().isEmpty();
				assert rig.getAccessibleAgent(hId).contains(agId)==rig.getAccessibleHost(agId).contains(hId):"\n"+rig.getAccessibleAgent(hId)+"\n"+rig.getAccessibleHost(agId);
				accesibilityGraph[i][j] =  rig.getAccessibleAgent(hId).contains(agId);
				assert ReplicationExperimentationParameters.completGraph?accesibilityGraph[i][j]==true:true;
			}
		}
	}
	/*
	 * Instance
	 */
	SocialChoiceType _socialChoice;

	final int nbAgents;
	final int nbHosts;
	ReplicaState[] ags;
	HostState[] hs;

	/*
	 * Constants
	 */

	IntegerConstantVariable[] agentCriticity;
	int[] repProcCharge;
	int[] repMemCharge;

	int[] hostLambda;
	int[] hostProcCap;
	int[] hostMemCap;
	boolean[][] accesibilityGraph;

	/*
	 * Variable
	 */

	IntegerVariable[][] hostsMatrix;
	IntegerVariable[][] agentsMatrix;
	
	//
	
	IntegerVariable socialWelfareOpt;
	IntegerExpressionVariable[] agentsValue;

	//
	// Method
	//

	public void solve(){
		if (_socialChoice.equals(SocialChoiceType.Utility)){
			this.generateConstant();
			this.generateVar();
			this.generateConstraints();

			//

			Solver s = new CPSolver();
			s.read(this.m);
			s.setValIntIterator(new DecreasingDomain());
			logMonologue("solving optimal...", LogService.onBoth);
//			s.solve();
			int firstTime=0;//s.getTimeCount();
//			assert s.isFeasible();
			s.setObjective(s.getVar(this.socialWelfareOpt));
			s.minimize(true);
			int optimalTime=s.getTimeCount();
			logMonologue("done!...", LogService.onBoth);

			//

			writeResults(s,firstTime,optimalTime);
			s.clear();
			s=null;
		} else {
			logMonologue("no optimal for "+_socialChoice, LogService.onBoth);
		}
	}

	private void writeResults(Solver s, int firstTime, int optimalTime) {
		Map<AgentIdentifier, ReplicaState> finalRepAlloc = new HashMap<AgentIdentifier, ReplicaState>();
		Map<ResourceIdentifier, HostState> finalHostAlloc = new HashMap<ResourceIdentifier, HostState>();

		for (int i = 0; i < this.nbAgents; i++){
			finalRepAlloc.put(ags[i].getMyAgentIdentifier(),ags[i]);
		}	
		for (int j = 0; j < this.nbHosts; j++){
			finalHostAlloc.put(hs[j].getMyAgentIdentifier(),hs[j]);						
		}

		String alloc="";
		for (int j = 0; j < this.nbHosts; j++){
			ResourceIdentifier hId = hs[j].getMyAgentIdentifier();
			alloc+="\n "+hId+" has allocated ";
			for (int i = 0; i < this.nbAgents; i++){
				assert s.getVar(hostsMatrix[j][i]).getVal()==s.getVar(agentsMatrix[i][j]).getVal();
				if (s.getVar(hostsMatrix[j][i]).getVal()==1){
					AgentIdentifier agId = ags[i].getMyAgentIdentifier();
					alloc+=agId+" , ";
					boolean allocOk = ReplicationInstanceGraph.allocateAgents(getMyAgent(), agId, hId, finalRepAlloc, finalHostAlloc);
					assert allocOk;
					
				}
			}
		}

		ReplicationObservingGlobalService rogs = new ReplicationObservingGlobalService();
		rogs.setMyAgent(getMyAgent());
		rogs.imTheOpt=true;
		rogs.firstoptimaltime=firstTime;
		rogs.optimalTime=optimalTime;
		rogs.initiate();

		for (ReplicaState r : finalRepAlloc.values()){
			ReplicationResultAgent agRes = new ReplicationResultAgent(r, new Date());
			agRes.setLastInfo();
			rogs.updateInfo(agRes);
			rogs.getFinalStates().add(agRes);
		}
		for (HostState h : finalHostAlloc.values()){
			ReplicationResultHost hostRes = new ReplicationResultHost(h,0 ,0, new Date(),0);
			hostRes.setLastInfo();
			rogs.updateInfo(hostRes);
			rogs.getFinalStates().add(hostRes);
		}
		logMonologue("First Time : "+firstTime/1000+"Optimal Time : "+optimalTime/1000
				+" The optimal allocation is : \n"+alloc
				+"\n"+finalHostAlloc.values()+"\n"+finalRepAlloc.values(), LogService.onBoth);
//		rogs.writeResult();
		
		System.out.println(rogs.analyseOptimal());
		
	}

	//
	// Primitive
	//

	private void generateConstant(){

		agentCriticity = new IntegerConstantVariable[this.nbAgents];
		repProcCharge = new int[this.nbAgents];
		repMemCharge = new int[this.nbAgents];

		hostLambda = new int[this.nbHosts];
		hostProcCap = new int[this.nbHosts];
		hostMemCap = new int[this.nbHosts];

		for (int i = 0; i < this.nbAgents; i++){
			this.agentCriticity[i]=Choco.constant(this.asInt(this.ags[i].getMyCriticity(),true));
			this.repProcCharge[i]=this.asInt(this.ags[i].getMyProcCharge(),false);
			this.repMemCharge[i]=this.asInt(this.ags[i].getMyMemCharge(),false);
//			System.out.println("crit proc rep de agent "+i+" "+this.agentCriticity[i]+" "+this.repProcCharge[i]+" "+this.repMemCharge[i]+"\n"+ags[i]);
		}

		for (int i = 0; i < this.nbHosts; i++){
			this.hostLambda[i]=this.asInt(this.hs[i].getLambda(),true);
			this.hostProcCap[i]=this.asInt(this.hs[i].getProcChargeMax(),false);
			this.hostMemCap[i]=this.asInt(this.hs[i].getMemChargeMax(),false);
//			System.out.println("lambde proc rep de host "+i+" "+this.hostLambda[i]+" "+this.hostProcCap[i]+" "+this.hostMemCap[i]+"\n"+hs[i]);
		}
	}

	private void generateVar(){

		//matrice d'allocation
		hostsMatrix = new IntegerVariable[this.nbHosts][this.nbAgents];
		agentsMatrix = new IntegerVariable[this.nbAgents][this.nbHosts];
		for (int i = 0; i < this.nbAgents; i++){
			for (int j = 0; j < this.nbHosts; j++){
				AgentIdentifier agId = ags[i].getMyAgentIdentifier();
				ResourceIdentifier hId = hs[j].getMyAgentIdentifier();
				final IntegerVariable agentIhostJ = Choco.makeIntVar(
						"agent_"+agId+"_host_"+hId, 0, accesibilityGraph[i][j]?1:0, Options.V_ENUM);
				this.agentsMatrix[i][j] = agentIhostJ;
				this.hostsMatrix[j][i] = agentIhostJ;
			}
		}

		//utilité des agents
		agentsValue = new IntegerExpressionVariable[this.nbAgents];
		for (int i = 0; i < this.nbAgents; i++){
//			final IntegerExpressionVariable dispo = Choco.minus(1, Choco.scalar(this.hostLambda, this.agentsMatrix[i]));
			final IntegerExpressionVariable dispo_i = Choco.scalar(this.hostLambda, this.agentsMatrix[i]);
//			if (this._socialChoice.equals(SocialChoiceType.Leximin)) {
//				this.agentsValue[i] = Choco.div(dispo,this.agentCriticity[i]);
//			} else {
				assert this._socialChoice.equals(SocialChoiceType.Utility):_socialChoice;
				this.agentsValue[i] = Choco.plus(dispo_i,this.agentCriticity[i]);
//			}
		}

		socialWelfareOpt = Choco.makeIntVar("welfare", 0, 10000,
				Options.V_BOUND, Options.V_NO_DECISION, Options.V_OBJECTIVE);
	}

	
	private void generateConstraints(){
		//Poids
		for (int j = 0; j < this.nbHosts; j++){
			if (ReplicationExperimentationParameters.multiDim) 
				this.m.addConstraint(Choco.leq(Choco.scalar(this.repProcCharge, this.hostsMatrix[j]), this.hostProcCap[j]));
			this.m.addConstraint(Choco.leq(Choco.scalar(this.repMemCharge, this.hostsMatrix[j]), this.hostMemCap[j]));
		}

		//Survie
		for (int i = 0; i < this.nbAgents; i++){
			this.m.addConstraint(Choco.gt(Choco.sum(this.agentsMatrix[i]),0));
//			this.m.addConstraint(Choco.eq(Choco.min(this.agentsMatrix[i]),1));
		}

//		//Optimisation social
//		if (this._socialChoice.equals(SocialChoiceType.Leximin)) {
//			this.m.addConstraint(Choco.eq(this.socialWelfareOpt, Choco.max(this.agentsValue)));
//		} else {
			assert this._socialChoice.equals(SocialChoiceType.Utility):_socialChoice;
			this.m.addConstraint(Choco.eq(this.socialWelfareOpt, Choco.sum(this.agentsValue)));
//		}
	}

	public static int asIntNashed(final double d, SocialChoiceType _socialChoice){
		if (_socialChoice.equals(SocialChoiceType.Nash)) {
			return asInt(d, true);
		} else {
			assert (_socialChoice.equals(SocialChoiceType.Leximin)
					|| _socialChoice.equals(SocialChoiceType.Utility)):_socialChoice;
			return asInt(d, false);
		}
	}
	public static int asInt(final double d, final boolean log){
		if (log) {
			return (int) (Math.log(100*(d+0.01)));
		} else {
			return (int) (100 * (d+0.01));
		}
	}
	
	public static void main(String[] args) throws IfailedException, CompetenceException, NotEnoughMachinesException{
		ReplicationExperimentationParameters p = ReplicationExperimentationParameters.getDefaultParameters();
		ReplicationLaborantin rl = new ReplicationLaborantin(p, null);		
		p.initiateParameters();
		ReplicationOptimalSolver ros = new ReplicationOptimalSolver(p);
		ros.solve();
	}
}
