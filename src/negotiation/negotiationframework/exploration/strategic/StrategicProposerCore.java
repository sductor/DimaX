package negotiation.negotiationframework.exploration.strategic;

import java.util.ArrayList;
import java.util.Collection;

import negotiation.negotiationframework.contracts.AbstractActionSpecification;
import negotiation.negotiationframework.contracts.AbstractContractTransition;
import negotiation.negotiationframework.protocoles.AbstractCommunicationProtocol.ProposerCore;
import dima.introspectionbasedagents.services.BasicAgentCompetence;
import dima.introspectionbasedagents.services.information.NoInformationAvailableException;

public class StrategicProposerCore<
ActionSpec extends AbstractActionSpecification,
PersonalState extends ActionSpec,
Contract extends AbstractContractTransition<ActionSpec>>
extends	BasicAgentCompetence<StrategicNegotiatingAgent<ActionSpec, PersonalState, Contract>>
implements ProposerCore<
StrategicNegotiatingAgent<ActionSpec, PersonalState, Contract>,
ActionSpec, PersonalState, Contract> {

	//
	// Constructor
	//

	/**
	 *
	 */
	private static final long serialVersionUID = -497374728643432838L;

	public StrategicProposerCore(
			// Collection<String> knownActions,
			// AbstractStrategicEvaluationModule<Contract, ActionSpec> myComparator,
			// AbstractStrategicExplorationModule<Contract, ActionSpec> myExplorator
			) {
		super();
	}

	//
	// Methods
	//

	@Override
	public Collection<Contract> getNextContractsToPropose()
			throws NoInformationAvailableException {
		final Collection<Contract> nextContractToPropose = new ArrayList<Contract>();
		nextContractToPropose.add(this
				.getMyAgent()
				.getMyExplorator()
				.getNextContractToPropose(this.getMyAgent().getMyComparator(),
						this.getMyAgent().getMyInformation().getKnownAgents(),
						this.getMyAgent().getKnownActions()));
		// if (!IsAStrictImprovment(nextContractToPropose))
		// System.err.println("NOT RATIONAL PROPOSAL!!");
		return nextContractToPropose;
	}
}
