package negotiation.negotiationframework;

import negotiation.faulttolerance.experimentation.ReplicationExperimentationProtocol;
import negotiation.negotiationframework.AbstractCommunicationProtocol.ProposerCore;
import negotiation.negotiationframework.AbstractCommunicationProtocol.SelectionCore;
import negotiation.negotiationframework.contracts.AbstractActionSpecification;
import negotiation.negotiationframework.contracts.AbstractContractTransition;
import negotiation.negotiationframework.contracts.ContractTrunk;
import negotiation.negotiationframework.protocoles.ReverseCFPProtocol;
import negotiation.negotiationframework.rationality.AllocationSocialWelfares;
import negotiation.negotiationframework.rationality.RationalCore;
import negotiation.negotiationframework.rationality.SimpleRationalAgent;
import negotiation.negotiationframework.selectioncores.AbstractSelectionCore;
import dima.basicagentcomponents.AgentIdentifier;
import dima.introspectionbasedagents.annotations.Competence;
import dima.introspectionbasedagents.annotations.MessageHandler;
import dima.introspectionbasedagents.annotations.ProactivityInitialisation;
import dima.introspectionbasedagents.annotations.StepComposant;
import dima.introspectionbasedagents.annotations.Transient;
import dima.introspectionbasedagents.services.AgentCompetence;
import dima.introspectionbasedagents.services.CompetenceException;
import dima.introspectionbasedagents.services.information.ObservationService;
import dima.introspectionbasedagents.services.loggingactivity.LogService;
import dima.introspectionbasedagents.services.observingagent.ShowYourPocket;

public class SimpleNegotiatingAgent<
ActionSpec extends AbstractActionSpecification,
PersonalState extends ActionSpec,
Contract extends AbstractContractTransition<ActionSpec>>
extends SimpleRationalAgent<ActionSpec, PersonalState, Contract> {
	private static final long serialVersionUID = 3480283369532419102L;

	//
	// Competences
	//

	@Competence()
	private final AbstractCommunicationProtocol<ActionSpec, PersonalState, Contract> protocol;

	@Competence()
	private final SelectionCore<ActionSpec, PersonalState, Contract> selectionCore;

	@Competence
	private final ProposerCore<? extends SimpleNegotiatingAgent, ActionSpec, PersonalState, Contract> myProposerCore;

	//
	// Constructors
	//

	public SimpleNegotiatingAgent(
			final AgentIdentifier id,
			final PersonalState myInitialState,
			final RationalCore<ActionSpec, PersonalState, Contract> myRationality,
			final SelectionCore<ActionSpec, PersonalState, Contract> selectionCore,
			final ProposerCore<? extends SimpleNegotiatingAgent, ActionSpec, PersonalState, Contract> proposerCore,
			final ObservationService myInformation,
			final ContractTrunk<Contract, ActionSpec, PersonalState> contracts)
					throws CompetenceException {
		super(id, myInitialState, myRationality, myInformation);

		this.selectionCore = selectionCore;
		this.selectionCore.setMyAgent(this);
		this.protocol = new ReverseCFPProtocol<ActionSpec, PersonalState, Contract>(this, contracts);


		this.myProposerCore = proposerCore;
		((AgentCompetence<SimpleNegotiatingAgent<ActionSpec, PersonalState, Contract>>) this.getMyProposerCore())
		.setMyAgent(this);

	}
	public SimpleNegotiatingAgent(
			final AgentIdentifier id,
			final PersonalState myInitialState,
			final RationalCore<ActionSpec, PersonalState, Contract> myRationality,
			final SelectionCore<ActionSpec, PersonalState, Contract> selectionCore,
			final ProposerCore<? extends SimpleNegotiatingAgent, ActionSpec, PersonalState, Contract> proposerCore,
			final ObservationService myInformation,
			AbstractCommunicationProtocol<ActionSpec, PersonalState, Contract> protocol)
					throws CompetenceException {
		super(id, myInitialState, myRationality, myInformation);

		this.selectionCore = selectionCore;
		this.selectionCore.setMyAgent(this);
		this.protocol = protocol;


		this.myProposerCore = proposerCore;
		((AgentCompetence<SimpleNegotiatingAgent<ActionSpec, PersonalState, Contract>>) this.getMyProposerCore())
		.setMyAgent(this);

	}
	@ProactivityInitialisation
	public void initialisation(){
		this.addLogKey(AllocationSocialWelfares.log_socialWelfareOrdering, false, false);
		this.addLogKey(AbstractCommunicationProtocol.log_negotiationStep, false, true);
		this.addLogKey(AbstractCommunicationProtocol.log_selectionStep, false, true);
		this.addLogKey(AbstractCommunicationProtocol.log_contractDataBaseManipulation, false, false);
	}

	//
	// Accessors
	//

	public AbstractCommunicationProtocol<ActionSpec, PersonalState, Contract> getMyProtocol() {
		return this.protocol;
	}

	public ProposerCore<? extends SimpleNegotiatingAgent, ActionSpec, PersonalState, Contract> getMyProposerCore() {
		return this.myProposerCore;
	}

	public SelectionCore<ActionSpec, PersonalState, Contract> getMySelectionCore() {
		return selectionCore;
	}

	//
	// Behavior
	//

	@StepComposant(ticker=ReplicationExperimentationProtocol._simulationTime)
	@Transient
	public boolean end(){
		this.setAlive(false);
		return true;
	}

	@MessageHandler()
	public void hereThereAre(final ShowYourPocket m) {
		String pockets = "My pockets!!! (asked by " + m.getAsker() + " on "
				+ m.getCallingMethod() + ")";
		pockets += "\n" + this.getMyProtocol();
		this.logMonologue(pockets,LogService.onFile);
	}
}














//	@ProactivityFinalisation
//	public void showInfo() {
//		//		this.logMonologue("terminating with this state : "
//		//				+ this.getMyCurrentState(), LogService.onScreen);
//	}

//
// Primitives
//

// @Override
// public boolean start(final StartSimulationMessage m){
// this.getMyCurrentState().resetUptime();
// return super.start(m);
// }

//}

//
// //
// // Roles
// //
//
// @Override
// public void actAsParticipant() {
// selectionCore.actAsParticipant();
// }
//
// @Override
// public void actAsInitiator() {
// selectionCore.actAsInitiator();
// }
//
// @Competence
// public final ConsensualInitiatorRole<PersonalState, Contract, ActionSpec>
// myInitiatorRole =
// new ConsensualInitiatorRole<PersonalState, Contract, ActionSpec>(
// this);
//
// @Competence
// protected final ConsensualParticipantRole<PersonalState, Contract,
// ActionSpec> myParticipantRole =
// new ConsensualParticipantRole<PersonalState, Contract, ActionSpec>(this);

// if (!cs.getUnlabelledContractsIdentifier().isEmpty()||
// !cs.getOnWaitingListContractsIdentifier().isEmpty())
// logMonologue("Participant : I'm selecting");// :\n"+cs);