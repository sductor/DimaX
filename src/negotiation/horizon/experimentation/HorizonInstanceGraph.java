package negotiation.horizon.experimentation;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import negotiation.faulttolerance.experimentation.ReplicationExperimentationParameters;
import negotiation.faulttolerance.experimentation.ReplicationInstanceGraph;
import negotiation.faulttolerance.negotiatingagent.HostState;
import negotiation.faulttolerance.negotiatingagent.ReplicaState;
import negotiation.faulttolerance.negotiatingagent.ReplicationCandidature;
import negotiation.horizon.negociatingagent.SubstrateNodeIdentifier;
import negotiation.horizon.negociatingagent.SubstrateNodeState;
import negotiation.horizon.negociatingagent.VirtualNetworkIdentifier;
import negotiation.horizon.negociatingagent.VirtualNetworkState;
import negotiation.negotiationframework.contracts.MatchingCandidature;
import negotiation.negotiationframework.contracts.ResourceIdentifier;
import negotiation.negotiationframework.contracts.AbstractContractTransition.IncompleteContractException;
import dima.basicagentcomponents.AgentIdentifier;
import dima.introspectionbasedagents.services.BasicAgentModule;
import dima.introspectionbasedagents.shells.BasicCompetentAgent;
import dimaxx.experimentation.IfailedException;
import dimaxx.tools.distribution.DistributionParameters;
import dimaxx.tools.mappedcollections.HashedHashSet;

public class HorizonInstanceGraph extends BasicAgentModule<HorizonLaborantin> {

    /**
     * Serial version identifier.
     */
    private static final long serialVersionUID = 90055860658730149L;

    private final Map<VirtualNetworkIdentifier, VirtualNetworkState> agents = new HashMap<VirtualNetworkIdentifier, VirtualNetworkState>();
    private final Map<SubstrateNodeIdentifier, SubstrateNodeState> hosts = new HashMap<SubstrateNodeIdentifier, SubstrateNodeState>();

    private HashedHashSet<VirtualNetworkIdentifier, SubstrateNodeIdentifier> accHosts = new HashedHashSet<VirtualNetworkIdentifier, SubstrateNodeIdentifier>();
    private HashedHashSet<SubstrateNodeIdentifier, VirtualNetworkIdentifier> accAgents = new HashedHashSet<SubstrateNodeIdentifier, VirtualNetworkIdentifier>();

    public HorizonInstanceGraph(final HorizonLaborantin ag,
	    final HorizonExperimentationParameters p) throws IfailedException {
	super(ag);
    }

    public Collection<VirtualNetworkIdentifier> getVirtualNetworkIdentifiers() {
	return this.agents.keySet();
    }

    public Collection<SubstrateNodeIdentifier> getSubstrateNodeIdentifiers() {
	return this.hosts.keySet();
    }

    public Collection<VirtualNetworkState> getVirtualNetworkStates() {
	return this.agents.values();
    }

    public Collection<SubstrateNodeState> getSubstrateNodeStates() {
	return this.hosts.values();
    }

    public VirtualNetworkState getVirtualNetworkState(
	    final VirtualNetworkIdentifier id) {
	return this.agents.get(id);
    }

    public SubstrateNodeState getSubstrateNodeState(final ResourceIdentifier id) {
	return this.hosts.get(id);
    }

    public Collection<SubstrateNodeIdentifier> getInitialAllocation(
	    final VirtualNetworkIdentifier id) {
	return this.getVirtualNetworkState(id).getMyResourceIdentifiers();
    }

    public Collection<VirtualNetworkIdentifier> getInitialAllocation(
	    final ResourceIdentifier id) {
	return this.getSubstrateNodeState(id).getMyResourceIdentifiers();
    }

    public Collection<SubstrateNodeIdentifier> getAccessibleHost(
	    final VirtualNetworkIdentifier id) {
	return this.accHosts.get(id);
    }

    public Collection<VirtualNetworkIdentifier> getAccessibleAgent(
	    final ResourceIdentifier id) {
	return this.accAgents.get(id);
    }

    //
    // Methods
    //

    void initiateAgents(final HorizonExperimentationParameters p) {

	/*
	 * Agents and hosts names
	 */

	for (int i = 0; i < p.nbVirtualNetworks; i++) {
	    this.agents.put(new VirtualNetworkIdentifier("#"
		    + p.getSimulationName() + "#DomainAgent_" + i), null);
	}

	for (int i = 0; i < p.nbSubstrateNodes; i++) {
	    this.hosts.put(new SubstrateNodeIdentifier("#"
		    + p.getSimulationName() + "#HostManager_" + i, 77), null);
	}

	/*
	 * States
	 */
	// TODO rewrite under that

	final DistributionParameters<AgentIdentifier> agentCriticity = new DistributionParameters<AgentIdentifier>(
		this.agents.keySet(), p.agentCriticityMean,
		p.agentCriticityDispersion);
	final DistributionParameters<AgentIdentifier> agentProcessor = new DistributionParameters<AgentIdentifier>(
		this.agents.keySet(), p.agentLoadMean, p.agentLoadDispersion);
	final DistributionParameters<AgentIdentifier> agentMemory = new DistributionParameters<AgentIdentifier>(
		this.agents.keySet(), p.agentLoadMean, p.agentLoadDispersion);
	final DistributionParameters<ResourceIdentifier> hostMemCapacity = new DistributionParameters<ResourceIdentifier>(
		this.hosts.keySet(), p.hostCapacityMean,
		p.hostCapacityDispersion);
	final DistributionParameters<ResourceIdentifier> hostProcCapacity = new DistributionParameters<ResourceIdentifier>(
		this.hosts.keySet(), p.hostCapacityMean,
		p.hostCapacityDispersion);
	final DistributionParameters<ResourceIdentifier> fault = new DistributionParameters<ResourceIdentifier>(
		this.hosts.keySet(), p.hostFaultProbabilityMean,
		p.hostDisponibilityDispersion);

	for (final AgentIdentifier id : this.agents.keySet()) {
	    this.agents.put(id, new ReplicaState(id, Math.min(
		    ReplicationExperimentationParameters._criticityMin
			    + agentCriticity.get(id), 1), agentProcessor
		    .get(id), agentMemory.get(id), p._socialWelfare));
	}

	for (final ResourceIdentifier hostId : this.hosts.keySet()) {
	    this.hosts.put(hostId, new HostState(hostId, hostProcCapacity
		    .get(hostId), hostMemCapacity.get(hostId), fault
		    .get(hostId)));
	}
	// System.out.println(hosts.values());
    }

