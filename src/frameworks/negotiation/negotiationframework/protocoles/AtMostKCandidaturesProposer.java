package frameworks.negotiation.negotiationframework.protocoles;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Set;

import dima.basicagentcomponents.AgentIdentifier;
import dima.introspectionbasedagents.services.BasicAgentCompetence;
import dima.introspectionbasedagents.services.UnrespectedCompetenceSyntaxException;
import dima.introspectionbasedagents.shells.NotReadyException;
import frameworks.negotiation.negotiationframework.NegotiatingAgent;
import frameworks.negotiation.negotiationframework.contracts.AbstractContractTransition;
import frameworks.negotiation.negotiationframework.contracts.ContractTrunk;
import frameworks.negotiation.negotiationframework.contracts.ResourceIdentifier;
import frameworks.negotiation.negotiationframework.protocoles.AbstractCommunicationProtocol.ProposerCore;
import frameworks.negotiation.negotiationframework.rationality.AgentState;

public  abstract class AtMostKCandidaturesProposer<
Agent extends NegotiatingAgent<PersonalState, Contract>,
PersonalState extends AgentState,
Contract extends AbstractContractTransition>
extends
BasicAgentCompetence<Agent>
implements
ProposerCore
<Agent,PersonalState,Contract> {
	private static final long serialVersionUID = -5315491050460219982L;

	public final int k;
	public final LinkedList<ResourceIdentifier> myKnownHosts = new LinkedList<ResourceIdentifier>();

	public AtMostKCandidaturesProposer(final int k) throws UnrespectedCompetenceSyntaxException {
		super();
		this.k = k;
	}

	@Override
	public Set<Contract> getNextContractsToPropose()
			throws NotReadyException {

		if (this.myKnownHosts.isEmpty()){
			this.myKnownHosts.addAll((Collection<ResourceIdentifier>) this.getMyAgent().getMyInformation().getKnownAgents());
			this.myKnownHosts.remove(this.getMyAgent().getIdentifier());
			this.myKnownHosts.removeAll(this.getMyAgent().getMyCurrentState().getMyResourceIdentifiers());
			Collections.shuffle(this.myKnownHosts);

		}

		this.myKnownHosts.removeAll(this.getMyAgent().getMyCurrentState().getMyResourceIdentifiers());
		assert this.KnownHostValidityVerification();
		final Iterator<ResourceIdentifier> itMyHosts = this.myKnownHosts.iterator();
		final Set<Contract> candidatures = new HashSet<Contract>();

		while (itMyHosts.hasNext() && candidatures.size()<this.k){
			final Contract c = this.constructCandidature(itMyHosts.next());
			candidatures.add(c);
			itMyHosts.remove();
		}
		assert this.candidatureValidityVerification(candidatures);
		return candidatures;
	}

	public abstract Contract constructCandidature(ResourceIdentifier id);

	@Override
	public boolean IWantToNegotiate(final ContractTrunk<Contract> contracts) {
		return !this.getMyAgent().getMyCurrentState().getMyResourceIdentifiers().containsAll(
				this.getMyAgent().getMyInformation().getKnownAgents());
	}

	@Override
	public boolean ImAllowedToNegotiate(final ContractTrunk<Contract> contracts) {
		return  contracts.getAllInitiatorContracts().isEmpty();
	}

	private boolean KnownHostValidityVerification(){
		for (final AgentIdentifier id : this.myKnownHosts){
			assert id instanceof ResourceIdentifier:id;
		assert !this.getMyAgent().getMyCurrentState().getMyResourceIdentifiers().contains(id):
			id+"\n "+this.getMyAgent().getMyCurrentState().getMyResourceIdentifiers()+"\n "+this.getMyAgent().getMyInformation().getKnownAgents();
		}
		return true;
	}
	private boolean candidatureValidityVerification(final Set<Contract> candidatures){
		final LinkedList<ResourceIdentifier> allHosts = new LinkedList<ResourceIdentifier>();
		allHosts.addAll((Collection<ResourceIdentifier>) this.getMyAgent().getMyInformation().getKnownAgents());
		allHosts.remove(this.getMyAgent().getIdentifier());

		assert !candidatures.isEmpty() ||
		this.getMyAgent().getMyCurrentState().getMyResourceIdentifiers().size()==allHosts.size():
			this.k+"\n "+this.getMyAgent().getMyCurrentState().getMyResourceIdentifiers()+"--> CurrentState \n  "+allHosts+"--> allHosts\n "+this.myKnownHosts;

		return true;
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