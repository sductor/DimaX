package negotiation.horizon.negotiatingagent;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import negotiation.negotiationframework.contracts.ResourceIdentifier;
import negotiation.negotiationframework.rationality.SimpleAgentState;
import dima.basicagentcomponents.AgentIdentifier;
import dima.introspectionbasedagents.services.information.ObservationService.Information;
import dimaxx.tools.aggregator.AbstractCompensativeAggregation;

/**
 * The state of a SubstrateNode (provided resources, available resources).
 * 
 * @author Vincent Letard
 */
public class SubstrateNodeState extends SimpleAgentState {

    /**
     * Serial version identifier.
     */
    private static final long serialVersionUID = -2415474717777657012L;

    /**
     * Parameters of this SubstrateNode.
     * 
     * @uml.property name="nodeParams"
     * @uml.associationEnd
     */
    private final HorizonParameters<SubstrateNodeIdentifier> nodeParams;

    /**
     * Remaining parameters after the VirtualNodes are instantiated.
     * 
     * @uml.property name="availableNodeParams"
     * @uml.associationEnd
     */
    private final HorizonParameters<SubstrateNodeIdentifier> availableNodeParams;

    /**
     * The Set of VirtualNetworks that have at least one VirtualNode hosted
     * here.
     */
    private final Set<VirtualNetworkIdentifier> nodesHostedNetworks;

    // Une seule allocation/destruction par construction d'objet
    /**
     * Number of VirtualNodes currently instantiated.
     */
    private final int nbHostedNodes;

    /**
     * Constructs a new SubstrateNodeState using the provided parameters.
     * 
     * @param myAgent
     *            Identifier of the Agent corresponding to this State
     * @param stateNumber
     * @param nodeParams
     *            SingleNodeParameters available on this SubstrateNode
     * @param ifacesParams
     *            LinkParameters available on each (one per List item) network
     *            interface of this SubstrateNode
     */
    public SubstrateNodeState(final AgentIdentifier myAgent,
	    final int stateNumber,
	    final HorizonParameters<SubstrateNodeIdentifier> nodeParams) {
	super(myAgent, stateNumber);

	if (!nodeParams.isValid()) {
	    throw new IllegalArgumentException(
		    "Substrate node parameters must be valid.");
	}
	this.nodeParams = nodeParams;
	this.availableNodeParams = nodeParams;
	this.nodesHostedNetworks = Collections
		.unmodifiableSet(new HashSet<VirtualNetworkIdentifier>());
	this.nbHostedNodes = 0;
    }

    /**
     * Constructs a new instance of SubstrateNodeState by performing
     * adding/deleting one instantiated VirtualNode from the initial state.
     * 
     * @param initial
     *            Initial SubstrateNodeState
     * @param nodeNetwork
     *            Identifier of the VirtualNetwork where belongs the
     *            added/deleted node
     * @param params
     *            Parameters of the allocation (negative if deletion)
     * @param creation
     *            Is it a creation or a deletion ?
     */
    public SubstrateNodeState(final SubstrateNodeState initial,
	    final VirtualNetworkIdentifier nodeNetwork,
	    final HorizonParameters<HorizonIdentifier> params,
	    final boolean creation) {
	super(initial.getMyAgentIdentifier(), initial.getStateCounter() + 1);

	this.nodeParams = initial.nodeParams;

	if (creation) {
	    this.availableNodeParams = new HorizonParameters<SubstrateNodeIdentifier>(
		    this.getMyAgentIdentifier(),
		    new NodeParameters(initial.availableNodeParams
			    .getNodeParameters().getProcessor()
			    - params.getNodeParameters().getProcessor(),
			    initial.availableNodeParams.getNodeParameters()
				    .getRam()
				    - params.getNodeParameters().getRam(),
			    initial.availableNodeParams.getNodeParameters()
				    .getAvailability()
				    - params.getNodeParameters()
					    .getAvailability()),
		    initial.availableNodeParams.getInterfacesParameters());

	    if (initial.nodesHostedNetworks.contains(nodeNetwork))
		this.nodesHostedNetworks = initial.nodesHostedNetworks;
	    else {
		Set<VirtualNetworkIdentifier> newNetworkSet = new HashSet<VirtualNetworkIdentifier>(
			initial.nodesHostedNetworks);
		newNetworkSet.add(nodeNetwork);
		this.nodesHostedNetworks = Collections
			.unmodifiableSet(newNetworkSet);
	    }
	    this.nbHostedNodes = initial.nbHostedNodes + 1;
	} else {
	    this.availableNodeParams = new HorizonParameters<SubstrateNodeIdentifier>(
		    this.getMyAgentIdentifier(),
		    new NodeParameters(initial.availableNodeParams
			    .getNodeParameters().getProcessor()
			    + params.getNodeParameters().getProcessor(),
			    initial.availableNodeParams.getNodeParameters()
				    .getRam()
				    + params.getNodeParameters().getRam(),
			    initial.availableNodeParams.getNodeParameters()
				    .getAvailability()
				    + params.getNodeParameters()
					    .getAvailability()),
		    initial.availableNodeParams.getInterfacesParameters());

	    Set<VirtualNetworkIdentifier> newNetworkSet = new HashSet<VirtualNetworkIdentifier>(
		    initial.nodesHostedNetworks);
	    if (!newNetworkSet.remove(nodeNetwork)) {
		throw new IllegalArgumentException(
			"Attempt to remove a non allocated node.");
	    }
	    this.nodesHostedNetworks = Collections
		    .unmodifiableSet(newNetworkSet);
	    this.nbHostedNodes = initial.nbHostedNodes - 1;
	}
    }

