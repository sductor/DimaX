package frameworks.faulttolerance.solver;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import dima.basicagentcomponents.AgentIdentifier;
import dima.introspectionbasedagents.kernel.CompetentComponent;
import dima.introspectionbasedagents.modules.faults.Assert;
import dima.introspectionbasedagents.services.BasicAgentModule;
import frameworks.faulttolerance.experimentation.ReplicationGraph;
import frameworks.faulttolerance.negotiatingagent.HostState;
import frameworks.faulttolerance.negotiatingagent.ReplicaState;
import frameworks.faulttolerance.negotiatingagent.ReplicationSocialOptimisation;
import frameworks.negotiation.contracts.ResourceIdentifier;
import frameworks.negotiation.exploration.Solver.UnsatisfiableException;
import frameworks.negotiation.rationality.SocialChoiceFunction.SocialChoiceType;

public abstract class RessourceAllocationProblem<SolutionType> extends BasicAgentModule<CompetentComponent>{

	/**
	 * 
	 */
	private static final long serialVersionUID = -1032674010472045848L;
	protected final SocialChoiceType socialChoice;
	protected final boolean isAgent;
	protected final boolean isHost;
	/******                    *******/
	public int n;
	public int m;
	private AgentIdentifier[] agId;
	private ResourceIdentifier[] hostId;
	protected Map<AgentIdentifier,Integer> reverseAgId;
	protected Map<ResourceIdentifier,Integer> reverseHostId;
	private double[] agCrit;
	private double[] hostLambda;
	private double[] agProcCharge;
	private double[] agMemCharge;
	private double[] hostProcMax;
	private double[] hostMemMax;
	private double[] agInitLambda;
	private double[] hostInitProcCharge;
	private double[] hostInitMemCharge;

	public double[] currentCharges=null;
	public double[] currentRepNumber=null;

	private double hostChargeTotal;
	private double agentChargeTotal;
	private double agMeanCriticality;


	public ReplicationGraph rig;


	protected Collection<AgentIdentifier> fixedVar = new ArrayList<AgentIdentifier>();
	protected SolutionType initialSolution;
	HostState myState;

	//
	// Constructors
	//

	public RessourceAllocationProblem(final SocialChoiceType socialChoice,
			final boolean isAgent, final boolean isHost) {
		this.socialChoice = socialChoice;
		this.isAgent = isAgent;
		this.isHost = isHost;
	}

	//
	//
	//

