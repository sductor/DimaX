package frameworks.negotiation.negotiationframework.protocoles.status;

import dima.basiccommunicationcomponents.Message;
import dima.introspectionbasedagents.services.information.ObservationService.Information;
public class StatusMessage extends Message {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4730021065299674687L;
	final Information transmittedState;

	public StatusMessage(final Information transmittedState) {
		super();
		this.transmittedState = transmittedState;
	}

	public Information getTransmittedState() {
		return this.transmittedState;
	}

}