package negotiation.horizon.negotiatingagent;

import java.util.Collection;

import negotiation.horizon.experimentation.SubstrateNode;
import negotiation.negotiationframework.contracts.ReallocationContract;
import negotiation.negotiationframework.contracts.AbstractContractTransition.IncompleteContractException;
import negotiation.negotiationframework.rationality.RationalCore;
import negotiation.negotiationframework.rationality.SimpleRationalAgent;
import negotiation.negotiationframework.rationality.SocialChoiceFunction.SocialChoiceType;
import dima.introspectionbasedagents.services.BasicAgentCompetence;

public class SubstrateNodeCore
	extends
	BasicAgentCompetence<SimpleRationalAgent<HorizonSpecification, SubstrateNodeState, HorizonContract>>
	implements
	RationalCore<HorizonSpecification, SubstrateNodeState, HorizonContract> {

    // private final _SubstrateChoiceFunction myChoiceFunction;

    /**
     * Serial version identifier.
     */
    private static final long serialVersionUID = -4617793988428190194L;

    private final HorizonPreferenceFunction myChoiceFunction;

    public SubstrateNodeCore(final SocialChoiceType socialWelfare) {
	this.myChoiceFunction = new HorizonPreferenceFunction(socialWelfare);
    }

    @Override
    public SubstrateNode getMyAgent() {
	return (SubstrateNode) super.getMyAgent();
    }

    @Override
    public SubstrateNodeIdentifier getIdentifier() {
	return (SubstrateNodeIdentifier) super.getIdentifier();
    }

    @Override
    public SubstrateNodeSpecification computeMySpecif(
	    final SubstrateNodeState s, final HorizonContract c) {
	return this.computeMySpecif();
    }

    public SubstrateNodeSpecification computeMySpecif() {
	return new SubstrateNodeSpecification(this.getIdentifier(), this
		.getMyAgent().myMeasureHandler.performMeasures());
    }

    @Override
    public Double evaluatePreference(Collection<HorizonContract> cs) {
	return this.myChoiceFunction.getUtility(cs);
	// green, nb de requêtes acceptées, qos
	// je n'ai fait qu'un seul critère (green) car l'agrégation dépend d'une
	// expertise dont je ne dispose pas
    }

    @Override
    public void execute(Collection<HorizonContract> contracts) {
	try {
	    this.getMyAgent().setNewState(
		    ReallocationContract.computeResultingState(this
			    .getMyAgent().getMyCurrentState(), contracts));
	} catch (IncompleteContractException e) {
	    throw new RuntimeException(e);
	}
    }

    @Override
    public int getAllocationPreference(final SubstrateNodeState s,
	    final Collection<HorizonContract> c1,
	    final Collection<HorizonContract> c2) {
	return this.myChoiceFunction.getSocialPreference(c1, c2);
    }
}
