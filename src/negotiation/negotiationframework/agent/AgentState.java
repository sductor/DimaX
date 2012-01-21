package negotiation.negotiationframework.agent;

import java.util.Collection;

import negotiation.negotiationframework.interaction.ResourceIdentifier;
import dima.basicagentcomponents.AgentIdentifier;
import dima.introspectionbasedagents.services.library.information.ObservationService.Information;

public interface AgentState extends Information {

	@Override
	public AgentIdentifier getMyAgentIdentifier();

	void resetUptime();

	public Collection<? extends AgentIdentifier> getMyResourceIdentifiers();

	public Class<? extends Information> getMyResourcesClass();


	public boolean isValid();

	// Return true if action had an effect
	boolean setLost(ResourceIdentifier h, boolean isLost);

	boolean isNewerThan(AgentState that);
}

// public interface SocialState<State extends AgentState> extends
// Collection<State>, AgentState{
//
// }
//

// void reset();
