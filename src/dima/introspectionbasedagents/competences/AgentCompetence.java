package dima.introspectionbasedagents.competences;

import dima.basicagentcomponents.AgentIdentifier;
import dima.basicinterfaces.ActiveComponentInterface;
import dima.introspectionbasedagents.CompetentComponent;

/**
 * Interface for the competences,
 * A competence mmust be annotated with @CompetenceProtocol
 * and thus be associated to protocol
 * @author Sylvain Ductor
 *
 */
public interface AgentCompetence<Agent extends CompetentComponent> extends ActiveComponentInterface{

	Agent getMyAgent();

	void setMyAgent(Agent ag) throws UnrespectedCompetenceSyntaxException;

	void die();
	
	AgentIdentifier getIdentifier();
	
	void setActive(boolean active);

}
