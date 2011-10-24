package dima.basicinterfaces;

import dima.basiccommunicationcomponents.AbstractMailBox;

public interface MailBoxBasedCommunicatingComponentInterface extends
		CommunicatingComponentInterface {


	/*
	 * Mail Box Primitives
	 */
	public AbstractMailBox getMailBox();

}