    // /**
    // * Constructs a new SubstrateNetworkState applying to initial the
    // * modifications specified.
    // *
    // * @param initial
    // * Initial SubstrateNodeState
    // * @param
    // * @param paramsDelta
    // * The variation of the parameters which is to be applied from
    // * the initial SubstrateNodeState
    // */
    // public SubstrateNodeState(final SubstrateNodeState initial,
    // final Set<VirtualNetworkIdentifier> newNodesNetworks,
    // final Set<VirtualNetworkIdentifier> formerNodesNetworks,
    // final SingleNodeParameters paramsAllocated,
    // final SingleNodeParameters paramsRealeased) {
    // super(initial.getMyAgentIdentifier(), initial.getStateCounter() + 1);
    //
    // assert (this.nodesHostedNetworks.containsAll(formerNodesNetworks) &&
    // Collections
    // .disjoint(this.nodesHostedNetworks, newNodesNetworks));
    //
    // this.nodeParams = initial.nodeParams;
    // this.ifacesParams = initial.ifacesParams;
    //
    // SingleNodeParameters[] pos = { initial.availableNodeParams,
    // paramsRealeased };
    // SingleNodeParameters[] neg = { paramsAllocated };
    // this.availableNodeParams = new SingleNodeParameters(Arrays.asList(pos),
    // Arrays.asList(neg));
    //
    // Set<VirtualNetworkIdentifier> hostedNodes = new
    // HashSet<VirtualNetworkIdentifier>(
    // initial.nodesHostedNetworks);
    // for (VirtualNetworkIdentifier network : newNodesNetworks) {
    // if (hostedNodes.contains(network)) {
    // hostedNodes.remove(network);
    // } else
    // hostedNodes.add(network);
    // }
    // this.nodesHostedNetworks = Collections.unmodifiableSet(hostedNodes);

    // SingleNodeParameters availableNodeParams = null;
    // List<LinkParameters> availableIfacesParams = null;
    // this.nodesHosted = new HashedHashSet<VirtualNetworkState, Integer>();
    // Iterator<Entry<VirtualNetworkState, Set<Integer>>> networksIt =
    // initial.nodesHosted
    // .entrySet().iterator();
    // boolean allocation = true;
    //
    // while (networksIt.hasNext()) {
    // Entry<VirtualNetworkState, Set<Integer>> ent = networksIt.next();
    //
    // if (ent.getKey().equals(newNodeNetwork)
    // && ent.getValue().contains(newNodeNumber)) {
    // allocation = false;
    // Set<Integer> networkNodes = new HashSet<Integer>(ent.getValue());
    // networkNodes.remove(newNodeNumber);
    // this.nodesHosted.put(newNodeNetwork, networkNodes);
    // availableNodeParams = new SingleNodeParameters(
    // initial.availableNodeParams.getProcessor()
    // + newNodeNetwork.getNodeParams(newNodeNumber)
    // .getProcessor(),
    // initial.availableNodeParams.getRam()
    // + newNodeNetwork.getNodeParams(newNodeNumber)
    // .getRam());
    // } else {
    // this.nodesHosted.put(newNodeNetwork, new HashSet<Integer>(ent
    // .getValue()));
    // }
    // }
    //
    // this.availableNodeParams = availableNodeParams;
    // }

    public HorizonParameters<SubstrateNodeIdentifier> getNodeParams() {
	return nodeParams;
    }

    public HorizonParameters<SubstrateNodeIdentifier> getAvailableNodeParams() {
	return availableNodeParams;
    }

    @Override
    public Set<VirtualNetworkIdentifier> getMyResourceIdentifiers() {
	return this.nodesHostedNetworks;
    }

    @Override
    public Class<? extends Information> getMyResourcesClass() {
	return VirtualNetworkState.class;
    }

    @Override
    public boolean isValid() {
	return this.availableNodeParams.isValid();
    }

    private Double getMyCharge() {
	return (double) Math
		.max(this.nodeParams.getNodeParameters().getProcessor()
			- this.availableNodeParams.getNodeParameters()
				.getProcessor(), this.nodeParams
			.getNodeParameters().getRam()
			- this.availableNodeParams.getNodeParameters().getRam());
    }

    // TODO supprimer
    private Double getMyCriticity() {
	return this.getMyCharge();
    }

    // TODO supprimer
    private Double getMyDisponibility() {
	if (0 == this.nbHostedNodes)
	    return Double.POSITIVE_INFINITY;
	else
	    return 1. / this.nbHostedNodes; // XXX ..en gros
    }

    // TODO supprimer
    private Double getMyReliability() {
	return 1 - ((1 - this.getMyCriticity()) * this.getMyDisponibility());
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
    public boolean setLost(ResourceIdentifier h, boolean isLost) {
	throw new UnsupportedOperationException();
    }

}