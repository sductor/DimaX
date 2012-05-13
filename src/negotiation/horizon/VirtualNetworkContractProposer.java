package negotiation.horizon;

import java.util.HashSet;
import java.util.Set;

import negotiation.horizon.experimentation.VirtualNetwork;
import negotiation.horizon.negotiatingagent.HorizonCandidature;
import negotiation.horizon.negotiatingagent.HorizonParameters;
import negotiation.horizon.negotiatingagent.VirtualNetworkIdentifier;
import negotiation.horizon.negotiatingagent.VirtualNetworkState;
import negotiation.negotiationframework.NegotiationParameters;
import negotiation.negotiationframework.contracts.ContractTrunk;
import negotiation.negotiationframework.contracts.ReallocationContract;
import negotiation.negotiationframework.contracts.ResourceIdentifier;
import negotiation.negotiationframework.protocoles.AtMostKCandidaturesProposer;
import dima.introspectionbasedagents.services.UnrespectedCompetenceSyntaxException;

public class VirtualNetworkContractProposer
	extends
	AtMostKCandidaturesProposer<HorizonParameters, VirtualNetworkState, ReallocationContract<HorizonCandidature, HorizonParameters>> {

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
