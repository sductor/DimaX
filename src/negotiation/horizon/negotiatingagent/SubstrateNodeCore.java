package negotiation.horizon.negotiatingagent;

import java.util.Collection;

import negotiation.negotiationframework.contracts.ReallocationContract;
import negotiation.negotiationframework.contracts.AbstractContractTransition.IncompleteContractException;
import negotiation.negotiationframework.rationality.RationalCore;
import negotiation.negotiationframework.rationality.SimpleRationalAgent;
import dima.introspectionbasedagents.services.BasicAgentCompetence;

public class SubstrateNodeCore
	extends
	BasicAgentCompetence<SimpleRationalAgent<HorizonParameters<HorizonIdentifier>, SubstrateNodeState, ReallocationContract<HorizonCandidature, HorizonParameters<HorizonIdentifier>>>>
	implements
	RationalCore<HorizonParameters<HorizonIdentifier>, SubstrateNodeState, ReallocationContract<HorizonCandidature, HorizonParameters<HorizonIdentifier>>> {

    
    
    /**
     * Serial version identifier.
     */
    private static final long serialVersionUID = -4617793988428190194L;

    @Override
    public Double evaluatePreference(
	    Collection<ReallocationContract<HorizonCandidature, HorizonParameters<HorizonIdentifier>>> cs) {
	return 
    }

    @Override
    public void execute(
	    Collection<ReallocationContract<HorizonCandidature, HorizonParameters<HorizonIdentifier>>> contracts) {
	SubstrateNodeState myState = this.getMyAgent().getMyCurrentState();

	try {
	    for (ReallocationContract<HorizonCandidature, HorizonParameters> c : contracts) {
		myState = c.computeResultingState(myState);
	    }
	} catch (IncompleteContractException e) {
	    throw new RuntimeException(e);
	}
	this.getMyAgent().setNewState(myState);
    }

    @Override
    public int getAllocationPreference(
	    SubstrateNodeState s,
	    Collection<ReallocationContract<HorizonCandidature, HorizonParameters>> c1,
	    Collection<ReallocationContract<HorizonCandidature, HorizonParameters>> c2) {
	// TODO Auto-generated method stub
	return 0;
    }

    @Override
    public HorizonParameters getMySpecif(SubstrateNodeState s,
	    ReallocationContract<HorizonCandidature, HorizonParameters> c) {
	try {
	    return c.getSpecificationOf(this.getIdentifier());
	} catch (IncompleteContractException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	    return null;
	}
    }

}
