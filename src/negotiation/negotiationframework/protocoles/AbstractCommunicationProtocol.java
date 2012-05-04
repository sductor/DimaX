package negotiation.negotiationframework.protocoles;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Set;

import negotiation.faulttolerance.experimentation.Host;
import negotiation.negotiationframework.NegotiationParameters;
import negotiation.negotiationframework.SimpleNegotiatingAgent;
import negotiation.negotiationframework.contracts.AbstractActionSpecification;
import negotiation.negotiationframework.contracts.AbstractContractTransition;
import negotiation.negotiationframework.contracts.ContractIdentifier;
import negotiation.negotiationframework.contracts.ContractTransition;
import negotiation.negotiationframework.contracts.ContractTrunk;
import negotiation.negotiationframework.contracts.UnknownContractException;
import dima.basicagentcomponents.AgentIdentifier;
import dima.basiccommunicationcomponents.Message;
import dima.introspectionbasedagents.annotations.MessageHandler;
import dima.introspectionbasedagents.annotations.StepComposant;
import dima.introspectionbasedagents.ontologies.Protocol;
import dima.introspectionbasedagents.ontologies.FIPAACLOntologie.FipaACLEnvelopeClass.FipaACLEnvelope;
import dima.introspectionbasedagents.ontologies.FIPAACLOntologie.FipaACLMessage;
import dima.introspectionbasedagents.ontologies.FIPAACLOntologie.Performative;
import dima.introspectionbasedagents.services.AgentCompetence;
import dima.introspectionbasedagents.services.UnrespectedCompetenceSyntaxException;
import dima.introspectionbasedagents.services.observingagent.ShowYourPocket;
import dima.introspectionbasedagents.shells.NotReadyException;

/**
 * Negotiation, as a protocol, provide : * the involved roles * the method to
 * send the different messages * the reception of answers
 *
 *
 * @author Sylvain Ductor
 *
 * @param <Contract>
 * @param <ActionSpec>
 */
