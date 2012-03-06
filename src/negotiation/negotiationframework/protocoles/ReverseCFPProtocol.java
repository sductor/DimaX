package negotiation.negotiationframework.protocoles;

import dima.introspectionbasedagents.annotations.StepComposant;
import dima.introspectionbasedagents.services.UnrespectedCompetenceSyntaxException;
import negotiation.faulttolerance.experimentation.ReplicationExperimentationProtocol;
import negotiation.negotiationframework.SimpleNegotiatingAgent;
import negotiation.negotiationframework.contracts.AbstractActionSpecification;
import negotiation.negotiationframework.contracts.AbstractContractTransition;
import negotiation.negotiationframework.contracts.ContractTrunk;

public class ReverseCFPProtocol <
ActionSpec extends AbstractActionSpecification,
State extends ActionSpec,
Contract extends AbstractContractTransition<ActionSpec>>
extends AbstractCommunicationProtocol<ActionSpec, State, Contract>{
	
	public ReverseCFPProtocol(
			SimpleNegotiatingAgent<ActionSpec, State, Contract> a,
			ContractTrunk<Contract, ActionSpec, State> contracts)
			throws UnrespectedCompetenceSyntaxException {
		super(a, contracts);
	}

	// @role(NegotiationParticipant.class)
	@StepComposant(ticker = ReplicationExperimentationProtocol._timeToCollect)
	void answer() {
		if (isActive())
			if (!this.getContracts().isEmpty()) {

				//
				// Selecting contracts
				//

				// logMonologue("What do I have?"+contracts.getOnWaitContracts());
				final ContractTrunk<Contract, ActionSpec, State> selectedContracts = this
						.getMyAgent().getMySelectionCore().select(this.getContracts());

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
						if (this.getContracts().getRequestableContracts().contains(contract)){//if the contract is consensual
							this.requestContract(contract);
						}
					} else
						this.acceptContract(contract);
				for (final Contract contract : selectedContracts.getContractsRejectedBy(this.getMyAgent().getIdentifier()))
					if (contract.getInitiator().equals(this.getMyAgent().getIdentifier())){
						this.cancelContract(contract);
					}else{// Participant Answering
						this.rejectContract(contract);
					}
			}
	}

	
}
