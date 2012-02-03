package negotiation.negotiationframework.interaction.consensualnegotiation;

import java.util.HashSet;
import java.util.Set;

import negotiation.negotiationframework.interaction.contracts.AbstractActionSpecification;
import negotiation.negotiationframework.interaction.contracts.AbstractContractTransition;
import negotiation.negotiationframework.strategy.StrategicNegotiatingAgent;
import dima.introspectionbasedagents.services.BasicAgentCompetence;
import dima.introspectionbasedagents.shells.NotReadyException;

public class InactiveProposerCore<
ActionSpec extends AbstractActionSpecification,
PersonalState extends ActionSpec,
Contract extends AbstractContractTransition<ActionSpec>>
extends	BasicAgentCompetence<StrategicNegotiatingAgent<ActionSpec, PersonalState, Contract>>
implements AbstractProposerCore<
StrategicNegotiatingAgent<ActionSpec, PersonalState, Contract>,
ActionSpec, PersonalState, Contract>  {

	/**
	 *
	 */
	private static final long serialVersionUID = -5019973485455813800L;

	public InactiveProposerCore() {
		super();
	}

	@Override
	public Set<Contract> getNextContractsToPropose()
			throws NotReadyException {
		return new HashSet<Contract>();
	}

}