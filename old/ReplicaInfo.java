package negotiation.faulttolerance.negotiatingagent;

import java.util.Collection;
import java.util.Map;

import negotiation.faulttolerance.negotiatingagent.NegotiatingReplica.ReplicaState;
import negotiation.negotiationframework.agent.SimpleAgentState;
import negotiation.negotiationframework.information.ObservationService.Information;
import negotiation.negotiationframework.interaction.ResourceIdentifier;
import negotiation.tools.aggregator.AbstractCompensativeAggregation;
import negotiation.tools.aggregator.LightAverageDoubleAggregation;
import dima.basicagentcomponents.AgentIdentifier;

