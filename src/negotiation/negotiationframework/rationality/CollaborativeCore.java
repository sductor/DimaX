package negotiation.negotiationframework.rationality;

import java.util.Collection;

import negotiation.negotiationframework.contracts.AbstractActionSpecification;
import negotiation.negotiationframework.contracts.AbstractContractTransition;
import dima.basicagentcomponents.AgentIdentifier;
import dima.introspectionbasedagents.CompetentComponent;
import dima.introspectionbasedagents.services.BasicAgentCompetence;
import dima.introspectionbasedagents.services.UnrespectedCompetenceSyntaxException;

public class CollaborativeCore<
ActionSpec extends AbstractActionSpecification,
PersonalState extends ActionSpec,
Contract extends AbstractContractTransition<ActionSpec>>
extends BasicAgentCompetence<SimpleRationalAgent<ActionSpec,PersonalState,Contract>>
implements RationalCore<ActionSpec, PersonalState, Contract>{

	//
	// Fields
	//

	/**
	 * 
	 */
	private static final long serialVersionUID = -2882287744826409737L;
	private final AllocationSocialWelfares<ActionSpec, Contract> myOptimiser;
	private final RationalCore<ActionSpec, PersonalState, Contract>  myPersonalCore;

	//
	// Constructor
	//

	public CollaborativeCore(
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
		for (final Contract c : c1)
			c.setSpecification(s);	for (final Contract c : c2)
				c.setSpecification(s);
					return this.myOptimiser.getSocialPreference(c1, c2);
	}

	//
	// Delegated methods
	//

	@Override
	public ActionSpec getMySpecif(PersonalState s, Contract c) {
		return myPersonalCore.getMySpecif(s, c);
	}

	@Override
	public void execute(Contract c) {
		myPersonalCore.execute(c);
	}

	@Override
	public Double evaluatePreference(PersonalState s1) {
		return myPersonalCore.evaluatePreference(s1);
	}

	@Override
	public boolean IWantToNegotiate(PersonalState s) {
		return myPersonalCore.IWantToNegotiate(s);
	}

	/*
	 * 
	 */
	

	@Override
	public boolean isActive() {
		return myPersonalCore.isActive();
	}

	@Override
	public SimpleRationalAgent<ActionSpec, PersonalState, Contract> getMyAgent() {
		return myPersonalCore.getMyAgent();
	}

	@Override
	public void setMyAgent(
			SimpleRationalAgent<ActionSpec, PersonalState, Contract> ag)
			throws UnrespectedCompetenceSyntaxException {
		myPersonalCore.setMyAgent(ag);
	}

	@Override
	public void die() {
		myPersonalCore.die();
	}

	@Override
	public AgentIdentifier getIdentifier() {
		return myPersonalCore.getIdentifier();
	}

	@Override
	public void setActive(boolean active) {
		myPersonalCore.setActive(active);
	}
}
