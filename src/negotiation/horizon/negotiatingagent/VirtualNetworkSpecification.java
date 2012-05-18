package negotiation.horizon.negotiatingagent;

import negotiation.horizon.negotiatingagent.VirtualNetworkIdentifier.VirtualNodeIdentifier;
import dima.basicagentcomponents.AgentIdentifier;

public class VirtualNetworkSpecification extends HorizonSpecification {

    /**
     * Serial version identifier.
     */
    private static final long serialVersionUID = -8399535995860205895L;

    private final VirtualNodeIdentifier node;

    public VirtualNetworkSpecification(final VirtualNodeIdentifier specif) {
	this.node = specif;
    }

    @Override
    public AgentIdentifier getMyAgentIdentifier() {
	return this.node.getMyNetwork();
    }

    public VirtualNodeIdentifier getNode() {
	return this.node;
    }

}