public abstract class AbstractCommunicationProtocol<
ActionSpec extends AbstractActionSpecification,
State extends ActionSpec,
Contract extends AbstractContractTransition<ActionSpec>>
extends Protocol<SimpleNegotiatingAgent<ActionSpec, State, Contract>> {
	private static final long serialVersionUID = 7728287555094295894L;

	//
	// Roles
	//

	public interface ProposerCore
	<Agent extends SimpleNegotiatingAgent<ActionSpec, PersonalState, Contract>,
	ActionSpec extends AbstractActionSpecification,
	PersonalState extends ActionSpec,
	Contract extends AbstractContractTransition<ActionSpec>>
	extends AgentCompetence<Agent>{

		public Set<? extends Contract> getNextContractsToPropose()
				throws NotReadyException;

		public boolean IWantToNegotiate(PersonalState myCurrentState,
				ContractTrunk<Contract, ActionSpec, PersonalState> contracts);

		public boolean ImAllowedToNegotiate(PersonalState myCurrentState,
				ContractTrunk<Contract, ActionSpec, PersonalState> contracts);


	}

	public interface SelectionCore<
	Agent extends SimpleNegotiatingAgent<ActionSpec, PersonalState, Contract>,
	ActionSpec extends AbstractActionSpecification,
	PersonalState extends ActionSpec,
	Contract extends AbstractContractTransition<ActionSpec>>
	extends	AgentCompetence<Agent> {

		// Select contract to accept/wait/reject for a participant
		// Select contract to request/cancel for a initiator
		public void select(
				ContractTrunk<Contract, ActionSpec, PersonalState> cs,
				Collection<Contract> toAccept,
				Collection<Contract> toReject,
				Collection<Contract> toPutOnWait);

	}

	//
	// Log keys
	//

	public static final String log_negotiationStep="negotiation step for log";
	public static final String log_contractDataBaseManipulation="manipulation of contracts database";
	public static final String log_selectionStep = "selection step of contract answering";

	//
	// Fields
	//

	private final ContractTrunk<Contract, ActionSpec, State> contracts;


	//
	// Constructor
	//


	public AbstractCommunicationProtocol(
			final SimpleNegotiatingAgent<ActionSpec, State, Contract> a,
			final ContractTrunk<Contract, ActionSpec, State> contracts) throws UnrespectedCompetenceSyntaxException {
		super(a);
		this.contracts = contracts;
	}

	public AbstractCommunicationProtocol(
			final ContractTrunk<Contract, ActionSpec, State> contracts) throws UnrespectedCompetenceSyntaxException {
		super();
		this.contracts = contracts;
	}

	@Override
	public void setMyAgent(final SimpleNegotiatingAgent<ActionSpec, State, Contract> ag) {
		super.setMyAgent(ag);
		this.getContracts().setMyAgent(ag);
	}

	//
	// Accessors
	//

	public ContractTrunk<Contract, ActionSpec, State> getContracts() {
		return this.contracts;
	}

	////////////////////////////////////////////////
	// Behavior
	//

	/*
	 * Initiator
	 */

	// @role(NegotiationInitiatorRole.class)
	@StepComposant(ticker = NegotiationParameters._initiatorPropositionFrequency)
	public void initiateNegotiation() {
		if (this.isActive() &&
				this.getMyAgent().getMyProposerCore().IWantToNegotiate(this.getMyAgent().getMyCurrentState(),this.contracts)
				&& this.getMyAgent().getMyProposerCore().ImAllowedToNegotiate(this.getMyAgent().getMyCurrentState(), this.contracts)) {
			try {
				final Collection<? extends Contract> cs =
						this.getMyAgent().getMyProposerCore()
						.getNextContractsToPropose();
				proposeContracts(cs);
			} catch (final NotReadyException e) {}
		}
	}

	private void proposeContracts(final Collection<? extends Contract> cs) {
		for (final Contract c : cs){
			this.logMonologue("**************> I propose "+c,AbstractCommunicationProtocol.log_negotiationStep);

			send(c, Receivers.NotInitiatingParticipant, new SimpleContractProposal(Performative.Propose, c));
			//, (Collection<Information>) this.getMyAgent().getMyResources()));

			this.contracts.addContract(c);
			assert receivedContract.add(c.getIdentifier());
		}
	}

	/*
	 * Participant
	 */

	// @role(NegotiationParticipant.class)
	@StepComposant(ticker = NegotiationParameters._timeToCollect)
	void answer() {
		if (this.isActive() && !this.getContracts().isEmpty()) {

			//
			// Selecting contracts
			//

			// logMonologue("What do I have?"+contracts.getOnWaitContracts());
			final ArrayList<Contract> toAccept = new ArrayList<Contract>();
			final ArrayList<Contract> toReject = new ArrayList<Contract>();
			final ArrayList<Contract> toPutOnWait = new ArrayList<Contract>();

			this.logMonologue(
					"initiating selection with following contracts :\n"+this.getContracts(),
					AbstractCommunicationProtocol.log_selectionStep);

			this.getMyAgent().getMySelectionCore().select(this.getContracts(), toAccept, toReject, toPutOnWait);

			//
			// Validity
			//

			assert AbstractCommunicationProtocol.partitioning(
					this.getContracts().getAllContracts(),
					toAccept, toReject,toPutOnWait):"->"+toAccept+"\n->"+toReject+"\n->"+toPutOnWait;

					//
					// Answering
					//

					this.answerAccepted(toAccept);
					this.answerRejected(toReject);
		}
	}

	protected abstract void answerAccepted(Collection<Contract> toAccept);

	protected abstract void answerRejected(Collection<Contract> toReject);

	protected abstract void putOnWait(Collection<Contract> toPutOnWait);

	////////////////////////////////////////////////
	// Sending Primitives
	//

	public enum Receivers { EveryParticipant, NotInitiatingParticipant, Initiator, None}

	private void send(final Contract c, final Receivers receivers, final Message m){
		Collection<AgentIdentifier> participant;
		switch (receivers){
		case EveryParticipant :
			participant = new ArrayList<AgentIdentifier>();
			participant.addAll(c.getAllParticipants());
			participant.remove(this.getIdentifier());
			this.sendMessage(participant, m);
			break;
		case NotInitiatingParticipant :
			participant = new ArrayList<AgentIdentifier>();
			participant.addAll(c.getNotInitiatingParticipants());
			participant.remove(this.getIdentifier());
			this.sendMessage(participant, m);
			break;
		case Initiator :
			assert !this.getIdentifier().equals(c.getInitiator());
			this.sendMessage(c.getInitiator(), m);
			break;
		}
	}


	protected void acceptContract(final Collection<Contract> contracts, final Receivers receivers) {
		assert ContractTransition.stillValid(contracts);

		if (!contracts.isEmpty()) {
			this.getMyAgent().logMonologue(
					"**************> I accept proposals "+contracts,
					AbstractCommunicationProtocol.log_negotiationStep);
		}

		for (final Contract contract : contracts){

			this.send(contract, receivers,
					new SimpleContractAnswer(
							Performative.AcceptProposal,
							contract.getIdentifier()));

			this.contracts.addAcceptation(
					this.getMyAgent().getIdentifier(),
					contract);
		}
	}

	protected void rejectContract(final Collection<Contract> contracts, final Receivers receivers) {
		assert ContractTransition.stillValid(contracts);

		if (!contracts.isEmpty()) {
			this.getMyAgent().logMonologue(
					"**************> I reject proposals "+contracts,
					AbstractCommunicationProtocol.log_negotiationStep);
		}

		for (final Contract contract : contracts){

			this.send(contract, receivers,
					new SimpleContractAnswer(
							Performative.RejectProposal,
							contract.getIdentifier()));

			this.contracts.addRejection(
					this.getMyAgent().getIdentifier(),
					contract);
		}
	}

	protected void confirmContract(final Collection<Contract> contracts, final Receivers receivers) {
		assert ContractTransition.stillValid(contracts);
		assert ContractTransition.allComplete(contracts);

		for (final Contract contract : contracts){
			this.getContracts().addAcceptation(this.getMyAgent().getIdentifier(),contract);
			assert this.getContracts().getRequestableContracts().contains(contract):contract;
		}

		if (!contracts.isEmpty()) {
			this.getMyAgent().logMonologue(
					"**************> I confirm!"+contracts,
					AbstractCommunicationProtocol.log_negotiationStep);
		}

		this.getMyAgent().execute(contracts);

		for (final Contract contract : contracts) {
			assert alreadyExecuted.add(contract.getIdentifier());
			this.send(contract, receivers,
					new SimpleContractAnswer(
							Performative.Confirm,
							contract.getIdentifier()));
		}

		this.contracts.removeAll(contracts);
	}

	protected void cancelContract(final Collection<Contract> contracts, final Receivers receivers) {
		assert ContractTransition.stillValid(contracts);

		if (!contracts.isEmpty()) {
			this.getMyAgent().logMonologue("**************> I cancel!"+contracts,
					AbstractCommunicationProtocol.log_negotiationStep);
		}


		for (final Contract contract : contracts){

			this.send(contract, receivers,
					new SimpleContractAnswer(
							Performative.Cancel,
							contract.getIdentifier()));

			assert alreadyCancelled.add(contract.getIdentifier());
			this.contracts.remove(contract);
		}
	}

	//////////////////////////////////////////////////////////////////
	// Reception Primitive
	//

	@MessageHandler()
	@FipaACLEnvelope(performative = Performative.Propose, protocol = AbstractCommunicationProtocol.class)
	protected void receiveProposal(final SimpleContractProposal delta)  {
		final Contract contract = delta.getMyContract();
		assert receivedContract.add(contract.getIdentifier());
		this.logMonologue("I've received proposal "+contract,AbstractCommunicationProtocol.log_negotiationStep);
		delta.getMyContract().setSpecification(this.getMyAgent().getMySpecif(delta.getMyContract()));
		this.contracts.addContract(contract);
		//		for (Information i : delta.attachedInfos)
		//			getMyAgent().getMyInformation().add(i);
	}
	//Updating contract spec
	//		for (final AgentIdentifier id : contract.getAllParticipants())
	//			try {
	//				final ActionSpec specificationOfId = contract.getSpecificationOf(id);
	//				if (!id.equals(this.getIdentifier())){
	//					try {
	//						assert this.getMyAgent().getMyInformation()
	//						.getInformation(specificationOfId.getClass(),id).isNewerThan(specificationOfId)<1;
	//					} catch (final NoInformationAvailableException e) {}
	//					this.getMyAgent().getMyInformation().add(specificationOfId);
	//				}
	//			} catch (final IncompleteContractException e) {}

	@MessageHandler()
	@FipaACLEnvelope(performative = Performative.AcceptProposal, protocol = AbstractCommunicationProtocol.class)
	protected void receiveAccept(final SimpleContractAnswer delta) {

		Contract contract;
		try {
			contract = this.contracts.getContract(delta.getIdentifier());
		} catch (final UnknownContractException e) {
			this.faceAnUnknownContract(e);
			return;
		}

		final ContractIdentifier c = delta.getIdentifier();
		assert !c.hasReachedExpirationTime();

		assert c.getInitiator().equals(this.getMyAgent().getIdentifier());
		assert this.contracts.contains(c) || c.willReachExpirationTime(NegotiationParameters._timeToCollect):
			"aaaaaaaaarrrgh" + "i should now "+ c;

		this.logMonologue("I 've been accepted! =) "+c//+"\n"+this.getMyAgent().getMyCurrentState()
				,AbstractCommunicationProtocol.log_negotiationStep);

		//		this.getMyAgent().getMyInformation().add(delta.getSpec());
		this.contracts.addAcceptation(delta.getSender(), contract);
		//		contract.setSpecification(delta.getSpec());
	}

	@MessageHandler()
	@FipaACLEnvelope(performative = Performative.RejectProposal, protocol = AbstractCommunicationProtocol.class)
	protected void receiveReject(final SimpleContractAnswer delta) {

		Contract contract;
		try {
			contract = this.contracts.getContract(delta.getIdentifier());
		} catch (final UnknownContractException e) {
//			this.faceAnUnknownContract(e);
			return;
		}
		assert contract.getInitiator().equals(this.getMyAgent().getIdentifier());

		this.logMonologue("I 've been rejected! =( "+contract//+"\n"+this.getMyAgent().getMyCurrentState()
				,AbstractCommunicationProtocol.log_negotiationStep);

		this.contracts.addRejection(delta.getSender(), contract);
		//		contract.setSpecification(delta.getSpec());
	}

	@MessageHandler()
	@FipaACLEnvelope(performative = Performative.Confirm, protocol = AbstractCommunicationProtocol.class)
	protected void receiveConfirm(final SimpleContractAnswer m) {

		Contract contract;
		try {
			contract = this.contracts.getContract(m.getIdentifier());
		} catch (final UnknownContractException e) {
			this.faceAnUnknownContract(e);
			return;
		}

		assert !contract.hasReachedExpirationTime();
		assert !(this.getMyAgent() instanceof Host)	|| !((Host) this.getMyAgent()).getMyCurrentState().isFaulty();
		assert this.contracts.getContractsAcceptedBy(this.getMyAgent().getIdentifier()).contains(contract);
		assert alreadyExecuted.add(contract.getIdentifier());
		//		try {
		//			assert contract.isViable(this.getMyAgent().getMyCurrentState()):"what the !!!!!!\n bad contract "
		//					+this.getContracts().statusOf(contract)
		//					+ "\nnew state "
		//					+ this.getMyAgent()
		//					.getMyResultingState(contract);
		//		} catch (final IncompleteContractException e1) {
		//			this.getMyAgent().signalException("what the !!!!!!\n bad contract "
		//					+this.getContracts().statusOf(contract)
		//					+ "\nnew state "
		//					+ this.getMyAgent()
		//					.getMyResultingState(contract));
		//		}
		//		assert m.getSpec()!=null;


		this.getMyAgent().logMonologue("I've been confirmed!!! I'll apply proposal "+contract,AbstractCommunicationProtocol.log_negotiationStep);//+"\n"+contracts);

		this.getMyAgent().execute(contract);

		this.contracts.remove(contract);
	}

	@MessageHandler()
	@FipaACLEnvelope(performative = Performative.Cancel, protocol = AbstractCommunicationProtocol.class)
	protected void receiveCancel(final SimpleContractAnswer m) {

		Contract contract;
		try {
			contract = this.contracts.getContract(m.getIdentifier());
		} catch (final UnknownContractException e) {
			this.faceAnUnknownContract(e);
			return;
		}

		final AgentIdentifier id = m.getSender();
		final ContractIdentifier c = m.getIdentifier();

		this.logMonologue("I've received cancel "+c,AbstractCommunicationProtocol.log_negotiationStep);
		assert alreadyCancelled.add(c);
		this.contracts.remove(contract);
	}

	//
	// Primitive
	//

	@Override
	public String toString() {
		return "Protocol of " + this.getMyAgent().getIdentifier()
				+ "\n  --------> Current state "
				+ this.getMyAgent().getMyCurrentState() + "\n  --------> "
				+ this.contracts + "\n ******** \n";
	}



	//
	// Subclasses : Envellope
	//

	/*
	 *
	 *
	 */

	public class SimpleContractAnswer extends FipaACLMessage {
		private static final long serialVersionUID = 1133114679526920774L;

		//
		// Fields
		//

		final ContractIdentifier answeredContract;
		//		final ActionSpec s;

		// private final AgentIdentifier sender;
		//
		// Constructor
		//

		public SimpleContractAnswer(final Performative p,
				final ContractIdentifier id) {
			super(p, AbstractCommunicationProtocol.class);
			assert  id!=null;
			//			this.s = AbstractCommunicationProtocol.this.getMyAgent().getMyCurrentState();
			this.answeredContract = id;
			assert AbstractCommunicationProtocol.this.getContracts().contains(id);
		}

		//
		// Accessors
		//

		public ContractIdentifier getIdentifier() {
			return this.answeredContract;
		}

		public String detail() {
			return "Answer of contract " + this.answeredContract;
		}

		//		public ActionSpec getSpec() {
		//			return this.s;
		//		}

		/*
		 *
		 */

	}

	//

	public class SimpleContractProposal extends FipaACLMessage {
		private static final long serialVersionUID = 7442568804092112906L;

		final Contract myContract;
		//		final Collection<Information> attachedInfos;

		public SimpleContractProposal(final Performative performative
				,final Contract myContract
				//				,Collection<Information> attachedInfos
				) {
			super(performative, AbstractCommunicationProtocol.class);
			assert myContract != null;
			myContract.setSpecification(AbstractCommunicationProtocol.this.getMyAgent().getMySpecif(myContract));
			// this.myContract = (Contract) myContract.clone();
			this.myContract = myContract;
			//			this.attachedInfos=attachedInfos;
		}

		public Contract getMyContract() {
			return this.myContract;
		}

		public ContractIdentifier getIdentifier() {
			return this.myContract.getIdentifier();
		}

		public String detail() {
			return "Envellope of contract " + this.myContract.getIdentifier();
		}
	}


	//
	// Assertion
	//

	Collection<ContractIdentifier> receivedContract = new
			ArrayList<ContractIdentifier>();
	Collection<ContractIdentifier> alreadyCancelled = new
			ArrayList<ContractIdentifier>();
	Collection<ContractIdentifier> alreadyExecuted = new
			ArrayList<ContractIdentifier>();
	private void faceAnUnknownContract(final UnknownContractException e) {
		if (alreadyCancelled.contains(e.getId()))
			alreadyCancelled.remove(e.getId());
		else {
			this.signalException("facing unknonw contract!!!!! ");
//			this.signalException("facing unknonw contract!!!!! " + e.getId()
//					+"\nreceivedContract?"+receivedContract.contains(e.getId())
//					+"\nalreadyCancelled?"+alreadyCancelled.contains(e.getId())
//					+"\nalreadyExecuted?"+alreadyExecuted.contains(e.getId())
//					+ " lost contracts are : ",e);// + this.losts, e);
//			this.sendMessage(e.getId().getParticipants(), new ShowYourPocket(
//					this.getIdentifier(), "facing an unknown contract"));
		}
	}



	// @role(NegotiationParticipant.class)

	public static <Contract extends AbstractContractTransition<?>>
	boolean partitioning(
			final Collection<Contract> all,
			final Collection<Contract> accepted,
			final Collection<Contract> rejected,
			final Collection<Contract> onWait) {

		//accepted rejected et onWait sont disjoint
		for (final Contract c : accepted){
			assert !rejected.contains(c);
			assert !onWait.contains(c);
		}
		for (final Contract c : rejected){
			assert !accepted.contains(c);
			assert !onWait.contains(c);
		}
		for (final Contract c : onWait){
			assert !accepted.contains(c);
			assert !rejected.contains(c);
		}

		final Collection<Contract> allBis = new ArrayList<Contract>(all);

		//accepted rejected et onWait contienent to all
		all.removeAll(onWait);
		all.removeAll(rejected);
		all.removeAll(accepted);

		assert all.isEmpty():"ALL Init \n"+allBis+"\n ACCEPTED \n"
		+accepted+"\n REJECTED \n"+rejected+"\n ON WAIT \n"+onWait+"\nALL fin \n"+allBis;

		return true;
	}

	public static <Contract extends AbstractContractTransition<?>>
	boolean allRequestable(
			final Collection<Contract> all,
			final ContractTrunk<Contract, ?, ?> ct){
		for (final Contract c : all) {
			assert ct.getRequestableContracts().contains(c);
		}
		return true;
	}
}




