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
import dimaxx.tools.mappedcollections.SymmetricBinaryAdjacencyMap;
import dimaxx.tools.mappedcollections.UnorderedPair;

public class VirtualNetworkState extends SimpleAgentState {

    /**
     * Serial version identifier.
     */
    private static final long serialVersionUID = -5576314995630464103L;

    /**
     * Map of the VirtualNodeIdentifiers to retrieve the corresponding
     * VirtualNodes.
     */
    private final Map<VirtualNodeIdentifier, VirtualNode> nodes;

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
	    final List<LinkMeasurableParameters> linksPreferenceParams)
	    throws IllegalArgumentException {
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
	    idList
		    .add(this.getMyAgentIdentifier().new VirtualNodeIdentifier(
			    i));

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
	    InterfacesParameters<LinkAllocableParameters> ifacesAllocable = new InterfacesParameters<LinkAllocableParameters>();
	    InterfacesParameters<LinkMeasurableParameters> ifacesMeasurable = new InterfacesParameters<LinkMeasurableParameters>();

	    for (VirtualNodeIdentifier idPaired : linksMap.getPaired(id)) {
		ifacesAllocable.put(idPaired, linksMap.get(
			new UnorderedPair<VirtualNodeIdentifier>(id, idPaired))
			.getAllocableParams());
		ifacesMeasurable.put(idPaired, linksMap.get(
			new UnorderedPair<VirtualNodeIdentifier>(id, idPaired))
			.getMeasurableParams());
	    }
	    VirtualNode test = nodesMap.put(idList.get(i), new VirtualNode(
		    new NodeParameters(new HorizonAllocableParameters(
			    nodesRequiredParams.get(i), ifacesAllocable),
			    new HorizonMeasurableParameters(
				    nodesPreferenceParams.get(i),
				    ifacesMeasurable))));

	    assert (test == null);
	}

	this.nodes = Collections.unmodifiableMap(nodesMap);

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
	    final SubstrateNodeIdentifier newHost, final NodeParameters params) {
	super(initial.getMyAgentIdentifier(), initial.getStateCounter() + 1);
	assert (this.nodes.get(reallocatedNode).param.equals(params));
	assert (this.nodes.containsKey(reallocatedNode));

	// this.links = initial.links;

	Map<VirtualNodeIdentifier, VirtualNode> newNodesMap = new HashMap<VirtualNodeIdentifier, VirtualNode>(
		initial.nodes);

	newNodesMap.put(reallocatedNode, new VirtualNode(this.nodes
		.get(reallocatedNode), newHost));
	this.nodes = Collections.unmodifiableMap(newNodesMap);
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

    public NodeParameters getNodeParams(final VirtualNodeIdentifier id) {
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

    @Override
    public boolean isValid() {
	assert (!this.nodes.values().contains(null));
	for (VirtualNode node : this.nodes.values()) {
	    if (null == node.getMyHost()) {
		return false;
	    }
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
	private final NodeParameters param;

	/**
	 * Identifier of the SubstrateNode hosting this VirtualNode.
	 */
	private final SubstrateNodeIdentifier myHost;

	public VirtualNode(final NodeParameters param) {
	    this.param = param;
	    this.myHost = null;
	}

	public VirtualNode(final VirtualNode initial,
		final SubstrateNodeIdentifier newHost) {
	    this.param = initial.param;
	    this.myHost = newHost;
	}

	/**
	 * Returns the NodeParameters of this VirtualNode.
	 * 
	 * @return the value of the field param
	 */
	public NodeParameters getParam() {
	    return this.param;
	}

	/**
	 * Returns the current host of this VirtualNode.
	 * 
	 * @return the value of the field myhost
	 */
	public SubstrateNodeIdentifier getMyHost() {
	    return this.myHost;
	}
    }
}