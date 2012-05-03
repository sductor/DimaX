package negotiation.horizon;

import java.util.Set;

import negotiation.horizon.experimentation.VirtualNetwork;
import negotiation.horizon.negociatingagent.HorizonContract;
import negotiation.horizon.negociatingagent.HorizonSpecification;
import negotiation.horizon.negociatingagent.VirtualNetworkState;
import negotiation.negotiationframework.contracts.ContractTrunk;
import negotiation.negotiationframework.protocoles.AbstractCommunicationProtocol.ProposerCore;
import dima.introspectionbasedagents.services.BasicAgentCompetence;
import dima.introspectionbasedagents.shells.NotReadyException;

public class VirtualNetworkContractProposer extends
	BasicAgentCompetence<VirtualNetwork>
	implements
	ProposerCore<VirtualNetwork, HorizonSpecification, VirtualNetworkState, HorizonContract> {

    /**
     * Serial version identifier.
     */
    private static final long serialVersionUID = -1795097423217512523L;

    @Override
    public Set<? extends HorizonContract> getNextContractsToPropose()
	    throws NotReadyException {
	this.getMyAgent().getResource(null);
    }

    @Override
    public boolean IWantToNegotiate(
	    VirtualNetworkState myCurrentState,
	    ContractTrunk<HorizonContract, HorizonSpecification, VirtualNetworkState> contracts) {
	// TODO Auto-generated method stub
	return false;
    }

    @Override
    public boolean ImAllowedToNegotiate(
	    VirtualNetworkState myCurrentState,
	    ContractTrunk<HorizonContract, HorizonSpecification, VirtualNetworkState> contracts) {
	// TODO Auto-generated method stub
	return false;
    }

}
