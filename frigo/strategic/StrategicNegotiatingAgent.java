package negotiation.negotiationframework.exploration.strategic;

import java.util.Collection;
import java.util.Date;

import negotiation.negotiationframework.SimpleNegotiatingAgent;
import negotiation.negotiationframework.contracts.AbstractActionSpecif;
import negotiation.negotiationframework.contracts.AbstractContractTransition;
import negotiation.negotiationframework.protocoles.strategic.exploration.AbstractStrategicExplorationModule;
import negotiation.negotiationframework.rationality.AgentState;
import negotiation.negotiationframework.rationality.RationalCore;
import negotiation.negotiationframework.selection.SimpleSelectionCore;
import dima.basicagentcomponents.AgentIdentifier;
import dima.introspectionbasedagents.services.CompetenceException;
import dima.introspectionbasedagents.services.information.ObservationService;
import dima.introspectionbasedagents.services.information.SimpleOpinionService;

public abstract class StrategicNegotiatingAgent<
ActionSpec extends AbstractActionSpecif,
PersonalState extends AgentState,
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
			final AgentIdentifier id,
			final Date horloge,
			final PersonalState myInitialState,
			final RationalCore<ActionSpec, PersonalState, Contract> myRationality,
			final SimpleSelectionCore<ActionSpec, PersonalState, Contract> selectionCore,
			final StrategicProposerCore<ActionSpec, PersonalState, Contract> myProposerCore,
			final ObservationService myInformation,
			final Collection<String> knownActions,
			final AbstractStrategicEvaluationModule<Contract, ActionSpec> myComparator,
			final AbstractStrategicExplorationModule<Contract, ActionSpec> myExplorator)
					throws CompetenceException {
		super(id, myInitialState, myRationality, selectionCore, myProposerCore, myInformation);
		this.knownActions = knownActions;
		this.myComparator = myComparator;
		this.myExplorator = myExplorator;
	}

	public StrategicNegotiatingAgent(
			final AgentIdentifier id,
			final PersonalState myInitialState,
			final RationalCore<ActionSpec, PersonalState, Contract> myRationality,
			final SimpleSelectionCore<ActionSpec, PersonalState, Contract> selectionCore,
			final StrategicProposerCore<ActionSpec, PersonalState, Contract> myProposerCore,
			final ObservationService myInformation,
			final Collection<String> knownActions,
			final AbstractStrategicEvaluationModule<Contract, ActionSpec> myComparator,
			final AbstractStrategicExplorationModule<Contract, ActionSpec> myExplorator)
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
