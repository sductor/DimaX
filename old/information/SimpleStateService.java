package negotiation.negotiationframework.information;

import negotiation.negotiationframework.agent.AgentState;

public class SimpleStateService<PersonalState extends AgentState> 
implements PersonalStateService<PersonalState>{

	//
	// Fields
	//
	
	private PersonalState myState;
	
	//
	// Constructor
	//
	
	public SimpleStateService(PersonalState myState) {
		super();
		this.myState = myState;
	}

	//
	// Accessors
	//

	@Override
	public PersonalState getMyCurrentState() {
		return this.myState;
	}


	@Override
	public void setNewState(PersonalState s) {
		myState=s;		
	}
}
