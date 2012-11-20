package dima.introspectionbasedagents.services.communicating;

import dima.basiccommunicationcomponents.AbstractMailBox;

public interface MailBoxBasedAsynchronousCommunicatingComponentInterface extends
AsynchronousCommunicationComponent {


	/*
	 * Mail Box Primitives
	 */
	public AbstractMailBox getMailBox();

}
