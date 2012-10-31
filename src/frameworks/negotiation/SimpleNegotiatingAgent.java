package frameworks.negotiation;

import dima.basicagentcomponents.AgentIdentifier;
import dima.introspectionbasedagents.annotations.Competence;
import dima.introspectionbasedagents.annotations.MessageHandler;
import dima.introspectionbasedagents.annotations.ProactivityInitialisation;
import dima.introspectionbasedagents.annotations.StepComposant;
import dima.introspectionbasedagents.services.AgentCompetence;
import dima.introspectionbasedagents.services.CompetenceException;
import dima.introspectionbasedagents.services.information.ObservationService;
import dima.introspectionbasedagents.services.loggingactivity.LogService;
import dima.introspectionbasedagents.services.observingagent.NotificationMessage;
import dima.introspectionbasedagents.services.observingagent.ShowYourPocket;
import dima.introspectionbasedagents.services.observingagent.NotificationEnvelopeClass.NotificationEnvelope;
import frameworks.experimentation.ObservingSelfService;
import frameworks.negotiation.contracts.AbstractContractTransition;
import frameworks.negotiation.protocoles.AbstractCommunicationProtocol;
import frameworks.negotiation.protocoles.AbstractCommunicationProtocol.ProposerCore;
import frameworks.negotiation.protocoles.AbstractCommunicationProtocol.SelectionCore;
import frameworks.negotiation.protocoles.dcopProtocol.DCOPLeaderProtocol;
import frameworks.negotiation.protocoles.dcopProtocol.DcopAgentProtocol;
import frameworks.negotiation.rationality.AgentState;
import frameworks.negotiation.rationality.RationalCore;
import frameworks.negotiation.rationality.SimpleRationalAgent;
import frameworks.negotiation.rationality.SocialChoiceFunction;

