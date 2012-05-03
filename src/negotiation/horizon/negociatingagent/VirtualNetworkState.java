package negotiation.horizon.negociatingagent;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import negotiation.negotiationframework.contracts.ResourceIdentifier;
import negotiation.negotiationframework.rationality.SimpleAgentState;
import dima.basicagentcomponents.AgentUniqueIdentifier;
import dima.introspectionbasedagents.services.information.ObservationService.Information;
import dimaxx.tools.aggregator.AbstractCompensativeAggregation;
import dimaxx.tools.aggregator.UtilitaristAnalyser;
import dimaxx.tools.mappedcollections.ReflexiveBinaryAdjacencyMap;

public class VirtualNetworkState extends SimpleAgentState implements
	HorizonSpecification {

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
	    final int stateNumber,
	    final List<SingleNodeParameters> nodesParams,
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

	for (SingleNodeParameters param : nodesParams) {
	    VirtualNodeIdentifier newVNId = new VirtualNodeIdentifier();
	    nodesMap.put(newVNId, new VirtualNode(param, null));
	    orderedVNList.add(newVNId);
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

    public VirtualNetworkState(final VirtualNetworkState initial,
	    final VirtualNodeIdentifier reallocatedNode,
	    final SubstrateNodeIdentifier newHost) {
	super(initial.getMyAgentIdentifier(), initial.getStateCounter() + 1);

	this.links = initial.links;
	if (this.nodes.containsKey(reallocatedNode)) {
	    Map<VirtualNodeIdentifier, VirtualNode> newNodesMap = new HashMap<VirtualNodeIdentifier, VirtualNode>(
		    initial.nodes);

	    newNodesMap.put(reallocatedNode, new VirtualNode(this.nodes
		    .get(reallocatedNode), newHost));
	    this.nodes = Collections.unmodifiableMap(newNodesMap);
	} else
	    this.nodes = initial.nodes; // TODO is that correct ?
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

    public SingleNodeParameters getNodeParams(final VirtualNodeIdentifier id) {
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

    @Override
    public Double getNumericValue(Information e) {
	// TODO agregation des VirtualNode.getNumericValue
	return null;
    }

    @Override
    public AbstractCompensativeAggregation<Information> fuse(
	    Collection<? extends AbstractCompensativeAggregation<? extends Information>> averages) {
	throw new UnsupportedOperationException();
    }

    @Override
    public Information getRepresentativeElement(
	    Collection<? extends Information> elems) {
	// TODO Auto-generated method stub
	return null;
    }

    @Override
    public Information getRepresentativeElement(
	    Map<? extends Information, Double> elems) {
	// TODO Auto-generated method stub
	return null;
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
     * Extension of an AgentUniqueIdentifier for clarity with VirtualNodes.
     * 
     * @author Vincent Letard
     */
    public class VirtualNodeIdentifier extends AgentUniqueIdentifier {
	/**
	 * Serial version identifier.
	 */
	private static final long serialVersionUID = -6126319326448434675L;

	// TODO Est-ce nécessaire ? Si oui, supprimer le constructeur sans
	// argument.
	private final VirtualNetworkIdentifier myVirtualNetworkIdentifier;

	private VirtualNodeIdentifier() {
	    super();
	    this.myVirtualNetworkIdentifier = null;
	}

	private VirtualNodeIdentifier(final VirtualNetworkIdentifier myVNetId) {
	    super();
	    this.myVirtualNetworkIdentifier = myVNetId;
	}
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
	 */
	private final SingleNodeParameters param;

	/**
	 * Identifier of the SubstrateNode hosting this VirtualNode.
	 */
	private final SubstrateNodeIdentifier myHost;

	public VirtualNode(final SingleNodeParameters param,
		final SubstrateNodeIdentifier host) {
	    this.param = param;
	    this.myHost = host;
	}

	public VirtualNode(final VirtualNode initial,
		final SubstrateNodeIdentifier newHost) {
	    this.param = initial.param;
	    this.myHost = newHost;
	}

	public SingleNodeParameters getParam() {
	    return this.param;
	}

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

    @Override
    public boolean setLost(ResourceIdentifier h, boolean isLost) {
	throw new UnsupportedOperationException();
	// TODO Remove this method
    }
}