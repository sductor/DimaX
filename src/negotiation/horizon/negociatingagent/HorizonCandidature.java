package negotiation.horizon.negociatingagent;

import negotiation.negotiationframework.interaction.contracts.MatchingCandidature;
import negotiation.negotiationframework.interaction.contracts.ResourceIdentifier;
import dima.basicagentcomponents.AgentIdentifier;

public class HorizonCandidature extends MatchingCandidature<HorizonSpecification> {

	public HorizonCandidature(AgentIdentifier intiator, AgentIdentifier a,
			ResourceIdentifier r, long validityTime) {
		super(intiator, a, r, validityTime);
		// TODO Auto-generated constructor stub
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 5688344205746523199L;

	@Override
	public HorizonSpecification computeResultingState(AgentIdentifier id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <State extends HorizonSpecification> State computeResultingState(
			State s) {
		// TODO Auto-generated method stub
		return null;
	}

}
