package frameworks.negotiation.protocoles.dcopProtocol;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;

import dima.basicagentcomponents.AgentIdentifier;
import dima.introspectionbasedagents.services.BasicAgentCompetence;
import dima.introspectionbasedagents.services.loggingactivity.LogService;

import frameworks.faulttolerance.experimentation.ReplicationInstanceGraph;
import frameworks.negotiation.NegotiatingAgent;
import frameworks.negotiation.contracts.ContractTrunk;
import frameworks.negotiation.contracts.MatchingCandidature;
import frameworks.negotiation.protocoles.AbstractCommunicationProtocol.SelectionCore;
import frameworks.negotiation.rationality.AgentState;

public class DcopLeaderSelectionCore<
State extends AgentState, 
Contract extends MatchingCandidature> 
extends DcopAgentSelectionCore<State,Contract>
implements 
SelectionCore<NegotiatingAgent<State,Contract>, State, Contract>  {

	public DCOPLeaderProtocol<State, Contract> getMyProtocol(){
		return (DCOPLeaderProtocol<State, Contract>) getMyAgent().getMyProtocol();
	}
	@Override
	public void select(ContractTrunk<Contract> cs, State currentState,
			Collection<Contract> toAccept, Collection<Contract> toReject,
			Collection<Contract> toPutOnWait) {

		super.select(cs, currentState, toAccept, toReject, toPutOnWait);

		//Cancelling partially refused requests
		for (Contract failedContract: cs.getFailedContracts()){
			if (getMyProtocol().lockedNodesToRig.containsKey(failedContract)){
				getMyProtocol().waitTime=getRandom().nextInt(getMyProtocol().maxWainttime);
				Collection<ReplicationInstanceGraph> rigToRemove = new ArrayList<ReplicationInstanceGraph>();
				for (ReplicationInstanceGraph rig : getMyProtocol().lockedNodesToRig.get(failedContract)){
					if (everyoneHasAnswered(cs, rig)){
						if (!DCOPLeaderProtocol.dcopProtocol.equals(LogService.onNone))logMonologue("canceling : some agent of the group have refused",LogService.onFile);
						rigToRemove.add(rig);
						getMyProtocol().gainFlag.add(rig);
						getMyProtocol().lockedRigs.remove(rig);
						//
						toReject.addAll(getMyProtocol().gainContracts.get(rig));
						toPutOnWait.removeAll(getMyProtocol().gainContracts.get(rig));
						toAccept.removeAll(getMyProtocol().gainContracts.get(rig));
					}
				}
				for (ReplicationInstanceGraph rig : rigToRemove){
					getMyProtocol().lockedNodesToRig.removeAvalue(rig);
				}
			}
		}

		//Cancelling obsolete requests
		for (ReplicationInstanceGraph rig : getMyProtocol().gainFlag){
			if (getMyProtocol().lockedRigs.contains(rig) && everyoneHasAnswered(cs, rig)){
				logMonologue("canceling : a new gain has been found",DCOPLeaderProtocol.dcopProtocol);
				toReject.addAll(getMyProtocol().gainContracts.get(rig));
				toPutOnWait.removeAll(getMyProtocol().gainContracts.get(rig));
				toAccept.removeAll(getMyProtocol().gainContracts.get(rig));
				//
				getMyProtocol().lockedRigs.remove(rig);
				getMyProtocol().lockedNodesToRig.removeAvalue(rig);
			} 
		}

		//Confirming consensual requests
		Collection<Contract> consensualcontracts = cs.getInitiatorRequestableContracts();
		Collection<ReplicationInstanceGraph> rigToRemove = new ArrayList<ReplicationInstanceGraph>();
		for (ReplicationInstanceGraph rig : getMyProtocol().lockedRigs){
			if (consensualcontracts.containsAll(getMyProtocol().gainContracts.get(rig))){
				logWarning("committing consensual change",DCOPLeaderProtocol.dcopProtocol);
				toAccept.addAll(getMyProtocol().gainContracts.get(rig));
				toPutOnWait.removeAll(getMyProtocol().gainContracts.get(rig));
				toReject.addAll(toPutOnWait);
				toPutOnWait.clear();
				getMyProtocol().gainContracts.remove(rig);
				rigToRemove.add(rig);
				getMyProtocol().lockedNodesToRig.removeAvalue(rig);
				if (rig.equals(getMyProtocol().currentlyOptimizedRig.getLast())){
					getMyProtocol().fringeNodes2ring.removeAvalue(getMyProtocol().currentlyOptimizedRig.getLast());
					getMyProtocol().rig2fringeNodes.remove(getMyProtocol().currentlyOptimizedRig.getLast());
					getMyProtocol().currentlyOptimizedRig.removeFirst();
					getMyProtocol().graphChanged=true;
				}
			}
		}
		for (ReplicationInstanceGraph rig : rigToRemove){
			getMyProtocol().lockedRigs.remove(rig);
		}

		if (!DCOPLeaderProtocol.dcopProtocol.equals(LogService.onNone)){
			if (!toPutOnWait.isEmpty()){
				HashSet<AgentIdentifier> agsWaited = new HashSet<AgentIdentifier>();
				String status = "";
				for (Contract c : toPutOnWait){
					agsWaited.addAll(c.getAllParticipants());
//					status+=getMyProtocol().getContracts().statusOf(c)+"\n";
				}
				logMonologue("wainting for "+agsWaited+"\n--\n"+status,LogService.onFile);
			}
		}
	}


	private boolean everyoneHasAnswered(ContractTrunk<Contract> cs, ReplicationInstanceGraph rig){
//		for (Contract c  : getMyProtocol().gainContracts.get(rig)){
//			for (AgentIdentifier id : c.getNotInitiatingParticipants()){
//				if (!(cs.getContractsAcceptedBy(id).contains(c) || cs.getContractsRejectedBy(id).contains(c)))
//					return false;
//			}
//		}
		return true;
	}

	//	public Collection<Contract> convertToContract(ReplicationInstanceGraph rig){
	//		Collection<Contract> result = new HashSet<Contract>();
	//		for (AgentIdentifier id : rig.getAgentsIdentifier()){
	//			result.addAll(convertTContract(rig, id));
	//		}
	//		for (AgentIdentifier id : rig.getHostsIdentifier()){
	//			result.addAll(convertTContract(rig, id));
	//		}
	//		return result;	
	//	}
	//
	//	public Collection<Contract> convertTContract(ReplicationInstanceGraph rig, AgentIdentifier id){
	//		AgentState idOriginalInst = getMyProtocol().localView.getAgentState(id);
	//		AgentState idNewInst = rig.getAgentState(id);
	//
	//		Collection<Contract> result = new HashSet<Contract>();
	//
	//		for (AgentIdentifier ress : idOriginalInst.getMyResourceIdentifiers()){
	//			if (!idNewInst.getMyResourceIdentifiers().contains(ress)){
	//				result.add(generateDestructionContract(id,ress));
	//			}
	//		}
	//		for (AgentIdentifier ress : idNewInst.getMyResourceIdentifiers()){
	//			if (!idOriginalInst.getMyResourceIdentifiers().contains(ress)){
	//				result.add(generateCreationContract(id,ress));
	//			}
	//		}
	//		return result;
	//	}
	//
	//
	//	public abstract Contract generateDestructionContract(AgentIdentifier id1,final AgentIdentifier id);
	//
	//	public abstract Contract generateCreationContract(AgentIdentifier id1,final AgentIdentifier id);
}
