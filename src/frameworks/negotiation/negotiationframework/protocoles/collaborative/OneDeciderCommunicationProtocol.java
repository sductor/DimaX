package frameworks.negotiation.negotiationframework.protocoles.collaborative;

import java.util.Collection;

import dima.introspectionbasedagents.services.UnrespectedCompetenceSyntaxException;
import frameworks.negotiation.negotiationframework.contracts.AbstractActionSpecif;
import frameworks.negotiation.negotiationframework.contracts.ContractTrunk;
import frameworks.negotiation.negotiationframework.protocoles.AbstractCommunicationProtocol;
import frameworks.negotiation.negotiationframework.rationality.AgentState;

public class OneDeciderCommunicationProtocol <
State extends AgentState,
Contract extends InformedCandidature<Contract>>
extends AbstractCommunicationProtocol<State,Contract>{

	/**
	 *
	 */
	private static final long serialVersionUID = -7022976048693084925L;
	boolean ImDecider;


	public OneDeciderCommunicationProtocol(
			final boolean iMDecider)
					throws UnrespectedCompetenceSyntaxException {
		super(iMDecider?new ResourceInformedCandidatureContractTrunk():new ContractTrunk());
		this.ImDecider = iMDecider;
	}

	@Override
	protected void answerAccepted(final Collection<Contract> toAccept) {
		if (this.ImDecider) {
			this.confirmContract(toAccept, Receivers.EveryParticipant);
		} else {
			this.acceptContract(toAccept, Receivers.Initiator);
		}
	}

	@Override
	protected void answerRejected(final Collection<Contract> toReject) {
		if (this.ImDecider) {
			this.cancelContract(toReject, Receivers.EveryParticipant);
		} else {
			this.rejectContract(toReject, Receivers.Initiator);
		}
	}

	@Override
	protected void putOnWait(final Collection<Contract> toPutOnWait) {
		// Do nothing
	}

	@Override
	protected boolean ImAllowedToNegotiate(ContractTrunk<Contract> contracts) {
		if (ImDecider)
			return true;
		else
			return  contracts.getAllInitiatorContracts().isEmpty();
	}
}


//Iterator<Contract> itCon = toAccept.iterator();
//while (itCon.hasNext()){
//	Contract a = itCon.next();
//	if (!a.isMatchingCreation()){
//		Collection<AgentIdentifier> participant = new ArrayList<AgentIdentifier>();
//		participant.addAll(a.getAllParticipants());
//		participant.remove(this.getIdentifier());
//		//					a.setSpecification(getMyAgent().getMyCurrentState());
//		this.requestContract(a, participant);
//		itCon.remove();
//	}
//}
//for (Contract c : toAccept){
//	Collection<AgentIdentifier> participant = new ArrayList<AgentIdentifier>();
//	participant.addAll(c.getAllParticipants());
//	participant.remove(this.getIdentifier());
//	this.requestContract(c, participant);
//}

//// @role(NegotiationInitiatorRole.class)
//protected void confirm(final Contract contract)
//	//		try {
//	//			assert contract.isViable():contract;
//	//		} catch (IncompleteContractException e) {
//	//			getMyAgent().signalException("impossible");
//	//		}
//	this.getContracts().addAcceptation(this.getMyAgent().getIdentifier(),contract);
//	assert this.getContracts().getRequestableContracts().contains(contract):contract;
//
//	this.getMyAgent().logMonologue("**************> I request!"+contract.getIdentifier()+" --> "
//			+getContracts().statusOf(contract)+"\n"+this.getMyAgent().getMyCurrentState(),AbstractCommunicationProtocol.log_negotiationStep);
//
//
//	Collection<AgentIdentifier> participant = new ArrayList<AgentIdentifier>();
//	participant.addAll(contract.getAllParticipants());
//	participant.remove(this.getIdentifier());
//
//	final SimpleContractAnswer request = new SimpleContractAnswer(
//			Performative.Request, contract.getIdentifier(), getMyAgent().getMySpecif(contract));
//	this.sendMessage(participant, request);
//
//	this.getMyAgent().execute(contract);
//	this.getContracts().remove(contract);
//}


//
//// @role(NegotiationParticipant.class)
//@StepComposant(ticker = ReplicationExperimentationProtocol._timeToCollect)
//void answer() {
//	if (isActive())
//		if (!getContracts().isEmpty()) {
//
//			//
//			// Selecting contracts
//			//
//
//			// logMonologue("What do I have?"+contracts.getOnWaitContracts());
//			final ContractTrunk<Contract, ActionSpec, State> selectedContracts = this
//					.getMyAgent().getMySelectionCore().select(this.getContracts());
//
//			//
//			// Answering
//			//
//			if (ImDecider){
//				//ACCEPTATION
//				Collection<Contract> accepteds =  selectedContracts.getContractsAcceptedBy(this.getMyAgent().getIdentifier());
//				Iterator<Contract> itCon = accepteds.iterator();
//				while (itCon.hasNext()){
//					Contract a = itCon.next();
//					if (!a.isMatchingCreation()){
//						//							a.setSpecification(getMyAgent().getMyCurrentState());
//						this.confirm(a);
//						itCon.remove();
//					}
//				}
//				for (Contract c : accepteds){
//					c.setSpecification(getMyAgent().getMyCurrentState());
//					this.confirm(c);
//				}
//
//				//REFUS
//				for (final Contract contract : selectedContracts.getContractsRejectedBy(this.getMyAgent().getIdentifier())){
//					this.cancelContract(contract);
//				}
//			} else {
//				for (final Contract contract :selectedContracts.getContractsAcceptedBy(this.getMyAgent().getIdentifier())){
//					this.acceptContract(contract);
//				}
//				for (final Contract contract : selectedContracts.getContractsRejectedBy(this.getMyAgent().getIdentifier())){
//					this.rejectContract(contract);
//				}
//			}
//		}
//}
