package negotiation.faulttolerance.collaborativecandidature;

import dima.basicagentcomponents.AgentIdentifier;
import dima.introspectionbasedagents.services.UnrespectedCompetenceSyntaxException;
import negotiation.faulttolerance.negotiatingagent.ReplicaState;
import negotiation.faulttolerance.negotiatingagent.ReplicationCandidature;
import negotiation.negotiationframework.contracts.InformedCandidature;
import negotiation.negotiationframework.contracts.ResourceIdentifier;
import negotiation.negotiationframework.protocoles.AtMostKCandidaturesProposer;

public class CollaborativeCandidatureProposer
extends AtMostKCandidaturesProposer<ReplicaState, InformedCandidature<ReplicationCandidature>>{
	private static final long serialVersionUID = 4899697150539667541L;

	public CollaborativeCandidatureProposer(int k)
			throws UnrespectedCompetenceSyntaxException {
		super(k);
		// TODO Auto-generated constructor stub
	}

	@Override
	public InformedCandidature<ReplicationCandidature> constructCandidature(
			final ResourceIdentifier id) {
		final InformedCandidature<ReplicationCandidature> c = 
				new InformedCandidature<ReplicationCandidature>(new ReplicationCandidature(id,this.getMyAgent().getIdentifier(),true,true));
//		c.getPossibleContracts().addAll(((CollaborativeAgent)getMyAgent()).getCrt().getPossible(c));
		this.getMyAgent().setMySpecif(c);
		c.setInitialState(this.getMyAgent().getMyCurrentState());
		return c;
	}
}