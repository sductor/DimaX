package frameworks.negotiation.protocoles.dcopProtocol;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import dima.basicagentcomponents.AgentIdentifier;
import dima.introspectionbasedagents.annotations.ProactivityInitialisation;
import dima.introspectionbasedagents.annotations.StepComposant;
import dima.introspectionbasedagents.modules.mappedcollections.HashedHashSet;
import dima.introspectionbasedagents.services.UnrespectedCompetenceSyntaxException;
import dima.introspectionbasedagents.services.loggingactivity.LogService;
import frameworks.faulttolerance.experimentation.ReplicationInstanceGraph;
import frameworks.faulttolerance.experimentation.SearchTimeNotif;
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

	/**
	 * 
	 */
	private static final long serialVersionUID = 856514418947631064L;

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

	public DCOPLeaderProtocol(final int k, final int t,
			final ResourceAllocationSolver<Contract, State> solver,
			final long maxComputingTime)
					throws UnrespectedCompetenceSyntaxException {
		super(k);
		this.numberOfSimulateonuslyOptimizedRig=t;
		this.solver = solver;
		this.solver.setMyAgent(this);
		this.maxComputingTime=maxComputingTime;
	}

	LocalViewInformationService<State, Contract> getMyInformation(){
		return (LocalViewInformationService<State, Contract>) this.getMyAgent().getMyInformation();
	}

	//
	// Behavior
	//

	@ProactivityInitialisation
	public void initView(){
		//		logMonologue("init");
		this.getMyInformation().setState(this.getMyAgent().getMyCurrentState());
		for (final AgentIdentifier id : this.getMyAgent().getKnownResources()){
			this.getMyInformation().addAcquaintance(this.getIdentifier(), id);
		}
		this.neighberhood.add(this.getIdentifier());
	}

	@Override
	public void beInformed(final DcopValueMessage<State> m){
		//update
		if (m instanceof DcopConstraintsMessage){
			//			if (m.getVariable() instanceof ResourceIdentifier)logMonologue("receiving  constraint message of "+m.getVariable());
			final DcopConstraintsMessage<State> constraintM = (DcopConstraintsMessage<State> )m;
			assert constraintM.getMyState()!=null;
			this.getMyInformation().setState(constraintM.getMyState());
			if (m.mustBeForwarded()){//It belongs to my k-distance neighberhood
				if (this.neighberhood.add(constraintM.getVariable())){
					if (!DCOPLeaderProtocol.dcopProtocol.equals(LogService.onNone)) {
						this.logMonologue("grapChanged",LogService.onFile);
					}
					for (final AgentIdentifier id : constraintM.getMyAcquaintances()){
						this.getMyInformation().addAcquaintance(constraintM.getVariable(), id);
					}
				}
			}
		} else if (m instanceof DcopValueMessage){
			//						logMonologue("receiving new value message of "+m.getVariable(),DCOPLeaderProtocol.dcopProtocol);
			//trigger new computation for fringe variable
			if (this.fringeNodes2ring.containsKey(m.getVariable()) &&
					this.getMyInformation().getAgentsIdentifier().contains(m.getVariable()) && !this.getMyInformation().getAgentState(m.getVariable()).equals(m.getMyState())
					|| !this.getMyInformation().getAgentsIdentifier().contains(m.getVariable())){
				this.changeFlag.addAll(this.fringeNodes2ring.get(m.getVariable()));
			}
			this.getMyInformation().setState(m.getMyState());
		}
		//forward
		super.beInformed(m);
	}

	public boolean iveFullInfo(final Collection<AgentIdentifier> group){
		for (final AgentIdentifier id : group){
			if (this.getMyInformation().getState(id)==null) {
				return false;
			}
			//			logMonologue(group+"\n"+localView);
			for (final AgentIdentifier ac : this.getMyInformation().getAcquaintances(id)){
				if (this.getMyInformation().getState(ac)==null) {
					return false;
				}
				if  (this.getMyInformation().getState(ac).hasResource(id)!=this.getMyInformation().getState(id).hasResource(ac)) {
					return false;
				}
			}
		}
		return true;
	}

	@StepComposant
	public void computeLeaderShip(){
		//		logMonologue("computing leader ship"+localView.getHostsIdentifier());
		if (this.getMyInformation().isCoherent()){
			if (this.graphChanged || this.currentlyOptimizedRig.size()<this.numberOfSimulateonuslyOptimizedRig){// && iveFullInfo()){
				assert this.localViewCheck();
				if (this.kSizeGroups==null || !this.kSizeGroups.hasNext()) {
					this.kSizeGroups=new CombinaisonIterator<AgentIdentifier>(new ArrayList(this.neighberhood), this.k);
				}
				while (this.kSizeGroups.hasNext()){
					final Collection<AgentIdentifier> group = this.kSizeGroups.next();
					//				if ( iveFullInfo(group) ) logWarning("yeah!");
					//				logMonologue("analysing "+group);
					if ( this.iveFullInfo(group) && this.iMLeader(group)){
						//					logWarning("double    yeeaaaaaaaaaa yeah!");
						if (!DCOPLeaderProtocol.dcopProtocol.equals(LogService.onNone)) {
							this.logMonologue("i'm leader of "+group,LogService.onBoth);
						}
						//Initialisation du rig
						//						assert getMyInformation().assertValidity();
						final ReplicationInstanceGraph neoRig = this.getRig(group);
						//Calcul des fringe
						final Collection<AgentIdentifier> fringeNodes = new HashSet<AgentIdentifier>();
						fringeNodes.addAll(neoRig.getAgentsIdentifier());
						fringeNodes.addAll(neoRig.getHostsIdentifier());
						fringeNodes.removeAll(group);
						for (final AgentIdentifier f : fringeNodes){
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
						this.changeFlag.add(group);
						this.currentlyOptimizedRig.add(group);
						this.graphChanged=false;
						break;
					} else {
						//					logMonologue("no ! leader is  ");
					}
				}
			}
		}
	}

	private ReplicationInstanceGraph getRig(final Collection<AgentIdentifier> group) {
		final ReplicationInstanceGraph neoRig = new ReplicationInstanceGraph(null);
		for (final AgentIdentifier id : group){
			neoRig.setState(this.getMyInformation().getState(id));
			for (final AgentIdentifier r : this.getMyInformation().getAcquaintances(id)){
				neoRig.addAcquaintance(id, r);
				neoRig.setState(this.getMyInformation().getState(r));
				for (final AgentIdentifier rp : this.getMyInformation().getAcquaintances(r)){
					if (neoRig.getEveryIdentifier().contains(rp)){
						//																	if (group.contains(rp)){
						neoRig.addAcquaintance(r, rp);
					}
				}
			}
		}
		try {
			//						assert neoRig.assertNeigborhoodValidity():group+"\n----------------------------------\n"+getMyInformation()+"\n----------------------------------\n"+neoRig;
		} catch (final AssertionError e){
			this.logWarning("is not valid!!! :"+group+"\n"+this.getMyInformation()+"\n----------------------------------\n"+neoRig,e);
		}
		return neoRig;
	}

	public boolean localViewCheck(){
		final Collection<AgentIdentifier> allKnownStates = new ArrayList<AgentIdentifier>();
		allKnownStates.addAll(this.getMyInformation().getAgentsIdentifier());
		allKnownStates.addAll(this.getMyInformation().getHostsIdentifier());
		assert allKnownStates.containsAll(this.neighberhood);
		return true;
	}
	//		@StepComposant(ticker=1000)
	//		public void sayAlive2() {
	//			logWarning("I'M STILL ALIVE",LogService.onScreen);
	//		}
	private boolean iMLeader(final Collection<AgentIdentifier> group) {
		assert this.getIdentifier()instanceof ResourceIdentifier;
		int nbHost=0;
		boolean hasAgent=false;
		Integer max=Integer.MIN_VALUE;
		Integer min=Integer.MAX_VALUE;

		for (final AgentIdentifier id : group){
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
		} else if (group.contains(this.getIdentifier())){
			assert ReplicationInstanceGraph.identifierToInt(this.getIdentifier())<=max && ReplicationInstanceGraph.identifierToInt(this.getIdentifier())>=min;
			if (nbHost%2==0){
				return ReplicationInstanceGraph.identifierToInt(this.getIdentifier()).equals(max);
			} else {
				return ReplicationInstanceGraph.identifierToInt(this.getIdentifier()).equals(min);
			}
		} else {
			return false;
		}
	}

	@StepComposant
	public void computeNewGain(){
		if (!this.changeFlag.isEmpty() && this.getMyInformation().isCoherent()){
			for (final Collection<AgentIdentifier> group : this.changeFlag){
				final ReplicationInstanceGraph rig = this.getRig(group);
				assert rig.assertNeigborhoodValidity():rig;
				final Date startingExploringTime=new Date();
				this.solver.setProblem(rig, this.rig2fringeNodes.get(group));
				this.solver.setTimeLimit((int) this.maxComputingTime);
				try {
					//					logMonologue("compution new allocation "+rig.getEveryIdentifier());// for "+rig+"\n fringe are "+rig2fringeNodes.get(rig));
					final Set<Contract> opt =  new HashSet(this.solver.getBestLocalSolution());
					this.notify(new SearchTimeNotif(new Double(new Date().getTime() - startingExploringTime.getTime())));
					assert this.solValidity(opt);
					//					logMonologue("new allo computed");// for "+rig+"\n fringe are "+rig2fringeNodes.get(rig));
					final Collection<Contract> fusedContract= this.gainContracts.getAllValues();
					for (final Contract c : fusedContract){
						try {
							if (opt.contains(c)){
								opt.remove(c);
								opt.add(c);
							}
						} catch (final AssertionError e){//l'assertion assurant qu'il n'y a pas deux contrat identique en circulation
							opt.remove(c);
							opt.add(c);
						}
					}
					final Collection<Contract> courant = this.gainContracts.containsKey(group)?this.gainContracts.get(group):new ArrayList<Contract>();
					assert opt!=null;
					assert courant!=null;
					if (this.getMyAgent().getMyAllocationPreferenceComparator().compare(opt, courant)>0){
						this.gainContracts.put(group,opt);
						this.gainFlag.add(group);
						this.logMonologue("new allocation  find!!!!!!!!!!!!!!! =D ",DCOPLeaderProtocol.dcopProtocol);
					}
				}
				catch (final UnsatisfiableException e) {/*do nothing*/
					this.logMonologue("unsatisfiable >=[");}
				catch (final ExceedLimitException e) {/*do nothing*/
					this.logMonologue("time limits! >=[");}

				//								logMonologue("no allocation  find!!!!!!!!!!!!!!! =(");
			}
		}
		this.changeFlag.clear();
	}

	private boolean solValidity(final Set<Contract> opt){
		final ReplicationInstanceGraph rig = new ReplicationInstanceGraph(null);
		for (final AgentState s : this.getMyInformation().getAgentStates()){
			rig.setState(s);
		}
		for (final AgentState s : this.getMyInformation().getHostsStates()){
			rig.setState(s);
		}
		for (final Contract c : opt){
			try {
				rig.setState(c.computeResultingState(rig.getState(c.getAgent())));
				rig.setState(c.computeResultingState(rig.getState(c.getResource())));
			} catch (final IncompleteContractException e) {
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
		return ((DcopLeaderProposerCore)this.getMyAgent().getMyProposerCore()).iWannaLock;
	}

	@Override
	public boolean iCanAcceptLock(final Contract lockRequest){
		if (!super.iCanAcceptLock(lockRequest)) {
			return false;
		} else if (this.getWannaLockContract().isEmpty())  {
			return true;
		} else {
			final List<Contract> myContracts = new ArrayList(this.getWannaLockContract().getAllValues());
			final Contract myBest = Collections.min(myContracts, this.getContractComparator());
			assert myBest.getInitiator().equals(this.getIdentifier());

			return this.getContractComparator().compare(lockRequest,myBest)<=0;
		}
	}

	@Override
	protected boolean ImAllowedToNegotiate(final ContractTrunk<Contract> contracts) {
		return true;
	}

	@Override
	protected Receivers getProposalReceivers() {
		return Receivers.NotInitiatingParticipant;
	}

	@Override
	protected void answerAccepted(final Collection<Contract> toAccept) {
		final Collection<Contract> confirm = new ArrayList<Contract>();
		final Collection<Contract> acceptLock = new ArrayList<Contract>();
		for (final Contract c : toAccept){
			if (c.getInitiator().equals(this.getMyAgent().getIdentifier())){
				confirm.add(c);
				this.myLock.get(c.getInitiator()).remove(c.getContractIdentifier());
				final ArrayList<Contract> cs = new ArrayList<Contract>(this.getContracts().getAllContracts());
				try {
					final AgentState newRessState = c.computeResultingState(this.getMyInformation().getState(c.getResource()));
					final AgentState newAgState = c.computeResultingState(this.getMyInformation().getState(c.getAgent()));
					if (!c.getResource().equals(this.getIdentifier())){
						this.getMyInformation().setState(newRessState);
					}
					this.getMyInformation().setState(newAgState);
					//					for (ReplicationInstanceGraph rig : currentlyOptimizedRig){
					//						if (!c.getResource().equals(getIdentifier())){
					//							rig.setState(newRessState);
					//						}
					//						rig.setState(newAgState);
					//					}


				} catch (final IncompleteContractException e) {
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
		this.confirmContract(confirm, Receivers.NotInitiatingParticipant);
		this.acceptContract(acceptLock, Receivers.Initiator);
	}

	@Override
	protected void answerRejected(final Collection<Contract> toReject) {
		final Collection<Contract> cancel = new ArrayList<Contract>();
		final Collection<Contract> refuseLock = new ArrayList<Contract>();
		for (final Contract c : toReject){
			if (c.getInitiator().equals(this.getMyAgent().getIdentifier())){
				cancel.add(c);
				this.myLock.remove(c.getInitiator(),c);
			} else {
				refuseLock.add(c);
			}
		}
		this.cancelContract(cancel, Receivers.NotInitiatingParticipant);
		this.rejectContract(refuseLock, Receivers.Initiator);
	}

}
