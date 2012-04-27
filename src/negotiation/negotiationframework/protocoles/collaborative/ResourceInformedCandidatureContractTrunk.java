package negotiation.negotiationframework.protocoles.collaborative;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;

import negotiation.negotiationframework.SimpleNegotiatingAgent;
import negotiation.negotiationframework.contracts.AbstractActionSpecification;
import negotiation.negotiationframework.contracts.ContractTrunk;
import negotiation.negotiationframework.contracts.InformedCandidature;
import negotiation.negotiationframework.contracts.MatchingCandidature;
import negotiation.negotiationframework.contracts.ReallocationContract;
import negotiation.negotiationframework.contracts.UnknownContractException;
import negotiation.negotiationframework.protocoles.AbstractCommunicationProtocol;
import dimaxx.tools.mappedcollections.HashedHashSet;

public class ResourceInformedCandidatureContractTrunk<
Contract extends MatchingCandidature<ActionSpec>,
ActionSpec extends AbstractActionSpecification,
PersonalState extends ActionSpec>
extends ContractTrunk<InformedCandidature<Contract, ActionSpec>, ActionSpec, PersonalState>{
	private static final long serialVersionUID = -5058077493662331641L;

	/**
	 *
	 */

	//	private final ContractTrunk<ReallocationContract<Contract, ActionSpec>> myLocalOptimisations;
	HashedHashSet<InformedCandidature<Contract, ActionSpec>, ReallocationContract<Contract, ActionSpec>> upgradingContracts=
			new HashedHashSet<InformedCandidature<Contract, ActionSpec>, ReallocationContract<Contract,ActionSpec>>();

	/*
	 *
	 */

	public ResourceInformedCandidatureContractTrunk(
			final SimpleNegotiatingAgent<ActionSpec, PersonalState, InformedCandidature<Contract, ActionSpec>> agent) {
		super(agent);
	}

	public ResourceInformedCandidatureContractTrunk() {
		super();
	}

	//
	// Methods
	//


	@Override
	public Collection<InformedCandidature<Contract, ActionSpec>> getLockedContracts(){
		return upgradingContracts.keySet();
	}
	
	public void addReallocContract(final ReallocationContract<Contract, ActionSpec> realloc){
		assert this.containsAllKey(realloc.getIdentifiers()):this+"\n ---> "+realloc;
		for (final Contract c : realloc) {
			try {
				this.upgradingContracts.add(this.getContract(c.getIdentifier()),realloc);
			} catch (final UnknownContractException e) {
				throw new RuntimeException(e);
			}
		}
	}

	/*
	 *
	 */

	public ReallocationContract<Contract, ActionSpec> getBestReallocationContract(
			final InformedCandidature<Contract, ActionSpec> c,
			final Comparator<Collection<Contract>> pref){
		final LinkedList<ReallocationContract<Contract, ActionSpec>> upCont =
				new LinkedList<ReallocationContract<Contract, ActionSpec>>(this.upgradingContracts.get(c));
		if (upCont.isEmpty()) {
			return null;
		} else {
			return Collections.max(upCont,pref);
		}
	}

	public ReallocationContract<Contract, ActionSpec> getBestRequestableReallocationContract(
			final Comparator<Collection<Contract>> pref){
		final LinkedList<ReallocationContract<Contract, ActionSpec>> upCont =
				new LinkedList<ReallocationContract<Contract, ActionSpec>>(this.upgradingContracts.getAllValues());
		Collections.sort(upCont,pref);
		while (!upCont.isEmpty()) {
			if (this.isRequestable(upCont.getFirst())) {
				return upCont.getFirst();
			} else {
				upCont.pop();
			}
		}
		return null;
	}

	public Collection<ReallocationContract<Contract, ActionSpec>> getReallocationContracts(){
		return this.upgradingContracts.getAllValues();
	}
	//
	// Primitive
	//

	private boolean isRequestable(final ReallocationContract<Contract, ActionSpec> r){
		assert this.containsAllKey(r.getIdentifiers());
		for (final Contract c : r){
			assert this.getAllContracts().contains(c);
			try {
				if (!c.isMatchingCreation()) {
					if (!this.isRequestable(this.getContract(c.getIdentifier()))) {
						//						logMonologue(r + " is not requestable !! =( because of "+c, AbstractCommunicationProtocol.log_selectionStep);
						return false;
					}
				}
			} catch (final UnknownContractException e) {
				throw new RuntimeException();
			}
		}
		this.logMonologue("CONTRACT TRUNK say \n"+r + "\n ----------------------------------- is requestable yoooouhouuu!! =)", AbstractCommunicationProtocol.log_selectionStep);
		return true;
	}

	//
	// Overrided
	//


	@Override
	public void remove(final  InformedCandidature<Contract, ActionSpec> c) {
		super.remove(c);
		final Collection<ReallocationContract<Contract, ActionSpec>> toRemove =
				new ArrayList<ReallocationContract<Contract,ActionSpec>>();
		toRemove.addAll(this.upgradingContracts.get(c));
		for (final ReallocationContract<Contract, ActionSpec> r : toRemove) {
			this.upgradingContracts.removeAvalue(r);
		}
	}

	@Override
	public String toString(){
		return super.toString()+"\n current upgrading contract are : \n "+this.upgradingContracts;
	}
}



