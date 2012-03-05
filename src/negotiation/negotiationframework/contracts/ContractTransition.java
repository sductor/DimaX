package negotiation.negotiationframework.contracts;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import negotiation.experimentationframework.Laborantin;

import dima.basicagentcomponents.AgentIdentifier;
import dima.basicagentcomponents.AgentName;

public abstract class ContractTransition<
ActionSpec extends AbstractActionSpecification> implements
AbstractContractTransition<ActionSpec> {
	public long getValidityTime() {
		return this.validityTime;
	}

	private static final long serialVersionUID = -3237282341034282940L;

	//
	// Fields
	//

	protected final AgentIdentifier creator;
	protected final Date creationTime = new Date();
	protected final long validityTime;

	protected final List<AgentIdentifier> actors;

	private final Map<AgentIdentifier, ActionSpec> specs = new Hashtable<AgentIdentifier, ActionSpec>(); // non
	// functionnal
	// properties

	//
	// Constructor
	//

	public ContractTransition(final AgentIdentifier creator,
			final List<AgentIdentifier> actors,
			final long validityTime) {
		super();
		this.actors = actors;
		this.creator = creator;
		this.validityTime = validityTime;
	}

	public ContractTransition(final AgentIdentifier creator,
			final AgentIdentifier[] actors,
			final long validityTime) {
		this(creator, Arrays.asList(actors), validityTime);
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
	public ActionSpec getSpecificationOf(final AgentIdentifier id) throws IncompleteContractException{
		if (!this.specs.containsKey(id))
			throw new IncompleteContractException();
		else
			return this.specs.get(id);

	}

	@Override
	public void setSpecification(final ActionSpec s) {
		assert s!=null;

		if (this.actors.contains(s.getMyAgentIdentifier()))
//			if (specs.containsKey(s.getMyAgentIdentifier())){
//				if (s.isNewerThan(specs.get(s.getMyAgentIdentifier()))>0)
//						this.specs.put(s.getMyAgentIdentifier(), s);
//			} else				
				this.specs.put(s.getMyAgentIdentifier(), s);
		else
			throw new RuntimeException("unappropriate specification set");

//		try {
//			if (!Laborantin.initialisation)
//				assert isInitiallyValid():this;
//		} catch (IncompleteContractException e){/*ok!*/}
	}

	/*
	 *
	 */

	public boolean isInitiallyValid()	
			throws IncompleteContractException {
		if (!specs.keySet().containsAll(actors)){
			throw new IncompleteContractException();
		} else {
			for (AgentIdentifier id : actors)
				if (!getSpecificationOf(id).isValid())
					return false;
		}
		return true;		
	}

	@Override
	public <State extends ActionSpec> boolean isViable(State... initialStates)
			throws IncompleteContractException {
		return isViable(Arrays.asList(initialStates));
	}

	@Override
	public <State extends ActionSpec> boolean isViable(
			Collection<State> initialStates)
					throws IncompleteContractException {
		Collection<AgentIdentifier> agents =
				new ArrayList<AgentIdentifier>();
		agents.addAll(actors);

		for (State s : initialStates){
			if (!computeResultingState(s).isValid())
				return false;
			else
				agents.remove(s.getMyAgentIdentifier());
		}

		for (AgentIdentifier id : agents){
			if (!computeResultingState(id).isValid())
				return false;
		}
		return true;
	}

	public boolean isViable() throws IncompleteContractException{
		if (!specs.keySet().containsAll(actors)){
			throw new IncompleteContractException();
		} else {
			for (AgentIdentifier id : actors)
				if (!computeResultingState(id).isValid())
					return false;
		}
		return true;
	}

	public static <Contract extends AbstractContractTransition<ActionSpec>, ActionSpec extends AbstractActionSpecification> 
	Boolean respectRights(final Collection<Contract> cs) throws IncompleteContractException {
		ReallocationContract<Contract, ActionSpec> reall = 
				new ReallocationContract<Contract, ActionSpec>(new AgentName("dummy"), cs);

		for (AgentIdentifier id : reall.getAllParticipants()){
			if (!reall.computeResultingState(id).isValid())
				return false;
		}

		return true;
	}

	public static <
	Contract extends AbstractContractTransition<ActionSpec>, 
	ActionSpec extends AbstractActionSpecification, 
	State extends ActionSpec> 
	Boolean respectRights(final Collection<Contract> cs, Collection<State> initialStates) throws IncompleteContractException {
		ReallocationContract<Contract, ActionSpec> reall = 
				new ReallocationContract<Contract, ActionSpec>(new AgentName("dummy"), cs);

		for (AgentIdentifier id : reall.getAllParticipants()){
			if (!reall.computeResultingState(id).isValid())
				return false;
		}

		return true;
	}
	public static <
	Contract extends AbstractContractTransition<ActionSpec>, 
	ActionSpec extends AbstractActionSpecification, 
	State extends ActionSpec> 
	Boolean respectRights(final Collection<Contract> cs, State... initialStates) throws IncompleteContractException {
		return respectRights(cs, Arrays.asList(initialStates));
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