	public void setProblem(final ReplicationGraph rig, final Collection<AgentIdentifier> fixedVar) {

		this.rig = rig;
		this.fixedVar=fixedVar;

		final List<ReplicaState> agentStates = new ArrayList<ReplicaState>(rig.getAgentStates());
		final List<HostState> hostsStates = new ArrayList<HostState>(rig.getHostsStates());
		this.n=agentStates.size();
		this.m=hostsStates.size();
		assert this.n>0:this.n+"\n"+rig;
		assert this.m>0;
		assert Assert.Imply(this.isLocal(), fixedVar.isEmpty());

		this.agId= new AgentIdentifier[this.n];
		this.hostId= new ResourceIdentifier[this.m] ;
		this.reverseAgId=new HashMap<AgentIdentifier, Integer>();
		this.reverseHostId=new HashMap<ResourceIdentifier, Integer>();

		this.hostChargeTotal=0;
		this.agentChargeTotal=0;

		this.agCrit= new double[this.n];
		this.hostLambda= new double[this.m];

		this.agProcCharge= new double[this.n];
		this.agMemCharge= new double[this.n];
		this.hostProcMax= new double[this.m];
		this.hostMemMax= new double[this.m];
		if (this.isLocal()){
			this.agInitLambda= new double[this.n];
			this.hostInitProcCharge= new double[this.m];
			this.hostInitMemCharge= new double[this.m];
		}

		for (int i = 0; i < this.n; i++){
			assert agentStates.get(i)!=null;
			this.agId[i]=agentStates.get(i).getMyAgentIdentifier();
			this.reverseAgId.put(agentStates.get(i).getMyAgentIdentifier(), i);
			this.agCrit[i]=agentStates.get(i).getMyCriticity();
			this.agMeanCriticality+=this.agCrit[i];
			this.agProcCharge[i]=agentStates.get(i).getMyProcCharge();
			this.agMemCharge[i]=agentStates.get(i).getMyMemCharge();
			this.agentChargeTotal+=Math.max(this.agMemCharge[i], this.agProcCharge[i]);
			if (this.isLocal()){
				assert this.m==1;
				final ReplicaState neOS=agentStates.get(i).allocate(this.myState, false);
				this.agInitLambda[i]=neOS.getMyFailureProb();
			}
		}
		this.agMeanCriticality=this.agMeanCriticality/this.n;

		for (int j = 0; j < this.m; j++){
			this.hostId[j]=hostsStates.get(j).getMyAgentIdentifier();
			this.reverseHostId.put(hostsStates.get(j).getMyAgentIdentifier(), j);
			this.hostLambda[j]=hostsStates.get(j).getFailureProb();
			this.hostProcMax[j]=hostsStates.get(j).getProcChargeMax();
			this.hostMemMax[j]=hostsStates.get(j).getMemChargeMax();

			this.hostChargeTotal+=Math.max(this.hostProcMax[j],this.hostMemMax[j] );
			if (this.isLocal()){
				assert this.m==1;
				HostState neoS=hostsStates.get(j);
				for (final ReplicaState rep : agentStates){
					neoS = neoS.allocate(rep,false);
				}
				this.hostInitMemCharge[j]=neoS.getCurrentMemCharge();
				this.hostInitProcCharge[j]=neoS.getCurrentProcCharge();
				this.hostChargeTotal-=Math.max(this.hostInitProcCharge[j],this.hostInitMemCharge[j] );
			}
		}
		//		averageNumberOfAgentPerHost=(Rep)
		//		assert this.socialChoice==rig.getSocialWelfare();
		//		System.out.println(hosts);
		//		System.out.println(agents);
		assert  this.boundValidity();
	}

	protected boolean boundValidity(){
		try {
			for (int i = 0; i < this.n; i++) {
				for (int j = 0; j < this.m; j++){
					final AgentIdentifier agentIdentifier = this.getAgentIdentifier(i);
					final ResourceIdentifier hostIdentifier = this.getHostIdentifier(j);
					assert this.getAllocationLowerBound(i, j)<=this.getAllocationUpperBound(i, j);

					assert Assert.IIF(
							this.rig.getAccessibleAgents(hostIdentifier).contains(agentIdentifier),
							this.rig.getAccessibleAgents(hostIdentifier).contains(agentIdentifier));
					if (this.rig.getAccessibleAgents(hostIdentifier).contains(agentIdentifier)){
						assert Assert.IIF(
								this.fixedVar.contains(agentIdentifier) || this.fixedVar.contains(hostIdentifier),
								this.getAllocationLowerBound(i, j)==this.getAllocationUpperBound(i, j));
						assert Assert.IIF(
								this.rig.getAgentState(agentIdentifier).hasResource(hostIdentifier),
								this.rig.getHostState(hostIdentifier).hasResource(agentIdentifier)):
									""+this.rig.getAgentState(agentIdentifier)+this.rig.getHostState(hostIdentifier);
								assert Assert.Imply(
										this.rig.getAgentState(agentIdentifier).hasResource(hostIdentifier),
										this.getAllocationUpperBound(i, j)==1);
								assert Assert.Imply(
										!this.rig.getAgentState(agentIdentifier).hasResource(hostIdentifier),
										this.getAllocationLowerBound(i, j)==0);
					} else {
						assert this.getAllocationLowerBound(i, j)==this.getAllocationUpperBound(i, j) && this.getAllocationUpperBound(i, j)==0;
					}
				}
			}	} catch (final UnsatisfiableException e) {
				throw new RuntimeException(e);
			}
		return true;
	}
	//
	// Position handling
	//


	private Map<Integer,Integer> variablePos = null;
	//	protected int getPos(int agent_i, int host_j) {
	//		return agent_i*m+host_j;
	//	}

