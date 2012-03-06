package negotiation.horizon.negociatingagent;

import java.util.Collection;

import negotiation.negotiationframework.rationality.RationalCore;
import negotiation.negotiationframework.rationality.SimpleRationalAgent;
import dima.introspectionbasedagents.services.BasicAgentCompetence;

public class SubstrateNodeCore extends
	BasicAgentCompetence<SimpleRationalAgent<HorizonSpecification, SubstrateNodeState, HorizonContract>>
	implements
	RationalCore<HorizonSpecification, SubstrateNodeState, HorizonContract> {

    /**
     * Serial version identifier.
     */
    private static final long serialVersionUID = -4617793988428190194L;

    @Override
    public boolean IWantToNegotiate(SubstrateNodeState s) {
	// TODO Auto-generated method stub
	return false;
    }

    @Override
    public Double evaluatePreference(SubstrateNodeState s1) {
	// TODO Auto-generated method stub
	return null;
    }

    @Override
    public void execute(HorizonContract c) {
	// TODO Auto-generated method stub
	
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
