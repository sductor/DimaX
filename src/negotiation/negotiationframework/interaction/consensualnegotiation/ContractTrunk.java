package negotiation.negotiationframework.interaction.consensualnegotiation;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import negotiation.negotiationframework.interaction.AbstractContractTransition;
import negotiation.negotiationframework.interaction.ContractIdentifier;
import dima.basicagentcomponents.AgentIdentifier;
import dima.basicinterfaces.DimaComponentInterface;
import dimaxx.tools.mappedcollections.HashedHashSet;

public class ContractTrunk<Contract extends AbstractContractTransition<?>> implements
		DimaComponentInterface {

	/**
	 *
	 */
	private static final long serialVersionUID = -4334790767170677224L;

	private final AgentIdentifier myAgentIdentifier;

	private final Map<ContractIdentifier, Contract> identifier2contract = new HashMap<ContractIdentifier, Contract>();

	private final Set<Contract> waitContracts = new HashSet<Contract>();
	private final Set<Contract> consensualContracts = new HashSet<Contract>();

	private final HashedHashSet<AgentIdentifier, Contract> acceptedContracts = new HashedHashSet<AgentIdentifier, Contract>();
	private final HashedHashSet<AgentIdentifier, Contract> rejectedContracts = new HashedHashSet<AgentIdentifier, Contract>();

	//
	//
	//

	public ContractTrunk(final AgentIdentifier myAgentIdentifier) {
		super();
		this.myAgentIdentifier = myAgentIdentifier;
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
		if (contract == null)
			throw new UnknownContractException(id);
		else
			return contract;
	}

	public Collection<Contract> getContracts(final AgentIdentifier id) {
		final ArrayList<Contract> l = new ArrayList<Contract>();
		for (final Contract c : this.getAllContracts())
			if (c.getAllInvolved().contains(id))
				l.add(c);
		return l;
	}

	public List<Contract> getContractsAcceptedBy(final AgentIdentifier id) {
		final ArrayList<Contract> l = new ArrayList<Contract>();
		l.addAll(this.acceptedContracts.get(id));
		return l;
	}

	public List<Contract> getContractsRejectedBy(final AgentIdentifier id) {
		final ArrayList<Contract> l = new ArrayList<Contract>();
		l.addAll(this.rejectedContracts.get(id));
		return l;
	}

	public List<Contract> getAllContracts() {
		final ArrayList<Contract> l = new ArrayList<Contract>();
		l.addAll(this.identifier2contract.values());
		return l;
	}

	/*
	 *
	 */

	public List<Contract> getOnWaitContracts() {
		final ArrayList<Contract> l = new ArrayList<Contract>();
		l.addAll(this.waitContracts);
		return l;
	}

	public List<Contract> getConsensualContracts() {
		final ArrayList<Contract> l = new ArrayList<Contract>();
		l.addAll(this.consensualContracts);
		return l;
	}

	public List<Contract> getRejectedContracts() {
		final ArrayList<Contract> l = new ArrayList<Contract>();
		l.addAll(this.rejectedContracts.getAllValues());
		return l;
	}

	/*
	 *
	 */
	public List<Contract> getAllInitiatorContracts() {
		final ArrayList<Contract> l = new ArrayList<Contract>();
		for (final Contract c : this.identifier2contract.values())
			if (c.getInitiator().equals(this.myAgentIdentifier))
				l.add(c);
		return l;
	}

	public List<Contract> getInitiatorConsensualContracts() {
		final ArrayList<Contract> l = new ArrayList<Contract>();
		for (final Contract c : this.consensualContracts)
			if (c.getInitiator().equals(this.myAgentIdentifier))
				l.add(c);
		return l;

	}

	public List<Contract> getInitiatorOnWaitContracts() {
		final ArrayList<Contract> l = new ArrayList<Contract>();
		for (final Contract c : this.waitContracts)
			if (c.getInitiator().equals(this.myAgentIdentifier))
				l.add(c);
		return l;

	}

	public List<Contract> getParticipantOnWaitContracts() {
		final ArrayList<Contract> l = new ArrayList<Contract>();
		for (final Contract c : this.waitContracts)
			if (!c.getInitiator().equals(this.myAgentIdentifier)
					&& !this.acceptedContracts.get(this.myAgentIdentifier)
							.contains(c)
					&& !this.rejectedContracts.get(this.myAgentIdentifier)
							.contains(c))
				l.add(c);
		return l;
	}

	public List<Contract> getParticipantAlreadyAcceptedContracts() {
		final ArrayList<Contract> l = new ArrayList<Contract>();
		for (final Contract c : this.getAllContracts())
			if (!c.getInitiator().equals(this.myAgentIdentifier)
					&& this.acceptedContracts.get(this.myAgentIdentifier)
							.contains(c))
				l.add(c);
		return l;
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
		this.identifier2contract.put(c.getIdentifier(), c);
		this.waitContracts.add(c);
		this.addAcceptation(c.getInitiator(), c);
	}

	public void addAcceptation(final AgentIdentifier id, final Contract c) {
//		if (c instanceof DestructionOrder)
//			throw new RuntimeException();
		this.acceptedContracts.add(id, c);
		/**/
		if (this.isRequestable(c)) {
			this.waitContracts.remove(c);
			this.consensualContracts.add(c);
		}
	}

	public void addRejection(final AgentIdentifier id, final Contract c) {
//		if (c instanceof DestructionOrder)
//			throw new RuntimeException();
		if (this.acceptedContracts.get(id).contains(c))
			if (id.equals(c.getInitiator()))
				this.acceptedContracts.get(id).remove(c);
			else
				throw new RuntimeException("impossible to reject " + c
						+ " : i'm participant that previously accepted");
		if (this.consensualContracts.contains(c))
			if (id.equals(c.getInitiator()))
				this.consensualContracts.remove(c);
			else
				throw new RuntimeException("impossible to reject " + c
						+ " : i'm participant that previously accepted");
		/**/
		this.rejectedContracts.add(id, c);
		/**/
		if (this.isAFailure(c))
			this.waitContracts.remove(c);
	}

	public void removeRejection(final AgentIdentifier id, final Contract c) {
		rejectedContracts.get(id).remove(c);
		this.waitContracts.add(c);
	}
	
	//
	// Primitive
	//

	// CONSENSUAL IMPLEMENTATION
	private boolean isRequestable(final Contract c) {
		for (final AgentIdentifier id : c.getAllParticipants())
			if (!this.acceptedContracts.get(id).contains(c))
				return false;
		return true;
	}

	// CONSENSUAL IMPLEMENTATION
	private boolean isAFailure(final Contract c) {
		for (final AgentIdentifier id : c.getAllParticipants())
			if (this.rejectedContracts.get(id).contains(c))
				return true;
		return false;
	}

	/*
	 *
	 */

	public boolean everyOneHasAnswered(final Contract c) {
		for (final AgentIdentifier id : c.getAllParticipants())
			if (!this.getContractsAcceptedBy(id).contains(c)
					&& !this.getContractsRejectedBy(id).contains(c))
				return false;
		return true;
	}

	public boolean isEmpty() {
		return this.identifier2contract.isEmpty();
	}

	public boolean contains(final ContractIdentifier c) {
		return this.identifier2contract.containsKey(c);
	}

	public boolean contains(final Contract c) {
		return this.identifier2contract.containsValue(c);
	}

	public void remove(final Contract c) {
		this.identifier2contract.remove(c.getIdentifier());
		this.acceptedContracts.removeAvalue(c);
		this.consensualContracts.remove(c);
		this.rejectedContracts.removeAvalue(c);
		this.waitContracts.remove(c);
	}

	public void remove(final ContractIdentifier id) {
		final Contract c = this.identifier2contract.remove(id);
		if (c != null) {
			this.acceptedContracts.removeAvalue(c);
			this.consensualContracts.remove(c);
			this.rejectedContracts.removeAvalue(c);
			this.waitContracts.remove(c);
		}
	}

	public void removeAll(final Collection<Contract> contracts) {
		for (final Contract c : contracts)
			this.remove(c);
	}

	public void clear() {
		this.identifier2contract.clear();
		this.rejectedContracts.clear();
		this.acceptedContracts.clear();
		this.waitContracts.clear();
		this.consensualContracts.clear();
	}

	/*
	 *
	 */

	@Override
	public String toString() {
		String result = "[Known contracts are : ";
		for (final Contract c : this.identifier2contract.values())
			result += this.statusOf(c);
		result += "]";
		return result;
	}

	public String statusOf(final Contract c) {
		String result = "\n*Status of " + c;
		if (this.getOnWaitContracts().contains(c))
			result += "wait;";
		if (this.getConsensualContracts().contains(c))
			result += "requestable;";
		if (this.getRejectedContracts().contains(c))
			result += "rejected;";

		result += "(accepted:";
		for (final AgentIdentifier id : c.getAllParticipants())
			if (this.acceptedContracts.get(id).contains(c))
				result += id + ";";
		result += ");";

		result += "(rejected:";
		for (final AgentIdentifier id : c.getAllParticipants())
			if (this.rejectedContracts.get(id).contains(c))
				result += id + ";";
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