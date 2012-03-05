package negotiation.negotiationframework.protocoles.collaborative;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import dima.basicagentcomponents.AgentIdentifier;
import dima.introspectionbasedagents.annotations.StepComposant;
import dima.introspectionbasedagents.ontologies.FIPAACLOntologie.Performative;
import dima.introspectionbasedagents.services.UnrespectedCompetenceSyntaxException;
import negotiation.faulttolerance.experimentation.ReplicationExperimentationProtocol;
import negotiation.negotiationframework.AbstractCommunicationProtocol;
import negotiation.negotiationframework.SimpleNegotiatingAgent;
import negotiation.negotiationframework.contracts.AbstractActionSpecification;
import negotiation.negotiationframework.contracts.AbstractContractTransition;
import negotiation.negotiationframework.contracts.AbstractContractTransition.IncompleteContractException;
import negotiation.negotiationframework.contracts.ContractIdentifier;
import negotiation.negotiationframework.contracts.ContractTrunk;
import negotiation.negotiationframework.contracts.UnknownContractException;

public class OneDeciderCommunicationProtocol <
ActionSpec extends AbstractActionSpecification,
State extends ActionSpec,
Contract extends InformedCandidature<Contract,ActionSpec>>
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

	public boolean negotiationAsInitiatorHasStarted() {

		if (ImDecider)
			return false;
		else
			return super.negotiationAsInitiatorHasStarted();		
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
				if (ImDecider){
					//ACCEPTATION
					Collection<Contract> accepteds =  selectedContracts.getContractsAcceptedBy(this.getMyAgent().getIdentifier());
					Iterator<Contract> itCon = accepteds.iterator();
					while (itCon.hasNext()){
						Contract a = itCon.next();
						if (!a.isMatchingCreation()){
//							a.setSpecification(getMyAgent().getMyCurrentState());
							this.confirm(a);
							itCon.remove();
						}
					}
					for (Contract c : accepteds){
						c.setSpecification(getMyAgent().getMyCurrentState());
						this.confirm(c);
					}

					//REFUS
					for (final Contract contract : selectedContracts.getContractsRejectedBy(this.getMyAgent().getIdentifier())){
						this.cancelContract(contract);
					}						
				} else {	
					for (final Contract contract :selectedContracts.getContractsAcceptedBy(this.getMyAgent().getIdentifier())){
						this.acceptContract(contract);
					}
					for (final Contract contract : selectedContracts.getContractsRejectedBy(this.getMyAgent().getIdentifier())){
						this.rejectContract(contract);
					}
				}
			}
	}

	// @role(NegotiationInitiatorRole.class)
	protected void confirm(final Contract contract) {
//		try {
//			assert contract.isViable():contract;
//		} catch (IncompleteContractException e) {
//			getMyAgent().signalException("impossible");
//		}
		this.getContracts().addAcceptation(this.getMyAgent().getIdentifier(),contract);
		assert this.getContracts().getRequestableContracts().contains(contract):contract;

		this.getMyAgent().logMonologue("**************> I request!"+contract.getIdentifier()+" --> "
				+getContracts().statusOf(contract)+"\n"+this.getMyAgent().getMyCurrentState(),AbstractCommunicationProtocol.log_negotiationStep);


		Collection<AgentIdentifier> participant = new ArrayList<AgentIdentifier>();
		participant.addAll(contract.getAllParticipants());
		participant.remove(this.getIdentifier());

		final SimpleContractAnswer request = new SimpleContractAnswer(
				Performative.Request, contract.getIdentifier(), getMyAgent().getMySpecif(contract));
		this.sendMessage(participant, request);

		this.getMyAgent().execute(contract);
		this.getContracts().remove(contract);
	}
}
