package frameworks.negotiation.protocoles.status;

import frameworks.negotiation.NegotiatingAgent;
import frameworks.negotiation.contracts.AbstractContractTransition;
import frameworks.negotiation.protocoles.status.StatusObservationCompetence.AgentStateStatus;
import frameworks.negotiation.rationality.AgentState;

public interface StatusAgent<
PersonalState extends AgentState,
Contract extends AbstractContractTransition> extends NegotiatingAgent<PersonalState, Contract>{


	public AgentStateStatus getMyStatus();

	public AgentStateStatus getStatus(PersonalState s);

	public void updateThreshold() ;

	public boolean stateStatusIs(PersonalState state, AgentStateStatus status);

}
