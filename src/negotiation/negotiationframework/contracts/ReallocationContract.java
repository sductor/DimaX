package negotiation.negotiationframework.contracts;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import dima.basicagentcomponents.AgentIdentifier;
import dimaxx.tools.mappedcollections.HashedHashSet;

public class ReallocationContract<
Contract extends AbstractContractTransition<ActionSpec>,
ActionSpec extends AbstractActionSpecification>
extends HashSet<Contract> implements
AbstractContractTransition<ActionSpec>{

	/**
	 * 
	 */
	private static final long serialVersionUID = -4851833906871445475L;
	protected final AgentIdentifier creator;
	protected final Date creationTime = new Date();
	protected final long validityTime;

	HashedHashSet<AgentIdentifier, Contract> actions = 
			new HashedHashSet<AgentIdentifier, Contract>();


	//
	// Constructor

	public ReallocationContract(
			final AgentIdentifier creator,
			final Collection<Contract> actions) {
		super(actions);
		this.creator = creator;

		//Bouh on doit calculer l'*expiration* time en fonction du min de tous les contrats!!!
		this.validityTime = Long.MAX_VALUE;

		for (final Contract a : actions){
			for (final AgentIdentifier id : a.getAllParticipants())
				this.actions.add(id, a);
		}
		//Cleaning states///////////////////

		final Map<AgentIdentifier, ActionSpec> result = 
				new HashMap<AgentIdentifier, ActionSpec>();

					for (final Contract c : actions){
						for (final AgentIdentifier id : c.getAllParticipants()){
							try {
									if (result.containsKey(id)){
										if (c.getSpecificationOf(id).isNewerThan(result.get(id))>1)
											result.put(id,c.getSpecificationOf(id));
									} else
										result.put(id,c.getSpecificationOf(id));
							} catch (IncompleteContractException e) {}
						}
					}
					//updating each contract with the freshest state
					for (final Contract c : this.actions.getAllValues()){
						for (final AgentIdentifier id : c.getAllParticipants())
							if (result.containsKey(id)){
								c.setSpecification(result.get(id));
							}
					}
	}


	public ReallocationContract(
			final AgentIdentifier creator,
			final Contract... actions) {
		this(creator, Arrays.asList(actions));
	}
	//
	// Methods
	//

	@Override
	public ContractIdentifier getIdentifier() {
		return new ContractIdentifier(this.creator, this.creationTime,
				this.validityTime, this.getAllParticipants());
	}

	@Override
	public AgentIdentifier getInitiator() {
		return this.creator;
	}

	@Override
	public Collection<AgentIdentifier> getAllParticipants() {
		return this.actions.keySet();
	}

	@Override
	public Collection<AgentIdentifier> getNotInitiatingParticipants() {
		final Collection<AgentIdentifier> result = new ArrayList<AgentIdentifier>(this.getAllParticipants());
		result.remove(this.creator);
		return result;
	}


	@Override
	public Collection<AgentIdentifier> getAllInvolved() {
		final Collection<AgentIdentifier> result = new ArrayList<AgentIdentifier>(this.getAllParticipants());
		result.add(this.creator);
		return result;
	}

	@Override
	public void setSpecification(final ActionSpec s) {
		for (final Contract a : this.actions.get(s.getMyAgentIdentifier()))
			a.setSpecification(s);
	}

	@Override
	public ActionSpec getSpecificationOf(final AgentIdentifier id) throws IncompleteContractException {
		return this.actions.get(id).iterator().next().getSpecificationOf(id);
	}



	@Override
	public <State extends ActionSpec> State computeResultingState(final State s) throws IncompleteContractException {
		final Set<Contract> contractOfS = this.actions.get(s.getMyAgentIdentifier());
		State s2 = s;
		for (final Contract m : contractOfS)
			s2 = m.computeResultingState(s2);
				return s2;
	}


	@Override
	public <State extends ActionSpec> State  computeResultingState(final AgentIdentifier id) 
			throws IncompleteContractException {
		return (State) this.computeResultingState(this.getSpecificationOf(id));
	}

	@Override
	public<State extends ActionSpec>  boolean isViable(final State... s) throws IncompleteContractException{
		return isViable(Arrays.asList(s));
	}


	@Override
	public <State extends ActionSpec> boolean isViable(
			Collection<State> initialStates)
			throws IncompleteContractException {
		for (Contract c : this){
			if (!c.isViable(initialStates))
				return false;
		}
		return true;
	}

	@Override
	public boolean isInitiallyValid()
			throws IncompleteContractException {
		for (Contract c : this){
			if (!c.isInitiallyValid())
				return false;
		}
		return true;
	}
	
	
	//
	// Primitive
	//

	@Override
	public  boolean remove(final Object o){
		throw new RuntimeException("not allowed");
	}

	@Override
	public  boolean removeAll(final Collection<?> o){
		throw new RuntimeException("not allowed");
	}

	@Override
	public long getUptime() {
		return new Date().getTime() - this.creationTime.getTime();
	}
	@Override
	public long getCreationTime() {
		return this.creationTime.getTime();
	}

	@Override
	public boolean hasReachedExpirationTime() {
		return this.getUptime() > this.validityTime;
	}

	@Override
	public boolean willReachExpirationTime(final long t) {
		return this.getUptime() + t > this.validityTime;
	}

	/*
	 *
	 */

	@Override
	public boolean equals(final Object o) {
		if (o instanceof ContractTransition) {
			@SuppressWarnings("unchecked")
			final ContractTransition<ActionSpec> that = (ContractTransition<ActionSpec>) o;
			return that.getIdentifier().equals(this.getIdentifier());
		}
		return false;
	}

	@Override
	public int hashCode() {
		return this.getIdentifier().hashCode();
	}

	public Collection<ContractIdentifier> getIdentifiers() {
		Collection<ContractIdentifier> myIds = new HashSet<ContractIdentifier>();
		for (Contract c : this)
			myIds.add(c.getIdentifier());
				return myIds;
	}


}
