package frameworks.negotiation.protocoles.dcopProtocol;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import sun.reflect.ReflectionFactory.GetReflectionFactoryAction;

import dima.introspectionbasedagents.services.BasicAgentCompetence;
import dima.introspectionbasedagents.services.loggingactivity.LogService;
import frameworks.faulttolerance.negotiatingagent.HostState;
import frameworks.faulttolerance.negotiatingagent.ReplicaState;
import frameworks.negotiation.NegotiatingAgent;
import frameworks.negotiation.contracts.ContractTrunk;
import frameworks.negotiation.contracts.MatchingCandidature;
import frameworks.negotiation.contracts.UnknownContractException;
import frameworks.negotiation.protocoles.AbstractCommunicationProtocol.ProposerCore;
import frameworks.negotiation.protocoles.AbstractCommunicationProtocol.SelectionCore;
import frameworks.negotiation.rationality.AgentState;

public class DcopAgentSelectionCore<
State extends AgentState, 
Contract extends MatchingCandidature> 
extends BasicAgentCompetence<NegotiatingAgent<State,Contract>>
implements 
SelectionCore<NegotiatingAgent<State,Contract>, State, Contract> {

	public DcopAgentProtocol<State, Contract> getMyProtocol(){
		return (DcopAgentProtocol<State, Contract>) getMyAgent().getMyProtocol();
	}

	@Override
	public void select(ContractTrunk<Contract> cs, State currentState,
			Collection<Contract> toAccept, Collection<Contract> toReject,
			Collection<Contract> toPutOnWait) {

		toPutOnWait.addAll(cs.getAllContracts());
		//Answering lockRequest
		List<Contract> contractsToAnswer = cs.getParticipantOnWaitContracts();

		//Refusing obsolete contract
		Iterator<Contract> cIt = contractsToAnswer.iterator();
		while (cIt.hasNext()){
			Contract c = cIt.next();
			if (currentState instanceof ReplicaState){
				if (currentState.hasResource(c.getResource())==c.isMatchingCreation()){
					cIt.remove();
					toReject.add(c);
				}
			}
			if (currentState instanceof HostState){
				if (currentState.hasResource(c.getAgent())==c.isMatchingCreation()){
					cIt.remove();
					toReject.add(c);
				}
			}
		}


		Collections.sort(contractsToAnswer,Collections.reverseOrder(getMyAgent().getMyPreferenceComparator()));
		for (Contract lockRequest : contractsToAnswer){
			if (getMyProtocol().iCanAcceptLock(lockRequest)){
				toAccept.add(lockRequest);
				getMyProtocol().myLock.add(lockRequest.getInitiator(),lockRequest);
			} else {
				toReject.add(lockRequest);
			}
		}

		toPutOnWait.removeAll(cs.getParticipantOnWaitContracts());
	}
}
