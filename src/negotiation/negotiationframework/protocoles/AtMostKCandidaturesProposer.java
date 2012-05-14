package negotiation.negotiationframework.protocoles;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Random;
import java.util.Set;

import com.jcraft.jsch.KnownHosts;

import sun.security.action.GetLongAction;

import negotiation.negotiationframework.SimpleNegotiatingAgent;
import negotiation.negotiationframework.contracts.AbstractActionSpecif;
import negotiation.negotiationframework.contracts.AbstractContractTransition;
import negotiation.negotiationframework.contracts.ContractTrunk;
import negotiation.negotiationframework.contracts.ResourceIdentifier;
import negotiation.negotiationframework.protocoles.AbstractCommunicationProtocol.ProposerCore;
import negotiation.negotiationframework.rationality.AgentState;
import dima.basicagentcomponents.AgentIdentifier;
import dima.introspectionbasedagents.services.BasicAgentCompetence;
import dima.introspectionbasedagents.services.UnrespectedCompetenceSyntaxException;
import dima.introspectionbasedagents.shells.NotReadyException;

public  abstract class AtMostKCandidaturesProposer<
ActionSpec extends AbstractActionSpecif,
PersonalState extends AgentState,
Contract extends AbstractContractTransition<ActionSpec>>
extends
BasicAgentCompetence<SimpleNegotiatingAgent<ActionSpec,PersonalState,Contract>>
implements
ProposerCore
<SimpleNegotiatingAgent<ActionSpec,PersonalState,Contract>,
ActionSpec,PersonalState,Contract> {
	private static final long serialVersionUID = -5315491050460219982L;

	public final int k;
	public final LinkedList<ResourceIdentifier> myKnownHosts = new LinkedList<ResourceIdentifier>();
		
	public AtMostKCandidaturesProposer(int k) throws UnrespectedCompetenceSyntaxException {
		super();
		this.k = k;
	}

	@Override
	public Set<Contract> getNextContractsToPropose()
			throws NotReadyException {

		if (myKnownHosts.isEmpty()){
			myKnownHosts.addAll((Collection<ResourceIdentifier>) this.getMyAgent().getMyInformation().getKnownAgents());
			myKnownHosts.remove(getMyAgent().getIdentifier());
			myKnownHosts.removeAll(getMyAgent().getMyCurrentState().getMyResourceIdentifiers());
			Collections.shuffle(myKnownHosts);
			
		}

		myKnownHosts.removeAll(getMyAgent().getMyCurrentState().getMyResourceIdentifiers());
		assert KnownHostValidityVerification();
		Iterator<ResourceIdentifier> itMyHosts = myKnownHosts.iterator();
		final Set<Contract> candidatures = new HashSet<Contract>();
		
		while (itMyHosts.hasNext() && candidatures.size()<k){
			final Contract c = this.constructCandidature(itMyHosts.next());
			c.setSpecificationNInitialState(
					this.getMyAgent().getMyCurrentState(),
					this.getMyAgent().computeMySpecif(c));
			candidatures.add(c);
			itMyHosts.remove();
		}
		assert candidatureValidityVerification(candidatures);
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

	private boolean KnownHostValidityVerification(){
		for (AgentIdentifier id : myKnownHosts){
			assert id instanceof ResourceIdentifier:id;
			assert !getMyAgent().getMyCurrentState().getMyResourceIdentifiers().contains(id):
				id+"\n "+getMyAgent().getMyCurrentState().getMyResourceIdentifiers()+"\n "+this.getMyAgent().getMyInformation().getKnownAgents();
		}
		return true;
	}
	private boolean candidatureValidityVerification(Set<Contract> candidatures){
		LinkedList<ResourceIdentifier> allHosts = new LinkedList<ResourceIdentifier>();
		allHosts.addAll((Collection<ResourceIdentifier>) this.getMyAgent().getMyInformation().getKnownAgents());
		allHosts.remove(getMyAgent().getIdentifier());
		
		assert !candidatures.isEmpty() ||  
		getMyAgent().getMyCurrentState().getMyResourceIdentifiers().size()==allHosts.size():
			k+"\n "+getMyAgent().getMyCurrentState().getMyResourceIdentifiers()+"--> CurrentState \n  "+allHosts+"--> allHosts\n "+myKnownHosts;
		
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