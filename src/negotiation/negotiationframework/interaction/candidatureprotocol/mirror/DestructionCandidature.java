package negotiation.negotiationframework.interaction.candidatureprotocol.mirror;

import negotiation.negotiationframework.interaction.AbstractActionSpecification;
import negotiation.negotiationframework.interaction.AbstractContractTransition;

public interface DestructionCandidature<
Contract extends AbstractContractTransition<ActionSpec>, 
ActionSpec extends AbstractActionSpecification>
extends AbstractContractTransition<AbstractActionSpecification>{

	public Contract getMinContract();
}
