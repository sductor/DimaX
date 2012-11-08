package frameworks.negotiation.protocoles.dcopProtocol;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Random;
import java.util.Set;

import dima.basicagentcomponents.AgentIdentifier;
import dima.introspectionbasedagents.kernel.NotReadyException;
import dima.introspectionbasedagents.modules.faults.Assert;
import dima.introspectionbasedagents.modules.mappedcollections.HashedHashSet;
import dima.introspectionbasedagents.services.BasicAgentCompetence;
import dima.introspectionbasedagents.services.loggingactivity.LogService;
import frameworks.faulttolerance.experimentation.ReplicationInstanceGraph;
import frameworks.negotiation.NegotiatingAgent;
import frameworks.negotiation.contracts.ContractTrunk;
import frameworks.negotiation.contracts.MatchingCandidature;
import frameworks.negotiation.contracts.UnknownContractException;
import frameworks.negotiation.protocoles.AbstractCommunicationProtocol.ProposerCore;
import frameworks.negotiation.protocoles.AbstractCommunicationProtocol.Receivers;
import frameworks.negotiation.rationality.AgentState;

public class DcopLeaderProposerCore<
State extends AgentState, 
Contract extends MatchingCandidature> 
extends BasicAgentCompetence<NegotiatingAgent<State,Contract>>
implements 
ProposerCore<NegotiatingAgent<State,Contract>, State, Contract> {

	HashedHashSet<AgentIdentifier, Contract> iWannaLock=new HashedHashSet<AgentIdentifier, Contract>();

	public DCOPLeaderProtocol<State, Contract> getMyProtocol(){
		return (DCOPLeaderProtocol<State, Contract>) getMyAgent().getMyProtocol();
	}

	/*
	 * Proposer core
	 */

	@Override
	public boolean IWantToNegotiate(ContractTrunk<Contract> contracts) {
//				logMonologue("i want to negotiaite? "+!getMyProtocol().gainFlag.isEmpty());
		if (getMyProtocol().waitTime>0)
			getMyProtocol().waitTime--;
		return getMyProtocol().waitTime==0 && getMyProtocol().myLock.isEmpty() && !getMyProtocol().gainFlag.isEmpty();
	}

	@Override
	public Set<? extends Contract> getNextContractsToPropose()
			throws NotReadyException {
				logMonologue("negotiating");
		Set<Contract> contractToPropose = new HashSet<Contract>();
		Iterator<Collection<AgentIdentifier>> itGainFlag = getMyProtocol().gainFlag.iterator();
		assert getMyProtocol().gainFlag.size()<=1;
		while (itGainFlag.hasNext()){
			Collection<AgentIdentifier> rig =itGainFlag.next();

			if (!getMyProtocol().lockedRigs.contains(rig)){//s'il a un gain et qui est locké il est d'abord delocke parle selection
				iWannaLock.clear();
				for (Contract c : getMyProtocol().gainContracts.get(rig)){
					assert (!getMyProtocol().lockedNodesToRig.keySet().contains(c));
					contractToPropose.add(c);
					//						
					iWannaLock.add(c.getInitiator(), c);		
//					getMyProtocol().myLock.add(c.getInitiator(), c);				
					getMyProtocol().lockedNodesToRig.add(c, rig);
				}
				itGainFlag.remove();
				getMyProtocol().lockedRigs.add(rig);
				assert !contractToPropose.isEmpty();
				if (!DCOPLeaderProtocol.dcopProtocol.equals(LogService.onNone))logMonologue("i propose ",LogService.onFile);
			}
		}
		//		try {
		//			long randomtimeout=new Long(new Random().nextInt(10));
		//			getMyAgent().wait(randomtimeout);
		//		} catch (InterruptedException e) {
		//			throw new RuntimeException();
		//		}
		assert Assert.allDiferent(contractToPropose);
		return contractToPropose;
	}	


	//	protected Receivers getProposalReceivers() {
	//		return Receivers.EveryParticipant;
	//	}
}
