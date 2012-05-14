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
import negotiation.negotiationframework.contracts.AbstractContractTransition.IncompleteContractException;
import negotiation.negotiationframework.contracts.MatchingCandidature;
import negotiation.negotiationframework.contracts.ResourceIdentifier;
import dima.basicagentcomponents.AgentIdentifier;
import dima.basicagentcomponents.AgentName;
import dima.introspectionbasedagents.services.BasicAgentModule;
import dima.introspectionbasedagents.shells.BasicCompetentAgent;
import dimaxx.experimentation.IfailedException;
import dimaxx.tools.distribution.DistributionParameters;
import dimaxx.tools.mappedcollections.HashedHashSet;

public class ReplicationInstanceGraph
extends BasicAgentModule<ReplicationLaborantin>{

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
	// Constructor
	//

	public ReplicationInstanceGraph(
			final ReplicationLaborantin ag, final ReplicationExperimentationParameters p)
					throws IfailedException {
		super(ag);
//		this.kAccessible = p.agentAccessiblePerHost;

	}

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
		return this.accHosts.get(id);
	}

	public Collection<AgentIdentifier> getAccessibleAgent(final ResourceIdentifier id){
		return this.accAgents.get(id);
	}

	//
	// Methods
	//

	void initiateAgents(final ReplicationExperimentationParameters p){

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

		final DistributionParameters<AgentIdentifier> agentCriticity = new DistributionParameters<AgentIdentifier>(
				this.agents.keySet(),
				p.agentCriticityMean,
				p.agentCriticityDispersion);
		final DistributionParameters<AgentIdentifier>  agentProcessor = new DistributionParameters<AgentIdentifier>(
				this.agents.keySet(),
				p.agentLoadMean,
				p.agentLoadDispersion);
		final DistributionParameters<AgentIdentifier> agentMemory = new DistributionParameters<AgentIdentifier>(
				this.agents.keySet(),
				p.agentLoadMean,
				p.agentLoadDispersion);
		final DistributionParameters<ResourceIdentifier> hostMemCapacity = new DistributionParameters<ResourceIdentifier>(
				this.hosts.keySet(),
				p.hostCapacityMean,
				p.hostCapacityDispersion);
		final DistributionParameters<ResourceIdentifier> hostProcCapacity = new DistributionParameters<ResourceIdentifier>(
				this.hosts.keySet(),
				p.hostCapacityMean,
				p.hostCapacityDispersion);
		final DistributionParameters<ResourceIdentifier> fault = new DistributionParameters<ResourceIdentifier>(
				this.hosts.keySet(),
				p.hostFaultProbabilityMean,
				p.hostDisponibilityDispersion);

		for (final AgentIdentifier id : this.agents.keySet()){
			this.agents.put(id,
					new ReplicaState(id,
							Math.min(ReplicationExperimentationParameters._criticityMin+ agentCriticity.get(id), 1),
							agentProcessor.get(id),
							agentMemory.get(id),
							p._socialWelfare));
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

	void setVoisinage(){
		this.accHosts =
				new HashedHashSet<AgentIdentifier, ResourceIdentifier>();
		this.accAgents =
				new HashedHashSet<ResourceIdentifier, AgentIdentifier>();

		if (getMyAgent().getSimulationParameters().completGraph){
			for (final AgentIdentifier agId : this.agents.keySet()){
				for (final ResourceIdentifier h : this.hosts.keySet()) {
					this.addAcquaintance(agId, h);
				}
			}
		}else{
			final List<ResourceIdentifier> hostsIdentifier = new ArrayList<ResourceIdentifier>(this.hosts.keySet());
			final List<AgentIdentifier> replicasIdentifier = new ArrayList<AgentIdentifier>(this.agents.keySet());

			Collections.shuffle(hostsIdentifier);
			Collections.shuffle(replicasIdentifier);
			Iterator<ResourceIdentifier> hotId = hostsIdentifier.iterator();
			for (final AgentIdentifier agId : replicasIdentifier){
				if (!hotId.hasNext()) {
					hotId = hostsIdentifier.iterator();
				}
				this.addAcquaintance(
						agId,
						hotId.next());
			}

			Collections.shuffle(hostsIdentifier);
			for (final ResourceIdentifier h : hostsIdentifier) {
				Collections.shuffle(replicasIdentifier);
				final Iterator<AgentIdentifier> agId = replicasIdentifier.iterator();
				/* Adding acquaintance for host within latence */
				while (this.getAccessibleAgent(h).size()<this.getMyAgent().getSimulationParameters().agentAccessiblePerHost) {
					this.addAcquaintance(agId.next(), h);
				}
			}
		}
		
	}

	void initialRep() throws IfailedException{
		//duplicating in case of failure
		final Map<AgentIdentifier, ReplicaState> agentsTemp =
				new HashMap<AgentIdentifier, ReplicaState>(this.agents);
		final Map<ResourceIdentifier, HostState> hostsTemp =
				new HashMap<ResourceIdentifier, HostState>(this.hosts);
		final List<AgentIdentifier> replicasIdentifier = new ArrayList<AgentIdentifier>(agentsTemp.keySet());
		Collections.shuffle(replicasIdentifier);

		final HashSet<AgentIdentifier> done = new HashSet<AgentIdentifier>();

		for (final AgentIdentifier agId : replicasIdentifier){
			final List<ResourceIdentifier> hostsIdentifier = new ArrayList<ResourceIdentifier>(this.getAccessibleHost(agId));
			Collections.shuffle(hostsIdentifier);
			final Iterator<ResourceIdentifier> itHost =
					hostsIdentifier.iterator();

			assert done.add(agId):agId+" "+done+" "+replicasIdentifier;
			assert itHost.hasNext():"no host? argh!"+agId+"\n";

			ResourceIdentifier firstReplicatedOnHost = itHost.next();
			while (!ReplicationInstanceGraph.allocateAgents(this.getMyAgent(),agId, firstReplicatedOnHost,agentsTemp,hostsTemp)) {
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

	}

	public static boolean allocateAgents(
			final BasicCompetentAgent caller,
			final AgentIdentifier r, final ResourceIdentifier h,
			final Map<AgentIdentifier, ReplicaState> agents,
			final Map<ResourceIdentifier, HostState> hosts){
		try {
			final ReplicationCandidature c = new ReplicationCandidature(
					h,
					r,
					true,true);
			c.setSpecification(agents.get(r));
			c.setSpecification(hosts.get(h));

			if (!c.computeResultingState(h).isValid() || !c.computeResultingState(r).isValid()) {
				return false;
			} else {
				agents.put(r, (ReplicaState) c.computeResultingState(r));
				hosts.put(h,  (HostState) c.computeResultingState(h));
				return true;
			}
		} catch (final IncompleteContractException e) {
			caller.signalException("impossible", e);
			throw new RuntimeException();
		}
	}

	//
	// Primitive
	//

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
