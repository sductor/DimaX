package frameworks.negotiation.contracts;

import dima.introspectionbasedagents.services.deployment.server.HostIdentifier;

public class ResourceIdentifier extends HostIdentifier {
	private static final long serialVersionUID = -8206624174118989779L;

	public ResourceIdentifier(final String url, final Integer port) {
		super(url, port);
	}
}
