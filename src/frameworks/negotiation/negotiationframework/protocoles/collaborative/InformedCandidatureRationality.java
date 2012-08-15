package frameworks.negotiation.negotiationframework.protocoles.collaborative;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;

import dima.basicagentcomponents.AgentIdentifier;
import dima.introspectionbasedagents.services.BasicAgentCompetence;
import frameworks.negotiation.negotiationframework.contracts.MatchingCandidature;
import frameworks.negotiation.negotiationframework.contracts.ReallocationContract;
import frameworks.negotiation.negotiationframework.rationality.AgentState;
import frameworks.negotiation.negotiationframework.rationality.RationalAgent;
import frameworks.negotiation.negotiationframework.rationality.RationalCore;
import frameworks.negotiation.negotiationframework.rationality.SimpleRationalAgent;

public class InformedCandidatureRationality<
PersonalState extends AgentState,
Contract extends MatchingCandidature>
extends BasicAgentCompetence<RationalAgent<PersonalState, InformedCandidature<Contract>>>
implements RationalCore<RationalAgent<PersonalState, InformedCandidature<Contract>>,PersonalState, InformedCandidature<Contract>> {
	private static final long serialVersionUID = 3012134209614654825L;

	public final RationalCore<? extends SimpleRationalAgent,PersonalState, Contract> referenceRationality;
	public final boolean optimiseWithBest;

	public InformedCandidatureRationality(
			final RationalCore<? extends SimpleRationalAgent,PersonalState, Contract> referenceRationality,
			final boolean optimiseWithBest) {
		super();
		this.referenceRationality = referenceRationality;
		this.optimiseWithBest = optimiseWithBest;
	}


	/*
	 * Delegated methods
	 */

	@Override
	public boolean iObserveMyRessourceChanges() {
		return this.referenceRationality.iObserveMyRessourceChanges();
	}
	@Override
	public boolean iMemorizeMyRessourceState() {
		return this.referenceRationality.iMemorizeMyRessourceState();
	}


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

//	@Override
//	public boolean isActive() {
//		return super.isActive() && this.referenceRationality.isActive();
//	}



	@Override
	public RationalAgent<PersonalState, InformedCandidature<Contract>> getMyAgent() {
		return this.referenceRationality.getMyAgent();
	}

	@Override
	public void setMyAgent(
			final RationalAgent<PersonalState, InformedCandidature<Contract>> ag) {
		((RationalCore<SimpleRationalAgent<PersonalState, Contract>,PersonalState, Contract>)this.referenceRationality).
		setMyAgent((SimpleRationalAgent<PersonalState, Contract>) ag);
	}

	@Override
	public void die() {
		this.referenceRationality.die();
	}

	@Override
	public AgentIdentifier getIdentifier() {
		return this.referenceRationality.getIdentifier();
	}

//	@Override
//	public void setActive(final boolean active) {
//		this.referenceRationality.setActive(active);
//	}
}
