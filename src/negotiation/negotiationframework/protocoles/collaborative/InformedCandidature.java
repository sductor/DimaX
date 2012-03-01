package negotiation.negotiationframework.protocoles.collaborative;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import negotiation.negotiationframework.contracts.AbstractActionSpecification;
import negotiation.negotiationframework.contracts.AbstractContractTransition;
import negotiation.negotiationframework.contracts.ContractIdentifier;
import negotiation.negotiationframework.contracts.MatchingCandidature;
import negotiation.negotiationframework.contracts.ReallocationContract;
import negotiation.negotiationframework.contracts.ResourceIdentifier;
import dima.basicagentcomponents.AgentIdentifier;

public class InformedCandidature<
Contract extends MatchingCandidature<ActionSpec>,
ActionSpec extends AbstractActionSpecification>
extends MatchingCandidature<ActionSpec>
implements AbstractContractTransition<ActionSpec>{

	/**
	 * 
	 */
	private static final long serialVersionUID = -5411058011701987490L;
	private final Contract candidature;
	//	private ReallocationContract<Contract, ActionSpec> consequentContract;
	private final Collection<ReallocationContract<Contract, ActionSpec>> possibleContracts;
	private Collection<ReallocationContract<Contract, ActionSpec>> requestedContracts;

	public InformedCandidature(final Contract c) {
		super(c.getInitiator(),c.getAgent(), c.getResource(), c.getValidityTime());
		this.candidature = c;
		final Collection<Contract> actions = new ArrayList<Contract>();
		actions.add(this.candidature);
		//		consequentContract=new ReallocationContract<Contract, ActionSpec>(
		//				candidature.getInitiator(),
		//				actions);
		this.possibleContracts = new ArrayList<ReallocationContract<Contract,ActionSpec>>();
		this.requestedContracts = new ArrayList<ReallocationContract<Contract,ActionSpec>>();
		this.possibleContracts.add(
				new ReallocationContract<Contract, ActionSpec>(
						this.candidature.getInitiator(),
						actions));
	}

	/*
	 * 
	 */

	public Contract getCandidature() {
		return this.candidature;
	}


	public Collection<ReallocationContract<Contract, ActionSpec>> getPossibleContracts() {
		return this.possibleContracts;
	}

	public Collection<ReallocationContract<Contract, ActionSpec>> getRequestedContracts() {
		return this.requestedContracts;
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
	public ReallocationContract<Contract, ActionSpec> getBestPossible(final Comparator<Collection<Contract>> pref){
		return Collections.max(this.possibleContracts, pref);
	}

	/*
	 * 
	 */

	@Override
	public <State extends ActionSpec> State computeResultingState(final State s) {
		return this.candidature.computeResultingState(s);
	}

	@Override
	public ActionSpec computeResultingState(final AgentIdentifier id) {
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
	public void setSpecification(final ActionSpec s) {
		this.candidature.setSpecification(s);
		for (ReallocationContract r : possibleContracts){
			r.setSpecification(s);
		}for (ReallocationContract r : requestedContracts){
			r.setSpecification(s);
		}
	}


	@Override
	public ContractIdentifier getIdentifier() {
		return this.candidature.getIdentifier();
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
	public ActionSpec getSpecificationOf(final AgentIdentifier id) {
		return this.candidature.getSpecificationOf(id);
	}


	@Override
	public boolean equals(final Object o) {
		return this.candidature.equals(o);
	}


	@Override
	public int hashCode() {
		return this.candidature.hashCode();
	}
}