	public void setConstantHandling() throws UnsatisfiableException {
		this.variablePos= new HashMap<Integer, Integer>();

		int currentVariablePos = 0;
		for (int i = 0; i < this.n; i++){
			for (int j = 0; j < this.m; j++){

				assert Assert.IIF(this.getAllocationLowerBound(i, j)==this.getAllocationUpperBound(i, j),
						this.fixedVar.contains(this.getAgentIdentifier(i)) ||
						this.fixedVar.contains(this.getHostIdentifier(j)) ||
						!this.rig.getAccessibleHosts(this.getAgentIdentifier(i)).contains(this.getHostIdentifier(j)));

				assert Assert.Imply(
						this.getAllocationLowerBound(i, j)==this.getAllocationUpperBound(i, j) && this.getAllocationUpperBound(i, j)==1,
						this.rig.getAgentState(this.getAgentIdentifier(i)).hasResource(this.getHostIdentifier(j))):
							this.getAgentIdentifier(i)+" "+this.getHostIdentifier(j)+" "+this.isLocal()
							+" "+this.fixedVar.contains(this.getAgentIdentifier(i))+" "+this.fixedVar.contains(this.getHostIdentifier(j))+" "+
							this.getAllocationLowerBound(i, j)+" "+this.getAllocationUpperBound(i, j)+" "+
							this.rig.getAgentState(this.getAgentIdentifier(i)).hasResource(this.getHostIdentifier(j))
							+" "+this.rig.getHostState(this.getHostIdentifier(j)).hasResource(this.getAgentIdentifier(i));


						if(this.getAllocationLowerBound(i, j)!=this.getAllocationUpperBound(i, j)){
							final int posIJ = this.getVectorNotation(i, j);
							this.variablePos.put(posIJ, currentVariablePos);
							currentVariablePos++;
						}
			}
		}
	}

	private int getVectorNotation(final int i, final int j) {
		final int posIJ=i*this.m+j;
		return posIJ;
	}

	protected boolean isConstant(final int agent_i, final int host_j){
		assert Assert.IIF(this.getPos(agent_i, host_j)==-1,this.variablePos!=null && !this.variablePos.containsKey(this.getVectorNotation(agent_i, host_j)));
		return this.variablePos!=null && !this.variablePos.containsKey(this.getVectorNotation(agent_i, host_j));
	}

	protected int getPos(final int agent_i, final int host_j) {
		final int posIJ = this.getVectorNotation(agent_i, host_j);
		if (this.variablePos==null){
			return posIJ;
		}else {
			if (this.variablePos.containsKey(posIJ)) {
				return this.variablePos.get(posIJ);
			} else {
				return -1;
			}
		}
	}

	protected int getVariableNumber() {
		if (this.variablePos!=null) {
			return this.variablePos.size();
		} else {
			return this.n*this.m;
		}
	}

	protected int getConstraintNumber() {
		int numConstraint=0;
		if (this.isAgent) {
			numConstraint+=this.n;
		}
		if (this.isHost) {
			numConstraint+=2*this.m;
		}
		if (this.isLocal()) {
			numConstraint++;
		}
		return numConstraint;
	}

	//
	// Variable reading
	//

	protected abstract SolutionType getInitialAllocAsSolution(double[] intialAlloc);


	protected abstract double readVariable(SolutionType var,  int varPos);

	protected final double read(final SolutionType var,  final int agent_i, final int host_j){
		final int varPos = this.getPos(agent_i, host_j);
		assert Assert.Imply(this.fixedVar.contains(this.getAgentIdentifier(agent_i)) || this.fixedVar.contains(this.getHostIdentifier(host_j)), this.getPos(agent_i, host_j)==-1);
		if (varPos!=-1) {
			return this.readVariable(var, varPos);
		} else {
			try {
				assert this.getAllocationLowerBound(agent_i, host_j)==this.getAllocationUpperBound(agent_i, host_j):
					"("+agent_i+","+host_j+") "+"("+this.getAgentIdentifier(agent_i)+","+this.getHostIdentifier(host_j)+")\n"
					+this.getAllocationLowerBound(agent_i, host_j)+" "+this.getAllocationUpperBound(agent_i, host_j)+" "+this.getPos(agent_i, host_j)
					+"\n"+this.variablePos;
				assert Assert.Imply(
						this.rig.getAgentState(this.getAgentIdentifier(agent_i)).hasResource(this.getHostIdentifier(host_j)),
						this.getAllocationLowerBound(agent_i, host_j)==1);
				assert Assert.Imply(
						!this.rig.getAgentState(this.getAgentIdentifier(agent_i)).hasResource(this.getHostIdentifier(host_j)),
						this.getAllocationUpperBound(agent_i, host_j)==0);
				return  this.getAllocationLowerBound(agent_i, host_j);
			} catch (final UnsatisfiableException e) {
				throw new RuntimeException("impossible");
			}
		}
	}




