package dima.introspectionbasedagents.services.criticity;

import dima.introspectionbasedagents.kernel.CompetentComponent;
import dima.introspectionbasedagents.services.AgentCompetence;

public interface CriticityHandler<Agent extends CompetentComponent> extends AgentCompetence<Agent> {

	AgentCriticity getMyCriticity();


}
