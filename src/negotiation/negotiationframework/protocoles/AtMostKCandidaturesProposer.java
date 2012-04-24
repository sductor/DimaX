package negotiation.negotiationframework.protocoles;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.Random;
import java.util.Set;

import negotiation.negotiationframework.SimpleNegotiatingAgent;
import negotiation.negotiationframework.contracts.AbstractActionSpecification;
import negotiation.negotiationframework.contracts.AbstractContractTransition;
import negotiation.negotiationframework.contracts.ContractTrunk;
import negotiation.negotiationframework.contracts.ResourceIdentifier;
import negotiation.negotiationframework.protocoles.AbstractCommunicationProtocol.ProposerCore;
import dima.basicagentcomponents.AgentIdentifier;
import dima.introspectionbasedagents.services.BasicAgentCompetence;
import dima.introspectionbasedagents.services.UnrespectedCompetenceSyntaxException;
import dima.introspectionbasedagents.shells.NotReadyException;

public  abstract class AtMostKCandidaturesProposer<
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

	public final Random rand = new Random();
	public final int k;
		
	public AtMostKCandidaturesProposer(int k) throws UnrespectedCompetenceSyntaxException {
		super();
		this.k = k;
	}

	@Override
	public Set<Contract> getNextContractsToPropose()
			throws NotReadyException {

		final Set<Contract> candidatures = new HashSet<Contract>();
		LinkedList<ResourceIdentifier> neoHost = new LinkedList<ResourceIdentifier>();

		for (final AgentIdentifier id : this.getMyAgent().getMyInformation().getKnownAgents()) {
			assert id instanceof ResourceIdentifier || id.equals(getIdentifier()):id;
			if (id instanceof ResourceIdentifier
					&& !this.getMyAgent().getMyCurrentState().getMyResourceIdentifiers()
					.contains(id)){
				neoHost.add((ResourceIdentifier)id);
			}
		}
		
		int selected = 0;
		while (!neoHost.isEmpty() && selected <= k){
			int r = rand.nextInt(neoHost.size());

			final Contract c = this.constructCandidature(neoHost.get(r));
			c.setSpecification(this.getMyAgent().getMySpecif(c));
			candidatures.add(c);
			
			neoHost.remove(r);
			selected++;
		}

		return candidatures;
	}

	public abstract Contract constructCandidature(ResourceIdentifier id);

	@Override
	public boolean IWantToNegotiate(final PersonalState myCurrentState,
			final ContractTrunk<Contract, ActionSpec, PersonalState> contracts) {
		return !myCurrentState.getMyResourceIdentifiers().containsAll(
				this.getMyAgent().getMyInformation().getKnownAgents());
	}

	@Override
	public boolean ImAllowedToNegotiate(final PersonalState myCurrentState,
			final ContractTrunk<Contract, ActionSpec, PersonalState> contracts) {
		return  contracts.getAllInitiatorContracts().isEmpty();
	}
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