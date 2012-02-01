package negotiation.negotiationframework.interaction.candidatureprotocol.mirror;

import negotiation.negotiationframework.interaction.contracts.AbstractActionSpecification;
import negotiation.negotiationframework.interaction.contracts.AbstractContractTransition;

public interface DestructionCandidature<
Contract extends AbstractContractTransition<ActionSpec>,
ActionSpec extends AbstractActionSpecification>
extends AbstractContractTransition<ActionSpec>{

	public Contract getMinContract();
}
