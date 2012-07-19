package dima.introspectionbasedagents.services.core.acquaintance;

import java.util.Collection;

import dima.basicagentcomponents.AgentIdentifier;
import dima.basicinterfaces.DimaComponentInterface;

public interface AcquaintancesHandler extends DimaComponentInterface {

	public Collection<AgentIdentifier> getAcquaintances();
	
	public boolean addAcquaintance(AgentIdentifier id);
	
	public boolean removeAcquaintance(AgentIdentifier id);
	
}
