package frameworks.negotiation.protocoles.dcopProtocol;

import java.sql.Array;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;

import com.sun.org.apache.bcel.internal.generic.NEW;

import dima.basicagentcomponents.AgentIdentifier;
import dima.introspectionbasedagents.annotations.MessageHandler;
import dima.introspectionbasedagents.annotations.PreStepComposant;
import dima.introspectionbasedagents.annotations.StepComposant;
import dima.introspectionbasedagents.annotations.Transient;
import dima.introspectionbasedagents.modules.mappedcollections.HashedHashSet;
import dima.introspectionbasedagents.services.UnrespectedCompetenceSyntaxException;
import dima.introspectionbasedagents.services.information.NoInformationAvailableException;

import frameworks.negotiation.contracts.AbstractContractTransition.IncompleteContractException;
import frameworks.negotiation.contracts.ContractIdentifier;
import frameworks.negotiation.contracts.ContractTrunk;
import frameworks.negotiation.contracts.MatchingCandidature;
import frameworks.negotiation.contracts.UnknownContractException;
import frameworks.negotiation.contracts.ValuedContract;
import frameworks.negotiation.protocoles.AbstractCommunicationProtocol;
import frameworks.negotiation.rationality.AgentState;

public class DcopAgentProtocol<
State extends AgentState, 
Contract extends MatchingCandidature> 
extends AbstractCommunicationProtocol<State, Contract>{

	final int k;
	HashedHashSet<AgentIdentifier, Contract> myLock=new HashedHashSet<AgentIdentifier, Contract>();

	public DcopAgentProtocol(int k)
			throws UnrespectedCompetenceSyntaxException {
		super(new ContractTrunk<Contract>());
		this.k = k;
	}

	public Comparator<ValuedContract> getContractComparator(){
		return new Comparator<ValuedContract>() {

			@Override
			public int compare(ValuedContract o1,ValuedContract o2) {

				int valuecomp = o1.getSocialValue().compareTo(o2.getSocialValue());
				if (valuecomp==0){
					return o1.getInitiator().toString().compareTo(o2.getInitiator().toString());
				} else {
					return valuecomp;
				}
			}
		};
	}
	//
	// Behavior
	//


	@PreStepComposant
	@Transient
	public boolean sendInitialisationMessage(){
		//				logMonologue("sendign init to "+getMyAgent().getKnownResources()+", "+(k+1));
		//		DcopValueMessage<State> constraintMessage = new DcopConstraintsMessage<State> (k+1, getMyAgent().getIdentifier(), getMyAgent().getMyCurrentState(),getMyAgent().getKnownResources());

		DcopValueMessage<State> constraintMessage = new DcopConstraintsMessage<State> (k+2, getMyAgent().getIdentifier(), getMyAgent().getMyCurrentState(),getMyAgent().getKnownResources());
		assert !getMyAgent().getKnownResources().contains(getMyAgent().getIdentifier());
		sendMessage(getMyAgent().getKnownResources(), constraintMessage);
		return true;
	}

	@MessageHandler
	public void beInformed(DcopValueMessage<State> m){	
		//				logMonologue("receiving "+m.getVariable()+", "+m.remainingHops,DCOPLeaderProtocol.dcopProtocol);
		assert !m.getVariable().equals(getIdentifier());
		m.decreaseHops();
		try {
			if (!getMyAgent().getMyInformation().hasInformation(m.getMyState().getClass(),m.getVariable())
					|| !m.getMyState().equals(getMyAgent().getMyInformation().getInformation(m.getMyState().getClass(),m.getVariable()))){
				//le message est bien une mise a jour!
				getMyAgent().getMyInformation().add(m.getMyState());
				if (m.mustBeForwarded()){
					this.mL.add(m);
				}	
			}
		} catch (NoInformationAvailableException e) {
			throw new RuntimeException("impossible");
		}
	}

	ArrayList<DcopValueMessage<State>> mL=new ArrayList();//aaaargggggggggh le bug!!
	@StepComposant
	public void sendMessageAQuiDeDroit(){
		for (DcopValueMessage<State> m : mL){
			Collection<AgentIdentifier> knownRe = new HashSet<AgentIdentifier>(getMyAgent().getKnownResources());
			knownRe.remove(m.getVariable());
			m.setSender(getIdentifier());
			sendMessage(knownRe, m);
		}
		mL.clear();
	}

	/*
	 * Protocol
	 */


	@Override
	protected boolean ImAllowedToNegotiate(ContractTrunk<Contract> contracts) {
		return false;
	}

	@Override
	protected void answerAccepted(Collection<Contract> toAccept) {
		for (Contract c : toAccept){
			assert myLock.isEmpty() || myLock.containsKey(c.getInitiator()):myLock+"\n\n------\n"+c;
			//			myLock.add(c.getInitiator(),c);
		}
		acceptContract(toAccept, Receivers.Initiator);
	}

	@Override
	protected void answerRejected(Collection<Contract> toReject) {
		rejectContract(toReject, Receivers.Initiator);
	}

	@Override
	protected void putOnWait(Collection<Contract> toPutOnWait) {}

	@Override
	protected void receiveCancel(final SimpleContractAnswer delta) {
		myLock.remove(delta.getIdentifier().getInitiator());
		//		assert myLock.isEmpty():myLock+"\n\n------\n"+delta.getIdentifier();
		//		assert ok;
		super.receiveCancel(delta);
	}

	protected void receiveConfirm(final SimpleContractAnswer delta) {
//		assert !myLock.isEmpty() && myLock.containsKey(delta.getIdentifier().getInitiator());
		logMonologue("updating state!!",DCOPLeaderProtocol.dcopProtocol);
		sendMessage(getMyAgent().getKnownResources(), new DcopValueMessage<State>(k+2, getMyAgent().getIdentifier(), getMyAgent().getMyCurrentState()));

//		try {	
//			myLock.remove(delta.getIdentifier().getInitiator(), getContracts().getContract(delta.getIdentifier()));	
			myLock.remove(delta.getIdentifier().getInitiator());
//		} catch (UnknownContractException e) {
//			throw new RuntimeException("impossible");
//		}

		super.receiveConfirm(delta);		
	}

	public boolean iCanAcceptLock(Contract lockRequest){
		boolean iHaveRessource = getMyAgent().getMyCurrentState().hasResource(lockRequest.getAgent())
				|| getMyAgent().getMyCurrentState().hasResource(lockRequest.getResource());
		assert myLock.keySet().size()<=1;
		if ( iHaveRessource==lockRequest.isMatchingCreation()){
			//contrat non applicable : le system a update;
			return false;
		} else if (myLock.isEmpty()) {
			assert lockRequest.getAllParticipants().contains(getIdentifier()):"should not have received";
			return true;
		} else if  (myLock.containsKey(lockRequest.getInitiator())){
			assert lockRequest.getAllParticipants().contains(getIdentifier()):"should not have received";
			return true;
		} else {
			//				State myCommitedState = getMyCommitedState();
			//				if (lockRequest.computeResultingState(myCommitedState).isValid()){
			//					return true;
			//				} else {
			//					return false;
			//				}
			return false;
		}
	}


	//	State getMyCommitedState() {
	//		State myCommitedState = getMyAgent().getMyCurrentState();
	//		for (Collection<Contract> cs : myLocks.values()){
	//			assert !cs.isEmpty();
	//			Contract cType = cs.iterator().next();
	//			//			assert !cType.getAllParticipants().equals(lockRequest.getAllParticipants()):myLocks+"\n--\n"+lockRequest;
	//			try {
	//				myCommitedState = cType.computeResultingState(myCommitedState);
	//			} catch (IncompleteContractException e) {
	//				throw new RuntimeException("impossible");
	//			}
	//
	//		}
	//
	//		assert myCommitedState.isValid();
	//		return myCommitedState;
	//	}
}
