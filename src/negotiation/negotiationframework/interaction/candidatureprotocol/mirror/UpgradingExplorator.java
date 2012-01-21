package negotiation.negotiationframework.interaction.candidatureprotocol.mirror;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import negotiation.negotiationframework.SimpleNegotiatingAgent;
import negotiation.negotiationframework.interaction.AbstractActionSpecification;
import negotiation.negotiationframework.interaction.AbstractContractTransition;
import negotiation.negotiationframework.interaction.consensualnegotiation.ContractTrunk;
import dima.basicagentcomponents.AgentIdentifier;

public abstract class UpgradingExplorator<
Contract extends AbstractContractTransition<ActionSpec>,
ActionSpec extends AbstractActionSpecification> {

	public Collection<Contract> generateUpgradingContracts(
			final SimpleNegotiatingAgent<ActionSpec, ?, Contract> myAgent,
			final ContractTrunk<Contract> n) {
//		myAgent.logMonologue("entering upgrading contract myState is "+myAgent.getMyCurrentState());
		final Collection<Contract> unacceptedContracts = n.getRejectedContracts();
		final Collection<Contract> toPutOnWait = new ArrayList<Contract>();
		final Map<AgentIdentifier,Contract> upgradingContracts =
				new HashMap<AgentIdentifier,Contract>();
		final Collection<Contract> emptycontract = new ArrayList<Contract>();

		if (!myAgent.getMyProtocol().negotiationAsInitiatorHasStarted()
				&& !unacceptedContracts.isEmpty() && n.getContractsAcceptedBy(myAgent.getIdentifier()).isEmpty())
			//The state of the host is stable and there is rejected contract :
			//we try to find if there is destruction that could loccally improve the system
			for (final ActionSpec stateToDestroy : myAgent.getMyResources())
				//				myAgent.logMonologue("should i destroy? "+stateToDestroy);
				for (final Contract c : unacceptedContracts){
//					myAgent.logMonologue("analysing contract "+c);
					final Collection<Contract> testingAllocation = new ArrayList<Contract>();
					final Contract destContract = this.generateDestructionContract(stateToDestroy,c);
					destContract.setSpecification(myAgent.getMySpecif(destContract));
					destContract.setSpecification(stateToDestroy);
					testingAllocation.add(c);
					testingAllocation.add(destContract);
//					myAgent.logMonologue(" upgrading contract myState is "+myAgent.getMyCurrentState());
					if (upgradingContracts.containsKey(stateToDestroy.getMyAgentIdentifier())){
						final Collection<Contract> alreadyMadeContract = new ArrayList<Contract>();
						alreadyMadeContract.add(upgradingContracts.get(stateToDestroy.getMyAgentIdentifier()));
						if (myAgent.getMyAllocationPreferenceComparator().compare(
								testingAllocation,
								alreadyMadeContract)>0){
							upgradingContracts.put(stateToDestroy.getMyAgentIdentifier(), destContract);
							toPutOnWait.add(c);
						}
					} else if (myAgent.getMyAllocationPreferenceComparator().compare(testingAllocation, emptycontract)>0){
						upgradingContracts.put(stateToDestroy.getMyAgentIdentifier(), destContract);
						toPutOnWait.add(c);
					}
				}

		for (final Contract c : toPutOnWait)
			n.removeRejection(myAgent.getIdentifier(), c);
		return upgradingContracts.values();
	}

	protected abstract Contract generateDestructionContract(ActionSpec state, Contract c);
}



//
//	Contract minRefused =
//			Collections.min(
//					unacceptedContracts,
//					myAgent().getMyPreferenceComparator());
////	Collection<ReplicationCandidature> toPutOnWait = new ArrayList<ReplicationCandidature>();
//	boolean iLLProposeDestruction = false;
////	for (ReplicationCandidature c : unacceptedContracts){
//		Iterator<ReplicaState> itAg =
//				this.getMyAgent().
//				getMyCurrentState().getMyAgents();
//
//		while (itAg.hasNext()){
//			ReplicaState agToKill = itAg.next();
//			if (agToKill.getMyReliability() > minRefused.getAgentInitialState().getMyReliability()){
//				ReplicationDestructionCandidature replicationDestructionCandidature = new ReplicationDestructionCandidature(
//						(ResourceIdentifier) this.getMyAgent().getIdentifier(),
//						agToKill.getMyAgentIdentifier(),
//						minRefused);
//				iLLProposeDestruction=true;
//				replicationDestructionCandidature.setSpecification(getMyAgent().getMySpecif(replicationDestructionCandidature));
//				replicationDestructionCandidature.setSpecification(agToKill);
//				try {
//					contractsToPropose.add(replicationDestructionCandidature);
//				} catch (Exception e){
//					throw new RuntimeException(
//							"myAgents : "+this.getMyAgent().
//							getMyCurrentState().getMyAgentsCollec()+" \n contracts : "+contractsToPropose, e);
//				}
////				toPutOnWait.add(c);
////			}
//		}
//	}
//
//		if (iLLProposeDestruction)
//	for (ReplicationCandidature c : unacceptedContracts){
//		n.getAnswers().removeRejection(this.getMyAgent().getIdentifier(), c);
//	}
//}
//}
