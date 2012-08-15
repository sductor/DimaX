package frameworks.negotiation.negotiationframework.protocoles;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Set;


import sun.reflect.ReflectionFactory.GetReflectionFactoryAction;

import dima.basicagentcomponents.AgentIdentifier;
import dima.basiccommunicationcomponents.Message;
import dima.introspectionbasedagents.annotations.MessageHandler;
import dima.introspectionbasedagents.annotations.PreStepComposant;
import dima.introspectionbasedagents.annotations.StepComposant;
import dima.introspectionbasedagents.kernel.NotReadyException;
import dima.introspectionbasedagents.ontologies.Protocol;
import dima.introspectionbasedagents.ontologies.FIPAACLOntologie.FipaACLEnvelopeClass.FipaACLEnvelope;
import dima.introspectionbasedagents.ontologies.FIPAACLOntologie.FipaACLMessage;
import dima.introspectionbasedagents.ontologies.FIPAACLOntologie.Performative;
import dima.introspectionbasedagents.services.AgentCompetence;
import dima.introspectionbasedagents.services.UnrespectedCompetenceSyntaxException;
import dima.introspectionbasedagents.services.information.NoInformationAvailableException;
import frameworks.negotiation.faulttolerance.Host;
import frameworks.negotiation.negotiationframework.NegotiatingAgent;
import frameworks.negotiation.negotiationframework.NegotiationParameters;
import frameworks.negotiation.negotiationframework.contracts.AbstractActionSpecif;
import frameworks.negotiation.negotiationframework.contracts.AbstractContractTransition;
import frameworks.negotiation.negotiationframework.contracts.AbstractContractTransition.IncompleteContractException;
import frameworks.negotiation.negotiationframework.contracts.ContractIdentifier;
import frameworks.negotiation.negotiationframework.contracts.ContractTransition;
import frameworks.negotiation.negotiationframework.contracts.ContractTrunk;
import frameworks.negotiation.negotiationframework.contracts.MatchingCandidature;
import frameworks.negotiation.negotiationframework.contracts.UnknownContractException;
import frameworks.negotiation.negotiationframework.rationality.AgentState;
import frameworks.negotiation.negotiationframework.rationality.SimpleRationalAgent;

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
PersonalState extends AgentState,
Contract extends AbstractContractTransition>
extends Protocol<NegotiatingAgent<PersonalState, Contract>> {
	private static final long serialVersionUID = 7728287555094295894L;

	//
	// Roles
	//

	public interface ProposerCore
	<Agent extends NegotiatingAgent<PersonalState, Contract>,
	PersonalState extends AgentState,
	Contract extends AbstractContractTransition>
	extends AgentCompetence<Agent>{

		public Set<? extends Contract> getNextContractsToPropose()
				throws NotReadyException;

		public boolean IWantToNegotiate(ContractTrunk<Contract> contracts);


	}

	public interface SelectionCore<
	Agent extends NegotiatingAgent<PersonalState, Contract>,
	PersonalState extends AgentState,
	Contract extends AbstractContractTransition>
	extends	AgentCompetence<Agent> {

		// Select contract to accept/wait/reject for a participant
		// Select contract to request/cancel for a initiator
		public void select(
				ContractTrunk<Contract> cs,
				PersonalState currentState,
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

	private final ContractTrunk<Contract> contracts;


	//
	// Constructor
	//

	public AbstractCommunicationProtocol(
			final ContractTrunk<Contract> contracts) throws UnrespectedCompetenceSyntaxException {
		super();
		this.contracts = contracts;
	}

	@Override
	public void setMyAgent(final NegotiatingAgent<PersonalState, Contract> ag) {
		super.setMyAgent(ag);
		this.getContracts().setMyAgent(ag);
	}

	//
	// Accessors
	//

	public ContractTrunk<Contract> getContracts() {
		return this.contracts;
	}

	//	@MessageHandler
	//	@NotificationEnvelope(SimpleObservationService.informationObservationKey)
	//	public void updateContracts(NotificationMessage<Serializable> notif){
	//
	//	}
	////////////////////////////////////////////////
	// Behavior
	//

	/*
	 * Initiator
	 */

	protected abstract boolean ImAllowedToNegotiate(ContractTrunk<Contract> contracts);

	// @role(NegotiationInitiatorRole.class)
	@PreStepComposant(ticker = NegotiationParameters._initiatorPropositionFrequency)
	public void initiateNegotiation() {
		if (this.isActive() &&
				ImAllowedToNegotiate(this.contracts)
				&& this.getMyAgent().getMyProposerCore().IWantToNegotiate(this.contracts)) {
			try {
				final Collection<? extends Contract> cs =
						this.getMyAgent().getMyProposerCore()
						.getNextContractsToPropose();
				this.proposeContracts(cs);
			} catch (final NotReadyException e) {}
		}
	}


	private void proposeContracts(final Collection<? extends Contract> cs) {
		for (final Contract c : cs){
			this.logMonologue("**************> I propose "+c,AbstractCommunicationProtocol.log_negotiationStep);

			this.getMyAgent().setMySpecif(c);
			c.setInitialState(this.getMyAgent().getMyCurrentState());
//			for (AgentIdentifier id : c .getNotInitiatingParticipants()){
//				try {
//					c.setInitialState(this.getMyAgent().getMyInformation().getInformation(
//							getMyAgent().getMyCurrentState().getMyResourcesClass(), 
//							id));
//				} catch (NoInformationAvailableException e) {}
//			}

			this.send(c, Receivers.NotInitiatingParticipant, new SimpleContractProposal(Performative.Propose, c));
			//, (Collection<Information>) this.getMyAgent().getMyResources()));

			this.contracts.addContract(c);
			assert this.receivedContract.add(c.getIdentifier());
		}
	}

	/*
	 * Participant
	 */

	// @role(NegotiationParticipant.class)
	@StepComposant(ticker = NegotiationParameters._timeToCollect)
	protected void answer() {
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

			//			assert ContractTransition.allComplete(getContracts().getAllContracts());

			this.getMyAgent().getMySelectionCore().select(
					this.getContracts(), getMyAgent().getMyCurrentState(),toAccept, toReject, toPutOnWait);

			//
			// Validity
			//

			assert AbstractCommunicationProtocol.partitioning(this.getContracts().getAllContracts(),toAccept, toReject,toPutOnWait):
				"->"+toAccept+"\n->"+toReject+"\n->"+toPutOnWait;

			//
			// Answerin
			//
			//important de mettre les rejet avant les acceptation!!
			//les acceptation conduisen a une modification de l'état qui trendra les contrat rejeté incohérent
			this.answerRejected(toReject);
			this.answerAccepted(toAccept);
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
			final SimpleContractAnswer m =new SimpleContractAnswer(
					Performative.AcceptProposal,
					contract.getIdentifier());
			m.setMyNewState(this.getMyAgent().getMyCurrentState());

			this.send(contract, receivers,m);

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
			final SimpleContractAnswer m =new SimpleContractAnswer(
					Performative.RejectProposal,
					contract.getIdentifier());
			m.setMyNewState(this.getMyAgent().getMyCurrentState());

			this.send(contract, receivers,m);

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

		AgentState myInitialState = this.getMyAgent().getMyCurrentState();
		this.contracts.removeAll(contracts);
		assert contractsAreClean(contracts);
		this.getMyAgent().execute(contracts);
		
		for (final Contract contract : contracts) {
			assert this.alreadyExecuted.add(contract.getIdentifier());
			final SimpleContractAnswer m =
					new SimpleContractAnswer(
							Performative.Confirm,
							contract.getIdentifier());
			m.setMyInitialState(myInitialState);
			m.setMyNewState(this.getMyAgent().getMyCurrentState());
			this.send(contract, receivers,m);
		}
		this.notify(this.getMyAgent().getMyCurrentState(), SimpleRationalAgent.stateChangementObservation);
	}
	
	private boolean contractsAreClean(final Collection<Contract> cs){
		for (Contract n:cs){
			for (AgentIdentifier id : n.getAllInvolved())
				if (!id.equals(getMyAgent().getIdentifier()))
				assert getContracts().getContracts(id).isEmpty():id+" -->\n"+getContracts().getContracts(id);
		}
	return true;
	}

	protected void cancelContract(final Collection<Contract> contracts, final Receivers receivers) {
		assert ContractTransition.stillValid(contracts);

		if (!contracts.isEmpty()) {
			this.getMyAgent().logMonologue("**************> I cancel!"+contracts,
					AbstractCommunicationProtocol.log_negotiationStep);
		}


		for (final Contract contract : contracts){
			final SimpleContractAnswer m =new SimpleContractAnswer(
					Performative.Cancel,
					contract.getIdentifier());
			m.setMyNewState(this.getMyAgent().getMyCurrentState());

			this.send(contract, receivers,m);


			assert this.alreadyCancelled.add(contract.getIdentifier());
			this.contracts.remove(contract);
		}
	}

	//////////////////////////////////////////////////////////////////
	// Reception Primitive
	//

	@MessageHandler()
	@FipaACLEnvelope(performative = Performative.Propose, protocol = AbstractCommunicationProtocol.class)
	public void receiveProposal(final SimpleContractProposal delta)  {
		final Contract contract = delta.getMyContract();

		//			try {
		//				contract.getInitialState(getMyAgent().getIdentifier());
		//				assert false:contract.getInitialState(getMyAgent().getIdentifier())+" "+contract;
		//			} catch (IncompleteContractException e) {
		//				//good
		//			}

		assert this.receivedContract.add(contract.getIdentifier());
		this.logMonologue("I've received proposal "+contract,AbstractCommunicationProtocol.log_negotiationStep);
		contract.setInitialState(this.getMyAgent().getMyCurrentState());
		this.getMyAgent().setMySpecif(contract);
		assert contract.isComplete();
		
		try {
			this.getMyAgent().setInformation(contract.getInitialState(contract.getInitiator()));
		} catch (IncompleteContractException e1) {
			assert false;
		}

		this.contracts.addContract(contract);
		try {
			assert this.getContracts().getContract(contract.getIdentifier()).isComplete();
		} catch (final UnknownContractException e) {
			throw new RuntimeException("impossible");
		}
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
		this.getMyAgent().setInformation(delta.getMyNewState());
		assert contract.isComplete():contract+" "+delta.getMyNewState();

		final ContractIdentifier c = delta.getIdentifier();
		assert !c.hasReachedExpirationTime();

		assert c.getInitiator().equals(this.getMyAgent().getIdentifier());
		assert this.contracts.contains(c) || c.willReachExpirationTime(NegotiationParameters._timeToCollect):
			"aaaaaaaaarrrgh" + "i should now "+ c;

		this.logMonologue("I 've been accepted! =) "+c//+"\n"+this.getMyAgent().getMyCurrentState()
				,AbstractCommunicationProtocol.log_negotiationStep);

		//		this.getMyAgent().getMyInformation().add(delta.getSpec());
		this.contracts.addAcceptation(delta.getSender(), contract);
		try {
			assert this.getContracts().getContract(contract.getIdentifier()).isComplete();
		} catch (final UnknownContractException e) {
			throw new RuntimeException("impossible");
		}
		//		contract.setSpecification(delta.getSpec());
	}

	@MessageHandler()
	@FipaACLEnvelope(performative = Performative.RejectProposal, protocol = AbstractCommunicationProtocol.class)
	protected void receiveReject(final SimpleContractAnswer delta) {

		Contract contract;
		this.getMyAgent().setInformation(delta.getMyNewState());
		try {
			contract = this.contracts.getContract(delta.getIdentifier());
		} catch (final UnknownContractException e) {
			//			this.faceAnUnknownContract(e);
			return;
		}
		assert contract.isComplete():contract;
		assert contract.getInitiator().equals(this.getMyAgent().getIdentifier());

		this.logMonologue("I 've been rejected! =( "+contract//+"\n"+this.getMyAgent().getMyCurrentState()
				,AbstractCommunicationProtocol.log_negotiationStep);

		this.contracts.addRejection(delta.getSender(), contract);
		try {
			assert this.getContracts().getContract(contract.getIdentifier()).isComplete();
		} catch (final UnknownContractException e) {
			throw new RuntimeException("impossible");
		}
		//		contract.setSpecification(delta.getSpec());
	}

	@MessageHandler()
	@FipaACLEnvelope(performative = Performative.Confirm, protocol = AbstractCommunicationProtocol.class)
	protected void receiveConfirm(final SimpleContractAnswer delta) {

		Contract contract;
		try {
			contract = this.contracts.getContract(delta.getIdentifier());
		} catch (final UnknownContractException e) {
			this.faceAnUnknownContract(e);
			return;
		}

		assert !contract.hasReachedExpirationTime();
		assert !(this.getMyAgent() instanceof Host)	|| !((Host) this.getMyAgent()).getMyCurrentState().isFaulty();
		assert this.contracts.getContractsAcceptedBy(this.getMyAgent().getIdentifier()).contains(contract);
		assert this.alreadyExecuted.add(contract.getIdentifier());
		assert delta.getMyInitialState()!=null;
		assert delta.getMyNewState()!=null;
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

		this.contracts.remove(contract);

		contract.setInitialState(delta.getMyInitialState());
		assert contract.isComplete():contract;
		
		this.getMyAgent().setInformation(delta.getMyNewState());
		this.getMyAgent().execute(contract);
		this.getMyAgent().setInformation(delta.getMyNewState());
		this.notify(this.getMyAgent().getMyCurrentState(), SimpleRationalAgent.stateChangementObservation);

	}

	@MessageHandler()
	@FipaACLEnvelope(performative = Performative.Cancel, protocol = AbstractCommunicationProtocol.class)
	protected void receiveCancel(final SimpleContractAnswer delta) {

		Contract contract;
		this.getMyAgent().setInformation(delta.getMyNewState());
		try {
			contract = this.contracts.getContract(delta.getIdentifier());
		} catch (final UnknownContractException e) {
			this.faceAnUnknownContract(e);
			return;
		}

		final AgentIdentifier id = delta.getSender();
		final ContractIdentifier c = delta.getIdentifier();

		this.logMonologue("I've received cancel "+c,AbstractCommunicationProtocol.log_negotiationStep);
		assert this.alreadyCancelled.add(c);
		this.contracts.remove(contract);
	}

	/*
	 *
	 */

	protected void separateInitiator(final Collection<Contract> all, final Collection<Contract> initiator, final Collection<Contract> participant){
		assert initiator.isEmpty();
		assert participant.isEmpty();

		for (final Contract a : all) {
			if (a.getInitiator().equals(this.getMyAgent().getIdentifier())) {
				initiator.add(a);
			} else {
				participant.add(a);
			}
		}
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
		AbstractActionSpecif mySpec=null;
		AgentState myNewState=null;
		AgentState myInitialState=null;

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
//			assert AbstractCommunicationProtocol.this.getContracts().contains(id);
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

		public AbstractActionSpecif getMySpec() {
			return this.mySpec;
		}

		public void setMySpec(final AbstractActionSpecif mySpec) {
			this.mySpec = mySpec;
		}

		public AgentState getMyNewState() {
			return this.myNewState;
		}

		public void setMyNewState(final AgentState myNewState) {
			this.myNewState = myNewState;
		}

		public AgentState getMyInitialState() {
			return myInitialState;
		}

		public void setMyInitialState(AgentState myInitialState) {
			this.myInitialState = myInitialState;
		}

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
			AbstractCommunicationProtocol.this.getMyAgent().setMySpecif(myContract);
			myContract.setInitialState(
					AbstractCommunicationProtocol.this.getMyAgent().getMyCurrentState());
			// this.myContract = (Contract) myContract.clone();
			this.myContract = (Contract) myContract.clone();
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
	protected void faceAnUnknownContract(final UnknownContractException e) {
		try {
			if (this.alreadyCancelled.contains(e.getId())) {
				this.alreadyCancelled.remove(e.getId());
			} else {
				this.signalException("facing unknonw contract!!!!! ");
				//			this.signalException("facing unknonw contract!!!!! " + e.getId()
				//					+"\nreceivedContract?"+receivedContract.contains(e.getId())
				//					+"\nalreadyCancelled?"+alreadyCancelled.contains(e.getId())
				//					+"\nalreadyExecuted?"+alreadyExecuted.contains(e.getId())
				//					+ " lost contracts are : ",e);// + this.losts, e);
				//			this.sendMessage(e.getId().getParticipants(), new ShowYourPocket(
				//					this.getIdentifier(), "facing an unknown contract"));
			}
		} catch (Exception ex){
			//			this.signalException("facing unknonw contract!!!!! ",e);			
		}
	}



	// @role(NegotiationParticipant.class)

	public static <Contract extends AbstractContractTransition>
	boolean partitioning(
			final Collection<Contract> allContracts,
			final Collection<Contract> accepted,
			final Collection<Contract> rejected,
			final Collection<Contract> onWait) {

		final Collection<Contract> all = new ArrayList<Contract>(allContracts);
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
		+accepted+"\n REJECTED \n"+rejected+"\n ON WAIT \n"+onWait+"\nALL fin \n"+all;

		return true;
	}

	public static <Contract extends AbstractContractTransition>
	boolean allRequestable(
			final Collection<Contract> all,
			final ContractTrunk<Contract> ct){
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
