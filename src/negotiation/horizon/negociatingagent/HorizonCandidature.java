package negotiation.horizon.negociatingagent;

import negotiation.negotiationframework.contracts.MatchingCandidature;
import negotiation.negotiationframework.contracts.ResourceIdentifier;
import dima.basicagentcomponents.AgentIdentifier;

public class HorizonCandidature extends MatchingCandidature<HorizonSpecification> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2602439825621634390L;

	public HorizonCandidature(AgentIdentifier intiator, AgentIdentifier a,
			ResourceIdentifier r, long validityTime) {
		super(intiator, a, r, validityTime);
		// TODO Auto-generated constructor stub
	}

	@Override
	public <State extends HorizonSpecification> State computeResultingState(
			State s) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public HorizonSpecification computeResultingState(AgentIdentifier id) {
		// TODO Auto-generated method stub
		return null;
	}
}
