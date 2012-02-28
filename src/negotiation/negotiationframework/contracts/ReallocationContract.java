package negotiation.negotiationframework.contracts;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import dima.basicagentcomponents.AgentIdentifier;
import dimaxx.tools.mappedcollections.HashedHashSet;

public class ReallocationContract<
Contract extends MatchingCandidature<ActionSpec>,
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

	HashedHashSet<AgentIdentifier, Contract> actions;


	//
	// Constructor

	public ReallocationContract(
			final AgentIdentifier creator,
			final Collection<Contract> actions) {
		super(actions);
		this.creator = creator;

		//Bouh on doit calculer l'*expiration* time en fonction du min de tous les contrats!!!
		this.validityTime = Long.MAX_VALUE;

		for (final Contract a : actions)
			for (final AgentIdentifier id : a.getAllParticipants())
				this.actions.add(id, a);

					//Cleaning states///////////////////

					final Map<AgentIdentifier, ActionSpec> result = new HashMap<AgentIdentifier, ActionSpec>();

					for (final Contract c : actions)
						for (final AgentIdentifier id : c.getAllParticipants())
							if (result.containsKey(id)){
								if (c.getSpecificationOf(id).isNewerThan(result.get(id))>1)
									result.put(id,c.getSpecificationOf(id));
							} else
								result.put(id,c.getSpecificationOf(id));

					//updating each contract with the freshest state
					for (final Contract c : this.actions.getAllValues())
						for (final AgentIdentifier id : c.getAllParticipants())
							c.setSpecification(result.get(id));
	}

	public Collection<Contract> getAllocation(){
		return this.actions.getAllValues();
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
	public ActionSpec getSpecificationOf(final AgentIdentifier id) {
		return this.actions.get(id).iterator().next().getSpecificationOf(id);
	}



	@Override
	public <State extends ActionSpec> State computeResultingState(final State s) {
		final Set<Contract> contractOfS = this.actions.get(s.getMyAgentIdentifier());
		State s2 = s;
		for (final Contract m : contractOfS)
			s2 = m.computeResultingState(s2);
				return s2;
	}


	@Override
	public ActionSpec computeResultingState(final AgentIdentifier id) {
		return this.computeResultingState(this.getSpecificationOf(id));
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
}
