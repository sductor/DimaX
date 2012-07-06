package negotiation.horizon.contracts;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import negotiation.horizon.negotiatingagent.SubstrateNodeIdentifier;
import negotiation.horizon.negotiatingagent.SubstrateNodeState;
import negotiation.horizon.negotiatingagent.VirtualNetworkIdentifier;
import negotiation.horizon.negotiatingagent.VirtualNetworkIdentifier.VirtualNodeIdentifier;
import negotiation.horizon.parameters.HorizonAllocableParameters;
import negotiation.horizon.parameters.InterfacesParameters;
import negotiation.horizon.parameters.LinkAllocableParameters;
import negotiation.negotiationframework.contracts.ReallocationContract;
import dima.basicagentcomponents.AgentIdentifier;

/**
 * One HorizonContract is the ReallocationContract gathering all the
 * HorizonCandidatures for a given VirtualNetwork, i.e. it is the complete
 * reallocation of the VirtualNodes of the network.
 * 
 * @author Vincent Letard
 */
public class HorizonContract extends ReallocationContract<HorizonCandidature> {

    /**
     * Serial version identifier.
     */
    private static final long serialVersionUID = 7804749910350934001L;

    /**
     * Map of the complete allocation specified by this contrat.
     */
    private final Map<VirtualNodeIdentifier, SubstrateNodeIdentifier> allocationMap;

    /**
     * Constructs a new HorizonContract created by the specified VirtualNetwork
     * and composed by the specified set of candidatures.
     * 
     * @param creator
     *            Initiator of the contract.
     * @param actions
     *            Set of HorizonCandidatures of the contract.
     */
    public HorizonContract(VirtualNetworkIdentifier creator,
	    HorizonCandidature[] actions) {
	super(creator, actions);
	this.allocationMap = this.createAllocationMap();
    }

    /**
     * Constructs a new HorizonContract created by the specified VirtualNetwork
     * and composed by the specified set of candidatures.
     * 
     * @param creator
     *            Initiator of the contract.
     * @param actions
     *            Set of HorizonCandidatures of the contract.
     */
    public HorizonContract(AgentIdentifier creator,
	    Collection<HorizonCandidature> actions) {
	super(creator, actions);
	this.allocationMap = this.createAllocationMap();
    }

    /**
     * Private method initializing the allocation map using the candidatures of
     * the contract.
     * 
     * @return the created map.
     */
    private Map<VirtualNodeIdentifier, SubstrateNodeIdentifier> createAllocationMap() {
	final Map<VirtualNodeIdentifier, SubstrateNodeIdentifier> allocationMap = new HashMap<VirtualNodeIdentifier, SubstrateNodeIdentifier>();
	for (HorizonCandidature candidature : this)
	    allocationMap.put(candidature.getNode(), candidature.getResource());
	return allocationMap;
    }

    // @Override
    // public HorizonSpecification getSpecificationOf(AgentIdentifier id)
    // throws IncompleteContractException {
    // if (id instanceof SubstrateNodeIdentifier)
    // return super.getSpecificationOf(id);
    // else if (id instanceof VirtualNetworkIdentifier) {
    // if (!this.networkSpecifs.containsKey(id)) {
    // final Map<VirtualNodeIdentifier, SubstrateNodeIdentifier> specifMap = new
    // HashMap<VirtualNodeIdentifier, SubstrateNodeIdentifier>();
    // for (HorizonCandidature candidature : this) {
    // try {
    // specifMap.put(candidature.getSpecificationOf(
    // (VirtualNetworkIdentifier) id).getNode(),
    // candidature.getResource());
    // } catch (final IncompleteContractException e) {
    // // Nothing
    // }
    // }
    // if (specifMap.isEmpty())
    // throw new IncompleteContractException();
    // this.networkSpecifs.put((VirtualNetworkIdentifier) id,
    // new VirtualNetworkSpecification(
    // (VirtualNetworkIdentifier) id, specifMap));
    // }
    // return this.networkSpecifs.get(id);
    // } else
    // throw new IllegalArgumentException();
    // }

    public SubstrateNodeState computeResultingState(final SubstrateNodeState s)
	    throws negotiation.negotiationframework.contracts.AbstractContractTransition.IncompleteContractException {
	SubstrateNodeState resultingState = s;
	for (HorizonCandidature candidature : this) {
	    HorizonAllocableParameters<VirtualNodeIdentifier> initialParams = candidature
		    .getInitialState(candidature.getAgent()).getNodeParams(
			    candidature.getNode()).getAllocableParams();

	    final Map<SubstrateNodeIdentifier, LinkAllocableParameters> switchingIfaceParams = new HashMap<SubstrateNodeIdentifier, LinkAllocableParameters>();
	    for (Map.Entry<VirtualNodeIdentifier, LinkAllocableParameters> entry : initialParams
		    .getInterfacesParameters().entrySet()) {
		switchingIfaceParams.put(
			this.allocationMap.get(entry.getKey()), entry
				.getValue());
	    }

	    HorizonAllocableParameters<SubstrateNodeIdentifier> params = new HorizonAllocableParameters<SubstrateNodeIdentifier>(
		    initialParams.getMachineParameters(),
		    new InterfacesParameters<SubstrateNodeIdentifier, LinkAllocableParameters>(
			    switchingIfaceParams));
	    resultingState = new SubstrateNodeState(resultingState, candidature
		    .getAgent(), params, candidature.isMatchingCreation());
	}
	return resultingState;
    }
}
