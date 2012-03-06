package negotiation.horizon.negociatingagent;

import java.util.Set;

import negotiation.negotiationframework.AbstractCommunicationProtocol.ProposerCore;
import dima.introspectionbasedagents.services.BasicAgentCompetence;
import dima.introspectionbasedagents.shells.NotReadyException;

public class VirtualNetworkContractProposer
extends BasicAgentCompetence<VirtualNetwork>
	implements
	ProposerCore<VirtualNetwork, HorizonSpecification, VirtualNetworkState, HorizonContract> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1795097423217512523L;

	@Override
	public Set<? extends HorizonContract> getNextContractsToPropose()
			throws NotReadyException {
		// TODO Auto-generated method stub
		return null;
	}

}
