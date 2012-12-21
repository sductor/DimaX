package frameworks.negotiation.protocoles.dcopProtocol;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;

import dima.basicagentcomponents.AgentIdentifier;
import dima.introspectionbasedagents.services.loggingactivity.LogService;
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

	/**
	 * 
	 */
	private static final long serialVersionUID = -7987714910417261147L;


	@Override
	public DCOPLeaderProtocol<State, Contract> getMyProtocol(){
		return (DCOPLeaderProtocol<State, Contract>) this.getMyAgent().getMyProtocol();
	}
	@Override
	public void select(final ContractTrunk<Contract> cs, 
			final Collection<Contract> toAccept, final Collection<Contract> toReject,
			final Collection<Contract> toPutOnWait) {
		super.select(cs,  toAccept, toReject, toPutOnWait);

		//		if (toAccept.isEmpty() && !getMyProtocol().getWannaLockContract().isEmpty()){
		//			getMyProtocol().myLock.putAll(getMyProtocol().getWannaLockContract());
		//			getMyProtocol().getWannaLockContract().clear();
		//		}

		//Cancelling partially refused requests
		for (final Contract failedContract: cs.getFailedContracts()){
			if (this.getMyProtocol().lockedNodesToRig.containsKey(failedContract)){
				this.getMyProtocol().waitTime=this.getRandom().nextInt(this.getMyProtocol().maxWainttime);
				final Collection<Collection<AgentIdentifier>> rigToRemove = new ArrayList<Collection<AgentIdentifier>>();
				for (final Collection<AgentIdentifier> rig : this.getMyProtocol().lockedNodesToRig.get(failedContract)){
					if (this.everyoneHasAnswered(cs, rig)){
						if (!DCOPLeaderProtocol.dcopProtocol.equals(LogService.onNone)) {
							this.logMonologue("canceling : some agent of the group have refused"+failedContract.getAllParticipants(),LogService.onScreen);
						}
						rigToRemove.add(rig);
						this.getMyProtocol().gainFlag.add(rig);
						this.getMyProtocol().lockedRigs.remove(rig);
						//
						toReject.addAll(this.getMyProtocol().gainContracts.get(rig));
						toPutOnWait.removeAll(this.getMyProtocol().gainContracts.get(rig));
						toAccept.removeAll(this.getMyProtocol().gainContracts.get(rig));
					}
				}
				for (final Collection<AgentIdentifier> rig : rigToRemove){
					this.getMyProtocol().lockedNodesToRig.removeAvalue(rig);
				}
			}
		}

		//Cancelling obsolete requests
		for (final Collection<AgentIdentifier> rig : this.getMyProtocol().gainFlag){
			if (this.getMyProtocol().lockedRigs.contains(rig) && this.everyoneHasAnswered(cs, rig)){
				this.logMonologue("canceling : a new gain has been found",DCOPLeaderProtocol.dcopProtocol);
				toReject.addAll(this.getMyProtocol().gainContracts.get(rig));
				toPutOnWait.removeAll(this.getMyProtocol().gainContracts.get(rig));
				toAccept.removeAll(this.getMyProtocol().gainContracts.get(rig));
				//
				this.getMyProtocol().lockedRigs.remove(rig);
				this.getMyProtocol().lockedNodesToRig.removeAvalue(rig);
				this.getMyProtocol().getWannaLockContract().clear();
			}
		}

		//Confirming consensual requests
		final Collection<Contract> consensualcontracts = cs.getInitiatorRequestableContracts();
		final Collection<Collection<AgentIdentifier>> rigToRemove = new ArrayList<Collection<AgentIdentifier>>();
		for (final Collection<AgentIdentifier> rig : this.getMyProtocol().lockedRigs){
			if (consensualcontracts.containsAll(this.getMyProtocol().gainContracts.get(rig))){
				this.logWarning("committing consensual change",DCOPLeaderProtocol.dcopProtocol);
				toAccept.addAll(this.getMyProtocol().gainContracts.get(rig));
				toPutOnWait.removeAll(this.getMyProtocol().gainContracts.get(rig));
				toReject.addAll(toPutOnWait);
				toPutOnWait.clear();
				this.getMyProtocol().gainContracts.remove(rig);
				rigToRemove.add(rig);
				this.getMyProtocol().lockedNodesToRig.removeAvalue(rig);
				if (rig.equals(this.getMyProtocol().currentlyOptimizedRig.getLast())){
					this.getMyProtocol().fringeNodes2ring.removeAvalue(this.getMyProtocol().currentlyOptimizedRig.getLast());
					this.getMyProtocol().rig2fringeNodes.remove(this.getMyProtocol().currentlyOptimizedRig.getLast());
					this.getMyProtocol().currentlyOptimizedRig.removeFirst();
					this.getMyProtocol().graphChanged=true;
				}
			}
		}
		for (final Collection<AgentIdentifier> rig : rigToRemove){
			this.getMyProtocol().lockedRigs.remove(rig);
		}

		if (!DCOPLeaderProtocol.dcopProtocol.equals(LogService.onNone)){
			if (!toPutOnWait.isEmpty()){
				final HashSet<AgentIdentifier> agsWaited = new HashSet<AgentIdentifier>();
				final String status = "";
				for (final Contract c : toPutOnWait){
					for (final AgentIdentifier id : c.getAllParticipants()){
						if (this.getMyProtocol().getContracts().getContractsAcceptedBy(id).contains(c) ||
								this.getMyProtocol().getContracts().getContractsRejectedBy(id).contains(c)){
							// c cool
						} else {
							agsWaited.add(id);
						}
					}

					//					status+=getMyProtocol().getContracts().statusOf(c)+"\n";
				}
				this.logMonologue("wainting for "+agsWaited+"\n--\n"+status,LogService.onFile);
			}
		}
	}


	private boolean everyoneHasAnswered(final ContractTrunk<Contract> cs, final Collection<AgentIdentifier> rig){
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
