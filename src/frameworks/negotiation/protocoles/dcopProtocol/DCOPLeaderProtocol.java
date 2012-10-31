package frameworks.negotiation.protocoles.dcopProtocol;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import dima.basicagentcomponents.AgentIdentifier;
import dima.introspectionbasedagents.annotations.ProactivityInitialisation;
import dima.introspectionbasedagents.annotations.StepComposant;
import dima.introspectionbasedagents.modules.mappedcollections.HashedHashSet;
import dima.introspectionbasedagents.services.UnrespectedCompetenceSyntaxException;
import dima.introspectionbasedagents.services.loggingactivity.LogException;
import dima.introspectionbasedagents.services.loggingactivity.LogService;
import frameworks.faulttolerance.experimentation.ReplicationInstanceGraph;
import frameworks.negotiation.contracts.AbstractContractTransition.IncompleteContractException;
import frameworks.negotiation.contracts.ContractTrunk;
import frameworks.negotiation.contracts.MatchingCandidature;
import frameworks.negotiation.contracts.ResourceIdentifier;
import frameworks.negotiation.exploration.CombinaisonIterator;
import frameworks.negotiation.exploration.ResourceAllocationSolver;
import frameworks.negotiation.exploration.Solver.ExceedLimitException;
import frameworks.negotiation.exploration.Solver.UnsatisfiableException;
import frameworks.negotiation.rationality.AgentState;

