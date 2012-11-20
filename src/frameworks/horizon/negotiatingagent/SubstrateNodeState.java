package frameworks.horizon.negotiatingagent;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import dima.basicagentcomponents.AgentIdentifier;
import frameworks.horizon.parameters.HorizonAllocableParameters;
import frameworks.negotiation.rationality.AgentState;
import frameworks.negotiation.rationality.SimpleAgentState;

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

	/**
	 * Coefficient of energy consumption if the load of the machine is not 0.
	 */
	private final int energyConsumptionCoef;

	// Nécessaire uniquement si on permet la modification dynamique des
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
	 *            initial state number (0 is recommended)
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
		if (energyConsumptionCoef < 0) {
			throw new IllegalArgumentException();
		}
		this.energyConsumptionCoef = energyConsumptionCoef;
	}

	/**
	 * Constructs a new instance of SubstrateNodeState by performing
	 * adding/deleting one instantiated VirtualNode from the initial state. The
	 * instance created is not valid (in the sense of isValid) since the
	 * 
	 * @param initial
	 *            Initial SubstrateNodeState
	 * @param vnId
	 *            Network of the allocated node
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
		assert params.isValid();

		final Map<VirtualNetworkIdentifier, Integer> newMap = new HashMap<VirtualNetworkIdentifier, Integer>(
				initial.nodesHostedNetworks);
		if (creation) {
			this.availableNodeParams = initial.availableNodeParams.add(params);

			if (newMap.containsKey(vnId)) {
				assert newMap.get(vnId) != null;
				newMap.put(vnId, newMap.get(vnId) + 1);
			} else {
				newMap.put(vnId, 1);
			}
		} else {
			this.availableNodeParams = initial.availableNodeParams
					.subtract(params);

			if (!newMap.containsKey(vnId)) {
				throw new IllegalArgumentException(
						"Attempt to remove a non allocated node.");
			} else {
				final Integer nb = newMap.get(vnId);
				assert nb != null;
				assert nb >= 0;
				if (nb == 0) {
					throw new IllegalArgumentException(
							"Attempt to remove a non allocated node.");
				} else if (nb == 1) {
					newMap.remove(vnId);
				} else {
					newMap.put(vnId, newMap.get(vnId) - 1);
				}
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

	/**
	 * Gives the amount remaining AllocableParameters on this node.
	 * 
	 * @return an object of type HorizonAllocableParameters containing the
	 *         available parameters
	 */
	public HorizonAllocableParameters<SubstrateNodeIdentifier> getAvailableNodeParams() {
		return this.availableNodeParams;
	}

	/**
	 * Returns the set of VirtualNetworks that have at least on node currently
	 * instanciated on this SubstrateNode
	 */
	@Override
	public Set<VirtualNetworkIdentifier> getMyResourceIdentifiers() {
		return this.nodesHostedNetworks.keySet();
	}

	@Override
	public boolean hasResource(final AgentIdentifier id) {
		return this.getMyResourceIdentifiers().contains(id);
	}
	/**
	 * Returns the class of the "resources" of a SubstrateNode
	 */
	@Override
	public Class<? extends AgentState> getMyResourcesClass() {
		return VirtualNetworkState.class;
	}

	/**
	 * Tests whether rights of the agent are respected in that state.
	 */
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


	/**
	 * Tests whether this SubstrateNode is hosting no VirtualNode.
	 * 
	 * @return <code>true</code> if the number of VirtualNetworks having at
	 *         least one node instanciated on this SubstrateNode is 0
	 */
	public boolean isEmpty() {
		return this.nodesHostedNetworks.size() == 0;
	}

	/**
	 * Computes a utility evaluation for this state.
	 * 
	 * @return a double value between 0. and 1. representing the satisfaction of
	 *         the node.
	 */
	public Double evaluateUtility() {
		return this.nodesHostedNetworks.isEmpty() ? 1
				: 1. / (this.energyConsumptionCoef + 1);
	}

	@Override
	public AgentState clone() {
		return this;
	}


}