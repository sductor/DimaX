package dima.introspectionbasedagents.services;

import java.util.Collection;

import dima.basicagentcomponents.AgentIdentifier;
import dima.introspectionbasedagents.kernel.CompetentComponent;
import dima.introspectionbasedagents.services.communicating.AbstractMessageInterface;
import dima.introspectionbasedagents.services.communicating.AsynchronousCommunicationComponent;

public class BasicCommunicatingCompetence<Agent extends
CompetentComponent & AsynchronousCommunicationComponent> extends BasicAgentCompetence<Agent>{
	private static final long serialVersionUID = -7270812729505868349L;


	public BasicCommunicatingCompetence(final Agent ag) throws UnrespectedCompetenceSyntaxException {
		this.setMyAgent(ag);
	}

	public BasicCommunicatingCompetence() {
	}

	public void sendMessage(final AgentIdentifier id, final AbstractMessageInterface m ){
		this.getMyAgent().sendMessage(id, m);
	}

	public void sendMessage(final Collection<? extends AgentIdentifier> ids, final AbstractMessageInterface m){
		assert m!=null:m;
		//		assert new HashSet(ids).size()==ids.size();
		for (final AgentIdentifier id :ids) {
			if (id!=this.myAgent.getIdentifier()) {
				m.setReceiver(id);
				m.setSender(this.getIdentifier());
				this.sendMessage(id, m);
			}
		}
	}

}