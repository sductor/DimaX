package dimaxx.experimentation;

import dima.basiccommunicationcomponents.Message;

class SimulationEndedMessage extends Message{
	private static final long serialVersionUID = -4584449577236269574L;

	ObservingGlobalService<?> ogs;

	public SimulationEndedMessage(final ObservingGlobalService<?> ogs) {
		super();
		this.ogs = ogs;
	}

	public ObservingGlobalService<?> getOgs() {
		return this.ogs;
	}
}