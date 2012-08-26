package frameworks.horizon.negotiatingagent;

import java.util.Collection;

import dima.introspectionbasedagents.services.BasicAgentCompetence;
import frameworks.horizon.SubstrateNode;
import frameworks.horizon.contracts.HorizonContract;
import frameworks.horizon.contracts.SubstrateNodeSpecification;
import frameworks.negotiation.SimpleNegotiatingAgent;
import frameworks.negotiation.contracts.ReallocationContract;
import frameworks.negotiation.contracts.AbstractContractTransition.IncompleteContractException;
import frameworks.negotiation.rationality.RationalCore;
import frameworks.negotiation.rationality.SimpleRationalAgent;
import frameworks.negotiation.rationality.SocialChoiceFunction.SocialChoiceType;

/**
 * This class is the RationalCore of the SubstrateNode, it provides means to
 * evaluate preferences on the allocations and to execute them.
 * 
 * @author Vincent Letard
 */
public class SubstrateNodeCore
extends
BasicAgentCompetence<SimpleNegotiatingAgent<SubstrateNodeState, HorizonContract>>
implements RationalCore<SimpleNegotiatingAgent<SubstrateNodeState, HorizonContract>,SubstrateNodeState, HorizonContract> {

	/**
	 * Serial version identifier.
	 */
	private static final long serialVersionUID = -4617793988428190194L;

	/**
	 * The referent object to evaluate preferences.
	 */
	private final HorizonPreferenceFunction myChoiceFunction;

	/**
	 * @param socialWelfare
	 *            The type of of ordering of utilities.
	 */
	public SubstrateNodeCore(final SocialChoiceType socialWelfare) {
		this.myChoiceFunction = new HorizonPreferenceFunction(socialWelfare);
	}

	/**
	 * SubstrateNode of this SubstrateNodeCore.
	 */
	@Override
	public SubstrateNode getMyAgent() {
		return (SubstrateNode) super.getMyAgent();
	}

	/**
	 * Identifier of the SubstrateNode of this SubstrateNodeCore.
	 */
	@Override
	public SubstrateNodeIdentifier getIdentifier() {
		return (SubstrateNodeIdentifier) super.getIdentifier();
	}

	/**
	 * Sets the current specification (measured level of service) to the
	 * specified contract.
	 */
	@Override
	public void setMySpecif(final SubstrateNodeState s, final HorizonContract c) {
		c.setSpecification(new SubstrateNodeSpecification(this.getIdentifier(),
				this.getMyAgent().myMeasureHandler.performMeasures()));
	}

	/**
	 * This method relies on the HorizonPreferenceFunction to give a utility
	 * evaluation for the specified collection of contracts.
	 */
	@Override
	public Double evaluatePreference(final Collection<HorizonContract> cs) {
		return this.myChoiceFunction.getUtility(cs);
	}

	/**
	 * Apply all changes pending with the specified collection of contracts to
	 * the SubstrateNode of this object.
	 */
	@Override
	public void execute(final Collection<HorizonContract> contracts) {
		try {
			this.getMyAgent().setNewState(
					ReallocationContract.computeResultingState(this
							.getMyAgent().getMyCurrentState(), contracts));
		} catch (final IncompleteContractException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * This method relies on the HorizonPreferenceFunction to match the two
	 * specified collections of contracts.
	 */
	@Override
	public int getAllocationPreference(
			final Collection<HorizonContract> c1,
			final Collection<HorizonContract> c2) {
		for (final HorizonContract c : c1) {
			c.setInitialState(this.getMyAgent().getMyCurrentState());
		}
		for (final HorizonContract c : c2) {
			c.setInitialState(this.getMyAgent().getMyCurrentState());

		}
		return this.myChoiceFunction.getSocialPreference(c1, c2);
	}

	@Override
	public boolean iObserveMyRessourceChanges() {
		throw new RuntimeException("todo");
	}

	@Override
	public boolean iMemorizeMyRessourceState() {
		throw new RuntimeException("todo");
	}
}
