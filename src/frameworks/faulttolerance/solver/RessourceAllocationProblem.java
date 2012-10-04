package frameworks.faulttolerance.solver;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import dima.basicagentcomponents.AgentIdentifier;
import dima.introspectionbasedagents.modules.faults.Assert;
import frameworks.faulttolerance.dcop.DcopSolver;
import frameworks.faulttolerance.experimentation.ReplicationGraph;
import frameworks.faulttolerance.negotiatingagent.HostState;
import frameworks.faulttolerance.negotiatingagent.ReplicaState;
import frameworks.faulttolerance.negotiatingagent.ReplicationCandidature;
import frameworks.faulttolerance.negotiatingagent.ReplicationSocialOptimisation;
import frameworks.negotiation.contracts.ResourceIdentifier;
import frameworks.negotiation.exploration.ResourceAllocationSolver;
import frameworks.negotiation.exploration.Solver;
import frameworks.negotiation.exploration.Solver.UnsatisfiableException;
import frameworks.negotiation.rationality.SocialChoiceFunction.SocialChoiceType;

public abstract class RessourceAllocationProblem<SolutionType>{

	protected final SocialChoiceType socialChoice;
	protected final boolean isAgent;
	protected final boolean isHost;
	/******                    *******/
	protected int n;
	protected int m;
	private AgentIdentifier[] agId;
	private ResourceIdentifier[] hostId;
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

	private double hostChargeTotal;
	private double agentChargeTotal;



	protected ReplicationGraph rig;


	protected Collection<AgentIdentifier> fixedVar = new ArrayList<AgentIdentifier>();
	protected SolutionType intialSolution;
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

	public void setProblem(ReplicationGraph rig) {

		this.rig = rig;

		List<ReplicaState> agentStates = new ArrayList<ReplicaState>(rig.getAgentStates());
		List<HostState> hostsStates = new ArrayList<HostState>(rig.getHostsStates());
		n=agentStates.size();
		m=hostsStates.size();
		assert n>0;
		assert m>0;
		assert Assert.Imply(isLocal(), fixedVar.isEmpty());

		agId= new AgentIdentifier[n];
		hostId= new ResourceIdentifier[m] ;


		hostChargeTotal=0;
		agentChargeTotal=0;

		agCrit= new double[n];
		hostLambda= new double[n];

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
			agCrit[i]=agentStates.get(i).getMyCriticity();
			agProcCharge[i]=agentStates.get(i).getMyProcCharge();
			agMemCharge[i]=agentStates.get(i).getMyMemCharge();
			agentChargeTotal+=Math.max(agMemCharge[i], agProcCharge[i]);
			if (isLocal()){
				assert m==1;
				ReplicaState neOS=agentStates.get(i).allocate(myState, false);
				agInitLambda[i]=neOS.getMyFailureProb();
			}			
		}