	//
	// Accessors
	//

	protected AgentIdentifier getAgentIdentifier(final int i) {
		return this.agId[i];
	}

	protected ResourceIdentifier getHostIdentifier(final int j) {
		return this.hostId[j];
	}

	protected boolean isLocal() {
		assert Assert.Imply(!this.isAgent && this.isHost,this.m==1):this.isAgent +" "+this.isHost+" "+this.m;
		return !this.isAgent && this.isHost;
	}

	/*
	 * Variables
	 */

	public void updateCurrentCharges(final SolutionType daX){
		if (this.currentCharges==null) {
			this.currentCharges=new double[this.m];
		}
		for (int j = 0; j < this.m; j++){
			this.currentCharges[j]=this.getHostCurrentCharge(daX, j);
		}
	}

	public void updateCurrentReplicasNumber(final SolutionType solution) {

		if (this.currentRepNumber==null) {
			this.currentRepNumber=new double[this.n];
		}
		for (int i = 0; i < this.n; i++){
			for (int j = 0; j < this.m; j++){
				this.currentRepNumber[i]+=this.read(solution, i, j);
			}

		}

	}

	public double getHostsChargeTotal() {
		return this.hostChargeTotal;
	}

	public double getAgentsChargeTotal() {
		return this.agentChargeTotal;
	}
	protected double getAgentMeanCriticality() {
		return this.agMeanCriticality;
	}
	protected double getAgentCriticality(final int i) {
		return this.agCrit[i];
	}

	protected double getHostFailureProbability(final int j) {
		return this.hostLambda[j];
	}

	protected double getAgentMemorycharge(final int i) {
		return this.agMemCharge[i];
	}

	protected double getAgentProcessorCharge(final int i) {
		return this.agProcCharge[i];
	}

	protected double getHostMaxMemory(final int j) {
		return this.hostMemMax[j];
	}

	protected double getHostMaxProcessor(final int j) {
		return this.hostProcMax[j];
	}

	protected double getHostMaxCharge(final int j) {
		return Math.max(this.getHostMaxProcessor(j), this.getHostMaxMemory(j)) ;
	}

	protected double getHostAvailableCharge(final int j) {
		return Math.max(this.getHostMaxProcessor(j)-this.getHostInitialProcessor(j), this.getHostMaxMemory(j)-this.getHostInitialMemory(j)) ;
	}

	protected double getHostInitialMemory(final int j) {
		if (!this.isLocal()) {
			return 0.;
		} else {
			return this.hostInitMemCharge[j];
		}
	}

	protected double getHostInitialProcessor(final int j) {
		if (!this.isLocal()) {
			return 0.;
		} else {
			return this.hostInitProcCharge[j];
		}
	}

	protected double getAgentInitialFailureProbability(final int i) {
		if (!this.isLocal()) {
			return 1.;
		} else {
			return this.agInitLambda[i];
		}
	}

