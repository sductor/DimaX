package negotiation.negotiationframework.protocoles.status;

import java.util.HashSet;
import java.util.Set;

import negotiation.negotiationframework.ProposerCore;
import negotiation.negotiationframework.SimpleNegotiatingAgent;
import negotiation.negotiationframework.contracts.AbstractActionSpecification;
import negotiation.negotiationframework.contracts.AbstractContractTransition;
import negotiation.negotiationframework.contracts.ResourceIdentifier;
import dima.basicagentcomponents.AgentIdentifier;
import dima.introspectionbasedagents.services.BasicAgentCompetence;
import dima.introspectionbasedagents.shells.NotReadyException;

public abstract class CandidatureProposer<
ActionSpec extends AbstractActionSpecification,
PersonalState extends ActionSpec,
Contract extends AbstractContractTransition<ActionSpec>>
extends
BasicAgentCompetence<SimpleNegotiatingAgent<ActionSpec,PersonalState,Contract>>
implements
ProposerCore
<SimpleNegotiatingAgent<ActionSpec,PersonalState,Contract>,
ActionSpec,PersonalState,Contract> {
	private static final long serialVersionUID = -5315491050460219982L;

	@Override
	public Set<Contract> getNextContractsToPropose()
			throws NotReadyException {

		final Set<Contract> candidatures = new HashSet<Contract>();

		for (final AgentIdentifier id : this.getMyAgent().getMyInformation().getKnownAgents())
			if (id instanceof ResourceIdentifier
					&& !this.getMyAgent().getMyCurrentState().getMyResourceIdentifiers()
					.contains(id)){
				Contract c = this.constructCandidature((ResourceIdentifier) id);
				c.setSpecification(getMyAgent().getMySpecif(c));
				candidatures.add(c);
			}

		return candidatures;
	}

	public abstract Contract constructCandidature(ResourceIdentifier id);

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