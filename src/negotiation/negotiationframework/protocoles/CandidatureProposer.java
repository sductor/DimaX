package negotiation.negotiationframework.protocoles;

import java.util.HashSet;
import java.util.Set;

import negotiation.negotiationframework.SimpleNegotiatingAgent;
import negotiation.negotiationframework.contracts.AbstractActionSpecification;
import negotiation.negotiationframework.contracts.AbstractContractTransition;
import negotiation.negotiationframework.contracts.ContractTrunk;
import negotiation.negotiationframework.contracts.ResourceIdentifier;
import negotiation.negotiationframework.protocoles.AbstractCommunicationProtocol.ProposerCore;
import dima.basicagentcomponents.AgentIdentifier;
import dima.introspectionbasedagents.services.BasicAgentCompetence;
import dima.introspectionbasedagents.shells.NotReadyException;

public  abstract class CandidatureProposer<
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

		for (final AgentIdentifier id : this.getMyAgent().getMyInformation().getKnownAgents()) {
			if (id instanceof ResourceIdentifier
					&& !this.getMyAgent().getMyCurrentState().getMyResourceIdentifiers()
					.contains(id)){
				final Contract c = this.constructCandidature((ResourceIdentifier) id);
				c.setSpecification(this.getMyAgent().getMySpecif(c));
				candidatures.add(c);
			}
		}

		return candidatures;
	}

	public abstract Contract constructCandidature(ResourceIdentifier id);
	
	@Override
	public boolean IWantToNegotiate(PersonalState myCurrentState,
			ContractTrunk<Contract, ActionSpec, PersonalState> contracts) {
		return !myCurrentState.getMyResourceIdentifiers().containsAll(
				this.getMyAgent().getMyInformation().getKnownAgents());
	}

	@Override
	public boolean ImAllowedToNegotiate(PersonalState myCurrentState,
			ContractTrunk<Contract, ActionSpec, PersonalState> contracts) {
		return  contracts.getAllInitiatorContracts().isEmpty();
	}

	//	@Override
	//	public boolean IWantToNegotiate(final ReplicaState s) {
	//		if (((Replica) this.getMyAgent()).IReplicate())
	//			if (!s.getMyResourceIdentifiers().containsAll(
	//					this.getMyAgent().getMyInformation().getKnownAgents()))
	//				return true;
	//			else
	//				// logMonologue("full!");
	//				return false;
	//		else
	//			return false;
	//	}

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