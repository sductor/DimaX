package negotiation.negotiationframework.interaction.consensualnegotiation;

import java.util.ArrayList;
import java.util.Collection;

import negotiation.negotiationframework.StrategicNegotiatingAgent;
import negotiation.negotiationframework.interaction.AbstractActionSpecification;
import negotiation.negotiationframework.interaction.AbstractContractTransition;
import dima.introspectionbasedagents.NotReadyException;
import dima.introspectionbasedagents.competences.BasicAgentCompetence;

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
	public Collection<Contract> getNextContractsToPropose()
			throws NotReadyException {
		return new ArrayList<Contract>();
	}

}