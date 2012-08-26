package frameworks.negotiation.protocoles.collaborative;

import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;

import dima.introspectionbasedagents.services.BasicAgentCompetence;
import frameworks.negotiation.NegotiatingAgent;
import frameworks.negotiation.contracts.AbstractActionSpecif;
import frameworks.negotiation.contracts.ContractTrunk;
import frameworks.negotiation.contracts.MatchingCandidature;
import frameworks.negotiation.contracts.AbstractContractTransition.IncompleteContractException;
import frameworks.negotiation.protocoles.AbstractCommunicationProtocol.SelectionCore;
import frameworks.negotiation.rationality.AgentState;

public class AgentInformedSelectionCore  <
ActionSpec extends AbstractActionSpecif,
PersonalState extends AgentState,
Contract extends MatchingCandidature>
extends
BasicAgentCompetence<NegotiatingAgent<PersonalState, InformedCandidature<Contract>>>
implements SelectionCore<
NegotiatingAgent<PersonalState, InformedCandidature<Contract>>,
PersonalState,
InformedCandidature<Contract>>{
	private static final long serialVersionUID = -9125593876913236812L;

	@Override
	public void select(
			final ContractTrunk<InformedCandidature<Contract>> contracts,
			PersonalState currentState,
			final Collection<InformedCandidature<Contract>> toAccept,
			final Collection<InformedCandidature<Contract>> toReject,
			final Collection<InformedCandidature<Contract>> toPutOnWait) {

		// Intitiation de l'état
		assert currentState.isValid():
			"what the  (1)!!!!!!"+ this.getMyAgent().getMyCurrentState();
		assert MatchingCandidature.assertAllDestruction(contracts.getParticipantAlreadyAcceptedContracts());
		currentState = this.getMyAgent()
				.getMyResultingState(
						this.getMyAgent().getMyCurrentState(),
						contracts.getParticipantAlreadyAcceptedContracts());
		// Verification de la consistance
		assert currentState.isValid():"what the  (2)!!!!!!"+ this.getMyAgent().getMyCurrentState();


		//interesting contracts
		final LinkedList<InformedCandidature<Contract>> allContracts =
				new LinkedList<InformedCandidature<Contract>>(contracts.getAllContracts());
		allContracts.removeAll(contracts.getAllInitiatorContracts());
		allContracts.removeAll(contracts.getParticipantAlreadyAcceptedContracts());

		toPutOnWait.addAll(contracts.getAllInitiatorContracts());
		toPutOnWait.addAll(contracts.getParticipantAlreadyAcceptedContracts());

		assert MatchingCandidature.assertAllDestruction(allContracts);

		if (!allContracts.isEmpty()){
			toReject.addAll(allContracts);

			final Iterator<InformedCandidature<Contract>> itContracts = allContracts.iterator();

			Collections.sort(allContracts, this.getMyAgent().getMyPreferenceComparator());
			InformedCandidature<Contract> c = allContracts.pop();

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
			final ContractTrunk<InformedCandidature<Contract>> given,
			final Collection<InformedCandidature<Contract>> accepted,
			final Collection<InformedCandidature<Contract>> rejected) {

		//toute creation est accepté
		for (final InformedCandidature<Contract> c : given.getAllContracts()) {
			if (c.isMatchingCreation()) {
				assert accepted.contains(c) || given.getContractsAcceptedBy(this.getIdentifier()).contains(c);
			}
		}

		//(nécessaire) tout ce qui est rejeté est destructions
		assert MatchingCandidature.assertAllDestruction(rejected);

		return true;
	}
}
