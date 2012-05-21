package negotiation.horizon.negotiatingagent;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import jtp.util.UnexpectedException;
import negotiation.horizon.EmptyIntervalException;
import negotiation.horizon.Interval;
import negotiation.horizon.negotiatingagent.HorizonPreferenceFunction.Service;
import negotiation.horizon.negotiatingagent.VirtualNetworkIdentifier.VirtualNodeIdentifier;
import negotiation.horizon.parameters.HorizonAllocableParameters;
import negotiation.horizon.parameters.HorizonMeasurableParameters;
import negotiation.horizon.parameters.InterfacesParameters;
import negotiation.horizon.parameters.LinkAllocableParameters;
import negotiation.horizon.parameters.LinkMeasurableParameters;
import negotiation.horizon.parameters.MachineAllocableParameters;
import negotiation.horizon.parameters.MachineMeasurableParameters;
import negotiation.horizon.parameters.NetworkLinkParameters;
import negotiation.horizon.parameters.NodeParameters;
import negotiation.negotiationframework.contracts.ResourceIdentifier;
import negotiation.negotiationframework.rationality.SimpleAgentState;
import dima.introspectionbasedagents.services.information.ObservationService.Information;
import dima.support.GimaObject;
import dimaxx.tools.aggregator.AbstractCompensativeAggregation;
import dimaxx.tools.aggregator.HeavyDoubleAggregation;
import dimaxx.tools.mappedcollections.SymmetricBinaryAdjacencyMap;
import dimaxx.tools.mappedcollections.UnorderedPair;