	private int getFixedBound(final int agent_i, final int host_j)
			throws UnsatisfiableException{
		final AgentIdentifier agentIdentifier = this.getAgentIdentifier(agent_i);
		final ResourceIdentifier hostIdentifier = this.getHostIdentifier(host_j);
		assert this.fixedVar.contains(agentIdentifier) || this.fixedVar.contains(hostIdentifier);
		assert this.rig.getAccessibleAgents(hostIdentifier).contains(agentIdentifier);
		assert this.rig.getAccessibleHosts(agentIdentifier).contains(hostIdentifier);
		if (this.fixedVar.contains(agentIdentifier) && this.fixedVar.contains(hostIdentifier)){
			if (this.rig.getHostState(hostIdentifier).hasResource(agentIdentifier)!=
					this.rig.getAgentState(agentIdentifier).hasResource(hostIdentifier)){
				throw new UnsatisfiableException(agentIdentifier+" "+hostIdentifier+"\n"+this.rig);
			} else {
				return this.rig.getHostState(hostIdentifier).hasResource(agentIdentifier)?1:0;
			}
		} else if (this.fixedVar.contains(agentIdentifier)){
			return this.rig.getAgentState(agentIdentifier).hasResource(hostIdentifier)?1:0;
		} else {//
			assert this.fixedVar.contains(hostIdentifier);
			return this.rig.getHostState(hostIdentifier).hasResource(agentIdentifier)?1:0;
		}
	}

	protected int getAllocationLowerBound(final int agent_i, final int host_j)
			throws UnsatisfiableException {
		final AgentIdentifier agentIdentifier = this.getAgentIdentifier(agent_i);
		final ResourceIdentifier hostIdentifier = this.getHostIdentifier(host_j);

		final Collection<ResourceIdentifier> hostAccessibletoAgent_i = this.rig.getAccessibleHosts(agentIdentifier);
		final Collection<AgentIdentifier> agentsAccessibleToHost_j = this.rig.getAccessibleAgents(hostIdentifier);
		assert agentsAccessibleToHost_j.contains(agentIdentifier)==hostAccessibletoAgent_i.contains(hostIdentifier);
		if (this.isLocal()){
			assert hostAccessibletoAgent_i.contains(hostIdentifier)
			&& agentsAccessibleToHost_j.contains(agentIdentifier);
			return 0;
		} else if (!agentsAccessibleToHost_j.contains(agentIdentifier)){
			assert !hostAccessibletoAgent_i.contains(hostIdentifier);
			return 0;
		} else if (!this.fixedVar.contains(agentIdentifier) && !this.fixedVar.contains(hostIdentifier)){
			return 0;
		} else {
			return this.getFixedBound(agent_i, host_j);
		}
	}

	protected int getAllocationUpperBound(final int agent_i, final int host_j)
			throws UnsatisfiableException {
		final AgentIdentifier agentIdentifier = this.getAgentIdentifier(agent_i);
		final ResourceIdentifier hostIdentifier = this.getHostIdentifier(host_j);

		final Collection<ResourceIdentifier> hostAccessibletoAgent_i = this.rig.getAccessibleHosts(agentIdentifier);
		final Collection<AgentIdentifier> agentsAccessibleToHost_j = this.rig.getAccessibleAgents(hostIdentifier);

		if (this.isLocal()){
			assert hostAccessibletoAgent_i.contains(hostIdentifier)
			&& agentsAccessibleToHost_j.contains(agentIdentifier);
			return 1;
		} else if (!agentsAccessibleToHost_j.contains(agentIdentifier)){
			assert !hostAccessibletoAgent_i.contains(hostIdentifier);
			return 0;
		} else if (!this.fixedVar.contains(agentIdentifier) && !this.fixedVar.contains(hostIdentifier)){
			return 1;
		} else {
			return this.getFixedBound(agent_i, host_j);
		}
	}


	/*
	 * Objectives
	 */

	protected double getSocWelfare(final SolutionType daX) {
		//objective
		double f;
		switch (this.socialChoice){
		case Utility :
			f = 0;
			break;
		case Nash :
			f=1;
			break;
		case Leximin :
			f=Double.MAX_VALUE;
			break;
		default :
			throw new RuntimeException();
		}

		for (int agent_i = 0; agent_i < this.n; agent_i++){
			final double relia=this.getIndividualWelfare(daX, agent_i);
			switch (this.socialChoice){
			case Utility :
				f += relia;
				break;
			case Nash :
				f *= relia;
				break;
			case Leximin :
				f =Math.min(f, relia);
				break;
			default :
				throw new RuntimeException();
			}
		}
		return f;
	}

