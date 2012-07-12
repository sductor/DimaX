package negotiation.negotiationframework.protocoles;

import java.util.HashSet;
import java.util.Set;

import negotiation.negotiationframework.NegotiatingAgent;
import negotiation.negotiationframework.contracts.AbstractActionSpecif;
import negotiation.negotiationframework.contracts.AbstractContractTransition;
import negotiation.negotiationframework.contracts.ContractTrunk;
import negotiation.negotiationframework.protocoles.AbstractCommunicationProtocol.ProposerCore;
import negotiation.negotiationframework.rationality.AgentState;
import dima.introspectionbasedagents.services.BasicAgentCompetence;
import dima.introspectionbasedagents.shells.NotReadyException;

public class InactiveProposerCore<
ActionSpec extends AbstractActionSpecif,
PersonalState extends AgentState,
Contract extends AbstractContractTransition>
extends	BasicAgentCompetence<NegotiatingAgent<PersonalState, Contract>>
implements ProposerCore<
NegotiatingAgent<PersonalState, Contract>,
PersonalState,
Contract>  {
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
	public boolean IWantToNegotiate(final ContractTrunk<Contract> contracts) {
		return false;
	}

	@Override
	public boolean ImAllowedToNegotiate(final ContractTrunk<Contract> contracts) {
		return false;
	}

}