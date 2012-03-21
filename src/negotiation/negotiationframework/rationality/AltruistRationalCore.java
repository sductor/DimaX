package negotiation.negotiationframework.rationality;

import java.util.Collection;

import negotiation.negotiationframework.contracts.AbstractActionSpecification;
import negotiation.negotiationframework.contracts.AbstractContractTransition;
import dima.basicagentcomponents.AgentIdentifier;
import dima.introspectionbasedagents.services.BasicAgentCompetence;
import dima.introspectionbasedagents.services.UnrespectedCompetenceSyntaxException;

public class AltruistRationalCore<
ActionSpec extends AbstractActionSpecification,
PersonalState extends ActionSpec,
Contract extends AbstractContractTransition<ActionSpec>>
extends BasicAgentCompetence<SimpleRationalAgent<ActionSpec,PersonalState,Contract>>
implements RationalCore<ActionSpec, PersonalState, Contract>{
	private static final long serialVersionUID = -2882287744826409737L;

	//
	// Fields
	//

	private final AllocationSocialWelfares<ActionSpec, Contract> myOptimiser;
	private final RationalCore<ActionSpec, PersonalState, Contract>  myPersonalCore;

	//
	// Constructor
	//

	public AltruistRationalCore(
			final AllocationSocialWelfares<ActionSpec, Contract> opt,
			final RationalCore<ActionSpec, PersonalState, Contract> rationality) {
		this.myOptimiser = opt;
		this.myPersonalCore= rationality;
	}

	//
	// Methods
	//

	@Override
	public final int getAllocationPreference(final PersonalState s,
			final Collection<Contract> c1,
			final Collection<Contract> c2) {
		for (final Contract c : c1) {
			c.setSpecification(s);
		}	for (final Contract c : c2) {
			c.setSpecification(s);
		}
		return this.myOptimiser.getSocialPreference(c1, c2);
	}

	@Override
	public Double evaluatePreference(final Collection<Contract> c) {
		return this.myOptimiser.getUtility(c);
	}
	
	//
	// Delegated methods
	//

	@Override
	public ActionSpec getMySpecif(final PersonalState s, final Contract c) {
		return this.myPersonalCore.getMySpecif(s, c);
	}

	@Override
	public void execute(final Collection<Contract> contracts) {
		this.myPersonalCore.execute(contracts);
	}

	/*
	 * 
	 */

	@Override
	public boolean competenceIsActive() {
		return this.myPersonalCore.competenceIsActive();
	}

	@Override
	public SimpleRationalAgent<ActionSpec, PersonalState, Contract> getMyAgent() {
		return this.myPersonalCore.getMyAgent();
	}

	@Override
	public void setMyAgent(
			final SimpleRationalAgent<ActionSpec, PersonalState, Contract> ag)
					throws UnrespectedCompetenceSyntaxException {
		this.myPersonalCore.setMyAgent(ag);
	}

	@Override
	public void die() {
		this.myPersonalCore.die();
	}

	@Override
	public AgentIdentifier getIdentifier() {
		return this.myPersonalCore.getIdentifier();
	}

	@Override
	public void activateCompetence(final boolean active) {
		this.myPersonalCore.activateCompetence(active);
	}
}
