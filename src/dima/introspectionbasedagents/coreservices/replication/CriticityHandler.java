package dima.introspectionbasedagents.coreservices.replication;

import dima.introspectionbasedagents.CompetentComponent;
import dima.introspectionbasedagents.competences.AgentCompetence;

public interface CriticityHandler<Agent extends CompetentComponent> extends AgentCompetence<Agent> {

	AgentCriticity getMyCriticity();


}
