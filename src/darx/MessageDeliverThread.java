/****
 *	Copyright (c) 2007, Olivier Marin, Laboratoire d'Informatique de Paris 6
 *	All rights reserved.
 ****/

/*
 This file is part of DARX.

 DARX is free software: you can redistribute it and/or modify
 it under the terms of the GNU Lesser General Public License as published by
 the Free Software Foundation, either version 3 of the License, or
 (at your option) any later version.

 DARX is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 GNU Lesser General Public License for more details.

 You should have received a copy of the GNU Lesser General Public License
 along with DARX. If not, see <http://www.gnu.org/licenses/>.
 */

package darx;

import java.io.Serializable;

/**
 * This is the thread that handles the reception of asynchronous messages for
 * the <code>TaskShell</code>.<BR>
 * <BR>
 *
 *
 *
 * @author Jacob Zimmermann
 * @author Olivier Marin
 *
 * @version %I%, %G%
 */
class MessageDeliverThread extends Thread {

	/**
	 * The received message, in DARX format.
	 */
	protected DarxMessage msg;

	/**
	 * The task to which the message is destined.
	 */
	protected DarxTask task;

	/**
	 * Constructs a new instance for this class.
	 *
	 * @param msg
	 *            the received message, in DARX format.
	 * @param task
	 *            the task to which the message is destined.
	 */
	MessageDeliverThread(final DarxMessage msg, final DarxTask task) {
		this.msg = msg;
		this.task = task;
	}

	/**
	 * Executes the message handling.
	 */
	@Override
	public void run() {
		// Disencapsulate the message
		final Serializable msgBody = this.msg.getContents();
		// Carry out the task handling of the message
		this.task.receiveAsyncMessage(msgBody);
	}
}
