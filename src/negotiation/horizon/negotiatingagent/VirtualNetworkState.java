package negotiation.horizon.negotiatingagent;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import negotiation.horizon.negotiatingagent.VirtualNetworkIdentifier.VirtualNodeIdentifier;
import negotiation.negotiationframework.contracts.ResourceIdentifier;
import negotiation.negotiationframework.rationality.SimpleAgentState;
import dima.introspectionbasedagents.services.information.ObservationService.Information;
import dimaxx.tools.aggregator.AbstractCompensativeAggregation;
import dimaxx.tools.aggregator.UtilitaristAnalyser;
import dimaxx.tools.mappedcollections.ReflexiveBinaryAdjacencyMap;

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

    /**
     * Represents the links between all the VirtualNodes and their parameters.
     */
    private Map<Set<VirtualNodeIdentifier>, LinkParameters> links;

    public VirtualNetworkState(final VirtualNetworkIdentifier myAgent,
	    final int stateNumber, final List<NodeParameters> nodesParams,
	    final List<Integer[]> links, final List<LinkParameters> linksParams)
	    throws IllegalArgumentException {
	super(myAgent, stateNumber);

	if (links.size() != linksParams.size()) {
	    throw new IllegalArgumentException(
		    "Numbers of links and parameters are not equal. Please refer to the documentation.");
	}

	// int s = nodesParams.size();
	// if (links.size() > (s * (s + 1)) / 2 + s)
	// throw new RuntimeException("Invalid argument");
	// int id = 0;
	// for (SingleNodeParameters node:nodes){
	// map.put(new VirtualNode(myAgent, id, node), null);
	// id++;
	// }

	Map<VirtualNodeIdentifier, VirtualNode> nodesMap = new HashMap<VirtualNodeIdentifier, VirtualNode>();

	// Temporary list for retrieving the VirtualNodeIdentifiers as building
	// the ReflexiveAdjacencyMap of links.
	List<VirtualNodeIdentifier> orderedVNList = new LinkedList<VirtualNodeIdentifier>();

	int i = 0;
	for (HorizonParameters<VirtualNodeIdentifier> param : nodesParams) {
	    VirtualNodeIdentifier newVNId = this.getMyAgentIdentifier().new VirtualNodeIdentifier(
		    i);
	    nodesMap.put(newVNId, new VirtualNode(param, null));
	    orderedVNList.add(newVNId);

	    i++;
	}

	final ReflexiveBinaryAdjacencyMap<VirtualNodeIdentifier, LinkParameters> linksMap = new ReflexiveBinaryAdjacencyMap<VirtualNodeIdentifier, LinkParameters>();

	Iterator<Integer[]> itLinks = links.iterator();
	Iterator<LinkParameters> itLinksParams = linksParams.iterator();
	while (itLinks.hasNext()) {
	    assert (itLinksParams.hasNext());

	    final Integer[] link = itLinks.next();
	    final LinkParameters param = itLinksParams.next();

	    if (2 != link.length) {
		throw new IllegalArgumentException(
			"Only binary links between nodes are supported. Please refer to the documentation.");
	    } else if (link[0] < 0 || link[0] >= nodesParams.size()
		    || link[1] < 0 || link[1] >= nodesParams.size()) {
		throw new IllegalArgumentException(
			"Cannot link together unexisting nodes. Please refer to the documentation.");
	    }
	    Set<VirtualNodeIdentifier> keySet = new HashSet<VirtualNodeIdentifier>();
	    keySet.add(orderedVNList.get(link[0]));
	    keySet.add(orderedVNList.get(link[1]));
	    LinkParameters test = linksMap.put(keySet, param);

	    assert (test == null);
	}

	this.nodes = Collections.unmodifiableMap(nodesMap);
	this.links = Collections.unmodifiableMap(linksMap);
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
	    final HorizonParameters<HorizonIdentifier> params) {
	super(initial.getMyAgentIdentifier(), initial.getStateCounter() + 1);
	assert (this.nodes.get(reallocatedNode).param.equals(params));
	assert (this.nodes.containsKey(reallocatedNode));

	this.links = initial.links;

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
    private boolean networkConsistentAndConnected() {
	return this.nodes.keySet().equals(this.links.keySet());
    }

    public HorizonParameters getNodeParams(final VirtualNodeIdentifier id) {
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
    // // XXX Efficacité ! Mémoizer le résultat ou créer une nouvelle classe
    // // pour nodelist avec les identifiers
    // }

    @Override
    public VirtualNetworkIdentifier getMyAgentIdentifier() {
	return (VirtualNetworkIdentifier) super.getMyAgentIdentifier();
    }

    private Double getMyCriticity() {
	return this.isValid() ? 1. : 0.;
    }

    @Override
    public Set<SubstrateNodeIdentifier> getMyResourceIdentifiers() {
	assert (!this.nodes.values().contains(null));
	Set<SubstrateNodeIdentifier> myResIds = new HashSet();
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
    private class VirtualNode implements UtilitaristAnalyser<VirtualNode> {

	/**
	 * Serial version identifier
	 */
	private static final long serialVersionUID = 6804972846662200779L;

	/**
	 * Parameters levels requested by the SLA.
	 * 
	 * @uml.property name="param"
	 * @uml.associationEnd
	 */
	private final HorizonParameters param;

	/**
	 * Identifier of the SubstrateNode hosting this VirtualNode.
	 * 
	 * @uml.property name="myHost"
	 * @uml.associationEnd
	 */
	private final SubstrateNodeIdentifier myHost;

	public VirtualNode(final HorizonParameters param,
		final SubstrateNodeIdentifier host) {
	    this.param = param;
	    this.myHost = host;
	}

	public VirtualNode(final VirtualNode initial,
		final SubstrateNodeIdentifier newHost) {
	    this.param = initial.param;
	    this.myHost = newHost;
	}

	/**
	 * @return
	 * @uml.property name="param"
	 */
	public HorizonParameters getParam() {
	    return this.param;
	}

	/**
	 * @return
	 * @uml.property name="myHost"
	 */
	public SubstrateNodeIdentifier getMyHost() {
	    return this.myHost;
	}

	public Double getMyReliability() {
	    // TODO écrire la fonction d'évaluation
	    return new Double(0);
	}

	@Override
	public Double getNumericValue(VirtualNode e) {
	    return this.getMyReliability();
	}
    }
}