package negotiation.negotiationframework.contracts;

import negotiation.negotiationframework.rationality.AgentState;
import dima.introspectionbasedagents.services.information.ObservationService.Information;

/**
 * Represents a state and a qos (args)
 * @author ductors
 *
 */
public interface AbstractActionSpecification extends Information,AgentState  {}
