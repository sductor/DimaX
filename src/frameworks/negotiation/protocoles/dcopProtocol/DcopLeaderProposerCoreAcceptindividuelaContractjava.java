package frameworks.negotiation.protocoles.dcopProtocol;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Random;
import java.util.Set;

import dima.introspectionbasedagents.kernel.NotReadyException;
import dima.introspectionbasedagents.modules.faults.Assert;
import dima.introspectionbasedagents.services.BasicAgentCompetence;
import frameworks.faulttolerance.experimentation.ReplicationInstanceGraph;
import frameworks.negotiation.NegotiatingAgent;
import frameworks.negotiation.contracts.ContractTrunk;
import frameworks.negotiation.contracts.MatchingCandidature;
import frameworks.negotiation.contracts.UnknownContractException;
import frameworks.negotiation.protocoles.AbstractCommunicationProtocol.ProposerCore;
import frameworks.negotiation.protocoles.AbstractCommunicationProtocol.Receivers;
import frameworks.negotiation.rationality.AgentState;

public class DcopLeaderProposerCoreAcceptindividuelaContractjava<
State extends AgentState, 
Contract extends MatchingCandidature> 
extends BasicAgentCompetence<NegotiatingAgent<State,Contract>>
implements 
ProposerCore<NegotiatingAgent<State,Contract>, State, Contract> {

	public DCOPLeaderProtocol<State, Contract> getMyProtocol(){
		return (DCOPLeaderProtocol<State, Contract>) getMyAgent().getMyProtocol();
	}

	/*
	 * Proposer core
	 */

	@Override
	public boolean IWantToNegotiate(ContractTrunk<Contract> contracts) {
		//		logMonologue("i want to negotiaite? "+!getMyProtocol().gainFlag.isEmpty());
		return getMyProtocol().myLock.isEmpty() && !getMyProtocol().gainFlag.isEmpty();
	}

	@Override
	public Set<? extends Contract> getNextContractsToPropose()
			throws NotReadyException {
		//		logMonologue("negotiating");
		Set<Contract> contractToPropose = new HashSet<Contract>();
		Iterator<ReplicationInstanceGraph> itGainFlag = getMyProtocol().gainFlag.iterator();
		assert getMyProtocol().gainFlag.size()<=1;
		while (itGainFlag.hasNext()){
			ReplicationInstanceGraph rig =itGainFlag.next();

			if (!getMyProtocol().lockedRigs.contains(rig)){//s'il a un gain et qui est locké il est d'abord delocke parle selection
				//on verifie que l'initiateur accepte
				boolean iCanDo=true;
				for (Contract lockRequest : getMyProtocol().gainContracts.get(rig)){
					if (!lockRequest.getAllParticipants().contains(getIdentifier())){
						//do nothing its ok!
					} else if (getMyProtocol().iCanAcceptLock(lockRequest)){//je fais partie de la lockrequest et je peux l'accepter
						getMyProtocol().myLocks.add(lockRequest.getAllParticipants(),lockRequest);
					} else {//je fais partie de la lockrequest mais  je ne peux PAS l'accepter
						iCanDo=false;
						for (Contract lockRequesttoRemove : getMyProtocol().gainContracts.get(rig)){
							getMyProtocol().myLocks.removeAvalue(lockRequesttoRemove);
						}
						break;
					}
				}

				if (iCanDo){
					logMonologue("i'll propose",DCOPLeaderProtocol.dcopProtocol);
					for (Contract c : getMyProtocol().gainContracts.get(rig)){
						if (!getMyProtocol().lockedNodesToRig.keySet().contains(c)){
							contractToPropose.add(c);
						}					
						getMyProtocol().lockedNodesToRig.add(c, rig);
					}
					itGainFlag.remove();
					getMyProtocol().lockedRigs.add(rig);
				}
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
