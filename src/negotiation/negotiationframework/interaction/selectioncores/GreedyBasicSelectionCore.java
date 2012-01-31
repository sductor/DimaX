package negotiation.negotiationframework.interaction.selectioncores;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import negotiation.negotiationframework.interaction.contracts.AbstractActionSpecification;
import negotiation.negotiationframework.interaction.contracts.AbstractContractTransition;

public class GreedyBasicSelectionCore<
ActionSpec extends AbstractActionSpecification,
PersonalState extends ActionSpec,
Contract extends AbstractContractTransition<ActionSpec>>
extends AbstractSelectionCore<ActionSpec, PersonalState, Contract> {
	private static final long serialVersionUID = 438513006322441185L;


	public GreedyBasicSelectionCore(
			final boolean fuseInitiatorNparticipant,
			final boolean considerOnWait) {
		super(fuseInitiatorNparticipant,considerOnWait);
	}

	//
	// Methods
	//

	@Override
	protected Collection<Contract> selection(
			PersonalState currentState,
			final List<Contract> contractsToExplore) {
		// logMonologue("!GreedySelection! : myState"+getMyAgent().getMyCurrentState());
		final Collection<Contract> toValidate = new ArrayList<Contract>();
		if (!contractsToExplore.isEmpty()) {
			this.sortContract(contractsToExplore);
			// logMonologue("analysed contract (1): -->\n"+currentContract+"\n SO? : "
			// +(this.getMyAgent().respectMyRights(currentState) &&
			// this.getMyAgent().Iaccept(currentState, currentContract)));

			while (this.getMyAgent().Iaccept(currentState,
					this.getNextContract(contractsToExplore))) {// this.getMyAgent().respectMyRights(currentState)
				// &&
				final Contract currentContract = this.popNextContract(contractsToExplore);
				toValidate.add(currentContract);
				currentState =
						this.getMyAgent().getMyResultingState(
								currentState,
								currentContract);

				if (contractsToExplore.isEmpty())
					break;

				// logMonologue("!GreedySelection! : myState"+getMyAgent().getMyCurrentState()+"\n analysed contract (2): "+currentContract+"\n SO? : "+(this.getMyAgent().respectMyRights(currentState)
				// &&
				// this.getMyAgent().Iaccept(currentState, currentContract)));
			}

			// Verification de la consistance
			if (!this.getMyAgent().respectMyRights(currentState))
				throw new RuntimeException(
						"what the  (3)!!!!!!\n accepted state : "
								+ currentState);

		}
		return toValidate;
	}

	protected void sortContract(final List<Contract> contracts) {
		Collections.sort(contracts, this.getMyAgent()
				.getMyPreferenceComparator());
	}

	protected Contract popNextContract(final List<Contract> contracts) {
		return contracts.remove(contracts.size() - 1);
	}

	protected Contract getNextContract(final List<Contract> contracts) {
		return contracts.get(contracts.size() - 1);
	}
}

// myAgent.logMonologue(
// "my State : "+currentState+
// "\n** i accept "+currentContract+"? "+
// myAgent.Iaccept(currentState, currentContract)
// +"\n my result state  : "+myAgent.getMyResultingState(
// currentState, currentContract));

// if (!this.myAgent.respectMyRights(
// this.myAgent.getMyResultingState(
// this.myAgent.getMyCurrentState(), result.getAcceptedContracts())))
// this.myAgent.logException("ahah!!"+this.myAgent.getMyResultingState(
// this.myAgent.getMyCurrentState(), result.getAcceptedContracts()));
// return result;

// if (!cs.getUnlabelledContracts().isEmpty() &&
// result.getAcceptedContracts().isEmpty() &&
// cs.getAcceptedContracts().isEmpty())
// myAgent.logMonologue("i do not accept any more contract!"+myAgent.getMyCurrentState());

// //Rejection or wl?
// if (cs.getAcceptedContractsIdentifier().isEmpty())
// //Aucune confirmation en attente on annule tout ce qu l'on ne peut supporter
// for (final Contract c : contracts)
// result.addRejected(c);
// else
// //Des confirmations en attente on met les autres en waiting list :
// for (final Contract c : contracts)
// result.addOnWaitingList(c);
// }
