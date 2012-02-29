package negotiation.horizon.negociatingagent;

import java.util.Set;

import negotiation.negotiationframework.interaction.consensualnegotiation.AbstractProposerCore;
import dima.introspectionbasedagents.NotReadyException;
import dima.introspectionbasedagents.services.BasicAgentCompetence;

public class VirtualNetworkContractProposer
extends BasicAgentCompetence<VirtualNetwork>
implements AbstractProposerCore<VirtualNetwork, HorizonSpecification, VirtualNetworkState, HorizonContract> {

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
