package dima.introspectionbasedagents.competences;

import java.util.Collection;

import dima.basicagentcomponents.AgentIdentifier;
import dima.basiccommunicationcomponents.Message;
import dima.basicinterfaces.CommunicatingComponentInterface;
import dima.introspectionbasedagents.CompetentComponent;

public class BasicAgentCommunicatingCompetence<Agent extends CompetentComponent & CommunicatingComponentInterface> extends BasicAgentCompetence<Agent>{
	private static final long serialVersionUID = -7270812729505868349L;


	public BasicAgentCommunicatingCompetence(Agent ag) throws UnrespectedCompetenceSyntaxException {
		setMyAgent(ag);
	}
	
	public BasicAgentCommunicatingCompetence() {
	}
	
	protected void sendMessage(final AgentIdentifier id, final Message m ){
		this.getMyAgent().sendMessage(id, m);
	}

	protected void sendMessage(final Collection<? extends AgentIdentifier> ids, final Message m){
		for (final AgentIdentifier id :ids)
			if (id!=this.myAgent.getIdentifier())
				this.sendMessage(id, m);
	}

	@Override
	public AgentIdentifier getIdentifier(){
		return this.getMyAgent().getIdentifier();
	}
}