package negotiation.faulttolerance.experimentation;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import negotiation.faulttolerance.negotiatingagent.HostState;
import negotiation.faulttolerance.negotiatingagent.ReplicaState;
import negotiation.faulttolerance.negotiatingagent.ReplicationCandidature;
import negotiation.negotiationframework.NegotiationParameters;
import negotiation.negotiationframework.contracts.InformedCandidature;
import negotiation.negotiationframework.contracts.MatchingCandidature;
import negotiation.negotiationframework.contracts.ResourceIdentifier;
import negotiation.negotiationframework.contracts.AbstractContractTransition.IncompleteContractException;
import negotiation.negotiationframework.rationality.SimpleRationalAgent;

import dima.basicagentcomponents.AgentIdentifier;
import dima.basicagentcomponents.AgentName;
import dima.introspectionbasedagents.services.BasicAgentModule;
import dima.introspectionbasedagents.services.loggingactivity.LogMonologue;
import dima.introspectionbasedagents.services.loggingactivity.LogService;
import dima.introspectionbasedagents.shells.BasicCompetentAgent;
import dimaxx.experimentation.ExperimentationParameters;
import dimaxx.experimentation.IfailedException;
import dimaxx.server.HostIdentifier;
import dimaxx.tools.distribution.DistributionParameters;
import dimaxx.tools.mappedcollections.HashedHashSet;

