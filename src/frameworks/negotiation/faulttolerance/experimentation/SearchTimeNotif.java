package frameworks.negotiation.faulttolerance.experimentation;

import dima.basiccommunicationcomponents.Message;

public class SearchTimeNotif extends Message {
	/**
	 * 
	 */
	private static final long serialVersionUID = 2868998305546594785L;
	Double value;

	public SearchTimeNotif(final Double value) {
		super();
		this.value = value;
	}

	public Double getValue() {
		return this.value;
	}

}
