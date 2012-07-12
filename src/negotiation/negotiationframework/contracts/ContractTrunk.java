package negotiation.negotiationframework.contracts;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import negotiation.negotiationframework.NegotiatingAgent;
import negotiation.negotiationframework.SimpleNegotiatingAgent;
import negotiation.negotiationframework.contracts.AbstractContractTransition.IncompleteContractException;
import negotiation.negotiationframework.protocoles.AtMostCContractSelectioner;
import negotiation.negotiationframework.rationality.AgentState;
import dima.basicagentcomponents.AgentIdentifier;
import dima.introspectionbasedagents.services.BasicAgentModule;
import dimaxx.tools.mappedcollections.HashedHashSet;

public class ContractTrunk<Contract extends AbstractContractTransition>
extends BasicAgentModule<NegotiatingAgent<?, Contract>> {

	/**
	 *
	 */
	private static final long serialVersionUID = -4334790767170677224L;


	private final Map<ContractIdentifier, Contract> identifier2contract =
			new HashMap<ContractIdentifier, Contract>();

	protected final Set<Contract> requestableContracts = new HashSet<Contract>();
	protected final Set<Contract> failedContracts = new HashSet<Contract>();

	protected final Set<Contract> waitContracts = new HashSet<Contract>();
	private final HashedHashSet<AgentIdentifier, Contract> acceptedContracts =
			new HashedHashSet<AgentIdentifier, Contract>();
	protected final HashedHashSet<AgentIdentifier, Contract> rejectedContracts =
			new HashedHashSet<AgentIdentifier, Contract>();

	protected final Set<Contract> initiatorContracts = new HashSet<Contract>();
	protected final Set<Contract> participantContracts = new HashSet<Contract>();

	//
	//
	//

	public ContractTrunk(final SimpleNegotiatingAgent<?, Contract> agent) {
		super(agent);
	}

	public ContractTrunk() {
		super();
	}

	//
	//
	//


	/*
	 *
	 */

	public Contract getContract(final ContractIdentifier id)
			throws UnknownContractException {
		final Contract contract = this.identifier2contract.get(id);
		if (contract == null) {
			throw new UnknownContractException(id);
		} else {
			return contract;
		}
	}

	//non optimisé
	//	public Collection<Contract> getContracts(final AgentIdentifier id) {
	//		final ArrayList<Contract> l = new ArrayList<Contract>();
	//		for (final Contract c : this.getAllContracts()) {
	//			if (c.getAllInvolved().contains(id)) {
	//				l.add(c);
	//			}
	//		}
	//		return l;
	//	}

	public Collection<Contract> getContractsAcceptedBy(final AgentIdentifier id) {
		return Collections.unmodifiableCollection(this.acceptedContracts.get(id));
	}

	public Collection<Contract> getContractsRejectedBy(final AgentIdentifier id) {
		return Collections.unmodifiableCollection(this.rejectedContracts.get(id));
	}

	public Collection<Contract> getAllContracts() {
		return Collections.unmodifiableCollection(this.identifier2contract.values());
	}

	/*
	 *
	 */

	public Collection<Contract> getOnWaitContracts() {
		return Collections.unmodifiableCollection(this.waitContracts);
	}

	public Collection<Contract> getRequestableContracts() {
		return Collections.unmodifiableCollection(this.requestableContracts);
	}

	public Collection<Contract> getFailedContracts() {
		return Collections.unmodifiableCollection(this.failedContracts);
	}

	public Collection<Contract> getAllInitiatorContracts() {
		return Collections.unmodifiableCollection(this.initiatorContracts);
	}

	public Collection<Contract> getAllParticipantContracts() {
		return Collections.unmodifiableCollection(this.participantContracts);
	}

	/*
	 *
	 */

	public List<Contract> getInitiatorRequestableContracts() {
		final ArrayList<Contract> l = new ArrayList<Contract>(this.requestableContracts);
		l.removeAll(this.participantContracts);
		return l;
	}

	public List<Contract> getInitiatorOnWaitContracts() {
		final ArrayList<Contract> l = new ArrayList<Contract>(this.waitContracts);
		l.removeAll(this.participantContracts);
		return l;
	}

	public List<Contract> getParticipantOnWaitContracts() {
		final ArrayList<Contract> l = new ArrayList<Contract>(this.waitContracts);
		l.removeAll(this.initiatorContracts);
		l.removeAll(this.acceptedContracts.get(this.getMyAgentIdentifier()));
		l.removeAll(this.rejectedContracts.get(this.getMyAgentIdentifier()));
		return l;
	}

	public List<Contract> getParticipantAlreadyAcceptedContracts() {
		final ArrayList<Contract> l = new ArrayList<Contract>(this.acceptedContracts.get(this.getMyAgentIdentifier()));
		l.removeAll(this.initiatorContracts);
		return l;
	}

	/*
	 * 
	 */

	/**
	 * 
	 * @return contract locked by the contract trunk, they can not be removed see {@link AtMostCContractSelectioner}
	 */
	public Collection<Contract> getLockedContracts(){
		return new ArrayList<Contract>();
	}


	/**
	 * Updates contract with the new initialState and actionSpec
	 * @param newState vaut null si non spécifier
	 * @param actionSpec vaut null si non spécifier
	 * @return the set of modified contracts
	 */
	public Collection<ContractIdentifier> updateContracts(final AgentState newState){
		final Collection<ContractIdentifier> modifiedContracts = new ArrayList<ContractIdentifier>();
		if (newState!=null) {

			final AgentIdentifier id = newState.getMyAgentIdentifier();
			AgentState assertedState = null;

			for (final Contract c : this.getAllContracts()) {
				if (c.getAllInvolved().contains(id)) {
					AgentState actualState;
					try {
						actualState = c.getInitialState(id);
					} catch (final IncompleteContractException e) {
						actualState=null;
					}

					//debut assertion vérification que tous les contrats sont cohérents
					if (assertedState==null) {
						assertedState = actualState;
					}
					else {
						assert assertedState.equals(actualState);
						//fin assertion
					}

					if (actualState==null || !actualState.equals(newState)){
						modifiedContracts.add(c.getIdentifier());
						c.setInitialState(newState);
					}
				}
			}
		}
		return modifiedContracts;
	}
	/*
	 *
	 */

	/**
	 * ajoute automatiquement en wait
	 */
	public void addContract(final Contract c) {
		//		if (c instanceof DestructionOrder)
		//			throw new RuntimeException();
		//		try {
		//			assert (c.getInitiator().equals(getMyAgentIdentifier()) || c.isInitiallyValid());
		//		} catch (IncompleteContractException e) {
		//			e.printStackTrace();
		//			assert false:"incomplete contract added "+c;
		//		}
		this.identifier2contract.put(c.getIdentifier(), c);
		this.waitContracts.add(c);
		if (c.getInitiator().equals(this.getMyAgentIdentifier())) {
			this.initiatorContracts.add(c);
		} else {
			this.participantContracts.add(c);
		}
		this.addAcceptation(c.getInitiator(), c);
	}

	public void addAcceptation(final AgentIdentifier id, final Contract c) {
		assert this.identifier2contract.containsKey(c.getIdentifier()):c;
		//		if (c instanceof DestructionOrder)
		//			throw new RuntimeException();
		this.acceptedContracts.add(id, c);
		/**/
		if (this.isRequestable(c))
		{
			this.waitContracts.remove(c);
			this.requestableContracts.add(c);
		}
	}

	public void addRejection(final AgentIdentifier id, final Contract c) {
		assert this.identifier2contract.containsKey(c.getIdentifier()):c;
		assert id.equals(c.getInitiator()) || !this.acceptedContracts.get(id).contains(c):"impossible to reject " + c
		+ " : "+id+" is participant t previously accepted";
		//		if (c instanceof DestructionOrder)
		//			throw new RuntimeException();
		//		if (this.acceptedContracts.get(id).contains(c))
		//			throw new RuntimeException("impossible to reject " + c
		//						+ " : i'm participant that previously accepted");
		//			if (id.equals(c.getInitiator()))
		//				this.acceptedContracts.get(id).remove(c);
		//			else

		//		if (this.consensualContracts.contains(c))
		//			if (id.equals(c.getInitiator()))
		//				this.consensualContracts.remove(c);
		//			else
		//				throw new RuntimeException("impossible to reject " + c
		//						+ " : i'm participant that previously accepted");
		/**/
		this.rejectedContracts.add(id, c);

		if (id.equals(c.getInitiator())) {
			this.acceptedContracts.get(id).remove(c);
		}
		/**/
		if (this.isAFailure(c)) {
			this.waitContracts.remove(c);
			this.failedContracts.add(c);
		}
	}
	//
	//		public void removeRejection(final AgentIdentifier id, final Contract c) {
	//			this.rejectedContracts.get(id).remove(c);
	//			this.waitContracts.add(c);
	//		}

	//
	// Primitive
	//

	// CONSENSUAL IMPLEMENTATION
	public boolean isRequestable(final Contract c) {
		for (final AgentIdentifier id : c.getAllParticipants()) {
			if (!this.acceptedContracts.get(id).contains(c)) {
				return false;
			}
		}
		return true;
	}

	// CONSENSUAL IMPLEMENTATION
	public boolean isAFailure(final Contract c) {
		for (final AgentIdentifier id : c.getAllParticipants()) {
			if (this.rejectedContracts.get(id).contains(c)) {
				return true;
			}
		}
		return false;
	}

	/*
	 *
	 */

	//	public boolean everyOneHasAnswered(final Contract c) {
	//		for (final AgentIdentifier id : c.getAllParticipants())
	//			if (!this.getContractsAcceptedBy(id).contains(c)
	//					&& !this.getContractsRejectedBy(id).contains(c))
	//				return false;
	//				return true;
	//	}

	public boolean isEmpty() {
		return this.identifier2contract.isEmpty();
	}

	public boolean contains(final ContractIdentifier c) {
		return this.identifier2contract.containsKey(c);
	}

	public boolean contains(final Contract c) {
		return this.identifier2contract.containsValue(c);
	}

	public boolean containsAllKey(final Collection<ContractIdentifier> c) {
		return this.identifier2contract.keySet().containsAll(c);
	}

	public boolean containsAllValues(final Collection<Contract> c) {
		return this.identifier2contract.values().containsAll(c);
	}

	public void remove(final Contract c) {
		this.identifier2contract.remove(c.getIdentifier());

		this.acceptedContracts.removeAvalue(c);
		this.rejectedContracts.removeAvalue(c);

		this.waitContracts.remove(c);

		this.requestableContracts.remove(c);
		this.failedContracts.remove(c);

		this.initiatorContracts.remove(c);
		this.participantContracts.remove(c);
	}

	//	public void remove(final ContractIdentifier id) {
	//		assert this.identifier2contract.containsKey(id);
	//		final Contract c = this.identifier2contract.get(id);
	//		remove(c);
	//	}

	public void removeAll(final Collection<Contract> contracts) {
		for (final Contract c : contracts) {
			this.remove(c);
		}
	}

	public void clear() {
		this.identifier2contract.clear();
		this.rejectedContracts.clear();
		this.acceptedContracts.clear();
		this.waitContracts.clear();
		this.requestableContracts.clear();
		this.failedContracts.clear();
		this.initiatorContracts.clear();
		this.participantContracts.clear();
	}

	/*
	 *
	 */

	@Override
	public String toString() {
		String result = "[Known contracts are : ";
		for (final Contract c : this.identifier2contract.values()) {
			result += this.statusOf(c);
		}
		result += "]";
		return result;
	}

	public String statusOf(final Contract c) {
		String result = "\n*Status of " + c+"\n";
		if (this.getOnWaitContracts().contains(c)) {
			result += "wait;";
		}
		if (this.getRequestableContracts().contains(c)) {
			result += "requestable;";
		}
		if (this.getFailedContracts().contains(c)) {
			result += "rejected;";
		}

		result += "(accepted:";
		for (final AgentIdentifier id : c.getAllParticipants()) {
			if (this.acceptedContracts.get(id).contains(c)) {
				result += id + ";";
			}
		}
		result += ");";

		result += "(rejected:";
		for (final AgentIdentifier id : c.getAllParticipants()) {
			if (this.rejectedContracts.get(id).contains(c)) {
				result += id + ";";
			}
		}
		result += ")*";

		return result;
	}
}

// public void clearRequestable() {
// for (Contract c : getRequestableContracts())
// remove(c);
//
// }

// public void clearRejected() {
// for (Contract c : getRejectedContracts())
// remove(c);
// }