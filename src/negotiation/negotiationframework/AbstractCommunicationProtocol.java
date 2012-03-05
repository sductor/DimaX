package negotiation.negotiationframework;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.Set;

import negotiation.experimentationframework.ExperimentationProtocol;
import negotiation.faulttolerance.candidaturewithstatus.Host;
import negotiation.faulttolerance.experimentation.ReplicationExperimentationProtocol;
import negotiation.negotiationframework.contracts.AbstractActionSpecification;
import negotiation.negotiationframework.contracts.AbstractContractTransition;
import negotiation.negotiationframework.contracts.AbstractContractTransition.IncompleteContractException;
import negotiation.negotiationframework.contracts.ContractIdentifier;
import negotiation.negotiationframework.contracts.ContractTrunk;
import negotiation.negotiationframework.contracts.ResourceIdentifier;
import negotiation.negotiationframework.contracts.UnknownContractException;
import negotiation.negotiationframework.protocoles.status.DestructionOrder;
import negotiation.negotiationframework.protocoles.status.DestructionOrder.DestructionOrderIdentifier;
import dima.basicagentcomponents.AgentIdentifier;
import dima.introspectionbasedagents.annotations.MessageHandler;
import dima.introspectionbasedagents.annotations.StepComposant;
import dima.introspectionbasedagents.ontologies.Protocol;
import dima.introspectionbasedagents.ontologies.FIPAACLOntologie.FipaACLEnvelopeClass.FipaACLEnvelope;
import dima.introspectionbasedagents.ontologies.FIPAACLOntologie.FipaACLMessage;
import dima.introspectionbasedagents.ontologies.FIPAACLOntologie.Performative;
import dima.introspectionbasedagents.services.AgentCompetence;
import dima.introspectionbasedagents.services.UnrespectedCompetenceSyntaxException;
import dima.introspectionbasedagents.services.information.NoInformationAvailableException;
import dima.introspectionbasedagents.services.loggingactivity.LogService;
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
	// Sp��cifs
	//


	public interface ProposerCore
	<Agent extends SimpleNegotiatingAgent<ActionSpec, PersonalState, Contract>,
	ActionSpec extends AbstractActionSpecification,
	PersonalState extends ActionSpec,
	Contract extends AbstractContractTransition<ActionSpec>>
	extends AgentCompetence<Agent>{

		public Set<? extends Contract> getNextContractsToPropose()
				throws NotReadyException;

	}

	public interface SelectionCore<
	ActionSpec extends AbstractActionSpecification,
	PersonalState extends ActionSpec,
	Contract extends AbstractContractTransition<ActionSpec>>
	extends	AgentCompetence<SimpleNegotiatingAgent<ActionSpec, PersonalState, Contract>> {

		// Select contract to accept/wait/reject for a participant
		// Select contract to request/cancel for a initiator
		public ContractTrunk<Contract, ActionSpec, PersonalState> select(ContractTrunk<Contract, ActionSpec, PersonalState> cs);

	}

	// public interface RationalInitiatorCore
	// <Contract extends ContractTransition<?>>
	// extends
	// ProposerCore<Contract>,
	// SelectionCore<Contract>{}
	//
	// public interface RationalParticipantCore
	// <Contract extends ContractTransition<?>>
	// extends
	// SelectionCore<Contract>{}

	//
	// Fields
	//

	private final ContractTrunk<Contract, ActionSpec, State> contracts;

	public static final String log_negotiationStep="negotiation step for log";
	public static final String log_contractDataBaseManipulation="manipulation of contracts database";
	public static final String log_selectionStep = "selection step of contract answering";
	//
	// Constructor
	//


	public AbstractCommunicationProtocol(
			final SimpleNegotiatingAgent<ActionSpec, State, Contract> a,
			ContractTrunk<Contract, ActionSpec, State> contracts) throws UnrespectedCompetenceSyntaxException {
		super(a);
		this.contracts = contracts;
	}
	public AbstractCommunicationProtocol(
			ContractTrunk<Contract, ActionSpec, State> contracts) throws UnrespectedCompetenceSyntaxException {
		super();
		this.contracts = contracts;
	}

	//
	// Accessors
	//

	public ContractTrunk<Contract, ActionSpec, State> getContracts() {
		return this.contracts;
	}

	public void start() {
		setActive(true);
	}

	public Collection<Contract> losts = new ArrayList<Contract>();

	public void setLost(final ResourceIdentifier host) {
		if (!host.equals(this.getMyAgent().getIdentifier()))
			for (final Contract c : this.contracts.getContracts(host)) {
				this.contracts.remove(c);
				this.losts.add(c);
				// final SimpleContractAnswer reject =
				// new SimpleContractAnswer(
				// Performative.RejectProposal,
				// c.getIdentifier());
				// reject.setSender(host);
				// receiveReject(reject);
			}
		else{
			this.losts.addAll(this.contracts.getAllContracts());
			this.contracts.clear();

		}
		//			throw new RuntimeException(
		//					"impossible!! vérifier le fault service!!");

	}

	public void stop() {
		this.losts.addAll(this.contracts.getAllContracts());
		this.contracts.clear();
		setActive(false);
	}

	public boolean negotiationAsInitiatorHasStarted() {
		final Collection<Contract> initiatorContracts = this.contracts
				.getAllInitiatorContracts();
		initiatorContracts.removeAll(this.contracts.getContractsRejectedBy(this
				.getMyAgent().getIdentifier()));
		if (initiatorContracts.isEmpty()) {// removing old contract before new
			// nego
			for (final Contract c : this.contracts.getAllInitiatorContracts()) {
				this.contracts.remove(c);
				this.losts.add(c);
			}
			return false;
		} else
			return true;
		// return !this.contracts.isEmpty();
	}

	Collection<Contract> cleaned = new ArrayList<Contract>();
	protected void cleanContracts() {
		for (final Contract c : this.contracts.getAllInitiatorContracts())
			if (c.hasReachedExpirationTime()) {
				this.logWarning("contract expired!!!! =( "
						+ this.contracts.statusOf(c),LogService.onBoth);
				if (!this.contracts.getContractsRejectedBy(
						this.getMyAgent().getIdentifier()).contains(c)) {
					this.contracts.addRejection(this.getMyAgent()
							.getIdentifier(), c);
					this.sendCancel(c.getIdentifier());
				}
				this.contracts.remove(c);
				this.cleaned.add(c);
			}
		final Iterator<Contract> itlost = this.losts.iterator();
		while (itlost.hasNext()) {
			final Contract c = itlost.next();
			if (c.hasReachedExpirationTime()) {
				itlost.remove();
				this.cleaned.add(c);
			}
		}
	}



	////////////////////////////////////////////////
	// Behavior
	//

	/*
	 * Initiator
	 */

	// @role(NegotiationInitiatorRole.class)
	@StepComposant(ticker = ReplicationExperimentationProtocol._initiatorPropositionFrequency)
	public void initiateNegotiation() {
		//			 this.logMonologue(" Initiating nego  1 : "+!this.getMyProtocol().negotiationAsInitiatorHasStarted()+" 2 : "
		//			 +this.myCore.IWantToNegotiate(this.getMyCurrentState())
		//			 +" ");
		//			 if (((ReplicaState)getMyCurrentState()).getMyStateStatus()
		//			 .equals(AgentStateStatus.Fragile))
		//			 logMonologue("yooooo!");
		//			 logMonologue("already nego? "+!myInitiatorRole2.negotiationHasStarted()
		//			 +"\n should nego? "+myCore.IWantToNegotiate()
		//			 +"\n myState "+getMyCurrentState());
		if (!this.negotiationAsInitiatorHasStarted()
				&& this.getMyAgent().myCore.IWantToNegotiate(this.getMyAgent().getMyCurrentState()))
			try {
				final Collection<? extends Contract> cs = this.getMyAgent().getMyProposerCore()
						.getNextContractsToPropose();
				for (final Contract c : cs)
					c.setSpecification(this.getMyAgent().getMyCurrentState());


						this.propose(cs);
						// if (!cs.isEmpty())
						// this.logMonologue(" I'm proposing those contracts : "+cs);//+"\n my state :\n"+
						// //
						// else{
						// //
						// logException("I dont propose :\n -----> "+getKnownAgents()
						// // +"\n -----> "+((ReplicaState)
						// getMyCurrentState()).getMyReplicas());
						// }
			} catch (final NotReadyException e) {}
	}

	/*
	 * Participant
	 */







	////////////////////////////////////////////////////
	// Communication Methods
	//

	/*
	 * Initiator
	 */

	// @role(NegotiationInitiatorRole.class)
	protected void propose(final Collection<? extends Contract> cs) {
		if (this.isActive())
			for (final Contract c : cs) {
				this.logMonologue("**************> I propose "+c,AbstractCommunicationProtocol.log_negotiationStep);
				this.sendPropose(c, this.getMyAgent().getMySpecif(c));
				/**/
				//				if (c instanceof DestructionOrder)
				//					getMyAgent().execute(c);
				//				else
				this.contracts.addContract(c);
			}
	}

	/*
	 * Participant
	 */

	// @role(NegotiationParticipant.class)
	protected void acceptContract(final Contract contract) {
		this.getMyAgent().logMonologue("**************> I accept proposal "+contract.getIdentifier()
				+"\n"+this.getMyAgent().getMyCurrentState(),AbstractCommunicationProtocol.log_negotiationStep);

		this.sendAccept(contract.getIdentifier(), this.getMyAgent()
				.getMySpecif(contract));
		this.contracts.addAcceptation(this.getMyAgent().getIdentifier(),
				contract);
	}

	// @role(NegotiationParticipant.class)
	protected void rejectContract(final Contract contract) {
		this.getMyAgent().logMonologue("**************> I reject proposal "+contract.getIdentifier()
				+"\n"+this.getMyAgent().getMyCurrentState(),AbstractCommunicationProtocol.log_negotiationStep);
		this.contracts.addRejection(this.getMyAgent().getIdentifier(), contract);
		this.notify(contract);
		this.sendReject(contract.getIdentifier());
	}

	/*
	 * Initiator
	 */

	// @role(NegotiationInitiatorRole.class)
	protected void requestContract(final Contract contract) {
		this.getMyAgent().logMonologue("**************> I request!"+contract.getIdentifier()+" --> "
				+this.contracts.statusOf(contract)+"\n"+this.getMyAgent().getMyCurrentState(),AbstractCommunicationProtocol.log_negotiationStep);
		this.sendRequest(contract.getIdentifier());
		this.getMyAgent().execute(contract);
		this.contracts.remove(contract);
	}

	// @role(NegotiationInitiatorRole.class)
	protected void cancelContract(final Contract contract) {
		this.getMyAgent().logMonologue("**************> I cancel!"+contract.getIdentifier()
				+"\n"+this.getMyAgent().getMyCurrentState(),AbstractCommunicationProtocol.log_negotiationStep);
		this.sendCancel(contract.getIdentifier());
		this.contracts.remove(contract);
	}

	//////////////////////////////////////////////////////////////////
	// Communication Primitive
	//

	//
	// Message Sending (Encapsule dans une envellope et envoie le message)

	/*
	 * Propose
	 */

	// @role(NegotiationInitiatorRole.class)
	void sendPropose(final Contract c, final ActionSpec s) {
		if (s == null)
			throw new NullPointerException();

		c.setSpecification(s);

		final SimpleContractEnvellope proposal = new SimpleContractEnvellope(
				Performative.Propose, c);
		this.sendMessage(c.getNotInitiatingParticipants(), proposal);
		// logMonologue("Message Send :\n"+proposal);
	}

	//

	//
	// Collection<ContractIdentifier> receivedContract = new
	// ArrayList<ContractIdentifier>();
	// Collection<ContractIdentifier> alreadyCancelled = new
	// ArrayList<ContractIdentifier>();
	// Collection<ContractIdentifier> alreadyExecuted = new
	// ArrayList<ContractIdentifier>();
	// @role(NegotiationParticipant.class)
	@MessageHandler()
	@FipaACLEnvelope(performative = Performative.Propose, protocol = AbstractCommunicationProtocol.class)
	void receiveProposal(final SimpleContractEnvellope delta) {
		this.updateInformations(delta.getMyContract());

		delta.getMyContract().setSpecification(this.getMyAgent().getMySpecif(delta.getMyContract()));
		this.cleanContracts();
		final Contract c = delta.getMyContract();
		this.logMonologue("I've received proposal "+c,AbstractCommunicationProtocol.log_negotiationStep);
		if ((this.getMyAgent() instanceof Host) && ((Host) this.getMyAgent()).getMyCurrentState().isFaulty()){
			this.contracts.addContract(c);
			this.sendReject(c.getIdentifier());
			//assert 1<0:"TODO?? : answer cancel??";
		}else if (c instanceof DestructionOrder){
			//			this.logMonologue("I've received destruction order "+c,CommunicationProtocol.log_negotiationStep);
			//acceptContract(c);
			this.getMyAgent().execute(c);
			this.sendAccept(c.getIdentifier(), this.getMyAgent().getMySpecif(c));
			//this.getMyAgent().execute(c);
		} else {
			this.contracts.addContract(c);
		}
	}

	private void updateInformations(final Contract delta) {
		for (final AgentIdentifier id : delta.getAllParticipants())
			try {
				if (!id.equals(this.getIdentifier()) && delta.getSpecificationOf(id)!=null){
					Boolean contractIsOutOfDate = null;

					try {
						contractIsOutOfDate = this.getMyAgent().getMyInformation()
								.getInformation(delta.getSpecificationOf(id).getClass(),id).isNewerThan(delta.getSpecificationOf(id))>1;
								if (contractIsOutOfDate)
									delta.setSpecification(
											(ActionSpec) this.getMyAgent().getMyInformation().getInformation(delta.getSpecificationOf(id).getClass(),id));
					} catch (final NoInformationAvailableException e) {
						contractIsOutOfDate = false;
					}

					this.getMyAgent().getMyInformation().add(delta.getSpecificationOf(id));//fait le test a l'intérieur;
				}
			} catch (IncompleteContractException e) {}
	}

	/*
	 * Accept/Reject
	 */

	// @role(NegotiationParticipant.class)
	void sendAccept(final ContractIdentifier c, final ActionSpec s) {
		final SimpleContractAnswer accept = new SimpleContractAnswer(
				Performative.AcceptProposal, c, s);
		this.sendMessage(c.getInitiator(), accept);
	}

	// @role(NegotiationParticipant.class)
	void sendReject(final ContractIdentifier c) {
		final SimpleContractAnswer reject = new SimpleContractAnswer(
				Performative.RejectProposal, c);
		this.sendMessage(c.getInitiator(), reject);


		//		if (c instanceof DestructionCandidature)
		//			logMonologue("sending destruction rejection "+c, NegotiationProtocol.log_mirrorProto);
	}

	//


	@MessageHandler()
	@FipaACLEnvelope(performative = Performative.AcceptProposal, protocol = AbstractCommunicationProtocol.class)
	void receiveAccept(final SimpleContractAnswer delta) {

		//		updateInformations(delta.getMyContract());
		this.cleanContracts();

		final AgentIdentifier id = delta.getSender();
		final ContractIdentifier c = delta.getIdentifier();
		final ActionSpec s = delta.getSpec();
		this.getMyAgent().getMyInformation().add(s);
		this.logMonologue("I 've been accepted! =) "+c//+"\n"+this.getMyAgent().getMyCurrentState()
				,AbstractCommunicationProtocol.log_negotiationStep);

		assert c.getInitiator().equals(this.getMyAgent().getIdentifier());
		assert this.contracts.contains(c) || c.willReachExpirationTime(ExperimentationProtocol._timeToCollect):
			"aaaaaaaaarrrgh" + "i should now "+ c + "!!!!!\n  -----> lost " + this.losts+" -------> ";

		//		if (c instanceof DestructionOrderIdentifier)//A déplacer dans un message handler!!!
		//			try {
		//				//				requestContract(this.getContracts().getContract(c));
		//				this.getMyAgent().execute(this.getContracts().getContract(c));
		//				this.contracts.remove(this.getContracts().getContract(c));
		//			} catch (final UnknownContractException e) {
		//				this.signalException("aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaahhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhh!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!",e);
		//			}
		//		else
		if (this.losts.contains(c)){
			// do nothing
		}
		else if (!c.hasReachedExpirationTime()) {
			Contract contract;
			try {
				contract = this.contracts.getContract(c);
			} catch (final UnknownContractException e) {
				this.faceAnUnknownContract(e);
				return;
			}
			this.contracts.addAcceptation(id, contract);
			contract.setSpecification(s);
		}
	}

	@MessageHandler()
	@FipaACLEnvelope(performative = Performative.RejectProposal, protocol = AbstractCommunicationProtocol.class)
	void receiveReject(final SimpleContractAnswer delta) {
		//		updateInformations(delta.getMyContract());
		this.cleanContracts();
		final AgentIdentifier id = delta.getSender();
		final ContractIdentifier c = delta.getIdentifier();
		this.logMonologue("I 've been rejected! =( "+c//+"\n"+this.getMyAgent().getMyCurrentState()
				,AbstractCommunicationProtocol.log_negotiationStep);


		if (!this.contracts.getContractsRejectedBy(
				this.getMyAgent().getIdentifier()).contains(c)) {
			this.sendCancel(c);// CONSENSUAL NEGOTIATION
			try{
				this.contracts.remove(this.contracts.getContract(c));
			} catch (final UnknownContractException e) {
				this.faceAnUnknownContract(e);
			}
		} else {
			// on ignore tout a d��j�� ��t�� fait!
		}
	}

	/*
	 * Request/Cancel
	 */

	// @role(NegotiationInitiatorRole.class)
	void sendRequest(final ContractIdentifier c) {
		assert c!=null;
		try {
			assert this.getContracts().getRequestableContracts().contains(this.contracts.getContract(c)):c;
		} catch (UnknownContractException e) {
			throw new RuntimeException();
		}

		final SimpleContractAnswer request = new SimpleContractAnswer(
				Performative.Request, c);
		this.sendMessage(c.getNotInitiatingParticipants(), request);
	}

	// @role(NegotiationInitiatorRole.class)
	void sendCancel(final ContractIdentifier c) {
		final SimpleContractAnswer cancel = new SimpleContractAnswer(
				Performative.Cancel, c);
		this.sendMessage(c.getNotInitiatingParticipants(), cancel);


		//		if (c instanceof DestructionCandidature)
		//			logMonologue("sending destruction cancel "+c, NegotiationProtocol.log_mirrorProto);


	}

	//

	// @role(NegotiationParticipant.class)
	@MessageHandler()
	@FipaACLEnvelope(performative = Performative.Request, protocol = AbstractCommunicationProtocol.class)
	void receiveRequest(final SimpleContractAnswer m) {		
		this.cleanContracts();
		final ContractIdentifier c = m.getIdentifier();
		Contract contract;
		try {
			contract = this.contracts.getContract(c);
		} catch (final UnknownContractException e) {
			this.faceAnUnknownContract(e);
			return;
		}

		assert !c.hasReachedExpirationTime();
		assert (!(this.getMyAgent() instanceof Host)	|| !((Host) this.getMyAgent()).getMyCurrentState().isFaulty());
		assert this.contracts.getContractsAcceptedBy(this.getMyAgent().getIdentifier()).contains(contract);

		final ActionSpec s = m.getSpec();
		if (m!=null) this.getMyAgent().getMyInformation().add(s);
		
		try {
			assert contract.isViable(this.getMyAgent().getMyCurrentState()):
				"what the !!!!!!\n bad contract "
				+this.getContracts().statusOf(contract)
				+ "\nnew state "
				+ this.getMyAgent()
				.getMyResultingState(contract);
		} catch (IncompleteContractException e1) {
			getMyAgent().signalException("what the !!!!!!\n bad contract "
					+this.getContracts().statusOf(contract)
					+ "\nnew state "
					+ this.getMyAgent()
					.getMyResultingState(contract));
		}		

		this.getMyAgent().logMonologue("I'll apply proposal "+c,AbstractCommunicationProtocol.log_negotiationStep);//+"\n"+contracts);


		this.getMyAgent().execute(contract);
		this.contracts.remove(contract);

		//updating
		for (final AgentIdentifier id : contract.getAllParticipants()){
			if (!id.equals(this.getIdentifier()))
				try {
					this.getMyAgent().getMyInformation().add(contract.computeResultingState(id));
				} catch (IncompleteContractException e) {
					throw new RuntimeException(e);
				}
		}
	}

	// @role(NegotiationParticipant.class)
	@MessageHandler()
	@FipaACLEnvelope(performative = Performative.Cancel, protocol = AbstractCommunicationProtocol.class)
	void receiveCancel(final SimpleContractAnswer m) {
		this.cleanContracts();
		final AgentIdentifier id = m.getSender();
		final ContractIdentifier c = m.getIdentifier();

		this.logMonologue("I've received cancel "+c,AbstractCommunicationProtocol.log_negotiationStep);

		//		if (!(this.getMyAgent() instanceof Host)
		//				|| !((Host) this.getMyAgent()).getMyCurrentState().isFaulty())
		// try {
		//			if (id.equals(c.getInitiator()) && !this.losts.contains(c))
		// this.contracts.addRejection(id,
		// this.contracts.getContract(c));//UTILISER LES EXPIRE POUR
		// AUTRE CHOSE QUE DES COANDIDATURE
		//				if (this.contracts.contains(c))// OOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHH
		// CACHE DES ERREURS
		// BIZZARES!!!!
		try {
			this.contracts.remove(this.contracts.getContract(c));
		} catch (final UnknownContractException e) {
			this.faceAnUnknownContract(e);
		}
		//			else
		//				throw new RuntimeException();
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

	private void faceAnUnknownContract(final UnknownContractException e) {
		this.signalException("facing unknonw contract!!!!! " + e.getId()
				+ " lost contracts are : " + this.losts, e);
		this.sendMessage(e.getId().getParticipants(), new ShowYourPocket(
				this.getIdentifier(), "facing an unknown contract"));
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
		final ActionSpec s;

		// private final AgentIdentifier sender;
		//
		// Constructor
		//

		public SimpleContractAnswer(final Performative p,
				final ContractIdentifier id, final ActionSpec s) {
			super(p, AbstractCommunicationProtocol.class);
			assert s!=null && id!=null;
			this.s = s;
			this.answeredContract = id;
		}

		public SimpleContractAnswer(final Performative p,
				final ContractIdentifier id) {
			super(p, AbstractCommunicationProtocol.class);
			this.s = null;
			this.answeredContract = id;
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

		public ActionSpec getSpec() {
			return this.s;
		}

		/*
		 *
		 */

	}

	//

	public class SimpleContractEnvellope extends FipaACLMessage {
		private static final long serialVersionUID = 7442568804092112906L;

		final Contract myContract;

		public SimpleContractEnvellope(final Performative performative,
				final Contract myContract) {
			super(performative, AbstractCommunicationProtocol.class);
			if (myContract == null)
				throw new NullPointerException();
			// this.myContract = (Contract) myContract.clone();
			this.myContract = myContract;
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
