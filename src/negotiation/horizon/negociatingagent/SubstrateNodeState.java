package negotiation.horizon.negociatingagent;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import negotiation.negotiationframework.contracts.ResourceIdentifier;
import negotiation.negotiationframework.rationality.SimpleAgentState;
import dima.basicagentcomponents.AgentIdentifier;
import dima.introspectionbasedagents.services.information.ObservationService.Information;
import dimaxx.tools.aggregator.AbstractCompensativeAggregation;

/**
 * The state of a SubstrateNode. The fields inherited from
 * {@link _AbstractSingleNodeState} represent the level of service provided by
 * the SubstrateNode.
 * 
 * @author Vincent Letard
 */
public class SubstrateNodeState extends SimpleAgentState implements
	HorizonSpecification {

    /**
     * Serial version identifier.
     */
    private static final long serialVersionUID = -2415474717777657012L;

    /**
     * Parameters of this SubstrateNode.
     */
    private final SingleNodeParameters nodeParams;

    /**
     * Remaining parameters after the VirtualNodes are instantiated.
     */
    private final SingleNodeParameters availableNodeParams;
    /**
     * List of the {@link LinkParameters} of each network interface of the node.
     * Contains as much items as there are network interfaces. This List is an
     * unmodifiable list since these parameters are not expected to change.
     */
    private final List<LinkParameters> ifacesParams;
    // private final List<LinkParameters> availableIfacesParams;
    // private final List<SubstrateNodeState> pairedNodes;

    /**
     * The Set of VirtualNetworks that have at least one VirtualNode hosted
     * here.
     */
    private final Set<VirtualNetworkIdentifier> nodesHostedNetworks;

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
	    final int stateNumber, final SingleNodeParameters nodeParams,
	    final List<LinkParameters> ifacesParams /*
						     * , final
						     * List<SubstrateNodeState>
						     * pairedNodes
						     */) {
	super(myAgent, stateNumber);

	if (!nodeParams.isValid()) {
	    throw new IllegalArgumentException(
		    "Substrate node parameters must be valid.");
	}
	this.nodeParams = nodeParams;
	this.ifacesParams = Collections.unmodifiableList(ifacesParams);
	this.availableNodeParams = nodeParams;
	// this.availableIfacesParams = this.ifacesParams;
	this.nodesHostedNetworks = Collections
		.unmodifiableSet(new HashSet<VirtualNetworkIdentifier>());
	// this.pairedNodes = Collections.unmodifiableList(pairedNodes);
    }

    public SubstrateNodeState(final SubstrateNodeState initial,
	    final VirtualNetworkIdentifier nodeNetwork,
	    final SingleNodeParameters params, final boolean creation) {
	super(initial.getMyAgentIdentifier(), initial.getStateCounter() + 1);

	this.ifacesParams = initial.ifacesParams;
	this.nodeParams = initial.nodeParams;

	if (creation) {
	    this.availableNodeParams = new SingleNodeParameters(
		    initial.availableNodeParams.getProcessor()
			    - params.getProcessor(),
		    initial.availableNodeParams.getRam() - params.getRam());

	    if (initial.nodesHostedNetworks.contains(nodeNetwork))
		this.nodesHostedNetworks = initial.nodesHostedNetworks;
	    else {
		Set<VirtualNetworkIdentifier> newNetworkSet = new HashSet<VirtualNetworkIdentifier>(
			initial.nodesHostedNetworks);
		newNetworkSet.add(nodeNetwork);
		this.nodesHostedNetworks = Collections
			.unmodifiableSet(newNetworkSet);
	    }
	} else {
	    this.availableNodeParams = new SingleNodeParameters(
		    initial.availableNodeParams.getProcessor()
			    + params.getProcessor(),
		    initial.availableNodeParams.getRam() + params.getRam());

	    Set<VirtualNetworkIdentifier> newNetworkSet = new HashSet<VirtualNetworkIdentifier>(
		    initial.nodesHostedNetworks);
	    if (!newNetworkSet.remove(nodeNetwork)) {
		throw new IllegalArgumentException(
			"Attempt to remove a non allocated node.");
	    }
	    this.nodesHostedNetworks = Collections
		    .unmodifiableSet(newNetworkSet);
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

    public SingleNodeParameters getNodeParams() {
	return nodeParams;
    }

    public SingleNodeParameters getAvailableNodeParams() {
	return availableNodeParams;
    }

    public List<LinkParameters> getIfacesParams() {
	return ifacesParams;
    }

    // public List<LinkParameters> getAvailableIfacesParams() {
    // return availableIfacesParams;
    // }

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
    public boolean setLost(ResourceIdentifier h, boolean isLost) {
	throw new UnsupportedOperationException();
	// TODO Remove this method
    }

}