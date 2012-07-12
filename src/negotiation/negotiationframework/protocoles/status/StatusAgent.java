package negotiation.negotiationframework.protocoles.status;

import negotiation.negotiationframework.NegotiatingAgent;
import negotiation.negotiationframework.contracts.AbstractContractTransition;
import negotiation.negotiationframework.protocoles.status.StatusObservationCompetence.AgentStateStatus;
import negotiation.negotiationframework.rationality.AgentState;

public interface StatusAgent<
PersonalState extends AgentState,
Contract extends AbstractContractTransition> extends NegotiatingAgent<PersonalState, Contract>{


	public AgentStateStatus getMyStatus();

	public AgentStateStatus getStatus(PersonalState s);

	public void updateThreshold() ;

	public boolean stateStatusIs(PersonalState state, AgentStateStatus status);

}
