package negotiation.negotiationframework.protocoles.collaborative;

import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;

import negotiation.negotiationframework.SimpleNegotiatingAgent;
import negotiation.negotiationframework.contracts.AbstractActionSpecification;
import negotiation.negotiationframework.contracts.AbstractContractTransition.IncompleteContractException;
import negotiation.negotiationframework.contracts.ContractTrunk;
import negotiation.negotiationframework.contracts.InformedCandidature;
import negotiation.negotiationframework.contracts.MatchingCandidature;
import negotiation.negotiationframework.protocoles.AbstractCommunicationProtocol.SelectionCore;
import dima.introspectionbasedagents.services.BasicAgentCompetence;

public class AgentInformedSelectionCore  <
ActionSpec extends AbstractActionSpecification,
PersonalState extends ActionSpec,
Contract extends MatchingCandidature<ActionSpec>>
extends
BasicAgentCompetence<SimpleNegotiatingAgent<ActionSpec, PersonalState, InformedCandidature<Contract,ActionSpec>>>
implements SelectionCore<
SimpleNegotiatingAgent<ActionSpec, PersonalState, InformedCandidature<Contract,ActionSpec>>,
ActionSpec, PersonalState, InformedCandidature<Contract,ActionSpec>>{
	private static final long serialVersionUID = -9125593876913236812L;

	@Override
	public void select(
			final ContractTrunk<InformedCandidature<Contract, ActionSpec>, ActionSpec, PersonalState> contracts,
			final Collection<InformedCandidature<Contract, ActionSpec>> toAccept,
			final Collection<InformedCandidature<Contract, ActionSpec>> toReject,
			final Collection<InformedCandidature<Contract, ActionSpec>> toPutOnWait) {

		// Intitiation de l'état
		assert this.getMyAgent().getMyCurrentState().isValid():
			"what the  (1)!!!!!!"+ this.getMyAgent().getMyCurrentState();
		assert MatchingCandidature.assertAllDestruction(contracts.getParticipantAlreadyAcceptedContracts());
		PersonalState currentState = this.getMyAgent()
				.getMyResultingState(
						this.getMyAgent().getMyCurrentState(),
						contracts.getParticipantAlreadyAcceptedContracts());
		// Verification de la consistance
		assert currentState.isValid():"what the  (2)!!!!!!"+ this.getMyAgent().getMyCurrentState();


		//interesting contracts
		final LinkedList<InformedCandidature<Contract,ActionSpec>> allContracts =
				new LinkedList<InformedCandidature<Contract,ActionSpec>>(contracts.getAllContracts());
		allContracts.removeAll(contracts.getAllInitiatorContracts());
		allContracts.removeAll(contracts.getParticipantAlreadyAcceptedContracts());

		toPutOnWait.addAll(contracts.getAllInitiatorContracts());
		toPutOnWait.addAll(contracts.getParticipantAlreadyAcceptedContracts());

		assert MatchingCandidature.assertAllDestruction(allContracts);

		if (!allContracts.isEmpty()){
			toReject.addAll(allContracts);

			final Iterator<InformedCandidature<Contract,ActionSpec>> itContracts = allContracts.iterator();

			Collections.sort(allContracts, this.getMyAgent().getMyPreferenceComparator());
			InformedCandidature<Contract,ActionSpec> c = allContracts.pop();

			try {
				while (c.computeResultingState(currentState).isValid()){
					//					assert getMyAgent().Iaccept(currentState, c);
					toAccept.add(c);
					currentState = c.computeResultingState(currentState);
					if (allContracts.isEmpty()) {
						break;
					} else {
						c = allContracts.pop();
					}
				}
			} catch (final IncompleteContractException e) {
				throw new RuntimeException();
			}
		}

		toReject.removeAll(toAccept);


		assert this.validityVerification(contracts, toAccept, toReject);



	}

	private boolean validityVerification(
			final ContractTrunk<InformedCandidature<Contract, ActionSpec>, ActionSpec, PersonalState> given,
			final Collection<InformedCandidature<Contract, ActionSpec>> accepted,
			final Collection<InformedCandidature<Contract, ActionSpec>> rejected) {

		//toute creation est accepté
		for (final InformedCandidature<Contract, ActionSpec> c : given.getAllContracts()) {
			if (c.isMatchingCreation()) {
				assert accepted.contains(c) || given.getContractsAcceptedBy(this.getIdentifier()).contains(c);
			}
		}

		//(nécessaire) tout ce qui est rejeté est destructions
		assert MatchingCandidature.assertAllDestruction(rejected);

		return true;
	}
}
