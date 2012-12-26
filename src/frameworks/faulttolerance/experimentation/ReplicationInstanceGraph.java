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
import dima.introspectionbasedagents.kernel.PseudoRandom;
import dima.introspectionbasedagents.modules.distribution.DistributionParameters;
import dima.introspectionbasedagents.modules.distribution.NormalLaw.DispersionSymbolicValue;
import dima.introspectionbasedagents.modules.mappedcollections.HashedHashSet;
import dima.support.GimaObject;
import frameworks.experimentation.ExperimentationParameters;
import frameworks.experimentation.IfailedException;
import frameworks.faulttolerance.negotiatingagent.HostState;
import frameworks.faulttolerance.negotiatingagent.ReplicaState;
import frameworks.faulttolerance.negotiatingagent.ReplicationCandidature;
import frameworks.negotiation.contracts.AbstractContractTransition.IncompleteContractException;
import frameworks.negotiation.contracts.ResourceIdentifier;
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

	public void setAgents(final Collection<ReplicaState> collection) {
		this.agents =new HashMap<AgentIdentifier, ReplicaState>();
		for (final ReplicaState a : collection){
			this.agents.put(a.getMyAgentIdentifier(),a);
		}
	}

	public void setHosts(final Collection<HostState> hostsStates) {
		this.hosts =	new HashMap<ResourceIdentifier, HostState>();
		for (final HostState a : hostsStates){
			this.hosts.put(a.getMyAgentIdentifier(),a);
		}
	}

	public void setState(final AgentState s){
		assert s!=null;
		if (s instanceof HostState){
			this.hosts.put(((HostState)s).getMyAgentIdentifier(), (HostState)s);
		} else {
			assert s instanceof ReplicaState:s;
		this.agents.put(s.getMyAgentIdentifier(), (ReplicaState)s);
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
			assert a2 instanceof ResourceIdentifier;
			ag = a1;
			h = (ResourceIdentifier) a2;
		}
		this.accHosts.add(ag,h);
		this.accAgents.add(h, ag);
		//		assert this.accAgents.get(h).size() <= this.kAccessible:this.accAgents;
	}
	public AgentState getState(final AgentIdentifier id) {
		if (id instanceof ResourceIdentifier){
			return this.getHostState((ResourceIdentifier)id);
		} else {
			return this.getAgentState(id);
		}
	}
	public Collection<? extends AgentIdentifier> getAcquaintances(final AgentIdentifier id){
		if (id instanceof ResourceIdentifier){
			return this.getAccessibleAgents((ResourceIdentifier)id);
		} else {
			return this.getAccessibleHosts(id);
		}
		//		assert this.accAgents.get(h).size() <= this.kAccessible:this.accAgents;
	}

	public ReplicationInstanceGraph(final SocialChoiceType socialWelfare) {
		super();
		this.socialWelfare = socialWelfare;
	}
	@Override
	public SocialChoiceType getSocialWelfare() {
		return this.socialWelfare;
	}


	public Collection<AgentIdentifier> getEveryIdentifier() {
		final Collection<AgentIdentifier> result = new ArrayList<AgentIdentifier>();
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
	public boolean areLinked(final AgentIdentifier a1, final AgentIdentifier a2) {
		final AgentIdentifier ag;
		final ResourceIdentifier h;
		if (a1 instanceof ResourceIdentifier){
			assert !(a2 instanceof ResourceIdentifier);
			ag = a2;
			h = (ResourceIdentifier) a1;
		} else {
			assert a2 instanceof ResourceIdentifier;
			ag = a1;
			h = (ResourceIdentifier) a2;
		}
		assert this.getAccessibleAgents(h).contains(ag)==this.getAccessibleHosts(ag).contains(h);
		return this.getAccessibleAgents(h).contains(ag);
	}
	//
	// Methods
	//

	public void randomInitiaition(
			final String simulationName,final long randSeed,
			final int nbAgents, final int nbHosts,
			final Double agentCriticityMean,final DispersionSymbolicValue agentCriticityDispersion,
			final Double agentLoadMean,final DispersionSymbolicValue agentLoadDispersion,
			final Double hostCapacityMean,final DispersionSymbolicValue hostCapacityDispersion,
			final Double hostFaultProbabilityMean,final DispersionSymbolicValue hostDisponibilityDispersion,
			final int agentAccessiblePerHost, final int maxHostAccessiblePerAgent) throws IfailedException{
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
		final Collection<ReplicaState> repSt = new ArrayList<ReplicaState>();
		for (final ReplicaState r : this.getAgentStates()){
			final ReplicaState r2 = r.freeAllResources();
			repSt.add(r2);
		}

		final Collection<HostState> hostSt = new ArrayList<HostState>();
		for (final HostState r : this.getHostsStates()){
			final HostState r2 = r.freeAllResources();
			hostSt.add(r2);
		}
		final ReplicationInstanceGraph rig = new ReplicationInstanceGraph(this.getSocialWelfare());
		rig.setAgents(repSt);
		rig.setHosts(hostSt);

		rig.accAgents=this.accAgents;
		rig.accHosts=this.accHosts;

		return rig;
	}

	public static Integer identifierToInt(final AgentIdentifier id){
		return new Integer(id.toString().split("-=-")[1]);
	}

	public static AgentIdentifier intToIdentifier(
			final String simulationName, final int i){
		if (i%2==0) {
			return new AgentName("#"+simulationName+"#DomainAgent_-=-"+i+"-=-");
		} else {
			return new ResourceIdentifier("#"+simulationName+"#HostManager_-=-"+i+"-=-",77);
		}
	}
	public static boolean isRessource(final AgentIdentifier id){
		int i = identifierToInt(id);
		if (i%2==0) {
			return false;
		} else {
			return true;
		}
	}

	//
	// Primitive
	//

	@Override
	public String toString(){
		String result = "Agents :\n"+this.getAgentStates()+"\n Host :\n"+this.getHostsStates()+"\n";
		for (final AgentIdentifier id : this.getAgentsIdentifier()){
			result+="Agent "+id+" ----> "+this.getAccessibleHosts(id)+"\n";
		}
		for (final ResourceIdentifier id : this.getHostsIdentifier()){
			result+="Host  "+id+" ----> "+this.getAccessibleAgents(id)+"\n";
		}
		return result;
	}

	//
	// Internals
	//


	private void initiateAgents(
			final String simulationName,final Random rand,
			final int nbAgents, final int nbHosts,
			final Double agentCriticityMean,final DispersionSymbolicValue agentCriticityDispersion,
			final Double agentLoadMean,final DispersionSymbolicValue agentLoadDispersion,
			final Double hostCapacityMean,final DispersionSymbolicValue hostCapacityDispersion,
			final Double hostFaultProbabilityMean,final DispersionSymbolicValue hostDisponibilityDispersion){


		/*
		 * Agents and hosts names
		 */
		this.agents =new HashMap<AgentIdentifier, ReplicaState>();
		this.hosts =	new HashMap<ResourceIdentifier, HostState>();

		for (int i=0; i<2*nbAgents; i+=2) {
			this.agents.put(ReplicationInstanceGraph.intToIdentifier(simulationName,i),null);
		}

		for (int i=1; i<2*nbHosts+1; i+=2) {
			this.hosts.put((ResourceIdentifier) ReplicationInstanceGraph.intToIdentifier(simulationName,i),null);
		}


		/*
		 * States
		 */


	
		final DistributionParameters<ResourceIdentifier> hostMemCapacity = new DistributionParameters<ResourceIdentifier>(
				this.hosts.keySet(),
				hostCapacityMean,
				hostCapacityDispersion,new Random(rand.nextInt()), true);
		final DistributionParameters<ResourceIdentifier> hostProcCapacity = new DistributionParameters<ResourceIdentifier>(
				this.hosts.keySet(),
				hostCapacityMean,
				hostCapacityDispersion,new Random(rand.nextInt()), true);
		final DistributionParameters<ResourceIdentifier> fault = new DistributionParameters<ResourceIdentifier>(
				this.hosts.keySet(),
				hostFaultProbabilityMean,
				hostDisponibilityDispersion,new Random(rand.nextInt()),false);
		
		final DistributionParameters<AgentIdentifier> agentCriticity = new DistributionParameters<AgentIdentifier>(
				this.agents.keySet(),
				agentCriticityMean,
				agentCriticityDispersion,new Random(rand.nextInt()),false);
		final DistributionParameters<AgentIdentifier>  agentProcessor = new DistributionParameters<AgentIdentifier>(
				this.agents.keySet(),
				agentLoadMean,
				agentLoadDispersion,new Random(rand.nextInt()), true);	
		final DistributionParameters<AgentIdentifier> agentMemory = new DistributionParameters<AgentIdentifier>(
				this.agents.keySet(),
				agentLoadMean,
				agentLoadDispersion,new Random(rand.nextInt()), true);
		
		for (final AgentIdentifier id : this.agents.keySet()){
			this.agents.put(id,
					new ReplicaState(id,
							Math.min(ReplicationExperimentationParameters._criticityMin+ agentCriticity.get(id), 1),
							agentProcessor.get(id),
							agentMemory.get(id),
							this.socialWelfare));
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

	private void setVoisinage(final int agentAccessiblePerHost, final int maxHostAccessiblePerAgent,final Random rand){
		this.accHosts =
				new HashedHashSet<AgentIdentifier, ResourceIdentifier>();
		this.accAgents =
				new HashedHashSet<ResourceIdentifier, AgentIdentifier>();
		final boolean completGraph = agentAccessiblePerHost>=this.getAgentsIdentifier().size()&&maxHostAccessiblePerAgent>=this.getHostsIdentifier().size();
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
					final AgentIdentifier a = agId.next();
					if (this.getAccessibleHosts(a).size()<maxHostAccessiblePerAgent) {
						this.addAcquaintance(a, h);
					}
				}
			}
			throw new RuntimeException("mauvais reglage (ensembkle de la branche else)");

		}

	}

	private void initialRep(final Random rand) throws IfailedException{
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
			while (!this.allocateAgents(agId, firstReplicatedOnHost,agentsTemp,hostsTemp)) {
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
			assert this.getAccessibleAgents(h).contains(r);
			assert this.getAccessibleHosts(r).contains(h);
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
		for (final ReplicaState s: this.agents.values()){
			for (final ResourceIdentifier r : s.getMyResourceIdentifiers()){
				if (this.hosts.containsKey(r)){
					if (!this.getAccessibleHosts(s.getMyAgentIdentifier()).contains(r) ||
							!this.getAccessibleAgents(r).contains(s.getMyAgentIdentifier()) ||
							!this.hosts.get(r).hasResource(s.getMyAgentIdentifier())){
						return false;
					}
				}
			}
		}
		for (final HostState s: this.hosts.values()){
			for (final AgentIdentifier r : s.getMyResourceIdentifiers()){
				if (this.agents.containsKey(r)){
					if (!this.getAccessibleAgents(s.getMyAgentIdentifier()).contains(r) ||
							!this.getAccessibleHosts(r).contains(s.getMyAgentIdentifier()) ||
							!this.agents.get(r).hasResource(s.getMyAgentIdentifier())){
						return false;
					}
				}
			}
		}
		return true;
	}

	public boolean assertAllocValid(){

		for (final ReplicaState s: this.agents.values()){
			assert s.isValid():s;
		}
		for (final HostState s: this.hosts.values()){
			assert s.isValid();
		}

		return true;
	}

	public boolean assertNeigborhoodValidity(){
		for (final ReplicaState s: this.agents.values()){
			for (final ResourceIdentifier r : s.getMyResourceIdentifiers()){
				if (this.hosts.containsKey(r)){
					assert this.getAccessibleHosts(s.getMyAgentIdentifier()).contains(r) :s.getMyAgentIdentifier()+" "+r;
					assert this.getAccessibleAgents(r).contains(s.getMyAgentIdentifier());
					assert this.hosts.get(r).hasResource(s.getMyAgentIdentifier());
				}
			}
		}
		for (final HostState s: this.hosts.values()){
			for (final AgentIdentifier r : s.getMyResourceIdentifiers()){
				if (this.agents.containsKey(r)){
					assert this.getAccessibleAgents(s.getMyAgentIdentifier()).contains(r) ;
					assert this.getAccessibleHosts(r).contains(s.getMyAgentIdentifier());
					assert this.agents.get(r).hasResource(s.getMyAgentIdentifier());
				}
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
