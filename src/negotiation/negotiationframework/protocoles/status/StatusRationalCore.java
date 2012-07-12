package negotiation.negotiationframework.protocoles.status;

import java.util.Collection;

import negotiation.negotiationframework.contracts.AbstractContractTransition;
import negotiation.negotiationframework.protocoles.status.StatusObservationCompetence.AgentStateStatus;
import negotiation.negotiationframework.rationality.AgentState;
import negotiation.negotiationframework.rationality.RationalCore;
import dima.basicagentcomponents.AgentIdentifier;
import dima.introspectionbasedagents.services.BasicCommunicatingCompetence;

public class StatusRationalCore<
PersonalState extends AgentState,
Contract extends AbstractContractTransition>
extends BasicCommunicatingCompetence<StatusAgent<PersonalState,Contract>>
implements RationalCore<StatusAgent<PersonalState,Contract>,PersonalState,Contract>{
	private static final long serialVersionUID = -3882932472033817195L;

	RationalCore referenceCore;

	public StatusRationalCore(final RationalCore referenceCore) {
		this.referenceCore=referenceCore;
	}

	@Override
	public int getAllocationPreference(
			final Collection<Contract> c1,
			final Collection<Contract> c2) {
		final PersonalState s1 = this.getMyAgent().getMyResultingState(this.getMyAgent().getMyCurrentState(), c1);
		final PersonalState s2 = this.getMyAgent().getMyResultingState(this.getMyAgent().getMyCurrentState(), c2);

		if (this.getMyAgent().getStatus(s1).equals(AgentStateStatus.Wastefull)
				&& this.getMyAgent().getStatus(s2).equals(AgentStateStatus.Wastefull)) {
			return this.referenceCore.getAllocationPreference(c1, c2);
			//			return this.getAllocationReliabilityPreference(c2, c1);// ATTENTION
			//			return this.getFirstLoadSecondReliabilitAllocationPreference(c1,
			//					c2);
		} else if (this.getMyAgent().getStatus(s1).equals(AgentStateStatus.Wastefull)) {
			// n'est
			// pas
			// wastefull
			return -1;
		} else if (this.getMyAgent().getStatus(s2).equals(AgentStateStatus.Wastefull)) {
			// n'est
			// pas
			// wastefull
			return 1;
		} else {
			// aucun contrat ne rend wastefull
			return this.referenceCore.getAllocationPreference(c1, c2);
			//			return getMyAgent().getAllocationReliabilityPreference(c2, c1);// ATTENTION
			//			return this.getFirstLoadSecondReliabilitAllocationPreference(c1,
			//					c2);
		}
	}


	//
	// Delegated Methods
	//



	@Override
	public boolean isActive() {
		return this.referenceCore.isActive();
	}


	@Override
	public void die() {
		this.referenceCore.die();
	}


	@Override
	public AgentIdentifier getIdentifier() {
		return this.referenceCore.getIdentifier();
	}


	@Override
	public void setActive(final boolean active) {
		this.referenceCore.setActive(active);
	}


	@Override
	public void setMySpecif(final AgentState s, final AbstractContractTransition c) {
		this.referenceCore.setMySpecif(s, c);
	}


	@Override
	public void execute(final Collection contracts) {
		this.referenceCore.execute(contracts);
	}


	@Override
	public Double evaluatePreference(final Collection cs) {
		return this.referenceCore.evaluatePreference(cs);
	}


	@Override
	public boolean iObserveMyRessourceChanges() {
		return this.referenceCore.iObserveMyRessourceChanges();
	}


	@Override
	public boolean iMemorizeMyRessourceState() {
		return this.referenceCore.iMemorizeMyRessourceState();
	}
}