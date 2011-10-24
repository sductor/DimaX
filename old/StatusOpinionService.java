package negotiation.negotiationframework.information;

import negotiation.negotiationframework.agent.AgentState;

public class StatusOpinionService<PersonalState extends AgentState> extends SimpleOpinionService<PersonalState> {

	public StatusOpinionService(PersonalState myInitialState) {
		super(myInitialState);
	}

}
