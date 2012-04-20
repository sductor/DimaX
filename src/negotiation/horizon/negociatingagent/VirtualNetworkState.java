package negotiation.horizon.negociatingagent;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import negotiation.negotiationframework.contracts.ResourceIdentifier;
import negotiation.negotiationframework.rationality.SimpleAgentState;
import dima.basicagentcomponents.AgentIdentifier;
import dima.introspectionbasedagents.services.information.ObservationService.Information;
import dimaxx.tools.aggregator.AbstractCompensativeAggregation;
import dimaxx.tools.mappedcollections.ReflexiveAdjacencyMap;

public class VirtualNetworkState extends SimpleAgentState implements
	HorizonSpecification {

    /**
     * Serial version identifier.
     */
    private static final long serialVersionUID = -5576314995630464103L;

    /**
     * Represents the mapping of all the virtual nodes to the substrate nodes
     * (all virtual nodes are in the mapping even if it's associated with no
     * substrate node).
     */
    private final List<VirtualNode> nodes;

    private static int virtualNodeNumber = 0;

    /**
     * Represents the links between all the VirtualNodes and their parameters.
     */
    private ReflexiveAdjacencyMap<VirtualNodeIdentifier, LinkParameters> links;

    /**
     * Constructs a new VirtualNetworkState using the topology information
     * provided in the arguments.
     * 
     * @param nodelist
     *            The Set of all the VirtualNodeState of the constituting
     *            virtual nodes.
     * @param links
     *            Maps a node index in the Set with the list of its successors
     *            in the virtual network.
     */
    public VirtualNetworkState(final AgentIdentifier myAgent,
	    final int stateNumber, final List<SingleNodeParameters> params,
	    final List<LinkParameters> links) {
	super(myAgent, stateNumber);
	int s = params.size();
	if (links.size() > (s * (s + 1)) / 2 + s)
	    throw new RuntimeException("Invalid argument");
	// int id = 0;
	// for (SingleNodeParameters node:nodes){
	// map.put(new VirtualNode(myAgent, id, node), null);
	// id++;
	// }
	this.nodes = new ArrayList<VirtualNode>(params.size());
	for (int id = 0; id < params.size(); id++) {
	    this.nodes.add(new VirtualNode(myAgent, id, params.get(id)));
	}
	this.links = new ReflexiveAdjacencyMap<VirtualNode, LinkParameters>();
	for (int i = 0; i < links.size(); i++) {
	    int[] vertices = ReflexiveAdjacencyMap.getCoupleOfIndex(i);
	    this.links.add(this.nodes.get(vertices[0]), this.nodes
		    .get(vertices[1]), links.get(i));
	}
    }

    private VirtualNetworkState(final AgentIdentifier myAgent,
	    final int stateNumber,
 final List<VirtualNode> nodes,
	    final ReflexiveAdjacencyMap<VirtualNode, LinkParameters> links) {
	super(myAgent, stateNumber);
	// this.nodes = nodes.;
	this.links = links;
	assert (networkConsistentAndConnected()); // TODO pas assert ->
	// exception
    }

    private boolean networkConsistentAndConnected() {
	return this.nodes.keySet().equals(this.links.keySet());
    }

    public SingleNodeParameters getNodeParams(final int node){
	return this.nodes.get(node).getParam();
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
    public AgentIdentifier getMyAgentIdentifier() {
	return super.getMyAgentIdentifier();
    }

    @Override
    public Double getNumericValue(Information e) {
	// TODO Auto-generated method stub
	return null;
    }

    @Override
    public AbstractCompensativeAggregation<Information> fuse(
	    Collection<? extends AbstractCompensativeAggregation<? extends Information>> averages) {
	// TODO Auto-generated method stub
	return null;
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
    public Set<? extends ResourceIdentifier> getMyResourceIdentifiers() {
	Set<ResourceIdentifier> resources = new HashSet<ResourceIdentifier>(
		this.nodes.values());
	resources.remove(null);
	return resources;
    }

    @Override
    public Class<? extends Information> getMyResourcesClass() {
	return SubstrateNodeState.class;
    }

    @Override
    public boolean isValid() {
	return this.nodes.values().contains(null);
    }

    @Override
    public boolean setLost(ResourceIdentifier h, boolean isLost) {
	Iterator<Entry<VirtualNode, ResourceIdentifier>> it = this.nodes
		.entrySet().iterator();
	while (it.hasNext()) {
	    Entry<VirtualNode, ResourceIdentifier> entry = it.next();
	    if (entry.getValue().equals(h))
		this.nodes.put(entry.getKey(), null);
	    // TODO est-ce bien ce qu'il faut faire ?
	}
	return true; // TODO retourner toujours true ?
    }

    public class VirtualNodeIdentifier {
	private final AgentIdentifier myVirtualNetworkIdentifier;
	private final int number;

	private VirtualNodeIdentifier(final AgentIdentifier myAgent,
		final int number) {
	    this.myVirtualNetworkIdentifier = myAgent;
	    this.number = number;
	}
	
	@Override
	public int hashCode(){
	    int a = this.number, b = this.myVirtualNetworkIdentifier.hashCode();
	    return ((a+b) * (a+b) + 3*a + b)/2;
	}
	
	@Override
	public boolean equals(Object obj){
	    if (obj instanceof VirtualNodeIdentifier){
		return ((VirtualNodeIdentifier) obj).getMyVirtualNetworkIdentifier().equals(this.myVirtualNetworkIdentifier)
		&& ((VirtualNodeIdentifier) obj).getNumber() == this.number;
	    }
	}

	private AgentIdentifier getMyVirtualNetworkIdentifier() {
	    return myVirtualNetworkIdentifier;
	}

	private int getNumber() {
	    return number;
	}
	
    }

    private class VirtualNode {

	/**
	 * Serial version identifier
	 */
	private static final long serialVersionUID = 6804972846662200779L;

	private final SingleNodeParameters param;
	private ResourceIdentifier myHost;

	public VirtualNode(final SingleNodeParameters param,
		final ResourceIdentifier host) {
	    this.param = param;
	    this.myHost = host;
	}

	public SingleNodeParameters getParam() {
	    return this.param;
	}

	public ResourceIdentifier getMyHost() {
	    return this.myHost;
	}

	@Override
	public int hashCode() {
	    return this.identifier;
	}

	@Override
	public boolean equals(Object obj){
	    if (obj instanceof VirtualNode)
		return this.identifier == ((VirtualNode) obj).getIdentifier();
	    else
		return false;
	}
    }
}