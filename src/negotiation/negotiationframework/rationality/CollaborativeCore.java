package negotiation.negotiationframework.rationality;

import java.util.Collection;

import negotiation.negotiationframework.contracts.AbstractActionSpecification;
import negotiation.negotiationframework.contracts.AbstractContractTransition;
import dima.introspectionbasedagents.services.BasicAgentCompetence;

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
	private final RationalCore  myPersonalCore;

	//
	// Constructor
	//

	public CollaborativeCore(
			final AllocationSocialWelfares<ActionSpec, Contract> opt,
			final RationalCore rationality) {
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
	public AbstractActionSpecification getMySpecif(
			final AbstractActionSpecification s, final AbstractContractTransition c) {
		return this.myPersonalCore.getMySpecif(s, c);
	}

	@Override
	public void execute(final AbstractContractTransition c) {
		this.myPersonalCore.execute(c);
	}

	@Override
	public Double evaluatePreference(final AbstractActionSpecification s1) {
		return this.myPersonalCore.evaluatePreference(s1);
	}

	@Override
	public boolean IWantToNegotiate(final AbstractActionSpecification s) {
		return this.myPersonalCore.IWantToNegotiate(s);
	}




}
