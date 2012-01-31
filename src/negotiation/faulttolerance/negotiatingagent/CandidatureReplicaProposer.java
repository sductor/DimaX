package negotiation.faulttolerance.negotiatingagent;

import java.util.HashSet;
import java.util.Set;

import negotiation.faulttolerance.negotiatingagent.ReplicaState;
import negotiation.faulttolerance.negotiatingagent.ReplicationCandidature;
import negotiation.faulttolerance.negotiatingagent.ReplicationSpecification;
import negotiation.negotiationframework.SimpleNegotiatingAgent;
import negotiation.negotiationframework.interaction.candidatureprotocol.CandidatureProposer;
import negotiation.negotiationframework.interaction.consensualnegotiation.AbstractProposerCore;
import negotiation.negotiationframework.interaction.contracts.ResourceIdentifier;
import dima.basicagentcomponents.AgentIdentifier;
import dima.introspectionbasedagents.NotReadyException;
import dima.introspectionbasedagents.services.BasicAgentCompetence;

public class CandidatureReplicaProposer
extends CandidatureProposer<ReplicationSpecification, ReplicaState, ReplicationCandidature>{
	private static final long serialVersionUID = -5315491050460219982L;

	@Override
	public ReplicationCandidature constructCandidature(ResourceIdentifier id) {
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