package frameworks.negotiation.negotiationframework.contracts;

import dima.introspectionbasedagents.services.core.information.ObservationService.Information;

/**
 * Represents a state and a qos (args)
 * 
 * @author ductors
 * 
 */
public interface AbstractActionSpecif extends Information {

	AbstractActionSpecif clone();

}
