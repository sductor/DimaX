package frameworks.negotiation.protocoles.dcopProtocol;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import sun.reflect.ReflectionFactory.GetReflectionFactoryAction;

import dima.introspectionbasedagents.services.BasicAgentCompetence;
import dima.introspectionbasedagents.services.loggingactivity.LogService;
import frameworks.faulttolerance.negotiatingagent.HostState;
import frameworks.faulttolerance.negotiatingagent.ReplicaState;
import frameworks.negotiation.NegotiatingAgent;
import frameworks.negotiation.contracts.ContractTransition;
import frameworks.negotiation.contracts.ContractTrunk;
import frameworks.negotiation.contracts.MatchingCandidature;
import frameworks.negotiation.contracts.UnknownContractException;
import frameworks.negotiation.contracts.ValuedContract;
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
		toPutOnWait.removeAll(cs.getParticipantOnWaitContracts());
//		if (this.getClass().isAssignableFrom(DcopAgentSelectionCore.class)){
//			logMonologue("hoyoyo");
//			assert toPutOnWait.containsAll(cs.getParticipantAlreadyAcceptedContracts()):cs;
//			assert cs.getParticipantAlreadyAnsweredContracts().containsAll(toPutOnWait):cs;
//		}
		
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
		
		//Answering lockRequest
		Collections.sort((List<ValuedContract>)contractsToAnswer,getMyProtocol().getContractComparator());
//		String log = "i will select :"+ContractTransition.toInitiator(contractsToAnswer)
//				+"\n ---- my lock is "+getMyProtocol().myLock.keySet();
		for (Contract lockRequest : contractsToAnswer){
			if (getMyProtocol().iCanAcceptLock(lockRequest)){
				toAccept.add(lockRequest);
//				log+="\naccepting "+lockRequest.getInitiator();
				getMyProtocol().myLock.add(lockRequest.getInitiator(),lockRequest);
			} else {
				toReject.add(lockRequest);
//				log+="\nrejecting "+lockRequest.getInitiator();
			}
		}
//		logMonologue(log+"\n",LogService.onBoth);
	}
}
