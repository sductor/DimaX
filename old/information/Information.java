package negotiation.negotiationframework.information;

import dima.basicagentcomponents.AgentIdentifier;
import dima.basicinterfaces.DimaComponentInterface;


public interface Information extends DimaComponentInterface{
	
	public AgentIdentifier getMyAgentIdentifier();
	
	public long getUptime();
}
