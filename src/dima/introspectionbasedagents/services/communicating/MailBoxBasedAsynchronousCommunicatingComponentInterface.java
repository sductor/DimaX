package dima.introspectionbasedagents.services.communicating;

import dima.basiccommunicationcomponents.AbstractMailBox;
import dima.basicinterfaces.DimaComponentInterface;

public interface MailBoxBasedAsynchronousCommunicatingComponentInterface extends
AsynchronousCommunicationComponent {


	/*
	 * Mail Box Primitives
	 */
	public AbstractMailBox getMailBox();

}