//@Override
//public void addContract(final InformedCandidature<Contract, ActionSpec> c){
//	super.addContract(c);
//	if (getMyAgent().Iaccept(getMyAgent().getMyCurrentState(), c)){
////		addAcceptation(getMyAgentIdentifier(), c);
//		this.upgradingContracts.add(c,new ReallocationContract<Contract, ActionSpec>(c.getInitiator(), c.getCandidature()));
//	}
//}


//
//@Override
//public void addRejection(final AgentIdentifier id,
//		final InformedCandidature<Contract, ActionSpec> c) {
//	super.addRejection(id, c);
////	for (ReallocationContract<Contract, ActionSpec> r : upgradingContracts.get(c)){
////		//			System.err.println("yooooooooooo111111111111111111111111111"+r.getIdentifiers());
////		upgradingContracts.removeAvalue(r);
////	}
//}


//public void addReallocContract(final ReallocationContract<Contract, ActionSpec> realloc){
//	assert this.containsAllKey(realloc.getIdentifiers());
//	//		if (!this.myLocalOptimisations.contains(realloc.getIdentifier()))
//	this.myLocalOptimisations.addContract(realloc);
//	for (final Contract c : realloc)
//		try {
//			this.getContract(c.getIdentifier()).getPossibleContracts().add(realloc);
//		} catch (final UnknownContractException e) {
//			throw new RuntimeException(e);
//		}
//}
//	//
//	// Overrided
//	//
//
//	/*
//	 *
//	 */
//
//
//	@Override
//	public void addContract(final InformedCandidature<Contract, ActionSpec> c) {
//
//	}
//
//
//
//
//
//
//
//
//
//
//
//
//
//	@Override
//	public void addContract(final InformedCandidature<Contract, ActionSpec> c) {
//		for (final ReallocationContract<Contract, ActionSpec> r : c.getPossibleContracts()){
//			this.myLocalOptimisations.addContract(r);
//		}
//		super.addContract(c);
//		for (final ReallocationContract<Contract, ActionSpec> r : c.getPossibleContracts()){
//			for (final Contract c2 : r)
//				if (!c2.equals(c))
//					try {
//						this.getContract(c2.getIdentifier()).getPossibleContracts().add(r);
//					} catch (final UnknownContractException e) {
//						throw new RuntimeException(e);
//					}
//		}
//	}
//
//	@Override
//	public void addAcceptation(final AgentIdentifier id,
//			final InformedCandidature<Contract, ActionSpec> c) {
//		for (final ReallocationContract<Contract, ActionSpec> r : c.getPossibleContracts()){
//			this.myLocalOptimisations.addAcceptation(id,r);
//		}
//		super.addAcceptation(id, c);
//	}
//
//	@Override
//	public void addRejection(final AgentIdentifier id,
//			final InformedCandidature<Contract, ActionSpec> c) {
//		for (final ReallocationContract<Contract, ActionSpec> r : c.getPossibleContracts()){
//			this.myLocalOptimisations.remove(r);
//		}
//		super.addRejection(id, c);
//	}
//
//	@Override
//	public void removeRejection(final AgentIdentifier id, final InformedCandidature<Contract, ActionSpec> c) {
//		for (final ReallocationContract<Contract, ActionSpec> r : c.getPossibleContracts()){
//			this.myLocalOptimisations.removeRejection(id,r);
//		}
//		super.removeRejection(id, c);
//	}
//
//	/*
//	 *
//	 */
//
//	@Override
//	public void remove(final  InformedCandidature<Contract, ActionSpec> c) {
//		for (final ReallocationContract<Contract, ActionSpec> r :this.myLocalOptimisations.getAllContracts()){
//			if (r.getAllParticipants().contains(c.getAgent()))
//				this.myLocalOptimisations.remove(r);
//		}
//
//		//		for (final ReallocationContract<Contract, ActionSpec> r : c.getPossibleContracts()){
//		//			this.myLocalOptimisations.remove(r);
//		//		}
//		super.remove(c);
//	}
//
//
//	@Override
//	public void clear() {
//		super.clear();
//		this.myLocalOptimisations.clear();
//	}
//}



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