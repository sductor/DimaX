package negotiation.negotiationframework.contracts;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;

import negotiation.negotiationframework.rationality.AgentState;
import dima.basicagentcomponents.AgentIdentifier;
import dimaxx.tools.mappedcollections.HashedHashSet;

public class ReallocationContract<
Contract extends AbstractContractTransition<ActionSpec>,
ActionSpec extends AbstractActionSpecif>
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
			for (final AgentIdentifier id : a.getAllParticipants()) {
				this.actions.add(id, a);
			}
		}

		final Map<AgentIdentifier, ActionSpec> result =	new HashMap<AgentIdentifier, ActionSpec>();
		final Map<AgentIdentifier, AgentState> resultState =	new HashMap<AgentIdentifier, AgentState>();

		for (final Contract c : actions){
			for (final AgentIdentifier id : c.getAllParticipants()) {
				try {
					if (result.containsKey(id)){
						if (c.getSpecificationOf(id).isNewerThan(result.get(id))>1) {
							result.put(id,c.getSpecificationOf(id));
							resultState.put(id,c.getInitialState(id));
						}
					} else {
						result.put(id,c.getSpecificationOf(id));
						resultState.put(id,c.getInitialState(id));
					}
				} catch (final IncompleteContractException e) {}
			}
		}
		//updating each contract with the freshest state
		for (final Contract c : this.actions.getAllValues()){
			for (final AgentIdentifier id : c.getAllParticipants()) {
				if (result.containsKey(id)) {
					c.setSpecificationNInitialState(resultState.get(id),result.get(id));
				}
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
	public void setSpecificationNInitialState(AgentState state, ActionSpec spec) {
		for (final Contract a : this.actions.get(state.getMyAgentIdentifier())) {
			a.setSpecificationNInitialState(state,spec);;
		}
	}

	@Override
	public ActionSpec getSpecificationOf(final AgentIdentifier id) throws IncompleteContractException {
		return this.actions.get(id).iterator().next().getSpecificationOf(id);
	}



	@Override
	public <State extends AgentState> State computeResultingState(final State s) throws IncompleteContractException {
		final Set<Contract> contractOfS = this.actions.get(s.getMyAgentIdentifier());
		State s2 = s;
		for (final Contract m : contractOfS) {
			s2 = m.computeResultingState(s2);
		}
		return s2;
	}


	@Override
	public AgentState computeResultingState(final AgentIdentifier id)
			throws IncompleteContractException {
		return this.computeResultingState(this.getInitialState(id));
	}

	@Override
	public<State extends AgentState>  boolean isViable(final State... s) throws IncompleteContractException{
		return this.isViable(Arrays.asList(s));
	}


	@Override
	public <State extends AgentState> boolean isViable(
			final Collection<State> initialStates)
					throws IncompleteContractException {
		for (final Contract c : this) {
			if (!c.isViable(initialStates)) {
				return false;
			}
		}
		return true;
	}

	@Override
	public boolean isInitiallyValid()
			throws IncompleteContractException {
		for (final Contract c : this) {
			if (!c.isInitiallyValid()) {
				return false;
			}
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


	//
	// Primitives
	//

	@Override
	public <State extends AgentState> State getInitialState(AgentIdentifier id)
			throws IncompleteContractException {
		assert initialStateVerif(id);
		if (this.isEmpty())
			throw new NoSuchElementException();
		else{
			Iterator<Contract> cIt = this.iterator();
			return cIt.next().getInitialState(id);
		}
	}


	
	private <State extends AgentState> boolean initialStateVerif(AgentIdentifier id) throws IncompleteContractException{
		if (!this.isEmpty()){
			Iterator<Contract> cIt = this.iterator();
			State initS = cIt.next().getInitialState(id);
			while (cIt.hasNext()){
				State s = cIt.next().getInitialState(id);
				assert initS.equals(s):initS+" "+s;
			}
		}
		return true;
	}
	/**
	 *
	 * @param a1
	 * @param a2
	 * @return the map of initial states of every agent of a1 and a2
	 * @throws IncompleteContractException
	 */
	public static
	<Contract extends AbstractContractTransition<ActionSpec>,
	ActionSpec extends AbstractActionSpecif>
	Map<AgentIdentifier, AgentState> getInitialStates(
			final Collection<Contract> a1,
			final Collection<Contract> a2) throws IncompleteContractException{
		final Map<AgentIdentifier, AgentState> result = new HashMap<AgentIdentifier, AgentState>();
		final Map<AgentIdentifier, ActionSpec> resultSpec = new HashMap<AgentIdentifier, ActionSpec>();
		final Collection<Contract> allContract = new ArrayList<Contract>();
		allContract.addAll(a1);
		allContract.addAll(a2);

		for (final Contract c : allContract) {
			for (final AgentIdentifier id : c.getAllParticipants()) {
				if (result.containsKey(id)){
					if (c.getSpecificationOf(id).isNewerThan(result.get(id))>1) {
						//						assert 1<0;//						System.out.println("remplacing a fresher state");
						result.put(id,c.getInitialState(id));
						resultSpec.put(id, c.getSpecificationOf(id));
					}
				} else {
					result.put(id,c.getInitialState(id));
					resultSpec.put(id, c.getSpecificationOf(id));
				}
			}
		}

		//updating each contract with the freshest spec
		for (final Contract cOld : allContract) {
			for (final AgentIdentifier id : cOld.getAllParticipants()) {
				cOld.setSpecificationNInitialState(result.get(id),resultSpec.get(id));
			}
		}
		return result;
	}

	/**
	 *
	 * @param initialStates
	 * @param alloc
	 * @return the collection of agent spec resulting from the application of the reallocation alloc to the initialStates collection
	 * @throws IncompleteContractException
	 */
	public static
	<Contract extends AbstractContractTransition<ActionSpec>,
	ActionSpec extends AbstractActionSpecif>

	Collection<AgentState> getResultingAllocation(
			final Map<AgentIdentifier, AgentState> initialStates,
			final Collection<Contract> alloc) throws IncompleteContractException{
		final Map<AgentIdentifier, AgentState> meAsMap =
				new HashMap<AgentIdentifier, AgentState>();
		meAsMap.putAll(initialStates);

		for (final Contract c : alloc) {
			for (final AgentIdentifier id : c.getAllParticipants()) {
				meAsMap.put(id, c.computeResultingState(meAsMap.get(id)));
			}
		}
		return meAsMap.values();
	}
	
	/**
	 * Computes the resulting state by performing the allocation specified by the Collection of Contract on the initial state provided.
	 * @param <Contract> Type of Contract dealt here
	 * @param <ActionSpec> Specification associated with this type of Contract
	 * @param <State> State being computed
	 * @param initialState The State from which starts the computation
	 * @param alloc The allocation describing actions to apply to the State
	 * @return the resulting State of the allocation
	 * @throws IncompleteContractException if there some Contract of the allocation is not well completed
	 */
	public static <Contract extends AbstractContractTransition<ActionSpec>,
	ActionSpec extends AbstractActionSpecif,
	State extends AgentState>
	State computeResultingState(final State initialState, final Collection<Contract> alloc)
	throws IncompleteContractException{
	    	State result = initialState;
		for (final Contract c : alloc) {
			result = c.computeResultingState(result);
		}
		return result;
	}

	/**
	 *
	 * @param alloc
	 * @return the collection of action spec that result of the application of alloc
	 * on the collection of action spec of the agent concerned by alloc' contracts
	 * @throws IncompleteContractException
	 */
	public static
	<Contract extends AbstractContractTransition<ActionSpec>,
	ActionSpec extends AbstractActionSpecif>
	Collection<AgentState> getResultingAllocation(
			final Collection<Contract> alloc) throws IncompleteContractException{
		return ReallocationContract.getResultingAllocation(ReallocationContract.getInitialStates(alloc, new ArrayList<Contract>()),alloc);
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
		final Collection<ContractIdentifier> myIds = new HashSet<ContractIdentifier>();
		for (final Contract c : this) {
			myIds.add(c.getIdentifier());
		}
		return myIds;
	}




}
