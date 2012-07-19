package dima.introspectionbasedagents.services.library.criticity;

import dima.introspectionbasedagents.services.AgentCompetence;
import dima.introspectionbasedagents.shells.CompetentComponent;

public interface CriticityHandler<Agent extends CompetentComponent> extends AgentCompetence<Agent> {

	AgentCriticity getMyCriticity();


}
