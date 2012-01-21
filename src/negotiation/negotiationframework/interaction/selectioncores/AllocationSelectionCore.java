package negotiation.negotiationframework.interaction.selectioncores;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import negotiation.negotiationframework.interaction.AbstractActionSpecification;
import negotiation.negotiationframework.interaction.AbstractContractTransition;

public class AllocationSelectionCore<
ActionSpec extends AbstractActionSpecification,
PersonalState extends ActionSpec,
Contract extends AbstractContractTransition<ActionSpec>> extends
AbstractSelectionCore<ActionSpec, PersonalState, Contract> {

	/**
	 *
	 */
	private static final long serialVersionUID = -6647203390126730926L;


	public AllocationSelectionCore(final boolean fuseInitiatorNparticipant,
			final boolean considerOnWait) {
		super(fuseInitiatorNparticipant, considerOnWait);
	}

	@Override
	protected Collection<Contract> selection(
			final PersonalState currentState,
			final List<Contract> contractsToExplore) {
		final Collection<Collection<Contract>> allocations = this.generateAllocations(currentState,contractsToExplore);
		if (allocations.isEmpty())
			return new ArrayList<Contract>();
		else
			try {
				return Collections.max(
						allocations,
						this.getMyAgent().getMyAllocationPreferenceComparator(currentState));
			} catch (final RuntimeException e) {
				this.getMyAgent().signalException(
						"my state "+currentState+", contracts "+contractsToExplore);
				throw e;
			}
	}


	//
	// Primitive
	//

	private Collection<Collection<Contract>> generateAllocations(
			final PersonalState currentState,
			final Collection<Contract> contractToAggregate) {

		final Collection<Collection<Contract>> result =
				new ArrayList<Collection<Contract>>();
		final Collection<Collection<Contract>> toAdd =
				new ArrayList<Collection<Contract>>();

		for (final Contract singleton : contractToAggregate) {
			final List<Contract> a = new ArrayList<Contract>();
			a.add(singleton);
			toAdd.add(a);

			for (final Collection<Contract> alloc : result){
				final List<Contract> a2= new ArrayList<Contract>();
				a2.addAll(alloc);
				a2.add(singleton);
				toAdd.add(a2);
			}

			result.addAll(toAdd);
			toAdd.clear();
		}

		this.cleanContracts(currentState,result);
		//		logMonologue("allocations générée for "+getMyAgent().getMyCurrentState()+" from "+contractToAggregate+"\n : "+result);
		return result;
	}


	protected void cleanContracts(
			final PersonalState currentState,
			final Collection<Collection<Contract>> allocationsToExplore) {
		// Removing forbidden allocations
		final Iterator<Collection<Contract>> r = allocationsToExplore.iterator();
		while (r.hasNext())
			if (!this.getMyAgent().respectMyRights(
					currentState,
					r.next()))
				r.remove();
	}
}
