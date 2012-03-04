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
			boolean optimiseWithBest) {
		super();
		this.referenceRationality = referenceRationality;
		this.optimiseWithBest = optimiseWithBest;
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

	public ActionSpec getMySimpleSpecif(PersonalState s, Contract c) {
		return referenceRationality.getMySpecif(s, c);
	}	

	@Override
	public void execute(final InformedCandidature<Contract, ActionSpec> c) {
		this.referenceRationality.execute(c.getCandidature());

	}

	@Override
	public int getAllocationPreference(final PersonalState s,
			final Collection<InformedCandidature<Contract, ActionSpec>> c1,
			final Collection<InformedCandidature<Contract, ActionSpec>> c2) {
		if (optimiseWithBest){//optimisation informé (pour les agents)
			final Collection<Contract> consequentC1 = new ArrayList<Contract>();
			final Collection<Contract> consequentC2 = new ArrayList<Contract>();
			for (final InformedCandidature<Contract, ActionSpec> c : c1){
				consequentC1.addAll(c.getBestPossible(
						this.referenceRationality.getMyAgent().
						getMyAllocationPreferenceComparator()));
			}	
			for (final InformedCandidature<Contract, ActionSpec> c : c2) {
				consequentC2.addAll(c.getBestPossible(
						this.referenceRationality.getMyAgent().
						getMyAllocationPreferenceComparator()));
			}
			return this.referenceRationality.getAllocationPreference(s,consequentC1,consequentC2);

		} else {//optimisation réel
			final Collection<Contract> consequentC1 = new ArrayList<Contract>();
			final Collection<Contract> consequentC2 = new ArrayList<Contract>();
			for (final InformedCandidature<Contract, ActionSpec> c : c1){
				consequentC1.add(c.getCandidature());
			}
			for (final InformedCandidature<Contract, ActionSpec> c : c2){
				consequentC2.add(c.getCandidature());
			}	
			return this.referenceRationality.getAllocationPreference(s,consequentC1,consequentC2);
		}
	}
	
	public Comparator<Collection<Contract>> getReferenceAllocationComparator(final PersonalState s) {
		return new Comparator<Collection<Contract>>() {

			@Override
			public int compare(Collection<Contract> c1, Collection<Contract> c2) {
				return referenceRationality.getAllocationPreference(s, c1, c2);
			}
		}; 
	}
	/*
	 * 
	 */

	@Override
	public boolean isActive() {
		return referenceRationality.isActive();
	}



	@Override
	public SimpleRationalAgent<ActionSpec, PersonalState, InformedCandidature<Contract, ActionSpec>> getMyAgent() {
		return (SimpleRationalAgent<ActionSpec, PersonalState, InformedCandidature<Contract, ActionSpec>>) referenceRationality.getMyAgent();
	}

	@Override
	public void setMyAgent(
			SimpleRationalAgent<ActionSpec, PersonalState, InformedCandidature<Contract, ActionSpec>> ag)
					throws UnrespectedCompetenceSyntaxException {
		referenceRationality.setMyAgent((SimpleRationalAgent<ActionSpec, PersonalState, Contract>) ag);
	}

	@Override
	public void die() {
		referenceRationality.die();
	}

	@Override
	public AgentIdentifier getIdentifier() {
		return referenceRationality.getIdentifier();
	}

	@Override
	public void setActive(boolean active) {
		referenceRationality.setActive(active);
	}
}