public class DCOPLeaderProtocol<
State extends AgentState, 
Contract extends MatchingCandidature> 
extends DcopAgentProtocol<State, Contract>{

	public static String dcopProtocol="key for dcop protocol log";

	//
	// Fields
	//
	final long maxComputingTime;
	//Map fringe node to k- group their are fringe of and this agent is leader
	//
	
	public final int maxWainttime=100;
	public int waitTime=0;

	//local view of the leader
	ReplicationInstanceGraph localView = new ReplicationInstanceGraph(null);
	//the local view structure has changed
	boolean graphChanged=true;
	final int numberOfSimulateonuslyOptimizedRig;
	CombinaisonIterator<AgentIdentifier> kSizeGroups;
	//all the node that the leader can modify
	Collection<AgentIdentifier> neighberhood = new HashSet<AgentIdentifier>();
	LinkedList<ReplicationInstanceGraph> currentlyOptimizedRig=new LinkedList<ReplicationInstanceGraph>();

	/*
	 * flags trigering actions
	 */
	//some of the fringe nodes of these groups has changed their value
	Set<ReplicationInstanceGraph> changeFlag=
			new HashSet<ReplicationInstanceGraph>();
	//a better allocation has been found for those groups
	Set<ReplicationInstanceGraph> gainFlag=
			new HashSet<ReplicationInstanceGraph>();
	//those groups has been locked
	Set<ReplicationInstanceGraph> lockedRigs=
			new HashSet<ReplicationInstanceGraph>();
	/*
	 * info
	 */
	// the diferent group and their respectiv fringe nodes
	HashedHashSet<AgentIdentifier, ReplicationInstanceGraph> fringeNodes2ring=
			new HashedHashSet<AgentIdentifier, ReplicationInstanceGraph>();
	HashedHashSet<ReplicationInstanceGraph, AgentIdentifier> rig2fringeNodes=
			new HashedHashSet<ReplicationInstanceGraph, AgentIdentifier>();
	//reallocation the leader want to apply
	HashedHashSet<ReplicationInstanceGraph,Contract> gainContracts=
			new HashedHashSet<ReplicationInstanceGraph, Contract>();
	//	//all the locked nodes and the group their belongs to : needed for cancelling global request after one reject
	HashedHashSet<Contract,ReplicationInstanceGraph> lockedNodesToRig=
			new HashedHashSet<Contract,ReplicationInstanceGraph>();



	final ResourceAllocationSolver<Contract, State> solver;

	//
	// Constructor
	//

	public DCOPLeaderProtocol(int k, int t,
			ResourceAllocationSolver<Contract, State> solver,
			final long maxComputingTime)
					throws UnrespectedCompetenceSyntaxException {
		super(k);
		this.numberOfSimulateonuslyOptimizedRig=t;
		this.solver = solver;
		this.solver.setMyAgent(this);
		this.maxComputingTime=maxComputingTime;
	}


	//
	// Behavior
	//

	@ProactivityInitialisation
	public void initView(){
		//		logMonologue("init");
		localView.setAgentState(getMyAgent().getMyCurrentState());
		for (AgentIdentifier id : getMyAgent().getKnownResources()){
			localView.addAcquaintance(getIdentifier(), id);		
		}
		neighberhood.add(getIdentifier());
	}

	@Override
	public void beInformed(DcopValueMessage<State> m){
		//update
		if (m instanceof DcopConstraintsMessage){
			//			if (m.getVariable() instanceof ResourceIdentifier)logMonologue("receiving  constraint message of "+m.getVariable());
			DcopConstraintsMessage<State> constraintM = (DcopConstraintsMessage<State> )m;
			assert constraintM.getMyState()!=null;
			localView.setAgentState(constraintM.getMyState());
			if (m.mustBeForwarded()){//It belongs to my k-distance neighberhood
				if (neighberhood.add(constraintM.getVariable())){
					logMonologue("grapChanged",DCOPLeaderProtocol.dcopProtocol);						
					for (AgentIdentifier id : constraintM.getMyAcquaintances()){
						localView.addAcquaintance(constraintM.getVariable(), id);
					}
				}
			}
		} else if (m instanceof DcopValueMessage){ 
			//						logMonologue("receiving new value message of "+m.getVariable(),DCOPLeaderProtocol.dcopProtocol);
			//trigger new computation for fringe variable
			if (fringeNodes2ring.containsKey(m.getVariable()) && 
					(localView.getAgentsIdentifier().contains(m.getVariable()) && !localView.getAgentState(m.getVariable()).equals(((DcopValueMessage<State>)m).getMyState()))
					|| !localView.getAgentsIdentifier().contains(m.getVariable())){
				changeFlag.addAll(fringeNodes2ring.get(m.getVariable()));
			}
			localView.setAgentState(((DcopValueMessage<State>)m).getMyState());
		}
		//forward
		super.beInformed(m);
	}

	public boolean iveFullInfo(Collection<AgentIdentifier> group){
		for (AgentIdentifier id : group){
			//			logMonologue(group+"\n"+localView);
			for (AgentIdentifier ac : localView.getAcquaintances(id))
				if (localView.getState(ac)==null)
					return false;
		}
		return true;
	}


	@StepComposant
	public void computeLeaderShip(){
		//		logMonologue("computing leader ship"+localView.getHostsIdentifier());
		if ((graphChanged || (currentlyOptimizedRig.size()<numberOfSimulateonuslyOptimizedRig))){// && iveFullInfo()){
			assert localViewCheck(); 
			if (kSizeGroups==null || !kSizeGroups.hasNext())kSizeGroups=new CombinaisonIterator<AgentIdentifier>(new ArrayList(neighberhood), k);
			while (kSizeGroups.hasNext()){
				Collection<AgentIdentifier> group = kSizeGroups.next();
				//				if ( iveFullInfo(group) ) logWarning("yeah!");
				//				logMonologue("analysing "+group);
				if ( iveFullInfo(group) && iMLeader(group)){
					//					logWarning("double    yeeaaaaaaaaaa yeah!");
					logMonologue("i'm leader of "+group,DCOPLeaderProtocol.dcopProtocol);
					//Initialisation du rig
					assert localView.assertValidity();
					ReplicationInstanceGraph neoRig = new ReplicationInstanceGraph(null);
					for (AgentIdentifier id : group){
						neoRig.setAgentState(localView.getState(id));
						for (AgentIdentifier r : localView.getAcquaintances(id)){
							neoRig.addAcquaintance(id, r);
							neoRig.setAgentState(localView.getState(r));
							for (AgentIdentifier rp : localView.getAcquaintances(r)){
								if (neoRig.getEveryIdentifier().contains(rp)){
									//								if (group.contains(rp)){
									neoRig.addAcquaintance(r, rp);
								}
							}
						}
					}
					try {
						assert neoRig.assertValidity():localView+"\n----------------------------------\n"+neoRig;
					} catch (AssertionError e){
						logWarning(group+"\n"+localView+"\n----------------------------------\n"+neoRig,e);
					}
					//Calcul des fringe	
					Collection<AgentIdentifier> fringeNodes = new HashSet<AgentIdentifier>();
					fringeNodes.addAll(neoRig.getAgentsIdentifier());
					fringeNodes.addAll(neoRig.getHostsIdentifier());
					fringeNodes.removeAll(group);
					for (AgentIdentifier f : fringeNodes){
						this.fringeNodes2ring.add(f,neoRig);
						this.rig2fringeNodes.add(neoRig,f);
					}
					//					Collection<AgentIdentifier> fringeNodes = new HashSet<AgentIdentifier>();
					//					fringeNodes.addAll(neoRig.getAgentsIdentifier());
					//					fringeNodes.addAll(neoRig.getHostsIdentifier());
					//					for (AgentIdentifier id : neoRig.getAgentsIdentifier()){
					//						fringeNodes.removeAll(neoRig.getAccessibleHosts(id));
					//					}
					//					for (ResourceIdentifier id : neoRig.getHostsIdentifier()){
					//						fringeNodes.removeAll(neoRig.getAccessibleAgents(id));
					//					}
					//					for (AgentIdentifier f : fringeNodes){
					//						this.fringeNodes.add(f,neoRig);
					//					}
					//update
					changeFlag.add(neoRig);
					currentlyOptimizedRig.add(neoRig);
					graphChanged=false;
					break;
				} else {					
					//					logMonologue("no ! leader is  ");
				}
			}
		}
	}

	public boolean localViewCheck(){
		Collection<AgentIdentifier> allKnownStates = new ArrayList<AgentIdentifier>();
		allKnownStates.addAll(localView.getAgentsIdentifier());
		allKnownStates.addAll(localView.getHostsIdentifier());
		assert allKnownStates.containsAll(neighberhood);
		return true;
	}
	//		@StepComposant(ticker=1000)
	//		public void sayAlive2() {
	//			logWarning("I'M STILL ALIVE",LogService.onScreen);
	//		}
	private boolean iMLeader(Collection<AgentIdentifier> group) {
		assert getIdentifier()instanceof ResourceIdentifier;
		int nbHost=0;
		boolean hasAgent=false;
		Integer max=Integer.MIN_VALUE;
		Integer min=Integer.MAX_VALUE;		

		for (AgentIdentifier id : group){
			if (id instanceof ResourceIdentifier){
				max=Math.max(max, ReplicationInstanceGraph.identifierToInt(id));
				min=Math.min(min, ReplicationInstanceGraph.identifierToInt(id));
				nbHost++;
			} else {
				hasAgent=true;
			}
		}
		//		logMonologue("imLeader of "+group+"?\n"+" ---> nbHost="+nbHost+", min = "+min+", max="+max+", me="+ReplicationInstanceGraph.identifierToInt(getIdentifier()));
		if (nbHost==0 || !hasAgent){
			assert min==Integer.MAX_VALUE && max==Integer.MIN_VALUE;	
			//le groupe n'a pas d'hote ou d'agent : pas de réallocation possible
			return false;
		} else if (group.contains(getIdentifier())){
			assert ReplicationInstanceGraph.identifierToInt(getIdentifier())<=max && ReplicationInstanceGraph.identifierToInt(getIdentifier())>=min;
			if (nbHost%2==0){
				return ReplicationInstanceGraph.identifierToInt(getIdentifier()).equals(max);
			} else {
				return ReplicationInstanceGraph.identifierToInt(getIdentifier()).equals(min);
			}
		} else {
			return false;
		}
	}

	@StepComposant
	public void computeNewGain(){
		if (!changeFlag.isEmpty()){
			for (ReplicationInstanceGraph rig : changeFlag){
				assert rig.assertValidity();
				solver.setProblem(rig, rig2fringeNodes.get(rig));
				solver.setTimeLimit((int) maxComputingTime);
				try {
					//					logMonologue("compution new allocation "+rig.getEveryIdentifier());// for "+rig+"\n fringe are "+rig2fringeNodes.get(rig));
					Set<Contract> opt =  new HashSet(solver.getBestLocalSolution());
					//					logMonologue("new allo computed");// for "+rig+"\n fringe are "+rig2fringeNodes.get(rig));
					Collection<Contract> fusedContract= gainContracts.getAllValues();
					for (Contract c : fusedContract){
						try {
							if (opt.contains(c)){
								opt.remove(c);
								opt.add(c);
							}
						} catch (AssertionError e){//l'assertion assurant qu'il n'y a pas deux contrat identique en circulation
							opt.remove(c);
							opt.add(c);
						}
					}
					Collection<Contract> courant = gainContracts.containsKey(rig)?gainContracts.get(rig):new ArrayList<Contract>();
					assert opt!=null;
					assert courant!=null;
					if (getMyAgent().getMyAllocationPreferenceComparator().compare(opt, courant)>0){
						gainContracts.put(rig,opt);
						gainFlag.add(rig);
						logMonologue("new allocation  find!!!!!!!!!!!!!!! =D ",DCOPLeaderProtocol.dcopProtocol);
					}
				} 
				catch (UnsatisfiableException e) {/*do nothing*/
					logMonologue("unsatisfiable >=[");} 
				catch (ExceedLimitException e) {/*do nothing*/
					logMonologue("time limits! >=[");}

				//								logMonologue("no allocation  find!!!!!!!!!!!!!!! =(");
			}
		}
		changeFlag.clear();
	}

	/*
	 * Selection Core
	 */

	/*
	 * Protocol
	 */


	@Override
	protected boolean ImAllowedToNegotiate(ContractTrunk<Contract> contracts) {
		return true;
	}

	@Override
	protected void answerAccepted(Collection<Contract> toAccept) {
		Collection<Contract> confirm = new ArrayList<Contract>();
		Collection<Contract> acceptLock = new ArrayList<Contract>();
		for (Contract c : toAccept){
			if (c.getInitiator().equals(getMyAgent().getIdentifier())){
				confirm.add(c);
				myLock.get(c.getInitiator()).remove(c.getContractIdentifier());
				try {
					localView.setAgentState(c.computeResultingState(localView.getAgentState(c.getAgent())));
					localView.setAgentState(c.computeResultingState(localView.getHostState(c.getResource())));
				} catch (IncompleteContractException e) {
					throw new RuntimeException("impossible");
				}
			} else {
				acceptLock.add(c);
				//				myLock.add(c.getInitiator(),c);
			}
		}
		confirmContract(confirm, Receivers.NotInitiatingParticipant);
		acceptContract(acceptLock, Receivers.Initiator);
	}

	@Override
	protected void answerRejected(Collection<Contract> toReject) {
		Collection<Contract> cancel = new ArrayList<Contract>();
		Collection<Contract> refuseLock = new ArrayList<Contract>();
		for (Contract c : toReject){
			if (c.getInitiator().equals(getMyAgent().getIdentifier())){
				cancel.add(c);
				myLock.remove(c.getInitiator(),c);
			}
			else
				refuseLock.add(c);
		}
		cancelContract(cancel, Receivers.NotInitiatingParticipant);
		rejectContract(refuseLock, Receivers.Initiator);
	}

}
