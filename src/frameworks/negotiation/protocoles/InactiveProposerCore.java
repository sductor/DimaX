package frameworks.negotiation.protocoles;

import java.util.HashSet;
import java.util.Set;


import dima.introspectionbasedagents.kernel.NotReadyException;
import dima.introspectionbasedagents.services.BasicAgentCompetence;
import frameworks.negotiation.NegotiatingAgent;
import frameworks.negotiation.SimpleNegotiatingAgent;
import frameworks.negotiation.contracts.AbstractActionSpecif;
import frameworks.negotiation.contracts.AbstractContractTransition;
import frameworks.negotiation.contracts.ContractTrunk;
import frameworks.negotiation.protocoles.AbstractCommunicationProtocol.ProposerCore;
import frameworks.negotiation.rationality.AgentState;

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

}