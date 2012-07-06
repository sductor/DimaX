package negotiation.negotiationframework.protocoles.collaborative;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;

import negotiation.negotiationframework.contracts.AbstractActionSpecif;
import negotiation.negotiationframework.contracts.InformedCandidature;
import negotiation.negotiationframework.contracts.MatchingCandidature;
import negotiation.negotiationframework.contracts.ReallocationContract;
import negotiation.negotiationframework.rationality.AgentState;
import negotiation.negotiationframework.rationality.RationalCore;
import negotiation.negotiationframework.rationality.SimpleRationalAgent;
import dima.basicagentcomponents.AgentIdentifier;
import dima.introspectionbasedagents.services.BasicAgentCompetence;
import dima.introspectionbasedagents.services.UnrespectedCompetenceSyntaxException;

public class InformedCandidatureRationality<
PersonalState extends AgentState,
Contract extends MatchingCandidature>
extends BasicAgentCompetence<
SimpleRationalAgent<PersonalState, InformedCandidature<Contract>>>
implements RationalCore<PersonalState, InformedCandidature<Contract>>{
	private static final long serialVersionUID = 3012134209614654825L;

	public final RationalCore<PersonalState, Contract> referenceRationality;
	public final boolean optimiseWithBest;

	public InformedCandidatureRationality(
			final RationalCore<PersonalState, Contract> referenceRationality,
			final boolean optimiseWithBest) {
		super();
		this.referenceRationality = referenceRationality;
		this.optimiseWithBest = optimiseWithBest;
	}


	/*
	 * Delegated methods
	 */

	@Override
	public void setMySpecif(final PersonalState s,
			final InformedCandidature<Contract> c) {
		this.referenceRationality.setMySpecif(s, c.getCandidature());
	}

	//	public ActionSpec getMySimpleSpecif(final PersonalState s, final Contract c) {
	//		return this.referenceRationality.getMySpecif(s, c);
	//	}


	@Override
	public Double evaluatePreference(
			final Collection<InformedCandidature<Contract>> cs) {
		return this.referenceRationality.evaluatePreference(InformedCandidature.toCandidatures(cs));
	}

	@Override
	public void execute(final Collection<InformedCandidature<Contract>> contracts) {
		this.referenceRationality.execute(InformedCandidature.toCandidatures(contracts));
	}

	@Override
	public int getAllocationPreference(
			final Collection<InformedCandidature<Contract>> c1,
			final Collection<InformedCandidature<Contract>> c2) {
		if (this.optimiseWithBest){//optimisation informé (pour les agents)
			final Collection<Contract> consequentC1 = new ArrayList<Contract>();
			final Collection<Contract> consequentC2 = new ArrayList<Contract>();
			for (final InformedCandidature<Contract> c : c1) {
				consequentC1.addAll(c.getBestPossible(
						this.referenceRationality.getMyAgent().
						getMyAllocationPreferenceComparator()));
			}
			for (final InformedCandidature<Contract> c : c2) {
				consequentC2.addAll(c.getBestPossible(
						this.referenceRationality.getMyAgent().
						getMyAllocationPreferenceComparator()));
			}
			return this.referenceRationality.getAllocationPreference(consequentC1,consequentC2);

		} else {//optimisation réel
			final Collection<Contract> consequentC1 = new ArrayList<Contract>();
			final Collection<Contract> consequentC2 = new ArrayList<Contract>();
			for (final InformedCandidature<Contract> c : c1) {
				consequentC1.add(c.getCandidature());
			}
			for (final InformedCandidature<Contract> c : c2) {
				consequentC2.add(c.getCandidature());
			}
			return this.referenceRationality.getAllocationPreference(consequentC1,consequentC2);
		}
	}

//	public Comparator<ReallocationContract<Contract,ActionSpec>> getReferenceAllocationComparator(final PersonalState s) {
//		return new Comparator<Collection<Contract>>() {
//
//			@Override
//			public int compare(final Collection<Contract> c1, final Collection<Contract> c2) {
//				return InformedCandidatureRationality.this.referenceRationality.getAllocationPreference(s, c1, c2);
//			}
//		};
//	}

	public Comparator<ReallocationContract<Contract>> getReferenceAllocationComparator() {
		return new Comparator<ReallocationContract<Contract>>() {

			@Override
			public int compare(final ReallocationContract<Contract> c1, final ReallocationContract<Contract> c2) {
				return InformedCandidatureRationality.this.referenceRationality.getAllocationPreference(c1, c2);
			}
		};
	}
	/*
	 *
	 */

	@Override
	public boolean isActive() {
		return this.referenceRationality.isActive();
	}



	@Override
	public SimpleRationalAgent<PersonalState, InformedCandidature<Contract>> getMyAgent() {
		return (SimpleRationalAgent<PersonalState, InformedCandidature<Contract>>) this.referenceRationality.getMyAgent();
	}

	@Override
	public void setMyAgent(
			final SimpleRationalAgent<PersonalState, InformedCandidature<Contract>> ag) {
		this.referenceRationality.setMyAgent((SimpleRationalAgent<PersonalState, Contract>) ag);
	}

	@Override
	public void die() {
		this.referenceRationality.die();
	}

	@Override
	public AgentIdentifier getIdentifier() {
		return this.referenceRationality.getIdentifier();
	}

	@Override
	public void activateCompetence(final boolean active) {
		this.referenceRationality.activateCompetence(active);
	}
}
