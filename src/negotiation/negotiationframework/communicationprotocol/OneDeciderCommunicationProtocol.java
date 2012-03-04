package negotiation.negotiationframework.communicationprotocol;

import dima.introspectionbasedagents.annotations.StepComposant;
import dima.introspectionbasedagents.services.UnrespectedCompetenceSyntaxException;
import negotiation.faulttolerance.experimentation.ReplicationExperimentationProtocol;
import negotiation.negotiationframework.SimpleNegotiatingAgent;
import negotiation.negotiationframework.contracts.AbstractActionSpecification;
import negotiation.negotiationframework.contracts.AbstractContractTransition;
import negotiation.negotiationframework.contracts.ContractTrunk;

public class OneDeciderCommunicationProtocol <
ActionSpec extends AbstractActionSpecification,
State extends ActionSpec,
Contract extends AbstractContractTransition<ActionSpec>>
extends AbstractCommunicationProtocol<ActionSpec, State, Contract>{

	boolean ImDecider;

	public OneDeciderCommunicationProtocol(
			SimpleNegotiatingAgent<ActionSpec, State, Contract> a,
			ContractTrunk<Contract, ActionSpec, State> contracts,
			boolean iMDecider)
					throws UnrespectedCompetenceSyntaxException {
		super(a, contracts);
		this.ImDecider = iMDecider;
	}

	public OneDeciderCommunicationProtocol(
			ContractTrunk<Contract, ActionSpec, State> contracts,
			boolean iMDecider)
					throws UnrespectedCompetenceSyntaxException {
		super(contracts);
		this.ImDecider = iMDecider;
	}
	
	// @role(NegotiationParticipant.class)
	@StepComposant(ticker = ReplicationExperimentationProtocol._timeToCollect)
	void answer() {
		if (isActive())
			if (!getContracts().isEmpty()) {

				//
				// Selecting contracts
				//

				// logMonologue("What do I have?"+contracts.getOnWaitContracts());
				final ContractTrunk<Contract, ActionSpec, State> selectedContracts = this
						.getMyAgent().getMySelectionCore().select(this.getContracts());

				//
				// Answering
				//
				for (final Contract contract : selectedContracts.getContractsAcceptedBy(this.getMyAgent().getIdentifier())){
					if (ImDecider){
						getContracts().addAcceptation(this.getMyAgent().getIdentifier(),
								contract);
						this.requestContract(contract);
					}else{
						this.acceptContract(contract);
					}
				}

				for (final Contract contract : selectedContracts.getContractsRejectedBy(this.getMyAgent().getIdentifier())){
					if (ImDecider)
						this.cancelContract(contract);
					else
						this.rejectContract(contract);
				}
			}
	}
}
