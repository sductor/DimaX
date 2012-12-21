package frameworks.negotiation.protocoles.status;

import java.util.ArrayList;
import java.util.Collection;

import dima.basicagentcomponents.AgentIdentifier;
import dima.introspectionbasedagents.modules.faults.Assert;
import dima.introspectionbasedagents.services.UnrespectedCompetenceSyntaxException;
import frameworks.negotiation.contracts.ContractTransition;
import frameworks.negotiation.contracts.ContractTrunk;
import frameworks.negotiation.contracts.MatchingCandidature;
import frameworks.negotiation.contracts.ResourceIdentifier;
import frameworks.negotiation.protocoles.AbstractCommunicationProtocol;
import frameworks.negotiation.rationality.AgentState;

public class StatusProtocolArchive<
PersonalState extends AgentState,
Contract extends MatchingCandidature>
extends AbstractCommunicationProtocol<PersonalState,Contract>{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1590563865576327573L;

	/**
	 * 
	 * @param contracts
	 * @param optimizeDestruction null if not a ressource ; if false, all destruction demand will be automatically confirmed
	 * @throws UnrespectedCompetenceSyntaxException
	 */
	public StatusProtocolArchive()
			throws UnrespectedCompetenceSyntaxException {
		super(new ContractTrunk<Contract>());
	}

	/*
	 * 
	 */
	@Override
	protected void answer() {
		if (this.isActive() && !this.getContracts().isEmpty()) {

			assert this.canonicVerif();
	
			assert this.canonicVerif();
			super.answer();
			assert this.canonicVerif();
		}
	}


	@Override
	public boolean ImAllowedToNegotiate(final ContractTrunk<Contract> contracts) {
		return  contracts.getAllInitiatorContracts().isEmpty();
	}

	/*
	 * 
	 */

	@Override
	protected void answerAccepted(final Collection<Contract> toAccept) {
		final ArrayList<Contract> initiator = new ArrayList<Contract>();
		final ArrayList<Contract> participant = new ArrayList<Contract>();

		this.separateInitiator(toAccept, initiator, participant);

		assert AbstractCommunicationProtocol.allRequestable(initiator, this.getContracts());

		
		
		assert ContractTransition.allComplete(initiator);
		assert ContractTransition.allComplete(participant);
		assert Assert.Imply(!initiator.isEmpty(),participant.isEmpty()):initiator+"\n -----------------------"+participant;
		assert Assert.Imply(!participant.isEmpty(), initiator.isEmpty()):initiator+"\n -----------------------"+participant;
		assert Assert.Imply(!participant.isEmpty(),this.getMyAgent().getIdentifier() instanceof ResourceIdentifier);
		assert Assert.Imply(!initiator.isEmpty(),!(this.getMyAgent().getIdentifier() instanceof ResourceIdentifier));

		this.confirmContract(initiator, Receivers.NotInitiatingParticipant);
		this.acceptContract(participant, Receivers.Initiator);
	}


	@Override
	protected void answerRejected(final Collection<Contract> toReject) {
		final ArrayList<Contract> initiator = new ArrayList<Contract>();
		final ArrayList<Contract> participant = new ArrayList<Contract>();

		//
		//		Collection<Contract> extractedContracts = new ArrayList<Contract>();
		//		if (optimizeDestruction!=null && !optimizeDestruction){
		//			assert getMyAgent().getIdentifier() instanceof ResourceIdentifier;
		//			Iterator<Contract> itRej = toReject.iterator();
		//			while (itRej.hasNext()){
		//				Contract n = itRej.next();
		//				if (!n.isMatchingCreation()){
		//					extractedContracts.add(n);
		//					itRej.remove();
		//				}
		//			}
		//		}


		this.separateInitiator(toReject, initiator, participant);

		assert ContractTransition.allComplete(initiator);
		assert ContractTransition.allComplete(participant);
		assert Assert.Imply(!initiator.isEmpty(),participant.isEmpty()):initiator+"\n -----------------------"+participant;
		assert Assert.Imply(!participant.isEmpty(), initiator.isEmpty()):initiator+"\n -----------------------"+participant;
		assert Assert.Imply(!participant.isEmpty(),this.getMyAgent().getIdentifier() instanceof ResourceIdentifier);
		assert Assert.Imply(!initiator.isEmpty(),!(this.getMyAgent().getIdentifier() instanceof ResourceIdentifier));

		this.cancelContract(initiator, Receivers.NotInitiatingParticipant);
		this.cancelContract(participant, Receivers.Initiator);

		//		assert Assert.Imply(!extractedContracts.isEmpty(), optimizeDestruction!=null && !optimizeDestruction);
		//		this.confirmContract(extractedContracts, Receivers.Initiator);
	}

	@Override
	protected void putOnWait(final Collection<Contract> toPutOnWait) {
		// TODO Auto-generated method stub

	}

	public boolean canonicVerif(){
		for (final Contract n:this.getContracts().getAllContracts()){
			for (final AgentIdentifier id : n.getAllInvolved()) {
				if (!id.equals(this.getMyAgent().getIdentifier())) {
					assert this.getContracts().getContracts(id).size()==1:id+" -->\n"+this.getContracts().getContracts(id);
				}
			}
		}
		return true;
	}
}


//
//	@Override
//	protected void answerRejected(final Collection<Contract> toReject) {
//
//		this.cancelContract(toReject, Receivers.EveryParticipant);
//	}
//
//	@Override
//	protected void putOnWait(final Collection<Contract> toPutOnWait) {
//		// Do nothing
//	}
//








//
//	public void receiveProposal(final SimpleContractProposal delta)  {
//		super.receiveProposal(delta);
////		if (optimizeDestruction!=null && !optimizeDestruction && !delta.getMyContract().isMatchingCreation()){
////			Collection<Contract> cs = new ArrayList<Contract>();
////			cs.add(delta.getMyContract());
////			acceptContract(cs, Receivers.Initiator);
////		}
//	}
//
//	public void receiveAccept(final SimpleContractAnswer delta)  {
//		super.receiveAccept(delta);
//
//		Contract contract;
//		try {
//			contract = this.getContracts().getContract(delta.getIdentifier());
//		} catch (final UnknownContractException e) {
//			this.faceAnUnknownContract(e);
//			return;
//		}
//
//		if (!contract.isMatchingCreation()){
//			Collection<Contract> cs = new ArrayList<Contract>();
//			cs.add(contract);
//			confirmContract(cs, Receivers.NotInitiatingParticipant);
//
//		}
//	}