	public Collection<AgentIdentifier> getRessources(final SolutionType daX, final AgentIdentifier id) {
		final Collection<AgentIdentifier> result = new ArrayList<AgentIdentifier>();
		if (id instanceof ResourceIdentifier){
			final int h = this.reverseHostId.get(id);

			for (int ag = 0; ag < this.n; ag++){
				if (this.read(daX, ag, h)==1.0){
					result.add(this.agId[ag]);
				} else {
					assert this.read(daX, ag, h)==0.;
				}
			}
		} else {
			final int ag = this.reverseAgId.get(id);

			for (int h = 0; h < this.m; h++){
				if (this.read(daX, ag, h)==1.0){
					assert this.hostId!=null;
					result.add(this.hostId[h]);
				} else {
					assert this.read(daX, ag, h)==0.;
				}
			}
		}
		return result;
	}

	protected double getDispo(final SolutionType daX, final int agent_i) {
		if (this.fixedVar.contains(this.getAgentIdentifier(agent_i))){
			return this.rig.getAgentState(this.getAgentIdentifier(agent_i)).getMyDisponibility();
		} else {
			double failProb = this.getAgentInitialFailureProbability(agent_i);
			for (int host_j = 0; host_j < this.m; host_j++){
				//					assert daX[getPos(agent_i,host_j)]==1. || daX[getPos(agent_i,host_j)]==0.:daX[getPos(agent_i,host_j)];
				failProb *= Math.pow(
						this.getHostFailureProbability(host_j),
						this.read(daX,agent_i,host_j));
			}
			return 1 - failProb;
		}
	}

	protected double getIndividualWelfare(final SolutionType daX, final int agent_i) {
		return ReplicationSocialOptimisation.getReliability(this.getDispo(daX, agent_i), this.getAgentCriticality(agent_i), this.socialChoice);
	}

	/*
	 * Constraints
	 */
	//
	//	protected double getAgentSurvie(SolutionType daX, int agent_i) {
	//		double result=0;
	//		for (int host_j=0; host_j < m; host_j++){
	//			result+=read(daX,agent_i,host_j);
	//			//			assert result<=m:result+" "+m+" "+print(daX);
	//		}
	//		return result;
	//	}

	protected double getHostMemoryCharge(final SolutionType daX, final int host_j) {
		if (this.fixedVar.contains(this.getHostIdentifier(host_j))){
			return this.rig.getHostState(this.getHostIdentifier(host_j)).getCurrentMemCharge();
		} else {
			double c=this.getHostInitialMemory(host_j);
			for (int agent_i=0; agent_i < this.n; agent_i++){
				//			assert daX[getPos(agent_i,host_j)]==1. || daX[getPos(agent_i,host_j)]==0.:daX[getPos(agent_i,host_j)];
				c+=this.read(daX,agent_i,host_j)*this.getAgentMemorycharge(agent_i);
			}
			return c;
		}
	}

	protected double getHostProcessorCharge(final SolutionType daX, final int host_j) {
		if (this.fixedVar.contains(this.getHostIdentifier(host_j))){
			return this.rig.getHostState(this.getHostIdentifier(host_j)).getCurrentProcCharge();
		} else {
			double c=this.getHostInitialProcessor(host_j);
			for (int agent_i=0; agent_i < this.n; agent_i++){
				//			assert daX[getPos(agent_i,host_j)]==1. || daX[getPos(agent_i,host_j)]==0.:daX[getPos(agent_i,host_j)];
				c+=this.read(daX,agent_i,host_j)*this.getAgentProcessorCharge(agent_i);
			}
			return c;
		}
	}

	protected double getHostCurrentCharge(final SolutionType daX, final int host_j) {
		return Math.max(this.getHostMemoryCharge(daX,host_j),this.getHostMemoryCharge(daX,host_j));
	}

	protected boolean isViableForAgent(final SolutionType daX, final int agent_i) {
		return !this.isAgent || this.getDispo(daX, agent_i)>0;
	}

	protected boolean isViableForhost(final SolutionType daX, final int host_j) {
		return !this.isHost || this.getHostMemoryCharge(daX, host_j)<=this.getHostMaxMemory(host_j) && this.getHostProcessorCharge(daX, host_j)<=this.getHostMaxProcessor(host_j);
	}

