package negotiation.faulttolerance.faulsimulation;

import negotiation.negotiationframework.interaction.ResourceIdentifier;

public class FaultEvent extends FaultStatusMessage {
	private static final long serialVersionUID = -9049458289584795578L;

	public FaultEvent(final ResourceIdentifier r) {
		super(r);
	}

	@Override
	public String toString() {
		return "fault!! " + this.r;
	}
}