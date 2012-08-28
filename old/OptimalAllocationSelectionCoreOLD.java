package negotiation.negotiationframework.interaction.selectioncores;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import negotiation.faulttolerance.experimentation.ReplicationExperimentationProtocol;
import negotiation.negotiationframework.agent.AgentState;
import negotiation.negotiationframework.interaction.AbstractActionSpecification;
import negotiation.negotiationframework.interaction.AllocationTransition;
import negotiation.negotiationframework.interaction.MatchingCandidature;
import dima.basicagentcomponents.AgentIdentifier;

public class OptimalAllocationSelectionCoreOLD<PersonalState extends AgentState, Contract extends MatchingCandidature<ActionSpec>, ActionSpec extends AbstractActionSpecification>
		extends GreedySelectionCore<PersonalState, Contract, ActionSpec> {


	public OptimalAllocationSelectionCoreOLD(boolean fuseInitiatorNparticipant,
			boolean considerOnWait) {
		super(fuseInitiatorNparticipant, considerOnWait);
	}

	private static final long serialVersionUID = -7384146367125673909L;

	@Override
	protected Collection<Contract> greedySelection(
			final PersonalState currentState,
			final List<Contract> contractsToExplore) {
		// Generating Allocation
		final List<AllocationTransition<Contract, ActionSpec>> allocationsToExplore = new ArrayList<AllocationTransition<Contract, ActionSpec>>();
		allocationsToExplore.addAll(this.generateAllocations(this.getMyAgent()
				.getId(),
				new ArrayList<AllocationTransition<Contract, ActionSpec>>(),
				contractsToExplore));

		if (!this.getContracts(allocationsToExplore).containsAll(
				contractsToExplore))
			throw new RuntimeException(
					"aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa!!!!!!"
							+ currentState);

		// Cleaning
		final Collection<Contract> cleanedContracts = this.cleanContracts(
				currentState, allocationsToExplore);

		// Sorting
		this.sortAllocation(allocationsToExplore);

		// Determining accepts and rejects
		final Collection<Contract> toValidate = this
				.popNextAllocation(allocationsToExplore);
		contractsToExplore.clear();
		contractsToExplore.addAll(this.getContracts(allocationsToExplore));
		contractsToExplore.addAll(cleanedContracts);
		contractsToExplore.removeAll(toValidate);

		// Verification de la consistance
		final PersonalState s = this.getMyAgent().getMyResultingState(
				currentState, toValidate);
		if (!this.getMyAgent().respectMyRights(s))
			throw new RuntimeException("what the  (4)!!!!!!" + toValidate);

		return toValidate;
	}

	//
	//
	//

	protected Collection<Contract> cleanContracts(
			final PersonalState current,
			final List<AllocationTransition<Contract, ActionSpec>> contractsToExplore) {
		// Removing forbidden allocations
		final Collection<Contract> result = new HashSet<Contract>();
		final Iterator<AllocationTransition<Contract, ActionSpec>> r = contractsToExplore
				.iterator();
		while (r.hasNext()) {
			final AllocationTransition<Contract, ActionSpec> alloc = r.next();
			if (!this.getMyAgent().respectMyRights(current, alloc)) {
				result.addAll(alloc);
				r.remove();
			}
		}
		return result;
	}

	protected void sortAllocation(
			final List<AllocationTransition<Contract, ActionSpec>> contracts) {
		Collections.sort(contracts, this.getMyAgent()
				.getMyAllocationPreferenceComparator());
	}

	protected AllocationTransition<Contract, ActionSpec> popNextAllocation(
			final List<AllocationTransition<Contract, ActionSpec>> contracts) {
		if (!contracts.isEmpty())
			return contracts.remove(contracts.size() - 1);
		else
			return new AllocationTransition<Contract, ActionSpec>(this
					.getMyAgent().getIdentifier(),
					ReplicationExperimentationProtocol._contractExpirationTime);
	}

	//
	// Primitive
	//

	private Set<AllocationTransition<Contract, ActionSpec>> generateAllocations(
			final AgentIdentifier analyser,
			final Collection<AllocationTransition<Contract, ActionSpec>> originalAllocations,
			final Collection<Contract> contractToAggregate) {

		final Set<AllocationTransition<Contract, ActionSpec>> result = new HashSet<AllocationTransition<Contract, ActionSpec>>();
		final Set<AllocationTransition<Contract, ActionSpec>> toAdd = new HashSet<AllocationTransition<Contract, ActionSpec>>();

		result.addAll(originalAllocations);

		for (final Contract singleton : contractToAggregate) {
			toAdd.add(new AllocationTransition<Contract, ActionSpec>(analyser,
					ReplicationExperimentationProtocol._contractExpirationTime,
					singleton));

			for (final AllocationTransition<Contract, ActionSpec> alloc : result)
				toAdd.add(alloc.getNewTransition(singleton));

			result.addAll(toAdd);
			toAdd.clear();
		}

		return result;
	}

	private Collection<Contract> getContracts(
			final Collection<AllocationTransition<Contract, ActionSpec>> as) {
		final Collection<Contract> result = new HashSet<Contract>();
		for (final Collection<Contract> a : as)
			result.addAll(a);
		return result;
	}
}
