package negotiation.faulttolerance.collaborativecandidature;

import negotiation.faulttolerance.negotiatingagent.ReplicaState;
import negotiation.faulttolerance.negotiatingagent.ReplicationCandidature;
import negotiation.faulttolerance.negotiatingagent.ReplicationSpecification;
import negotiation.negotiationframework.contracts.ResourceIdentifier;
import negotiation.negotiationframework.protocoles.collaborative.InformedCandidature;
import negotiation.negotiationframework.protocoles.status.CandidatureProposer;

public class CollaborativeCandidatureProposer
extends CandidatureProposer<ReplicationSpecification, ReplicaState, InformedCandidature<ReplicationCandidature,ReplicationSpecification>>{
	@Override
	public InformedCandidature<ReplicationCandidature,ReplicationSpecification> constructCandidature(
			final ResourceIdentifier id) {
		final InformedCandidature c = new InformedCandidature(new ReplicationCandidature(id,this.getMyAgent().getIdentifier(),true,true));
		c.setSpecification(this.getMyAgent().getMySpecif(c));
		return c;
	}
}