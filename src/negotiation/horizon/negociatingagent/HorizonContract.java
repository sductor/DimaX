package negotiation.horizon.negociatingagent;

import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import negotiation.negotiationframework.contracts.ContractTransition;
import dima.basicagentcomponents.AgentIdentifier;

public class HorizonContract extends ContractTransition<HorizonSpecification> {

    /**
     * Serial version identifier.
     */
    private static final long serialVersionUID = 2543486525336033954L;

    // private final ContractTransition<HorizonSpecification> canonicalContract;
    private final Set<HorizonCandidature> candidatureSet;

    public HorizonContract(final VirtualNetworkIdentifier initiator,
	    final long validityTime, final Set<HorizonCandidature> candidatures) {
	super(initiator, retrieveActors(candidatures), validityTime);
	this.candidatureSet = Collections.unmodifiableSet(candidatures);
    }

    /**
     * Makes the list of the actors of the contract using the set in argument.
     * 
     * @param candidatures
     *            Set of bilateral contracts.
     * @return the list of all the actors of this contract.
     */
    private static List<AgentIdentifier> retrieveActors(
	    Set<HorizonCandidature> candidatures) {
	List<AgentIdentifier> actors = new LinkedList<AgentIdentifier>();
	Iterator<HorizonCandidature> it = candidatures.iterator();
	while (it.hasNext()) {
	    actors.addAll(it.next().getAllParticipants());
	}
	return actors;
    }

    @Override
    public VirtualNetworkIdentifier getInitiator() {
	return (VirtualNetworkIdentifier) super.getInitiator();
    }

    @Override
    public <State extends HorizonSpecification> State computeResultingState(
	    State s) throws IncompleteContractException {
	throw new RuntimeException("Unexpected behavior");
    }

    public VirtualNetworkState computeResultingState(VirtualNetworkState s)
	    throws IncompleteContractException {
	for (HorizonCandidature c : this.candidatureSet) {
	    s = c.computeResultingState(s);
	}
	return s;

	// if (!this.getInitiator().equals(s.getMyAgentIdentifier())) {
	// return s;
	// } else {
	// Iterator<_VirtualNodeState> it = ((VirtualNetworkState) s)
	// .getNodes().iterator();
	// Set<_VirtualNodeState> nodelist = new HashSet<_VirtualNodeState>();
	// while (it.hasNext()) {
	// nodelist.add(this.computeResultingState(it.next()));
	// }
	// return new VirtualNetworkState(((VirtualNetworkState) s)
	// .getMyAgentIdentifier(), 0 , nodelist,
	// ((VirtualNetworkState) s).getLinks());
	// }
    }

    @Override
    public HorizonSpecification computeResultingState(final AgentIdentifier id)
	    throws IncompleteContractException {
	return this.computeResultingState(this.getSpecificationOf(id));
    }

    @Override
    public HorizonSpecification getSpecificationOf(final AgentIdentifier id) {
	throw new RuntimeException("Unexpected behavior");
    }

    public SubstrateNodeState getSpecificationOf(
	    final SubstrateNodeIdentifier id)
	    throws IncompleteContractException {
	return (SubstrateNodeState) super.getSpecificationOf(id);
    }

    public VirtualNetworkState getSpecificationOf(
	    final VirtualNetworkIdentifier id)
	    throws IncompleteContractException {
	return (VirtualNetworkState) super.getSpecificationOf(id);
    }
}
