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
import frameworks.faulttolerance.negotiatingagent.ReplicationCandidature;
import frameworks.faulttolerance.negotiatingagent.ReplicationSocialOptimisation;
import frameworks.faulttolerance.olddcop.DcopSolver;
import frameworks.faulttolerance.solver.jmetal.core.Solution;
import frameworks.negotiation.contracts.ResourceIdentifier;
import frameworks.negotiation.exploration.ResourceAllocationSolver;
import frameworks.negotiation.exploration.Solver;
import frameworks.negotiation.exploration.Solver.UnsatisfiableException;
import frameworks.negotiation.rationality.SocialChoiceFunction.SocialChoiceType;

public abstract class RessourceAllocationProblem<SolutionType> extends BasicAgentModule<CompetentComponent>{

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

	public RessourceAllocationProblem(SocialChoiceType socialChoice,
			boolean isAgent, boolean isHost) {
		this.socialChoice = socialChoice;
		this.isAgent = isAgent;
		this.isHost = isHost;
	}

	//
	//
	//

	public void setProblem(ReplicationGraph rig, Collection<AgentIdentifier> fixedVar) {

		this.rig = rig;
		this.fixedVar=fixedVar;

		List<ReplicaState> agentStates = new ArrayList<ReplicaState>(rig.getAgentStates());
		List<HostState> hostsStates = new ArrayList<HostState>(rig.getHostsStates());
		n=agentStates.size();
		m=hostsStates.size();
		assert n>0;
		assert m>0;
		assert Assert.Imply(isLocal(), fixedVar.isEmpty());

		agId= new AgentIdentifier[n];
		hostId= new ResourceIdentifier[m] ;
		reverseAgId=new HashMap<AgentIdentifier, Integer>();
		reverseHostId=new HashMap<ResourceIdentifier, Integer>();

		hostChargeTotal=0;
		agentChargeTotal=0;

		agCrit= new double[n];
		hostLambda= new double[m];

		agProcCharge= new double[n];
		agMemCharge= new double[n];
		hostProcMax= new double[m];
		hostMemMax= new double[m];
		if (isLocal()){
			agInitLambda= new double[n];
			hostInitProcCharge= new double[m];
			hostInitMemCharge= new double[m];
		}

		for (int i = 0; i < n; i++){
			assert agentStates.get(i)!=null;
			agId[i]=agentStates.get(i).getMyAgentIdentifier();
			reverseAgId.put(agentStates.get(i).getMyAgentIdentifier(), i);
			agCrit[i]=agentStates.get(i).getMyCriticity();
			agMeanCriticality+=agCrit[i];
			agProcCharge[i]=agentStates.get(i).getMyProcCharge();
			agMemCharge[i]=agentStates.get(i).getMyMemCharge();
			agentChargeTotal+=Math.max(agMemCharge[i], agProcCharge[i]);
			if (isLocal()){
				assert m==1;
				ReplicaState neOS=agentStates.get(i).allocate(myState, false);
				agInitLambda[i]=neOS.getMyFailureProb();
			}			
		}
		agMeanCriticality=agMeanCriticality/n;

		for (int j = 0; j < m; j++){
			hostId[j]=hostsStates.get(j).getMyAgentIdentifier();
			reverseHostId.put(hostsStates.get(j).getMyAgentIdentifier(), j);
			hostLambda[j]=hostsStates.get(j).getFailureProb();
			hostProcMax[j]=hostsStates.get(j).getProcChargeMax();
			hostMemMax[j]=hostsStates.get(j).getMemChargeMax();

			hostChargeTotal+=Math.max(hostProcMax[j],hostMemMax[j] );
			if (isLocal()){
				assert m==1;
				HostState neoS=hostsStates.get(j);
				for (ReplicaState rep : agentStates){
					neoS = neoS.allocate(rep,false);
				}
				hostInitMemCharge[j]=neoS.getCurrentMemCharge();
				hostInitProcCharge[j]=neoS.getCurrentProcCharge();
				hostChargeTotal-=Math.max(hostInitProcCharge[j],hostInitMemCharge[j] );				
			}			
		}
		//		averageNumberOfAgentPerHost=(Rep)
		//		assert this.socialChoice==rig.getSocialWelfare();
		//		System.out.println(hosts);
		//		System.out.println(agents);
		assert  boundValidity();
	}

