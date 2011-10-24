package negotiation.faulttolerance.faulsimulation;

import negotiation.negotiationframework.interaction.ResourceIdentifier;

public class RepairEvent extends FaultStatusMessage {
	private static final long serialVersionUID = 7479584662585992253L;

	public RepairEvent(final ResourceIdentifier r) {
		super(r);
	}

	@Override
	public String toString() {
		return "repair!! " + this.r;
	}
}