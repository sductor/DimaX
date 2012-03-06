package negotiation.horizon.negociatingagent;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import negotiation.negotiationframework.agent.SimpleAgentState;
import negotiation.negotiationframework.interaction.contracts.ResourceIdentifier;
import dima.basicagentcomponents.AgentIdentifier;
import dima.introspectionbasedagents.services.library.information.ObservationService.Information;
import dimaxx.tools.aggregator.AbstractCompensativeAggregation;

public class VirtualNetworkState extends SimpleAgentState implements
	HorizonSpecification {

    /**
     * Serial version identifier.
     */
    private static final long serialVersionUID = -5576314995630464103L;

    /**
     * Collection of the states of the virtual nodes constituting this virtual
     * network.
     */
    private Set<VirtualNodeState> nodelist;
    /**
     * Which nodes are connected ? The Integer values are indexes of the nodes
     * in the nodelist.
     */
    private Map<Integer, List<Integer>> links;

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
    public VirtualNetworkState(AgentIdentifier myAgent, int stateNumber,
	    Set<VirtualNodeState> nodelist, Map<Integer, List<Integer>> links) {
	super(myAgent, stateNumber);
	// TODO Auto-generated constructor stub
    }

    @Override
    public Long getCreationTime() {
	// TODO Auto-generated method stub
	return null;
    }

    @Override
    public AgentIdentifier getMyAgentIdentifier() {
	// TODO Auto-generated method stub
	return null;
    }

    @Override
    public long getUptime() {
	// TODO Auto-generated method stub
	return 0;
    }

    @Override
    public int isNewerThan(Information that) {
	// TODO Auto-generated method stub
	return 0;
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
    public Collection<? extends AgentIdentifier> getMyResourceIdentifiers() {
	// TODO Auto-generated method stub
	return null;
    }

    @Override
    public Class<? extends Information> getMyResourcesClass() {
	return SubstrateNodeState.class;
    }

    @Override
    public int getStateCounter() {
	// TODO Auto-generated method stub
	return 0;
    }

    @Override
    public boolean isValid() {
	// TODO Auto-generated method stub
	return false;
    }

    @Override
    public boolean setLost(ResourceIdentifier h, boolean isLost) {
	// TODO Auto-generated method stub
	return false;
    }

}
