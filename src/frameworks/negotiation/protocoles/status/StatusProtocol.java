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

public class StatusProtocol<
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
	public StatusProtocol()
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
		assert ContractTransition.allInitiator(toAccept, getMyAgent().getIdentifier());	
		assert ContractTransition.allComplete(toAccept);
//		assert AbstractCommunicationProtocol.allRequestable(toAccept, this.getContracts());	

		this.confirmContract(toAccept, Receivers.NotInitiatingParticipant);
	}


	@Override
	protected void answerRejected(final Collection<Contract> toReject) {
		assert ContractTransition.allInitiator(toReject, getMyAgent().getIdentifier());	
		assert ContractTransition.allComplete(toReject);
//		assert AbstractCommunicationProtocol.allRequestable(toReject, this.getContracts());	
		this.cancelContract(toReject, Receivers.Initiator);
	}

	@Override
	protected void putOnWait(final Collection<Contract> toPutOnWait) {
		throw new RuntimeException();
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
