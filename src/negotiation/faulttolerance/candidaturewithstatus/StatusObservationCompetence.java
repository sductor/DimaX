package negotiation.faulttolerance.candidaturewithstatus;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Random;

import dima.basicagentcomponents.AgentIdentifier;
import dima.introspectionbasedagents.services.BasicAgentModule;
import dima.introspectionbasedagents.services.BasicCommunicatingCompetence;
import dima.introspectionbasedagents.services.UnrespectedCompetenceSyntaxException;
import dima.introspectionbasedagents.services.observingagent.NotificationMessage;
import negotiation.faulttolerance.experimentation.Replica;
import negotiation.faulttolerance.negotiatingagent.ReplicaState;
import negotiation.negotiationframework.contracts.ResourceIdentifier;

public class StatusObservationCompetence extends BasicAgentModule<Replica>{

	public final boolean centralised;
	
	public final AgentIdentifier myLaborantin;//si centralised
	public final Integer numberTodiffuse;//sinon
	
	Random rand = new Random();
	
	public StatusObservationCompetence(AgentIdentifier myLaborantin) {
		super();
		this.centralised = true;
		this.myLaborantin = myLaborantin;
		this.numberTodiffuse = null;
	}

	public StatusObservationCompetence(Replica ag, int numberTodiffuse)
			throws UnrespectedCompetenceSyntaxException {
		super(ag);
		this.centralised = false;
		this.myLaborantin = null;
		this.numberTodiffuse = numberTodiffuse;
	}

	public void diffuse(Serializable s) {
		
		if (centralised){
			assert s instanceof ReplicaState;
			getMyAgent().sendMessage(myLaborantin, new NotificationMessage("status information",s));
		} else {
			int numberdiffused=0;
			ArrayList<AgentIdentifier> allAgents = new ArrayList<AgentIdentifier>(getMyAgent().getMyInformation().getKnownAgents());
			while (numberdiffused < numberTodiffuse){
				AgentIdentifier id = allAgents.remove(rand.nextInt(allAgents.size()));
				assert !(getMyAgent().getIdentifier() instanceof AgentIdentifier) || id instanceof ResourceIdentifier;
				assert !(getMyAgent().getIdentifier() instanceof ResourceIdentifier) || id instanceof AgentIdentifier;
				getMyAgent().sendMessage(id, new NotificationMessage("status information",s));
				numberdiffused++;
			}
		}		
	}

}
