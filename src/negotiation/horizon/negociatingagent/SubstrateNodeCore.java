package negotiation.horizon.negociatingagent;

import java.util.Collection;

import negotiation.negotiationframework.contracts.AbstractContractTransition.IncompleteContractException;
import negotiation.negotiationframework.rationality.RationalCore;
import negotiation.negotiationframework.rationality.SimpleRationalAgent;
import dima.introspectionbasedagents.services.BasicAgentCompetence;

public class SubstrateNodeCore
	extends
	BasicAgentCompetence<SimpleRationalAgent<HorizonSpecification, SubstrateNodeState, HorizonContract>>
	implements
	RationalCore<HorizonSpecification, SubstrateNodeState, HorizonContract> {

    /**
     * Serial version identifier.
     */
    private static final long serialVersionUID = -4617793988428190194L;

    @Override
    public Double evaluatePreference(Collection<HorizonContract> cs) {
	// TODO Auto-generated method stub
	return null;
    }

    @Override
    public void execute(Collection<HorizonContract> contracts) {
	SubstrateNodeState myState = this.getMyAgent().getMyCurrentState();

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
    public int getAllocationPreference(SubstrateNodeState s,
	    Collection<HorizonContract> c1, Collection<HorizonContract> c2) {
	// TODO Auto-generated method stub
	return 0;
    }

    @Override
    public HorizonSpecification getMySpecif(SubstrateNodeState s,
	    HorizonContract c) {
	// TODO Auto-generated method stub
	return null;
    }

}
