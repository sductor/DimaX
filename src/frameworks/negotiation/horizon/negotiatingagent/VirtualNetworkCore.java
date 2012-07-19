package frameworks.negotiation.horizon.negotiatingagent;

import java.util.Collection;

import dima.introspectionbasedagents.services.BasicAgentCompetence;
import frameworks.negotiation.horizon.VirtualNetwork;
import frameworks.negotiation.horizon.contracts.HorizonContract;
import frameworks.negotiation.negotiationframework.SimpleNegotiatingAgent;
import frameworks.negotiation.negotiationframework.contracts.AbstractContractTransition.IncompleteContractException;
import frameworks.negotiation.negotiationframework.rationality.RationalCore;
import frameworks.negotiation.negotiationframework.rationality.SimpleRationalAgent;
import frameworks.negotiation.negotiationframework.rationality.SocialChoiceFunction.SocialChoiceType;

/**
 * This class is the RationalCore of the VirtualNetwork, it provides means to
 * evaluate preferences on the allocations and to execute them.
 * 
 * @author Vincent Letard
 */
public class VirtualNetworkCore
extends
BasicAgentCompetence<SimpleNegotiatingAgent<VirtualNetworkState, HorizonContract>>
implements RationalCore<SimpleNegotiatingAgent<VirtualNetworkState, HorizonContract>,VirtualNetworkState, HorizonContract> {

	/**
	 * The referent object to evaluate preferences.
	 */
	private final HorizonPreferenceFunction myChoiceFunction;

	/**
	 * Serial version identifier.
	 */
	private static final long serialVersionUID = -3189589813751237257L;

	/**
	 * @param socialWelfare
	 *            The type of of ordering of utilities.
	 */
	public VirtualNetworkCore(final SocialChoiceType socialWelfare) {
		this.myChoiceFunction = new HorizonPreferenceFunction(socialWelfare);
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
	 * the VirtualNetwork of this object (and all its VirtualNodes).
	 */
	@Override
	public void execute(final Collection<HorizonContract> contracts) {
		VirtualNetworkState myState = this.getMyAgent().getMyCurrentState();

		try {
			for (final HorizonContract c : contracts) {
				myState = c.computeResultingState(myState);
			}
		} catch (final IncompleteContractException e) {
			throw new RuntimeException(e);
		}
		this.getMyAgent().setNewState(myState);
	}

	/**
	 * This method relies on the HorizonPreferenceFunction to match the two
	 * specified collections of contracts.
	 */
	@Override
	public int getAllocationPreference(
			final Collection<HorizonContract> c1, final Collection<HorizonContract> c2) {
		for (final HorizonContract c : c1) {
			c.setInitialState(this.getMyAgent().getMyCurrentState());
		}
		for (final HorizonContract c : c2) {
			c.setInitialState(this.getMyAgent().getMyCurrentState());

		}
		return this.myChoiceFunction.getSocialPreference(c1, c2);
	}

	/**
	 * Sets the current specification (VirtualNode to be allocated) to the
	 * specified contract.
	 */
	@Override
	public void setMySpecif(final VirtualNetworkState s, final HorizonContract c) {
		try {
			c.setSpecification(c.getSpecificationOf(s.getMyAgentIdentifier()));
		} catch (final IncompleteContractException e) {
			throw new RuntimeException(e);
		}
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