package frameworks.negotiation.negotiationframework.protocoles.collaborative;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;


import dima.introspectionbasedagents.kernel.NotReadyException;
import dima.introspectionbasedagents.services.BasicAgentCompetence;
import frameworks.negotiation.negotiationframework.NegotiatingAgent;
import frameworks.negotiation.negotiationframework.contracts.ContractTrunk;
import frameworks.negotiation.negotiationframework.contracts.MatchingCandidature;
import frameworks.negotiation.negotiationframework.protocoles.AbstractCommunicationProtocol;
import frameworks.negotiation.negotiationframework.protocoles.AbstractCommunicationProtocol.ProposerCore;
import frameworks.negotiation.negotiationframework.rationality.AgentState;

public class ResourceInformedProposerCore<
Contract extends MatchingCandidature,
PersonalState extends AgentState>
extends BasicAgentCompetence<NegotiatingAgent<PersonalState, InformedCandidature<Contract>>>
implements ProposerCore<
NegotiatingAgent< PersonalState, InformedCandidature<Contract>>,
PersonalState,
InformedCandidature<Contract>> {

	/**
	 *
	 */
	private static final long serialVersionUID = -2607277289289395798L;
	private final Collection<InformedCandidature<Contract>> contractsToPropose =
			new HashSet<InformedCandidature<Contract>>();


	public void addContractsToPropose(
			final Collection<InformedCandidature<Contract>> contractsToPropose) {
		this.contractsToPropose.addAll(contractsToPropose);
	}


	@Override
	public Set<? extends InformedCandidature<Contract>> getNextContractsToPropose()
			throws NotReadyException {
		this.logMonologue("proposing "+this.contractsToPropose, AbstractCommunicationProtocol.log_negotiationStep);
		final Set<InformedCandidature<Contract>> result =
				new HashSet<InformedCandidature<Contract>>();
		result.addAll(this.contractsToPropose);
		this.contractsToPropose.clear();
		return result;
	}


	@Override
	public boolean IWantToNegotiate(
			final ContractTrunk<InformedCandidature<Contract>> contracts) {
		return !this.contractsToPropose.isEmpty();
	}


}
