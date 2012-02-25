package negotiation.negotiationframework.interaction.contracts;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import dima.basicagentcomponents.AgentIdentifier;
import dimaxx.tools.mappedcollections.HashedHashSet;

public class ReallocationContract<
Contract extends MatchingCandidature<ActionSpec>,
ActionSpec extends AbstractActionSpecification> implements 
AbstractContractTransition<ActionSpec>{
	
	protected final AgentIdentifier creator;
	protected final Date creationTime = new Date();
	protected final long validityTime;
	
	HashedHashSet<AgentIdentifier, Contract> actions;
	

	//
	// Constructor
	
	public ReallocationContract(
			AgentIdentifier creator, 
			Collection<Contract> actions,
			long validityTime) {
		this.creator = creator;
		this.validityTime = validityTime;
		
		for (Contract a : actions){
			for (AgentIdentifier id : a.getAllParticipants())
				this.actions.add(id, a);		
		}
		
		//Cleaning states///////////////////
		
		final Map<AgentIdentifier, ActionSpec> result = new HashMap<AgentIdentifier, ActionSpec>();

		for (Contract c : actions)
			for (AgentIdentifier id : c.getAllParticipants())
				if (result.containsKey(id)){
					if (c.getSpecificationOf(id).isNewerThan(result.get(id))>1)
						result.put(id,c.getSpecificationOf(id));
				} else
					result.put(id,c.getSpecificationOf(id));

		//updating each contract with the freshest state
		for (final Contract c : this.actions.getAllValues())
			for (AgentIdentifier id : c.getAllParticipants())
				c.setSpecification(result.get(id));	
	}

	public Collection<Contract> getAllocation(){
		return actions.getAllValues();
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
		return creator;
	}

	@Override
	public Collection<AgentIdentifier> getAllParticipants() {
		return actions.keySet();
	}
	
	@Override
	public Collection<AgentIdentifier> getNotInitiatingParticipants() {
		final Collection<AgentIdentifier> result = new ArrayList<AgentIdentifier>(getAllParticipants());
		result.remove(this.creator);
		return result;
	}


	@Override
	public Collection<AgentIdentifier> getAllInvolved() {
		final Collection<AgentIdentifier> result = new ArrayList<AgentIdentifier>(getAllParticipants());
		result.add(this.creator);
		return result;		
	}

	@Override
	public void setSpecification(ActionSpec s) {
		for (Contract a : actions.get(s.getMyAgentIdentifier())){
			a.setSpecification(s);
		}		
	}

	@Override
	public ActionSpec getSpecificationOf(AgentIdentifier id) {
		return actions.get(id).iterator().next().getSpecificationOf(id);
	}


	
	@Override
	public <State extends ActionSpec> State computeResultingState(State s) {
		Set<Contract> contractOfS = actions.get(s.getMyAgentIdentifier());
		State s2 = s;
		for (Contract m : contractOfS)
			s2 = m.computeResultingState(s2);
		return s2;
	}


	@Override
	public ActionSpec computeResultingState(AgentIdentifier id) {
		return computeResultingState(getSpecificationOf(id));
	}

	//
	// Primitive
	//


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
