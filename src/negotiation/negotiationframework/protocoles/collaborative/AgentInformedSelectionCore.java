package negotiation.negotiationframework.protocoles.collaborative;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import negotiation.negotiationframework.SimpleNegotiatingAgent;
import negotiation.negotiationframework.AbstractCommunicationProtocol.SelectionCore;
import negotiation.negotiationframework.contracts.AbstractActionSpecification;
import negotiation.negotiationframework.contracts.AbstractContractTransition.IncompleteContractException;
import negotiation.negotiationframework.contracts.ContractTrunk;
import negotiation.negotiationframework.contracts.MatchingCandidature;
import dima.introspectionbasedagents.services.BasicAgentCompetence;

public class AgentInformedSelectionCore <
ActionSpec extends AbstractActionSpecification,
PersonalState extends ActionSpec,
Contract extends MatchingCandidature<ActionSpec>>
extends
BasicAgentCompetence<SimpleNegotiatingAgent<ActionSpec, PersonalState, InformedCandidature<Contract,ActionSpec>>>
implements SelectionCore<ActionSpec, PersonalState, InformedCandidature<Contract,ActionSpec>> {

	@Override
	public ContractTrunk<InformedCandidature<Contract, ActionSpec>, ActionSpec, PersonalState> select(
			ContractTrunk<InformedCandidature<Contract, ActionSpec>, ActionSpec, PersonalState> contracts) {

		// Intitiation de l'état
		assert this.getMyAgent().getMyCurrentState().isValid():
			"what the  (1)!!!!!!"+ this.getMyAgent().getMyCurrentState();
		assert allDestruction(contracts.getParticipantAlreadyAcceptedContracts());		
		PersonalState currentState = this.getMyAgent()
				.getMyResultingState(
						this.getMyAgent().getMyCurrentState(),
						contracts.getParticipantAlreadyAcceptedContracts());
		// Verification de la consistance
		assert currentState.isValid():"what the  (2)!!!!!!"+ this.getMyAgent().getMyCurrentState();

		
		//contract lists		
		Collection<InformedCandidature<Contract, ActionSpec>> accepted = 
				new HashSet<InformedCandidature<Contract, ActionSpec>>();
		Collection<InformedCandidature<Contract, ActionSpec>> rejected = 
				new HashSet<InformedCandidature<Contract, ActionSpec>>();

		//interesting contracts
		LinkedList<InformedCandidature<Contract,ActionSpec>> allContracts = 
				new LinkedList<InformedCandidature<Contract,ActionSpec>>(contracts.getAllContracts());
		allContracts.removeAll(contracts.getAllInitiatorContracts());
		allContracts.removeAll(contracts.getParticipantAlreadyAcceptedContracts());	
		
		assert allDestruction(allContracts);

		if (!allContracts.isEmpty()){
			rejected.addAll(allContracts);	
			
			Iterator<InformedCandidature<Contract,ActionSpec>> itContracts = allContracts.iterator();
			
			Collections.sort(allContracts, getMyAgent().getMyPreferenceComparator());			
			InformedCandidature<Contract,ActionSpec> c = allContracts.pop();
			
			try {
				while (c.computeResultingState(currentState).isValid()){
//					assert getMyAgent().Iaccept(currentState, c);
					accepted.add(c);
					currentState = c.computeResultingState(currentState);
					if (allContracts.isEmpty())
						break;
					else
						c = allContracts.pop();
				}
			} catch (IncompleteContractException e) {
				throw new RuntimeException();
			}
		}

		rejected.removeAll(accepted);

		/*
		 * Instanciating returned contract trunk 		
		 */

		ContractTrunk<InformedCandidature<Contract,ActionSpec>, ActionSpec, PersonalState> returned = 
				new ContractTrunk<InformedCandidature<Contract,ActionSpec>, ActionSpec, PersonalState>(getMyAgent());

		assert validityVerification(contracts, accepted, rejected);

		// ACCEPTATION
		for (final InformedCandidature<Contract, ActionSpec> c : accepted) {
			returned.addContract(c);
			returned.addAcceptation(this.getMyAgent().getIdentifier(), c);
		}

		// REFUS
		for (final InformedCandidature<Contract, ActionSpec> c : rejected) {
			returned.addContract(c);
			returned.addRejection(this.getMyAgent().getIdentifier(), c);
		}

		return returned;

	}

	private boolean validityVerification(
			ContractTrunk<InformedCandidature<Contract, ActionSpec>, ActionSpec, PersonalState> given,
			Collection<InformedCandidature<Contract, ActionSpec>> accepted,
			Collection<InformedCandidature<Contract, ActionSpec>> rejected) {

		//accepted et rejected sont disjoint
		for (InformedCandidature<Contract, ActionSpec> c : accepted){
			assert (!rejected.contains(c));
		}
		for (InformedCandidature<Contract, ActionSpec> c : rejected){
			assert (!accepted.contains(c));
		}
		
		//toute creation est accepté
		for (InformedCandidature<Contract, ActionSpec> c : given.getAllContracts()){
			if (c.isMatchingCreation())
				assert (accepted.contains(c) || given.getContractsAcceptedBy(getIdentifier()).contains(c));
		}

		//(nécessaire) tout ce qui est rejeté est destructions
		assert (allDestruction(rejected));

		return true;
	}
	
	private boolean allDestruction(Collection<InformedCandidature<Contract, ActionSpec>> contracts){
		for (InformedCandidature<Contract, ActionSpec> c : contracts){
			if (c.getCandidature().isMatchingCreation())
				return false;
		}
		return true;
	}
}
