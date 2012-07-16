package negotiation.negotiationframework.rationality;

import java.util.Collection;

import negotiation.negotiationframework.contracts.AbstractActionSpecif;
import negotiation.negotiationframework.contracts.AbstractContractTransition;
import negotiation.negotiationframework.contracts.ReallocationContract;
import negotiation.negotiationframework.contracts.AbstractContractTransition.IncompleteContractException;
import dima.basicagentcomponents.AgentIdentifier;
import dima.introspectionbasedagents.services.BasicAgentCompetence;
import dima.introspectionbasedagents.services.UnrespectedCompetenceSyntaxException;

public class AltruistRationalCore<
PersonalState extends AgentState,
Contract extends AbstractContractTransition>
extends BasicAgentCompetence<SimpleRationalAgent<PersonalState,Contract>>
implements RationalCore<PersonalState, Contract>{
	private static final long serialVersionUID = -2882287744826409737L;

	//
	// Fields
	//

	private final SocialChoiceFunction<Contract> myOptimiser;
	private final RationalCore<PersonalState, Contract>  myPersonalCore;

	//
	// Constructor
	//

	public AltruistRationalCore(
			final SocialChoiceFunction<Contract> opt,
			final RationalCore<PersonalState, Contract> rationality) {
		this.myOptimiser = opt;
		this.myPersonalCore= rationality;
	}

	//
	// Methods
	//

	@Override
	public final int getAllocationPreference(
			final Collection<Contract> c1,
			final Collection<Contract> c2) {
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
	public void setMySpecif(final PersonalState s, final Contract c) {
		this.myPersonalCore.setMySpecif(s, c);
	}

	@Override
	public void execute(final Collection<Contract> contracts) {
		this.myPersonalCore.execute(contracts);
	}

	/*
	 *
	 */

	@Override
	public boolean isActive() {
		return this.myPersonalCore.isActive();
	}

	@Override
	public SimpleRationalAgent<PersonalState, Contract> getMyAgent() {
		return this.myPersonalCore.getMyAgent();
	}

	@Override
	public void setMyAgent(
			final SimpleRationalAgent<PersonalState, Contract> ag) {
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

	//
	// Assertion test
	//

	public static  <Contract extends AbstractContractTransition> boolean verifyStateConsistency(
			SimpleRationalAgent<?, Contract> myAgent,
			final Collection<Contract> c1,
			final Collection<Contract> c2){
		try {
		for (final Contract ca : c1) {
			for (final Contract cb : c2) {
				assert ca.getInitialState(myAgent.getIdentifier()).equals(cb.getInitialState(myAgent.getIdentifier()));
			}
		}
		for (final Contract ca : c1) {
			assert ca.getInitialState(myAgent.getIdentifier()).equals(myAgent.getMyCurrentState());
		}
		return true;
		} catch (IncompleteContractException c){
			assert false;
			return true;
		}
	}

}