	protected boolean assertIsViable(final SolutionType daX) {
		for (int i = 0; i < this.n; i++){
			assert this.isViableForAgent(daX, i):
				this.getAgentIdentifier(i)+" :\n "+this.getDispo(daX, i)+" "+this.getAgentInitialFailureProbability(i)+"\n fixed var "+
				this.fixedVar+"\n bounds "+this.printBounds()+"\n "+this.print(daX)+"\n "+this.getRessources(daX, this.getAgentIdentifier(i));
		}
		for (int j = 0; j < this.m; j++){
			assert this.isViableForhost(daX, j):
				this.getHostIdentifier(j)+" "+this.getHostMemoryCharge(daX, j)+" "+this.getHostProcessorCharge(daX, j)+"\n fixed var "+
				this.fixedVar+this.getRessources(daX,this.getHostIdentifier(j))+"\n  "+this.print(daX)+"";;
		}

		for (int i = 0; i < this.n; i++){
			for (int j = 0; j < this.m; j++){
				assert this.read(daX,i,j)==1.0 || this.read(daX,i,j)==0.0;
			}
		}
		assert !this.isLocal() || this.isUpgrading(daX);
		return true;
	}


	protected boolean isViable(final SolutionType daX) {
		for (int i = 0; i < this.n; i++){
			if (!this.isViableForAgent(daX, i)){
				return false;
			}
		}
		for (int j = 0; j < this.m; j++){
			if (!this.isViableForhost(daX, j)){
				return false;
			}
		}

		for (int i = 0; i < this.n; i++){
			for (int j = 0; j < this.m; j++){
				if(!(this.read(daX,i,j)==1.0 || this.read(daX,i,j)==0.0)) {
					return false;
				}
			}
		}

		if (!(!this.isLocal() || this.isUpgrading(daX))){
			return false;
		}

		return true;
	}

	protected boolean isUpgrading(final SolutionType solution) {
		return this.getSocWelfare(solution)>=this.getSocWelfare(this.initialSolution);
	}


	/*
	 * Derivative
	 */


	protected double getDRondSocWelfare(final SolutionType daX, final int agent_i, final int host_j) {
		double r;
		if (this.socialChoice.equals(SocialChoiceType.Utility)){
			r=-Math.log(this.getHostFailureProbability(host_j))*this.getIndividualWelfare(daX,agent_i);
		}  else if (this.socialChoice.equals(SocialChoiceType.Nash)) {
			r=-Math.log(this.getHostFailureProbability(host_j));
			for (int j = 0; j < this.m; j++){
				r *= Math.pow(
						this.getHostFailureProbability(j),
						this.read(daX,agent_i,j));
			}
			for (int i = 0; i < this.n; i++){
				if (i!=agent_i){
					r*=this.getIndividualWelfare(daX,i);
				}
			}
		}  else {
			assert this.socialChoice.equals(SocialChoiceType.Leximin);
			throw new RuntimeException();
		}
		return r;
	}

	protected double getDRondDeuxSocWelfare(final SolutionType daX, final int agent_i,
			final int host_j, final int agent_ip, final int host_jp) {
		double r;
		if (this.socialChoice.equals(SocialChoiceType.Utility)){
			assert agent_i==agent_ip;
			r=-Math.log(this.getHostFailureProbability(host_jp))*Math.log(this.getHostFailureProbability(host_j))*this.getIndividualWelfare(daX,agent_i);
		}  else if (this.socialChoice.equals(SocialChoiceType.Nash)) {
			r=-Math.log(this.getHostFailureProbability(host_j));
			for (int j = 0; j < this.m; j++){
				r *= Math.pow(
						this.getHostFailureProbability(j),
						this.read(daX,agent_i,j));
			}
			r*=-Math.log(this.getHostFailureProbability(host_jp));
			for (int j = 0; j < this.m; j++){
				r *= Math.pow(
						this.getHostFailureProbability(j),
						this.read(daX,agent_ip,j));
			}
			for (int i = 0; i < this.n; i++){
				if (i!=agent_i && i!=agent_ip){
					r*=this.getIndividualWelfare(daX,i);
				}
			}

		}  else {
			assert this.socialChoice.equals(SocialChoiceType.Leximin);
			throw new RuntimeException();
		}
		return r;
	}

