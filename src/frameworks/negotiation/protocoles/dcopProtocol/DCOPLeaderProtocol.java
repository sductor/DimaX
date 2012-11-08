package frameworks.negotiation.protocoles.dcopProtocol;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
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
import frameworks.experimentation.ExperimentationParameters;
import frameworks.faulttolerance.experimentation.ReplicationInstanceGraph;
import frameworks.negotiation.contracts.AbstractContractTransition.IncompleteContractException;
import frameworks.negotiation.contracts.ContractTrunk;
import frameworks.negotiation.contracts.MatchingCandidature;
import frameworks.negotiation.contracts.ResourceIdentifier;
import frameworks.negotiation.contracts.ValuedContract;
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

	public final int maxWainttime=100000;//1000;//
	public int waitTime=0;

	//the local view structure has changed
	boolean graphChanged=true;
	final int numberOfSimulateonuslyOptimizedRig;
	CombinaisonIterator<AgentIdentifier> kSizeGroups;
	//all the node that the leader can modify
	Collection<AgentIdentifier> neighberhood = new HashSet<AgentIdentifier>();
	LinkedList<Collection<AgentIdentifier>> currentlyOptimizedRig=new LinkedList<Collection<AgentIdentifier>>();

	/*
	 * flags trigering actions
	 */
	//some of the fringe nodes of these groups has changed their value
	Set<Collection<AgentIdentifier>> changeFlag=
			new HashSet<Collection<AgentIdentifier>>();
	//a better allocation has been found for those groups
	Set<Collection<AgentIdentifier>> gainFlag=
			new HashSet<Collection<AgentIdentifier>>();
	//those groups has been locked
	Set<Collection<AgentIdentifier>> lockedRigs=
			new HashSet<Collection<AgentIdentifier>>();
	/*
	 * info
	 */
	// the diferent group and their respectiv fringe nodes
	HashedHashSet<AgentIdentifier, Collection<AgentIdentifier>> fringeNodes2ring=
			new HashedHashSet<AgentIdentifier, Collection<AgentIdentifier>>();
	HashedHashSet<Collection<AgentIdentifier>, AgentIdentifier> rig2fringeNodes=
			new HashedHashSet<Collection<AgentIdentifier>, AgentIdentifier>();
	//reallocation the leader want to apply
	HashedHashSet<Collection<AgentIdentifier>,Contract> gainContracts=
			new HashedHashSet<Collection<AgentIdentifier>, Contract>();
	//	//all the locked nodes and the group their belongs to : needed for cancelling global request after one reject
	HashedHashSet<Contract,Collection<AgentIdentifier>> lockedNodesToRig=
			new HashedHashSet<Contract,Collection<AgentIdentifier>>();



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

	LocalViewInformationService<State, Contract> getMyInformation(){
		return (LocalViewInformationService<State, Contract>) getMyAgent().getMyInformation();
	}

	//
	// Behavior
	//

	@ProactivityInitialisation
	public void initView(){
		//		logMonologue("init");
		getMyInformation().setState(getMyAgent().getMyCurrentState());
		for (AgentIdentifier id : getMyAgent().getKnownResources()){
			getMyInformation().addAcquaintance(getIdentifier(), id);		
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
			getMyInformation().setState(constraintM.getMyState());
			if (m.mustBeForwarded()){//It belongs to my k-distance neighberhood
				if (neighberhood.add(constraintM.getVariable())){
					if (!DCOPLeaderProtocol.dcopProtocol.equals(LogService.onNone))logMonologue("grapChanged",LogService.onFile);						
					for (AgentIdentifier id : constraintM.getMyAcquaintances()){
						getMyInformation().addAcquaintance(constraintM.getVariable(), id);
					}
				}
			}
		} else if (m instanceof DcopValueMessage){ 
			//						logMonologue("receiving new value message of "+m.getVariable(),DCOPLeaderProtocol.dcopProtocol);
			//trigger new computation for fringe variable
			if (fringeNodes2ring.containsKey(m.getVariable()) && 
					(getMyInformation().getAgentsIdentifier().contains(m.getVariable()) && !getMyInformation().getAgentState(m.getVariable()).equals(((DcopValueMessage<State>)m).getMyState()))
					|| !getMyInformation().getAgentsIdentifier().contains(m.getVariable())){
				changeFlag.addAll(fringeNodes2ring.get(m.getVariable()));
			}
			getMyInformation().setState(((DcopValueMessage<State>)m).getMyState());
		}
		//forward
		super.beInformed(m);
	}

	public boolean iveFullInfo(Collection<AgentIdentifier> group){
		for (AgentIdentifier id : group){
			if (getMyInformation().getState(id)==null)
				return false;
			//			logMonologue(group+"\n"+localView);
			for (AgentIdentifier ac : getMyInformation().getAcquaintances(id)){
				if (getMyInformation().getState(ac)==null)
					return false;
				if  (getMyInformation().getState(ac).hasResource(id)!=getMyInformation().getState(id).hasResource(ac))
					return false;
			}
		}
		return true;
	}

	@StepComposant
	public void computeLeaderShip(){
		//		logMonologue("computing leader ship"+localView.getHostsIdentifier());
		if (getMyInformation().isCoherent()){
			if ((graphChanged || (currentlyOptimizedRig.size()<numberOfSimulateonuslyOptimizedRig))){// && iveFullInfo()){
				assert localViewCheck(); 
				if (kSizeGroups==null || !kSizeGroups.hasNext())kSizeGroups=new CombinaisonIterator<AgentIdentifier>(new ArrayList(neighberhood), k);
				while (kSizeGroups.hasNext()){
					Collection<AgentIdentifier> group = kSizeGroups.next();
					//				if ( iveFullInfo(group) ) logWarning("yeah!");
					//				logMonologue("analysing "+group);
					if ( iveFullInfo(group) && iMLeader(group)){
						//					logWarning("double    yeeaaaaaaaaaa yeah!");
						if (!DCOPLeaderProtocol.dcopProtocol.equals(LogService.onNone))logMonologue("i'm leader of "+group,LogService.onBoth);
						//Initialisation du rig
						//						assert getMyInformation().assertValidity();
						ReplicationInstanceGraph neoRig = getRig(group);
						//Calcul des fringe	
						Collection<AgentIdentifier> fringeNodes = new HashSet<AgentIdentifier>();
						fringeNodes.addAll(neoRig.getAgentsIdentifier());
						fringeNodes.addAll(neoRig.getHostsIdentifier());
						fringeNodes.removeAll(group);
						for (AgentIdentifier f : fringeNodes){
							this.fringeNodes2ring.add(f,group);
							this.rig2fringeNodes.add(group,f);
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
						changeFlag.add(group);
						currentlyOptimizedRig.add(group);
						graphChanged=false;
						break;
					} else {					
						//					logMonologue("no ! leader is  ");
					}
				}
			}
		}
	}

	private ReplicationInstanceGraph getRig(Collection<AgentIdentifier> group) {
		ReplicationInstanceGraph neoRig = new ReplicationInstanceGraph(null);
		for (AgentIdentifier id : group){
			neoRig.setState(getMyInformation().getState(id));
			for (AgentIdentifier r : getMyInformation().getAcquaintances(id)){
				neoRig.addAcquaintance(id, r);
				neoRig.setState(getMyInformation().getState(r));
				for (AgentIdentifier rp : getMyInformation().getAcquaintances(r)){
					if (neoRig.getEveryIdentifier().contains(rp)){
						//																	if (group.contains(rp)){
						neoRig.addAcquaintance(r, rp);
					}
				}
			}
		}
		try {
			//						assert neoRig.assertNeigborhoodValidity():group+"\n----------------------------------\n"+getMyInformation()+"\n----------------------------------\n"+neoRig;
		} catch (AssertionError e){
			logWarning("is not valid!!! :"+group+"\n"+getMyInformation()+"\n----------------------------------\n"+neoRig,e);
		}
		return neoRig;
	}

	public boolean localViewCheck(){
		Collection<AgentIdentifier> allKnownStates = new ArrayList<AgentIdentifier>();
		allKnownStates.addAll(getMyInformation().getAgentsIdentifier());
		allKnownStates.addAll(getMyInformation().getHostsIdentifier());
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
		if (!changeFlag.isEmpty() && getMyInformation().isCoherent()){
			for (Collection<AgentIdentifier> group : changeFlag){
				ReplicationInstanceGraph rig = getRig(group);
				assert rig.assertNeigborhoodValidity():rig;
				solver.setProblem(rig, rig2fringeNodes.get(group));
				solver.setTimeLimit((int) maxComputingTime);
				try {
					//					logMonologue("compution new allocation "+rig.getEveryIdentifier());// for "+rig+"\n fringe are "+rig2fringeNodes.get(rig));
					Set<Contract> opt =  new HashSet(solver.getBestLocalSolution());
					assert solValidity(opt);
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
					Collection<Contract> courant = gainContracts.containsKey(group)?gainContracts.get(group):new ArrayList<Contract>();
					assert opt!=null;
					assert courant!=null;
					if (getMyAgent().getMyAllocationPreferenceComparator().compare(opt, courant)>0){
						gainContracts.put(group,opt);
						gainFlag.add(group);
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

	private boolean solValidity(Set<Contract> opt){
		ReplicationInstanceGraph rig = new ReplicationInstanceGraph(null);
		for (AgentState s : getMyInformation().getAgentStates()){
			rig.setState(s);
		}
		for (AgentState s : getMyInformation().getHostsStates()){
			rig.setState(s);
		}
		for (Contract c : opt){
			try {
				rig.setState(c.computeResultingState(rig.getState(c.getAgent())));
				rig.setState(c.computeResultingState(rig.getState(c.getResource())));
			} catch (IncompleteContractException e) {
				throw new RuntimeException("impossible");
			}
		}
		assert rig.assertAllocValid();
		return true;
	}

	/*
	 * Selection Core
	 */

	/*
	 * Protocol
	 */

	public HashedHashSet<AgentIdentifier, Contract> getWannaLockContract(){
		return ((DcopLeaderProposerCore)getMyAgent().getMyProposerCore()).iWannaLock;
	}

	public boolean iCanAcceptLock(Contract lockRequest){
		if (!super.iCanAcceptLock(lockRequest))
			return false;
		else {
			List<Contract> myContracts = new ArrayList(getWannaLockContract().getAllValues());
			Contract myBest = Collections.min(myContracts, getContractComparator());
			assert myBest.getInitiator().equals(getIdentifier());
			
			return getContractComparator().compare(lockRequest,myBest)<=0;
		}
	}

	@Override
	protected boolean ImAllowedToNegotiate(ContractTrunk<Contract> contracts) {
		return true;
	}
	
	protected Receivers getProposalReceivers() {
		return Receivers.NotInitiatingParticipant;
	}
	
	@Override
	protected void answerAccepted(Collection<Contract> toAccept) {
		Collection<Contract> confirm = new ArrayList<Contract>();
		Collection<Contract> acceptLock = new ArrayList<Contract>();
		for (Contract c : toAccept){
			if (c.getInitiator().equals(getMyAgent().getIdentifier())){
				confirm.add(c);
				myLock.get(c.getInitiator()).remove(c.getContractIdentifier());
				ArrayList<Contract> cs = new ArrayList<Contract>(getContracts().getAllContracts());
				try {
					AgentState newRessState = c.computeResultingState(getMyInformation().getState(c.getResource()));
					AgentState newAgState = c.computeResultingState(getMyInformation().getState(c.getAgent()));
					if (!c.getResource().equals(getIdentifier())){
						getMyInformation().setState(newRessState);
					}
					getMyInformation().setState(newAgState);
//					for (ReplicationInstanceGraph rig : currentlyOptimizedRig){
//						if (!c.getResource().equals(getIdentifier())){
//							rig.setState(newRessState);
//						}
//						rig.setState(newAgState);						
//					}


				} catch (IncompleteContractException e) {
					throw new RuntimeException("impossible");
				}
				//				for (Contract c2 : cs){
				//					if (!c2.equals(c) && c2.getAllParticipants().equals(c.getAllParticipants())){
				//						//						System.err.println(c2+" "+c);
				//						assert c2.isMatchingCreation()==c.isMatchingCreation();
				//						getContracts().remove(c2);
				//					}
				//				}
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
