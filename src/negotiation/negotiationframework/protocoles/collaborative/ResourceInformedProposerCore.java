package negotiation.negotiationframework.protocoles.collaborative;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import negotiation.negotiationframework.SimpleNegotiatingAgent;
import negotiation.negotiationframework.contracts.AbstractActionSpecif;
import negotiation.negotiationframework.contracts.ContractTrunk;
import negotiation.negotiationframework.contracts.InformedCandidature;
import negotiation.negotiationframework.contracts.MatchingCandidature;
import negotiation.negotiationframework.protocoles.AbstractCommunicationProtocol;
import negotiation.negotiationframework.protocoles.AbstractCommunicationProtocol.ProposerCore;
import negotiation.negotiationframework.rationality.AgentState;
import dima.introspectionbasedagents.services.BasicAgentCompetence;
import dima.introspectionbasedagents.shells.NotReadyException;

public class ResourceInformedProposerCore<
Contract extends MatchingCandidature<ActionSpec>,
ActionSpec extends AbstractActionSpecif,
PersonalState extends AgentState>
extends BasicAgentCompetence<SimpleNegotiatingAgent<ActionSpec, PersonalState, InformedCandidature<Contract, ActionSpec>>>
implements ProposerCore<
SimpleNegotiatingAgent<ActionSpec, PersonalState, InformedCandidature<Contract,ActionSpec>>,
ActionSpec,
PersonalState,
InformedCandidature<Contract,ActionSpec>> {

	/**
	 *
	 */
	private static final long serialVersionUID = -2607277289289395798L;
	private final Collection<InformedCandidature<Contract, ActionSpec>> contractsToPropose =
			new HashSet<InformedCandidature<Contract, ActionSpec>>();


	public void addContractsToPropose(
			final Collection<InformedCandidature<Contract, ActionSpec>> contractsToPropose) {
		this.contractsToPropose.addAll(contractsToPropose);
	}


	@Override
	public Set<? extends InformedCandidature<Contract, ActionSpec>> getNextContractsToPropose()
			throws NotReadyException {
		this.logMonologue("proposing "+this.contractsToPropose, AbstractCommunicationProtocol.log_negotiationStep);
		final Set<InformedCandidature<Contract, ActionSpec>> result =
				new HashSet<InformedCandidature<Contract, ActionSpec>>();
		result.addAll(this.contractsToPropose);
		this.contractsToPropose.clear();
		return result;
	}


	@Override
	public boolean IWantToNegotiate(
			final PersonalState myCurrentState,
			final ContractTrunk<InformedCandidature<Contract, ActionSpec>, ActionSpec, PersonalState> contracts) {
		return !this.contractsToPropose.isEmpty();
	}


	@Override
	public boolean ImAllowedToNegotiate(
			final PersonalState myCurrentState,
			final ContractTrunk<InformedCandidature<Contract, ActionSpec>, ActionSpec, PersonalState> contracts) {
		return true;
	}

}
