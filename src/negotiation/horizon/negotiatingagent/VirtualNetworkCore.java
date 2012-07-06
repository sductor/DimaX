package negotiation.horizon.negotiatingagent;

import java.util.Collection;

import negotiation.negotiationframework.contracts.AbstractContractTransition.IncompleteContractException;
import negotiation.negotiationframework.rationality.RationalCore;
import negotiation.negotiationframework.rationality.SimpleRationalAgent;
import negotiation.negotiationframework.rationality.SocialChoiceFunction.SocialChoiceType;
import dima.introspectionbasedagents.services.BasicAgentCompetence;
import dima.introspectionbasedagents.services.UnrespectedCompetenceSyntaxException;

public class VirtualNetworkCore
	extends
	BasicAgentCompetence<SimpleRationalAgent<HorizonSpecification, VirtualNetworkState, HorizonContract>>
	implements
	RationalCore<HorizonSpecification, VirtualNetworkState, HorizonContract> {

    private final HorizonPreferenceFunction myChoiceFunction;

    /**
     * Serial version identifier.
     */
    private static final long serialVersionUID = -3189589813751237257L;

    public VirtualNetworkCore(final SocialChoiceType socialWelfare)
	    throws UnrespectedCompetenceSyntaxException {
	this.myChoiceFunction = new HorizonPreferenceFunction(socialWelfare);
    }

    @Override
    public Double evaluatePreference(Collection<HorizonContract> cs) {
	return this.myChoiceFunction.getUtility(cs);
    }

    @Override
    public void execute(Collection<HorizonContract> contracts) {
	VirtualNetworkState myState = this.getMyAgent().getMyCurrentState();

	try {
	    for (HorizonContract c : contracts) {
		myState = c.computeResultingState(myState);
	    }
	} catch (IncompleteContractException e) {
	    throw new RuntimeException(e);
	}
	this.getMyAgent().setNewState(myState);
    }

    @Override
    public int getAllocationPreference(VirtualNetworkState s,
	    Collection<HorizonContract> c1, Collection<HorizonContract> c2) {
	return this.myChoiceFunction.getSocialPreference(c1, c2);
    }

    @Override
    public HorizonSpecification computeMySpecif(VirtualNetworkState s,
	    HorizonContract c) {
	try {
	    return c.getSpecificationOf(s.getMyAgentIdentifier());
	} catch (IncompleteContractException e) {
	    throw new RuntimeException(e);
	}
    }
}
