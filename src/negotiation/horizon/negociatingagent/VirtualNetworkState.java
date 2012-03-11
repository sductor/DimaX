package negotiation.horizon.negociatingagent;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;

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
     * Set of the nodes in the VirtualNetwork
     */
    private Set<VirtualNodeState> nodelist;

    /**
     * Associates each VirtualNodeState with
     */
    private ReflexiveAdjacencyMap<VirtualNodeState> links;

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
	    Set<VirtualNodeState> nodelist,
	    ReflexiveAdjacencyMap<VirtualNodeState> links) {
	super(myAgent, stateNumber);
	this.links = links;
	this.nodelist = nodelist;
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
	Collection<AgentIdentifier> c = new LinkedList<AgentIdentifier>();
	Iterator<VirtualNodeState> it = this.nodelist.iterator();
	while (it.hasNext())
	    c.add(it.next().getMyAgentIdentifier());
	return c;
    }

    @Override
    public Class<? extends Information> getMyResourcesClass() {
	return SubstrateNodeState.class;
    }

    @Override
    public boolean isValid() {
	Iterator<VirtualNodeState> it = this.nodelist.iterator();
	boolean valid = true;
	while (valid && it.hasNext())
	    valid = it.next().isValid() && valid;
	return valid;
    }

    @Override
    public boolean setLost(ResourceIdentifier h, boolean isLost) {
	// TODO Auto-generated method stub
	return false;
    }

}
