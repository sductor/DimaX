package negotiation.negotiationframework.information;

import java.util.Collection;

import dima.basicagentcomponents.AgentIdentifier;

public interface AcquaintanceService {
	
	public Collection<AgentIdentifier> getKnownAgents();
	
	public void add(AgentIdentifier agentId);
	
	public void addAll(Collection<? extends AgentIdentifier> agents);
	
	public void remove(AgentIdentifier agentId);
	
}
