package negotiation.horizon.negociatingagent;

import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import negotiation.negotiationframework.contracts.ContractTransition;
import dima.basicagentcomponents.AgentIdentifier;

public class HorizonContract extends
 ContractTransition<HorizonSpecification> {

    /**
     * Serial version identifier.
     */
    private static final long serialVersionUID = 2543486525336033954L;

    // private final ContractTransition<HorizonSpecification> canonicalContract;
    private final Set<HorizonCandidature> candidatureSet;

    public HorizonContract(final _VirtualNetworkIdentifier initiator,
	    final long validityTime,
	    Set<HorizonCandidature> candidatures) {
	super(initiator, retrieveActors(candidatures), validityTime);
	this.candidatureSet = new HashSet<HorizonCandidature>(candidatures);
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
	/* XXX pk actors est une List ? */
	while (it.hasNext()) {
	    actors.addAll(it.next().getAllParticipants());
	}
	return actors;
    }

    @Override
    public _VirtualNetworkIdentifier getInitiator() {
	return (_VirtualNetworkIdentifier) super.getInitiator();
    }

    @Override
    public <State extends HorizonSpecification> State computeResultingState(
	    State s) throws IncompleteContractException {
	assert (false); // XXX Bonne solution ?
	throw new RuntimeException("Unexpected behavior");
    }

    public VirtualNetworkState computeResultingState(
	    VirtualNetworkState s) throws IncompleteContractException {
	if (!this.getInitiator().equals(s.getMyAgentIdentifier())) {
	    return s;
	} else {
	    Iterator<_VirtualNodeState> it = ((VirtualNetworkState) s)
		    .getNodes().iterator();
	    Set<_VirtualNodeState> nodelist = new HashSet<_VirtualNodeState>();
	    while (it.hasNext()) {
		nodelist.add(this.computeResultingState(it.next()));
	    }
	    return new VirtualNetworkState(((VirtualNetworkState) s)
		    .getMyAgentIdentifier(), 0 /* XXX */, nodelist,
		    ((VirtualNetworkState) s).getLinks());
	}
    }

    public _VirtualNodeState computeResultingState(_VirtualNodeState s)
	    throws IncompleteContractException {
	if (!this.getAllParticipants().contains(s.getMyAgentIdentifier()))
	    return s;
	else {
	    Iterator<HorizonCandidature> it = this.candidatureSet.iterator();
	    while (it.hasNext()){
		HorizonCandidature candidature = it.next();
		if (candidature.getAgent().getId().equals(s.getMyAgentIdentifier())){
		    if (candidature.isMatchingCreation() && !candidature.getResource().equals(s.getHost())){
			// TODO regrouper les paramètres sous Parameters
			_VirtualNodeState newVNS = new _VirtualNodeState(s
				.getMyAgentIdentifier(), 0 /* XXX ? */,
				candidature.getResource(),
				packetLossRate, delay, jitter, bandwidth,
				processor, ram);
		    }
		}
	    }
	}
    }

    @Override
    public <State extends HorizonSpecification> State computeResultingState(
	    AgentIdentifier id) throws IncompleteContractException {
	// TODO Auto-generated method stub
	return null;
    }
}
