package negotiation.negotiationframework.interaction.consensualnegotiation;

import java.util.Collection;

import negotiation.negotiationframework.agent.AgentState;
import negotiation.negotiationframework.interaction.ActionSpecification;
import negotiation.negotiationframework.interaction.ContractTransition;

public interface ProposerCore
<PersonalState extends AgentState, Contract extends ContractTransition<ActionSpec>, ActionSpec extends ActionSpecification> {

	public class NotReadyException extends Exception {
		private static final long serialVersionUID = -804054179327698565L;
	}

	public Collection<Contract> getNextContractsToPropose()
			throws NotReadyException;
}
