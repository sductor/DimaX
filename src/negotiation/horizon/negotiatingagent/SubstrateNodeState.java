package negotiation.horizon.negotiatingagent;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import negotiation.horizon.parameters.HorizonAllocableParameters;
import negotiation.negotiationframework.contracts.ResourceIdentifier;
import negotiation.negotiationframework.rationality.SimpleAgentState;
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
     * Remaining parameters after the VirtualNodes are instantiated.
     */
    private final HorizonAllocableParameters<SubstrateNodeIdentifier> availableNodeParams;

    private final int energyConsumptionCoef;

    // XXX Nécessaire uniquement si on permet la modification dynamique des
    // paramètres des réseaux virtuels.
    // private final Map<VirtualNodeIdentifier, NodeParameters> allocations;

    /**
     * Number of nodes hosted per VirtualNetwork.
     */
    private final Map<VirtualNetworkIdentifier, Integer> nodesHostedNetworks;

    // private final Map<VirtualNodeIdentifier, SubstrateNodeIdentifier>
    // associatedLinks;

    /**
     * Constructs a new SubstrateNodeState using the provided parameters.
     * 
     * @param myAgent
     *            Identifier of the Agent corresponding to this State
     * @param stateNumber
     * @param nodeParams
     *            All available parameters on this SubstrateNode
     * @param energyConsumptionCoef
     *            An indicator of the energy consumption of the machine
     */
    public SubstrateNodeState(
	    final SubstrateNodeIdentifier myAgent,
	    final int stateNumber,
	    final HorizonAllocableParameters<SubstrateNodeIdentifier> nodeParams,
	    final int energyConsumptionCoef) {
	super(myAgent, stateNumber);

	if (!nodeParams.isValid()) {
	    throw new IllegalArgumentException(
		    "Substrate node parameters must be valid.");
	}
	this.availableNodeParams = nodeParams;

	this.nodesHostedNetworks = Collections
		.unmodifiableMap(new HashMap<VirtualNetworkIdentifier, Integer>());
	if (energyConsumptionCoef < 0)
	    throw new IllegalArgumentException();
	this.energyConsumptionCoef = energyConsumptionCoef;
    }

    /**
     * Constructs a new instance of SubstrateNodeState by performing
     * adding/deleting one instantiated VirtualNode from the initial state. The
     * instance created is not valid (in the sense of isValid) since the
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
	    final VirtualNetworkIdentifier vnId,
	    final HorizonAllocableParameters<SubstrateNodeIdentifier> params,
	    final boolean creation) {
	super(initial.getMyAgentIdentifier(), initial.getStateCounter() + 1);
	assert (params.isValid());

	Map<VirtualNetworkIdentifier, Integer> newMap = new HashMap<VirtualNetworkIdentifier, Integer>(
		initial.nodesHostedNetworks);
	if (creation) {
	    this.availableNodeParams = initial.availableNodeParams.add(params);

	    if (newMap.containsKey(vnId)) {
		assert (newMap.get(vnId) != null);
		newMap.put(vnId, newMap.get(vnId) + 1);
	    } else
		newMap.put(vnId, 1);
	} else {
	    this.availableNodeParams = initial.availableNodeParams
		    .subtract(params);

	    if (!newMap.containsKey(vnId)) {
		throw new IllegalArgumentException(
			"Attempt to remove a non allocated node.");
	    } else {
		final Integer nb = newMap.get(vnId);
		assert (nb != null);
		assert (nb >= 0);
		if (nb == 0)
		    throw new IllegalArgumentException(
			    "Attempt to remove a non allocated node.");
		else if (nb == 1)
		    newMap.remove(vnId);
		else
		    newMap.put(vnId, newMap.get(vnId) - 1);
	    }
	}
	this.nodesHostedNetworks = Collections.unmodifiableMap(newMap);
	this.energyConsumptionCoef = initial.energyConsumptionCoef;
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

    public HorizonAllocableParameters<SubstrateNodeIdentifier> getAvailableNodeParams() {
	return availableNodeParams;
    }

    @Override
    public Set<VirtualNetworkIdentifier> getMyResourceIdentifiers() {
	return this.nodesHostedNetworks.keySet();
    }

    @Override
    public Class<? extends Information> getMyResourcesClass() {
	return VirtualNetworkState.class;
    }

    @Override
    public boolean isValid() {
	return this.availableNodeParams.isValid();
    }

    // private Double getMyCharge() {
    // return (double) Math
    // .max(this.nodeParams.getNodeParameters().getProcessor()
    // - this.availableNodeParams.getNodeParameters()
    // .getProcessor(), this.nodeParams
    // .getNodeParameters().getRam()
    // - this.availableNodeParams.getNodeParameters().getRam());
    // }

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

    public boolean isEmpty() {
	return this.nodesHostedNetworks.size() == 0;
    }

    public Double evaluateUtility() {
	return this.nodesHostedNetworks.isEmpty() ? 1
		: 1. / (this.energyConsumptionCoef + 1);
    }

}