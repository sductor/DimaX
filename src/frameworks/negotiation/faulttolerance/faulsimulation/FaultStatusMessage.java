package frameworks.negotiation.faulttolerance.faulsimulation;

import dima.basiccommunicationcomponents.Message;
import frameworks.negotiation.negotiationframework.contracts.ResourceIdentifier;

public class FaultStatusMessage extends Message {
	private static final long serialVersionUID = -7015696793109757279L;

	private final ResourceIdentifier r;
	private final boolean isFaultEvent;

	public FaultStatusMessage(ResourceIdentifier r, boolean isFaultEvent) {
		super();
		this.r = r;
		this.isFaultEvent = isFaultEvent;
	}

	public ResourceIdentifier getHost() {
		return this.r;
	}

	public boolean isFaultEvent() {
		return isFaultEvent;
	}
}