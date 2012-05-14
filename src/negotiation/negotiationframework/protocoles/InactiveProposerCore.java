package negotiation.negotiationframework.protocoles;

import java.util.HashSet;
import java.util.Set;

import negotiation.negotiationframework.contracts.AbstractActionSpecification;
import negotiation.negotiationframework.contracts.AbstractContractTransition;
import negotiation.negotiationframework.contracts.ContractTrunk;
import negotiation.negotiationframework.protocoles.AbstractCommunicationProtocol.ProposerCore;
import negotiation.negotiationframework.protocoles.strategic.StrategicNegotiatingAgent;
import negotiation.negotiationframework.rationality.AgentState;
import dima.introspectionbasedagents.services.BasicAgentCompetence;
import dima.introspectionbasedagents.shells.NotReadyException;

public class InactiveProposerCore<
ActionSpec extends AbstractActionSpecification,
PersonalState extends AgentState,
Contract extends AbstractContractTransition<ActionSpec>>
extends	BasicAgentCompetence<StrategicNegotiatingAgent<ActionSpec, PersonalState, Contract>>
implements ProposerCore<
StrategicNegotiatingAgent<ActionSpec, PersonalState, Contract>,
ActionSpec, PersonalState, Contract>  {
	private static final long serialVersionUID = -5019973485455813800L;

	public InactiveProposerCore() {
		super();
	}

	@Override
	public Set<Contract> getNextContractsToPropose()
			throws NotReadyException {
		return new HashSet<Contract>();
	}

	@Override
	public boolean IWantToNegotiate(final PersonalState myCurrentState,
			final ContractTrunk<Contract, ActionSpec, PersonalState> contracts) {
		return false;
	}

	@Override
	public boolean ImAllowedToNegotiate(final PersonalState myCurrentState,
			final ContractTrunk<Contract, ActionSpec, PersonalState> contracts) {
		return false;
	}

}