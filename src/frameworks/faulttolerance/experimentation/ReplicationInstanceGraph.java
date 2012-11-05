package frameworks.faulttolerance.experimentation;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;



import dima.basicagentcomponents.AgentIdentifier;
import dima.basicagentcomponents.AgentName;
import dima.introspectionbasedagents.modules.distribution.DistributionParameters;
import dima.introspectionbasedagents.modules.distribution.NormalLaw.DispersionSymbolicValue;
import dima.introspectionbasedagents.modules.mappedcollections.HashedHashSet;
import dima.support.GimaObject;
import frameworks.experimentation.ExperimentationParameters;
import frameworks.experimentation.IfailedException;
import frameworks.faulttolerance.negotiatingagent.HostState;
import frameworks.faulttolerance.negotiatingagent.ReplicaState;
import frameworks.faulttolerance.negotiatingagent.ReplicationCandidature;
import frameworks.faulttolerance.olddcop.DCOPFactory;
import frameworks.negotiation.contracts.ResourceIdentifier;
import frameworks.negotiation.contracts.AbstractContractTransition.IncompleteContractException;
import frameworks.negotiation.rationality.AgentState;
import frameworks.negotiation.rationality.SocialChoiceFunction.SocialChoiceType;

public class ReplicationInstanceGraph
extends GimaObject implements ReplicationGraph{

	/**
	 *
	 */
	private static final long serialVersionUID = -1869273198805540149L;

	//	private final int kAccessible;

	private Map<AgentIdentifier, ReplicaState> agents=new HashMap<AgentIdentifier, ReplicaState>();
	private Map<ResourceIdentifier, HostState> hosts=new HashMap<ResourceIdentifier, HostState>();

	private HashedHashSet<AgentIdentifier, ResourceIdentifier> accHosts=
			new HashedHashSet<AgentIdentifier, ResourceIdentifier>();
	private HashedHashSet<ResourceIdentifier, AgentIdentifier> accAgents =
			new HashedHashSet<ResourceIdentifier, AgentIdentifier>();

	final SocialChoiceType socialWelfare;

	//
	// Getter
	//

	public void setAgents(Collection<ReplicaState> collection) {
		agents =new HashMap<AgentIdentifier, ReplicaState>();
		for (ReplicaState a : collection){
			agents.put(a.getMyAgentIdentifier(),a);
		}
	}

	public void setHosts(Collection<HostState> hostsStates) {
		hosts =	new HashMap<ResourceIdentifier, HostState>();
		for (HostState a : hostsStates){
			hosts.put(a.getMyAgentIdentifier(),a);
		}
	}

	public void setState(AgentState s){
		assert s!=null;
		if (s instanceof HostState){
			hosts.put(((HostState)s).getMyAgentIdentifier(), (HostState)s);
		} else {
			assert s instanceof ReplicaState:s;
		agents.put(s.getMyAgentIdentifier(), (ReplicaState)s);
		}
	}
	public void addAcquaintance(final AgentIdentifier a1, final AgentIdentifier a2){
		final AgentIdentifier ag;
		final ResourceIdentifier h;
		if (a1 instanceof ResourceIdentifier){
			assert !(a2 instanceof ResourceIdentifier);
			ag = a2;
			h = (ResourceIdentifier) a1;
		} else {
			assert (a2 instanceof ResourceIdentifier);
			ag = a1;
			h = (ResourceIdentifier) a2;			
		}
		this.accHosts.add(ag,h);
		this.accAgents.add(h, ag);
		//		assert this.accAgents.get(h).size() <= this.kAccessible:this.accAgents;
	}
	public AgentState getState(AgentIdentifier id) {
		if (id instanceof ResourceIdentifier){
			return getHostState((ResourceIdentifier)id);
		} else {		
			return getAgentState(id);
		}
	}
	public Collection<? extends AgentIdentifier> getAcquaintances(final AgentIdentifier id){
		if (id instanceof ResourceIdentifier){
			return getAccessibleAgents((ResourceIdentifier)id);
		} else {		
			return getAccessibleHosts(id);
		}
		//		assert this.accAgents.get(h).size() <= this.kAccessible:this.accAgents;
	}

	public ReplicationInstanceGraph(SocialChoiceType socialWelfare) {
		super();
		this.socialWelfare = socialWelfare;
	}
	@Override
	public SocialChoiceType getSocialWelfare() {
		return socialWelfare;
	}


	public Collection<AgentIdentifier> getEveryIdentifier() {
		Collection<AgentIdentifier> result = new ArrayList<AgentIdentifier>();
		result.addAll(this.agents.keySet());
		result.addAll(this.hosts.keySet());
		return result;
	}

	@Override
	public Collection<AgentIdentifier> getAgentsIdentifier() {
		return this.agents.keySet();
	}

	@Override
	public Collection<ResourceIdentifier> getHostsIdentifier() {
		return this.hosts.keySet();
	}

	@Override
	public Collection<ReplicaState> getAgentStates() {
		return this.agents.values();
	}

	@Override
	public Collection<HostState> getHostsStates() {
		return this.hosts.values();
	}

	@Override
	public ReplicaState getAgentState(final AgentIdentifier id){
		return this.agents.get(id);
	}

	@Override
	public HostState getHostState(final ResourceIdentifier id){
		return this.hosts.get(id);
	}

	public Collection<ResourceIdentifier> getInitialReplication(final AgentIdentifier id){
		return this.getAgentState(id).getMyResourceIdentifiers();
	}

	public Collection<AgentIdentifier> getInitialReplication(final ResourceIdentifier id){
		return this.getHostState(id).getMyResourceIdentifiers();
	}


	@Override
	public Collection<ResourceIdentifier> getAccessibleHosts(final AgentIdentifier id){
		return Collections.unmodifiableSet(this.accHosts.get(id));
	}


	@Override
	public Collection<AgentIdentifier> getAccessibleAgents(final ResourceIdentifier id){
		return Collections.unmodifiableSet(this.accAgents.get(id));
	}

	@Override
	public boolean areLinked(AgentIdentifier a1, AgentIdentifier a2) {
		final AgentIdentifier ag;
		final ResourceIdentifier h;
		if (a1 instanceof ResourceIdentifier){
			assert !(a2 instanceof ResourceIdentifier);
			ag = a2;
			h = (ResourceIdentifier) a1;
		} else {
			assert (a2 instanceof ResourceIdentifier);
			ag = a1;
			h = (ResourceIdentifier) a2;			
		}
		assert getAccessibleAgents(h).contains(ag)==getAccessibleHosts(ag).contains(h);
		return getAccessibleAgents(h).contains(ag);
	}
	//
	// Methods
	//

	public void randomInitiaition(
			final String simulationName,long randSeed,
			final int nbAgents, int nbHosts,
			final Double agentCriticityMean,final DispersionSymbolicValue agentCriticityDispersion,
			final Double agentLoadMean,final DispersionSymbolicValue agentLoadDispersion,
			final Double hostCapacityMean,final DispersionSymbolicValue hostCapacityDispersion,
			final Double hostFaultProbabilityMean,final DispersionSymbolicValue hostDisponibilityDispersion, 
			int agentAccessiblePerHost, int maxHostAccessiblePerAgent) throws IfailedException{
		Random rand;
		rand = new Random(randSeed);
		this.initiateAgents(
				simulationName, rand,
				nbAgents, nbHosts, 
				agentCriticityMean, agentCriticityDispersion, 
				agentLoadMean, agentLoadDispersion, 
				hostCapacityMean, hostCapacityDispersion, hostFaultProbabilityMean, hostDisponibilityDispersion);


		//		this.logMonologue("Agents & Hosts:\n"+this.rig.getAgentStates()+"\n"+this.rig.getHostsStates(), LogService.onFile);

		int count = 5;
		boolean iFailed=false;
		do {
			try{
				iFailed=false;
				this.setVoisinage(agentAccessiblePerHost, maxHostAccessiblePerAgent, rand);
				this.initialRep(rand);
				this.assertNeigborhoodValidity();
			} catch (final IfailedException e) {
				iFailed=true;
				//				this.logWarning("I'v faileeeeeddddddddddddd RETRYINNNGGGGG "+count+e, LogService.onBoth);
				count--;
				if (count==0) {
					throw e;
				}
			}
		}while(iFailed && count > 0);
	}

	public ReplicationInstanceGraph getUnallocatedGraph(){
		Collection<ReplicaState> repSt = new ArrayList<ReplicaState>();
		for (ReplicaState r : this.getAgentStates()){
			ReplicaState r2 = r.freeAllResources();
			repSt.add(r2);
		}

		Collection<HostState> hostSt = new ArrayList<HostState>();
		for (HostState r : this.getHostsStates()){
			HostState r2 = r.freeAllResources();
			hostSt.add(r2);
		}
		ReplicationInstanceGraph rig = new ReplicationInstanceGraph(this.getSocialWelfare());
		rig.setAgents(repSt);
		rig.setHosts(hostSt);

		rig.accAgents=this.accAgents;
		rig.accHosts=this.accHosts;

		return rig;
	}

	public static Integer identifierToInt(AgentIdentifier id){
		return new Integer(id.toString().split("-=-")[1]);
	}

	public static AgentIdentifier intToIdentifier(
			String simulationName, int i){
		if (i%2==0)
			return new AgentName("#"+simulationName+"#DomainAgent_-=-"+i+"-=-");
		else
			return new ResourceIdentifier("#"+simulationName+"#HostManager_-=-"+i+"-=-",77);
	}


	//
	// Primitive
	//

	public String toString(){
		String result = "Agents :\n"+getAgentStates()+"\n Host :\n"+getHostsStates()+"\n";
		for (AgentIdentifier id : getAgentsIdentifier()){
			result+="Agent "+id+" ----> "+getAccessibleHosts(id)+"\n";
		}
		for (ResourceIdentifier id : getHostsIdentifier()){
			result+="Host  "+id+" ----> "+getAccessibleAgents(id)+"\n";
		}
		return result;
	}

	//
	// Internals
	//


	private void initiateAgents(
			final String simulationName,Random rand,
			final int nbAgents, int nbHosts,
			final Double agentCriticityMean,final DispersionSymbolicValue agentCriticityDispersion,
			final Double agentLoadMean,final DispersionSymbolicValue agentLoadDispersion,
			final Double hostCapacityMean,final DispersionSymbolicValue hostCapacityDispersion,
			final Double hostFaultProbabilityMean,final DispersionSymbolicValue hostDisponibilityDispersion){


		/*
		 * Agents and hosts names
		 */
		agents =new HashMap<AgentIdentifier, ReplicaState>();
		hosts =	new HashMap<ResourceIdentifier, HostState>();

		for (int i=0; i<2*nbAgents; i+=2) {
			this.agents.put(intToIdentifier(simulationName,i),null);
		}

		for (int i=1; i<2*nbHosts+1; i+=2) {
			this.hosts.put((ResourceIdentifier) intToIdentifier(simulationName,i),null);
		}


		/*
		 * States
		 */

		final DistributionParameters<AgentIdentifier> agentCriticity = new DistributionParameters<AgentIdentifier>(
				this.agents.keySet(),
				agentCriticityMean,
				agentCriticityDispersion,rand,false);
		final DistributionParameters<AgentIdentifier>  agentProcessor = new DistributionParameters<AgentIdentifier>(
				this.agents.keySet(),
				agentLoadMean,
				agentLoadDispersion,rand, true);
		final DistributionParameters<AgentIdentifier> agentMemory = new DistributionParameters<AgentIdentifier>(
				this.agents.keySet(),
				agentLoadMean,
				agentLoadDispersion,rand, true);
		final DistributionParameters<ResourceIdentifier> hostMemCapacity = new DistributionParameters<ResourceIdentifier>(
				this.hosts.keySet(),
				hostCapacityMean,
				hostCapacityDispersion,rand, true);
		final DistributionParameters<ResourceIdentifier> hostProcCapacity = new DistributionParameters<ResourceIdentifier>(
				this.hosts.keySet(),
				hostCapacityMean,
				hostCapacityDispersion,rand, true);
		final DistributionParameters<ResourceIdentifier> fault = new DistributionParameters<ResourceIdentifier>(
				this.hosts.keySet(),
				hostFaultProbabilityMean,
				hostDisponibilityDispersion,rand,false);

		for (final AgentIdentifier id : this.agents.keySet()){
			this.agents.put(id,
					new ReplicaState(id,
							Math.min(ReplicationExperimentationParameters._criticityMin+ agentCriticity.get(id), 1),
							agentProcessor.get(id),
							agentMemory.get(id),
							socialWelfare));
		}

		for (final ResourceIdentifier hostId : this.hosts.keySet()){
			this.hosts.put(hostId,
					new HostState(hostId,
							hostProcCapacity.get(hostId),
							hostMemCapacity.get(hostId),
							fault.get(hostId)));
		}
		//		System.out.println(hosts.values());
	}

	private void setVoisinage(int agentAccessiblePerHost, int maxHostAccessiblePerAgent,Random rand){
		this.accHosts =
				new HashedHashSet<AgentIdentifier, ResourceIdentifier>();
		this.accAgents =
				new HashedHashSet<ResourceIdentifier, AgentIdentifier>();
		boolean completGraph = agentAccessiblePerHost>=getAgentsIdentifier().size()&&maxHostAccessiblePerAgent>=getHostsIdentifier().size();
		if (completGraph){
			for (final AgentIdentifier agId : this.agents.keySet()){
				for (final ResourceIdentifier h : this.hosts.keySet()) {
					this.addAcquaintance(agId, h);
				}
			}
		}else{
			final List<ResourceIdentifier> hostsIdentifier = new ArrayList<ResourceIdentifier>(this.hosts.keySet());
			final List<AgentIdentifier> replicasIdentifier = new ArrayList<AgentIdentifier>(this.agents.keySet());

			Collections.shuffle(hostsIdentifier,rand);
			Collections.shuffle(replicasIdentifier,rand);
			Iterator<ResourceIdentifier> hotId = hostsIdentifier.iterator();
			//making a initial valid neigborhood : one host per agent
			for (final AgentIdentifier agId : replicasIdentifier){
				if (!hotId.hasNext()) {
					hotId = hostsIdentifier.iterator();
				}
				this.addAcquaintance(
						agId,
						hotId.next());
			}

			Collections.shuffle(hostsIdentifier,rand);
			for (final ResourceIdentifier h : hostsIdentifier) {
				Collections.shuffle(replicasIdentifier,rand);
				final Iterator<AgentIdentifier> agId = replicasIdentifier.iterator();
				/* Adding acquaintance for host within latence */
				while (this.getAccessibleAgents(h).size()<agentAccessiblePerHost && agId.hasNext()) {
					AgentIdentifier a = agId.next();
					if (getAccessibleHosts(a).size()<maxHostAccessiblePerAgent)
						this.addAcquaintance(a, h);
				}
			}
		}

	}

	private void initialRep(Random rand) throws IfailedException{
		ExperimentationParameters.currentlyInstanciating=true;
		//duplicating in case of failure
		final Map<AgentIdentifier, ReplicaState> agentsTemp =
				new HashMap<AgentIdentifier, ReplicaState>(this.agents);
		final Map<ResourceIdentifier, HostState> hostsTemp =
				new HashMap<ResourceIdentifier, HostState>(this.hosts);
		final List<AgentIdentifier> replicasIdentifier = new ArrayList<AgentIdentifier>(agentsTemp.keySet());
		Collections.shuffle(replicasIdentifier,rand);

		final HashSet<AgentIdentifier> done = new HashSet<AgentIdentifier>();

		for (final AgentIdentifier agId : replicasIdentifier){
			final List<ResourceIdentifier> hostsIdentifier = new ArrayList<ResourceIdentifier>(this.getAccessibleHosts(agId));
			Collections.shuffle(hostsIdentifier,rand);
			final Iterator<ResourceIdentifier> itHost =
					hostsIdentifier.iterator();

			assert done.add(agId):agId+" "+done+" "+replicasIdentifier;
			assert itHost.hasNext():"no host? argh!\n"+agId+"\n"+this.getAccessibleHosts(agId);

			ResourceIdentifier firstReplicatedOnHost = itHost.next();
			while (!allocateAgents(agId, firstReplicatedOnHost,agentsTemp,hostsTemp)) {
				if (!itHost.hasNext()) {
					throw new IfailedException("can not create at least one rep for each agent\n");
				} else {
					firstReplicatedOnHost = itHost.next();
				}
			}
		}

		this.agents.clear();
		this.agents.putAll(agentsTemp);
		this.hosts.clear();
		this.hosts.putAll(hostsTemp);
		ExperimentationParameters.currentlyInstanciating=false;

	}

	private boolean allocateAgents(
			final AgentIdentifier r, final ResourceIdentifier h,
			final Map<AgentIdentifier, ReplicaState> agents,
			final Map<ResourceIdentifier, HostState> hosts){
		try {
			assert getAccessibleAgents(h).contains(r);
			assert getAccessibleHosts(r).contains(h);
			final ReplicationCandidature c = new ReplicationCandidature(
					h,
					r,
					true,true);
			c.setInitialState(agents.get(r));
			c.setInitialState(hosts.get(h));

			if (!c.computeResultingState(h).isValid() || !c.computeResultingState(r).isValid()) {
				return false;
			} else {
				agents.put(r, (ReplicaState) c.computeResultingState(r));
				hosts.put(h,  (HostState) c.computeResultingState(h));
				return true;
			}
		} catch (final IncompleteContractException e) {
			throw new RuntimeException();
		}
	}

	public boolean isCoherent(){
		for (ReplicaState s: agents.values()){
			for (ResourceIdentifier r : s.getMyResourceIdentifiers()){
				if (hosts.containsValue(r)){
					if (!getAccessibleHosts(s.getMyAgentIdentifier()).contains(r) ||
							!getAccessibleAgents(r).contains(s.getMyAgentIdentifier()) ||
							!hosts.get(r).hasResource(s.getMyAgentIdentifier())){
						return false;
					}
				}
			}
		}
		for (HostState s: hosts.values()){
			for (AgentIdentifier r : s.getMyResourceIdentifiers()){
				if (agents.containsValue(r)){
					if (!getAccessibleAgents(s.getMyAgentIdentifier()).contains(r) ||
							!getAccessibleHosts(r).contains(s.getMyAgentIdentifier()) || 
							!agents.get(r).hasResource(s.getMyAgentIdentifier())){
						return false;
					}
				}
			}
		}
		return true;
	}

	public boolean assertAllocValid(){

		for (ReplicaState s: agents.values()){
			assert s.isValid():s;
		}
		for (HostState s: hosts.values()){
			assert s.isValid();			
		}

		return true;
	}

	public boolean assertNeigborhoodValidity(){
		for (ReplicaState s: agents.values()){
			for (ResourceIdentifier r : s.getMyResourceIdentifiers()){
				assert getAccessibleHosts(s.getMyAgentIdentifier()).contains(r) :s.getMyAgentIdentifier()+" "+r;
				assert getAccessibleAgents(r).contains(s.getMyAgentIdentifier());
				assert hosts.get(r).hasResource(s.getMyAgentIdentifier());
			}
		}
		for (HostState s: hosts.values()){
			for (AgentIdentifier r : s.getMyResourceIdentifiers()){
				assert getAccessibleAgents(s.getMyAgentIdentifier()).contains(r) ;
				assert getAccessibleHosts(r).contains(s.getMyAgentIdentifier());
				assert agents.get(r).hasResource(s.getMyAgentIdentifier());
			}
		}
		return true;
	}

}

//int count = 5;
//boolean iFailed=false;
//do {
//	iFailed=false;
//	try {
//		p.clearParameters();
//	} catch (final IfailedException e) {
//		iFailed=true;
//		this.logWarning("I'v faileeeeeddddddddddddd RETRYINNNGGGGG "+e, LogService.onBoth);
//		count--;
//		if (count==0) {
//			throw e;
//		}
//	}
//}while(iFailed && count > 0);
//DistributionParameters<AgentIdentifier> agentCriticity;
//DistributionParameters<AgentIdentifier> agentProcessor;
//DistributionParameters<AgentIdentifier> agentMemory;
//
//
//DistributionParameters<ResourceIdentifier> hostProcCapacity;
//DistributionParameters<ResourceIdentifier> hostMemCapacity;
//
//
//DistributionParameters<ResourceIdentifier> fault;
//HostDisponibilityComputer dispos;//NON INSTANCIER
