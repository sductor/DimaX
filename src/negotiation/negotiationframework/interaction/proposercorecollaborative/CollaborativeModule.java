package negotiation.negotiationframework.interaction.proposercorecollaborative;

import java.util.Collection;

import negotiation.faulttolerance.negotiatingagent.ReplicaCore;
import negotiation.faulttolerance.negotiatingagent.ReplicaState;
import negotiation.faulttolerance.negotiatingagent.ReplicationCandidature;
import negotiation.faulttolerance.negotiatingagent.ReplicationSocialOptimisation;
import negotiation.negotiationframework.AllocationSocialWelfares;
import negotiation.negotiationframework.agent.RationalCore;
import negotiation.negotiationframework.interaction.contracts.AbstractActionSpecification;
import negotiation.negotiationframework.interaction.contracts.AbstractContractTransition;

public abstract class CollaborativeModule<
ActionSpec extends AbstractActionSpecification,
PersonalState extends ActionSpec,
Contract extends AbstractContractTransition<ActionSpec>>  {

	private final AllocationSocialWelfares<ActionSpec, Contract> myOptimiser;

	public CollaborativeModule(AllocationSocialWelfares<ActionSpec, Contract> opt) {
		this.myOptimiser = opt;
	}
	
	public final int getAllocationPreference(final PersonalState s,
			final Collection<Contract> c1,
			final Collection<Contract> c2) {
		return this.myOptimiser.getSocialPreference(c1, c2);
	}
}
