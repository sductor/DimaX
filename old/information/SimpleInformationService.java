package negotiation.negotiationframework.information;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import dima.basicagentcomponents.AgentIdentifier;

public class SimpleInformationService implements InformationService, AcquaintanceService {

	//
	// Fields
	//
	
	private final Set<AgentIdentifier> knownAgents =
		new HashSet<AgentIdentifier>();

	private HashMap<AgentIdentifier, Information> infos =
		new HashMap<AgentIdentifier, Information>();
	

	//
	// Accessors
	//

	/*
	 * Acquaintances
	 */
	
	@Override
	public Collection<AgentIdentifier> getKnownAgents() {
		return knownAgents;
	}

	@Override
	public void add(AgentIdentifier agentId) {
		knownAgents.add(agentId);		
	}

	@Override
	public void addAll(Collection<? extends AgentIdentifier> agents) {
		knownAgents.addAll(agents);
	}

	@Override
	public void remove(AgentIdentifier agentId) {
		knownAgents.remove(agentId);
		infos.remove(agentId);				
	}	
	
	/*
	 * Information
	 */

	@Override
	public <Info extends Information> Info get(Class<Info> informationType,
			AgentIdentifier agentId) {
		return (Info) infos.get(agentId);
	}

	@Override
	public <Info extends Information> void add(Info information) {
		infos.put(information.getMyAgentIdentifier(), information);
		this.add(information.getMyAgentIdentifier());
	}

	@Override
	public <Info extends Information> void remove(Info information) {
		infos.remove(information.getMyAgentIdentifier());		
	}
}
