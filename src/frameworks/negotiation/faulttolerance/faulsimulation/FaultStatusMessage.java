package frameworks.negotiation.faulttolerance.faulsimulation;

import dima.basiccommunicationcomponents.Message;
import frameworks.negotiation.negotiationframework.contracts.ResourceIdentifier;

public class FaultStatusMessage extends Message {
	private static final long serialVersionUID = -7015696793109757279L;

	final ResourceIdentifier r;

	public FaultStatusMessage(final ResourceIdentifier r) {
		super();
		this.r = r;
	}

	public ResourceIdentifier getHost() {
		return this.r;
	}
}