package negotiation.horizon.negociatingagent;

import negotiation.negotiationframework.contracts.ResourceIdentifier;
import dima.basicagentcomponents.AgentUniqueIdentifier;

/**
 * Extension of an AgentUniqueIdentifier for clarity with SubstrateNodes.
 * 
 * @author Vincent Letard
 */
public class SubstrateNodeIdentifier extends ResourceIdentifier {

    private final AgentUniqueIdentifier id;

    /**
     * Serial version identifier.
     */
    private static final long serialVersionUID = 8272223481939666990L;

    public SubstrateNodeIdentifier(String url, Integer port) {
	super(url, port);
	this.id = new AgentUniqueIdentifier();
    }

    @Override
    public int hashCode() {
	return super.hashCode() ^ id.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
	assert (this.id != null);
	if (!getClass().equals(obj.getClass()))
	    return false;
	else
	    return this.id.equals(((SubstrateNodeIdentifier) obj).id);
    }

}
