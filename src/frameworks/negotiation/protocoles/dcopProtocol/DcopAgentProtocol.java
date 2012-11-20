package frameworks.negotiation.protocoles.dcopProtocol;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashSet;

import dima.basicagentcomponents.AgentIdentifier;
import dima.introspectionbasedagents.annotations.MessageHandler;
import dima.introspectionbasedagents.annotations.PreStepComposant;
import dima.introspectionbasedagents.annotations.StepComposant;
import dima.introspectionbasedagents.annotations.Transient;
import dima.introspectionbasedagents.modules.mappedcollections.HashedHashSet;
import dima.introspectionbasedagents.services.UnrespectedCompetenceSyntaxException;
import dima.introspectionbasedagents.services.information.NoInformationAvailableException;
import frameworks.negotiation.contracts.ContractTrunk;
import frameworks.negotiation.contracts.MatchingCandidature;
import frameworks.negotiation.contracts.ValuedContract;
import frameworks.negotiation.protocoles.AbstractCommunicationProtocol;
import frameworks.negotiation.rationality.AgentState;

public class DcopAgentProtocol<
State extends AgentState,
Contract extends MatchingCandidature>
extends AbstractCommunicationProtocol<State, Contract>{

	/**
	 * 
	 */
	private static final long serialVersionUID = 7150294482085881907L;
	final int k;
	HashedHashSet<AgentIdentifier, Contract> myLock=new HashedHashSet<AgentIdentifier, Contract>();

	public DcopAgentProtocol(final int k)
			throws UnrespectedCompetenceSyntaxException {
		super(new ContractTrunk<Contract>());
		this.k = k;
	}

	public Comparator<ValuedContract> getContractComparator(){
		return new Comparator<ValuedContract>() {

			@Override
			public int compare(final ValuedContract o1,final ValuedContract o2) {

				final int valuecomp = o1.getSocialValue().compareTo(o2.getSocialValue());
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

		final DcopValueMessage<State> constraintMessage = new DcopConstraintsMessage<State> (this.k+2, this.getMyAgent().getIdentifier(), this.getMyAgent().getMyCurrentState(),this.getMyAgent().getKnownResources());
		assert !this.getMyAgent().getKnownResources().contains(this.getMyAgent().getIdentifier());
		this.sendMessage(this.getMyAgent().getKnownResources(), constraintMessage);
		return true;
	}

	@MessageHandler
	public void beInformed(final DcopValueMessage<State> m){
		//				logMonologue("receiving "+m.getVariable()+", "+m.remainingHops,DCOPLeaderProtocol.dcopProtocol);
		assert !m.getVariable().equals(this.getIdentifier());
		m.decreaseHops();
		try {
			if (!this.getMyAgent().getMyInformation().hasInformation(m.getMyState().getClass(),m.getVariable())
					|| !m.getMyState().equals(this.getMyAgent().getMyInformation().getInformation(m.getMyState().getClass(),m.getVariable()))){
				//le message est bien une mise a jour!
				this.getMyAgent().getMyInformation().add(m.getMyState());
				if (m.mustBeForwarded()){
					this.mL.add(m);
				}
			}
		} catch (final NoInformationAvailableException e) {
			throw new RuntimeException("impossible");
		}
	}

	ArrayList<DcopValueMessage<State>> mL=new ArrayList();//aaaargggggggggh le bug!!
	@StepComposant
	public void sendMessageAQuiDeDroit(){
		for (final DcopValueMessage<State> m : this.mL){
			final Collection<AgentIdentifier> knownRe = new HashSet<AgentIdentifier>(this.getMyAgent().getKnownResources());
			knownRe.remove(m.getVariable());
			m.setSender(this.getIdentifier());
			this.sendMessage(knownRe, m);
		}
		this.mL.clear();
	}

	/*
	 * Protocol
	 */


	@Override
	protected boolean ImAllowedToNegotiate(final ContractTrunk<Contract> contracts) {
		return false;
	}

	@Override
	protected void answerAccepted(final Collection<Contract> toAccept) {
		for (final Contract c : toAccept){
			assert this.myLock.isEmpty() || this.myLock.containsKey(c.getInitiator()):this.myLock+"\n\n------\n"+c;
			//			myLock.add(c.getInitiator(),c);
		}
		this.acceptContract(toAccept, Receivers.Initiator);
	}

	@Override
	protected void answerRejected(final Collection<Contract> toReject) {
		this.rejectContract(toReject, Receivers.Initiator);
	}

	@Override
	protected void putOnWait(final Collection<Contract> toPutOnWait) {}

	@Override
	protected void receiveCancel(final SimpleContractAnswer delta) {
		this.myLock.remove(delta.getIdentifier().getInitiator());
		//		assert myLock.isEmpty():myLock+"\n\n------\n"+delta.getIdentifier();
		//		assert ok;
		super.receiveCancel(delta);
	}

	@Override
	protected void receiveConfirm(final SimpleContractAnswer delta) {
		//		assert !myLock.isEmpty() && myLock.containsKey(delta.getIdentifier().getInitiator());
		this.logMonologue("updating state!!",DCOPLeaderProtocol.dcopProtocol);
		this.sendMessage(this.getMyAgent().getKnownResources(), new DcopValueMessage<State>(this.k+2, this.getMyAgent().getIdentifier(), this.getMyAgent().getMyCurrentState()));

		//		try {
		//			myLock.remove(delta.getIdentifier().getInitiator(), getContracts().getContract(delta.getIdentifier()));
		this.myLock.remove(delta.getIdentifier().getInitiator());
		//		} catch (UnknownContractException e) {
		//			throw new RuntimeException("impossible");
		//		}

		super.receiveConfirm(delta);
	}

	public boolean iCanAcceptLock(final Contract lockRequest){
		final boolean iHaveRessource = this.getMyAgent().getMyCurrentState().hasResource(lockRequest.getAgent())
				|| this.getMyAgent().getMyCurrentState().hasResource(lockRequest.getResource());
		assert this.myLock.keySet().size()<=1;
		if ( iHaveRessource==lockRequest.isMatchingCreation()){
			//contrat non applicable : le system a update;
			return false;
		} else if (this.myLock.isEmpty()) {
			assert lockRequest.getAllParticipants().contains(this.getIdentifier()):"should not have received";
			return true;
		} else if  (this.myLock.containsKey(lockRequest.getInitiator())){
			assert lockRequest.getAllParticipants().contains(this.getIdentifier()):"should not have received";
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
