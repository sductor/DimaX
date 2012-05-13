package negotiation.horizon.negotiatingagent;

import java.util.Collection;

import negotiation.negotiationframework.contracts.AbstractContractTransition.IncompleteContractException;
import negotiation.negotiationframework.rationality.RationalCore;
import negotiation.negotiationframework.rationality.SimpleRationalAgent;
import dima.introspectionbasedagents.services.BasicAgentCompetence;

public class VirtualNetworkCore
	extends
	BasicAgentCompetence<SimpleRationalAgent<HorizonSpecification, VirtualNetworkState, HorizonContract>>
	implements
	RationalCore<HorizonSpecification, VirtualNetworkState, HorizonContract> {

    /**
     * Serial version identifier.
     */
    private static final long serialVersionUID = -3189589813751237257L;

    @Override
    public Double evaluatePreference(Collection<HorizonContract> cs) {
	// TODO Auto-generated method stub
	return null;
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
	// TODO Auto-generated method stub
	return 0;
    }

    @Override
    public HorizonSpecification getMySpecif(VirtualNetworkState s,
	    HorizonContract c) {
	// TODO Auto-generated method stub
	return null;
    }

}
