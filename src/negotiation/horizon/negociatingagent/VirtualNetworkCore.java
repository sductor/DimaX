package negotiation.horizon.negociatingagent;

import java.util.Collection;

import negotiation.negotiationframework.rationality.RationalCore;
import negotiation.negotiationframework.rationality.SimpleRationalAgent;
import dima.introspectionbasedagents.services.BasicAgentCompetence;
import dima.introspectionbasedagents.services.UnrespectedCompetenceSyntaxException;

public class VirtualNetworkCore
	// extends BasicAgentCompetence<VirtualNetwork>
	extends
	BasicAgentCompetence<SimpleRationalAgent<HorizonSpecification, VirtualNetworkState, HorizonContract>>
	implements
	RationalCore<HorizonSpecification, VirtualNetworkState, HorizonContract> {

    /**
     * Serial version identifier.
     */
    private static final long serialVersionUID = -3189589813751237257L;

    @Override
    public boolean IWantToNegotiate(VirtualNetworkState s) {
	// TODO Auto-generated method stub
	return false;
    }

    @Override
    public Double evaluatePreference(VirtualNetworkState s1) {
	// TODO Auto-generated method stub
	return null;
    }

    @Override
    public void execute(HorizonContract c) {
	// TODO Auto-generated method stub

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

    @Override
    public void setMyAgent(
	    SimpleRationalAgent<HorizonSpecification, VirtualNetworkState, HorizonContract> ag)
	    throws UnrespectedCompetenceSyntaxException {
	// TODO Auto-generated method stub

    }

}
