package negotiation.negotiationframework.rationality;

import java.util.Collection;

import dima.introspectionbasedagents.services.BasicAgentCompetence;

import negotiation.faulttolerance.negotiatingagent.ReplicaCore;
import negotiation.faulttolerance.negotiatingagent.ReplicaState;
import negotiation.faulttolerance.negotiatingagent.ReplicationCandidature;
import negotiation.faulttolerance.negotiatingagent.ReplicationSocialOptimisation;
import negotiation.negotiationframework.contracts.AbstractActionSpecification;
import negotiation.negotiationframework.contracts.AbstractContractTransition;

public class CollaborativeCore<
ActionSpec extends AbstractActionSpecification,
PersonalState extends ActionSpec,
Contract extends AbstractContractTransition<ActionSpec>>
extends BasicAgentCompetence<SimpleRationalAgent<ActionSpec,PersonalState,Contract>>
implements RationalCore<ActionSpec, PersonalState, Contract>{

	//
	// Fields
	//
	
	private final AllocationSocialWelfares<ActionSpec, Contract> myOptimiser;
	private final RationalCore  myPersonalCore;
	
	//
	// Constructor
	//
	
	public CollaborativeCore(
			AllocationSocialWelfares<ActionSpec, Contract> opt,
			RationalCore rationality) {
		this.myOptimiser = opt;
		this.myPersonalCore= rationality;
	}
	
	//
	// Methods
	//
	
	public final int getAllocationPreference(final PersonalState s,
			final Collection<Contract> c1,
			final Collection<Contract> c2) {
		return this.myOptimiser.getSocialPreference(c1, c2);
	}
	
	//
	// Delegated methods
	//
	
	public AbstractActionSpecification getMySpecif(
			AbstractActionSpecification s, AbstractContractTransition c) {
		return myPersonalCore.getMySpecif(s, c);
	}

	public void execute(AbstractContractTransition c) {
		myPersonalCore.execute(c);
	}

	public Double evaluatePreference(AbstractActionSpecification s1) {
		return myPersonalCore.evaluatePreference(s1);
	}

	public boolean IWantToNegotiate(AbstractActionSpecification s) {
		return myPersonalCore.IWantToNegotiate(s);
	}

	
	
	
}