// @MessageHandler()
// @FipaACLEnvelope(
// performative=Performative.RejectProposal,
// protocol=NegotiationProtocol.class)
// void receiveRejectDebugVersion(final SimpleContractAnswer delta){
// final AgentIdentifier id = delta.getSender();
// final ContractIdentifier c = delta.getIdentifier();
// // logMonologue("I 've been rejected! =( "+c);
//
// if
// (!contracts.getContractsRejectedBy(getMyAgent().getIdentifier()).contains(c)){
// this.contracts.addRejection(getMyAgent().getIdentifier(),contracts.getContract(c));
// sendCancel(c);//CONSENSUAL NEGOTIATION
// }
//
// if (c.hasReachedExpirationTime()){
// contracts.remove(c);
// }else if (!contracts.contains(c) &&
// !c.willReachExpirationTime(StaticParameters._timeToCollect)){
// throw new RuntimeException("aaaaaaaaarrrgh i should now "+c+"!!!!!");
// } else {
// if (contracts.everyOneHasAnswered(contracts.getContract(c)))
// contracts.remove(contracts.getContract(c));
// }
// }





//
// Fields
//
//
// RationalParticipantCore<State, Contract, ActionSpec> myParticipant =
// null;
// RationalInitiatorCore<State, Contract, ActionSpec> myInitiator = null;
// private boolean myInitiatorActive = true;
// private boolean myParticipantActive = true;



// @role(NegotiationInitiatorRole.class)
// @MessageHandler()
// @FipaACLEnvelope(
// performative=Performative.Wait,
// protocol=NegotiationProtocol.class)
// void receiveWait(final SimpleContractEnvellope delta){
// this.myInitiator.receiveWait(delta.getMyContract());
// }

// @role(NegotiationParticipant.class)
// public void putOnWaitingList(final Contract c){
// final SimpleContractEnvellope waiting =
// new SimpleContractEnvellope(
// Performative.Wait,
// c);
// this.sendMessage(c.getInitiator(), waiting);
// // getMyAgent().logMonologue("I put on waiting list "+c);
// }
