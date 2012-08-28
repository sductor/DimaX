package negotiation.negotiationframework.information;

import negotiation.negotiationframework.agent.AgentState;

public interface KnowledgeService<PersonalState extends AgentState>
extends PersonalStateService<PersonalState>, AcquaintanceService, InformationService {

}
