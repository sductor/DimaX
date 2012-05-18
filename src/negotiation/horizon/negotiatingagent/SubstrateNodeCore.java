package negotiation.horizon.negotiatingagent;

import java.util.Collection;

import negotiation.horizon.experimentation.SubstrateNode;
import negotiation.negotiationframework.contracts.ReallocationContract;
import negotiation.negotiationframework.contracts.AbstractContractTransition.IncompleteContractException;
import negotiation.negotiationframework.rationality.AgentState;
import negotiation.negotiationframework.rationality.RationalCore;
import negotiation.negotiationframework.rationality.SimpleRationalAgent;
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
		.getMyAgent().myMeasureHandler.getMeasurableParameters());
    }

    @Override
    public Double evaluatePreference(Collection<HorizonContract> cs) {
	int preference = 0;
	Collection<AgentState> resultingAlloc;
	try {
	    resultingAlloc = ReallocationContract.getResultingAllocation(cs);
	} catch (IncompleteContractException e) {
	    throw new RuntimeException(e);
	}
	for (AgentState s : resultingAlloc) {
	    if (s instanceof SubstrateNodeState
		    && ((SubstrateNodeState) s).isEmpty())
		preference++;
	}
	return new Double(preference);
    }

    @Override
    public void execute(Collection<HorizonContract> contracts) {
	try {
	    this.getMyAgent().setNewState(ReallocationContract.computeResultingState(this.getMyAgent().getMyCurrentState(), contracts));
	} catch (IncompleteContractException e) {
	    throw new RuntimeException(e);
	}
    }

    @Override
    public int getAllocationPreference(final SubstrateNodeState s,
	    final Collection<HorizonContract> c1,
	    final Collection<HorizonContract> c2) {
	final SubstrateNodeSpecification mySpecif = this.computeMySpecif();
	for (final HorizonContract c : c1) {
	    c.setSpecificationNInitialState(s, mySpecif);
	}
	for (final HorizonContract c : c2) {
	    c.setSpecificationNInitialState(s, mySpecif);
	}

	// this.logMonologue("Preference : " + pref + " for \n " + c1 + "\n" +
	// c2, SocialChoiceFunction.log_socialWelfareOrdering);
	return this.evaluatePreference(c1).compareTo(
		this.evaluatePreference(c2));
    }
}
