package frameworks.negotiation.negotiationframework.protocoles.status;

import frameworks.negotiation.negotiationframework.NegotiatingAgent;
import frameworks.negotiation.negotiationframework.contracts.AbstractContractTransition;
import frameworks.negotiation.negotiationframework.protocoles.status.StatusObservationCompetence.AgentStateStatus;
import frameworks.negotiation.negotiationframework.rationality.AgentState;

public interface StatusAgent<
PersonalState extends AgentState,
Contract extends AbstractContractTransition> extends NegotiatingAgent<PersonalState, Contract>{


	public AgentStateStatus getMyStatus();

	public AgentStateStatus getStatus(PersonalState s);

	public void updateThreshold() ;

	public boolean stateStatusIs(PersonalState state, AgentStateStatus status);

}
