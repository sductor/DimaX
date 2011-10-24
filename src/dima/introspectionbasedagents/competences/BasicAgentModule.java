package dima.introspectionbasedagents.competences;

import dima.basicagentcomponents.AgentIdentifier;
import dima.basicinterfaces.DimaComponentInterface;
import dima.introspectionbasedagents.CompetentComponent;

public class BasicAgentModule<Agent extends CompetentComponent> implements DimaComponentInterface{
	private static final long serialVersionUID = -8166804401339182512L;

	//
	// Fields
	//

	Agent myAgent;
	
	//
	// Constructors
	//


	public BasicAgentModule(final Agent ag){
		this.myAgent = ag;
	}

	public BasicAgentModule(){
	}

	//
	// Accessors
	//

	public AgentIdentifier getIdentifier(){
		return this.getMyAgent().getIdentifier();
	}
	
	public void setMyAgent(final Agent ag) {
		this.myAgent=ag;
	}

	public Agent getMyAgent() {
		return this.myAgent;
	}
}
