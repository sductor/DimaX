package frameworks.negotiation.protocoles.collaborative;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;

import dima.basicagentcomponents.AgentIdentifier;
import frameworks.negotiation.contracts.AbstractActionSpecif;
import frameworks.negotiation.contracts.AbstractContractTransition;
import frameworks.negotiation.contracts.ContractIdentifier;
import frameworks.negotiation.contracts.MatchingCandidature;
import frameworks.negotiation.contracts.ReallocationContract;
import frameworks.negotiation.contracts.ResourceIdentifier;
import frameworks.negotiation.rationality.AgentState;

public class InformedCandidature<Contract extends MatchingCandidature>
extends MatchingCandidature
implements AbstractContractTransition{

	/**
	 *
	 */
	private static final long serialVersionUID = -5411058011701987490L;
	private final Contract candidature;
	//	private ReallocationContract<Contract, ActionSpec> consequentContract;
	private final Collection<ReallocationContract<Contract>> possibleContracts;
	private final Collection<ReallocationContract<Contract>> requestedContracts;

	public InformedCandidature(final Contract c) {
		super(c.getInitiator(),c.getAgent(), c.getResource(), c.getValidityTime());
		this.candidature = c;
		final Collection<Contract> actions = new ArrayList<Contract>();
		actions.add(this.candidature);
		//		consequentContract=new ReallocationContract<Contract, ActionSpec>(
		//				candidature.getInitiator(),
		//				actions);
		this.possibleContracts =  new ArrayList<ReallocationContract<Contract>>();
		this.requestedContracts = new ArrayList<ReallocationContract<Contract>>();
		this.possibleContracts.add(
				new ReallocationContract<Contract>(
						this.candidature.getInitiator(),
						actions));
	}

	/*
	 *
	 */

	public Contract getCandidature() {
		return this.candidature;
	}


	public Collection<ReallocationContract<Contract>> getPossibleContracts() {
		return this.possibleContracts;
	}

	public Collection<ReallocationContract<Contract>> getRequestedContracts() {
		return this.requestedContracts;
	}

	/*
	 *
	 */

	public static
	<Contract extends MatchingCandidature>
	Collection<ReallocationContract<Contract>> toPossibles(final Collection<InformedCandidature<Contract>> contracts){
		final Collection<ReallocationContract<Contract>> result = new HashSet<ReallocationContract<Contract>>();
		for (final InformedCandidature<Contract> c : contracts) {
			result.addAll(c.getPossibleContracts());
		}
		return result;
	}


	public static<
	Contract extends MatchingCandidature>
	Collection<ReallocationContract<Contract>> toRequested(final Collection<InformedCandidature<Contract>> contracts){
		final Collection<ReallocationContract<Contract>> result = new HashSet<ReallocationContract<Contract>>();
		for (final InformedCandidature<Contract> c : contracts) {
			result.addAll(c.getRequestedContracts());
		}
		return result;
	}


	public static<
	Contract extends MatchingCandidature>
	Collection<Contract> toCandidatures(final Collection<InformedCandidature<Contract>> contracts){
		final Collection<Contract> result = new ArrayList<Contract>();
		for (final InformedCandidature<Contract> c : contracts) {
			result.add(c.getCandidature());
		}
		return result;
	}


	/*
	 *
	 */

	//	public void setConsequentContract(
	//			ReallocationContract<Contract, ActionSpec> consequentContract) {
	//		this.consequentContract = consequentContract;
	//	}
	//
	//
	//	public ReallocationContract<Contract, ActionSpec> getConsequentContract() {
	//		return consequentContract;
	//	}
	public ReallocationContract<Contract> getBestPossible(final Comparator<Collection<Contract>> pref){
		return Collections.max(this.possibleContracts, pref);
	}

	/*
	 *
	 */

	@Override
	public <State extends AgentState> State computeResultingState(final State s) throws IncompleteContractException {
		return this.candidature.computeResultingState(s);
	}

	@Override
	public AgentState computeResultingState(final AgentIdentifier id) throws IncompleteContractException {
		return this.candidature.computeResultingState(id);
	}

	@Override
	public long getValidityTime() {
		return this.candidature.getValidityTime();
	}


	@Override
	public AgentIdentifier getAgent() {
		return this.candidature.getAgent();
	}


	@Override
	public ResourceIdentifier getResource() {
		return this.candidature.getResource();
	}


	@Override
	public boolean isMatchingCreation() {
		return this.candidature.isMatchingCreation();
	}


	@Override
	public Boolean getCreation() {
		return this.candidature.getCreation();
	}


	@Override
	public AgentIdentifier getInitiator() {
		return this.candidature.getInitiator();
	}


	@Override
	public void setCreation(final Boolean creation) {
		this.candidature.setCreation(creation);
	}


	@Override
	public Collection<AgentIdentifier> getAllInvolved() {
		return this.candidature.getAllInvolved();
	}


	@Override
	public String toString() {
		return this.candidature.toString();
	}


	@Override
	public List<AgentIdentifier> getAllParticipants() {
		return this.candidature.getAllParticipants();
	}


	@Override
	public Collection<AgentIdentifier> getNotInitiatingParticipants() {
		return this.candidature.getNotInitiatingParticipants();
	}


	@Override
	public <ActionSpec extends AbstractActionSpecif> void setSpecification(final ActionSpec spec) {
		this.candidature.setSpecification(spec);
		for (final ReallocationContract r : this.possibleContracts) {
			r.setSpecification(spec);
		}for (final ReallocationContract r : this.requestedContracts) {
			r.setSpecification(spec);
		}
	}

	@Override
	public <State extends AgentState> void setInitialState(final State state) {
		this.candidature.setInitialState(state);
		for (final ReallocationContract r : this.possibleContracts) {
			r.setInitialState(state);
		}for (final ReallocationContract r : this.requestedContracts) {
			r.setInitialState(state);
		}
	}

	@Override
	public ContractIdentifier getContractIdentifier() {
		return this.candidature.getContractIdentifier();
	}


	@Override
	public long getUptime() {
		return this.candidature.getUptime();
	}


	@Override
	public long getCreationTime() {
		return this.candidature.getCreationTime();
	}


	@Override
	public boolean hasReachedExpirationTime() {
		return this.candidature.hasReachedExpirationTime();
	}


	@Override
	public boolean willReachExpirationTime(final long t) {
		return this.candidature.willReachExpirationTime(t);
	}


	@Override
	public AbstractActionSpecif getSpecificationOf(final AgentIdentifier id) throws IncompleteContractException {
		return this.candidature.getSpecificationOf(id);
	}


	@Override
	public boolean isViable()
			throws IncompleteContractException {
		return this.candidature.isViable();
	}

	@Override
	public <State extends AgentState> boolean isViable(final State... initialStates)
			throws IncompleteContractException {
		return this.candidature.isViable(initialStates);
	}

	@Override
	public <State extends AgentState> boolean isViable(
			final Collection<State> initialStates)
					throws IncompleteContractException {
		return this.candidature.isViable(initialStates);
	}

	@Override
	public boolean equals(final Object o) {
		return this.candidature.equals(o);
	}


	@Override
	public int hashCode() {
		return this.candidature.hashCode();
	}

	@Override
	public AgentState getInitialState(final AgentIdentifier id)	throws IncompleteContractException {
		return this.candidature.getInitialState(id);
	}

	@Override
	public InformedCandidature<Contract> clone() {
		return new InformedCandidature<Contract>((Contract) this.getCandidature().clone());
	}

}
