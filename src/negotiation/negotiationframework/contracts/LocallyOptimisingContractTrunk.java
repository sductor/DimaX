package negotiation.negotiationframework.contracts;

import java.util.Collection;

import dima.basicagentcomponents.AgentIdentifier;
import dimaxx.tools.mappedcollections.HashedHashSet;

public class LocallyOptimisingContractTrunk
<Contract extends MatchingCandidature<ActionSpec>,
ActionSpec extends AbstractActionSpecification>
extends ContractTrunk<Contract>{

	//
	// Fields
	//

	ContractTrunk<ReallocationContract<Contract, ActionSpec>> myLocalOptimisations;
	HashedHashSet<Contract, ReallocationContract<Contract, ActionSpec>> possibleOptimisations=
			new HashedHashSet<Contract, ReallocationContract<Contract,ActionSpec>>();

	//
	// Constructor
	//

	public LocallyOptimisingContractTrunk(AgentIdentifier myAgentIdentifier) {
		super(myAgentIdentifier);
		myLocalOptimisations = new ContractTrunk<ReallocationContract<Contract,ActionSpec>>(myAgentIdentifier);
	}

	//
	// Methods
	//

	public void link(Collection<Contract> cs){
		ReallocationContract<Contract, ActionSpec> realloc = 
				new ReallocationContract<Contract, ActionSpec>(getMyAgentIdentifier(), cs);
		myLocalOptimisations.addContract(realloc);
		for (Contract c : cs){
			possibleOptimisations.add(c, realloc);
			for (AgentIdentifier p : c.getAllParticipants()){
				if (getContractsAcceptedBy(p).contains(c))
					myLocalOptimisations.addAcceptation(p, realloc);
			}
		}
	}


	public void addAcceptation(final AgentIdentifier id, final Contract c) {
		if (!possibleOptimisations.containsKey(c))
			super.addAcceptation(id,c);
		else{
			for (ReallocationContract<Contract, ActionSpec> realloc :possibleOptimisations.get(c)){
				myLocalOptimisations.addAcceptation(id, realloc);
			}
			/**/
			if (this.isRequestable(c)) {
				for (ReallocationContract<Contract, ActionSpec> realloc :possibleOptimisations.get(c)){
					if (myLocalOptimisations.getConsensualContracts().contains(realloc))
						for(Contract c2 : realloc){
							if (isRequestable(c2)){
								this.waitContracts.remove(c2);
								this.consensualContracts.add(c2);
							}
						}
				}
			}
		}
	}

	public void addRejection(final AgentIdentifier id, final Contract c) {
		if (!possibleOptimisations.containsKey(c))
			super.addAcceptation(id,c);
		else{
			for (ReallocationContract<Contract, ActionSpec> realloc :possibleOptimisations.get(c)){
				myLocalOptimisations.addRejection(id, realloc);
				if (myLocalOptimisations.isAFailure(realloc)){
					for(Contract c2 : realloc){
						possibleOptimisations.get(c2).remove(realloc);
						if (this.isAFailure(c)){
							this.waitContracts.remove(c);
							this.rejectedContracts.add(id, c);
						}
					}
				}
			}
		}
	}

	/*
	 * 
	 */

	protected boolean isRequestable(final Contract c) {
		if (!possibleOptimisations.containsKey(c))
			return super.isRequestable(c);
		else{
			for (ReallocationContract<Contract, ActionSpec> realloc :possibleOptimisations.get(c)){
				if (myLocalOptimisations.getConsensualContracts().contains(realloc))
					return true;
			}
			return false;
		}
	}
	protected boolean isAFailure(final Contract c) {
		if (!possibleOptimisations.containsKey(c))
			return super.isRequestable(c);
		else{
			if (possibleOptimisations.get(c).isEmpty()){
				possibleOptimisations.remove(c);
				return true;
			} else
				return false;
		}
	}
}