    void setVoisinage() {
	this.accHosts = new HashedHashSet<AgentIdentifier, ResourceIdentifier>();
	this.accAgents = new HashedHashSet<ResourceIdentifier, AgentIdentifier>();

	if (getMyAgent().getSimulationParameters().completGraph) {
	    for (final AgentIdentifier agId : this.agents.keySet()) {
		for (final ResourceIdentifier h : this.hosts.keySet()) {
		    this.addAcquaintance(agId, h);
		}
	    }
	} else {
	    final List<ResourceIdentifier> hostsIdentifier = new ArrayList<ResourceIdentifier>(
		    this.hosts.keySet());
	    final List<AgentIdentifier> replicasIdentifier = new ArrayList<AgentIdentifier>(
		    this.agents.keySet());

	    Collections.shuffle(hostsIdentifier);
	    Collections.shuffle(replicasIdentifier);
	    Iterator<ResourceIdentifier> hotId = hostsIdentifier.iterator();
	    for (final AgentIdentifier agId : replicasIdentifier) {
		if (!hotId.hasNext()) {
		    hotId = hostsIdentifier.iterator();
		}
		this.addAcquaintance(agId, hotId.next());
	    }

	    Collections.shuffle(hostsIdentifier);
	    for (final ResourceIdentifier h : hostsIdentifier) {
		Collections.shuffle(replicasIdentifier);
		final Iterator<AgentIdentifier> agId = replicasIdentifier
			.iterator();
		/* Adding acquaintance for host within latence */
		while (this.getAccessibleAgent(h).size() < this.getMyAgent()
			.getSimulationParameters().agentAccessiblePerHost) {
		    this.addAcquaintance(agId.next(), h);
		}
	    }
	}

    }

    void initialRep() throws IfailedException {
	// duplicating in case of failure
	final Map<AgentIdentifier, ReplicaState> agentsTemp = new HashMap<AgentIdentifier, ReplicaState>(
		this.agents);
	final Map<ResourceIdentifier, HostState> hostsTemp = new HashMap<ResourceIdentifier, HostState>(
		this.hosts);
	final List<AgentIdentifier> replicasIdentifier = new ArrayList<AgentIdentifier>(
		agentsTemp.keySet());
	Collections.shuffle(replicasIdentifier);

	final HashSet<AgentIdentifier> done = new HashSet<AgentIdentifier>();

	for (final AgentIdentifier agId : replicasIdentifier) {
	    final List<ResourceIdentifier> hostsIdentifier = new ArrayList<ResourceIdentifier>(
		    this.getAccessibleHost(agId));
	    Collections.shuffle(hostsIdentifier);
	    final Iterator<ResourceIdentifier> itHost = hostsIdentifier
		    .iterator();

	    assert done.add(agId) : agId + " " + done + " "
		    + replicasIdentifier;
	    assert itHost.hasNext() : "no host? argh!" + agId + "\n";

	    ResourceIdentifier firstReplicatedOnHost = itHost.next();
	    while (!ReplicationInstanceGraph.allocateAgents(this.getMyAgent(),
		    agId, firstReplicatedOnHost, agentsTemp, hostsTemp)) {
		if (!itHost.hasNext()) {
		    throw new IfailedException(
			    "can not create at least one rep for each agent\n");
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

    public static boolean allocateAgents(final BasicCompetentAgent caller,
	    final AgentIdentifier r, final ResourceIdentifier h,
	    final Map<AgentIdentifier, ReplicaState> agents,
	    final Map<ResourceIdentifier, HostState> hosts) {
	try {
	    final MatchingCandidature c = new ReplicationCandidature(h, r,
		    true, true);
	    c.setSpecification(agents.get(r));
	    c.setSpecification(hosts.get(h));

	    if (!c.computeResultingState(h).isValid()
		    || !c.computeResultingState(r).isValid()) {
		return false;
	    } else {
		agents.put(r, (ReplicaState) c.computeResultingState(r));
		hosts.put(h, (HostState) c.computeResultingState(h));
		return true;
	    }
	} catch (final IncompleteContractException e) {
	    caller.signalException("impossible", e);
	    throw new RuntimeException();
	}
    }
}
