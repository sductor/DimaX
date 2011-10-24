package negotiation.negotiationframework.interaction;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import dima.basicagentcomponents.AgentIdentifier;

public abstract class ContractTransition<
ActionSpec extends AbstractActionSpecification> implements
		AbstractContractTransition<ActionSpec> {

	//
	// Fields
	//

	/**
	 * 
	 */
	private static final long serialVersionUID = -3237282341034282940L;
	protected final AgentIdentifier creator;
	protected final Date creationTime = new Date();
	protected final long validityTime;

	protected final String name;
	protected final List<AgentIdentifier> actors;
	protected final List<Object> args; // functionnal properties

	private final HashMap<AgentIdentifier, ActionSpec> specs = new HashMap<AgentIdentifier, ActionSpec>(); // non
																											// functionnal
																											// properties

	//
	// Constructor
	//

	public ContractTransition(AgentIdentifier creator, String name,
			List<AgentIdentifier> actors, List<Object> args,
			final long validityTime) {
		super();
		this.actors = actors;
		this.creator = creator;
		this.name = name;
		this.args = args;
		this.validityTime = validityTime;
	}

	public ContractTransition(AgentIdentifier creator, String name,
			AgentIdentifier[] actors, Object[] args, long validityTime) {
		super();
		this.actors = Arrays.asList(actors);
		this.creator = creator;
		this.name = name;
		this.args = Arrays.asList(args);
		this.validityTime = validityTime;
	}

	//
	// Accessors
	//

	@Override
	public AgentIdentifier getInitiator() {
		return this.creator;
	}

	@Override
	public Collection<AgentIdentifier> getAllInvolved() {
		final Collection<AgentIdentifier> result = new ArrayList<AgentIdentifier>(
				this.actors);
		result.add(this.creator);
		return result;
	}

	@Override
	public List<AgentIdentifier> getAllParticipants() {
		return this.actors;
	}

	@Override
	public Collection<AgentIdentifier> getNotInitiatingParticipants() {
		final Collection<AgentIdentifier> result = new ArrayList<AgentIdentifier>(
				this.actors);
		result.remove(this.creator);
		return result;
	}

	/*
	 *
	 */

	@Override
	public void setSpecification(final ActionSpec s) {
		if (this.actors.contains(s.getMyAgentIdentifier()))
			this.specs.put(s.getMyAgentIdentifier(), s);
		else
			throw new RuntimeException("unappropriate specification set");
	}

	/*
	 *
	 */

	@Override
	public ContractIdentifier getIdentifier() {
		return new ContractIdentifier(this.creator, this.creationTime,
				this.validityTime, this.actors);
	}

	/*
	 *
	 */

	@Override
	public long getUptime() {
		return new Date().getTime() - this.creationTime.getTime();
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
	public ActionSpec getSpecificationOf(AgentIdentifier id){
		return specs.get(id);
	}
	

	//
	// Primitive
	//

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
