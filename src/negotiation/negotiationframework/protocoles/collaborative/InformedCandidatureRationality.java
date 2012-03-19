package negotiation.negotiationframework.protocoles.collaborative;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;

import negotiation.negotiationframework.contracts.AbstractActionSpecification;
import negotiation.negotiationframework.contracts.MatchingCandidature;
import negotiation.negotiationframework.rationality.RationalCore;
import negotiation.negotiationframework.rationality.SimpleRationalAgent;
import dima.basicagentcomponents.AgentIdentifier;
import dima.introspectionbasedagents.services.BasicAgentCompetence;
import dima.introspectionbasedagents.services.UnrespectedCompetenceSyntaxException;

public class InformedCandidatureRationality<
ActionSpec extends AbstractActionSpecification,
PersonalState extends ActionSpec,
Contract extends MatchingCandidature<ActionSpec>>
extends BasicAgentCompetence<
SimpleRationalAgent<ActionSpec, PersonalState, InformedCandidature<Contract,ActionSpec>>>
implements RationalCore<ActionSpec, PersonalState, InformedCandidature<Contract,ActionSpec>>{
	private static final long serialVersionUID = 3012134209614654825L;

	public final RationalCore<ActionSpec, PersonalState, Contract> referenceRationality;
	public final boolean optimiseWithBest;

	public InformedCandidatureRationality(
			final RationalCore<ActionSpec, PersonalState, Contract> referenceRationality,
			final boolean optimiseWithBest) {
		super();
		this.referenceRationality = referenceRationality;
		this.optimiseWithBest = optimiseWithBest;
	}


	/*
	 * Delegated methods
	 */


	@Override
	public boolean IWantToNegotiate(final PersonalState s) {
		return this.referenceRationality.IWantToNegotiate(s);
	}

	/*
	 * 
	 */

	@Override
	public ActionSpec getMySpecif(final PersonalState s,
			final InformedCandidature<Contract, ActionSpec> c) {
		return this.referenceRationality.getMySpecif(s, c.getCandidature());
	}

//	public ActionSpec getMySimpleSpecif(final PersonalState s, final Contract c) {
//		return this.referenceRationality.getMySpecif(s, c);
//	}


	@Override
	public Double evaluatePreference(
			Collection<InformedCandidature<Contract, ActionSpec>> cs) {
		return this.referenceRationality.evaluatePreference(InformedCandidature.toCandidatures(cs));
	}
	
	@Override
	public void execute(final Collection<InformedCandidature<Contract, ActionSpec>> contracts) {
		this.referenceRationality.execute(InformedCandidature.toCandidatures(contracts));
	}

	@Override
	public int getAllocationPreference(final PersonalState s,
			final Collection<InformedCandidature<Contract, ActionSpec>> c1,
			final Collection<InformedCandidature<Contract, ActionSpec>> c2) {
		if (this.optimiseWithBest){//optimisation informé (pour les agents)
			final Collection<Contract> consequentC1 = new ArrayList<Contract>();
			final Collection<Contract> consequentC2 = new ArrayList<Contract>();
			for (final InformedCandidature<Contract, ActionSpec> c : c1)
				consequentC1.addAll(c.getBestPossible(
						this.referenceRationality.getMyAgent().
						getMyAllocationPreferenceComparator()));
					for (final InformedCandidature<Contract, ActionSpec> c : c2)
						consequentC2.addAll(c.getBestPossible(
								this.referenceRationality.getMyAgent().
								getMyAllocationPreferenceComparator()));
							return this.referenceRationality.getAllocationPreference(s,consequentC1,consequentC2);

		} else {//optimisation réel
			final Collection<Contract> consequentC1 = new ArrayList<Contract>();
			final Collection<Contract> consequentC2 = new ArrayList<Contract>();
			for (final InformedCandidature<Contract, ActionSpec> c : c1)
				consequentC1.add(c.getCandidature());
					for (final InformedCandidature<Contract, ActionSpec> c : c2)
						consequentC2.add(c.getCandidature());
							return this.referenceRationality.getAllocationPreference(s,consequentC1,consequentC2);
		}
	}

	public Comparator<Collection<Contract>> getReferenceAllocationComparator(final PersonalState s) {
		return new Comparator<Collection<Contract>>() {

			@Override
			public int compare(final Collection<Contract> c1, final Collection<Contract> c2) {
				return InformedCandidatureRationality.this.referenceRationality.getAllocationPreference(s, c1, c2);
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
	public SimpleRationalAgent<ActionSpec, PersonalState, InformedCandidature<Contract, ActionSpec>> getMyAgent() {
		return (SimpleRationalAgent<ActionSpec, PersonalState, InformedCandidature<Contract, ActionSpec>>) this.referenceRationality.getMyAgent();
	}

	@Override
	public void setMyAgent(
			final SimpleRationalAgent<ActionSpec, PersonalState, InformedCandidature<Contract, ActionSpec>> ag)
					throws UnrespectedCompetenceSyntaxException {
		this.referenceRationality.setMyAgent((SimpleRationalAgent<ActionSpec, PersonalState, Contract>) ag);
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
	public void setActive(final boolean active) {
		this.referenceRationality.setActive(active);
	}
}
