package negotiation.faulttolerance.collaborativecandidature;

import negotiation.faulttolerance.negotiatingagent.ReplicaState;
import negotiation.faulttolerance.negotiatingagent.ReplicationCandidature;
import negotiation.faulttolerance.negotiatingagent.ReplicationSpecification;
import negotiation.negotiationframework.contracts.InformedCandidature;
import negotiation.negotiationframework.contracts.ResourceIdentifier;
import negotiation.negotiationframework.protocoles.CandidatureProposer;

public class CollaborativeCandidatureProposer
extends CandidatureProposer<ReplicationSpecification, ReplicaState, InformedCandidature<ReplicationCandidature,ReplicationSpecification>>{
	private static final long serialVersionUID = 4899697150539667541L;

	@Override
	public InformedCandidature<ReplicationCandidature,ReplicationSpecification> constructCandidature(
			final ResourceIdentifier id) {
		final InformedCandidature c = new InformedCandidature(new ReplicationCandidature(id,this.getMyAgent().getIdentifier(),true,true));
		c.setSpecification(this.getMyAgent().getMySpecif(c));
		return c;
	}
}