	protected boolean boundValidity(){
		try {
			for (int i = 0; i < n; i++)
				for (int j = 0; j < m; j++){
					AgentIdentifier agentIdentifier = getAgentIdentifier(i);
					ResourceIdentifier hostIdentifier = getHostIdentifier(j);
					assert getAllocationLowerBound(i, j)<=getAllocationUpperBound(i, j);

					assert Assert.IIF(
							rig.getAccessibleAgents(hostIdentifier).contains(agentIdentifier), 
							rig.getAccessibleAgents(hostIdentifier).contains(agentIdentifier));
					if (rig.getAccessibleAgents(hostIdentifier).contains(agentIdentifier)){
						assert Assert.IIF(
								fixedVar.contains(agentIdentifier) || fixedVar.contains(hostIdentifier), 
								getAllocationLowerBound(i, j)==getAllocationUpperBound(i, j));
						assert Assert.IIF(
								rig.getAgentState(agentIdentifier).hasResource(hostIdentifier), 
								rig.getHostState(hostIdentifier).hasResource(agentIdentifier));
						assert Assert.Imply(
								rig.getAgentState(agentIdentifier).hasResource(hostIdentifier), 
								getAllocationUpperBound(i, j)==1);
						assert Assert.Imply(
								!rig.getAgentState(agentIdentifier).hasResource(hostIdentifier), 
								getAllocationLowerBound(i, j)==0);
					} else {
						assert getAllocationLowerBound(i, j)==getAllocationUpperBound(i, j) && getAllocationUpperBound(i, j)==0;
					}
				}	} catch (UnsatisfiableException e) {
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
		variablePos= new HashMap<Integer, Integer>();

		int currentVariablePos = 0;
		for (int i = 0; i < n; i++){
			for (int j = 0; j < m; j++){

				assert Assert.IIF(getAllocationLowerBound(i, j)==getAllocationUpperBound(i, j), 
						fixedVar.contains(getAgentIdentifier(i)) || 
						fixedVar.contains(getHostIdentifier(j)) || 
						!rig.getAccessibleHosts(getAgentIdentifier(i)).contains(getHostIdentifier(j)));

				assert Assert.Imply(
						getAllocationLowerBound(i, j)==getAllocationUpperBound(i, j) && getAllocationUpperBound(i, j)==1,
						rig.getAgentState(getAgentIdentifier(i)).hasResource(getHostIdentifier(j))):
							getAgentIdentifier(i)+" "+getHostIdentifier(j)+" "+isLocal()
							+" "+fixedVar.contains(getAgentIdentifier(i))+" "+fixedVar.contains(getHostIdentifier(j))+" "+
							getAllocationLowerBound(i, j)+" "+getAllocationUpperBound(i, j)+" "+
							rig.getAgentState(getAgentIdentifier(i)).hasResource(getHostIdentifier(j))
							+" "+rig.getHostState(getHostIdentifier(j)).hasResource(getAgentIdentifier(i));


						if((getAllocationLowerBound(i, j)!=getAllocationUpperBound(i, j))){
							int posIJ = getVectorNotation(i, j);
							variablePos.put(posIJ, currentVariablePos);
							currentVariablePos++;
						}
			}
		}
	}

	private int getVectorNotation(int i, int j) {
		int posIJ=i*m+j;
		return posIJ;
	}

	protected boolean isConstant(int agent_i, int host_j){
		assert Assert.IIF(getPos(agent_i, host_j)==-1,variablePos!=null && !variablePos.containsKey(getVectorNotation(agent_i, host_j)));
		return variablePos!=null && !variablePos.containsKey(getVectorNotation(agent_i, host_j));
	}

	protected int getPos(int agent_i, int host_j) {
		int posIJ = getVectorNotation(agent_i, host_j);
		if (variablePos==null){
			return posIJ;			
		}else {
			if (variablePos.containsKey(posIJ))
				return variablePos.get(posIJ);
			else 
				return -1;
		}
	}

	protected int getVariableNumber() {
		if (variablePos!=null)
			return variablePos.size();
		else
			return n*m;
	}

	protected int getConstraintNumber() {
		int numConstraint=0;
		if (isAgent)
			numConstraint+=n;
		if (isHost)
			numConstraint+=2*m;
		if (isLocal())
			numConstraint++;
		return numConstraint;
	}

	//
	// Variable reading
	//

	protected abstract SolutionType getInitialAllocAsSolution(double[] intialAlloc);


	protected abstract double readVariable(SolutionType var,  int varPos);

	protected final double read(SolutionType var,  int agent_i, int host_j){
		int varPos = getPos(agent_i, host_j);
		assert Assert.Imply(fixedVar.contains(getAgentIdentifier(agent_i)) || fixedVar.contains(getHostIdentifier(host_j)), getPos(agent_i, host_j)==-1); 
		if (varPos!=-1)//une variable
			return readVariable(var, varPos);
		else //une constante
			try {
				assert getAllocationLowerBound(agent_i, host_j)==getAllocationUpperBound(agent_i, host_j):
					"("+agent_i+","+host_j+") "+"("+getAgentIdentifier(agent_i)+","+getHostIdentifier(host_j)+")\n"
					+getAllocationLowerBound(agent_i, host_j)+" "+getAllocationUpperBound(agent_i, host_j)+" "+getPos(agent_i, host_j)
					+"\n"+variablePos;
				assert Assert.Imply(
						rig.getAgentState(getAgentIdentifier(agent_i)).hasResource(getHostIdentifier(host_j)), 
						getAllocationLowerBound(agent_i, host_j)==1);
				assert Assert.Imply(
						!rig.getAgentState(getAgentIdentifier(agent_i)).hasResource(getHostIdentifier(host_j)), 
						getAllocationUpperBound(agent_i, host_j)==0);
				return  getAllocationLowerBound(agent_i, host_j);
			} catch (UnsatisfiableException e) {
				throw new RuntimeException("impossible");
			}
	}




	//
	// Accessors
	//

	protected AgentIdentifier getAgentIdentifier(int i) {
		return agId[i];
	}

	protected ResourceIdentifier getHostIdentifier(int j) {
		return hostId[j];
	}

	protected boolean isLocal() {
		assert Assert.Imply(!isAgent && isHost,m==1):isAgent +" "+isHost+" "+m;
		return !isAgent && isHost;
	}

	/*
	 * Variables
	 */

	public void updateCurrentCharges(SolutionType daX){
		if (currentCharges==null)
			currentCharges=new double[m];
		for (int j = 0; j < m; j++){
			currentCharges[j]=getHostCurrentCharge(daX, j);
		}
	}

	public void updateCurrentReplicasNumber(SolutionType solution) {

		if (currentRepNumber==null)
			currentRepNumber=new double[n];
		for (int i = 0; i < n; i++){
			for (int j = 0; j < m; j++){
				currentRepNumber[i]+=read(solution, i, j);
			}

		}

	}

	public double getHostsChargeTotal() {
		return hostChargeTotal;
	}

	public double getAgentsChargeTotal() {
		return agentChargeTotal;
	}
	protected double getAgentMeanCriticality() {
		return agMeanCriticality;
	}
	protected double getAgentCriticality(int i) {
		return agCrit[i];
	}

	protected double getHostFailureProbability(int j) {
		return hostLambda[j];
	}

	protected double getAgentMemorycharge(int i) {
		return agMemCharge[i];
	}

	protected double getAgentProcessorCharge(int i) {
		return agProcCharge[i];
	}

	protected double getHostMaxMemory(int j) {
		return hostMemMax[j];
	}

	protected double getHostMaxProcessor(int j) {
		return hostProcMax[j];
	}

	protected double getHostMaxCharge(int j) {
		return Math.max(getHostMaxProcessor(j), getHostMaxMemory(j)) ;
	}

	protected double getHostAvailableCharge(int j) {
		return Math.max(getHostMaxProcessor(j)-getHostInitialProcessor(j), getHostMaxMemory(j)-getHostInitialMemory(j)) ;
	}

	protected double getHostInitialMemory(int j) {
		if (!isLocal())
			return 0.;
		else
			return hostInitMemCharge[j];
	}

	protected double getHostInitialProcessor(int j) {
		if (!isLocal())
			return 0.;
		else
			return hostInitProcCharge[j];
	}

	protected double getAgentInitialFailureProbability(int i) {
		if (!isLocal())
			return 1.;
		else
			return agInitLambda[i];
	}

	private int getFixedBound(int agent_i, int host_j)
			throws UnsatisfiableException{
		AgentIdentifier agentIdentifier = getAgentIdentifier(agent_i);
		ResourceIdentifier hostIdentifier = getHostIdentifier(host_j);
		assert fixedVar.contains(agentIdentifier) || fixedVar.contains(hostIdentifier);
		assert rig.getAccessibleAgents(hostIdentifier).contains(agentIdentifier);
		assert rig.getAccessibleHosts(agentIdentifier).contains(hostIdentifier);
		if (fixedVar.contains(agentIdentifier) && fixedVar.contains(hostIdentifier)){
			if (rig.getHostState(hostIdentifier).hasResource(agentIdentifier)!=
					rig.getAgentState(agentIdentifier).hasResource(hostIdentifier)){
				throw new UnsatisfiableException(agentIdentifier+" "+hostIdentifier+"\n"+rig);
			} else {
				return rig.getHostState(hostIdentifier).hasResource(agentIdentifier)?1:0;
			}
		} else if (fixedVar.contains(agentIdentifier)){
			return rig.getAgentState(agentIdentifier).hasResource(hostIdentifier)?1:0;
		} else {//	
			assert fixedVar.contains(hostIdentifier);
			return rig.getHostState(hostIdentifier).hasResource(agentIdentifier)?1:0;
		}
	}

	protected int getAllocationLowerBound(int agent_i, int host_j)
			throws UnsatisfiableException {
		AgentIdentifier agentIdentifier = getAgentIdentifier(agent_i);
		ResourceIdentifier hostIdentifier = getHostIdentifier(host_j);

		Collection<ResourceIdentifier> hostAccessibletoAgent_i = rig.getAccessibleHosts(agentIdentifier);
		Collection<AgentIdentifier> agentsAccessibleToHost_j = rig.getAccessibleAgents(hostIdentifier);
		assert agentsAccessibleToHost_j.contains(agentIdentifier)==hostAccessibletoAgent_i.contains(hostIdentifier);
		if (isLocal()){
			assert hostAccessibletoAgent_i.contains(hostIdentifier)
			&& agentsAccessibleToHost_j.contains(agentIdentifier);
			return 0;
		} else if (!agentsAccessibleToHost_j.contains(agentIdentifier)){
			assert !hostAccessibletoAgent_i.contains(hostIdentifier);
			return 0;	
		} else if (!fixedVar.contains(agentIdentifier) && !fixedVar.contains(hostIdentifier)){
			return 0;		
		} else {
			return getFixedBound(agent_i, host_j);
		}
	}

	protected int getAllocationUpperBound(int agent_i, int host_j)
			throws UnsatisfiableException {
		AgentIdentifier agentIdentifier = getAgentIdentifier(agent_i);
		ResourceIdentifier hostIdentifier = getHostIdentifier(host_j);

		Collection<ResourceIdentifier> hostAccessibletoAgent_i = rig.getAccessibleHosts(agentIdentifier);
		Collection<AgentIdentifier> agentsAccessibleToHost_j = rig.getAccessibleAgents(hostIdentifier);

		if (isLocal()){
			assert hostAccessibletoAgent_i.contains(hostIdentifier)
			&& agentsAccessibleToHost_j.contains(agentIdentifier);
			return 1;
		} else if (!agentsAccessibleToHost_j.contains(agentIdentifier)){
			assert !hostAccessibletoAgent_i.contains(hostIdentifier);
			return 0;	
		} else if (!fixedVar.contains(agentIdentifier) && !fixedVar.contains(hostIdentifier)){
			return 1;		
		} else {
			return getFixedBound(agent_i, host_j);
		}
	}


	/*
	 * Objectives
	 */

	protected double getSocWelfare(SolutionType daX) {
		//objective
		double f;
		switch (socialChoice){
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

		for (int agent_i = 0; agent_i < n; agent_i++){
			double relia=getIndividualWelfare(daX, agent_i);
			switch (socialChoice){
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

	public Collection<AgentIdentifier> getRessources(SolutionType daX, AgentIdentifier id) {
		Collection<AgentIdentifier> result = new ArrayList<AgentIdentifier>();
		if (id instanceof ResourceIdentifier){
			int h = reverseHostId.get((ResourceIdentifier)id);

			for (int ag = 0; ag < n; ag++){
				if (read(daX, ag, h)==1.0){
					result.add(agId[ag]);
				} else {
					assert read(daX, ag, h)==0.;
				}
			}
		} else {
			int ag = reverseAgId.get(id);

			for (int h = 0; h < m; h++){
				if (read(daX, ag, h)==1.0){
					assert hostId!=null;
					result.add(hostId[h]);
				} else {
					assert read(daX, ag, h)==0.;
				}
			}
		}
		return result;
	}

	protected double getDispo(SolutionType daX, int agent_i) {
		double failProb = getAgentInitialFailureProbability(agent_i);
		for (int host_j = 0; host_j < m; host_j++){
			//					assert daX[getPos(agent_i,host_j)]==1. || daX[getPos(agent_i,host_j)]==0.:daX[getPos(agent_i,host_j)];
			failProb *= Math.pow(
					getHostFailureProbability(host_j),
					read(daX,agent_i,host_j));	
		}	
		return 1 - failProb;
	}

	protected double getIndividualWelfare(SolutionType daX, int agent_i) {
		return ReplicationSocialOptimisation.getReliability(getDispo(daX, agent_i), getAgentCriticality(agent_i), socialChoice);
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

	protected double getHostMemoryCharge(SolutionType daX, int host_j) {
		double c=getHostInitialMemory(host_j);
		for (int agent_i=0; agent_i < n; agent_i++){
			//			assert daX[getPos(agent_i,host_j)]==1. || daX[getPos(agent_i,host_j)]==0.:daX[getPos(agent_i,host_j)];
			c+=read(daX,agent_i,host_j)*getAgentMemorycharge(agent_i);
		}
		return c;
	}

	protected double getHostProcessorCharge(SolutionType daX, int host_j) {
		double c=getHostInitialProcessor(host_j);
		for (int agent_i=0; agent_i < n; agent_i++){
			//			assert daX[getPos(agent_i,host_j)]==1. || daX[getPos(agent_i,host_j)]==0.:daX[getPos(agent_i,host_j)];
			c+=read(daX,agent_i,host_j)*getAgentProcessorCharge(agent_i);
		}
		return c;
	}

	protected double getHostCurrentCharge(SolutionType daX, int host_j) {
		return Math.max(getHostMemoryCharge(daX,host_j),getHostMemoryCharge(daX,host_j));
	}

	protected boolean isViableForAgent(SolutionType daX, int agent_i) {
		return !isAgent || getDispo(daX, agent_i)>0;
	}

	protected boolean isViableForhost(SolutionType daX, int host_j) {
		return !isHost || (getHostMemoryCharge(daX, host_j)<=getHostMaxMemory(host_j) && getHostProcessorCharge(daX, host_j)<=getHostMaxProcessor(host_j));
	}

	protected boolean assertIsViable(SolutionType daX) {
		for (int i = 0; i < n; i++){
			assert isViableForAgent(daX, i):
				getAgentIdentifier(i)+" :\n "+getDispo(daX, i)+" "+getAgentInitialFailureProbability(i)+"\n fixed var "+
				fixedVar+"\n bounds "+printBounds()+"\n "+print(daX)+"\n "+getRessources(daX, getAgentIdentifier(i));
		}
		for (int j = 0; j < m; j++){
			assert isViableForhost(daX, j): getHostIdentifier(j)+" "+getHostMemoryCharge(daX, j)+" "+getHostProcessorCharge(daX, j)+"\n fixed var "+
					fixedVar+getRessources(daX,getHostIdentifier(j))+"\n  "+print(daX)+"";;
		}

		for (int i = 0; i < n; i++){
			for (int j = 0; j < m; j++){
				assert (read(daX,i,j)==1.0 || read(daX,i,j)==0.0);
			}
		}
		return !isLocal() || isUpgrading(daX);
	}


	protected boolean isViable(SolutionType daX) {
		for (int i = 0; i < n; i++){
			if (!isViableForAgent(daX, i)){
				return false;
			}
		}
		for (int j = 0; j < m; j++){
			if (!isViableForhost(daX, j)){
				return false;
			}
		}

		for (int i = 0; i < n; i++){
			for (int j = 0; j < m; j++){
				if(!(read(daX,i,j)==1.0 || read(daX,i,j)==0.0))
					return false;
			}
		}
		return !isLocal() || isUpgrading(daX);
	}

	protected boolean isUpgrading(SolutionType solution) {
		return getSocWelfare(solution)>=getSocWelfare(initialSolution);
	}


	/*
	 * Derivative
	 */


	protected double getDRondSocWelfare(SolutionType daX, int agent_i, int host_j) {
		double r;
		if (socialChoice.equals(SocialChoiceType.Utility)){
			r=-Math.log(getHostFailureProbability(host_j))*getIndividualWelfare(daX,agent_i);
		}  else if (socialChoice.equals(SocialChoiceType.Nash)) {
			r=-Math.log(getHostFailureProbability(host_j));
			for (int j = 0; j < m; j++){
				r *= Math.pow(
						getHostFailureProbability(j),
						read(daX,agent_i,j));
			}
			for (int i = 0; i < n; i++){
				if (i!=agent_i){
					r*=getIndividualWelfare(daX,i);
				}
			}
		}  else {
			assert (socialChoice.equals(SocialChoiceType.Leximin));
			throw new RuntimeException();
		}
		return r;
	}

	protected double getDRondDeuxSocWelfare(SolutionType daX, int agent_i,
			int host_j, int agent_ip, int host_jp) {
		double r;
		if (socialChoice.equals(SocialChoiceType.Utility)){
			assert agent_i==agent_ip;
			r=-Math.log(getHostFailureProbability(host_jp))*Math.log(getHostFailureProbability(host_j))*getIndividualWelfare(daX,agent_i);
		}  else if (socialChoice.equals(SocialChoiceType.Nash)) {
			r=-Math.log(getHostFailureProbability(host_j));
			for (int j = 0; j < m; j++){
				r *= Math.pow(
						getHostFailureProbability(j),
						read(daX,agent_i,j));
			}			
			r*=-Math.log(getHostFailureProbability(host_jp));
			for (int j = 0; j < m; j++){
				r *= Math.pow(
						getHostFailureProbability(j),
						read(daX,agent_ip,j));
			}
			for (int i = 0; i < n; i++){
				if (i!=agent_i && i!=agent_ip){
					r*=getIndividualWelfare(daX,i);
				}
			}

		}  else {
			assert (socialChoice.equals(SocialChoiceType.Leximin));
			throw new RuntimeException();
		}
		return r;
	}

	protected double getDRondSurvie(int consideredAgent, int agent_i, int host_j) {
		assert consideredAgent==agent_i;
		if (consideredAgent==agent_i)
			return 1;
		else
			return 0;
	}

	protected double getDRondMemory(int consideredHost, int agent_i, int host_j) {
		assert consideredHost==host_j;
		if (consideredHost==host_j)
			return getAgentMemorycharge(agent_i);
		else
			return 0;
	}

	protected double getDRondProcessor(int consideredHost, int agent_i, int host_j) {
		assert consideredHost==host_j;
		if (consideredHost==host_j)
			return getAgentProcessorCharge(agent_i);
		else
			return 0;
	}


	/******                    *******/

	protected double[] getBestTriviaSol(List<ReplicaState> best, double hostCap) {
		List<ReplicaState> agsToSort = new ArrayList<ReplicaState>(rig.getAgentStates());
		List<ReplicaState> agentStates = new ArrayList<ReplicaState>(rig.getAgentStates());
		Comparator<ReplicaState> c = new Comparator<ReplicaState>() {

			@Override
			public int compare(ReplicaState o1, ReplicaState o2) {
				return o1.getMyCriticity().compareTo(o2.getMyCriticity());
			}
		}; 
		Collections.sort(agsToSort, Collections.reverseOrder(c));
		//		System.out.println(ags);
		for (int i =0; i < hostCap; i++){
			best.add(agsToSort.get(i));
		}

		double[] bestPossible = new double[n*m];
		for (int i = 0; i < n*m; i++){
			if (best.contains(agentStates.get(i)))
				bestPossible[i]=1.;
			else
				bestPossible[i]=0.;
		}
		return bestPossible;
	}


	/******                    *******/
	//
	// Affichage
	//
	public static String asMatrix(double[] vect, int nbCol) {
		String result ="[ ";
		for (int i = 0; i < vect.length; i++){
			if (i%nbCol==0)
				result+="\n ";
			result+=vect[i]+" ";
		}
		return result;
	}

	public String printBounds(){

		try {
			String result ="\n[ "+Arrays.asList(agId)+"]\n";
			for (int j = 0; j < m; j++){
				result+="HOST "+getHostIdentifier(j)+" :  ";
				for (int i = 0; i < n; i++){
					result+="("+getAllocationLowerBound(i, j)+","+getAllocationUpperBound(i, j)+")"+" \t ";
				}
				result+="\n";
			}
			result+="]";
			return result;
		} catch (UnsatisfiableException e) {
			throw new RuntimeException();
		}
	}

	public String print(SolutionType vect) {
		String result ="\n[ "+Arrays.asList(agId)+"]\n";
		for (int j = 0; j < m; j++){
			result+="HOST "+getHostIdentifier(j)+" :  ";
			for (int i = 0; i < n; i++){
				result+=read(vect, i,j)+" \t ";
			}
			result+="\n";
		}
		result+="]";
		return result;
	}

	public static String asMatrix(ArrayList vect, int nbCol) {
		String result ="[ ";
		for (int i = 0; i < vect.size(); i++){
			if (i%nbCol==0)
				result+="\n ";
			result+=vect.get(i)+" ";
		}
		return result;
	}

	public static String print(double[] vect) {
		String result ="[ ";
		for (int i = 0; i < vect.length; i++){
			result+=vect[i]+" ";
		}
		result+="]";
		return result;
	}

	public static String print(ReplicaState[] vect) {
		String result ="[ ";
		for (int i = 0; i < vect.length; i++){
			result+=vect[i].getMyAgentIdentifier()+" ";
		}
		result+="]";
		return result;
	}


}