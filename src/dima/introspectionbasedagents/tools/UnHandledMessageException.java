package dima.introspectionbasedagents.tools;

import dima.basiccommunicationcomponents.AbstractMessage;
import dima.support.DimaException;


class UnHandledMessageException extends DimaException {

	private static final long serialVersionUID = 9075235122415400081L;
	AbstractMessage mess;

	public UnHandledMessageException(final AbstractMessage mess) {
		this.mess = mess;
	}
}