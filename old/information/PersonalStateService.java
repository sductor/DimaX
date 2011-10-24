package negotiation.negotiationframework.information;

import negotiation.negotiationframework.agent.AgentState;

public interface PersonalStateService<PersonalState extends AgentState> {
	
	public PersonalState getMyCurrentState();

	public void setNewState(PersonalState s);
	
}