		for (int j = 0; j < m; j++){
			hostId[j]=hostsStates.get(j).getMyAgentIdentifier();
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

				int posIJ=i*m+j;

				if(!(getAllocationLowerBound(i, j)==getAllocationUpperBound(i, j))){
					variablePos.put(posIJ, currentVariablePos);
					currentVariablePos++;
				}
			}
		}
	}

	protected int getPos(int agent_i, int host_j) {
		int posIJ = agent_i*m+host_j;
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


	protected abstract double read(SolutionType var,  int agent_i, int host_j);

	private double readVariable(SolutionType var,  int agent_i, int host_j){
		if (variablePos==null || variablePos.containsKey(getPos(agent_i, host_j))){
			return read(var, agent_i, host_j);
		}else {
			try {
				assert getAllocationLowerBound(agent_i, host_j)==getAllocationUpperBound(agent_i, host_j);
				return  getAllocationLowerBound(agent_i, host_j);
			} catch (UnsatisfiableException e) {
				throw new RuntimeException("impossible");
			}
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


	public double getHostsChargeTotal() {
		return hostChargeTotal;
	}

	public double getAgentsChargeTotal() {
		return agentChargeTotal;
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

	protected int getAllocationLowerBound(int agent_i, int host_j)
			throws UnsatisfiableException {
		if (isLocal()){

			assert rig.getAccessibleHosts(getAgentIdentifier(agent_i)).contains(getHostIdentifier(host_j))
			&& rig.getAccessibleAgents(getHostIdentifier(host_j)).contains(getAgentIdentifier(agent_i));
			return 0;

		} else if (rig.getAccessibleAgents(getHostIdentifier(host_j)).contains(getAgentIdentifier(agent_i))){

			assert rig.getAccessibleHosts(getAgentIdentifier(agent_i)).contains(getHostIdentifier(host_j));

			if (!fixedVar.contains(getAgentIdentifier(agent_i)) && !fixedVar.contains(getHostIdentifier(host_j))){
				return 0;

			} else if (fixedVar.contains(getAgentIdentifier(agent_i)) && fixedVar.contains(getHostIdentifier(host_j))){
				if (
						(rig.getHostState(getHostIdentifier(host_j)).hasResource(getAgentIdentifier(agent_i))&& 
								rig.getAgentState(getAgentIdentifier(agent_i)).hasResource(getHostIdentifier(host_j)))
								||
								(!rig.getHostState(getHostIdentifier(host_j)).hasResource(getAgentIdentifier(agent_i)) && 
										!rig.getAgentState(getAgentIdentifier(agent_i)).hasResource(getHostIdentifier(host_j)))){
					throw new UnsatisfiableException();
				} else
					return rig.getHostState(getHostIdentifier(host_j)).hasResource(getAgentIdentifier(agent_i))?1:0;

			}else if (fixedVar.contains(getAgentIdentifier(agent_i))){
				return rig.getAgentState(getAgentIdentifier(agent_i)).hasResource(getHostIdentifier(host_j))?1:0;

			} else {//	
				assert fixedVar.contains(getHostIdentifier(host_j));
				return rig.getHostState(getHostIdentifier(host_j)).hasResource(getAgentIdentifier(agent_i))?1:0;
			}
		} else {
			assert !rig.getAccessibleHosts(getAgentIdentifier(agent_i)).contains(getHostIdentifier(host_j));
			return 0;
		}
	}

	protected int getAllocationUpperBound(int agent_i, int host_j)
			throws UnsatisfiableException {
		if (isLocal()){

			assert rig.getAccessibleHosts(getAgentIdentifier(agent_i)).contains(getHostIdentifier(host_j))
			&& rig.getAccessibleAgents(getHostIdentifier(host_j)).contains(getAgentIdentifier(agent_i));
			return 1;

		} else if (rig.getAccessibleAgents(getHostIdentifier(host_j)).contains(getAgentIdentifier(agent_i))){

			assert rig.getAccessibleHosts(getAgentIdentifier(agent_i)).contains(getHostIdentifier(host_j));

			if (!fixedVar.contains(getAgentIdentifier(agent_i)) && !fixedVar.contains(getHostIdentifier(host_j))){
				return 1;

			} else if (fixedVar.contains(getAgentIdentifier(agent_i)) && fixedVar.contains(getHostIdentifier(host_j))){
				if (
						(rig.getHostState(getHostIdentifier(host_j)).hasResource(getAgentIdentifier(agent_i))&& 
								rig.getAgentState(getAgentIdentifier(agent_i)).hasResource(getHostIdentifier(host_j)))
								||
								(!rig.getHostState(getHostIdentifier(host_j)).hasResource(getAgentIdentifier(agent_i)) && 
										!rig.getAgentState(getAgentIdentifier(agent_i)).hasResource(getHostIdentifier(host_j)))){
					throw new UnsatisfiableException();
				} else
					return rig.getHostState(getHostIdentifier(host_j)).hasResource(getAgentIdentifier(agent_i))?1:0;

			}else if (fixedVar.contains(getAgentIdentifier(agent_i))){
				return rig.getAgentState(getAgentIdentifier(agent_i)).hasResource(getHostIdentifier(host_j))?1:0;

			} else {//	
				assert fixedVar.contains(getHostIdentifier(host_j));
				return rig.getHostState(getHostIdentifier(host_j)).hasResource(getAgentIdentifier(agent_i))?1:0;
			}
		} else {
			assert !rig.getAccessibleHosts(getAgentIdentifier(agent_i)).contains(getHostIdentifier(host_j));
			return 0;
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

	protected double getDispo(SolutionType daX, int agent_i) {
		double failProb = getAgentInitialFailureProbability(agent_i);
		for (int host_j = 0; host_j < m; host_j++){
			//					assert daX[getPos(agent_i,host_j)]==1. || daX[getPos(agent_i,host_j)]==0.:daX[getPos(agent_i,host_j)];
			failProb *= Math.pow(
					getHostFailureProbability(host_j),
					readVariable(daX,agent_i,host_j));	
		}	
		return 1 - failProb;
	}

	protected double getIndividualWelfare(SolutionType daX, int agent_i) {
		return ReplicationSocialOptimisation.getReliability(getDispo(daX, agent_i), getAgentCriticality(agent_i), socialChoice);
	}

	/*
	 * Constraints 
	 */

	protected double getAgentSurvie(SolutionType daX, int agent_i) {
		double result=0;
		for (int host_j=0; host_j < m; host_j++){
			result+=readVariable(daX,agent_i,host_j);
			//			assert result<=m:result+" "+m+" "+print(daX);
		}
		return result;
	}

	protected double getHostMemoryCharge(SolutionType daX, int host_j) {
		double c=getHostInitialMemory(host_j);
		for (int agent_i=0; agent_i < n; agent_i++){
			//			assert daX[getPos(agent_i,host_j)]==1. || daX[getPos(agent_i,host_j)]==0.:daX[getPos(agent_i,host_j)];
			c+=readVariable(daX,agent_i,host_j)*getAgentMemorycharge(agent_i);
		}
		return c;
	}

	protected double getHostProcessorCharge(SolutionType daX, int host_j) {
		double c=getHostInitialProcessor(host_j);
		for (int agent_i=0; agent_i < n; agent_i++){
			//			assert daX[getPos(agent_i,host_j)]==1. || daX[getPos(agent_i,host_j)]==0.:daX[getPos(agent_i,host_j)];
			c+=readVariable(daX,agent_i,host_j)*getAgentProcessorCharge(agent_i);
		}
		return c;
	}

	protected double getHostCurrentCharge(SolutionType daX, int host_j) {
		return Math.max(getHostMemoryCharge(daX,host_j),getHostMemoryCharge(daX,host_j));
	}

	protected boolean isViableForAgent(SolutionType daX, int agent_i) {
		return !isAgent || getAgentSurvie(daX, agent_i)>0;
	}

	protected boolean isViableForhost(SolutionType daX, int host_j) {
		return !isHost || (getHostMemoryCharge(daX, host_j)<=getHostMaxMemory(host_j) && getHostProcessorCharge(daX, host_j)<=getHostMaxProcessor(host_j));
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
				if(!(readVariable(daX,i,j)==1.0 || readVariable(daX,i,j)==0.0))
					return false;
			}
		}
		return !isLocal() || isUpgrading(daX);
	}

	protected boolean isUpgrading(SolutionType solution) {
		return getSocWelfare(solution)>=getSocWelfare(intialSolution);
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
						readVariable(daX,agent_i,j));
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
						readVariable(daX,agent_i,j));
			}			
			r*=-Math.log(getHostFailureProbability(host_jp));
			for (int j = 0; j < m; j++){
				r *= Math.pow(
						getHostFailureProbability(j),
						readVariable(daX,agent_ip,j));
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


	public String print(SolutionType vect) {
		String result ="[ ";
		for (int j = 0; j < m; j++){
			result+="HOST :  ";
			for (int i = 0; i < n; i++){
				result+=readVariable(vect, i,j)+" \t ";
			}
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