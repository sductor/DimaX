package negotiation.negotiationframework.interaction.consensualnegotiation;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import com.sun.org.apache.bcel.internal.generic.INSTANCEOF;

import negotiation.experimentationframework.ExperimentationProtocol;
import negotiation.faulttolerance.candidaturenegotiation.mirrordestruction.ReplicationDestructionCandidature;
import negotiation.faulttolerance.experimentation.ReplicationExperimentationProtocol;
import negotiation.faulttolerance.negotiatingagent.Host;
import negotiation.faulttolerance.negotiatingagent.Replica;
import negotiation.negotiationframework.SimpleNegotiatingAgent;
import negotiation.negotiationframework.interaction.candidatureprotocol.mirror.DestructionCandidature;
import negotiation.negotiationframework.interaction.candidatureprotocol.status.DestructionOrder;
import negotiation.negotiationframework.interaction.candidatureprotocol.status.DestructionOrder.DestructionOrderIdentifier;
import negotiation.negotiationframework.interaction.contracts.AbstractActionSpecification;
import negotiation.negotiationframework.interaction.contracts.AbstractContractTransition;
import negotiation.negotiationframework.interaction.contracts.ContractIdentifier;
import negotiation.negotiationframework.interaction.contracts.ResourceIdentifier;
import dima.basicagentcomponents.AgentIdentifier;
import dima.introspectionbasedagents.NotReadyException;
import dima.introspectionbasedagents.annotations.MessageHandler;
import dima.introspectionbasedagents.annotations.StepComposant;
import dima.introspectionbasedagents.ontologies.Protocol;
import dima.introspectionbasedagents.ontologies.FIPAACLOntologie.FipaACLEnvelopeClass.FipaACLEnvelope;
import dima.introspectionbasedagents.ontologies.FIPAACLOntologie.FipaACLMessage;
import dima.introspectionbasedagents.ontologies.FIPAACLOntologie.Performative;
import dima.introspectionbasedagents.services.UnrespectedCompetenceSyntaxException;
import dima.introspectionbasedagents.services.core.loggingactivity.LogMonologue;
import dima.introspectionbasedagents.services.core.loggingactivity.LogService;
import dima.introspectionbasedagents.services.core.observingagent.ShowYourPocket;
import dima.introspectionbasedagents.services.library.information.NoInformationAvailableException;

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
public class NegotiationProtocol<
ActionSpec extends AbstractActionSpecification,
State extends ActionSpec,
Contract extends AbstractContractTransition<ActionSpec>>
extends Protocol<SimpleNegotiatingAgent<ActionSpec, State, Contract>> {
	private static final long serialVersionUID = 7728287555094295894L;

	//
	// Sp��cifs
	//

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

	private boolean ImActive = true;
	private final ContractTrunk<Contract> contracts = new ContractTrunk<Contract>(
			this.getMyAgent().getIdentifier());

	public static final String log_negotiationStep="negotiation step for log";
	public static final String log_mirrorProto="mirror proto step for log";
	public static final String log_contractDataBaseManipulation="manipulation of contracts database";
	public static final String log_selectionStep = "selection step of contract answering";
	//
	// Constructor
	//


	public NegotiationProtocol(
			final SimpleNegotiatingAgent<ActionSpec, State, Contract> a) throws UnrespectedCompetenceSyntaxException {
		super(a);
	}

	//
	// Accessors
	//

	public ContractTrunk<Contract> getContracts() {
		return this.contracts;
	}

	public void start() {
		this.ImActive = true;
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
		else
			throw new RuntimeException(
					"impossible!! vérifier le fault service!!");

	}

	public void stop() {
		this.losts.addAll(this.contracts.getAllContracts());
		this.contracts.clear();
		this.ImActive = false;
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
				if (!this.contracts.getContractsRejectedBy(
						this.getMyAgent().getIdentifier()).contains(c)) {
					this.logWarning("contract expired!!!! =( "
							+ this.contracts.statusOf(c),LogService.onBoth);
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

	// @role(NegotiationParticipant.class)
	@StepComposant(ticker = ReplicationExperimentationProtocol._timeToCollect)
	void answer() {
		if (this.ImActive)
			if (!this.contracts.isEmpty()) {

				//
				// Selecting contracts
				//

				// logMonologue("What do I have?"+contracts.getOnWaitContracts());
				final ContractTrunk<Contract> selectedContracts = this
						.getMyAgent().select(this.contracts);

				//
				// Cleaning contracts
				//

				this.cleanContracts();
				//				final Collection<Contract> allSelectedContracts = selectedContracts
				//						.getAllContracts();
				//				for (final Contract c : this.cleaned)
				//					try {
				//						if (allSelectedContracts.contains(c))
				//							throw new RuntimeException(
				//									"can not negotiate an expired contracts!!");
				//					} catch (final RuntimeException e) {
				//						// c'est bon c'��tait pas le mm
				//					}
				this.cleaned.clear();
				// logMonologue("What I select?"+(answers.isEmpty()?"NUTTIN' >=[":answers));
				// this.contracts.clearRejected();
				// contracts.removeAll(expired);

				//
				// Answering
				//
				for (final Contract contract : selectedContracts.getContractsAcceptedBy(this.getMyAgent().getIdentifier()))
					if (contract.getInitiator().equals(this.getMyAgent().getIdentifier())) {// if im initiator
						if (this.getContracts().getConsensualContracts().contains(contract))//if the contract is consensual
							this.requestContract(contract);
					} else
						this.acceptContract(contract);
				for (final Contract contract : selectedContracts.getRejectedContracts())
					if (contract.getInitiator().equals(this.getMyAgent().getIdentifier())){
						this.cancelContract(contract);
					}else
						// Participant Answering
						this.rejectContract(contract);
			}
	}







	////////////////////////////////////////////////////
	// Communication Methods
	//

	/*
	 * Initiator
	 */

	// @role(NegotiationInitiatorRole.class)
	protected void propose(final Collection<? extends Contract> cs) {
		if (this.ImActive)
			for (final Contract c : cs) {
				this.logMonologue("**************> I propose "+c+"\n"+this.getMyAgent().getMyCurrentState(),NegotiationProtocol.log_negotiationStep);
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
		this.getMyAgent().logMonologue("**************> I accept proposal "+contract+"\n"+this.getMyAgent().getMyCurrentState(),NegotiationProtocol.log_negotiationStep);

		this.sendAccept(contract.getIdentifier(), this.getMyAgent()
				.getMySpecif(contract));
		this.contracts.addAcceptation(this.getMyAgent().getIdentifier(),
				contract);
	}

	// @role(NegotiationParticipant.class)
	protected void rejectContract(final Contract contract) {
		getMyAgent().logMonologue("**************> I reject proposal "+contract+"\n"+getMyAgent().getMyCurrentState(),log_negotiationStep);
		this.contracts
		.addRejection(this.getMyAgent().getIdentifier(), contract);
		this.notify(contract);
		this.sendReject(contract.getIdentifier());
	}

	/*
	 * Initiator
	 */

	// @role(NegotiationInitiatorRole.class)
	protected void requestContract(final Contract contract) {
		this.getMyAgent().logMonologue("**************> I request!"+contract+" --> "+this.contracts.statusOf(contract)+"\n"+this.getMyAgent().getMyCurrentState(),NegotiationProtocol.log_negotiationStep);
		this.sendRequest(contract.getIdentifier());
		this.getMyAgent().execute(contract);
		this.contracts.remove(contract);
	}

	// @role(NegotiationInitiatorRole.class)
	protected void cancelContract(final Contract contract) {
		this.getMyAgent().logMonologue("**************> I cancel!"+contract+"\n"+this.getMyAgent().getMyCurrentState(),NegotiationProtocol.log_negotiationStep);
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
	@FipaACLEnvelope(performative = Performative.Propose, protocol = NegotiationProtocol.class)
	void receiveProposal(final SimpleContractEnvellope delta) {
		this.updateInformations(delta.getMyContract());
		delta.getMyContract().setSpecification(this.getMyAgent().getMySpecif(delta.getMyContract()));
		this.cleanContracts();
		final Contract c = delta.getMyContract();
		this.logMonologue("I've received proposal "+c,NegotiationProtocol.log_negotiationStep);
		if (!(this.getMyAgent() instanceof Host)
			|| !((Host) this.getMyAgent()).getMyCurrentState().isFaulty()){
			if (c instanceof DestructionOrder){
				this.logMonologue("I've received destruction order "+c,NegotiationProtocol.log_negotiationStep);
				//				acceptContract(c);
				this.getMyAgent().execute(c);
				this.sendAccept(c.getIdentifier(), this.getMyAgent().getMySpecif(c));
				//				this.getMyAgent().execute(c);
			} else {
				String spec = "";
				for (final AgentIdentifier id : c.getAllParticipants())
					spec+="\n"+c.getSpecificationOf(id);
						//						getMyAgent().logMonologue("I've received proposal "+c+" spec :"+spec,log_negotiationStep);
						// try {
						this.contracts.addContract(c);
			}
		} else {
			assert 1<0:"TODO?? : answer cancel??";
		}
	}

	private void updateInformations(final Contract delta) {
		for (final AgentIdentifier id : delta.getAllParticipants())
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
	@FipaACLEnvelope(performative = Performative.AcceptProposal, protocol = NegotiationProtocol.class)
	void receiveAccept(final SimpleContractAnswer delta) {
		//		updateInformations(delta.getMyContract());
		this.cleanContracts();

		final AgentIdentifier id = delta.getSender();
		final ContractIdentifier c = delta.getIdentifier();
		final ActionSpec s = delta.getSpec();
		this.getMyAgent().getMyInformation().add(s);
		this.logMonologue("I 've been accepted! =) "+c+"\n"+this.getMyAgent().getMyCurrentState(),NegotiationProtocol.log_negotiationStep);


		if (c instanceof DestructionOrderIdentifier)
			try {
				//				requestContract(this.getContracts().getContract(c));
				this.getMyAgent().execute(this.getContracts().getContract(c));
				this.contracts.remove(this.getContracts().getContract(c));
			} catch (final UnknownContractException e) {
				this.signalException("aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaahhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhh!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!",e);
			}
		else
			if (!c.hasReachedExpirationTime()) {

				final boolean lost = this.losts.contains(c);

				if (s == null)
					throw new RuntimeException("aaaaaaaaarrrgh");
				else if (!c.getInitiator()
						.equals(this.getMyAgent().getIdentifier()))
					throw new RuntimeException("i should not receive this!!!");
				else if (lost) {
					// do nothing
				} else if (!this.contracts.contains(c)
						&& !c.willReachExpirationTime(ExperimentationProtocol._timeToCollect))
					throw new RuntimeException("aaaaaaaaarrrgh" + "i should now "
							+ c + "!!!!!\n" + this.losts);
				// do nothing : probleme avec losts : contract identifier mal
				// reconnu???
				else {
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
	}

	@MessageHandler()
	@FipaACLEnvelope(performative = Performative.RejectProposal, protocol = NegotiationProtocol.class)
	void receiveReject(final SimpleContractAnswer delta) {
		//		updateInformations(delta.getMyContract());
		this.cleanContracts();
		final AgentIdentifier id = delta.getSender();
		final ContractIdentifier c = delta.getIdentifier();
		this.logMonologue("I 've been rejected! =( "+c+"\n"+this.getMyAgent().getMyCurrentState(),NegotiationProtocol.log_negotiationStep);


		if (!this.contracts.getContractsRejectedBy(
				this.getMyAgent().getIdentifier()).contains(c)) {
			this.sendCancel(c);// CONSENSUAL NEGOTIATION
			this.contracts.remove(c);
		} else {
			// on ignore tout a d��j�� ��t�� fait!
		}
	}

	/*
	 * Request/Cancel
	 */

	// @role(NegotiationInitiatorRole.class)
	void sendRequest(final ContractIdentifier c) {
		if (c == null)
			throw new NullPointerException();
		try {
			if (!this.getContracts().getConsensualContracts().contains(this.contracts.getContract(c)))
				throw new RuntimeException(c.toString());
		} catch (final UnknownContractException e) {
			throw new RuntimeException(c.toString());
		}

		//		if (c instanceof DestructionCandidature)
		//			logMonologue("sending destruction request "+c, NegotiationProtocol.log_mirrorProto);



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
	@FipaACLEnvelope(performative = Performative.Request, protocol = NegotiationProtocol.class)
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
		this.getMyAgent().logMonologue("I'll apply proposal "+c,NegotiationProtocol.log_negotiationStep);//+"\n"+contracts);
		// try {
		if (!c.hasReachedExpirationTime())
			if (!(this.getMyAgent() instanceof Host)
					|| !((Host) this.getMyAgent()).getMyCurrentState().isFaulty())
				if (this.contracts.getContractsAcceptedBy(
						this.getMyAgent().getIdentifier()).contains(contract)) {
					if (!this.getMyAgent().respectMyRights(
							this.getMyAgent().getMyCurrentState(), contract))
						throw new RuntimeException(
								"what the !!!!!!\n bad contract "
										+this.getContracts().statusOf(contract)
										+ "\nnew state "
										+ this.getMyAgent()
										.getMyResultingState(contract));
					else {

						this.getMyAgent().execute(contract);
						this.contracts.remove(contract);
						// alreadyExecuted.add(c);

						//updating
						for (final AgentIdentifier id : contract.getAllParticipants())
							if (!id.equals(this.getIdentifier()))
								this.getMyAgent().getMyInformation().add(contract.computeResultingState(id));
					}
				} else {
					this.getMyAgent().signalException("I can not execute a contract i have not accepted!!"+ c);
					this.sendMessage(c.getParticipants(), new ShowYourPocket(this.getIdentifier(),"receiveRequest"));
				}
	}

	// @role(NegotiationParticipant.class)
	@MessageHandler()
	@FipaACLEnvelope(performative = Performative.Cancel, protocol = NegotiationProtocol.class)
	void receiveCancel(final SimpleContractAnswer m) {
		this.cleanContracts();
		final AgentIdentifier id = m.getSender();
		final ContractIdentifier c = m.getIdentifier();
		if (!(this.getMyAgent() instanceof Host)
				|| !((Host) this.getMyAgent()).getMyCurrentState().isFaulty())
			logMonologue("I've received cancel "+c,NegotiationProtocol.log_negotiationStep);
		// try {
		if (id.equals(c.getInitiator()) && !this.losts.contains(c)) {
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
		} else
			throw new RuntimeException();
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
			super(p, NegotiationProtocol.class);
			this.s = s;
			this.answeredContract = id;
		}

		public SimpleContractAnswer(final Performative p,
				final ContractIdentifier id) {
			super(p, NegotiationProtocol.class);
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
			super(performative, NegotiationProtocol.class);
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
