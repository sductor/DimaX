package frameworks.negotiation.protocoles.dcopProtocol;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import dima.basicagentcomponents.AgentIdentifier;
import dima.introspectionbasedagents.kernel.NotReadyException;
import dima.introspectionbasedagents.modules.faults.Assert;
import dima.introspectionbasedagents.modules.mappedcollections.HashedHashSet;
import dima.introspectionbasedagents.services.BasicAgentCompetence;
import dima.introspectionbasedagents.services.loggingactivity.LogService;
import frameworks.negotiation.NegotiatingAgent;
import frameworks.negotiation.contracts.ContractTrunk;
import frameworks.negotiation.contracts.MatchingCandidature;
import frameworks.negotiation.protocoles.AbstractCommunicationProtocol.ProposerCore;
import frameworks.negotiation.rationality.AgentState;

public class DcopLeaderProposerCore<
State extends AgentState,
Contract extends MatchingCandidature>
extends BasicAgentCompetence<NegotiatingAgent<State,Contract>>
implements
ProposerCore<NegotiatingAgent<State,Contract>, State, Contract> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8279053078112074428L;
	HashedHashSet<AgentIdentifier, Contract> iWannaLock=new HashedHashSet<AgentIdentifier, Contract>();

	public DCOPLeaderProtocol<State, Contract> getMyProtocol(){
		return (DCOPLeaderProtocol<State, Contract>) this.getMyAgent().getMyProtocol();
	}

	/*
	 * Proposer core
	 */

	@Override
	public boolean IWantToNegotiate(final ContractTrunk<Contract> contracts) {
		//				logMonologue("i want to negotiaite? "+!getMyProtocol().gainFlag.isEmpty());
		if (this.getMyProtocol().waitTime>0) {
			this.getMyProtocol().waitTime--;
		}
		return this.getMyProtocol().waitTime==0 && this.getMyProtocol().myLock.isEmpty() && !this.getMyProtocol().gainFlag.isEmpty();
	}

	@Override
	public Set<? extends Contract> getNextContractsToPropose()
			throws NotReadyException {
		this.logMonologue("negotiating");
		final Set<Contract> contractToPropose = new HashSet<Contract>();
		final Iterator<Collection<AgentIdentifier>> itGainFlag = this.getMyProtocol().gainFlag.iterator();
		assert this.getMyProtocol().gainFlag.size()<=1;
		while (itGainFlag.hasNext()){
			final Collection<AgentIdentifier> rig =itGainFlag.next();

			if (!this.getMyProtocol().lockedRigs.contains(rig)){//s'il a un gain et qui est locké il est d'abord delocke parle selection
				this.iWannaLock.clear();
				for (final Contract c : this.getMyProtocol().gainContracts.get(rig)){
					assert !this.getMyProtocol().lockedNodesToRig.keySet().contains(c);
					contractToPropose.add(c);
					//
					this.iWannaLock.add(c.getInitiator(), c);
					//					getMyProtocol().myLock.add(c.getInitiator(), c);
					this.getMyProtocol().lockedNodesToRig.add(c, rig);
				}
				itGainFlag.remove();
				this.getMyProtocol().lockedRigs.add(rig);
				assert !contractToPropose.isEmpty();
				if (!DCOPLeaderProtocol.dcopProtocol.equals(LogService.onNone)) {
					this.logMonologue("i propose ",LogService.onFile);
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
