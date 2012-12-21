package frameworks.negotiation.protocoles.dcopProtocol;

import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import dima.introspectionbasedagents.services.BasicAgentCompetence;
import frameworks.faulttolerance.negotiatingagent.HostState;
import frameworks.faulttolerance.negotiatingagent.ReplicaState;
import frameworks.negotiation.NegotiatingAgent;
import frameworks.negotiation.contracts.ContractTrunk;
import frameworks.negotiation.contracts.MatchingCandidature;
import frameworks.negotiation.contracts.ValuedContract;
import frameworks.negotiation.protocoles.AbstractCommunicationProtocol.SelectionCore;
import frameworks.negotiation.rationality.AgentState;

public class DcopAgentSelectionCore<
State extends AgentState,
Contract extends MatchingCandidature>
extends BasicAgentCompetence<NegotiatingAgent<State,Contract>>
implements
SelectionCore<NegotiatingAgent<State,Contract>, State, Contract> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6057578829825636624L;

	public DcopAgentProtocol<State, Contract> getMyProtocol(){
		return (DcopAgentProtocol<State, Contract>) this.getMyAgent().getMyProtocol();
	}

	@Override
	public void select(final ContractTrunk<Contract> cs,
			final Collection<Contract> toAccept, final Collection<Contract> toReject,
			final Collection<Contract> toPutOnWait) {
		 final State currentState = getMyAgent().getMyCurrentState();
		toPutOnWait.addAll(cs.getAllContracts());
		toPutOnWait.removeAll(cs.getParticipantOnWaitContracts());
		//		if (this.getClass().isAssignableFrom(DcopAgentSelectionCore.class)){
		//			logMonologue("hoyoyo");
		//			assert toPutOnWait.containsAll(cs.getParticipantAlreadyAcceptedContracts()):cs;
		//			assert cs.getParticipantAlreadyAnsweredContracts().containsAll(toPutOnWait):cs;
		//		}

		final List<Contract> contractsToAnswer = cs.getParticipantOnWaitContracts();

		//Refusing obsolete contract
		final Iterator<Contract> cIt = contractsToAnswer.iterator();
		while (cIt.hasNext()){
			final Contract c = cIt.next();
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
		Collections.sort((List<ValuedContract>)contractsToAnswer,this.getMyProtocol().getContractComparator());
		//		String log = "i will select :"+ContractTransition.toInitiator(contractsToAnswer)
		//				+"\n ---- my lock is "+getMyProtocol().myLock.keySet();
		for (final Contract lockRequest : contractsToAnswer){
			if (this.getMyProtocol().iCanAcceptLock(lockRequest)){
				toAccept.add(lockRequest);
				//				log+="\naccepting "+lockRequest.getInitiator();
				this.getMyProtocol().myLock.add(lockRequest.getInitiator(),lockRequest);
			} else {
				toReject.add(lockRequest);
				//				log+="\nrejecting "+lockRequest.getInitiator();
			}
		}
		//		logMonologue(log+"\n",LogService.onBoth);
	}
}
