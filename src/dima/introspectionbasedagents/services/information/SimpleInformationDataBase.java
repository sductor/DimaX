package dima.introspectionbasedagents.services.information;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

import dima.basicagentcomponents.AgentIdentifier;
import dima.introspectionbasedagents.services.information.ObservationService.Information;
import dima.introspectionbasedagents.services.information.SimpleObservationService.InformationDataBase;

public class SimpleInformationDataBase<Info extends Information>
extends HashMap<AgentIdentifier, Info>
implements InformationDataBase<Info> {
	private static final long serialVersionUID = -1691723780496506679L;

	@Override
	public Info add(final Info o) {
		return this.put(o.getMyAgentIdentifier(), o);
	}


	public Info remove(final AgentIdentifier id) {
		return this.remove(id);
	}


	@Override
	public Collection<AgentIdentifier> getAgents(){
		return new ArrayList<AgentIdentifier>(this.keySet());
	}


}