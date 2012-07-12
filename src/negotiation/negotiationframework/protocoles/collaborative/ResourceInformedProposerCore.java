package negotiation.negotiationframework.protocoles.collaborative;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import negotiation.negotiationframework.SimpleNegotiatingAgent;
import negotiation.negotiationframework.contracts.ContractTrunk;
import negotiation.negotiationframework.contracts.MatchingCandidature;
import negotiation.negotiationframework.protocoles.AbstractCommunicationProtocol;
import negotiation.negotiationframework.protocoles.AbstractCommunicationProtocol.ProposerCore;
import negotiation.negotiationframework.rationality.AgentState;
import dima.introspectionbasedagents.services.BasicAgentCompetence;
import dima.introspectionbasedagents.shells.NotReadyException;

public class ResourceInformedProposerCore<
Contract extends MatchingCandidature,
PersonalState extends AgentState>
extends BasicAgentCompetence<SimpleNegotiatingAgent<PersonalState, InformedCandidature<Contract>>>
implements ProposerCore<
SimpleNegotiatingAgent< PersonalState, InformedCandidature<Contract>>,
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


	@Override
	public boolean ImAllowedToNegotiate(
			final ContractTrunk<InformedCandidature<Contract>> contracts) {
		return true;
	}

}
