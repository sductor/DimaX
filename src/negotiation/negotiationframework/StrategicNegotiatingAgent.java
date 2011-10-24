package negotiation.negotiationframework;

import java.util.Collection;
import java.util.Date;

import negotiation.negotiationframework.agent.RationalCore;
import negotiation.negotiationframework.interaction.AbstractActionSpecification;
import negotiation.negotiationframework.interaction.AbstractContractTransition;
import negotiation.negotiationframework.interaction.selectioncores.AbstractSelectionCore;
import negotiation.negotiationframework.strategy.StrategicProposerCore;
import negotiation.negotiationframework.strategy.evaluation.AbstractStrategicEvaluationModule;
import negotiation.negotiationframework.strategy.exploration.AbstractStrategicExplorationModule;
import dima.basicagentcomponents.AgentIdentifier;
import dima.introspectionbasedagents.services.CompetenceException;
import dima.introspectionbasedagents.services.library.information.ObservationService;
import dima.introspectionbasedagents.services.library.information.SimpleOpinionService;

public abstract class StrategicNegotiatingAgent<
ActionSpec extends AbstractActionSpecification,
PersonalState extends ActionSpec, 
Contract extends AbstractContractTransition<ActionSpec>>
		extends SimpleNegotiatingAgent<ActionSpec, PersonalState, Contract> {

	//
	// Fields
	//

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private final Collection<String> knownActions;
	private final AbstractStrategicEvaluationModule<Contract, ActionSpec> myComparator;
	private final AbstractStrategicExplorationModule<Contract, ActionSpec> myExplorator;

	//
	// Constructor
	//

	public StrategicNegotiatingAgent(
			AgentIdentifier id,
			Date horloge,
			PersonalState myInitialState,
			RationalCore<ActionSpec, PersonalState, Contract> myRationality,
			AbstractSelectionCore<ActionSpec, PersonalState, Contract> selectionCore,
			StrategicProposerCore<ActionSpec, PersonalState, Contract> myProposerCore,
			ObservationService myInformation,
			Collection<String> knownActions,
			AbstractStrategicEvaluationModule<Contract, ActionSpec> myComparator,
			AbstractStrategicExplorationModule<Contract, ActionSpec> myExplorator)
			throws CompetenceException {
		super(id, myInitialState, myRationality, selectionCore, myProposerCore, myInformation);
		this.knownActions = knownActions;
		this.myComparator = myComparator;
		this.myExplorator = myExplorator;
	}

	public StrategicNegotiatingAgent(
			AgentIdentifier id,
			PersonalState myInitialState,
			RationalCore<ActionSpec, PersonalState, Contract> myRationality,
			AbstractSelectionCore<ActionSpec, PersonalState, Contract> selectionCore,
			StrategicProposerCore<ActionSpec, PersonalState, Contract> myProposerCore,
			ObservationService myInformation,
			Collection<String> knownActions,
			AbstractStrategicEvaluationModule<Contract, ActionSpec> myComparator,
			AbstractStrategicExplorationModule<Contract, ActionSpec> myExplorator)
			throws CompetenceException {
		super(id, myInitialState, myRationality, selectionCore, myProposerCore, myInformation);
		this.knownActions = knownActions;
		this.myComparator = myComparator;
		this.myExplorator = myExplorator;
	}

	//
	// Accessors
	//

	@Override
	@SuppressWarnings("unchecked")
	public SimpleOpinionService getMyInformation() {
		return (SimpleOpinionService) super.getMyInformation();
	}

	public Collection<String> getKnownActions() {
		return this.knownActions;
	}

	public AbstractStrategicEvaluationModule<Contract, ActionSpec> getMyComparator() {
		return this.myComparator;
	}

	public AbstractStrategicExplorationModule<Contract, ActionSpec> getMyExplorator() {
		return this.myExplorator;
	}

	//
	// Methods
	//

//	public abstract Double evaluatePreference(PersonalState s1);

}
