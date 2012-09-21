package frameworks.negotiation.protocoles;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Set;


import dima.basicagentcomponents.AgentIdentifier;
import dima.introspectionbasedagents.kernel.NotReadyException;
import dima.introspectionbasedagents.services.BasicAgentCompetence;
import dima.introspectionbasedagents.services.UnrespectedCompetenceSyntaxException;
import frameworks.negotiation.NegotiatingAgent;
import frameworks.negotiation.contracts.AbstractContractTransition;
import frameworks.negotiation.contracts.ContractTrunk;
import frameworks.negotiation.contracts.ResourceIdentifier;
import frameworks.negotiation.protocoles.AbstractCommunicationProtocol.ProposerCore;
import frameworks.negotiation.rationality.AgentState;

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
		final Set<Contract> candidatures = new HashSet<Contract>();

		if (myKnownHosts.isEmpty())
			try {
				Thread.sleep(1000, 10);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		else {
			assert this.KnownHostValidityVerification();
			final Iterator<ResourceIdentifier> itMyHosts = this.myKnownHosts.iterator();
			while (itMyHosts.hasNext() && candidatures.size()<this.k){
				final Contract c = this.constructCandidature(itMyHosts.next());
				candidatures.add(c);
				itMyHosts.remove();
			}
			assert this.candidatureValidityVerification(candidatures);
		}
		return candidatures;
	}

	public abstract Contract constructCandidature(ResourceIdentifier id);

	@Override
	public boolean IWantToNegotiate(final ContractTrunk<Contract> contracts) {
		return !this.getMyAgent().getMyCurrentState().getMyResourceIdentifiers().containsAll(
				this.getMyAgent().getMyInformation().getKnownAgents());
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