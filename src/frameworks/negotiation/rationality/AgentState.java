package frameworks.negotiation.rationality;

import java.util.Collection;

import dima.basicagentcomponents.AgentIdentifier;
import dima.introspectionbasedagents.services.information.ObservationService.Information;

public interface AgentState extends Information {

	@Override
	public AgentIdentifier getMyAgentIdentifier();

	public Collection<? extends AgentIdentifier> getMyResourceIdentifiers();
	public boolean hasResource(AgentIdentifier id);

	public Class<? extends AgentState> getMyResourcesClass();

	//droit = satisfaction
	public boolean isValid();

	public int getStateCounter();

	public AgentState clone();

}

// public interface SocialState<State extends AgentState> extends
// Collection<State>, AgentState{
//
// }
//

//	void resetUptime();
//	// Return true if action had an effect
//	boolean setLost(ResourceIdentifier h, boolean isLost);


// void reset();
