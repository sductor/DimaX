package negotiation.faulttolerance.negotiatingagent;

import negotiation.negotiationframework.contracts.ResourceIdentifier;
import negotiation.negotiationframework.protocoles.status.CandidatureProposer;

public class CandidatureReplicaProposer
extends CandidatureProposer<ReplicationSpecification, ReplicaState, ReplicationCandidature>{
	private static final long serialVersionUID = -5315491050460219982L;

	@Override
	public ReplicationCandidature constructCandidature(final ResourceIdentifier id) {
		return new ReplicationCandidature(id,this.getMyAgent().getIdentifier(),true,true);

	}

}
//
//	final boolean mirrorProto;
//
//	public CandidatureReplicaProposer(boolean mirrorProto) {
//		this.mirrorProto = mirrorProto;
//	}
//this.mirrorProto ? new ReplicationCandidatureWithMinInfo(
//								(ResourceIdentifier) id, this.getMyAgent()
//										.getIdentifier(), true)
//								: