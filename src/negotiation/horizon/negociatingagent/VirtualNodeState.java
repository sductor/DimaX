package negotiation.horizon.negociatingagent;

import java.util.Collection;
import java.util.HashSet;

import negotiation.negotiationframework.contracts.ResourceIdentifier;
import dima.basicagentcomponents.AgentIdentifier;
import dima.introspectionbasedagents.services.information.ObservationService.Information;

/**
 * The state of a virtual node. The fields inherited from
 * {@link AbstractSingleNodeState} represent the requirements of the agent.
 * 
 * @author Vincent Letard
 */
public class VirtualNodeState extends AbstractSingleNodeState {

    /**
     * Serial version identifier.
     */
    private static final long serialVersionUID = -2721088186118421802L;

    /**
     * The substrate node being running this virtual node.
     */
    private ResourceIdentifier host = null;

    /**
     * Constructs a new VirtualNodeState, initially hosted nowhere (host =
     * null).
     * 
     * @param myAgent
     * @param stateNumber
     * @param packetLossRate
     * @param delay
     * @param jitter
     * @param bandwidth
     * @param processor
     * @param ram
     */
    public VirtualNodeState(AgentIdentifier myAgent, int stateNumber,
	    float packetLossRate, int delay, int jitter, int bandwidth,
	    int processor, int ram) {
	super(myAgent, stateNumber, packetLossRate, delay, jitter, bandwidth,
		processor, ram);
	this.host = null;
    }

    /**
     * @return the identifier of the agent hosting this VirtualNode if there is
     *         one, null otherwise.
     */
    public ResourceIdentifier getHost() {
	return this.host;
    }

    /**
     * Defines the resource hosting this VirtualNode.
     * 
     * @param host
     */
    public void setHost(ResourceIdentifier host) {
	// TODO assert precondition on host ?
	this.host = host;
    }

    @Override
    public Collection<? extends AgentIdentifier> getMyResourceIdentifiers() {
	Collection<ResourceIdentifier> c = new HashSet<ResourceIdentifier>();
	c.add(this.host);

	assert (c.size() == 1);
	return c;
    }

    @Override
    public Class<? extends Information> getMyResourcesClass() {
	return SubstrateNodeState.class;
    }

    @Override
    public boolean isValid() {
	return (this.host != null);
    }

}
