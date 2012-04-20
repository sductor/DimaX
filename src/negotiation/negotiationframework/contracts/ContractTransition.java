package negotiation.negotiationframework.contracts;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import dima.basicagentcomponents.AgentIdentifier;
import dima.basicagentcomponents.AgentName;
import dimaxx.experimentation.ExperimentationParameters;

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
		if (!this.specs.containsKey(id)) {
			throw new IncompleteContractException();
		} else {
			return this.specs.get(id);
		}

	}

	@Override
	public void setSpecification(final ActionSpec s) {
		assert s!=null;
		assert !this.specs.containsKey(s.getMyAgentIdentifier()) || s.isNewerThan(this.specs.get(s.getMyAgentIdentifier()))>=0:
			s+" "+this.specs.get(s.getMyAgentIdentifier());

		if (this.actors.contains(s.getMyAgentIdentifier())) {
			this.specs.put(s.getMyAgentIdentifier(), s);
		} else {
			throw new RuntimeException("unappropriate specification set");
		}

		try {
			if (!ExperimentationParameters.currentlyInstanciating) {
				assert this.isInitiallyValid():this;
			}
		} catch (final IncompleteContractException e){/*ok!*/}
	}

	/*
	 *
	 */

	@Override
	public boolean isInitiallyValid()
			throws IncompleteContractException {
		if (!this.specs.keySet().containsAll(this.actors)) {
			throw new IncompleteContractException();
		} else {
			for (final AgentIdentifier id : this.actors) {
				if (!this.getSpecificationOf(id).isValid()) {
					return false;
				}
			}
		}
		return true;
	}

	@Override
	public <State extends ActionSpec> boolean isViable(final State... initialStates)
			throws IncompleteContractException {
		return this.isViable(Arrays.asList(initialStates));
	}

	@Override
	public <State extends ActionSpec> boolean isViable(
			final Collection<State> initialStates)
					throws IncompleteContractException {
		final Collection<AgentIdentifier> agents =
				new ArrayList<AgentIdentifier>();
		agents.addAll(this.actors);

		for (final State s : initialStates) {
			if (!this.computeResultingState(s).isValid()) {
				return false;
			} else {
				agents.remove(s.getMyAgentIdentifier());
			}
		}

		for (final AgentIdentifier id : agents) {
			if (!this.computeResultingState(id).isValid()) {
				return false;
			}
		}
		return true;
	}

	public boolean isViable() throws IncompleteContractException{
		if (!this.specs.keySet().containsAll(this.actors)) {
			throw new IncompleteContractException();
		} else {
			for (final AgentIdentifier id : this.actors) {
				if (!this.computeResultingState(id).isValid()) {
					return false;
				}
			}
		}
		return true;
	}

	public static <Contract extends AbstractContractTransition<ActionSpec>, ActionSpec extends AbstractActionSpecification>
	Boolean respectRights(final Collection<Contract> cs) throws IncompleteContractException {
		final ReallocationContract<Contract, ActionSpec> reall =
				new ReallocationContract<Contract, ActionSpec>(new AgentName("dummy"), cs);

		for (final AgentIdentifier id : reall.getAllParticipants()) {
			if (!reall.computeResultingState(id).isValid()) {
				return false;
			}
		}

		return true;
	}

	public static <
	Contract extends AbstractContractTransition<ActionSpec>,
	ActionSpec extends AbstractActionSpecification,
	State extends ActionSpec>
	Boolean respectRights(final Collection<Contract> cs, final Collection<State> initialStates) throws IncompleteContractException {
		final ReallocationContract<Contract, ActionSpec> reall =
				new ReallocationContract<Contract, ActionSpec>(new AgentName("dummy"), cs);

		for (final AgentIdentifier id : reall.getAllParticipants()) {
			if (!reall.computeResultingState(id).isValid()) {
				return false;
			}
		}

		return true;
	}
	public static <
	Contract extends AbstractContractTransition<ActionSpec>,
	ActionSpec extends AbstractActionSpecification,
	State extends ActionSpec>
	Boolean respectRights(final Collection<Contract> cs, final State... initialStates) throws IncompleteContractException {
		return ContractTransition.respectRights(cs, Arrays.asList(initialStates));
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

	//
	// Assertion
	//


	public static <Contract extends AbstractContractTransition>
	boolean allViable(final Collection<Contract> contracts)
			throws IncompleteContractException{
		for (final Contract c : contracts) {
			assert c.isViable():c;
		}

		return true;
	}

	public static <Contract extends AbstractContractTransition<?>>
	boolean stillValid(final Collection<Contract> cs){
		for (final Contract c : cs) {
			assert !c.hasReachedExpirationTime();
		}
		return true;
	}

	public static <Contract extends AbstractContractTransition<?>>
	boolean allComplete(final Collection<Contract> contracts){
		for (final Contract c : contracts) {
			for (final AgentIdentifier id : c.getAllParticipants()) {
				try {
					c.computeResultingState(id);
				} catch (final IncompleteContractException e) {
					assert 1<0:c;
				}
			}
		}
		return true;
	}
}