public abstract class SimpleNegotiatingAgent<
PersonalState extends AgentState,
Contract extends AbstractContractTransition>
extends SimpleRationalAgent<PersonalState, Contract>
implements NegotiatingAgent<PersonalState, Contract>{
	private static final long serialVersionUID = 3480283369532419102L;

	//
	// Competences
	//

	@Competence()
	private final AbstractCommunicationProtocol<PersonalState,Contract> protocol;

	@Competence()
	private final SelectionCore<? extends SimpleNegotiatingAgent, PersonalState, Contract> selectionCore;

	@Competence()
	private final ProposerCore<? extends SimpleNegotiatingAgent, PersonalState, Contract> myProposerCore;

	//
	// Constructors
	//

	public SimpleNegotiatingAgent(
			final AgentIdentifier id,
			final PersonalState myInitialState,
			final RationalCore<? extends SimpleNegotiatingAgent, PersonalState, Contract> myRationality,
			final SelectionCore<? extends SimpleNegotiatingAgent, PersonalState, Contract> selectionCore,
			final ProposerCore<? extends SimpleNegotiatingAgent, PersonalState, Contract> proposerCore,
			final ObservationService myInformation,
			final AbstractCommunicationProtocol<PersonalState,Contract> protocol)
					throws CompetenceException {
		super(id, myInitialState, myRationality, myInformation);

		this.selectionCore = selectionCore;
		((AgentCompetence<SimpleNegotiatingAgent<PersonalState, Contract>>)
				this.selectionCore).setMyAgent(this);
		this.protocol = protocol;

		this.myProposerCore = proposerCore;
		((AgentCompetence<SimpleNegotiatingAgent<PersonalState, Contract>>)
				this.getMyProposerCore()).setMyAgent(this);
		this.getMyProtocol().setMyAgent(this);
	}

	@ProactivityInitialisation
	public void initialisation(){
		this.addLogKey(SocialChoiceFunction.log_socialWelfareOrdering, LogService.onNone);
		this.addLogKey(AbstractCommunicationProtocol.log_negotiationStep, LogService.onFile);
		this.addLogKey(AbstractCommunicationProtocol.log_selectionStep, LogService.onNone);
		this.addLogKey(AbstractCommunicationProtocol.log_contractDataBaseManipulation, LogService.onNone);
		this.addLogKey(ObservingSelfService.observationLog, LogService.onNone);
		this.addLogKey(DCOPLeaderProtocol.dcopProtocol, LogService.onBoth);
	}

	//
	// Accessors
	//

	@Override
	public AbstractCommunicationProtocol<PersonalState,Contract> getMyProtocol() {
		return this.protocol;
	}

	@Override
	public ProposerCore<? extends SimpleNegotiatingAgent, PersonalState, Contract> getMyProposerCore() {
		return this.myProposerCore;
	}

	@Override
	public SelectionCore<? extends SimpleNegotiatingAgent,PersonalState, Contract> getMySelectionCore() {
		return this.selectionCore;
	}

	@Override
	public void setNewState(final PersonalState s) {
		super.setNewState(s);
		if (this.protocol!=null && !(this.protocol instanceof DcopAgentProtocol)) {
			setInformation(s);
		}
	}

//	@StepComposant(ticker=1000)
//	public void sayAlive() {
//		logMonologue("I'M STILL ALIVE!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!",LogService.onBoth);
//	}
	//
	// Behavior
	//

	//	@StepComposant(ticker=ExperimentationParameters._maxSimulationTime)
	//	@PreStepComposant(ticker=ExperimentationParameters._maxSimulationTime)
	//	@PostStepComposant(ticker=ExperimentationParameters._maxSimulationTime)
	//	@Transient
	//	public boolean end(){
	////		this.setAlive(false);
	//		this.setActive(false);
	//		return true;
	//	}

	//	@ProactivityFinalisation
	//	public boolean waitForEveryone(){
	//		logMonologue("jjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjj", LogService.onBoth);
	//		wwait(60000);
	//		return true;
	//	}


	//	@PreStepComposant(ticker=ExperimentationParameters._maxSimulationTime)
	//	@Transient
	//	public boolean end(){
	//		this.setActive(false);
	//		this.setAlive(false);
	//		Thread.yield();
	//		wwait(60000);
	//		return true;
	//	}

	@MessageHandler()
	public void hereThereAre(final ShowYourPocket m) {
		String pockets = "My pockets!!! (asked by " + m.getAsker() + " on "
				+ m.getDebugCallingMethod() + ")";
		pockets += "\n" + this.getMyProtocol();
		this.logMonologue(pockets,LogService.onFile);
	}

	@MessageHandler
	@NotificationEnvelope(SimpleRationalAgent.stateChangementObservation)
	public void receiveInformation(
			final NotificationMessage<AgentState> o) {
//		setInformation(o.getNotification());
	}

	@Override
	public void setInformation(
			final AgentState o) {
		assert o!=null;
		if (o!=null){
			this.getMyInformation().add(o);
			this.getMyProtocol().getContracts().updateContracts(o);
		}
	}
}








//
//public SimpleNegotiatingAgent(
//		final AgentIdentifier id,
//		final PersonalState myInitialState,
//		final RationalCore<ActionSpec, PersonalState, Contract> myRationality,
//		final SelectionCore<ActionSpec, PersonalState, Contract> selectionCore,
//		final ProposerCore<? extends SimpleNegotiatingAgent, ActionSpec, PersonalState, Contract> proposerCore,
//		final ObservationService myInformation,
//		final ContractTrunk<Contract, ActionSpec, PersonalState> contracts)
//				throws CompetenceException {
//	super(id, myInitialState, myRationality, myInformation);
//
//	this.selectionCore = selectionCore;
//	this.selectionCore.setMyAgent(this);
//	this.protocol = new ReverseCFPProtocol<ActionSpec, PersonalState, Contract>(this, contracts);
//
//
//	this.myProposerCore = proposerCore;
//	((AgentCompetence<SimpleNegotiatingAgent<ActionSpec, PersonalState, Contract>>) this.getMyProposerCore())
//	.setMyAgent(this);
//
//}





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