package frameworks.negotiation.horizon.negotiatingagent;

import frameworks.negotiation.negotiationframework.contracts.ResourceIdentifier;

/**
 * Extension of an AgentUniqueIdentifier for clarity with SubstrateNodes.
 * 
 * @author Vincent Letard
 */
public class SubstrateNodeIdentifier extends ResourceIdentifier implements
HorizonIdentifier {

	/**
	 * Serial version identifier.
	 */
	private static final long serialVersionUID = 8272223481939666990L;

	/**
	 * Constructs a new SubstrateNodeIdentifier using the specified addressing
	 * information.
	 * 
	 * @param url
	 *            Location of the machine.
	 * @param port
	 *            The port number to communicate with the agent.
	 */
	public SubstrateNodeIdentifier(final String url, final Integer port) {
		super(url, port);
	}
}
