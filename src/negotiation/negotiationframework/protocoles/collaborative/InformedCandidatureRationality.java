package negotiation.negotiationframework.protocoles.collaborative;

import java.util.ArrayList;
import java.util.Collection;

import negotiation.negotiationframework.contracts.AbstractActionSpecification;
import negotiation.negotiationframework.contracts.MatchingCandidature;
import negotiation.negotiationframework.rationality.RationalCore;
import negotiation.negotiationframework.rationality.SimpleRationalAgent;
import dima.introspectionbasedagents.services.BasicAgentCompetence;

public class InformedCandidatureRationality<
ActionSpec extends AbstractActionSpecification,
PersonalState extends ActionSpec,
Contract extends MatchingCandidature<ActionSpec>>
extends BasicAgentCompetence<
SimpleRationalAgent<ActionSpec, PersonalState, InformedCandidature<Contract,ActionSpec>>>
implements RationalCore<ActionSpec, PersonalState, InformedCandidature<Contract,ActionSpec>>{

	/**
	 * 
	 */
	private static final long serialVersionUID = 3012134209614654825L;
	public RationalCore<ActionSpec, PersonalState, Contract> referenceRationality;

	public InformedCandidatureRationality(
			final RationalCore<ActionSpec, PersonalState, Contract> referenceRationality) {
		super();
		this.referenceRationality = referenceRationality;
	}

	/*
	 * Delegated methods
	 */

	@Override
	public Double evaluatePreference(final PersonalState s1) {
		return this.referenceRationality.evaluatePreference(s1);
	}

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

	@Override
	public void execute(final InformedCandidature<Contract, ActionSpec> c) {
		this.referenceRationality.execute(c.getCandidature());

	}

	@Override
	public int getAllocationPreference(final PersonalState s,
			final Collection<InformedCandidature<Contract, ActionSpec>> c1,
			final Collection<InformedCandidature<Contract, ActionSpec>> c2) {
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
	}

}
