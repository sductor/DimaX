package negotiation.horizon.negociatingagent;

<<<<<<< HEAD
import java.util.Collection;
import java.util.Collections;
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
import dimaxx.tools.mappedcollections.HashedHashSet;

/**
 * The state of a SubstrateNode. The fields inherited from
 * {@link AbstractSingleNodeState} represent the level of service provided by
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

    private final SingleNodeParameters nodeParams;
    private final SingleNodeParameters availableNodeParams;
    /**
     * List of the {@link LinkParameters} of each network interface of the node.
     * Contains as much items as there are network interfaces. This List is an
     * unmodifiable list since these parameters are not expected to change.
     */
    private final List<LinkParameters> ifacesParams;
    private final List<LinkParameters> availableIfacesParams;
    private final List<SubstrateNodeState> pairedNodes;
    // TODO classe interne
    
    private final HashedHashSet<VirtualNetworkState, Integer> nodesHosted;

    /**
     * Constructs a new SubstrateNodeState using the provided parameters.
     */
    public SubstrateNodeState(final AgentIdentifier myAgent,
	    final int stateNumber, final SingleNodeParameters nodeParams,
	    final List<LinkParameters> ifacesParams,
	    final List<SubstrateNodeState> pairedNodes) {
	super(myAgent, stateNumber);
	this.nodeParams = nodeParams;
	this.ifacesParams = Collections.unmodifiableList(ifacesParams);
	this.availableNodeParams = nodeParams;
	this.availableIfacesParams = this.ifacesParams;
	this.nodesHosted = new HashedHashSet<VirtualNetworkState, Integer>();
	this.pairedNodes = Collections.unmodifiableList(pairedNodes);
    }

    public SubstrateNodeState(final SubstrateNodeState initial,
	    final VirtualNetworkState newNodeNetwork,
	    final Integer newNodeNumber) {
	super(initial.getMyAgentIdentifier(), initial.getStateCounter() + 1);
	this.nodeParams = initial.nodeParams;
	this.ifacesParams = initial.ifacesParams;
	this.pairedNodes = initial.pairedNodes;
	SingleNodeParameters availableNodeParams = null;
	List<LinkParameters> availableIfacesParams = null;
	this.nodesHosted = new HashedHashSet<VirtualNetworkState, Integer>();
	Iterator<Entry<VirtualNetworkState, Set<Integer>>> networksIt = initial.nodesHosted
		.entrySet().iterator();
	boolean allocation = true;

	while (networksIt.hasNext()) {
	    Entry<VirtualNetworkState, Set<Integer>> ent = networksIt.next();

	    if (ent.getKey().equals(newNodeNetwork)
		    && ent.getValue().contains(newNodeNumber)) {
		allocation = false;
		Set<Integer> networkNodes = new HashSet<Integer>(ent.getValue());
		networkNodes.remove(newNodeNumber);
		this.nodesHosted.put(newNodeNetwork, networkNodes);
		availableNodeParams = new SingleNodeParameters(
			initial.availableNodeParams.getProcessor()
				+ newNodeNetwork.getNodeParams(newNodeNumber)
					.getProcessor(),
			initial.availableNodeParams.getRam()
				+ newNodeNetwork.getNodeParams(newNodeNumber)
					.getRam());
	    } else {
		this.nodesHosted.put(newNodeNetwork, new HashSet<Integer>(ent
			.getValue()));
	    }
	}

	this.availableNodeParams = availableNodeParams;
    }

    private SubstrateNodeState(final ResourceIdentifier myAgent,
	    final int stateNumber, final SingleNodeParameters nodeParams,
	    final List<LinkParameters> ifacesParams) {
	super(myAgent, stateNumber);
	this.nodeParams = nodeParams;
	this.ifacesParams = Collections.unmodifiableList(ifacesParams);
    }

    public SingleNodeParameters getNodeParams() {
	return nodeParams;
    }

    public SingleNodeParameters getAvailableNodeParams() {
	return availableNodeParams;
    }

    public List<LinkParameters> getIfacesParams() {
	return ifacesParams;
    }

    public List<LinkParameters> getAvailableIfacesParams() {
	return availableIfacesParams;
    }

    /**
     * Modifies the amount of allocated processor.
     * 
     * @param amount
     *            The amount allocated/freed (could be positive or negative).
     */
    public void allocateProcessor(int amount) {
	int new_val = amount + this.allocatedProcessor;
	assert (new_val >= 0);
	this.allocatedProcessor = new_val;
    }

    /**
     * Checks the validity of the new value of allocatedProcessor, which must be
     * positive and lower than the available amount.
     * 
     * @param new_val
     *            the new_value computed
     * @return false if the value is not valid, true otherwise.
     */
    private boolean checkProcUpdate(int new_val) {
	return (new_val >= 0 && new_val <= this.getProc());
    };

    /**
     * @return the value of the field allocatedRAM.
     */
    public int getAllocatedRAM() {
	return this.allocatedRAM;
    }

    /**
     * Modifies the amount of allocated ram.
     * 
     * @param amount
     *            The amount allocated/freed (could be positive or negative).
     */
    public void allocateRAM(int amount) {
	int new_val = amount + this.allocatedRAM;
	assert (new_val >= 0);
	this.allocatedRAM = new_val;
    }

    /**
     * Checks the validity of the new value of allocatedRAM, which must be
     * positive and lower than the available amount.
     * 
     * @param new_val
     *            the new_value computed
     * @return false if the value is not valid, true otherwise.
     */
    private boolean checkRAMUpdate(int new_val) {
	return (new_val >= 0 && new_val <= this.getRAM());
    };

    @Override
    public Collection<? extends AgentIdentifier> getMyResourceIdentifiers() {
	// TODO Auto-generated method stub
	return null;
    }

    @Override
    public Class<? extends Information> getMyResourcesClass() {
	return _VirtualNodeState.class;
    }

    @Override
    public boolean isValid() {
	return (this.allocatedProcessor >= 0
		&& this.allocatedProcessor <= this.getProc()
		&& this.allocatedRAM >= 0 && this.allocatedRAM <= this.getRAM());
    }

    @Override
    public boolean setLost(ResourceIdentifier h, boolean isLost) {
	// TODO Auto-generated method stub
	return false;
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

}
=======
public class SubstrateNodeState extends HorizonSpecification {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2415474717777657012L;

}
>>>>>>> ff05a2abd98b6bd6d7d50c25d4150cd3add7b19a