public class VirtualNetworkState extends SimpleAgentState implements
	Comparable<VirtualNetworkState> {

    /**
     * Serial version identifier.
     */
    private static final long serialVersionUID = -5576314995630464103L;

    /**
     * Map of the VirtualNodeIdentifiers to retrieve the corresponding
     * VirtualNodes.
     */
    private final Map<VirtualNodeIdentifier, VirtualNode> nodes;

    private final Service serviceName;

    // /**
    // * Represents the links between all the VirtualNodes and their parameters.
    // */
    // XXX Note : a priori inutilisé
    // private Map<Set<VirtualNodeIdentifier>, LinkAllocableParameters> links;

    /**
     * TODO Doc... (links est une liste de tableaux à deux éléments : les
     * indices des nœuds dans nodesRequiredParams et nodesPreferenceParams)
     * 
     */
    public VirtualNetworkState(final VirtualNetworkIdentifier myAgent,
	    final int stateNumber,
	    final List<MachineAllocableParameters> nodesRequiredParams,
	    final List<MachineMeasurableParameters> nodesPreferenceParams,
	    final List<UnorderedPair<Integer>> links,
	    final List<LinkAllocableParameters> linksRequiredParams,
	    final List<LinkMeasurableParameters> linksPreferenceParams,
	    final Service myPreferredQoS) throws IllegalArgumentException {
	super(myAgent, stateNumber);

	if (links.size() != linksRequiredParams.size()
		|| links.size() != linksPreferenceParams.size()) {
	    throw new IllegalArgumentException(
		    "Numbers of links and parameters are not equal. Please refer to the documentation.");
	}

	final int nbnodes = nodesPreferenceParams.size();
	if (nodesRequiredParams.size() != nbnodes) {
	    throw new IllegalArgumentException(
		    "Numbers of nodes is ambiguous. Please refer to the documentation.");
	}

	final List<VirtualNodeIdentifier> idList = new ArrayList<VirtualNodeIdentifier>(
		nbnodes);
	for (int i = 0; i < nbnodes; i++)
	    idList.add(this.getMyAgentIdentifier().new VirtualNodeIdentifier());

	final SymmetricBinaryAdjacencyMap<VirtualNodeIdentifier, NetworkLinkParameters> linksMap = new SymmetricBinaryAdjacencyMap<VirtualNodeIdentifier, NetworkLinkParameters>();

	Iterator<LinkAllocableParameters> itLinksReq = linksRequiredParams
		.iterator();
	Iterator<LinkMeasurableParameters> itLinksPref = linksPreferenceParams
		.iterator();
	for (UnorderedPair<Integer> link : links) {
	    assert (itLinksReq.hasNext() && itLinksPref.hasNext());

	    if (link.getFirst() < 0 || link.getSecond() >= nbnodes) {
		throw new IllegalArgumentException(
			"Cannot link together unexisting nodes. Please refer to the documentation.");
	    }

	    NetworkLinkParameters test = linksMap.put(
		    new UnorderedPair<VirtualNodeIdentifier>(idList.get(link
			    .getFirst()), idList.get(link.getSecond())),
		    new NetworkLinkParameters(itLinksReq.next(), itLinksPref
			    .next()));

	    assert (test == null);
	}

	Map<VirtualNodeIdentifier, VirtualNode> nodesMap = new HashMap<VirtualNodeIdentifier, VirtualNode>();

	for (int i = 0; i < nbnodes; i++) {
	    VirtualNodeIdentifier id = idList.get(i);
	    InterfacesParameters<VirtualNodeIdentifier, LinkAllocableParameters> ifacesAllocable = new InterfacesParameters<VirtualNodeIdentifier, LinkAllocableParameters>();
	    InterfacesParameters<VirtualNodeIdentifier, LinkMeasurableParameters> ifacesMeasurable = new InterfacesParameters<VirtualNodeIdentifier, LinkMeasurableParameters>();

	    for (VirtualNodeIdentifier idPaired : linksMap.getPaired(id)) {
		ifacesAllocable.put(idPaired, linksMap.get(
			new UnorderedPair<VirtualNodeIdentifier>(id, idPaired))
			.getAllocableParams());
		ifacesMeasurable.put(idPaired, linksMap.get(
			new UnorderedPair<VirtualNodeIdentifier>(id, idPaired))
			.getMeasurableParams());
	    }
	    VirtualNode test = nodesMap
		    .put(
			    idList.get(i),
			    new VirtualNode(
				    new NodeParameters<VirtualNodeIdentifier>(
					    new HorizonAllocableParameters<VirtualNodeIdentifier>(
						    nodesRequiredParams.get(i),
						    ifacesAllocable),
					    new HorizonMeasurableParameters<VirtualNodeIdentifier>(
						    nodesPreferenceParams
							    .get(i),
						    ifacesMeasurable))));

	    assert (test == null);
	}

	this.nodes = Collections.unmodifiableMap(nodesMap);
	this.serviceName = myPreferredQoS;

	// XXX Note : a priori inutilisé
	// this.links = Collections.unmodifiableMap(linksMap);
    }

    /**
     * Constructs a new VirtualNetworkState by applying changes to initial.
     * 
     * @param initial
     *            starting (previous) VirtualNetworkState
     * @param reallocatedNode
     *            VirtualNode whose host changes
     * @param newHost
     *            the identifier of the new SubstrateNode
     * @param params
     *            parameters of the node for verification
     */
    public VirtualNetworkState(final VirtualNetworkState initial,
	    final VirtualNodeIdentifier reallocatedNode,
	    final SubstrateNodeIdentifier newHost,
	    final HorizonMeasurableParameters<SubstrateNodeIdentifier> params) {
	super(initial.getMyAgentIdentifier(), initial.getStateCounter() + 1);
	assert (this.nodes.get(reallocatedNode).param.equals(params));
	assert (this.nodes.containsKey(reallocatedNode));

	// this.links = initial.links;

	Map<VirtualNodeIdentifier, VirtualNode> newNodesMap = new HashMap<VirtualNodeIdentifier, VirtualNode>(
		initial.nodes);

	newNodesMap.put(reallocatedNode, new VirtualNode(this.nodes
		.get(reallocatedNode), newHost, params));
	this.nodes = Collections.unmodifiableMap(newNodesMap);
	this.serviceName = initial.serviceName;
    }

    // public VirtualNetworkState(final VirtualNetworkState initial,
    // final List<VirtualNodeIdentifier> reallocatedNodes,
    // final List<SubstrateNodeIdentifier> newHosts) {
    // super(initial.getMyAgentIdentifier(), initial.getStateCounter() + 1);
    //
    // assert (reallocatedNodes.size() == newHosts.size());
    //
    // this.nodes = new HashMap<VirtualNodeIdentifier, VirtualNode>(
    // initial.nodes);
    // this.links = initial.links;
    //
    // Iterator<VirtualNodeIdentifier> itReallocatedNodes = reallocatedNodes
    // .iterator();
    // Iterator<SubstrateNodeIdentifier> itNewHosts = newHosts.iterator();
    //
    // while (itReallocatedNodes.hasNext()) {
    // assert (itNewHosts.hasNext());
    //
    // final VirtualNodeIdentifier reallocatedNode = itReallocatedNodes
    // .next();
    // this.nodes.put(reallocatedNode, new VirtualNode(this.nodes
    // .get(reallocatedNode), itNewHosts.next()));
    // }
    // }

    // Incomplet pour vérifier la connexité -> faire des méthodes dans
    // ReflexiveAdjacencyMap
    // private boolean networkConsistentAndConnected() {
    // return this.nodes.keySet().equals(this.links.keySet());
    // }

    public NodeParameters<VirtualNodeIdentifier> getNodeParams(
	    final VirtualNodeIdentifier id) {
	return this.nodes.get(id).getParam();
    }

    // /**
    // * @return the Set of the VirtualNodeStates of the VirtualNetwork
    // */
    // public Set<VirtualNodeState> getNodes() {
    // return this.nodelist;
    // }

    // /**
    // * @return the {@link ReflexiveAdjacencyMap} representing the links
    // between
    // * nodes.
    // */
    // public ReflexiveAdjacencyMap<VirtualNodeState> getLinks() {
    // return this.links;
    // }

    // /**
    // * @return the Set of the AgentIdentifiers of the VirtualNodes of the
    // * VirtualNetwork
    // */
    // public Set<AgentIdentifier> getNodesID() {
    // Iterator<VirtualNodeState> it = this.nodelist.iterator();
    // Set<AgentIdentifier> nodesID = new HashSet<AgentIdentifier>();
    // while (it.hasNext())
    // nodesID.add(it.next().getMyAgentIdentifier());
    // return nodesID;
    // // Efficacité ! Mémoizer le résultat ou créer une nouvelle classe
    // // pour nodelist avec les identifiers
    // }

    @Override
    public VirtualNetworkIdentifier getMyAgentIdentifier() {
	return (VirtualNetworkIdentifier) super.getMyAgentIdentifier();
    }

    @Override
    public Set<SubstrateNodeIdentifier> getMyResourceIdentifiers() {
	assert (!this.nodes.values().contains(null));
	Set<SubstrateNodeIdentifier> myResIds = new HashSet<SubstrateNodeIdentifier>();
	for (VirtualNode node : this.nodes.values()) {
	    myResIds.add(node.getMyHost());
	}
	return myResIds;
    }

    /**
     * Resources of the VirtualNetwork are SubstrateNodes.
     * 
     * @return the type of the State of the Resources
     */
    @Override
    public Class<? extends Information> getMyResourcesClass() {
	return SubstrateNodeState.class;
    }

    @Override
    public boolean setLost(ResourceIdentifier h, boolean isLost) {
	throw new UnsupportedOperationException();
    }

    @Override
    public Double getNumericValue(Information e) {
	throw new UnsupportedOperationException();
    }

    @Override
    public AbstractCompensativeAggregation<Information> fuse(
	    Collection<? extends AbstractCompensativeAggregation<? extends Information>> averages) {
	throw new UnsupportedOperationException();
    }

    @Override
    public Information getRepresentativeElement(
	    Collection<? extends Information> elems) {
	throw new UnsupportedOperationException();
    }

    @Override
    public Information getRepresentativeElement(
	    Map<? extends Information, Double> elems) {
	throw new UnsupportedOperationException();
    }

    /**
     * Tests the validity of this VirtualNetworkState according to the rights of
     * the agent.
     * 
     * @return <code>true</code> if this State satisfies the rights of the agent
     *         (SLA).
     */
    @Override
    public boolean isValid() {
	// The map associating VirtualNodesIdentifiers to VirtualNode has no
	// reason to contain null values.
	assert (!this.nodes.values().contains(null));

	for (VirtualNode node : this.nodes.values()) {
	    if (!node.isValid())
		return false;

	}
	return true;
    }

    /**
     * Represents a VirtualNode taking part of a VirtualNetwork.
     * 
     * @author Vincent Letard
     */
    private class VirtualNode extends GimaObject {

	/**
	 * Serial version identifier
	 */
	private static final long serialVersionUID = 6804972846662200779L;

	/**
	 * Parameters levels requested by the SLA.
	 */
	private final NodeParameters<VirtualNodeIdentifier> param;

	/**
	 * Parameters provided by the current host.
	 */
	private final HorizonMeasurableParameters<SubstrateNodeIdentifier> currentAllocation;

	/**
	 * Identifier of the SubstrateNode hosting this VirtualNode.
	 */
	private final SubstrateNodeIdentifier myHost;

	public VirtualNode(final NodeParameters<VirtualNodeIdentifier> param) {
	    this.param = param;
	    this.myHost = null;
	    this.currentAllocation = null;
	}

	public boolean isValid() {
	    if (this.myHost == null)
		return false;
	    for (Map.Entry<VirtualNodeIdentifier, LinkMeasurableParameters> link : this.param
		    .getMeasurableParams().getInterfacesParameters().entrySet()) {
		if (!this.currentAllocation.getInterfacesParameters()
			.get(
				VirtualNetworkState.this.nodes.get(link
					.getKey()).myHost).satisfies(
				link.getValue()))
		    return false;
	    }
	    return true;
	}

	public VirtualNode(
		final VirtualNode initial,
		final SubstrateNodeIdentifier newHost,
		final HorizonMeasurableParameters<SubstrateNodeIdentifier> params) {
	    this.param = initial.param;
	    this.myHost = newHost;
	    this.currentAllocation = params;
	}

	/**
	 * Returns the NodeParameters of this VirtualNode.
	 * 
	 * @return the value of the field param
	 */
	public NodeParameters<VirtualNodeIdentifier> getParam() {
	    return this.param;
	}

	public HorizonMeasurableParameters<SubstrateNodeIdentifier> getCurrentAllocation()
		throws NodeNotInstanciatedException {
	    assert ((this.myHost == null) == (this.currentAllocation == null));
	    if (this.myHost == null)
		throw new NodeNotInstanciatedException();
	    else
		return this.currentAllocation;
	}

	/**
	 * Returns the current host of this VirtualNode.
	 * 
	 * @return the value of the field myhost
	 */
	public SubstrateNodeIdentifier getMyHost() {
	    return this.myHost;
	}

	public Double evaluatePacketLossRateUtility() {
	    final HeavyDoubleAggregation aggreg = new HeavyDoubleAggregation();

	    for (Map.Entry<VirtualNodeIdentifier, LinkMeasurableParameters> demanded : this.param
		    .getMeasurableParams().getInterfacesParameters().entrySet()) {

		Interval<Float> obtainedPacketLossRate = this.currentAllocation
			.getInterfacesParameters().get(
				VirtualNetworkState.this.nodes.get(demanded
					.getKey()).myHost).getPacketLossRate();
		Interval<Float> demandedPacketLossRate = Interval.inter(
			obtainedPacketLossRate, demanded.getValue()
				.getPacketLossRate());
		Double packetLossRateEval;
		try {
		    packetLossRateEval = 1. / (obtainedPacketLossRate
			    .getLower() + 1);
		} catch (final EmptyIntervalException e) {
		    throw new UnexpectedException(e);
		}
		try {
		    packetLossRateEval /= (demandedPacketLossRate.getUpper()
			    - demandedPacketLossRate.getLower() + 1);
		} catch (EmptyIntervalException e) {
		    packetLossRateEval *= 1;
		}
		aggreg.add(packetLossRateEval);
	    }
	    return aggreg.getMediane();
	}

	public Double evaluateDelayUtility() {
	    final HeavyDoubleAggregation aggreg = new HeavyDoubleAggregation();

	    for (Map.Entry<VirtualNodeIdentifier, LinkMeasurableParameters> demanded : this.param
		    .getMeasurableParams().getInterfacesParameters().entrySet()) {

		Interval<Integer> obtainedDelay = this.currentAllocation
			.getInterfacesParameters().get(
				VirtualNetworkState.this.nodes.get(demanded
					.getKey()).myHost).getDelay();
		Interval<Integer> satisfyingDelay = Interval.inter(
			obtainedDelay, demanded.getValue().getDelay());
		Double delayEval;
		try {
		    delayEval = 1. / (obtainedDelay.getLower() + 1);
		} catch (final EmptyIntervalException e) {
		    throw new UnexpectedException(e);
		}
		try {
		    delayEval /= (satisfyingDelay.getUpper()
			    - satisfyingDelay.getLower() + 1);
		} catch (EmptyIntervalException e) {
		    delayEval *= 1;
		}
		aggreg.add(delayEval);
	    }

	    return aggreg.getMediane();
	}

	public Double evaluateJitterUtility() {
	    HeavyDoubleAggregation aggreg = new HeavyDoubleAggregation();

	    for (Map.Entry<VirtualNodeIdentifier, LinkMeasurableParameters> demanded : this.param
		    .getMeasurableParams().getInterfacesParameters().entrySet()) {

		Interval<Integer> obtainedJitter = this.currentAllocation
			.getInterfacesParameters().get(
				VirtualNetworkState.this.nodes.get(demanded
					.getKey()).myHost).getJitter();
		Interval<Integer> satisfyingJitter = Interval.inter(
			obtainedJitter, demanded.getValue().getJitter());
		Double jitterEval;
		try {
		    jitterEval = 1. / (obtainedJitter.getLower() + 1);
		} catch (final EmptyIntervalException e) {
		    throw new UnexpectedException(e);
		}
		try {
		    jitterEval /= (satisfyingJitter.getUpper()
			    - satisfyingJitter.getLower() + 1);
		} catch (EmptyIntervalException e) {
		    jitterEval *= 1;
		}
		aggreg.add(jitterEval);
	    }
	    return aggreg.getMediane();
	}

	public Double evaluateAvailabilityUtility() {
	    Interval<Integer> obtainedAvailability = this.currentAllocation
		    .getMachineParameters().getAvailability();
	    Interval<Integer> satisfyingAvailability = Interval.inter(
		    obtainedAvailability, this.param.getMeasurableParams()
			    .getMachineParameters().getAvailability());
	    Double availabilityEval;
	    try {
		availabilityEval = 1. - (1. / (obtainedAvailability.getUpper() + 1));
	    } catch (final EmptyIntervalException e) {
		throw new UnexpectedException(e);
	    }
	    try {
		availabilityEval /= (satisfyingAvailability.getUpper()
			- satisfyingAvailability.getLower() + 1);
	    } catch (EmptyIntervalException e) {
		availabilityEval *= 1;
	    }
	    return availabilityEval;
	}

	public Double evaluateUtility() {
	    final HeavyDoubleAggregation aggreg = new HeavyDoubleAggregation();
	    final Service serviceType = VirtualNetworkState.this.serviceName;
	    aggreg.add(this.evaluatePacketLossRateUtility()
		    / serviceType.getPriority(1));
	    aggreg
		    .add(this.evaluateDelayUtility()
			    / serviceType.getPriority(2));
	    aggreg.add(this.evaluateJitterUtility()
		    / serviceType.getPriority(3));
	    aggreg.add(this.evaluateAvailabilityUtility()
		    / serviceType.getPriority(4));
	    return aggreg.getMediane();
	}
    }

    public class NodeNotInstanciatedException extends Exception {

	/**
	 * Serial version identifier.
	 */
	private static final long serialVersionUID = 3482865179834602964L;

    }

    public Service getQoS() {
	return this.serviceName;
    }

    public List<HorizonMeasurableParameters<VirtualNodeIdentifier>> getNodesPreferences() {
	List<HorizonMeasurableParameters<VirtualNodeIdentifier>> nodesPrefs = new ArrayList<HorizonMeasurableParameters<VirtualNodeIdentifier>>();
	for (VirtualNode node : this.nodes.values()) {
	    nodesPrefs.add(node.getParam().getMeasurableParams());
	}
	return nodesPrefs;
    }

    public List<HorizonMeasurableParameters<SubstrateNodeIdentifier>> getNodesCurrentService()
	    throws NodeNotInstanciatedException {
	List<HorizonMeasurableParameters<SubstrateNodeIdentifier>> nodesPrefs = new ArrayList<HorizonMeasurableParameters<SubstrateNodeIdentifier>>();
	for (VirtualNode node : this.nodes.values()) {
	    nodesPrefs.add(node.getCurrentAllocation());
	}
	return nodesPrefs;
    }

    public int getNodesCount() {
	return this.nodes.size();
    }

    public Double evaluateUtility() {
	final HeavyDoubleAggregation aggregation = new HeavyDoubleAggregation();
	for (VirtualNode node : this.nodes.values()) {
	    aggregation.add(node.evaluateUtility());
	}
	return aggregation.getMediane();
    }

    public int compareTo(final VirtualNetworkState state) {
	HeavyDoubleAggregation[][] aggregs = {
		{ new HeavyDoubleAggregation(), new HeavyDoubleAggregation(),
			new HeavyDoubleAggregation(),
			new HeavyDoubleAggregation() },
		{ new HeavyDoubleAggregation(), new HeavyDoubleAggregation(),
			new HeavyDoubleAggregation(),
			new HeavyDoubleAggregation() } };

	for (VirtualNode node : this.nodes.values()) {
	    aggregs[0][0].add(node.evaluatePacketLossRateUtility());
	    aggregs[0][1].add(node.evaluateDelayUtility());
	    aggregs[0][2].add(node.evaluateJitterUtility());
	    aggregs[0][3].add(node.evaluateAvailabilityUtility());
	}

	for (VirtualNode node : state.nodes.values()) {
	    aggregs[1][0].add(node.evaluatePacketLossRateUtility());
	    aggregs[1][1].add(node.evaluateDelayUtility());
	    aggregs[1][2].add(node.evaluateJitterUtility());
	    aggregs[1][3].add(node.evaluateAvailabilityUtility());
	}

	List<Integer> comparedValues = new ArrayList<Integer>();
	comparedValues.add(aggregs[0][0].getMediane().compareTo(
		aggregs[1][0].getMediane()));
	comparedValues.add(aggregs[0][1].getMediane().compareTo(
		aggregs[1][1].getMediane()));
	comparedValues.add(aggregs[0][2].getMediane().compareTo(
		aggregs[1][2].getMediane()));
	comparedValues.add(aggregs[0][3].getMediane().compareTo(
		aggregs[1][3].getMediane()));
	for (int value : this.serviceName.sort(comparedValues)) {
	    if (value != 0)
		return value;
	}
	return 0;
    }
}