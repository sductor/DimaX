package negotiation.horizon.negotiatingagent;

import java.util.Collection;

import negotiation.negotiationframework.contracts.ReallocationContract;
import dima.basicagentcomponents.AgentIdentifier;

public class HorizonContract extends
	ReallocationContract<HorizonCandidature, HorizonSpecification> {

    /**
     * Serial version identifier.
     */
    private static final long serialVersionUID = 7804749910350934001L;

    public HorizonContract(AgentIdentifier creator, HorizonCandidature[] actions) {
	super(creator, actions);
    }

    public HorizonContract(AgentIdentifier creator,
	    Collection<HorizonCandidature> actions) {
	super(creator, actions);
    }

}
