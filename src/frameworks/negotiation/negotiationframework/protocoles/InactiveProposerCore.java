package frameworks.negotiation.negotiationframework.protocoles;

import java.util.HashSet;
import java.util.Set;

import dima.introspectionbasedagents.services.BasicAgentCompetence;
import dima.introspectionbasedagents.shells.NotReadyException;
import frameworks.negotiation.negotiationframework.NegotiatingAgent;
import frameworks.negotiation.negotiationframework.SimpleNegotiatingAgent;
import frameworks.negotiation.negotiationframework.contracts.AbstractActionSpecif;
import frameworks.negotiation.negotiationframework.contracts.AbstractContractTransition;
import frameworks.negotiation.negotiationframework.contracts.ContractTrunk;
import frameworks.negotiation.negotiationframework.protocoles.AbstractCommunicationProtocol.ProposerCore;
import frameworks.negotiation.negotiationframework.rationality.AgentState;

public class InactiveProposerCore<
ActionSpec extends AbstractActionSpecif,
PersonalState extends AgentState,
Contract extends AbstractContractTransition>
extends	BasicAgentCompetence<SimpleNegotiatingAgent<PersonalState, Contract>>
implements ProposerCore<
SimpleNegotiatingAgent<PersonalState, Contract>,
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