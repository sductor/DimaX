package negotiation.negotiationframework.protocoles.collaborative;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import com.sun.org.apache.bcel.internal.generic.NEW;

import negotiation.negotiationframework.SimpleNegotiatingAgent;
import negotiation.negotiationframework.communicationprotocol.AbstractCommunicationProtocol;
import negotiation.negotiationframework.contracts.AbstractActionSpecification;
import negotiation.negotiationframework.contracts.ContractIdentifier;
import negotiation.negotiationframework.contracts.ContractTrunk;
import negotiation.negotiationframework.contracts.MatchingCandidature;
import negotiation.negotiationframework.contracts.ReallocationContract;
import negotiation.negotiationframework.contracts.UnknownContractException;
import dima.basicagentcomponents.AgentIdentifier;
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


	public void addReallocContract(final ReallocationContract<Contract, ActionSpec> realloc){
		assert this.containsAllKey(realloc.getIdentifiers()):this+"\n ---> "+realloc;
		for (final Contract c : realloc)
			try {
				this.upgradingContracts.add(getContract(c.getIdentifier()),realloc);
			} catch (final UnknownContractException e) {
				throw new RuntimeException(e);
			}
	}

	/*
	 * 
	 */

	public ReallocationContract<Contract, ActionSpec> getBestReallocationContract(
			InformedCandidature<Contract, ActionSpec> c,
			Comparator<Collection<Contract>> pref){
		LinkedList<ReallocationContract<Contract, ActionSpec>> upCont = 
				new LinkedList<ReallocationContract<Contract, ActionSpec>>(upgradingContracts.get(c));
		if (upCont.isEmpty())
			return null;
		else
			return Collections.max(upCont,pref);
	}

	public ReallocationContract<Contract, ActionSpec> getBestRequestableReallocationContract(
			Comparator<Collection<Contract>> pref){
		LinkedList<ReallocationContract<Contract, ActionSpec>> upCont = 
				new LinkedList<ReallocationContract<Contract, ActionSpec>>(upgradingContracts.getAllValues());
		Collections.sort(upCont,pref);
		while (!upCont.isEmpty())
			if (isRequestable(upCont.getFirst()))
				return upCont.getFirst();
			else
				upCont.pop();
		return null;
	}

	public Collection<ReallocationContract<Contract, ActionSpec>> getReallocationContracts(){
		return upgradingContracts.getAllValues();
	}
	//
	// Primitive
	//

	private boolean isRequestable(ReallocationContract<Contract, ActionSpec> r){
		assert this.containsAllKey(r.getIdentifiers());
		for (Contract c : r){
			assert this.getAllContracts().contains(c);
			try {
				if (!c.isMatchingCreation()){
					if (!isRequestable(this.getContract(c.getIdentifier()))){
//						logMonologue(r + " is not requestable !! =( because of "+c, AbstractCommunicationProtocol.log_selectionStep);
						return false;
					}
				}
//				if (!(!c.isMatchingCreation() || isRequestable(this.getContract(c.getIdentifier())))){
//					logMonologue(r + " is not requestable !! =( because of "+c, AbstractCommunicationProtocol.log_selectionStep);
//					return false;
//				}
			} catch (UnknownContractException e) {
				throw new RuntimeException();
			}
		}
		logMonologue("CONTRACT TRUNK say \n"+r + "\n ----------------------------------- is requestable yoooouhouuu!! =)", AbstractCommunicationProtocol.log_selectionStep);
		return true;
	}	

	//
	// Overrided
	//


	@Override
	public void remove(final  InformedCandidature<Contract, ActionSpec> c) {
		super.remove(c);
		Collection<ReallocationContract<Contract, ActionSpec>> toRemove = 
				new ArrayList<ReallocationContract<Contract,ActionSpec>>();
		toRemove.addAll(upgradingContracts.get(c));
		for (ReallocationContract<Contract, ActionSpec> r : toRemove){
			upgradingContracts.removeAvalue(r);		
		}
	}

	public String toString(){
		return super.toString()+"\n up --> "+upgradingContracts;
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