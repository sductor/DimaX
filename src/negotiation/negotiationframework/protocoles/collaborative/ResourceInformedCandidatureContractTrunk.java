package negotiation.negotiationframework.protocoles.collaborative;

import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;

import negotiation.negotiationframework.contracts.AbstractActionSpecification;
import negotiation.negotiationframework.contracts.ContractIdentifier;
import negotiation.negotiationframework.contracts.ContractTrunk;
import negotiation.negotiationframework.contracts.MatchingCandidature;
import negotiation.negotiationframework.contracts.ReallocationContract;
import negotiation.negotiationframework.contracts.UnknownContractException;
import dima.basicagentcomponents.AgentIdentifier;

public class ResourceInformedCandidatureContractTrunk<
Contract extends MatchingCandidature<ActionSpec>,
ActionSpec extends AbstractActionSpecification>
extends ContractTrunk<InformedCandidature<Contract, ActionSpec>>{
	private static final long serialVersionUID = -5058077493662331641L;

	/**
	 * 
	 */

	private final ContractTrunk<ReallocationContract<Contract, ActionSpec>> myLocalOptimisations;

	/*
	 * 
	 */

	public ResourceInformedCandidatureContractTrunk(final AgentIdentifier myAgentIdentifier) {
		super(myAgentIdentifier);
		this.myLocalOptimisations = new ContractTrunk<ReallocationContract<Contract,ActionSpec>>(myAgentIdentifier);
	}

	//
	// Methods
	//

	
	public Collection<ReallocationContract<Contract, ActionSpec>> getRequestableReallocationContracts(){
		return myLocalOptimisations.getRequestableContracts();
	}

	public void addReallocContract(final ReallocationContract<Contract, ActionSpec> realloc){
		assert this.containsAllKey(realloc.getIdentifiers());
		if (!this.myLocalOptimisations.contains(realloc.getIdentifier()))
			this.myLocalOptimisations.addContract(realloc);
		for (final Contract c : realloc)
			try {
				this.getContract(c.getIdentifier()).getPossibleContracts().add(realloc);
			} catch (final UnknownContractException e) {
				throw new RuntimeException(e);
			}
	}

	//
	// Overrided
	//

	/*
	 *
	 */

	@Override
	public void addContract(final InformedCandidature<Contract, ActionSpec> c) {
		for (final ReallocationContract<Contract, ActionSpec> r : c.getPossibleContracts()){
			this.myLocalOptimisations.addContract(r);
		}
		super.addContract(c);
		for (final ReallocationContract<Contract, ActionSpec> r : c.getPossibleContracts()){
			for (final Contract c2 : r)
				if (!c2.equals(c))
					try {
						this.getContract(c2.getIdentifier()).getPossibleContracts().add(r);
					} catch (final UnknownContractException e) {
						throw new RuntimeException(e);
					}
		}
	}

	@Override
	public void addAcceptation(final AgentIdentifier id,
			final InformedCandidature<Contract, ActionSpec> c) {
		for (final ReallocationContract<Contract, ActionSpec> r : c.getPossibleContracts()){
			this.myLocalOptimisations.addAcceptation(id,r);
		}
		super.addAcceptation(id, c);
	}

	@Override
	public void addRejection(final AgentIdentifier id,
			final InformedCandidature<Contract, ActionSpec> c) {
		for (final ReallocationContract<Contract, ActionSpec> r : c.getPossibleContracts()){
			this.myLocalOptimisations.remove(r);
		}
		super.addRejection(id, c);
	}

	@Override
	public void removeRejection(final AgentIdentifier id, final InformedCandidature<Contract, ActionSpec> c) {	
		for (final ReallocationContract<Contract, ActionSpec> r : c.getPossibleContracts()){
			this.myLocalOptimisations.removeRejection(id,r);
		}
		super.removeRejection(id, c);
	}

	/*
	 * 
	 */

	@Override
	public void remove(final  InformedCandidature<Contract, ActionSpec> c) {
		for (final ReallocationContract<Contract, ActionSpec> r : c.getPossibleContracts()){
			this.myLocalOptimisations.remove(r);
		}
		super.remove(c);
	}


	@Override
	public void clear() {
		super.clear();
		this.myLocalOptimisations.clear();
	}
}



//public ReallocationContract<Contract, ActionSpec> getBestRequestable(
//		final InformedCandidature<Contract, ActionSpec> c,
//		final Comparator<Collection<Contract>> pref){
//	final Iterator<ReallocationContract<Contract, ActionSpec>> itPossible =
//			c.getPossibleContracts().iterator();
//	assert itPossible.hasNext():"initialisé!!";
//	ReallocationContract<Contract, ActionSpec> max = itPossible.next();
//	for (final ReallocationContract<Contract, ActionSpec> r : c.getPossibleContracts()){
//		final ReallocationContract<Contract, ActionSpec> neo = itPossible.next();
//		max = pref.compare(neo, max)>1?neo:max;
//	}
//	return max;
//}
//@Override
//public boolean isRequestable(final InformedCandidature<Contract, ActionSpec> c) {
//	for (final ReallocationContract<Contract, ActionSpec> realloc : c.getPossibleContracts())
//		if (this.myLocalOptimisations.isRequestable(realloc)){
//			return true;
//		}
//	return false;
//}

//@Override
//public boolean isAFailure(final InformedCandidature<Contract, ActionSpec> c) {
//	if (super.isAFailure(c))
//		return true;
//	else {
//		boolean everyOneFailed = true;
//		for (final ReallocationContract<Contract, ActionSpec> realloc : c.getPossibleContracts()){
//			if (!this.myLocalOptimisations.isAFailure(realloc))
//				everyOneFailed=false;
//		}
//		return everyOneFailed;
//	}
//}
//			for (final Contract c2 : r)
//				try {
//					this.getContract(c2.getIdentifier()).getPossibleContracts().remove(r);
//				} catch (UnknownContractException e) {
//					throw new RuntimeException(e.toString()+" \n realloc "+r+"\n base "+this);
//				}