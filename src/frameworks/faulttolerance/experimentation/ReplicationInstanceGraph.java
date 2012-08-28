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
import frameworks.negotiation.contracts.ResourceIdentifier;
import frameworks.negotiation.contracts.AbstractContractTransition.IncompleteContractException;
import frameworks.negotiation.rationality.SocialChoiceFunction.SocialChoiceType;

public class ReplicationInstanceGraph
extends GimaObject{

	/**
	 *
	 */
	private static final long serialVersionUID = -1869273198805540149L;

	//	private final int kAccessible;

	private final Map<AgentIdentifier, ReplicaState> agents =
			new HashMap<AgentIdentifier, ReplicaState>();
	private final Map<ResourceIdentifier, HostState> hosts =
			new HashMap<ResourceIdentifier, HostState>();

	private HashedHashSet<AgentIdentifier, ResourceIdentifier> accHosts =
			new HashedHashSet<AgentIdentifier, ResourceIdentifier>();
	private HashedHashSet<ResourceIdentifier, AgentIdentifier> accAgents =
			new HashedHashSet<ResourceIdentifier, AgentIdentifier>();

	
	//
	// Getter
	//

	public Collection<AgentIdentifier> getAgentsIdentifier() {
		return this.agents.keySet();
	}

	public Collection<ResourceIdentifier> getHostsIdentifier() {
		return this.hosts.keySet();
	}

	public Collection<ReplicaState> getAgentStates() {
		return this.agents.values();
	}

	public Collection<HostState> getHostsStates() {
		return this.hosts.values();
	}
	public ReplicaState getAgentState(final AgentIdentifier id){
		return this.agents.get(id);
	}

	public HostState getHostState(final ResourceIdentifier id){
		return this.hosts.get(id);
	}

	public Collection<ResourceIdentifier> getInitialReplication(final AgentIdentifier id){
		return this.getAgentState(id).getMyResourceIdentifiers();
	}

	public Collection<AgentIdentifier> getInitialReplication(final ResourceIdentifier id){
		return this.getHostState(id).getMyResourceIdentifiers();
	}


	public Collection<ResourceIdentifier> getAccessibleHost(final AgentIdentifier id){
		return Collections.unmodifiableSet(this.accHosts.get(id));
	}

	public Collection<AgentIdentifier> getAccessibleAgent(final ResourceIdentifier id){
		return Collections.unmodifiableSet(this.accAgents.get(id));
	}

	//
	// Methods
	//

	public void initiate(
			final String simulationName,long randSeed,
			final int nbAgents, int nbHosts,
			final Double agentCriticityMean,final DispersionSymbolicValue agentCriticityDispersion,
			final Double agentLoadMean,final DispersionSymbolicValue agentLoadDispersion,
			final Double hostCapacityMean,final DispersionSymbolicValue hostCapacityDispersion,
			final Double hostFaultProbabilityMean,final DispersionSymbolicValue hostDisponibilityDispersion, 
			SocialChoiceType _socialWelfare,
			boolean completGraph, int agentAccessiblePerHost) throws IfailedException{
		Random rand;
		rand = new Random(randSeed);
		this.initiateAgents(
				simulationName, rand,
				nbAgents, nbHosts, 
				agentCriticityMean, agentCriticityDispersion, 
				agentLoadMean, agentLoadDispersion, 
				hostCapacityMean, hostCapacityDispersion, hostFaultProbabilityMean, hostDisponibilityDispersion, 
				_socialWelfare);


//		this.logMonologue("Agents & Hosts:\n"+this.rig.getAgentStates()+"\n"+this.rig.getHostsStates(), LogService.onFile);

		int count = 5;
		boolean iFailed=false;
		do {
			try{
				iFailed=false;
				this.setVoisinage(completGraph, agentAccessiblePerHost, rand);
				this.initialRep(rand);
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
	

	//
	// Primitive
	//

	public String toString(){
		return "Agents :\n"+getAgentStates()+"\n"+accAgents+"\n Host :\n"+getHostsStates()+"\n"+accHosts;
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
			final Double hostFaultProbabilityMean,final DispersionSymbolicValue hostDisponibilityDispersion, 
			SocialChoiceType _socialWelfare){

		
		/*
		 * Agents and hosts names
		 */

		for (int i=0; i<nbAgents; i++) {
			this.agents.put(
					new AgentName("#"+simulationName+"#DomainAgent_-=-"+i+"-=-"),null);
		}

		for (int i=0; i<nbHosts; i++) {
			this.hosts.put(
					new ResourceIdentifier("#"+simulationName+"#HostManager_-=-"+i+"-=-",77),null);
		}


		/*
		 * States
		 */

		final DistributionParameters<AgentIdentifier> agentCriticity = new DistributionParameters<AgentIdentifier>(
				this.agents.keySet(),
				agentCriticityMean,
				agentCriticityDispersion,rand);
		final DistributionParameters<AgentIdentifier>  agentProcessor = new DistributionParameters<AgentIdentifier>(
				this.agents.keySet(),
				agentLoadMean,
				agentLoadDispersion,rand);
		final DistributionParameters<AgentIdentifier> agentMemory = new DistributionParameters<AgentIdentifier>(
				this.agents.keySet(),
			agentLoadMean,
				agentLoadDispersion,rand);
		final DistributionParameters<ResourceIdentifier> hostMemCapacity = new DistributionParameters<ResourceIdentifier>(
				this.hosts.keySet(),
				hostCapacityMean,
				hostCapacityDispersion,rand);
		final DistributionParameters<ResourceIdentifier> hostProcCapacity = new DistributionParameters<ResourceIdentifier>(
				this.hosts.keySet(),
				hostCapacityMean,
				hostCapacityDispersion,rand);
		final DistributionParameters<ResourceIdentifier> fault = new DistributionParameters<ResourceIdentifier>(
				this.hosts.keySet(),
				hostFaultProbabilityMean,
				hostDisponibilityDispersion,rand);

		for (final AgentIdentifier id : this.agents.keySet()){
			this.agents.put(id,
					new ReplicaState(id,
							Math.min(ReplicationExperimentationParameters._criticityMin+ agentCriticity.get(id), 1),
							agentProcessor.get(id),
							agentMemory.get(id),
							_socialWelfare));
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

	private void setVoisinage(boolean completGraph, int agentAccessiblePerHost,Random rand){
		this.accHosts =
				new HashedHashSet<AgentIdentifier, ResourceIdentifier>();
		this.accAgents =
				new HashedHashSet<ResourceIdentifier, AgentIdentifier>();

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
				while (this.getAccessibleAgent(h).size()<agentAccessiblePerHost) {
					this.addAcquaintance(agId.next(), h);
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
			final List<ResourceIdentifier> hostsIdentifier = new ArrayList<ResourceIdentifier>(this.getAccessibleHost(agId));
			Collections.shuffle(hostsIdentifier,rand);
			final Iterator<ResourceIdentifier> itHost =
					hostsIdentifier.iterator();

			assert done.add(agId):agId+" "+done+" "+replicasIdentifier;
			assert itHost.hasNext():"no host? argh!"+agId+"\n";

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

	private void addAcquaintance(final AgentIdentifier ag, final ResourceIdentifier h){
		this.accHosts.add(ag,h);
		this.accAgents.add(h, ag);
		//		assert this.accAgents.get(h).size() <= this.kAccessible:this.accAgents;
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
