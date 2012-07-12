package negotiation.negotiationframework.contracts;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import negotiation.negotiationframework.rationality.AgentState;
import dima.basicagentcomponents.AgentIdentifier;
import dima.basicagentcomponents.AgentName;
import dima.introspectionbasedagents.services.information.ObservationService.Information;
import dimaxx.experimentation.ExperimentationParameters;
import dimaxx.tools.aggregator.AbstractCompensativeAggregation;

public abstract class ContractTransition implements
AbstractContractTransition {
	public long getValidityTime() {
		return this.validityTime;
	}

	private static final long serialVersionUID = -3237282341034282940L;

	//
	// Fields
	//

	protected final AgentIdentifier creator;
	protected Date creationTime = new Date();
	protected final long validityTime;

	protected final List<AgentIdentifier> actors;

	protected Map<AgentIdentifier, AbstractActionSpecif> specs = null;
	protected final Map<AgentIdentifier, AgentState> initState = new Hashtable<AgentIdentifier, AgentState>();

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
	public AgentState getInitialState(final AgentIdentifier id) throws IncompleteContractException{
		if (!this.initState.containsKey(id)) {
			throw new IncompleteContractException(id+"missing in "+this.getIdentifier()+"\n----------- "+this.initState);
		} else {
			return this.initState.get(id);
		}
	}

	@Override
	public AbstractActionSpecif getSpecificationOf(final AgentIdentifier id) throws IncompleteContractException {

		if (this.specs==null){
			return new NullActionSpec(id);
		} else if (!this.specs.containsKey(id)) {
			throw new IncompleteContractException();
		} else {
			return this.specs.get(id);
		}

	}

	@Override
	public <ActionSpec extends AbstractActionSpecif> void setSpecification(final ActionSpec s) {

		assert s!=null;

		if (s instanceof NullActionSpec) {
			return;
		}

		if (this.specs == null){
			this.specs=new HashMap<AgentIdentifier, AbstractActionSpecif>();
		}

		assert this.specs.isEmpty()?true:this.specs.values().iterator().next().getClass().isAssignableFrom(s.getClass());
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

	/**
	 * If the application does not require specification, this methods is to be used instead of setSpecificationNInitialState
	 * @param state
	 * @param s
	 */
	@Override
	public <State extends AgentState> void setInitialState(final State state) {
		assert state != null;

		if (this.actors.contains(state.getMyAgentIdentifier())) {
			//			assert !this.initState.containsKey(state.getMyAgentIdentifier()) || this.initState.get(state.getMyAgentIdentifier()).equals(state):
			//				this.initState.get(state.getMyAgentIdentifier())+" "+state;
			this.initState.put(state.getMyAgentIdentifier(), state);
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
		if (this.specs!=null && !this.specs.keySet().containsAll(this.actors)) {
			throw new IncompleteContractException();
		} else if (!this.initState.keySet().containsAll(this.actors)){
			throw new IncompleteContractException("initSate "+this.initState+"\n actors : "+this.actors);
		} else {
			for (final AgentIdentifier id : this.actors) {
				if (!this.getInitialState(id).isValid()) {
					System.out.println("res : "+this.computeResultingState(id));
					return false;
				}
			}
		}
		return true;
	}

	@Override
	public <State extends AgentState> boolean isViable(final State... initialStates)
			throws IncompleteContractException {
		return this.isViable(Arrays.asList(initialStates));
	}

	@Override
	public <State extends AgentState> boolean isViable(
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

	@Override
	public boolean isComplete(){
		for (final AgentIdentifier id : this.getAllParticipants()){
			try {
				this.getInitialState(id);
			} catch (final IncompleteContractException e) {
				return false;
			}
		}
		return true;
	}
	@Override
	public <State extends AgentState> State computeResultingState(final AgentIdentifier id) throws IncompleteContractException {
		return (State) this.computeResultingState(this.getInitialState(id));
	}

	/*
	 * 
	 */

	public static <Contract extends AbstractContractTransition, ActionSpec extends AbstractActionSpecif>
	Boolean respectRights(final Collection<Contract> cs) throws IncompleteContractException {
		final ReallocationContract<Contract> reall =
				new ReallocationContract<Contract>(new AgentName("dummy"), cs);

		for (final AgentIdentifier id : reall.getAllParticipants()) {
			if (!reall.computeResultingState(id).isValid()) {
				return false;
			}
		}

		return true;
	}

	public static <
	Contract extends AbstractContractTransition,
	ActionSpec extends AbstractActionSpecif,
	State extends AgentState>
	Boolean respectRights(final Collection<Contract> cs, final Collection<State> initialStates) throws IncompleteContractException {
		final ReallocationContract<Contract> reall =
				new ReallocationContract<Contract>(new AgentName("dummy"), cs);

		for (final AgentIdentifier id : reall.getAllParticipants()) {
			if (!reall.computeResultingState(id).isValid()) {
				return false;
			}
		}

		return true;
	}
	public static <
	Contract extends AbstractContractTransition,
	ActionSpec extends AbstractActionSpecif,
	State extends AgentState>
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
			final ContractTransition that = (ContractTransition) o;
			return that.getIdentifier().equals(this.getIdentifier());
		}
		return false;
	}

	@Override
	public int hashCode() {
		return this.getIdentifier().hashCode();
	}

	@Override
	public abstract AbstractContractTransition clone() ;

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

	public static <Contract extends AbstractContractTransition>
	boolean stillValid(final Collection<Contract> cs){
		for (final Contract c : cs) {
			assert !c.hasReachedExpirationTime();
		}
		return true;
	}

	public static <Contract extends AbstractContractTransition>
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

	//
	// Sublcasses
	//


	public class NullActionSpec implements AbstractActionSpecif{

		/**
		 * 
		 */
		private static final long serialVersionUID = -6523162275321856460L;
		AgentIdentifier id;
		private final Long creationTime;

		public NullActionSpec(final AgentIdentifier id) {
			this.id=id;
			this.creationTime = new Date().getTime();
		}

		@Override
		public AgentIdentifier getMyAgentIdentifier() {
			return this.id;
		}



		@Override
		public Long getCreationTime() {
			return this.creationTime;
		}

		@Override
		public long getUptime() {
			return new Date().getTime() - this.creationTime;
		}


		@Override
		public int isNewerThan(final Information i) {
			return 0;
		}

		@Override
		public Double getNumericValue(final Information e) {
			assert false;
			return null;
		}

		@Override
		public Information getRepresentativeElement(
				final Collection<? extends Information> elems) {
			assert false;
			return null;
		}

		@Override
		public Information getRepresentativeElement(
				final Map<? extends Information, Double> elems) {
			assert false;
			return null;
		}

		@Override
		public AbstractCompensativeAggregation<Information> fuse(
				final Collection<? extends AbstractCompensativeAggregation<? extends Information>> averages) {
			assert false;
			return null;
		}

		@Override
		public NullActionSpec clone(){
			return new NullActionSpec(this.id);
		}
	}
}
