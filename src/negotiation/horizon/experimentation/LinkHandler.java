package negotiation.horizon.experimentation;

import negotiation.horizon.parameters.InterfacesParameters;
import negotiation.horizon.parameters.NetworkLinkParameters;
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
    private InterfacesParameters<NetworkLinkParameters> links;

    public LinkHandler(final InterfacesParameters<NetworkLinkParameters> links) {
	this.links = links;
    }

    public InterfacesParameters<NetworkLinkParameters> getLinkState() {
	return this.links;
    }
}
