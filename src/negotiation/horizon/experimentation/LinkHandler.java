package negotiation.horizon.experimentation;

import negotiation.horizon.negotiatingagent.InterfacesParameters;
import negotiation.horizon.negotiatingagent.SubstrateNodeIdentifier;
import dima.introspectionbasedagents.services.BasicAgentCompetence;

public class LinkHandler extends BasicAgentCompetence<SubstrateNode> {

    /**
     * Serial version identifier.
     */
    private static final long serialVersionUID = 4373271413596692061L;

    /**
     * Defined parameters of each link connected to the machine. Intend to be
     * replaced by real and dynamic measures.
     */
    private InterfacesParameters<SubstrateNodeIdentifier> links;
}
