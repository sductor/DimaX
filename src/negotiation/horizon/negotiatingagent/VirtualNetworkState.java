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
import negotiation.horizon.parameters.UnexistingLinkException;
import negotiation.horizon.util.EmptyInterval.EmptyIntervalException;
import negotiation.horizon.util.Interval;
import negotiation.negotiationframework.contracts.ResourceIdentifier;
import negotiation.negotiationframework.rationality.AgentState;
import negotiation.negotiationframework.rationality.SimpleAgentState;
import dima.introspectionbasedagents.services.information.ObservationService.Information;
import dima.support.GimaObject;
import dimaxx.tools.aggregator.AbstractCompensativeAggregation;
import dimaxx.tools.aggregator.LightAverageDoubleAggregation;
import dimaxx.tools.mappedcollections.ElementsNotLinkedException;
import dimaxx.tools.mappedcollections.OrderedPair;
import dimaxx.tools.mappedcollections.SymmetricBinaryAdjacencyMap;

/**
 * This class represents the state of a VirtualNetwork. Thereby it contains all
 * the specifications of its composing nodes and the links between them.
 * 
 * @author Vincent Letard
 */
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

	/**
	 * The service requested for this VirtualNetwork
	 */
	private final Service serviceName;

	// /**
	// * Represents the links between all the VirtualNodes and their parameters.
	// */
	// XXX Note : a priori inutilisé
	// private Map<Set<VirtualNodeIdentifier>, LinkAllocableParameters> links;

	/**
	 * Constructs a new VirtualNetworkState using the provided specifications.
	 * 
	 * @param myAgent
	 *            Identifier of the agent.
	 * @param stateNumber
	 *            InitialState number (0 is recommended).
	 * @param nodesRequiredParams
	 *            Required parameters for each node of the network (must have
	 *            the same size as nodesPreferenceParams)
	 * @param nodesPreferenceParams
	 *            Preferred levels of non functional parameters for each node of
	 *            the network (must have the same size as nodesRequiredParams)
	 * @param links
	 *            List of the links of the network, represented by the pairs of
	 *            indices of the nodes in nodesRequiredParameters and
	 *            nodePreferenceParameters (which must be coherent).
	 * @param linksRequiredParams
	 *            Required parameters on the links with same indices as in the
	 *            argument links (and same size)
	 * @param linksPreferenceParams
	 *            Preferred parameters on the links with same indices as in the
	 *            argument links (and same size)
	 * @param myPreferredQoS
	 *            Type of service needed by this virtual network.
	 * @throws IllegalArgumentException
	 *             if any of the requirement listed here is not respected.
	 * 
	 */
	public VirtualNetworkState(final VirtualNetworkIdentifier myAgent,
			final int stateNumber,
			final List<MachineAllocableParameters> nodesRequiredParams,
			final List<MachineMeasurableParameters> nodesPreferenceParams,
			final List<OrderedPair<Integer>> links,
			final List<LinkAllocableParameters> linksRequiredParams,
			final List<LinkMeasurableParameters> linksPreferenceParams,
			final Service myPreferredQoS) throws IllegalArgumentException {
		super(myAgent, stateNumber);

		if (nodesRequiredParams == null || nodesPreferenceParams == null
				|| links == null || linksRequiredParams == null
				|| linksPreferenceParams == null
				|| nodesRequiredParams.contains(null)
				|| nodesPreferenceParams.contains(null) || links.contains(null)
				|| linksRequiredParams.contains(null)
				|| linksPreferenceParams.contains(null)) {
			throw new IllegalArgumentException(new NullPointerException());
		}

		if (links.size() != linksRequiredParams.size()
				|| links.size() != linksPreferenceParams.size()) {
			throw new IllegalArgumentException(
					"Numbers of links and parameters are not equal. Please refer to the documentation.");
		}

		final int nbnodes = nodesRequiredParams.size();
		if (nodesPreferenceParams.size() != nbnodes) {
			throw new IllegalArgumentException(
					"Numbers of nodes is ambiguous. Please refer to the documentation.");
		}

		final List<VirtualNodeIdentifier> idList = new ArrayList<VirtualNodeIdentifier>(
				nbnodes);
		for (int i = 0; i < nbnodes; i++) {
			idList.add(this.getMyAgentIdentifier().new VirtualNodeIdentifier());
		}

		final SymmetricBinaryAdjacencyMap<VirtualNodeIdentifier, NetworkLinkParameters> linksMap = new SymmetricBinaryAdjacencyMap<VirtualNodeIdentifier, NetworkLinkParameters>();

		final Iterator<LinkAllocableParameters> itLinksReq = linksRequiredParams
				.iterator();
		final Iterator<LinkMeasurableParameters> itLinksPref = linksPreferenceParams
				.iterator();
		for (final OrderedPair<Integer> link : links) {
			assert itLinksReq.hasNext() && itLinksPref.hasNext();

			if (link.getFirst() < 0 || link.getSecond() >= nbnodes) {
				throw new IllegalArgumentException(
						"Cannot link together unexisting nodes. Please refer to the documentation.");
			} else if (link.getFirst().equals(link.getSecond())) {
				// Ignoring reflexive links
				// throw new IllegalArgumentException();
			} else {

				final NetworkLinkParameters test = linksMap.add(
						new OrderedPair<VirtualNodeIdentifier>(idList.get(link
								.getFirst()), idList.get(link.getSecond())),
								new NetworkLinkParameters(itLinksReq.next(),
										itLinksPref.next()));

				assert test == null;
			}
		}

		final Map<VirtualNodeIdentifier, VirtualNode> nodesMap = new HashMap<VirtualNodeIdentifier, VirtualNode>();

		for (int i = 0; i < nbnodes; i++) {
			final VirtualNodeIdentifier id = idList.get(i);
			final Map<VirtualNodeIdentifier, LinkAllocableParameters> ifacesAllocable = new HashMap<VirtualNodeIdentifier, LinkAllocableParameters>();
			final Map<VirtualNodeIdentifier, LinkMeasurableParameters> ifacesMeasurable = new HashMap<VirtualNodeIdentifier, LinkMeasurableParameters>();

			// TODO Vérifier
			for (final VirtualNodeIdentifier idPaired : linksMap.getPaired(id)) {
				try {
					ifacesAllocable.put(idPaired, linksMap
							.getLinkParam(
									new OrderedPair<VirtualNodeIdentifier>(id,
											idPaired)).getAllocableParams());
					ifacesMeasurable.put(idPaired, linksMap
							.getLinkParam(
									new OrderedPair<VirtualNodeIdentifier>(id,
											idPaired)).getMeasurableParams());
				} catch (final ElementsNotLinkedException e) {
					assert false;
					throw new UnexpectedException(e);
				}
			}
			final VirtualNode test = nodesMap
					.put(
							idList.get(i),
							new VirtualNode(
									new NodeParameters<VirtualNodeIdentifier>(
											new HorizonAllocableParameters<VirtualNodeIdentifier>(
													nodesRequiredParams.get(i),
													new InterfacesParameters<VirtualNodeIdentifier, LinkAllocableParameters>(
															ifacesAllocable)),
															new HorizonMeasurableParameters<VirtualNodeIdentifier>(
																	nodesPreferenceParams
																	.get(i),
																	new InterfacesParameters<VirtualNodeIdentifier, LinkMeasurableParameters>(
																			ifacesMeasurable)))));

			assert test == null;
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
		// assert (this.nodes.get(reallocatedNode).param.equals(params));
		assert initial.nodes.containsKey(reallocatedNode);

		// this.links = initial.links;

		final Map<VirtualNodeIdentifier, VirtualNode> newNodesMap = new HashMap<VirtualNodeIdentifier, VirtualNode>();

		// Cette opération est nécessaire car il faut rattacher tous les
		// VirtualNode au nouveau VirtualNetworkState
		for (final Map.Entry<VirtualNodeIdentifier, VirtualNode> entry : initial.nodes
				.entrySet()) {
			newNodesMap.put(entry.getKey(), new VirtualNode(entry.getValue()));
		}

		newNodesMap.put(reallocatedNode, new VirtualNode(initial.nodes
				.get(reallocatedNode), newHost, params));
		this.nodes = Collections.unmodifiableMap(newNodesMap);

		assert this.nodes.get(reallocatedNode).myHost.equals(newHost);

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

	/**
	 * Returns the parameters of the specified node.
	 * 
	 * @param id
	 *            Identifier of the node
	 * @return the parameters associated to the node.
	 */
	public NodeParameters<VirtualNodeIdentifier> getNodeParams(
			final VirtualNodeIdentifier id) {
		return this.nodes.get(id).getParam();
	}

	/**
	 * Gives the set of the Identifiers for all nodes in this VirtualNetwork
	 * 
	 * @return the Set of all VirtualNodeIdentifier
	 */
	public Set<VirtualNodeIdentifier> getNodes() {
		return this.nodes.keySet();
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

	/**
	 * Gives the identifier of the agent representing this VirtualNetwork.
	 */
	@Override
	public VirtualNetworkIdentifier getMyAgentIdentifier() {
		return (VirtualNetworkIdentifier) super.getMyAgentIdentifier();
	}

	/**
	 * Returns the set of the identifiers of the resources hosting the nodes of
	 * this VirtualNetwork.
	 */
	@Override
	public Set<SubstrateNodeIdentifier> getMyResourceIdentifiers() {
		assert !this.nodes.values().contains(null);
		final Set<SubstrateNodeIdentifier> myResIds = new HashSet<SubstrateNodeIdentifier>();
		for (final VirtualNode node : this.nodes.values()) {
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
	public Class<? extends AgentState> getMyResourcesClass() {
		return SubstrateNodeState.class;
	}

	/**
	 * This method is not designed for this implementation of the framework.
	 */
	@Override
	public boolean setLost(final ResourceIdentifier h, final boolean isLost) {
		throw new UnsupportedOperationException();
	}

	/**
	 * Unused method of the framework.
	 */
	@Override
	public Double getNumericValue(final Information e) {
		throw new UnsupportedOperationException();
	}

	/**
	 * Unused method of the framework.
	 */
	@Override
	public AbstractCompensativeAggregation<Information> fuse(
			final Collection<? extends AbstractCompensativeAggregation<? extends Information>> averages) {
		throw new UnsupportedOperationException();
	}

	/**
	 * Unused method of the framework.
	 */
	@Override
	public Information getRepresentativeElement(
			final Collection<? extends Information> elems) {
		throw new UnsupportedOperationException();
	}

	/**
	 * Unused method of the framework.
	 */
	@Override
	public Information getRepresentativeElement(
			final Map<? extends Information, Double> elems) {
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
		assert !this.nodes.values().contains(null);

		for (final VirtualNode node : this.nodes.values()) {
			if (!node.isValid()) {
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
		private final NodeParameters<VirtualNodeIdentifier> param;

		/**
		 * Parameters provided by the current host.
		 */
		private final HorizonMeasurableParameters<SubstrateNodeIdentifier> currentAllocation;

		/**
		 * Identifier of the SubstrateNode hosting this VirtualNode.
		 */
		private final SubstrateNodeIdentifier myHost;

		/**
		 * Constructs a new VirtualNode initially not hosted.
		 * 
		 * @param param
		 *            Parameters required by this node.
		 */
		private VirtualNode(final NodeParameters<VirtualNodeIdentifier> param) {
			this.param = param;
			this.myHost = null;
			this.currentAllocation = null;
		}

		/**
		 * Constructs a copy of the VirtualNode. Useful to make the VirtualNode
		 * refer to another VirtualNetworkState.
		 * 
		 * @param initial
		 *            the VirtualNode to copy
		 */
		public VirtualNode(final VirtualNode initial) {
			this.currentAllocation = initial.currentAllocation;
			this.myHost = initial.myHost;
			this.param = initial.param;
		}

		/**
		 * Tests whether the rights are respected for this VirtualNode.
		 * 
		 * @return <code>true</code> if the rights are respected
		 */
		private boolean isValid() {
			if (this.myHost == null) {
				return false;
			}
			if (!this.currentAllocation.getMachineParameters().satisfies(
					this.param.getMeasurableParams().getMachineParameters())) {
				return false;
			}
			for (final Map.Entry<VirtualNodeIdentifier, LinkMeasurableParameters> link : this.param
					.getMeasurableParams().getInterfacesParameters().entrySet()) {
				final SubstrateNodeIdentifier peerHost = VirtualNetworkState.this.nodes
						.get(link.getKey()).myHost;

				try {
					if (!peerHost.equals(this.myHost)
							&& (peerHost == null || !this.currentAllocation
							.getInterfacesParameters().get(peerHost)
							.satisfies(link.getValue()))) {
						return false;
					}
				} catch (final UnexistingLinkException e) {
					return false;
				}
			}
			return true;
		}

		/**
		 * Constructs a new state for this Node by changing its host.
		 * 
		 * @param initial
		 *            Initial state of the node
		 * @param newHost
		 *            New host (null if no longer hosted)
		 * @param params
		 *            Parameters of the host
		 */
		private VirtualNode(
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
		private NodeParameters<VirtualNodeIdentifier> getParam() {
			return this.param;
		}

		/**
		 * Gives the parameters offered by the SubstrateNode hosting this
		 * VirtualNode
		 * 
		 * @return the obtained MeasurableParameters
		 * @throws NodeNotInstanciatedException
		 *             if the node is not hosted
		 */
		private HorizonMeasurableParameters<SubstrateNodeIdentifier> getCurrentAllocation()
				throws NodeNotInstanciatedException {
			assert this.myHost == null == (this.currentAllocation == null);
			if (this.myHost == null) {
				throw new NodeNotInstanciatedException();
			} else {
				return this.currentAllocation;
			}
		}

		/**
		 * Returns the current host of this VirtualNode.
		 * 
		 * @return the value of the field myhost
		 */
		private SubstrateNodeIdentifier getMyHost() {
			return this.myHost;
		}

		/**
		 * Evaluates the utility for the packet loss rate of the links of this
		 * node.
		 * 
		 * @return the computed utility value
		 */
		private Double evaluatePacketLossRateUtility() {
			final LightAverageDoubleAggregation aggreg = new LightAverageDoubleAggregation();

			for (final Map.Entry<VirtualNodeIdentifier, LinkMeasurableParameters> demanded : this.param
					.getMeasurableParams().getInterfacesParameters().entrySet()) {
				if (VirtualNetworkState.this.nodes.get(demanded.getKey()).myHost
						.equals(this.myHost)) {
					aggreg.add(1.);
					continue;
				}

				Interval<Float> obtainedPacketLossRate;
				try {
					obtainedPacketLossRate = this.currentAllocation
							.getInterfacesParameters().get(
									VirtualNetworkState.this.nodes.get(demanded
											.getKey()).myHost)
											.getPacketLossRate();
				} catch (final UnexistingLinkException e) {
					throw new UnexpectedException(e);
				}
				final Interval<Float> demandedPacketLossRate = obtainedPacketLossRate
						.inter(demanded.getValue().getPacketLossRate());
				Double packetLossRateEval;
				try {
					packetLossRateEval = 1. / (obtainedPacketLossRate
							.getLower() + 1);
				} catch (final EmptyIntervalException e) {
					throw new UnexpectedException(e);
				}
				try {
					packetLossRateEval /= (obtainedPacketLossRate.getUpper() + obtainedPacketLossRate
							.getLower())
							* (1. - 1. / (demandedPacketLossRate.getUpper()
									- demandedPacketLossRate.getLower() + 1));
				} catch (final EmptyIntervalException e) {
					packetLossRateEval *= 1;
				}
				aggreg.add(packetLossRateEval);
			}
			return aggreg.getRepresentativeElement();
		}

		/**
		 * Evaluates the utility for the delay of the links of this node.
		 * 
		 * @return the computed utility value
		 */
		private Double evaluateDelayUtility() {
			final LightAverageDoubleAggregation aggreg = new LightAverageDoubleAggregation();

			for (final Map.Entry<VirtualNodeIdentifier, LinkMeasurableParameters> demanded : this.param
					.getMeasurableParams().getInterfacesParameters().entrySet()) {
				if (VirtualNetworkState.this.nodes.get(demanded.getKey()).myHost
						.equals(this.myHost)) {
					aggreg.add(1.);
					continue;
				}

				Interval<Integer> obtainedDelay;
				try {
					obtainedDelay = this.currentAllocation
							.getInterfacesParameters().get(
									VirtualNetworkState.this.nodes.get(demanded
											.getKey()).myHost).getDelay();
				} catch (final UnexistingLinkException e) {
					throw new UnexpectedException(e);
				}
				final Interval<Integer> satisfyingDelay = obtainedDelay
						.inter(demanded.getValue().getDelay());
				Double delayEval;
				try {
					delayEval = 1. / (obtainedDelay.getLower() + 1);
				} catch (final EmptyIntervalException e) {
					throw new UnexpectedException(e);
				}
				try {
					delayEval /= (obtainedDelay.getUpper() + obtainedDelay
							.getLower())
							* (1. - 1. / (satisfyingDelay.getUpper()
									- satisfyingDelay.getLower() + 1));
				} catch (final EmptyIntervalException e) {
					delayEval *= 1;
				}
				aggreg.add(delayEval);
			}

			return aggreg.getRepresentativeElement();
		}

		/**
		 * Evaluates the utility for the jitter of the links of this node.
		 * 
		 * @return the computed utility value
		 */
		private Double evaluateJitterUtility() {
			final LightAverageDoubleAggregation aggreg = new LightAverageDoubleAggregation();

			for (final Map.Entry<VirtualNodeIdentifier, LinkMeasurableParameters> demanded : this.param
					.getMeasurableParams().getInterfacesParameters().entrySet()) {
				if (VirtualNetworkState.this.nodes.get(demanded.getKey()).myHost
						.equals(this.myHost)) {
					aggreg.add(1.);
					continue;
				}

				Interval<Integer> obtainedJitter;
				try {
					obtainedJitter = this.currentAllocation
							.getInterfacesParameters().get(
									VirtualNetworkState.this.nodes.get(demanded
											.getKey()).myHost).getJitter();
				} catch (final UnexistingLinkException e) {
					throw new UnexpectedException(e);
				}
				final Interval<Integer> satisfyingJitter = obtainedJitter
						.inter(demanded.getValue().getJitter());
				Double jitterEval;
				try {
					jitterEval = 1. / (obtainedJitter.getLower() + 1);
				} catch (final EmptyIntervalException e) {
					throw new UnexpectedException(e);
				}
				try {
					jitterEval /= (obtainedJitter.getUpper() + obtainedJitter
							.getLower())
							* (1. - 1. / (satisfyingJitter.getUpper()
									- satisfyingJitter.getLower() + 1));
				} catch (final EmptyIntervalException e) {
					jitterEval *= 1;
				}
				aggreg.add(jitterEval);
			}
			return aggreg.getRepresentativeElement();
		}

		/**
		 * Evaluates the utility for the availability of this node.
		 * 
		 * @return the computed utility value
		 */
		private Double evaluateAvailabilityUtility() {
			final Interval<Integer> obtainedAvailability = this.currentAllocation
					.getMachineParameters().getAvailability();
			final Interval<Integer> satisfyingAvailability = obtainedAvailability
					.inter(this.param.getMeasurableParams()
							.getMachineParameters().getAvailability());
			Double availabilityEval;
			try {
				availabilityEval = 1. - 1. / (obtainedAvailability.getUpper() + 1);
			} catch (final EmptyIntervalException e) {
				throw new UnexpectedException(e);
			}
			try {
				availabilityEval *= (obtainedAvailability.getUpper() + obtainedAvailability
						.getLower())
						/ (satisfyingAvailability.getUpper()
								- satisfyingAvailability.getLower() + 1);
			} catch (final EmptyIntervalException e) {
				availabilityEval *= 1;
			}
			return availabilityEval;
		}

		/**
		 * Evaluates the aggregated utility for this node.
		 * 
		 * @return the computed utility value
		 */
		private Double evaluateUtility() {
			final LightAverageDoubleAggregation aggreg = new LightAverageDoubleAggregation();
			final Service serviceType = VirtualNetworkState.this.serviceName;
			aggreg.add(this.evaluatePacketLossRateUtility()
					/ serviceType.getPriority(0));
			aggreg
			.add(this.evaluateDelayUtility()
					/ serviceType.getPriority(1));
			aggreg.add(this.evaluateJitterUtility()
					/ serviceType.getPriority(2));
			aggreg.add(this.evaluateAvailabilityUtility()
					/ serviceType.getPriority(3));
			return aggreg.getRepresentativeElement();
		}
	}

	/**
	 * Exception to be thrown if an operation could not be performed because a
	 * node must be
	 * 
	 * @author Vincent Letard
	 */
	public class NodeNotInstanciatedException extends Exception {

		/**
		 * Serial version identifier.
		 */
		private static final long serialVersionUID = 3482865179834602964L;

	}

	/**
	 * Gives the type of service needed in this VirtualNetwork.
	 * 
	 * @return the desired quality of service
	 */
	public Service getQoS() {
		return this.serviceName;
	}

	// /**
	// * Gives the non functional parameters associated with all the nodes of
	// this
	// * network.
	// *
	// * @return
	// */
	// public List<HorizonMeasurableParameters<VirtualNodeIdentifier>>
	// getNodesPreferences() {
	// List<HorizonMeasurableParameters<VirtualNodeIdentifier>> nodesPrefs = new
	// ArrayList<HorizonMeasurableParameters<VirtualNodeIdentifier>>();
	// for (VirtualNode node : this.nodes.values()) {
	// nodesPrefs.add(node.getParam().getMeasurableParams());
	// }
	// return nodesPrefs;
	// }

	// public List<HorizonMeasurableParameters<SubstrateNodeIdentifier>>
	// getNodesCurrentService()
	// throws NodeNotInstanciatedException {
	// List<HorizonMeasurableParameters<SubstrateNodeIdentifier>> nodesPrefs =
	// new ArrayList<HorizonMeasurableParameters<SubstrateNodeIdentifier>>();
	// for (VirtualNode node : this.nodes.values()) {
	// nodesPrefs.add(node.getCurrentAllocation());
	// }
	// return nodesPrefs;
	// }

	// public int getNodesCount() {
	// return this.nodes.size();
	// }

	/**
	 * Computes a utility value for this VirtualNetworkState. Needs the state to
	 * be valid !
	 * 
	 * @return an evaluation of the network satisfaction.
	 */
	public Double evaluateUtility() {
		assert this.isValid();
		final LightAverageDoubleAggregation aggregation = new LightAverageDoubleAggregation();
		for (final VirtualNode node : this.nodes.values()) {
			aggregation.add(node.evaluateUtility());
		}
		return aggregation.getRepresentativeElement();
	}

	/**
	 * Compares this VirtualNetworkState to another to determine the most
	 * satisfying. Respects the contract of java.lang.Comparable.compareTo
	 */
	@Override
	public int compareTo(final VirtualNetworkState state) {
		final LightAverageDoubleAggregation[][] aggregs = {
				{ new LightAverageDoubleAggregation(),
					new LightAverageDoubleAggregation(),
					new LightAverageDoubleAggregation(),
					new LightAverageDoubleAggregation() },
					{ new LightAverageDoubleAggregation(),
						new LightAverageDoubleAggregation(),
						new LightAverageDoubleAggregation(),
						new LightAverageDoubleAggregation() } };

		for (final VirtualNode node : this.nodes.values()) {
			aggregs[0][0].add(node.evaluatePacketLossRateUtility());
			aggregs[0][1].add(node.evaluateDelayUtility());
			aggregs[0][2].add(node.evaluateJitterUtility());
			aggregs[0][3].add(node.evaluateAvailabilityUtility());
		}

		for (final VirtualNode node : state.nodes.values()) {
			aggregs[1][0].add(node.evaluatePacketLossRateUtility());
			aggregs[1][1].add(node.evaluateDelayUtility());
			aggregs[1][2].add(node.evaluateJitterUtility());
			aggregs[1][3].add(node.evaluateAvailabilityUtility());
		}

		final List<Integer> comparedValues = new ArrayList<Integer>();
		comparedValues.add(aggregs[0][0].getRepresentativeElement().compareTo(
				aggregs[1][0].getRepresentativeElement()));
		comparedValues.add(aggregs[0][1].getRepresentativeElement().compareTo(
				aggregs[1][1].getRepresentativeElement()));
		comparedValues.add(aggregs[0][2].getRepresentativeElement().compareTo(
				aggregs[1][2].getRepresentativeElement()));
		comparedValues.add(aggregs[0][3].getRepresentativeElement().compareTo(
				aggregs[1][3].getRepresentativeElement()));
		for (final int value : this.serviceName.sort(comparedValues)) {
			if (value != 0) {
				return value;
			}
		}
		return 0;
	}
}