	protected double getDRondSurvie(final int consideredAgent, final int agent_i, final int host_j) {
		assert consideredAgent==agent_i;
		if (consideredAgent==agent_i) {
			return 1;
		} else {
			return 0;
		}
	}

	protected double getDRondMemory(final int consideredHost, final int agent_i, final int host_j) {
		assert consideredHost==host_j;
		if (consideredHost==host_j) {
			return this.getAgentMemorycharge(agent_i);
		} else {
			return 0;
		}
	}

	protected double getDRondProcessor(final int consideredHost, final int agent_i, final int host_j) {
		assert consideredHost==host_j;
		if (consideredHost==host_j) {
			return this.getAgentProcessorCharge(agent_i);
		} else {
			return 0;
		}
	}


	/******                    *******/

	protected double[] getBestTriviaSol(final List<ReplicaState> best, final double hostCap) {
		final List<ReplicaState> agsToSort = new ArrayList<ReplicaState>(this.rig.getAgentStates());
		final List<ReplicaState> agentStates = new ArrayList<ReplicaState>(this.rig.getAgentStates());
		final Comparator<ReplicaState> c = new Comparator<ReplicaState>() {

			@Override
			public int compare(final ReplicaState o1, final ReplicaState o2) {
				return o1.getMyCriticity().compareTo(o2.getMyCriticity());
			}
		};
		Collections.sort(agsToSort, Collections.reverseOrder(c));
		//		System.out.println(ags);
		for (int i =0; i < hostCap; i++){
			best.add(agsToSort.get(i));
		}

		final double[] bestPossible = new double[this.n*this.m];
		for (int i = 0; i < this.n*this.m; i++){
			if (best.contains(agentStates.get(i))) {
				bestPossible[i]=1.;
			} else {
				bestPossible[i]=0.;
			}
		}
		return bestPossible;
	}


	/******                    *******/
	//
	// Affichage
	//
	public static String asMatrix(final double[] vect, final int nbCol) {
		String result ="[ ";
		for (int i = 0; i < vect.length; i++){
			if (i%nbCol==0) {
				result+="\n ";
			}
			result+=vect[i]+" ";
		}
		return result;
	}

	public String printBounds(){

		try {
			String result ="\n[ "+Arrays.asList(this.agId)+"]\n";
			for (int j = 0; j < this.m; j++){
				result+="HOST "+this.getHostIdentifier(j)+" :  ";
				for (int i = 0; i < this.n; i++){
					result+="("+this.getAllocationLowerBound(i, j)+","+this.getAllocationUpperBound(i, j)+")"+" \t ";
				}
				result+="\n";
			}
			result+="]";
			return result;
		} catch (final UnsatisfiableException e) {
			throw new RuntimeException();
		}
	}

	public String print(final SolutionType vect) {
		String result ="\n[ "+Arrays.asList(this.agId)+"]\n";
		for (int j = 0; j < this.m; j++){
			result+="HOST "+this.getHostIdentifier(j)+" :  ";
			for (int i = 0; i < this.n; i++){
				result+=this.read(vect, i,j)+" \t ";
			}
			result+="\n";
		}
		result+="]";
		return result;
	}

	public static String asMatrix(final ArrayList vect, final int nbCol) {
		String result ="[ ";
		for (int i = 0; i < vect.size(); i++){
			if (i%nbCol==0) {
				result+="\n ";
			}
			result+=vect.get(i)+" ";
		}
		return result;
	}

	public static String print(final double[] vect) {
		String result ="[ ";
		for (final double element : vect) {
			result+=element+" ";
		}
		result+="]";
		return result;
	}

	public static String print(final ReplicaState[] vect) {
		String result ="[ ";
		for (final ReplicaState element : vect) {
			result+=element.getMyAgentIdentifier()+" ";
		}
		result+="]";
		return result;
	}


}