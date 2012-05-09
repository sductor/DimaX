package negotiation.horizon;

import java.util.HashSet;
import java.util.Set;

import negotiation.horizon.experimentation.VirtualNetwork;
import negotiation.horizon.negociatingagent.HorizonCandidature;
import negotiation.horizon.negociatingagent.HorizonContract;
import negotiation.horizon.negociatingagent.HorizonSpecification;
import negotiation.horizon.negociatingagent.VirtualNetworkIdentifier;
import negotiation.horizon.negociatingagent.VirtualNetworkState;
import negotiation.negotiationframework.NegotiationParameters;
import negotiation.negotiationframework.contracts.ContractTrunk;
import negotiation.negotiationframework.contracts.ResourceIdentifier;
import negotiation.negotiationframework.protocoles.AtMostKCandidaturesProposer;
import dima.introspectionbasedagents.services.UnrespectedCompetenceSyntaxException;

public class VirtualNetworkContractProposer
	extends
	AtMostKCandidaturesProposer<HorizonSpecification, VirtualNetworkState, HorizonContract> {

    /**
     * Serial version identifier.
     */
    private static final long serialVersionUID = -1795097423217512523L;

    /**
     * Constructs a VirtualNetworkContractProposer for the agent ag which
     * proposes at most k contracts.
     * 
     * @param ag
     * @param k
     * @throws UnrespectedCompetenceSyntaxException
     */
    public VirtualNetworkContractProposer(final VirtualNetwork agent,
	    final int k) throws UnrespectedCompetenceSyntaxException {
	super(k);
	this.setMyAgent(agent);
    }

    @Override
    public boolean IWantToNegotiate(
	    VirtualNetworkState myCurrentState,
	    ContractTrunk<HorizonContract, HorizonSpecification, VirtualNetworkState> contracts) {
	return true; // TODO ok ?
    }

    @Override
    public VirtualNetwork getMyAgent() {
	return (VirtualNetwork) super.getMyAgent();
    }

    @Override
    public VirtualNetworkIdentifier getIdentifier() {
	return this.getMyAgent().getIdentifier();
    }

    @Override
    public HorizonContract constructCandidature(ResourceIdentifier id) {
	Set<HorizonCandidature> candidaturesSet = new HashSet<HorizonCandidature>();

	return new HorizonContract(this.getIdentifier(),
		NegotiationParameters._contractExpirationTime, candidaturesSet);
    }
}
