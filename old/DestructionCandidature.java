package negotiation.negotiationframework.interaction.candidatureprotocol.mirror;

import java.util.Arrays;
import java.util.Collection;

import dima.basicagentcomponents.AgentIdentifier;

import negotiation.negotiationframework.interaction.AbstractActionSpecification;
import negotiation.negotiationframework.interaction.AbstractContractTransition;
import negotiation.negotiationframework.interaction.ContractTransition;
import negotiation.negotiationframework.interaction.MatchingCandidature;
import negotiation.negotiationframework.interaction.ResourceIdentifier;

public abstract class DestructionCandidature<
ActionSpec extends AbstractActionSpecification,
Contract extends AbstractContractTransition<ActionSpec>>
extends MatchingCandidature<ActionSpec>{


	private final Collection<Contract> requestingContract;
	
	

	public DestructionCandidature(
			AgentIdentifier intiator, 
			AgentIdentifier a,
			ResourceIdentifier r, 
			long validityTime,
			Collection<Contract> requestingContract) {
		super(intiator, a, r, validityTime);
		this.requestingContract = requestingContract;
	}

	public DestructionCandidature(
			AgentIdentifier intiator, 
			AgentIdentifier a,
			ResourceIdentifier r, 
			long validityTime,
			Contract... requestingContract) {
		super(intiator, a, r, validityTime);
		this.requestingContract = Arrays.asList(requestingContract);
	}
	
	public Collection<Contract> getRequestingContract() {
		return requestingContract;
	}
}