public class ReplicationInstanceGraph 
extends BasicAgentModule<ReplicationLaborantin>{

	private final int kAccessible;

	private Map<AgentIdentifier, ReplicaState> agents =
			new HashMap<AgentIdentifier, ReplicaState>();
	private Map<ResourceIdentifier, HostState> hosts =
			new HashMap<ResourceIdentifier, HostState>();

	private HashedHashSet<AgentIdentifier, ResourceIdentifier> accHosts =
			new HashedHashSet<AgentIdentifier, ResourceIdentifier>();
	private HashedHashSet<ResourceIdentifier, AgentIdentifier> accAgents = 
			new HashedHashSet<ResourceIdentifier, AgentIdentifier>();

	//
	// Constructor
	//

	public ReplicationInstanceGraph(
			ReplicationLaborantin ag, ReplicationExperimentationParameters p) 
					throws IfailedException {
		super(ag);
		this.kAccessible = p.agentAccessiblePerHost;

	}

	//
	// Getter
	//

	public Collection<AgentIdentifier> getAgentsIdentifier() {
		return agents.keySet();
	}

	public Collection<ResourceIdentifier> getHostsIdentifier() {
		return hosts.keySet();
	}

	public Collection<ReplicaState> getAgentStates() {
		return agents.values();
	}

	public Collection<HostState> getHostsStates() {
		return hosts.values();
	}
	public ReplicaState getAgentState(AgentIdentifier id){
		return agents.get(id);
	}

	public HostState getHostState(ResourceIdentifier id){
		return hosts.get(id);
	}

	public Collection<ResourceIdentifier> getInitialReplication(AgentIdentifier id){
		return getAgentState(id).getMyResourceIdentifiers();
	}

	public Collection<AgentIdentifier> getInitialReplication(ResourceIdentifier id){
		return getHostState(id).getMyResourceIdentifiers();
	}


	public Collection<ResourceIdentifier> getAccessibleHost(AgentIdentifier id){
		return accHosts.get(id);
	}

	public Collection<AgentIdentifier> getAccessibleAgent(ResourceIdentifier id){
		return accAgents.get(id);
	}

	//
	// Methods
	//

	void initiateAgents(ReplicationExperimentationParameters p){

		/*
		 * Agents and hosts names
		 */

		for (int i=0; i<p.nbAgents; i++) {
			this.agents.put(
					new AgentName("#"+p.getSimulationName()+"#DomainAgent_"+i),null);
		}

		for (int i=0; i<p.nbHosts; i++) {
			this.hosts.put(
					new ResourceIdentifier("#"+p.getSimulationName()+"#HostManager_"+i,77),null);
		}


		/*
		 * States
		 */

		DistributionParameters<AgentIdentifier> agentCriticity = new DistributionParameters<AgentIdentifier>(
				agents.keySet(),
				p.agentCriticityMean,
				p.agentCriticityDispersion);
		DistributionParameters<AgentIdentifier>  agentProcessor = new DistributionParameters<AgentIdentifier>(
				agents.keySet(), 
				p.agentLoadMean,
				p.agentLoadDispersion);
		DistributionParameters<AgentIdentifier> agentMemory = new DistributionParameters<AgentIdentifier>(
				agents.keySet(), 
				p.agentLoadMean,
				p.agentLoadDispersion);
		DistributionParameters<ResourceIdentifier> hostMemCapacity = new DistributionParameters<ResourceIdentifier>(
				hosts.keySet(), 
				p.hostCapacityMean,
				p.hostCapacityDispersion);
		DistributionParameters<ResourceIdentifier> hostProcCapacity = new DistributionParameters<ResourceIdentifier>(
				hosts.keySet(), 
				p.hostCapacityMean,
				p.hostCapacityDispersion);
		DistributionParameters<ResourceIdentifier> fault = new DistributionParameters<ResourceIdentifier>(
				hosts.keySet(),
				p.hostFaultProbabilityMean,
				p.hostDisponibilityDispersion);

		for (AgentIdentifier id : agents.keySet()){
			agents.put(id, 
					new ReplicaState(id, 
							Math.min(p._criticityMin+ agentCriticity.get(id), 1), 
							agentProcessor.get(id), 
							agentMemory.get(id),
							new HashSet(), 
							p._socialWelfare,
							-1));
		}

		for (ResourceIdentifier hostId : hosts.keySet()){
			hosts.put(hostId, 
					new HostState(hostId, 
							kAccessible * hostProcCapacity.get(hostId),
							kAccessible * hostMemCapacity.get(hostId),
							fault.get(hostId),
							-1));
		}
	}

	void setVoisinage(){
		accHosts =
				new HashedHashSet<AgentIdentifier, ResourceIdentifier>();
		accAgents = 
				new HashedHashSet<ResourceIdentifier, AgentIdentifier>();
		
		List<ResourceIdentifier> hostsIdentifier = new ArrayList<ResourceIdentifier>(hosts.keySet());
		List<AgentIdentifier> replicasIdentifier = new ArrayList<AgentIdentifier>(agents.keySet());

		Collections.shuffle(hostsIdentifier);
		Collections.shuffle(replicasIdentifier);
		Iterator<ResourceIdentifier> hotId = hostsIdentifier.iterator();
		for (AgentIdentifier agId : replicasIdentifier){
			if (!hotId.hasNext())
				hotId = hostsIdentifier.iterator();
			addAcquaintance(
					agId, 
					hotId.next());
		}

		Collections.shuffle(hostsIdentifier);
		for (ResourceIdentifier h : hostsIdentifier) {
			Collections.shuffle(replicasIdentifier);
			Iterator<AgentIdentifier> agId = replicasIdentifier.iterator();
			/* Adding acquaintance for host within latence */
			while (getAccessibleAgent(h).size()<this.kAccessible)
				addAcquaintance(agId.next(), h);
		}
	}

	void initialRep() throws IfailedException{
		//duplicating in case of failure
		Map<AgentIdentifier, ReplicaState> agentsTemp =
				new HashMap<AgentIdentifier, ReplicaState>(agents);
		Map<ResourceIdentifier, HostState> hostsTemp =
				new HashMap<ResourceIdentifier, HostState>(hosts);
		List<AgentIdentifier> replicasIdentifier = new ArrayList<AgentIdentifier>(agentsTemp.keySet());
		Collections.shuffle(replicasIdentifier);

		HashSet<AgentIdentifier> done = new HashSet<AgentIdentifier>();
		
		for (AgentIdentifier agId : replicasIdentifier){	
			List<ResourceIdentifier> hostsIdentifier = new ArrayList<ResourceIdentifier>(getAccessibleHost(agId));
			Collections.shuffle(hostsIdentifier);
			final Iterator<ResourceIdentifier> itHost =
					hostsIdentifier.iterator();		
			
			assert done.add(agId):agId+" "+done+" "+replicasIdentifier;
			assert itHost.hasNext():"no host? argh!"+agId+"\n";

			ResourceIdentifier firstReplicatedOnHost = itHost.next();
			while (!allocateAgents(getMyAgent(),agId, firstReplicatedOnHost,agentsTemp,hostsTemp)) {
				if (!itHost.hasNext()) {
					throw new IfailedException("can not create at least one rep for each agent\n");
				} else {
					firstReplicatedOnHost = itHost.next();
				}
			}
		}
		
		agents.clear();
		agents.putAll(agentsTemp);
		hosts.clear();
		hosts.putAll(hostsTemp);

	}

	public static boolean allocateAgents(
			BasicCompetentAgent caller,
			AgentIdentifier r, ResourceIdentifier h,
			Map<AgentIdentifier, ReplicaState> agents, 
			Map<ResourceIdentifier, HostState> hosts){
		try {	
			MatchingCandidature c = new ReplicationCandidature(
					h,
					r,
					true,true);
			c.setSpecification(agents.get(r));
			c.setSpecification(hosts.get(h));

			if (!c.computeResultingState(h).isValid() || !c.computeResultingState(r).isValid())
				return false;
			else {
				agents.put(r, (ReplicaState) c.computeResultingState(r));
				hosts.put(h,  (HostState) c.computeResultingState(h));
				return true;
			}
		} catch (IncompleteContractException e) {
			caller.signalException("impossible", e);
			throw new RuntimeException();
		}
	}

	//
	// Primitive
	//

	private void addAcquaintance(AgentIdentifier ag, ResourceIdentifier h){
		accHosts.add(ag,h);
		accAgents.add(h, ag);
		assert accAgents.get(h).size() <= kAccessible:accAgents;
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
