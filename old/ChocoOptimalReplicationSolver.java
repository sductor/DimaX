package frameworks.faulttolerance.solver;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

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
import dima.basicagentcomponents.AgentIdentifier;
import dima.introspectionbasedagents.services.BasicAgentModule;
import dima.introspectionbasedagents.services.CompetenceException;
import dima.introspectionbasedagents.services.loggingactivity.LogService;
import frameworks.experimentation.IfailedException;
import frameworks.experimentation.Laborantin.NotEnoughMachinesException;
import frameworks.faulttolerance.experimentation.ReplicationExperimentationParameters;
import frameworks.faulttolerance.experimentation.ReplicationInstanceGraph;
import frameworks.faulttolerance.experimentation.ReplicationLaborantin;
import frameworks.faulttolerance.experimentation.ReplicationObservingGlobalService;
import frameworks.faulttolerance.experimentation.ReplicationResultAgent;
import frameworks.faulttolerance.experimentation.ReplicationResultHost;
import frameworks.faulttolerance.negotiatingagent.HostState;
import frameworks.faulttolerance.negotiatingagent.ReplicaState;
import frameworks.negotiation.contracts.ResourceIdentifier;
import frameworks.negotiation.rationality.SocialChoiceFunction.SocialChoiceType;

public class ChocoOptimalReplicationSolver 
extends BasicAgentModule<ReplicationLaborantin> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7958932620338008564L;

	Model m = new CPModel();

	public ChocoOptimalReplicationSolver(final ReplicationLaborantin l) {
		super(l);
		this._socialChoice = l.getSimulationParameters()._socialWelfare;

		final ReplicationInstanceGraph rig = l.getSimulationParameters().rig;
		this.nbAgents =rig.getAgentsIdentifier().size();
		this.nbHosts=rig.getHostsIdentifier().size();
		this.ags = rig.getAgentStates().toArray(new ReplicaState[this.nbAgents]);
		this.hs = rig.getHostsStates().toArray(new HostState[this.nbHosts]);
		this.accesibilityGraph = new boolean[this.nbAgents][this.nbHosts];

		//		charge = new IntegerExpressionVariable[this.nbHosts];
		//		numberOfRep = new IntegerExpressionVariable[this.nbAgents];

		for (int i = 0; i < this.nbAgents; i++){
			for (int j = 0; j < this.nbHosts; j++){
				final AgentIdentifier agId = this.ags[i].getMyAgentIdentifier();
				final ResourceIdentifier hId = this.hs[j].getMyAgentIdentifier();
				assert this.ags[i].getMyResourceIdentifiers().isEmpty();
				assert this.hs[j].getMyResourceIdentifiers().isEmpty();
				assert rig.getAccessibleAgent(hId).contains(agId)==rig.getAccessibleHost(agId).contains(hId):"\n"+rig.getAccessibleAgent(hId)+"\n"+rig.getAccessibleHost(agId);
				this.accesibilityGraph[i][j] =  rig.getAccessibleAgent(hId).contains(agId);
				assert this.getMyAgent().getSimulationParameters().completGraph?this.accesibilityGraph[i][j]==true:true;
			}
		}
	}

	private ChocoOptimalReplicationSolver(final ReplicationExperimentationParameters p) {
		super();
		this._socialChoice = p._socialWelfare;

		final ReplicationInstanceGraph rig = p.rig;
		this.nbAgents =rig.getAgentsIdentifier().size();
		this.nbHosts=rig.getHostsIdentifier().size();
		this.ags = rig.getAgentStates().toArray(new ReplicaState[this.nbAgents]);
		this.hs = rig.getHostsStates().toArray(new HostState[this.nbHosts]);
		this.accesibilityGraph = new boolean[this.nbAgents][this.nbHosts];

		//		charge = new IntegerExpressionVariable[this.nbHosts];
		//		numberOfRep = new IntegerExpressionVariable[this.nbAgents];

		for (int i = 0; i < this.nbAgents; i++){
			for (int j = 0; j < this.nbHosts; j++){
				final AgentIdentifier agId = this.ags[i].getMyAgentIdentifier();
				final ResourceIdentifier hId = this.hs[j].getMyAgentIdentifier();
				assert this.ags[i].getMyResourceIdentifiers().isEmpty();
				assert this.hs[j].getMyResourceIdentifiers().isEmpty();
				assert rig.getAccessibleAgent(hId).contains(agId)==rig.getAccessibleHost(agId).contains(hId):"\n"+rig.getAccessibleAgent(hId)+"\n"+rig.getAccessibleHost(agId);
				this.accesibilityGraph[i][j] =  rig.getAccessibleAgent(hId).contains(agId);
				assert this.getMyAgent().getSimulationParameters().completGraph?this.accesibilityGraph[i][j]==true:true;
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
		if (this._socialChoice.equals(SocialChoiceType.Utility)){
			this.generateConstant();
			this.generateVar();
			this.generateConstraints();

			//

			Solver s = new CPSolver();
			s.read(this.m);
			s.setValIntIterator(new DecreasingDomain());
			this.logMonologue("solving optimal...", LogService.onBoth);
			//			s.solve();
			final int firstTime=0;//s.getTimeCount();
			//			assert s.isFeasible();
			s.setObjective(s.getVar(this.socialWelfareOpt));
			s.minimize(false);
			final int optimalTime=s.getTimeCount();
			this.logMonologue("done!...", LogService.onBoth);

			//

			this.writeResults(s,firstTime,optimalTime);
			s.clear();
			s=null;
		} else {
			this.logMonologue("no optimal for "+this._socialChoice, LogService.onBoth);
		}
	}

	private void writeResults(final Solver s, final int firstTime, final int optimalTime) {
		final Map<AgentIdentifier, ReplicaState> finalRepAlloc = new HashMap<AgentIdentifier, ReplicaState>();
		final Map<ResourceIdentifier, HostState> finalHostAlloc = new HashMap<ResourceIdentifier, HostState>();

		for (int i = 0; i < this.nbAgents; i++){
			finalRepAlloc.put(this.ags[i].getMyAgentIdentifier(),this.ags[i]);
		}
		for (int j = 0; j < this.nbHosts; j++){
			finalHostAlloc.put(this.hs[j].getMyAgentIdentifier(),this.hs[j]);
		}

		String alloc="";
		for (int j = 0; j < this.nbHosts; j++){
			final ResourceIdentifier hId = this.hs[j].getMyAgentIdentifier();
			alloc+="\n "+hId+" has allocated ";
			for (int i = 0; i < this.nbAgents; i++){
				assert s.getVar(this.hostsMatrix[j][i]).getVal()==s.getVar(this.agentsMatrix[i][j]).getVal();
				if (s.getVar(this.hostsMatrix[j][i]).getVal()==1){
					final AgentIdentifier agId = this.ags[i].getMyAgentIdentifier();
					alloc+=agId+" , ";
					final boolean allocOk = ReplicationInstanceGraph.allocateAgents(agId, hId, finalRepAlloc, finalHostAlloc);
					assert allocOk;

				}
			}
		}

		final ReplicationObservingGlobalService rogs = new ReplicationObservingGlobalService(this.getMyAgent().getSimulationParameters());
		rogs.setMyAgent(this.getMyAgent());
		rogs.imTheOpt=true;
		rogs.firstoptimaltime=firstTime;
		rogs.optimalTime=optimalTime;
		rogs.initiate();

		for (final ReplicaState r : finalRepAlloc.values()){
			final ReplicationResultAgent agRes = new ReplicationResultAgent(r, new Date());
			agRes.setLastInfo();
			rogs.updateInfo(agRes);
			rogs.getFinalStates().add(agRes);
		}
		for (final HostState h : finalHostAlloc.values()){
			final ReplicationResultHost hostRes = new ReplicationResultHost(h,0 , new Date(),0,null);
			hostRes.setLastInfo();
			rogs.updateInfo(hostRes);
			rogs.getFinalStates().add(hostRes);
		}
		this.logMonologue("First Time : "+firstTime/1000+"Optimal Time : "+optimalTime/1000
				//				+" The optimal allocation is : \n"+alloc
				//				+"\n"+finalHostAlloc.values()+"\n"+finalRepAlloc.values()
				, LogService.onBoth);
		rogs.writeResult();

		//		System.out.println(rogs.analyseOptimal());

	}

	//
	// Primitive
	//

	private void generateConstant(){

		this.agentCriticity = new IntegerConstantVariable[this.nbAgents];
		this.repProcCharge = new int[this.nbAgents];
		this.repMemCharge = new int[this.nbAgents];

		this.hostLambda = new int[this.nbHosts];
		this.hostProcCap = new int[this.nbHosts];
		this.hostMemCap = new int[this.nbHosts];

		for (int i = 0; i < this.nbAgents; i++){
			this.agentCriticity[i]=Choco.constant(ChocoOptimalReplicationSolver.asInt(this.ags[i].getMyCriticity(),true));
			this.repProcCharge[i]=ChocoOptimalReplicationSolver.asInt(this.ags[i].getMyProcCharge(),false);
			this.repMemCharge[i]=ChocoOptimalReplicationSolver.asInt(this.ags[i].getMyMemCharge(),false);
			//			System.out.println("crit proc rep de agent "+i+" "+this.agentCriticity[i]+" "+this.repProcCharge[i]+" "+this.repMemCharge[i]+"\n"+ags[i]);
		}

		for (int i = 0; i < this.nbHosts; i++){
			this.hostLambda[i]=ChocoOptimalReplicationSolver.asInt(this.hs[i].getLambda(),true);
			this.hostProcCap[i]=ChocoOptimalReplicationSolver.asInt(this.hs[i].getProcChargeMax(),false);
			this.hostMemCap[i]=ChocoOptimalReplicationSolver.asInt(this.hs[i].getMemChargeMax(),false);
			//			System.out.println("lambde proc rep de host "+i+" "+this.hostLambda[i]+" "+this.hostProcCap[i]+" "+this.hostMemCap[i]+"\n"+hs[i]);
		}
	}

	private void generateVar(){

		//matrice d'allocation
		this.hostsMatrix = new IntegerVariable[this.nbHosts][this.nbAgents];
		this.agentsMatrix = new IntegerVariable[this.nbAgents][this.nbHosts];
		for (int i = 0; i < this.nbAgents; i++){
			for (int j = 0; j < this.nbHosts; j++){
				final AgentIdentifier agId = this.ags[i].getMyAgentIdentifier();
				final ResourceIdentifier hId = this.hs[j].getMyAgentIdentifier();
				final IntegerVariable agentIhostJ = Choco.makeIntVar(
						"agent_"+agId+"_host_"+hId, 0, this.accesibilityGraph[i][j]?1:0, Options.V_ENUM);
				this.agentsMatrix[i][j] = agentIhostJ;
				this.hostsMatrix[j][i] = agentIhostJ;
			}
		}

		//utilité des agents
		this.agentsValue = new IntegerExpressionVariable[this.nbAgents];
		for (int i = 0; i < this.nbAgents; i++){
			//			final IntegerExpressionVariable dispo = Choco.minus(1, Choco.scalar(this.hostLambda, this.agentsMatrix[i]));
			final IntegerExpressionVariable dispo_i = Choco.scalar(this.hostLambda, this.agentsMatrix[i]);
			//			if (this._socialChoice.equals(SocialChoiceType.Leximin)) {
			//				this.agentsValue[i] = Choco.div(dispo,this.agentCriticity[i]);
			//			} else {
			assert this._socialChoice.equals(SocialChoiceType.Utility):this._socialChoice;
			this.agentsValue[i] = Choco.plus(dispo_i,this.agentCriticity[i]);
			//			}
		}

		this.socialWelfareOpt = Choco.makeIntVar("welfare", 0, 10000,
				Options.V_BOUND, Options.V_NO_DECISION, Options.V_OBJECTIVE);
	}


	private void generateConstraints(){
		//Poids
		for (int j = 0; j < this.nbHosts; j++){
			if (ReplicationExperimentationParameters.multiDim) {
				this.m.addConstraint(Choco.leq(Choco.scalar(this.repProcCharge, this.hostsMatrix[j]), this.hostProcCap[j]));
			}
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
		assert this._socialChoice.equals(SocialChoiceType.Utility):this._socialChoice;
		this.m.addConstraint(Choco.eq(this.socialWelfareOpt, Choco.sum(this.agentsValue)));
		//		}
	}

	public static int asIntNashed(final double d, final SocialChoiceType _socialChoice){
		if (_socialChoice.equals(SocialChoiceType.Nash)) {
			return ChocoOptimalReplicationSolver.asInt(d, true);
		} else {
			assert _socialChoice.equals(SocialChoiceType.Leximin)
			|| _socialChoice.equals(SocialChoiceType.Utility):_socialChoice;
			return ChocoOptimalReplicationSolver.asInt(d, false);
		}
	}
	public static int asInt(final double d, final boolean log){
		if (log) {
			return (int) Math.log(100*(d+0.01));
		} else {
			return (int) (100 * (d+0.01));
		}
	}

	public static void main(final String[] args) throws IfailedException, CompetenceException, NotEnoughMachinesException{
		final ReplicationExperimentationParameters p = ReplicationExperimentationParameters.getDefaultParameters();
		final ReplicationLaborantin rl = new ReplicationLaborantin(p, null);
		p.initiateParameters();
		final ChocoOptimalReplicationSolver ros = new ChocoOptimalReplicationSolver(p);
		ros.solve();
	}
}
