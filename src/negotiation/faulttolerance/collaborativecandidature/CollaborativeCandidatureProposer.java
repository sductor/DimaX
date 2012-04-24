package negotiation.faulttolerance.collaborativecandidature;

import dima.introspectionbasedagents.services.UnrespectedCompetenceSyntaxException;
import negotiation.faulttolerance.negotiatingagent.ReplicaState;
import negotiation.faulttolerance.negotiatingagent.ReplicationCandidature;
import negotiation.faulttolerance.negotiatingagent.ReplicationSpecification;
import negotiation.negotiationframework.contracts.InformedCandidature;
import negotiation.negotiationframework.contracts.ResourceIdentifier;
import negotiation.negotiationframework.protocoles.AtMostKCandidaturesProposer;

public class CollaborativeCandidatureProposer
extends AtMostKCandidaturesProposer<ReplicationSpecification, ReplicaState, InformedCandidature<ReplicationCandidature,ReplicationSpecification>>{
	private static final long serialVersionUID = 4899697150539667541L;

	public CollaborativeCandidatureProposer(int k)
			throws UnrespectedCompetenceSyntaxException {
		super(k);
		// TODO Auto-generated constructor stub
	}

	@Override
	public InformedCandidature<ReplicationCandidature,ReplicationSpecification> constructCandidature(
			final ResourceIdentifier id) {
		final InformedCandidature c = new InformedCandidature(new ReplicationCandidature(id,this.getMyAgent().getIdentifier(),true,true));
//		c.getPossibleContracts().addAll(((CollaborativeAgent)getMyAgent()).getCrt().getPossible(c));
		c.setSpecification(this.getMyAgent().getMySpecif(c));
		return c;
	}
}