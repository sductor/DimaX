package negotiation.negotiationframework.protocoles.collaborative;

import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;

import negotiation.negotiationframework.contracts.AbstractActionSpecification;
import negotiation.negotiationframework.contracts.ContractTrunk;
import negotiation.negotiationframework.contracts.MatchingCandidature;
import negotiation.negotiationframework.contracts.ReallocationContract;
import negotiation.negotiationframework.contracts.UnknownContractException;
import dima.basicagentcomponents.AgentIdentifier;

public class InformedCandidatureContractTrunk<
Contract extends MatchingCandidature<ActionSpec>,
ActionSpec extends AbstractActionSpecification>
extends ContractTrunk<InformedCandidature<Contract, ActionSpec>>{

	/**
	 * 
	 */
	private static final long serialVersionUID = -5058077493662331641L;
	private final ContractTrunk<ReallocationContract<Contract, ActionSpec>> myLocalOptimisations;

	public InformedCandidatureContractTrunk(final AgentIdentifier myAgentIdentifier) {
		super(myAgentIdentifier);
		this.myLocalOptimisations = new ContractTrunk<ReallocationContract<Contract,ActionSpec>>(myAgentIdentifier);
	}

	//
	// Methods
	//

	public ReallocationContract<Contract, ActionSpec> getBestRequestable(
			final InformedCandidature<Contract, ActionSpec> c,
			final Comparator<Collection<Contract>> pref){
		final Iterator<ReallocationContract<Contract, ActionSpec>> itPossible =
				c.getPossibleContracts().iterator();
		assert itPossible.hasNext():"initialisé!!";
		ReallocationContract<Contract, ActionSpec> max = itPossible.next();
		for (final ReallocationContract<Contract, ActionSpec> r : c.getPossibleContracts()){
			final ReallocationContract<Contract, ActionSpec> neo = itPossible.next();
			max = pref.compare(neo, max)>1?neo:max;
		}
		return max;
	}

	public void addReallocContract(final ReallocationContract<Contract, ActionSpec> realloc){
		this.myLocalOptimisations.addContract(realloc);
		for (final Contract c : realloc)
			try {
				this.getContract(c.getIdentifier()).getPossibleContracts().add(realloc);
			} catch (final UnknownContractException e) {
				throw new RuntimeException(e);
			}
	}

	//
	//
	//

	/*
	 *
	 */

	@Override
	public void addContract(final InformedCandidature<Contract, ActionSpec> c) {
		super.addContract(c);
		for (final ReallocationContract<Contract, ActionSpec> r : c.getPossibleContracts())
			this.myLocalOptimisations.addContract(r);
	}

	@Override
	public void addAcceptation(final AgentIdentifier id,
			final InformedCandidature<Contract, ActionSpec> c) {
		super.addAcceptation(id, c);
		for (final ReallocationContract<Contract, ActionSpec> r : c.getPossibleContracts())
			this.myLocalOptimisations.addAcceptation(id,r);
	}

	@Override
	public void addRejection(final AgentIdentifier id,
			final InformedCandidature<Contract, ActionSpec> c) {
		super.addRejection(id, c);
		for (final ReallocationContract<Contract, ActionSpec> r : c.getPossibleContracts())
			this.myLocalOptimisations.addAcceptation(id,r);
	}

	@Override
	public void clear() {
		super.clear();
		this.myLocalOptimisations.clear();
	}

	/*
	 * 
	 */

	@Override
	public boolean isRequestable(final InformedCandidature<Contract, ActionSpec> c) {
		for (final ReallocationContract<Contract, ActionSpec> realloc : c.getPossibleContracts())
			if (this.myLocalOptimisations.isRequestable(realloc))
				return true;
				return false;
	}

	@Override
	public boolean isAFailure(final InformedCandidature<Contract, ActionSpec> c) {
		for (final ReallocationContract<Contract, ActionSpec> realloc : c.getPossibleContracts())
			if (this.myLocalOptimisations.isAFailure(realloc))
				return true;
				return false;
	}
}
