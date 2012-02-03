package negotiation.negotiationframework.interaction.contracts;

import negotiation.negotiationframework.agent.AgentState;
import dima.introspectionbasedagents.services.information.ObservationService.Information;

/**
 * Represents a state and a qos (args)
 * @author ductors
 *
 */
public interface AbstractActionSpecification extends Information,AgentState  {}
