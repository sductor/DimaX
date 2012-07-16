package negotiation.faulttolerance.experimentation;

import dima.basiccommunicationcomponents.Message;

public class SearchTimeNotif extends Message {
	Double value;
	
	public SearchTimeNotif(Double value) {
		super();
		this.value = value;
	}

	public Double getValue() {
		return value;
	}

}
